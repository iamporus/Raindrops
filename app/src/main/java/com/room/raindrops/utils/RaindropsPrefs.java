package com.room.raindrops.utils;

import android.content.Context;

import com.room.raindrops.helper.PrefsHandler;
import com.room.raindrops.models.MyLocation;

public class RaindropsPrefs extends PrefsHandler {


    private static String APP_ID = "Raindrops";
    public static final String HOME_LOCATION_ID = "home_location_id";
    public static final String HOME_LOCATION_NAME = "home_location_name";
    private static final String HOME_LOCATION_COUNTRY = "home_location_country";
    private static final String APPLICATION_STATE = "application_state";
    private static final String IS_FIRST_TIME = "is_first_time";

    public RaindropsPrefs() {
        super(APP_ID);
    }

    public boolean isFirstTime(Context context){
        return readBoolean(context,IS_FIRST_TIME,true);
    }

    public void setFirstTime(Context context){
        writeBoolean(context,IS_FIRST_TIME,false);
    }
    public int getApplicationState(Context context) {
        return readInt(context, APPLICATION_STATE);
    }

    public void setApplicationState(Context context, int state) {
        writeInt(context, APPLICATION_STATE, state);
    }

    public void setHomeLocation(Context context, long cityId, String cityName, String country) {
        writeLong(context, HOME_LOCATION_ID, cityId);
        writeString(context, HOME_LOCATION_NAME, cityName);
        writeString(context, HOME_LOCATION_COUNTRY, country);
    }

    public MyLocation getHomeLocation(Context context) {
        return new MyLocation(readLong(context, HOME_LOCATION_ID),
                              readString(context, HOME_LOCATION_NAME),
                              readString(context, HOME_LOCATION_COUNTRY));
    }
}
