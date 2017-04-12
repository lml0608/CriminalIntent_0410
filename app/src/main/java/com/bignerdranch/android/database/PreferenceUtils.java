package com.bignerdranch.android.database;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by lfs-ios on 2017/4/12.
 */

public class PreferenceUtils {

    private static SharedPreferences sPreferences;


    public static void putBoolean(Context context, String key, boolean value) {
        if (sPreferences == null) {

            sPreferences = context.getSharedPreferences("config", 0);
        }
        sPreferences.edit().putBoolean(key, value).commit();
    }


    public static boolean getBoolean(Context context, String key, boolean defvalue) {
        if (sPreferences == null) {

            sPreferences = context.getSharedPreferences("config", 0);
        }
        return sPreferences.getBoolean(key, defvalue);
    }
}
