package com.room.raindrops.ui;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.room.raindrops.R;
import com.room.raindrops.databases._ForecastDB;
import com.room.raindrops.models.DailyFeed;
import com.room.raindrops.models.List;
import com.room.raindrops.models.MyLocation;
import com.room.raindrops.models.WeatherFeedForecast;
import com.room.raindrops.receivers.WebServiceResultReceiver;
import com.room.raindrops.utils.Logger;
import com.room.raindrops.utils.RaindropsPrefs;
import com.room.raindrops.utils.WebServiceHandler;

import java.text.SimpleDateFormat;
import java.util.Date;

public class FortNightFeedFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    private RecyclerView fortnightRecyclerView;
    private DailyFeed[] locations = new DailyFeed[]{};
    private FortnightFeedAdapter mAdapter;
    private MyLocation mHomeCity;

//    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FortNightFeedFragment.
     */

    public static FortNightFeedFragment newInstance(String param1, String param2) {
        FortNightFeedFragment fragment = new FortNightFeedFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public FortNightFeedFragment() {
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_fort_night_feed, container, false);
        initView(view);
        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mHomeCity = new RaindropsPrefs().getHomeLocation(getActivity());
        pullFeedFromDB(mHomeCity.getId());
    }

    public void refreshForecast(long id) {

        WebServiceHandler.getWeatherForecastForCity(getActivity(), id, new WebServiceResultReceiver(new Handler()) {
            @Override
            protected void onReceiveResult(int resultCode, Bundle resultData) {
                super.onReceiveResult(resultCode, resultData);
                try {

                    String response = (String) resultData.get("result");
                    WeatherFeedForecast feed = new Gson().fromJson(response, WeatherFeedForecast.class);

                    DailyFeed[] locations = new DailyFeed[feed.getList().length];
                    int i = 0;
                    SimpleDateFormat dt = new SimpleDateFormat("c, MMM dd");
                    long cityId = Long.parseLong(feed.getCity().getId());
                    for (List item : feed.getList()) {
                        long mils = Long.parseLong(item.getDt()) * 1000;
                        String date = dt.format(new Date(mils));

                        DailyFeed feedDaily = new DailyFeed(cityId, date, item.getTemp().getMax(), item.getTemp().getMin());
                        Logger.log(feedDaily.toString());
                        locations[i++] = feedDaily;
                    }

                    new _ForecastDB(getActivity()).insertForecastForCity(locations);

                    populateRecylewerView(locations);


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

        });
    }

    public void pullFeedFromDB(long id) {
        DailyFeed[] forecast = new _ForecastDB(getActivity()).getForecastFromId(id);
        if (forecast != null) {
             populateRecylewerView(forecast);
        }
        else {
            Logger.log("Forecast from Net ");
            refreshForecast(id);
        }
    }

    private void populateRecylewerView(DailyFeed[] forecast) {

        try {
            mAdapter.setDataSet(forecast);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initView(View view) {

        fortnightRecyclerView = (RecyclerView) view.findViewById(R.id.fortnight_recycler_view);
        fortnightRecyclerView.setHasFixedSize(true);
        fortnightRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mAdapter = new FortnightFeedAdapter(locations);
        fortnightRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    public class FortnightFeedAdapter extends RecyclerView.Adapter<FortnightFeedAdapter.ViewHolder> {


        private DailyFeed[] mFeed;

        public FortnightFeedAdapter(DailyFeed[] dailyFeeds) {
            mFeed = dailyFeeds;
        }

        public void setDataSet(DailyFeed[] dailyFeeds) {

            mFeed = dailyFeeds;
            notifyDataSetChanged();
        }

        public void clear(){
            mFeed = new DailyFeed[]{};
            mAdapter.notifyDataSetChanged();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            private final TextView mDayTextView;
            private final TextView mTemperatureTv;

            public ViewHolder(View view) {
                super(view);
                mDayTextView = (TextView) view.findViewById(R.id.dayTextView);
                mTemperatureTv = (TextView) view.findViewById(R.id.dayTempTextView);
            }
        }

        @Override
        public FortnightFeedAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.fortnight_list_item, viewGroup, false);
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(FortnightFeedAdapter.ViewHolder viewHolder, int i) {

            DailyFeed location = mFeed[i];
            viewHolder.mDayTextView.setText(location.getDate());
            viewHolder.mTemperatureTv.setText(location.getMin_temp() + (char) 0x00B0 + " / " + location.getMax_temp() + (char) 0x00B0);
        }

        @Override
        public int getItemCount() {
            return mFeed.length;
        }
    }

}
