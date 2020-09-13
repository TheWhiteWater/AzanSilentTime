package nz.co.redice.azansilenttime.services.foreground_service;

import android.annotation.SuppressLint;
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
import nz.co.redice.azansilenttime.utils.SharedPreferencesHelper;

import static nz.co.redice.azansilenttime.services.alarm_service.AlarmService.DO_NOT_DISTURB_OFF;
import static nz.co.redice.azansilenttime.services.alarm_service.AlarmService.DO_NOT_DISTURB_ON;
import static nz.co.redice.azansilenttime.services.notification_service.NotificationServiceImpl.QUIT_APP;

@AndroidEntryPoint
public class ForegroundService extends JobIntentService implements SharedPreferences.OnSharedPreferenceChangeListener {

    private final IBinder mBinder = new LocalBinder();
    @Inject SharedPreferencesHelper mSharedPreferencesHelper;
    @Inject ForegroundFacade mForegroundFacade;

    private boolean mChangingConfiguration = false;


    @SuppressLint("CheckResult")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        startForeground(mForegroundFacade.getNotificationChannel(), mForegroundFacade.getNotificationBuilder().build());
        mSharedPreferencesHelper.getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

        if (intent.getAction() != null)
            switch (intent.getAction()) {
                case QUIT_APP:
                    stopSelf();
                    break;
                case DO_NOT_DISTURB_ON:
                    mForegroundFacade.turnDndOn();
                    mForegroundFacade.scheduleWakeUpAlarm();
                    break;
                case DO_NOT_DISTURB_OFF:
                    mForegroundFacade.turnDndOff();
                    break;
            }

        startObservingAlarmTimings();
        return START_REDELIVER_INTENT;
    }

    @SuppressLint("CheckResult")
    private void startObservingAlarmTimings() {
//        if (!mSharedPreferencesHelper.isFridaysOnlyModeActive())
//            mForegroundFacade.getSchedulesFromNextTwoDaysStartingFrom(LocalDate.now());
//        else
//            mForegroundFacade.getSchedulesFromNextTwoFridaysStartingFrom(LocalDate.now());
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mChangingConfiguration = true;
    }


    @Nullable
    @Override
    public IBinder onBind(@NotNull Intent intent) {
        super.onBind(intent);
        stopForeground(true);
        mChangingConfiguration = false;
        return mBinder;
    }

    @Override
    public void onRebind(Intent intent) {
        stopForeground(true);
        mChangingConfiguration = false;
        super.onRebind(intent);
    }


    @Override
    public boolean onUnbind(Intent intent) {
        if (!mChangingConfiguration)
            startForeground(mForegroundFacade.getNotificationChannel(), mForegroundFacade.getNotificationBuilder().build());
        return true;
    }


    @Override
    protected void onHandleWork(@NonNull Intent intent) {

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        if (s.equals(SharedPreferencesHelper.FRIDAYS_ONLY_MODE)) {
            startObservingAlarmTimings();
        }
    }

    public class LocalBinder extends Binder {
        public ForegroundService getService() {
            return ForegroundService.this;
        }
    }


}
