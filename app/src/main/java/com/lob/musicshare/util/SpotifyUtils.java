package com.lob.musicshare.util;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.provider.MediaStore;

public class SpotifyUtils {

    private static final String SPOTIFY_PACKAGE = "com.spotify.music";

    public static void startSpotifySearch(Context context, String query) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setAction(MediaStore.INTENT_ACTION_MEDIA_PLAY_FROM_SEARCH);
        intent.setComponent(new ComponentName(SPOTIFY_PACKAGE, SPOTIFY_PACKAGE + ".MainActivity"));
        intent.putExtra(SearchManager.QUERY, query);
        context.startActivity(intent);
    }

    public static boolean isSpotifyInstalled(Context context) {
        PackageManager packageManager = context.getPackageManager();
        try {
            packageManager.getPackageInfo(SPOTIFY_PACKAGE, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
}
