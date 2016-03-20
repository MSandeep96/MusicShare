package com.lob.musicshare.util.web;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.StrictMode;

import java.net.HttpURLConnection;
import java.net.URL;

public class InternetUtils {

    static {
        StrictMode.ThreadPolicy policy =
                new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    public static boolean hasActiveInternetConnection(Context context) {
        if (isNetworkAvailable(context)) {
            try {
                HttpURLConnection urlConnection = (HttpURLConnection) (new URL("http://www.google.com").openConnection());
                urlConnection.setRequestProperty("User-Agent", "Test");
                urlConnection.setRequestProperty("Connection", "close");
                urlConnection.setConnectTimeout(1500);
                urlConnection.connect();
                return (urlConnection.getResponseCode() == 200);
            } catch (Exception ignored) {
            }
        }
        return false;
    }

    private static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }
}
