package com.lob.musicshare;

import android.app.Application;

import com.lob.musicshare.util.Constants;

import org.acra.ACRA;
import org.acra.annotation.ReportsCrashes;

@ReportsCrashes(formUri = Constants.CRASH_REPORT_URL)
public class MusicShare extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ACRA.init(this);
    }
}

