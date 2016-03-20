package com.lob.musicshare.receiver;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.lob.musicshare.R;
import com.lob.musicshare.activity.LoginActivity;
import com.lob.musicshare.util.Constants;
import com.lob.musicshare.util.Debug;
import com.lob.musicshare.util.ParseValues;
import com.lob.musicshare.util.PreferencesUtils;
import com.lob.musicshare.util.web.InternetUtils;
import com.lob.musicshare.util.web.ServerConnectionUtils;

public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, Intent intent) {
        if (PreferencesUtils.isLoggedIn(context) && InternetUtils.hasActiveInternetConnection(context)) {

            String result = ServerConnectionUtils.getContent(Constants.GET_NEW_FOLLOWERS
                    + "?myemail=" + ParseValues.getParsedEmail(PreferencesUtils.getEmail(context)));

            Debug.log("Checking new followers...  " + result);

            if (result != null) {
                if (!result.equals("") && !result.equals(" ")
                        && !result.equals("no_one") && !result.contains("error:")) {

                    String[] newFollowers = result.replace(ParseValues.TWO_DASHES, " ").split("__OTHERUSER__");

                    String notificationTitle = context.getString(R.string.app_name);
                    String notificationMessage = "";

                    boolean onlyOneNewFollower = newFollowers.length == 1;
                    for (int i = 0; i < newFollowers.length; i++) {
                        notificationMessage = notificationMessage.concat(newFollowers[i]
                                + ((onlyOneNewFollower || i == newFollowers.length - 1) ? "" : ", "));
                    }
                    notificationMessage = notificationMessage.concat(" " + context.getString(onlyOneNewFollower ? R.string.is_now_following_you : R.string.are_now_following_you));

                    NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setContentTitle(notificationTitle)
                            .setContentText(notificationMessage)
                            .setAutoCancel(true);

                    Intent notificationIntent = new Intent(context, LoginActivity.class);
                    PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, Intent.FILL_IN_ACTION);
                    notificationBuilder.setContentIntent(pendingIntent);
                    NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                    mNotificationManager.notify(0, notificationBuilder.build());

                    ServerConnectionUtils.getContent(Constants.REMOVE_NEW_FOLLOWERS
                            + "?myemail=" + ParseValues.getParsedEmail(PreferencesUtils.getEmail(context)));
                }
            }
        }
    }
}
