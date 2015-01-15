package com.room.raindrops.helper;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;


public class PrefsHandler {

    private static String APP_ID = "";
    private static final int MODE = Activity.MODE_PRIVATE;

    public PrefsHandler(String appId){
        APP_ID = appId;
    }

    public static boolean readBoolean(Context ctx, String key) {
        SharedPreferences pref = ctx.getSharedPreferences(APP_ID,
                MODE);
        return pref.getBoolean(key, false);
    }

    public static boolean readBoolean(Context ctx, String key, boolean flag) {
        SharedPreferences pref = ctx.getSharedPreferences(APP_ID,
                MODE);
        return pref.getBoolean(key, flag);
    }

    public static void writeBoolean(Context ctx, String key, boolean val) {
        SharedPreferences pref = ctx.getSharedPreferences(APP_ID,
                MODE);
        Editor editor = pref.edit();
        editor.putBoolean(key, val);
        editor.commit();
    }

    public static long readLong(Context ctx, String key) {
        SharedPreferences pref = ctx.getSharedPreferences(APP_ID,
                MODE);
        return pref.getLong(key, -1);
    }

    public static void writeLong(Context ctx, String key, long val) {
        SharedPreferences pref = ctx.getSharedPreferences(APP_ID,
                MODE);
        Editor editor = pref.edit();
        editor.putLong(key, val);
        editor.commit();
    }

    public static int readInt(Context ctx, String key) {
        SharedPreferences pref = ctx.getSharedPreferences(APP_ID,
                MODE);
        return pref.getInt(key, 0);
    }

    private static int readInt(Context context, String key, int i) {
        SharedPreferences pref = context.getSharedPreferences(APP_ID,
                MODE);
        return pref.getInt(key, i);
    }

    public static void writeInt(Context ctx, String key, int val) {
        SharedPreferences pref = ctx.getSharedPreferences(APP_ID,
                MODE);
        Editor editor = pref.edit();
        editor.putInt(key, val);
        editor.commit();
    }

    public static String readString(Context ctx, String key) {
        SharedPreferences pref = ctx.getSharedPreferences(APP_ID,
                MODE);
        return pref.getString(key, "");
    }

    public static void writeString(Context ctx, String key, String val) {
        SharedPreferences pref = ctx.getSharedPreferences(APP_ID,
                MODE);
        Editor editor = pref.edit();
        editor.putString(key, val);
        editor.commit();
    }


}
