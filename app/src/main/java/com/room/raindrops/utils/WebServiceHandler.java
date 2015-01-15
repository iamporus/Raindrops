package com.room.raindrops.utils;

import android.content.Context;
import android.content.Intent;

import com.room.raindrops.receivers.WebServiceResultReceiver;

public class WebServiceHandler {

    public static final String API_ID = "fb36ee141837ded2f5b069115951e6c2";
       public static final String OPEN_WEATHER_CURRENT_FEED_URL = "http://api.openweathermap.org/data/2.5/weather?id=%s&units=metric&APPID=%s";
       public static final String OPEN_WEATHER_CURRENT_GROUP_FEED_URL = "http://api.openweathermap.org/data/2.5/group?id=%s&units=metric&APPID=%s";
       public static final String OPEN_WEATHER_FORECAST_FEED_URL = "http://api.openweathermap.org/data/2.5/forecast/daily?id=%s&units=metric&cnt=14&APPID=%s";

    public static void getCurrentWeatherDetailsForCity(Context context,long id, WebServiceResultReceiver mWebServiceListener){

        // It will create an intent for the intent service start it .

        Intent i = new Intent(context,UpdaterIntentService.class);
        i.putExtra("url",String.format(OPEN_WEATHER_CURRENT_FEED_URL,id,API_ID));
        i.putExtra("listener",mWebServiceListener);
        context.startService(i);
    }

    public static void getCurrentFeedForCities(Context context,long[] id, WebServiceResultReceiver mWebServiceListener){

        // It will create an intent for the intent service start it .

        Intent i = new Intent(context,UpdaterIntentService.class);
        String outData = "";
        for(long in:id)
            outData+=in+",";

        outData = outData.substring(0,outData.length()-1);

        i.putExtra("url",String.format(OPEN_WEATHER_CURRENT_GROUP_FEED_URL,outData,API_ID));
        i.putExtra("listener",mWebServiceListener);
        context.startService(i);
    }

    public static void getWeatherForecastForCity(Context context,long id, WebServiceResultReceiver mWebServiceListener){

        // It will create an intent for the intent service start it .

        Intent i = new Intent(context,UpdaterIntentService.class);
        i.putExtra("url",String.format(OPEN_WEATHER_FORECAST_FEED_URL,id,API_ID));
        i.putExtra("listener",mWebServiceListener);
        context.startService(i);
    }
}
