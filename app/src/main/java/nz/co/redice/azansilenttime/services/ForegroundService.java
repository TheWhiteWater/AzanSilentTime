package nz.co.redice.azansilenttime.services;

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
import nz.co.redice.azansilenttime.repo.Repository;
import nz.co.redice.azansilenttime.utils.DndHelper;
import nz.co.redice.azansilenttime.utils.NotificationHelper;
import nz.co.redice.azansilenttime.utils.PrefHelper;

import static nz.co.redice.azansilenttime.utils.DndHelper.DND_OFF;
import static nz.co.redice.azansilenttime.utils.DndHelper.DND_ON;
import static nz.co.redice.azansilenttime.utils.NotificationHelper.QUIT_APP;

@AndroidEntryPoint
public class ForegroundService extends JobIntentService implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = "App Service";

    private final IBinder mBinder = new LocalBinder();
    @Inject Repository mRepository;
    @Inject PrefHelper mPrefHelper;
    @Inject DndHelper mDndHelper;
    @Inject NotificationHelper mNotificationHelper;
    private NotificationManager mNotificationManager;

    private boolean mChangingConfiguration = false;


    @SuppressLint("CheckResult")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        startForeground(NotificationHelper.NOTIFICATION_ID, mNotificationHelper.mNotificationBuilder.build());

        mPrefHelper.getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

        if (intent.getAction() != null)
            switch (intent.getAction()) {
                case QUIT_APP:
                    stopSelf();
                    break;
                case DND_ON:
                    mDndHelper.turnDndOn();
                    break;
                case DND_OFF:
                    mDndHelper.turnDndOff();
                    break;
            }

        startObservingAlarmTimings();


        mDndHelper.mNextAlarmTime.subscribe(s -> {
            mNotificationHelper.mNotificationBuilder.setContentText(s);
            mNotificationManager.notify(NotificationHelper.NOTIFICATION_ID, mNotificationHelper.mNotificationBuilder.build());
        });


        Log.d(TAG, "onStartCommand: service started");

        return START_REDELIVER_INTENT;
    }

    @SuppressLint("CheckResult")
    private void startObservingAlarmTimings() {
        if (!mPrefHelper.isDndForFridaysOnly())
            mDndHelper.setObserverForRegularDay(LocalDate.now());
        else
            mDndHelper.setObserverForNextFriday(LocalDate.now());
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
            startForeground(NotificationHelper.NOTIFICATION_ID, mNotificationHelper.mNotificationBuilder.build());
        return true;
    }


    @Override
    protected void onHandleWork(@NonNull Intent intent) {

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        if (s.equals(PrefHelper.DND_FRIDAYS_ONLY)) {
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
