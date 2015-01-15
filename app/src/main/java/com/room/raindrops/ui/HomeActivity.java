package com.room.raindrops.ui;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.room.raindrops.R;
import com.room.raindrops.components.SlidingTabLayout;
import com.room.raindrops.databases._CitiesDB;
import com.room.raindrops.helper.WeatherUpdaterWakefulReceiver;
import com.room.raindrops.models.MyLocation;

import java.text.SimpleDateFormat;
import java.util.Date;


public class HomeActivity extends ActionBarActivity implements MyLocationsFragment.OnLocationChangedListener {

    private SlidingTabLayout mSlidingTabLayout;
    private ViewPager mViewPager;
    private OurViewPagerAdapter mPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        initToolBar(toolbar);
        setSupportActionBar(toolbar);

        initTabs();

        setFeedFetcherAlarm();
    }

    private void initToolBar(Toolbar toolbar) {
        toolbar.findViewById(R.id.addLocationImageView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(HomeActivity.this, AddCityActivity.class);
                i.putExtra("isFromHome", true);
                startActivityForResult(i, 0);
            }
        });
    }

    private void initTabs() {

        mSlidingTabLayout = (SlidingTabLayout) findViewById(R.id.tab_layout);
        mSlidingTabLayout.setCustomTabView(R.layout.tab_indicator, android.R.id.text1);

        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        mPagerAdapter = new OurViewPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mPagerAdapter);

        //TODO: change color of tab text according to selection

        mSlidingTabLayout.setSelectedIndicatorColors(0x00b0ff);
        mSlidingTabLayout.setDistributeEvenly(true);
        mSlidingTabLayout.setViewPager(mViewPager);

        mViewPager.setCurrentItem(1);
    }


    private void setFeedFetcherAlarm() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(this, WeatherUpdaterWakefulReceiver.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(this, 0, i, 0);
        alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                AlarmManager.INTERVAL_HALF_HOUR, AlarmManager.INTERVAL_HALF_HOUR, alarmIntent);
    }


    @Override
    public void onLocationChanged(long id) {

        MyLocation location = new _CitiesDB(this).getCityFromId(id);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm a, z");
        setToolbarCityText(location.getName() + ", " + location.getCountry(), sdf.format(new Date(System.currentTimeMillis())));

        refreshOtherFeedsForCity(id);
    }

    private void refreshOtherFeedsForCity(long id) {

        FortNightFeedFragment fortNightFeedFragment = (FortNightFeedFragment) getSupportFragmentManager().findFragmentByTag(
                "android:switcher:" + R.id.view_pager + ":2");
        if (fortNightFeedFragment != null) {
            fortNightFeedFragment.pullFeedFromDB(id);
        }
    }

    private void refreshCurrentFeedForCity() {

        MyLocationsFragment myLocationsFragment = (MyLocationsFragment) getSupportFragmentManager().findFragmentByTag(
                "android:switcher:" + R.id.view_pager + ":1");
        if (myLocationsFragment != null) {
            myLocationsFragment.fetchCurrentWeatherForCities();
        }
    }

    @Override
    public void onLocationLoaded(int position) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 0 && resultCode == RESULT_OK) {
            refreshCurrentFeedForCity();
            Toast.makeText(getApplicationContext(), "Scroll down to see newly added location.", Toast.LENGTH_LONG).show();
        }
    }

    private void setToolbarCityText(String cityName, String time) {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ((TextView) toolbar.findViewById(R.id.action_bar_selected_city)).setText(cityName);
        ((TextView) toolbar.findViewById(R.id.action_bar_selected_city_time)).setText(time);
        setSupportActionBar(toolbar);

    }

    private class OurViewPagerAdapter extends FragmentPagerAdapter {

        public OurViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            Fragment frag;
            Bundle args = new Bundle();

            switch (position) {
                case 0:
                    frag = new HourlyFeedsFragment();
                    break;
                case 1:
                    frag = new MyLocationsFragment();
                    break;
                case 2:
                    frag = new FortNightFeedFragment();
                    break;
                default:
                    frag = new Fragment();
            }

            frag.setArguments(args);

            return frag;
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return getPageName(position);
        }

        private CharSequence getPageName(int position) {
            switch (position) {
                case 0:
                    return "48 Hours";
                case 1:
                    return "Currently";
                case 2:
                    return "15 Days";
            }
            return null;
        }
    }
}
