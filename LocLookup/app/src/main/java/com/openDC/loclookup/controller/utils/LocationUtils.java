package com.openDC.loclookup.controller.utils;

import android.content.Context;
import android.os.Build;
import android.provider.Settings;

public class LocationUtils {
    /**
     * Checks if location provider is enabled
     *
     * @param context the context that will be used to check
     * @return true if location provider is enabled
     */
    public static boolean isLocationEnabled(Context context) {
        int locationMode;
        String locationProviders;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);
            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
                return false;
            }
            return locationMode != Settings.Secure.LOCATION_MODE_OFF;
        } else {
            locationProviders = Settings.Secure.getString(context.getContentResolver(),
                    Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !locationProviders.isEmpty();
        }
    }
}
