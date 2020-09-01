package nz.co.redice.azansilenttime.services.foreground_service;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.JobIntentService;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import nz.co.redice.azansilenttime.services.notification_service.NotificationServiceImpl;
import nz.co.redice.azansilenttime.utils.SharedPreferencesHelper;

import static nz.co.redice.azansilenttime.services.alarm_service.AlarmService.DND_OFF;
import static nz.co.redice.azansilenttime.services.alarm_service.AlarmService.DND_ON;
import static nz.co.redice.azansilenttime.services.notification_service.NotificationServiceImpl.QUIT_APP;

@AndroidEntryPoint
public class ForegroundService extends JobIntentService implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = "App Service";

    private final IBinder mBinder = new LocalBinder();
    @Inject SharedPreferencesHelper mSharedPreferencesHelper;
    @Inject AlarmFacade mAlarmFacade;
    private NotificationManager mNotificationManager;

    private boolean mChangingConfiguration = false;


    @SuppressLint("CheckResult")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        startForeground(NotificationServiceImpl.NOTIFICATION_ID, mAlarmFacade.getNotificationBuilder().build());
        mSharedPreferencesHelper.getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

        if (intent.getAction() != null)
            switch (intent.getAction()) {
                case QUIT_APP:
                    stopSelf();
                    break;
                case DND_ON:
                    turnAudioServicesOn();
                    break;
                case DND_OFF:
                    turnAudioServicesOff();
                    break;
            }

        startObservingAlarmTimings();

        mAlarmFacade.getNextAlarmTimeBehaviorSubject().subscribe(s -> {
            mAlarmFacade.getNotificationBuilder().setContentText(s);
            mNotificationManager.notify(NotificationServiceImpl.NOTIFICATION_ID, mAlarmFacade.getNotificationBuilder().build());
        });

        Log.d(TAG, "onStartCommand: service started");

        return START_REDELIVER_INTENT;
    }

    @SuppressLint("CheckResult")
    private void startObservingAlarmTimings() {
        if (!mSharedPreferencesHelper.isFridaysOnlyModeActive())
            mAlarmFacade.getSchedulesFromNextTwoDaysStartingFrom(LocalDate.now());
        else
            mAlarmFacade.getSchedulesFromNextTwoFridaysStartingFrom(LocalDate.now());
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mChangingConfiguration = true;
    }

    public void turnAudioServicesOn() {
        mAlarmFacade.turnAudioServicesOn();
        Log.d(TAG, "RingerMode: SILENT");
        mAlarmFacade.scheduleWakeUpAlarm();
    }

    public void turnAudioServicesOff() {
        mAlarmFacade.turnAudioServicesOff();
        Log.d(TAG, "RingerMode: UNSILENT");
    }


    @Nullable
    @Override
    public IBinder onBind(@NotNull Intent intent) {
        super.onBind(intent);
        Log.d(TAG, "onBind: Service bound");
        stopForeground(true);
        mChangingConfiguration = false;
        return mBinder;
    }

    @Override
    public void onRebind(Intent intent) {
        Log.d(TAG, "onRebind: ");
        stopForeground(true);
        mChangingConfiguration = false;
        super.onRebind(intent);
    }


    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "onUnbind: ");
        if (!mChangingConfiguration)
            startForeground(NotificationServiceImpl.NOTIFICATION_ID, mAlarmFacade.getNotificationBuilder().build());
        return true;
    }


    @Override
    protected void onHandleWork(@NonNull Intent intent) {

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        if (s.equals(SharedPreferencesHelper.FRIDAYS_ONLY_MODE)) {
            Log.d(TAG, "onSharedPreferenceChanged: ");
            startObservingAlarmTimings();
        }
    }

    public class LocalBinder extends Binder {
        public ForegroundService getService() {
            return ForegroundService.this;
        }
    }


}
