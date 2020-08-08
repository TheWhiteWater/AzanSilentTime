package nz.co.redice.demoservice.services;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleService;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import nz.co.redice.demoservice.repo.Repository;
import nz.co.redice.demoservice.utils.NotificationHelper;
import nz.co.redice.demoservice.utils.PrefHelper;

import static nz.co.redice.demoservice.utils.NotificationHelper.QUIT_APP;

@AndroidEntryPoint
public class ForegroundService extends LifecycleService {

    private static final int FAJR_ALARM = 1;
    private static final int DHUR_ALARM = 2;
    private static final int ASR_ALARM = 3;
    private static final int MAGHRIB_ALARM = 4;
    private static final int ISHA_ALARM = 5;
    private static final int NEXT_DAY = 6;

    private static final long ONE_DAY = 1;
    private static final String WAKE_UP = "wake_up";
    private final IBinder mBinder = new LocalBinder();
    @Inject Repository mRepository;
    @Inject PrefHelper mPrefHelper;
    @Inject NotificationHelper mNotificationHelper;
    private boolean mFajrAlarmStatus;
    private boolean mDhurAlarmStatus;
    private boolean mAsrAlarmStatus;
    private boolean mMaghribAlarmStatus;
    private boolean mIshaAlarmStatus;
    private boolean mChangingConfiguration = false;
    private AlarmManager alarmMgr;
    private AudioManager mAudioManager;
    private ZonedDateTime mTargetDayTime;


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        mAudioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        alarmMgr = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);

        mTargetDayTime = LocalDate.now().atStartOfDay(ZoneId.systemDefault());

        if (intent.getBooleanExtra(QUIT_APP, false)) {
            stopSelf();
        }
        if (!mPrefHelper.getDndOnFridaysOnly())
            observeTargetDay(mTargetDayTime);

        if (intent.getBooleanExtra(WAKE_UP, false)) {
            Log.d("App", "onStartCommand:  trigger activated");
            muteAllStreams();
        }


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
        super.onBind(intent);
        Log.d("App", "onBind: Service bound");
        stopForeground(true);
        mChangingConfiguration = false;
        return mBinder;
    }

    @Override
    public void onRebind(Intent intent) {
        stopForeground(true);
        mChangingConfiguration = false;
        Log.d("App", "onRebind: ");
        super.onRebind(intent);
    }


    @Override
    public boolean onUnbind(Intent intent) {
        Log.d("App", "onUnbind: ");
        if (!mChangingConfiguration)
            startForeground(NotificationHelper.NOTIFICATION_ID, mNotificationHelper.createForegroundNotification(this));
        return true;
    }

    private void observeTargetDay(ZonedDateTime day) {
        Long targetDayEpoch = day.toEpochSecond();
        mRepository.getRegularEntry(targetDayEpoch).observe(this, model -> {
            if (model != null) {
                Log.d("App", "setObserverOnTargetDay: " + getCurrentTimeInSeconds());
                if (getCurrentTimeInSeconds() <= model.getFajrEpoch()) {
                    if (model.getFajrSilent()) {
                        setAlarmManager(model.getFajrEpoch(), FAJR_ALARM, true);
                        mFajrAlarmStatus = true;
                        Log.d("App", "Alarm set on fajr. " + model.getDateString() + ", " + model.getFajrString());
                    }
                    if (!model.getFajrSilent() && mFajrAlarmStatus) {
                        setAlarmManager(model.getFajrEpoch(), FAJR_ALARM, false);
                        Log.d("App", "Alarm canceled on fajr. " + model.getDateString() + ", " + model.getFajrString());
                    }
                }
                if (getCurrentTimeInSeconds() <= model.getDhuhrEpoch()) {
                    if (model.getDhuhrSilent()) {
                        setAlarmManager(model.getDhuhrEpoch(), DHUR_ALARM, true);
                        mDhurAlarmStatus = true;
                        Log.d("App", "Alarm set on dhuhr. " + model.getDateString() + ", " + model.getDhuhrString());
                    }
                    if (!model.getDhuhrSilent() && mDhurAlarmStatus) {
                        setAlarmManager(model.getDhuhrEpoch(), DHUR_ALARM, false);
                        Log.d("App", "Alarm canceled on dhuhr. " + model.getDateString() + ", " + model.getDhuhrString());
                    }
                }
                if (getCurrentTimeInSeconds() <= model.getAsrEpoch()) {
                    if (model.getAsrSilent()) {
                        setAlarmManager(model.getAsrEpoch(), ASR_ALARM, true);
                        mAsrAlarmStatus = true;
                        Log.d("App", "Alarm set on asr. " + model.getDateString() + ", " + model.getAsrString());
                    }
                    if (!model.getAsrSilent() && mAsrAlarmStatus) {
                        setAlarmManager(model.getAsrEpoch(), ASR_ALARM, false);
                        Log.d("App", "Alarm canceled on asr. " + model.getDateString() + ", " + model.getAsrString());
                    }
                }
                if (getCurrentTimeInSeconds() <= model.getMaghribEpoch()) {
                    if (model.getMaghribSilent()) {
                        setAlarmManager(model.getMaghribEpoch(), MAGHRIB_ALARM, true);
                        mMaghribAlarmStatus = true;
                        Log.d("App", "Alarm set on maghrib. " + model.getDateString() + ", " + model.getMaghribString());
                    }
                    if (!model.getMaghribSilent() && mMaghribAlarmStatus) {
                        setAlarmManager(model.getMaghribEpoch(), MAGHRIB_ALARM, false);
                        Log.d("App", "Alarm canceled on maghrib. " + model.getDateString() + ", " + model.getMaghribString());
                    }
                }
                if (getCurrentTimeInSeconds() <= model.getIshaEpoch()) {
                    if (model.getIshaSilent()) {
                        setAlarmManager(model.getIshaEpoch(), ISHA_ALARM, true);
                        mIshaAlarmStatus = true;
                        Log.d("App", "Alarm set on isha. " + model.getDateString() + ", " + model.getIshaString());
                    }
                    if (!model.getIshaSilent() && mIshaAlarmStatus) {
                        setAlarmManager(model.getIshaEpoch(), ISHA_ALARM, false);
                        Log.d("App", "Alarm canceled on isha. " + model.getDateString() + ", " + model.getIshaString());
                    }
                } else {
                    Log.d("App", "setObserverOnTargetDay current target : " + mTargetDayTime);
                    ZonedDateTime newTarget = mTargetDayTime.plusDays(ONE_DAY);
                    Log.d("App", "setObserverOnTargetDay new target : " + newTarget);
                    setAlarmManager(newTarget.toEpochSecond(), NEXT_DAY, true);
                    Log.d("App", "setObserverOnTargetDay: observer set on the next day");
                }
            }
        });
    }


    private void setAlarmManager(Long triggerAtSeconds, int requestCode, boolean status) {
        Intent intent = new Intent(getApplicationContext(), ForegroundService.class);
        intent.putExtra(WAKE_UP, true);
        PendingIntent alarmIntent = PendingIntent.getService(getApplicationContext(), requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        if (status) {
            alarmMgr.setExact(AlarmManager.RTC_WAKEUP, triggerAtSeconds * 1000, alarmIntent);
            Log.d("App", "AlarmManager activated on " + triggerAtSeconds);
        } else {
            alarmMgr.cancel(alarmIntent);
            Log.d("App", "AlarmManager canceled on " + triggerAtSeconds);
        }
    }

    private long getCurrentTimeInSeconds() {
        return Calendar.getInstance().getTime().getTime() / 1000;
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

    private void muteAllStreams() {
        mAudioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
        Log.d("App", "setRingerMode: SILENT");
    }

    private void unMuteAllStreams() {
        mAudioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
        Log.d("App", "setRingerMode: UNSILENT");
    }

    public class LocalBinder extends Binder {
        public ForegroundService getService() {
            return ForegroundService.this;
        }
    }
}
