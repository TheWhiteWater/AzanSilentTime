package nz.co.redice.demoservice.utils;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.hilt.android.qualifiers.ApplicationContext;
import nz.co.redice.demoservice.R;
import nz.co.redice.demoservice.services.ForegroundService;
import nz.co.redice.demoservice.view.MainActivity;

@Singleton
public class NotificationHelper {

    private static final String FOREGROUND_CHANNEL_ID = "channelId";
    public static final String QUIT_APP = "quit app";
    // The identifier for the notification displayed for the foreground service.
    public static final int NOTIFICATION_ID = 12345678;

    private Context mContext;

    @Inject
    public NotificationHelper(@ApplicationContext Context context) {
        mContext = context;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel foregroundNotificationChannel = new NotificationChannel(
                    FOREGROUND_CHANNEL_ID,
                    "ForegroundChannelId",
                    NotificationManager.IMPORTANCE_LOW);

            NotificationManager manager = mContext.getSystemService(NotificationManager.class);
            manager.createNotificationChannel(foregroundNotificationChannel);

        }
    }

    public Notification createForegroundNotification(Context context) {
        Intent serviceSwitchingOffIntent = new Intent(mContext, ForegroundService.class);

        // Extra to help us figure out if we arrived in onStartCommand via the notification or not.
        serviceSwitchingOffIntent.putExtra(QUIT_APP, true);


        // The PendingIntent that leads to a call to onStartCommand() in this service.
        PendingIntent servicePendingIntent = PendingIntent.getService(mContext, 0, serviceSwitchingOffIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);


        Intent activityLaunchingIntent = new Intent(context, MainActivity.class);

        //last flag tells what to do when pending updated / re-send
        PendingIntent activityPendingIntent = PendingIntent.getActivity(context, 0, activityLaunchingIntent, 0);

        return new NotificationCompat.Builder(context, FOREGROUND_CHANNEL_ID)
                .addAction(R.drawable.ic_settings, "Home", activityPendingIntent)
                .addAction(R.drawable.ic_cancel, "Quit", servicePendingIntent)
                .setContentTitle("Content Title")
                .setContentText("Content Text")
                .setSmallIcon(R.drawable.ic_android)
                .build();
    }


}
