package com.lob.musicshare.util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.lob.musicshare.receiver.NotificationReceiver;

public class NotificationUtils {
    public static void setNotificationAlarm(Context context) {
        AlarmManager alarmManager = ((AlarmManager) context.getSystemService(Context.ALARM_SERVICE));

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0,
                new Intent(context, NotificationReceiver.class), 0);

        int frequency = PreferencesUtils.getNotificationFrequency(context);

        alarmManager.cancel(pendingIntent);

        if (frequency != 0) {
            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),
                    frequency, pendingIntent);
        }

        Debug.log("ContentActivity -> Setting AlarmManager | Frequency -> " + frequency);
    }
}
