package com.room.raindrops.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;
import com.room.raindrops.R;
import com.room.raindrops.databases._CitiesDB;
import com.room.raindrops.models.CityLocal;
import com.room.raindrops.models.MyLocation;
import com.room.raindrops.receivers.WebServiceListener;
import com.room.raindrops.utils.HttpUtils;
import com.room.raindrops.utils.Logger;
import com.room.raindrops.utils.RaindropsPrefs;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class CurrentLocationActivity extends ActionBarActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient mGoogleApiClient;

    // Request code to use when launching the resolution activity
    private static final int REQUEST_RESOLVE_ERROR = 1001;
    // Unique tag for the error dialog fragment
    private static final String DIALOG_ERROR = "dialog_error";
    // Bool to track whether the app is already resolving an error
    private boolean mResolvingError = false;
    private Location mLastLocation;
    private TextView textView;
    private ProgressBar progress;
    private CityLocal[] cities;
    private boolean navigatingForGPS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_location);

        enableGPS();
        initViews();
        buildGoogleApiClient();
    }

    private void enableGPS() {
        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE );
        boolean statusOfGPS = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if(statusOfGPS){

            navigatingForGPS = false;
        }else{
            navigatingForGPS = true;
            showGPSDisabledAlertToUser();
        }
    }




    private void showGPSDisabledAlertToUser(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Location Services are disabled. Would you like to enable it?")
                .setCancelable(false)
                .setPositiveButton("Enable GPS",
                        new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int id){

                                Intent callGPSSettingIntent = new Intent(
                                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(callGPSSettingIntent);
                            }
                        });
        alertDialogBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id){
                        dialog.cancel();
                        navigatingForGPS= false;
                    }
                });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    private void initViews() {
        textView = (TextView) findViewById(R.id.textView6);
        progress = (ProgressBar)findViewById(R.id.progressBar);
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!mResolvingError) {
            mGoogleApiClient.connect();
        }
        if(navigatingForGPS){
            enableGPS();
        }
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        if(!navigatingForGPS)
            finish();
        super.onStop();
    }
    @Override
    public void onConnected(Bundle bundle) {
        Logger.log("connected");

        getCities();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                        mGoogleApiClient);
                if (mLastLocation != null) {
                    Logger.log("Lat: " + String.valueOf(mLastLocation.getLatitude()) + "/ " + String.valueOf(mLastLocation.getLongitude()));
                    showLocation(String.valueOf(mLastLocation.getLatitude()), String.valueOf(mLastLocation.getLongitude()));
                } else {
                    Logger.log("Location is null");

                }
            }
        }, 10000);

    }

    private void getCities() {
        HttpUtils.getCitiesLocal(getApplicationContext(), new WebServiceListener() {
            @Override
            public void onError(int responseCode, String errorMessage) {
                System.out.println("Error");
            }

            @Override
            public void onResult(String response) {

                cities = new Gson().fromJson(response, CityLocal[].class);


            }
        });
    }

    private void showLocation(String lat, String lon) {

        Geocoder gcd = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = gcd.getFromLocation(Double.parseDouble(lat), Double.parseDouble(lon), 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (addresses.size() > 0) {
            String name = addresses.get(0).getLocality().trim().toLowerCase();
            if(cities!=null){
                for (CityLocal city : cities) {
                    if (city.getName().trim().toLowerCase().contentEquals(name)) {
                        LocationDialogFragment fragment = new LocationDialogFragment();
                        Bundle args = new Bundle();
                        args.putString("name", city.getName());
                        args.putString("country_name", city.getCountryCode());
                        args.putLong("id", city.getId());
                        fragment.setArguments(args);
                        fragment.show(getSupportFragmentManager(), "location_fragment");
                        break;
                    }
                }
            }
        }

    }

    @Override
    public void onConnectionSuspended(int i) {
        Logger.log("connection suspended");

    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Logger.log("connection failed");
        if (mResolvingError) {
            // Already attempting to resolve an error.
            return;
        } else if (result.hasResolution()) {
            try {
                mResolvingError = true;
                result.startResolutionForResult(this, REQUEST_RESOLVE_ERROR);
            } catch (IntentSender.SendIntentException e) {
                // There was an error with the resolution intent. Try again.
                mGoogleApiClient.connect();
            }
        } else {
            // Show dialog using GooglePlayServicesUtil.getErrorDialog()
            showErrorDialog(result.getErrorCode());
            mResolvingError = true;
        }
    }

    private static final String STATE_RESOLVING_ERROR = "resolving_error";

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(STATE_RESOLVING_ERROR, mResolvingError);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_RESOLVE_ERROR) {
            mResolvingError = false;
            if (resultCode == RESULT_OK) {
                // Make sure the app is not already connected or attempting to connect
                if (!mGoogleApiClient.isConnecting() &&
                        !mGoogleApiClient.isConnected()) {
                    mGoogleApiClient.connect();
                }
            }
        }
    }

    private void showErrorDialog(int errorCode) {
        // Create a fragment for the error dialog
        ErrorDialogFragment dialogFragment = new ErrorDialogFragment();
        // Pass the error that should be displayed
        Bundle args = new Bundle();
        args.putInt(DIALOG_ERROR, errorCode);
        dialogFragment.setArguments(args);
        dialogFragment.show(getSupportFragmentManager(), "errordialog");
    }

    /* Called from ErrorDialogFragment when the dialog is dismissed. */
    public void onDialogDismissed() {
        mResolvingError = false;
    }


    public static class ErrorDialogFragment extends DialogFragment {
        public ErrorDialogFragment() { }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Get the error code and retrieve the appropriate dialog
            int errorCode = this.getArguments().getInt(DIALOG_ERROR);
            return GooglePlayServicesUtil.getErrorDialog(errorCode,
                    this.getActivity(), REQUEST_RESOLVE_ERROR);
        }

        @Override
        public void onDismiss(DialogInterface dialog) {
            ((CurrentLocationActivity)getActivity()).onDialogDismissed();
        }
    }
    public static class LocationDialogFragment extends DialogFragment {
        private String countryName;

        public LocationDialogFragment() { }
        private String name;
        private long id;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            Bundle args = getArguments();
            if(args!=null){
                id = args.getLong("id");
                name = args.getString("name");
                countryName = args.getString("country_name");
            }
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {


            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("Location: " + name +", "+countryName);
            builder.setPositiveButton("Okay",new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    RaindropsPrefs mPrefs = new RaindropsPrefs();
                    mPrefs.setHomeLocation(getActivity(), id, name, countryName);
                    mPrefs.setFirstTime(getActivity());
                    new _CitiesDB(getActivity()).insertCity(new MyLocation(id, name, countryName));

                    startActivity(new Intent(getActivity(), HomeActivity.class));
                }
            }).setCancelable(false);

            return  builder.create();
        }

        @Override
        public void onDismiss(DialogInterface dialog) {
            ((CurrentLocationActivity)getActivity()).onDialogDismissed();
        }
    }
}
