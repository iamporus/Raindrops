package com.room.raindrops.utils;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.room.raindrops.databases._CitiesDB;

public class DatabaseManager {

	private static DatabaseOpenHelper DBHelper;
	private SQLiteDatabase db;
	private Context context;

	private static DatabaseManager databaseManager = null;

	protected DatabaseManager(Context ctx) {
		if (DBHelper == null)
			DBHelper = new DatabaseOpenHelper(ctx);
		this.context = ctx;
	}

	public static DatabaseManager getInstance(Context ctx) {
		if (databaseManager == null) {
			databaseManager = new DatabaseManager(ctx);
		}

		return databaseManager;
	}

	protected SQLiteDatabase open() throws SQLException {
		if (DBHelper != null)
			db = DBHelper.getWritableDatabase();
		else {
			DBHelper = new DatabaseOpenHelper(context);
			db = DBHelper.getWritableDatabase();
		}
		return db;
	}

	public void cleanUp(Long currentTime) {

		new _CitiesDB(context).clearAllCities();
	}
}