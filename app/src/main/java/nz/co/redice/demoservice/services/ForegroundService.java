package nz.co.redice.demoservice.services;

import android.annotation.SuppressLint;
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
import androidx.lifecycle.Observer;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.Calendar;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import nz.co.redice.demoservice.repo.Repository;
import nz.co.redice.demoservice.repo.local.entity.FridayEntry;
import nz.co.redice.demoservice.utils.NotificationHelper;
import nz.co.redice.demoservice.utils.PrefHelper;

import static nz.co.redice.demoservice.utils.NotificationHelper.QUIT_APP;

@AndroidEntryPoint
public class ForegroundService extends LifecycleService {

    private static final String TAG = "App Service";
    private static final int FAJR_ALARM = 1;
    private static final int DHUR_ALARM = 2;
    private static final int ASR_ALARM = 3;
    private static final int MAGHRIB_ALARM = 4;
    private static final int ISHA_ALARM = 5;
    private static final int NEXT_DAY_MORNING_ALARM = 6;
    private static final int FRIDAY_ALARM = 7;

    private static final long ONE_DAY = 1;
    private static final String WAKE_UP = "wake_up";
    private final IBinder mBinder = new LocalBinder();
    @Inject Repository mRepository;
    @Inject PrefHelper mPrefHelper;
    @Inject NotificationHelper mNotificationHelper;
    private boolean mFajrAlarmActivated;
    private boolean mDhurAlarmActivated;
    private boolean mAsrAlarmActivated;
    private boolean mMaghribAlarmActivated;
    private boolean mIshaAlarmActivated;

    private boolean mFridayAlarmActivated;

    private boolean mChangingConfiguration = false;
    private AlarmManager alarmMgr;
    private AudioManager mAudioManager;


    @SuppressLint("CheckResult")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        startForeground(NotificationHelper.NOTIFICATION_ID, mNotificationHelper.createForegroundNotification(this));
        mAudioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        alarmMgr = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);


        if (intent.getBooleanExtra(QUIT_APP, false)) {
            stopSelf();
        }

        if (!mPrefHelper.getDndOnFridaysOnly()) {
            setAlarmForRegularDay(LocalDate.now());
        } else {
            setAlarmForNextFriday(LocalDate.now());
        }

        mPrefHelper.getDndForFridayOnly()
                .observeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> {
                            Log.d(TAG, "onStartCommand: DND status = " + s);
                            setAlarmForRegularDay(LocalDate.now());
                            setAlarmForNextFriday(LocalDate.now());
                        }
                );


        if (intent.getBooleanExtra(WAKE_UP, false)) {
            Log.d(TAG, "onStartCommand:  trigger activated");
            muteAllStreams();
        }


        Log.d(TAG, "onStartCommand: service started");
        return START_REDELIVER_INTENT;
    }

    private void setAlarmForNextFriday(LocalDate date) {
        LocalDate targetDay = date.with(TemporalAdjusters.next(DayOfWeek.FRIDAY));
        Long targetEpoch = targetDay.atStartOfDay(ZoneId.systemDefault()).toEpochSecond();
        mRepository.getFridayEntry(targetEpoch).observe(this, new Observer<FridayEntry>() {
            @Override
            public void onChanged(FridayEntry fridayEntry) {
                if (fridayEntry != null) {
                    if (getCurrentTimeInSeconds() <= fridayEntry.getTimeEpoch()) {
                        if (fridayEntry.getSilent() && mPrefHelper.getDndOnFridaysOnly()) {
                            setAlarmManager(fridayEntry.getTimeEpoch(), FRIDAY_ALARM, true);
                            mFridayAlarmActivated = true;
                            Log.d(TAG, "Alarm set for friday. " + fridayEntry.getDateString() + ", " + fridayEntry.getTimeString());
                        }
                        if (!fridayEntry.getSilent() && mFridayAlarmActivated || mFridayAlarmActivated && !mPrefHelper.getDndOnFridaysOnly()) {
                            setAlarmManager(fridayEntry.getTimeEpoch(), FRIDAY_ALARM, false);
                            Log.d(TAG, "Alarm canceled for friday. " + fridayEntry.getDateString() + ", " + fridayEntry.getTimeString());
                        }
                    }
                }
            }
        });
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
            startForeground(NotificationHelper.NOTIFICATION_ID, mNotificationHelper.createForegroundNotification(this));
        return true;
    }

    @SuppressLint("CheckResult")
    private void setAlarmForRegularDay(LocalDate day) {
        Long targetDayEpoch = day.atStartOfDay(ZoneId.systemDefault()).toEpochSecond();
        mRepository.getRegularEntry(targetDayEpoch).observe(this, model -> {
            if (model != null) {
                Log.d(TAG, "setObserverOnTargetDay: " + getCurrentTimeInSeconds());
                if (getCurrentTimeInSeconds() <= model.getFajrEpoch()) {
                    if (model.getFajrSilent() && !mPrefHelper.getDndOnFridaysOnly()) {
                        setAlarmManager(model.getFajrEpoch(), FAJR_ALARM, true);
                        mFajrAlarmActivated = true;
                        Log.d(TAG, "Alarm set on fajr. " + model.getDateString() + ", " + model.getFajrString());
                    }
                    if (!model.getFajrSilent() && mFajrAlarmActivated || mFajrAlarmActivated && mPrefHelper.getDndOnFridaysOnly()) {
                        setAlarmManager(model.getFajrEpoch(), FAJR_ALARM, false);
                        Log.d(TAG, "Alarm canceled on fajr. " + model.getDateString() + ", " + model.getFajrString());
                    }
                }
                if (getCurrentTimeInSeconds() <= model.getDhuhrEpoch()) {
                    if (model.getDhuhrSilent() && !mPrefHelper.getDndOnFridaysOnly()) {
                        setAlarmManager(model.getDhuhrEpoch(), DHUR_ALARM, true);
                        mDhurAlarmActivated = true;
                        Log.d(TAG, "Alarm set on dhuhr. " + model.getDateString() + ", " + model.getDhuhrString());
                    }
                    if (!model.getDhuhrSilent() && mDhurAlarmActivated || mDhurAlarmActivated && mPrefHelper.getDndOnFridaysOnly()) {
                        setAlarmManager(model.getDhuhrEpoch(), DHUR_ALARM, false);
                        Log.d(TAG, "Alarm canceled on dhuhr. " + model.getDateString() + ", " + model.getDhuhrString());
                    }
                }
                if (getCurrentTimeInSeconds() <= model.getAsrEpoch()) {
                    if (model.getAsrSilent() && !mPrefHelper.getDndOnFridaysOnly()) {
                        setAlarmManager(model.getAsrEpoch(), ASR_ALARM, true);
                        mAsrAlarmActivated = true;
                        Log.d(TAG, "Alarm set on asr. " + model.getDateString() + ", " + model.getAsrString());
                    }
                    if (!model.getAsrSilent() && mAsrAlarmActivated || mAsrAlarmActivated && mPrefHelper.getDndOnFridaysOnly()) {
                        setAlarmManager(model.getAsrEpoch(), ASR_ALARM, false);
                        Log.d(TAG, "Alarm canceled on asr. " + model.getDateString() + ", " + model.getAsrString());
                    }
                }
                if (getCurrentTimeInSeconds() <= model.getMaghribEpoch()) {
                    if (model.getMaghribSilent() && !mPrefHelper.getDndOnFridaysOnly()) {
                        setAlarmManager(model.getMaghribEpoch(), MAGHRIB_ALARM, true);
                        mMaghribAlarmActivated = true;
                        Log.d(TAG, "Alarm set on maghrib. " + model.getDateString() + ", " + model.getMaghribString());
                    }
                    if (!model.getMaghribSilent() && mMaghribAlarmActivated || mMaghribAlarmActivated && mPrefHelper.getDndOnFridaysOnly()) {
                        setAlarmManager(model.getMaghribEpoch(), MAGHRIB_ALARM, false);
                        Log.d(TAG, "Alarm canceled on maghrib. " + model.getDateString() + ", " + model.getMaghribString());
                    }
                }
                if (getCurrentTimeInSeconds() <= model.getIshaEpoch()) {
                    if (model.getIshaSilent() && !mPrefHelper.getDndOnFridaysOnly()) {
                        setAlarmManager(model.getIshaEpoch(), ISHA_ALARM, true);
                        mIshaAlarmActivated = true;
                        Log.d(TAG, "Alarm set on isha. " + model.getDateString() + ", " + model.getIshaString());
                    }
                    if (!model.getIshaSilent() && mIshaAlarmActivated || mIshaAlarmActivated && mPrefHelper.getDndOnFridaysOnly()) {
                        setAlarmManager(model.getIshaEpoch(), ISHA_ALARM, false);
                        Log.d(TAG, "Alarm canceled on isha. " + model.getDateString() + ", " + model.getIshaString());
                    }
                } else {
                    if (!mPrefHelper.getDndOnFridaysOnly()) {
                        Log.d(TAG, "setObserverOnTargetDay current target : " + day);
                        Long nextDay = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).plusDays(ONE_DAY).toEpochSecond();
                        Log.d(TAG, "setObserverOnTargetDay new target : " + nextDay);
                        setAlarmManager(nextDay, NEXT_DAY_MORNING_ALARM, true);
                        Log.d(TAG, "setObserverOnTargetDay: observer set on the next day");
                    }
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
            Log.d(TAG, "AlarmManager activated on " + triggerAtSeconds);
        } else {
            alarmMgr.cancel(alarmIntent);
            Log.d(TAG, "AlarmManager canceled on " + triggerAtSeconds);
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
        Log.d(TAG, "setRingerMode: SILENT");
    }

    private void unMuteAllStreams() {
        mAudioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
        Log.d(TAG, "setRingerMode: UNSILENT");
    }

    public class LocalBinder extends Binder {
        public ForegroundService getService() {
            return ForegroundService.this;
        }
    }
}
