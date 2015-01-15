package com.room.raindrops.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.room.raindrops.databases._CitiesDB;
import com.room.raindrops.databases._ForecastDB;

public class DatabaseOpenHelper extends SQLiteOpenHelper {

	public static final String DATABASE_NAME = "raindrops.db";
	public static final int DATABASE_VERSION = 1;

	public DatabaseOpenHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(_CitiesDB._City.CREATE_TABLE);
		db.execSQL(_ForecastDB._Forecast.CREATE_TABLE);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}

}
