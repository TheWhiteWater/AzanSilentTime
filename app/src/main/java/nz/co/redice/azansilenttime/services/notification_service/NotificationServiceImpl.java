package nz.co.redice.azansilenttime.services.notification_service;


import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.hilt.android.qualifiers.ApplicationContext;
import nz.co.redice.azansilenttime.R;
import nz.co.redice.azansilenttime.services.foreground_service.ForegroundService;
import nz.co.redice.azansilenttime.ui.presentation.Converters;
import nz.co.redice.azansilenttime.utils.SharedPreferencesHelper;
import nz.co.redice.azansilenttime.ui.MainActivity;

@Singleton
public class NotificationServiceImpl implements NotificationService {

    public static final String QUIT_APP = "quit app";
    public static final int NOTIFICATION_ID = 12345678;
    private static final String FOREGROUND_CHANNEL_ID = "channelId";
    public NotificationCompat.Builder mNotificationBuilder;
    private int mNotificationChannel;
    private Context mContext;
    private SharedPreferencesHelper mSharedPreferencesHelper;
    private NotificationManager mNotificationManager;


    @SuppressLint("CheckResult")
    @Inject
    public NotificationServiceImpl(@ApplicationContext Context context,
                                   SharedPreferencesHelper sharedPreferencesHelper) {
        mContext = context;
        mSharedPreferencesHelper = sharedPreferencesHelper;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel foregroundNotificationChannel = new NotificationChannel(
                    FOREGROUND_CHANNEL_ID,
                    "ForegroundChannelId",
                    NotificationManager.IMPORTANCE_LOW);
            mNotificationManager = context.getSystemService(NotificationManager.class);
            mNotificationManager.createNotificationChannel(foregroundNotificationChannel);
            mNotificationChannel = NOTIFICATION_ID;
        }


        Intent turnOffIntent = new Intent(context, ForegroundService.class);
        turnOffIntent.setAction(QUIT_APP);


        // The PendingIntent that leads to a call to onStartCommand() in this service.
        PendingIntent servicePendingIntent = PendingIntent.getService(context, 0, turnOffIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);


        Intent activityLaunchingIntent = new Intent(context, MainActivity.class);

        //last flag tells what to do when pending updated / re-send
        PendingIntent activityPendingIntent = PendingIntent.getActivity(context, 0, activityLaunchingIntent, 0);

        mNotificationBuilder = new NotificationCompat.Builder(context, FOREGROUND_CHANNEL_ID)
                .addAction(R.drawable.ic_settings, context.getString(R.string.notificatin_home_button_value), activityPendingIntent)
                .addAction(R.drawable.ic_cancel, context.getString(R.string.notification_quit_button_value), servicePendingIntent)
                .setContentTitle(context.getString(R.string.notification_content_title))
                .setContentText(context.getString(R.string.notification_empty_content_text))
                .setSmallIcon(R.drawable.ic_bell_small);
    }


    private void convertTimestampIntoNotificationContextText(Long timing) {
        if (timing == null) {
            publishNotificationContextText(mContext.getString(R.string.notification_no_active_alarm_detected));
            return;
        }
        Date date = new Date(timing);

        LocalDateTime startMuteTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        LocalDateTime endMuteTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().plusMinutes(mSharedPreferencesHelper.getDndPeriod());

        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm a", Locale.getDefault());
        DateTimeFormatter dayFormatter = DateTimeFormatter.ofPattern("dd MMM", Locale.getDefault());

        if (startMuteTime.getDayOfMonth() == LocalDate.now().getDayOfMonth())
            publishNotificationContextText(String.format(mContext.getString(R.string.notification_content_format_today), timeFormatter.format(startMuteTime), timeFormatter.format(endMuteTime)));
        else
            publishNotificationContextText(String.format( mContext.getString(R.string.notification_content_format_not_today), dayFormatter.format(startMuteTime),
                    timeFormatter.format(startMuteTime), timeFormatter.format(endMuteTime)));
    }

    @Override
    public NotificationCompat.Builder getNotificationBuilder() {
        return mNotificationBuilder;
    }

    @Override
    public int getNotificationChannel() {
        return mNotificationChannel;
    }

    @Override
    public void notifyNewAlarmScheduled(Long timing) {
        convertTimestampIntoNotificationContextText(timing);
    }

    private void publishNotificationContextText(String text) {
        mNotificationBuilder.setContentText(text);
        mNotificationManager.notify(NOTIFICATION_ID, mNotificationBuilder.build());
    }
}


