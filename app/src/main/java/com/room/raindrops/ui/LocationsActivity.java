package com.room.raindrops.ui;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.room.raindrops.R;
import com.room.raindrops.components.CustomTextView;
import com.room.raindrops.models.MyLocation;

public class LocationsActivity extends ActionBarActivity implements SwipeRefreshLayout.OnRefreshListener {

    private RecyclerView mCityRecyclerView;

    private MyLocation[] locations = {
            new MyLocation(1,"Pune, IN", "74"),
            new MyLocation(2,"New York, US", "43"),
            new MyLocation(3,"San Francisco, US", "24")};

    private MyLocationsAdapter mAdapter;
    private SwipeRefreshLayout swipeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locations);

        initViews();
        bindAdapters();
    }


    private void initViews() {

        mCityRecyclerView = (RecyclerView) findViewById(R.id.city_recycler_view);
        mCityRecyclerView.setHasFixedSize(true);
        mCityRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        mAdapter = new MyLocationsAdapter(locations);
        mCityRecyclerView.setAdapter(mAdapter);

        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        swipeLayout.setOnRefreshListener(this);
        swipeLayout.setColorScheme(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

    }


    private void bindAdapters() {


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_locations, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRefresh() {

        new Handler().postDelayed(new Runnable() {
            @Override public void run() {
                swipeLayout.setRefreshing(false);
            }
        }, 5000);
    }

    public class MyLocationsAdapter extends RecyclerView.Adapter<MyLocationsAdapter.ViewHolder> {


        private final MyLocation[] mLocations;

        public MyLocationsAdapter(MyLocation[] locations) {
            mLocations = locations;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            private final TextView mLocationTv;
            private final CustomTextView mTemperatureTv;

            public ViewHolder(View view) {
                super(view);
                mLocationTv = (TextView) view.findViewById(R.id.locationNameTextView);
                mTemperatureTv = (CustomTextView) view.findViewById(R.id.temperatureTextView);
            }
        }

        @Override
        public MyLocationsAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.locations_grid_item,viewGroup,false);
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(MyLocationsAdapter.ViewHolder viewHolder, int i) {

            MyLocation location = mLocations[i];
            viewHolder.mLocationTv.setText(location.getName());
            viewHolder.mTemperatureTv.setText(location.getTemperature());
        }

        @Override
        public int getItemCount() {
            return mLocations.length;
        }
    }
}
