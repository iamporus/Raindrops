package com.room.raindrops.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.room.raindrops.R;
import com.room.raindrops.components.CustomTextView;
import com.room.raindrops.databases._CitiesDB;
import com.room.raindrops.models.CityLocal;
import com.room.raindrops.models.MyLocation;
import com.room.raindrops.receivers.WebServiceListener;
import com.room.raindrops.utils.HttpUtils;
import com.room.raindrops.utils.RaindropsPrefs;

public class AddCityActivity extends ActionBarActivity implements TextWatcher, AdapterView.OnItemClickListener, View.OnClickListener {

    private EditText mCitySearchEditText;
    private CitySearchAdapter mAdapter;
    private ListView mListView;
    private Handler handler;
    private CityLocal[] cities;
    private Gson gson = new Gson();
    private CityLocal[] result = new CityLocal[15];
    private CustomTextView mOkayBtn;
    private boolean startContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_city);

        if(getIntent().getExtras()!=null)
        startContext = getIntent().getBooleanExtra("isFromHome",false);

        initViews();
        getCities();
        bindAdapters();

        handler = new Handler();
    }


    private void initViews() {

        mCitySearchEditText = (EditText) findViewById(R.id.citySearchEditText);
        mCitySearchEditText.addTextChangedListener(this);
        mListView = (ListView) findViewById(R.id.listView);
        mListView.setEmptyView(findViewById(android.R.id.empty));
        mListView.setOnItemClickListener(this);
        mOkayBtn = (CustomTextView)findViewById(R.id.okayImDoneButton);
        mOkayBtn.setOnClickListener(this);

        if(startContext)
            mOkayBtn.setVisibility(View.VISIBLE);
    }


    private void bindAdapters() {

        mAdapter = new CitySearchAdapter(this, R.layout.city_list_item);
        mListView.setAdapter(mAdapter);
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
                handler.post(new Runnable() {
                    @Override
                    public void run() {

                        mListView.getEmptyView().setVisibility(View.GONE);
                    }
                });

            }
        });
    }

    private void getFilteredCities(String constraint) {

        int i = 0;
        if (cities != null) {
            for (CityLocal city : cities) {
                if (city.getName().startsWith(constraint))
                    result[i++] = city;
                if (i == 15)
                    break;
            }

            handler.post(new Runnable() {
                @Override
                public void run() {
                    mAdapter.clear();
                    for (CityLocal city : result)
                        mAdapter.add(city);

                    mAdapter.notifyDataSetChanged();
                }
            });
        }
    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        getFilteredCities(s.toString());
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        CityLocal city = (CityLocal) parent.getAdapter().getItem(position);

        RaindropsPrefs mPrefs = new RaindropsPrefs();
        if (mPrefs.getHomeLocation(AddCityActivity.this).getId() == -1) {
            showCurrentCityDialog(city);
        } else {
            saveFavoriteCityDialog(city);
        }


    }

    private void saveFavoriteCityDialog(final CityLocal city) {

        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(AddCityActivity.this);
            builder.setMessage("Add " + city.getName() + ", " + city.getCountryCode() + " into your favorite Locations?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            new _CitiesDB(AddCityActivity.this).insertCity(new MyLocation(city.getId(), city.getName(), city.getCountryCode()));
                            dialog.dismiss();
                            Toast.makeText(AddCityActivity.this,city.getName()+", "+city.getCountryCode()+" added to Favorite cities.",Toast.LENGTH_LONG).show();
                        }
                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).setCancelable(false).setInverseBackgroundForced(true);

            Dialog dialog = builder.create();
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.show();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showCurrentCityDialog(final CityLocal city) {

        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(AddCityActivity.this);
            builder.setMessage(city.getName() + ", " + city.getCountryCode() + " will be selected as your Home City.")
                    .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            RaindropsPrefs mPrefs = new RaindropsPrefs();
                            mPrefs.setHomeLocation(AddCityActivity.this, city.getId(), city.getName(), city.getCountryCode());
                            mPrefs.setFirstTime(AddCityActivity.this);
                            new _CitiesDB(AddCityActivity.this).insertCity(new MyLocation(city.getId(), city.getName(), city.getCountryCode()));

                            startActivity(new Intent(AddCityActivity.this, HomeActivity.class));
                        }
                    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).setCancelable(false).setInverseBackgroundForced(true);

            Dialog dialog = builder.create();
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.show();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(!startContext)
            finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.okayImDoneButton:
                if(startContext){
                    setResult(RESULT_OK);
                    finish();
                }
                else
                    startActivity(new Intent(this,HomeActivity.class));
                break;
        }
    }


    public class CitySearchAdapter extends ArrayAdapter<CityLocal> {

        private final Context context;
        private final int resource;

        public CitySearchAdapter(Context context, int resource) {
            super(context, resource);
            this.context = context;
            this.resource = resource;
        }

        @Override
        public CityLocal getItem(int position) {
            return super.getItem(position);
        }

        @Override
        public int getCount() {
            return super.getCount();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = View.inflate(context, resource, null);
                holder = new ViewHolder();
                holder.cityNameTextView = (TextView) convertView.findViewById(R.id.cityNameTextView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            CityLocal item = getItem(position);
            if (item != null) {
                holder.cityNameTextView.setText(item.getName() + ", " + item.getCountryCode());
            }
            return convertView;
        }

        private class ViewHolder {
            TextView cityNameTextView;
        }

    }
}
