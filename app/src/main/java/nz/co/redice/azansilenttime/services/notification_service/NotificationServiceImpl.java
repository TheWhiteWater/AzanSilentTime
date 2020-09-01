package nz.co.redice.azansilenttime.services.notification_service;


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
import io.reactivex.subjects.BehaviorSubject;
import nz.co.redice.azansilenttime.R;
import nz.co.redice.azansilenttime.services.alarm_service.OnNewAlarmListener;
import nz.co.redice.azansilenttime.services.foreground_service.ForegroundService;
import nz.co.redice.azansilenttime.utils.SharedPreferencesHelper;
import nz.co.redice.azansilenttime.view.MainActivity;

@Singleton
public class NotificationServiceImpl implements NotificationService {

    public static final String QUIT_APP = "quit app";
    // The identifier for the notification displayed for the foreground service.
    public static final int NOTIFICATION_ID = 12345678;
    private static final String FOREGROUND_CHANNEL_ID = "channelId";
    public BehaviorSubject<String> mNextAlarmTime;
    private SharedPreferencesHelper mSharedPreferencesHelper;


    public NotificationCompat.Builder mNotificationBuilder;


    @Inject
    public NotificationServiceImpl(@ApplicationContext Context context,
                                   SharedPreferencesHelper sharedPreferencesHelper) {
        mSharedPreferencesHelper = sharedPreferencesHelper;
        mNextAlarmTime = BehaviorSubject.create();


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel foregroundNotificationChannel = new NotificationChannel(
                    FOREGROUND_CHANNEL_ID,
                    "ForegroundChannelId",
                    NotificationManager.IMPORTANCE_LOW);

            NotificationManager manager = context.getSystemService(NotificationManager.class);
            manager.createNotificationChannel(foregroundNotificationChannel);

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
                .addAction(R.drawable.ic_settings, "Home", activityPendingIntent)
                .addAction(R.drawable.ic_cancel, "Quit", servicePendingIntent)
                .setContentTitle("Next Mute period:")
                .setContentText("Not set yet")
                .setSmallIcon(R.drawable.ic_blc_small);
    }




    public void generateNotificationText(Long timing) {
            Date date = new Date(timing);

            LocalDateTime startMuteTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            LocalDateTime endMuteTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().plusMinutes(mSharedPreferencesHelper.getDndPeriod());

            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm a", Locale.getDefault());
            DateTimeFormatter dayFormatter = DateTimeFormatter.ofPattern("dd MMM", Locale.getDefault());

            if (startMuteTime.getDayOfMonth() == LocalDate.now().getDayOfMonth())
                mNextAlarmTime.onNext("today between " + timeFormatter.format(startMuteTime)
                        + " - " + timeFormatter.format(endMuteTime));
            else
                mNextAlarmTime.onNext(dayFormatter.format(startMuteTime) + " between "
                        + timeFormatter.format(startMuteTime) + " - " + timeFormatter.format(endMuteTime));
        }

    @Override
    public NotificationCompat.Builder getNotificationBuilder() {
        return mNotificationBuilder;
    }

    public BehaviorSubject<String> getNextAlarmTextTimeBehaviorSubject() {
        return mNextAlarmTime;
    }

    @Override
    public void onNewAlarmSet(Long timing) {
        generateNotificationText(timing);
    }
}


