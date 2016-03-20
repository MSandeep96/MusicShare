package com.lob.musicshare.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.lob.musicshare.R;

public class PreferencesUtils {
    private static SharedPreferences getDefaultSharedPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static boolean isLoggedIn(Context context) {
        return getDefaultSharedPreferences(context).getBoolean("loggedIn", false);
    }

    public static String getName(Context context) {
        return getDefaultSharedPreferences(context).getString("my-name", "");
    }

    public static String getEmail(Context context) {
        return getDefaultSharedPreferences(context).getString("email", "");
    }

    public static String getPassword(Context context) {
        return getDefaultSharedPreferences(context).getString("password", "");
    }

    public static void setIsLoggedIn(Context context, boolean isLoggedIn) {
        getDefaultSharedPreferences(context).edit().putBoolean("loggedIn", isLoggedIn).apply();
    }

    public static void setName(Context context, String name) {
        getDefaultSharedPreferences(context).edit().putString("my-name", name).apply();
    }

    public static void setEmail(Context context, String email) {
        getDefaultSharedPreferences(context).edit().putString("email", email).apply();
    }

    public static void setPassword(Context context, String password) {
        getDefaultSharedPreferences(context).edit().putString("password", password).apply();
    }

    public static void setSuccessfulLogIn(Context context, String email, String password) {
        setIsLoggedIn(context, true);
        setEmail(context, email);
        setPassword(context, password);
    }

    public static boolean useSpotify(Context context) {
        return getDefaultSharedPreferences(context).getBoolean(context
                .getString(R.string.use_spotify), true);
    }

    public static int getNotificationFrequency(Context context) {
        return Integer.valueOf(getDefaultSharedPreferences(context)
                .getString(context.getString(R.string.notification_check_frequency), "7200000"));
    }

    public static void exit(Context context) {
        setIsLoggedIn(context, false);
        setEmail(context, "");
        setPassword(context, "");
    }

    public static boolean isFirstTime(Context context) {
        SharedPreferences sharedPreferences = getDefaultSharedPreferences(context);
        if (sharedPreferences.getBoolean("first_time", true)) {
            sharedPreferences.edit().putBoolean("first_time", false).apply();
            return true;
        }
        return false;
    }
}
