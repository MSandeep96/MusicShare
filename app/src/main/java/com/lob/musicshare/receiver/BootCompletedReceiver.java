package com.lob.musicshare.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.lob.musicshare.util.Debug;
import com.lob.musicshare.util.NotificationUtils;

public class BootCompletedReceiver extends BroadcastReceiver {

    private static final String BOOT_COMPLETED = "android.intent.action.BOOT_COMPLETED";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(BOOT_COMPLETED)) {
            NotificationUtils.setNotificationAlarm(context);

            Debug.log("BootCompletedReceiver -> alarm set");
        }
    }
}
