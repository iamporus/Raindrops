package com.room.raindrops.helper;

import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

public class WeatherUpdaterWakefulReceiver extends WakefulBroadcastReceiver {
    @Override 
    public void onReceive(Context context, Intent intent) {
        Intent service = new Intent(context, WeatherWakefulService.class);
 
        Log.i("SimpleWakefulReceiver", "Starting service @ " + SystemClock.elapsedRealtime());
        startWakefulService(context, service);
    } 
} 