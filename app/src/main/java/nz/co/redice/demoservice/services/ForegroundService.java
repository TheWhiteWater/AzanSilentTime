package nz.co.redice.demoservice.services;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import java.time.LocalDate;
import java.time.ZoneId;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import nz.co.redice.demoservice.repo.Repository;
import nz.co.redice.demoservice.repo.local.entity.EntryModel;
import nz.co.redice.demoservice.utils.NotificationHelper;
import nz.co.redice.demoservice.utils.PrefHelper;

import static nz.co.redice.demoservice.utils.NotificationHelper.QUIT_APP;

@AndroidEntryPoint
public class ForegroundService extends Service {

    @Inject Repository mRepository;
    @Inject PrefHelper mPrefHelper;
    @Inject NotificationHelper mNotificationHelper;
    private final IBinder mBinder = new LocalBinder();
    private EntryModel mTimings;
    private boolean mChangingConfiguration = false;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent.getBooleanExtra(QUIT_APP, false)) {

            stopSelf();
        }

//        setCurrentDay(mTimings);
//        setNextAlarm();
        Log.d("App", "onStartCommand: service started");
        return START_REDELIVER_INTENT;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mChangingConfiguration = true;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d("App", "onBind: Service bound");
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
        Log.d("App", "onUnbind: !!!!!!!!");
        if (!mChangingConfiguration)
            startForeground(NotificationHelper.NOTIFICATION_ID, mNotificationHelper.createForegroundNotification(this));
        return true;
    }

    private void setCurrentDay(EntryModel timings) {
        Long currentDayEpoch = LocalDate.now()
                .atStartOfDay(ZoneId.systemDefault())
                .toEpochSecond();

//        if (mTimings == null || !mTimings.getDate().equals(currentDayEpoch)) {
//            mTimings = mRepository.getTimesForSelectedDate(currentDayEpoch).getValue();
//        }
    }

    private void setNextAlarm() {

    }

    public boolean serviceIsRunningInForeground(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(
                Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(
                Integer.MAX_VALUE)) {
            if (getClass().getName().equals(service.service.getClassName())) {
                if (service.foreground) {
                    return true;
                }
            }
        }
        return false;
    }

    public class LocalBinder extends Binder {
        public ForegroundService getService() {
            return ForegroundService.this;
        }
    }
}
