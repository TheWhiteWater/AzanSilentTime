package nz.co.redice.azansilenttime.utils;


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
import nz.co.redice.azansilenttime.R;
import nz.co.redice.azansilenttime.services.ForegroundService;
import nz.co.redice.azansilenttime.view.MainActivity;

@Singleton
public class NotificationHelper {

    public static final String QUIT_APP = "quit app";
    // The identifier for the notification displayed for the foreground service.
    public static final int NOTIFICATION_ID = 12345678;
    private static final String FOREGROUND_CHANNEL_ID = "channelId";
    @Inject DndHelper mDndHelper;

    private PendingIntent activityPendingIntent;
    private PendingIntent servicePendingIntent;
    public NotificationCompat.Builder mNotificationBuilder;


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


        Intent turnOffIntent = new Intent(mContext, ForegroundService.class);
        turnOffIntent.setAction(QUIT_APP);


        // The PendingIntent that leads to a call to onStartCommand() in this service.
        servicePendingIntent = PendingIntent.getService(mContext, 0, turnOffIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);


        Intent activityLaunchingIntent = new Intent(context, MainActivity.class);

        //last flag tells what to do when pending updated / re-send
        activityPendingIntent = PendingIntent.getActivity(context, 0, activityLaunchingIntent, 0);

        mNotificationBuilder = new NotificationCompat.Builder(context, FOREGROUND_CHANNEL_ID)
                .addAction(R.drawable.ic_settings, "Home", activityPendingIntent)
                .addAction(R.drawable.ic_cancel, "Quit", servicePendingIntent)
                .setContentTitle("Next Mute period:")
                .setContentText("Not set yet")
                .setSmallIcon(R.drawable.ic_black_24);
    }


}
