package nz.co.redice.demoservice.services;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;

import androidx.annotation.Nullable;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import nz.co.redice.demoservice.repo.Repository;
import nz.co.redice.demoservice.utils.NotificationHelper;
import nz.co.redice.demoservice.utils.PrefHelper;
import nz.co.redice.demoservice.view.MainActivity;

import static nz.co.redice.demoservice.utils.NotificationHelper.QUIT_APP;

@AndroidEntryPoint
public class ForegroundService extends Service {

    @Inject Repository mRepository;
    @Inject PrefHelper mPrefHelper;
    @Inject NotificationHelper mNotificationHelper;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForeground(1, mNotificationHelper.createForegroundNotification(this));

        if (intent.getBooleanExtra(QUIT_APP, false)) {
            stopSelf();
        }


        return START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
