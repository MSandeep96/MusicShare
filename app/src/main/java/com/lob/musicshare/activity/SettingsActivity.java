package com.lob.musicshare.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
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
        String FEEDBACK = getResources().getString(R.string.feedback_key);

        addPreferencesFromResource(R.xml.settings);

        if (!SpotifyUtils.isSpotifyInstalled(getApplicationContext())) {
            getPreferenceScreen().removePreference(findPreference(MISCELLANEOUS));
        }

        findPreference(FEEDBACK).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_EMAIL, "matteolob1704@gmail.com");
                intent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.email_subject));
                intent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.email_body));

                startActivity(Intent.createChooser(intent, getResources().getString(R.string.send_email)));
                return false;
            }
        });

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