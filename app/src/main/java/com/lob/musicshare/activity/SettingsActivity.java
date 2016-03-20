package com.lob.musicshare.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;

import com.lob.musicshare.R;
import com.lob.musicshare.util.AndroidOverViewUtils;
import com.lob.musicshare.util.NotificationUtils;
import com.lob.musicshare.util.SpotifyUtils;

public class SettingsActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AndroidOverViewUtils.setHeader(this);

        String MISCELLANEOUS = getResources().getString(R.string.pref_miscellaneous);

        addPreferencesFromResource(R.xml.settings);

        if (!SpotifyUtils.isSpotifyInstalled(getApplicationContext())) {
            getPreferenceScreen().removePreference(findPreference(MISCELLANEOUS));
        }

        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

    }

    @Override
    public void onPause() {
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        if (s.equals(getResources().getString(R.string.notification_check_frequency))) {
            NotificationUtils.setNotificationAlarm(getApplicationContext());
        }
    }
}