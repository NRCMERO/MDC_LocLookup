package com.openDC.loclookup.model;

import android.content.Context;
import android.content.SharedPreferences;

public class AppPrefs {
    private static SharedPreferences prefs = null;
    private static final String SELECTED_MAP = "SELECTED_MAP";
    private static final String FIELDS = "FIELDS";

    private static synchronized SharedPreferences getPrefs(Context context) {
        if (prefs == null) {
            prefs = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        }
        return prefs;
    }

    public static void setMap(Context context, String mapName) {
        SharedPreferences.Editor prefEditor = getPrefs(context).edit();
        prefEditor.putString(SELECTED_MAP, mapName).commit();
    }

    public static String getMap(Context context) {
        return getPrefs(context).getString(SELECTED_MAP, null);
    }

    public static void setFields(Context context, String map, String fields) {
        String key = FIELDS + "_" + map;
        SharedPreferences.Editor prefEditor = getPrefs(context).edit();
        prefEditor.putString(key, fields).commit();
    }

    public static String getFields(Context context, String map) {
        String key = FIELDS + "_" + map;
        return getPrefs(context).getString(key, null);
    }
}
