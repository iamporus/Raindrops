package com.room.raindrops.ui;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.room.raindrops.R;
import com.room.raindrops.components.LocationView;
import com.room.raindrops.components.SnappingScrollView;
import com.room.raindrops.databases._CitiesDB;
import com.room.raindrops.models.GroupFeed;
import com.room.raindrops.models.List;
import com.room.raindrops.models.MyLocation;
import com.room.raindrops.receivers.WebServiceResultReceiver;
import com.room.raindrops.utils.Logger;
import com.room.raindrops.utils.RaindropsPrefs;
import com.room.raindrops.utils.WebServiceHandler;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link com.room.raindrops.ui.MyLocationsFragment.OnLocationChangedListener} interface
 * to handle interaction events.
 * Use the {@link MyLocationsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyLocationsFragment extends Fragment implements SnappingScrollView.OnScrollerItemSelectedListener, SwipeRefreshLayout.OnRefreshListener {

    private ArrayList<MyLocation> locations;
    private MyLocation selectedLocation;

    public interface OnLocationChangedListener {
        public void onLocationChanged(long id);

        public void onLocationLoaded(int position);

    }

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;


    private OnLocationChangedListener mListener;
    private SnappingScrollView locationSnappingScrollView;
    private SwipeRefreshLayout swipeLayout;
    private LocationView mHomeLocationView;
    private MyLocation mDisplayedLocation;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MyLocationsFragment.
     */
    public static MyLocationsFragment newInstance(String param1, String param2) {
        MyLocationsFragment fragment = new MyLocationsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public MyLocationsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        MyLocation mHomeCity = new RaindropsPrefs().getHomeLocation(getActivity());
        selectedLocation = new _CitiesDB(getActivity()).getCityFromId(mHomeCity.getId());
        fetchCurrentWeatherForCities();
    }

    public void fetchCurrentWeatherForCities() {
        populateCities();
        refreshWeatherFeedForCities();
    }


    private void updateDisplayedCity(MyLocation mLocation) {
        try {
            ((LocationView) locationSnappingScrollView.getSelectedItem()).setLocation(mLocation);
            Logger.log("Location updated");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_my_locations, container, false);

        initViews(view);
        return view;
    }

    private void initViews(View view) {

        swipeLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
        swipeLayout.setOnRefreshListener(this);
        swipeLayout.setColorScheme(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);


        locationSnappingScrollView = (SnappingScrollView) view.findViewById(R.id.locationSnappingScrollView);

    }

    private void populateCities() {

        locations = new _CitiesDB(getActivity()).getAllCities();
        for (MyLocation loc : locations)
            Logger.log("ALL cities: " + loc.getName());

        LocationView[] views = new LocationView[locations.size()];

        mHomeLocationView = new LocationView(getActivity(), null);
        mHomeLocationView.setLocation(selectedLocation);
        views[0] = mHomeLocationView;

        for (int i = 1; i < locations.size(); i++) {
            LocationView text = new LocationView(getActivity(), null);
            views[i] = text;
        }
        locationSnappingScrollView.clearFeatureItems();
        locationSnappingScrollView.setFeatureItems(views);
        locationSnappingScrollView.setOnScrollerItemSelectedListener(this);
    }


    public void onLocationChanged(long id) {
        if (mListener != null) {
            mListener.onLocationChanged(id);
        }
    }

    @Override
    public void onRefresh() {

        refreshWeatherFeedForCities();
        //TODO: log in the time of update
    }

    private void refreshWeatherFeedForCities() {

        long[] ids = new long[locations.size()];
        int i = 0;
        for (MyLocation loc : locations) {
            ids[i++] = loc.getId();
        }

        WebServiceHandler.getCurrentFeedForCities(getActivity(), ids, new WebServiceResultReceiver(new Handler()) {
            @Override
            protected void onReceiveResult(int resultCode, Bundle resultData) {
                super.onReceiveResult(resultCode, resultData);
                try {
                    String response = (String) resultData.get("result");
                    GroupFeed feed = new Gson().fromJson(response, GroupFeed.class);
                    Logger.log("Group Feed: " + feed);
                    if (feed != null) {
                        List[] lists = feed.getList();
                        _CitiesDB db = new _CitiesDB(getActivity());
                        for (List list : lists) {
                            db.updateWeatherForCity(Long.parseLong(list.getId()),
                                    list.getMain().getTemp(),list.getWeather()[0].getMain());
                        }

                        selectedLocation = db.getCityFromId(selectedLocation.getId());
                        updateDisplayedCity(selectedLocation);
                        swipeLayout.setRefreshing(false);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnLocationChangedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onScrollerItemSelected(int selectedItem) {

        try {
            Log.d("Raindrops", "Selected item: " + selectedItem);
            selectedLocation = new _CitiesDB(getActivity()).getCityFromId(locations.get(selectedItem).getId());
            updateDisplayedCity(selectedLocation);
            onLocationChanged(locations.get(selectedItem).getId());
        } catch (Exception e) {

        }

    }


}
