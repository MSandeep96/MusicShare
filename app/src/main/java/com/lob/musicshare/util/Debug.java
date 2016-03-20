package com.lob.musicshare.util;

import android.util.Log;

public class Debug {
    private static final boolean DEBUG = true;

    public static void log(Object whatToLog) {
        if (DEBUG) Log.d("Music Share", String.valueOf(whatToLog != null ? whatToLog : "null"));
    }
}
