package com.room.raindrops.helper;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.google.gson.Gson;
import com.room.raindrops.databases._CitiesDB;
import com.room.raindrops.models.GroupFeed;
import com.room.raindrops.models.List;
import com.room.raindrops.models.MyLocation;
import com.room.raindrops.receivers.WebServiceResultReceiver;
import com.room.raindrops.utils.Logger;
import com.room.raindrops.utils.WebServiceHandler;

import java.util.ArrayList;

public class WeatherWakefulService extends IntentService {
    public WeatherWakefulService() {
        super("SimpleWakefulService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        try {
            final _CitiesDB db = new _CitiesDB(getApplicationContext());
            ArrayList<MyLocation> locations = db.getAllCities();
            if (locations != null) {
                long[] ids = new long[locations.size()];
                int i = 0;
                for (MyLocation loc : locations) {
                    ids[i++] = loc.getId();
                }

                WebServiceHandler.getCurrentFeedForCities(getApplicationContext(), ids, new WebServiceResultReceiver(new Handler()) {
                    @Override
                    protected void onReceiveResult(int resultCode, Bundle resultData) {
                        super.onReceiveResult(resultCode, resultData);

                        //TODO: update in db
                        String response = (String) resultData.get("result");
                        GroupFeed feed = new Gson().fromJson(response, GroupFeed.class);
                        if (feed != null) {
                            List[] lists = feed.getList();
                            for (List list : lists) {
                                db.updateWeatherForCity(Long.parseLong(list.getId()),
                                        list.getMain().getTemp(), list.getWeather()[0].getMain());
                            }
                            Logger.log("Feed updated via routine service.");
                        }
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        WeatherUpdaterWakefulReceiver.completeWakefulIntent(intent);
    }
} 