package com.room.raindrops.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.room.raindrops.models.DailyFeed;
import com.room.raindrops.utils.DatabaseManager;
import com.room.raindrops.utils.Logger;


public class _ForecastDB extends DatabaseManager {

    public _ForecastDB(Context ctx) {
        super(ctx);
    }

    public static class _Forecast {
        public static final String TABLE = "Forecast";
        public static final String ID = "_id";
        public static final String CITY_ID = "city_id";
        public static final String DATE = "date";
        public static final String MIN_TEMP = "min_temp";
        public static final String MAX_TEMP = "max_temp";
        public static final String CREATE_TABLE = " create table " + TABLE
                + " (" + ID + " INTEGER primary key autoincrement,"
                + " " + CITY_ID + " INTEGER,"
                + " " + DATE + " TEXT,"
                + " " + MIN_TEMP + " TEXT,"
                + " " + MAX_TEMP + " TEXT)"
                + ";";

    }

    public void insertForecastForCity(DailyFeed[] feedForecast) {
        SQLiteDatabase db = open();
        //TODO: check if forecast for city is already added.
        Cursor cur = db.query(_Forecast.TABLE, new String[]{_Forecast.CITY_ID}, _Forecast.CITY_ID + " =? ",
                new String[]{feedForecast[0].getCityId() + ""}, null, null, null);
        if(cur.moveToFirst()){
            Logger.log("City is already added with rows: " + cur.getCount());
            updateForecastCity(feedForecast[0].getCityId(),feedForecast);

        }else {
            for (DailyFeed feed : feedForecast) {
                ContentValues values = new ContentValues();
                values.put(_Forecast.CITY_ID, feed.getCityId());
                values.put(_Forecast.DATE, feed.getDate());
                values.put(_Forecast.MIN_TEMP, feed.getMin_temp());
                values.put(_Forecast.MAX_TEMP, feed.getMax_temp());
                db.insert(_Forecast.TABLE, null, values);
            }
        }
        cur.close();
    }

    public void updateForecastCity(long id, DailyFeed[] dailyFeed) {
        Logger.log("Updating forecast for city: " + id);
        SQLiteDatabase db = open();
        for (DailyFeed feed : dailyFeed) {
            ContentValues values = new ContentValues();
            values.put(_Forecast.DATE, feed.getDate());
            values.put(_Forecast.MIN_TEMP, feed.getMin_temp());
            values.put(_Forecast.MAX_TEMP, feed.getMax_temp());
            db.update(_Forecast.TABLE, values, _Forecast.CITY_ID + " =? ", new String[]{id + ""});
        }
    }


    private DailyFeed createFeedFromCursor(Cursor cur) {

        long id = cur.getLong(cur.getColumnIndex(_Forecast.CITY_ID));
        String date = cur.getString(cur.getColumnIndex(_Forecast.DATE));
        String minTemp = cur.getString(cur.getColumnIndex(_Forecast.MIN_TEMP));
        String maxTemp = cur.getString(cur.getColumnIndex(_Forecast.MAX_TEMP));
        return new DailyFeed(id, date, minTemp, maxTemp);
    }

    public DailyFeed[] getForecastFromId(long id) {
        SQLiteDatabase db = open();
        Logger.log("Forecast for city: " + id);
        DailyFeed[] forecast = null;
        Cursor cur = db.query(_Forecast.TABLE, null, _Forecast.CITY_ID + "=?", new String[]{id + ""}, null, null, null);
        if (cur.moveToFirst()) {
            int i = 0;
            forecast = new DailyFeed[cur.getCount()];
            do {
                DailyFeed feed = createFeedFromCursor(cur);
                forecast[i++] = feed;
            } while (cur.moveToNext());
        } else
            return null;
        cur.close();
        return forecast;
    }

    public void clearAllForecast() {
        SQLiteDatabase db = open();
        db.delete(_Forecast.TABLE, null, null);
    }
}
