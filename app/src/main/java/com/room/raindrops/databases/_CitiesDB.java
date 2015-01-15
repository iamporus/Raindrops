package com.room.raindrops.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.room.raindrops.models.MyLocation;
import com.room.raindrops.utils.DatabaseManager;

import java.util.ArrayList;


public class _CitiesDB extends DatabaseManager {

    public _CitiesDB(Context ctx) {
        super(ctx);
    }

    public static class _City {
        public static final String TABLE = "Cities";
        public static final String ID = "_id";
        public static final String NAME = "name";
        public static final String COUNTRY_CODE = "country_code";
        public static final String CURRENT_TEMP = "current_temp";
        public static final String CURRENT_MSG = "current_msg";
        public static final String CREATE_TABLE = " create table " + TABLE
                + " (" + ID + " INTEGER primary key ,"
                + " " + NAME + " TEXT,"
                + " " + CURRENT_TEMP + " TEXT,"
                + " " + CURRENT_MSG + " TEXT,"
                + " " + COUNTRY_CODE + " TEXT)"
                + ";";

    }

    public void insertCity(MyLocation city) {
        SQLiteDatabase db = open();
        ContentValues values = new ContentValues();
        values.put(_City.ID, city.getId());
        values.put(_City.NAME, city.getName());
        values.put(_City.COUNTRY_CODE, city.getCountry());
        db.insert(_City.TABLE, null, values);
    }

    public void updateWeatherForCity(long id, String temp,String message){
        SQLiteDatabase db = open();
        ContentValues values = new ContentValues();
        values.put(_City.CURRENT_TEMP, temp);
        values.put(_City.CURRENT_MSG, message);
        db.update(_City.TABLE,values,_City.ID+" =? ",new String[]{id+""});
    }


    public ArrayList<MyLocation> getAllCities() {
        SQLiteDatabase db = open();
        ArrayList<MyLocation> list = new ArrayList<>();
        Cursor cur = db.query(_City.TABLE, null, null, null, null, null, null);

        if (cur.moveToFirst()) {
            do {
                list.add(createCityFromCursor(cur));
            } while (cur.moveToNext());

        }
        cur.close();
        return list;
    }

    private MyLocation createCityFromCursor(Cursor cur) {
        long id = cur.getLong(cur.getColumnIndex(_City.ID));
        String name = cur.getString(cur.getColumnIndex(_City.NAME));
        String countryCode = cur.getString(cur.getColumnIndex(_City.COUNTRY_CODE));
        String temperature = cur.getString(cur.getColumnIndex(_City.CURRENT_TEMP));
        String msg = cur.getString(cur.getColumnIndex(_City.CURRENT_MSG));
        MyLocation location = new MyLocation(id, name, countryCode);
        location.setTemperature(temperature);
        location.setMessage(msg);
        return location;
    }

    public MyLocation getCityFromId(long id) {
        SQLiteDatabase db = open();
        MyLocation city = null;
        Cursor cur = db.query(_City.TABLE, null, _City.ID + "=?", new String[]{id + ""}, null, null, null);
        if (cur.moveToFirst()) {
            city = createCityFromCursor(cur);
        } else
            return null;
        cur.close();
        return city;
    }

    public void clearAllCities() {
        SQLiteDatabase db = open();
        db.delete(_City.TABLE, null, null);
    }
}
