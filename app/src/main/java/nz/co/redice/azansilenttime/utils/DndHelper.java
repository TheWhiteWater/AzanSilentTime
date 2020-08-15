package nz.co.redice.azansilenttime.utils;


import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.util.Log;

import androidx.lifecycle.LifecycleOwner;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.Calendar;
import java.util.Date;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.hilt.android.qualifiers.ApplicationContext;
import nz.co.redice.azansilenttime.repo.Repository;
import nz.co.redice.azansilenttime.services.ForegroundService;

@Singleton
public class DndHelper {

    public static final String DND_ON = "dnd_on";
    public static final String DND_OFF = "dnd_off";
    private static final String TAG = "App DndHelper";
    private static final int FAJR_ALARM = 1;
    private static final int DHUR_ALARM = 2;
    private static final int ASR_ALARM = 3;
    private static final int MAGHRIB_ALARM = 4;
    private static final int ISHA_ALARM = 5;
    private static final int FRIDAY_ALARM = 7;
    private static final long ONE_DAY = 1;
    @Inject PrefHelper mPrefHelper;
    @Inject Repository mRepository;
    private Context mContext;
    private boolean mFajrAlarmActivated;
    private boolean mDhurAlarmActivated;
    private boolean mAsrAlarmActivated;
    private boolean mMaghribAlarmActivated;
    private boolean mIshaAlarmActivated;
    private boolean mFridayAlarmActivated;
    private AlarmManager mAlarmManager;
    private AudioManager mAudioManager;


    @Inject
    public DndHelper(@ApplicationContext Context context) {
        mContext = context;
        mAlarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
    }

    private long getCurrentTimeInSeconds() {
        return Calendar.getInstance().getTime().getTime() / 1000;
    }


    public void setAlarmForRegularDay(LifecycleOwner lifecycleOwner, LocalDate day) {
        Long targetDayEpoch = day.atStartOfDay(ZoneId.systemDefault()).toEpochSecond();


        mRepository.getRegularEntry(targetDayEpoch).observe(lifecycleOwner, model -> {
            if (model != null) {
                boolean notTooLateForFajr = getCurrentTimeInSeconds() <= model.getFajrEpoch();
                boolean isFajrActive = model.getFajrSilent();
                boolean notFridayDnd = !mPrefHelper.getDndOnFridaysOnly();
                boolean isFajrGood2Go = notTooLateForFajr && isFajrActive && notFridayDnd;
                boolean fajrDeactivated = mFajrAlarmActivated && !notFridayDnd || mFajrAlarmActivated && !isFajrActive;

                if (!mFajrAlarmActivated && isFajrGood2Go) {
                    setAlarmManager(model.getFajrEpoch(), FAJR_ALARM, true);
                    mFajrAlarmActivated = true;
                    Log.d(TAG, "Alarm set on fajr. " + model.getDateString() + ", " + model.getFajrString());
                }
                if (fajrDeactivated) {
                    setAlarmManager(model.getFajrEpoch(), FAJR_ALARM, false);
                    Log.d(TAG, "Alarm canceled on fajr. " + model.getDateString() + ", " + model.getFajrString());
                }


                boolean notTooLateForDhuhr = getCurrentTimeInSeconds() <= model.getDhuhrEpoch();
                boolean isDhuhrActive = model.getDhuhrSilent();
                boolean isDhuhrGood2Go = notTooLateForDhuhr && isDhuhrActive && notFridayDnd;
                boolean dhurDeactivated = mDhurAlarmActivated && !notFridayDnd || mDhurAlarmActivated && !isDhuhrActive;

                if (!mDhurAlarmActivated && isDhuhrGood2Go) {
                    setAlarmManager(model.getDhuhrEpoch(), DHUR_ALARM, true);
                    mDhurAlarmActivated = true;
                    Log.d(TAG, "Alarm set on dhuhr. " + model.getDateString() + ", " + model.getDhuhrString());
                }
                if (dhurDeactivated) {
                    setAlarmManager(model.getDhuhrEpoch(), DHUR_ALARM, false);
                    Log.d(TAG, "Alarm canceled on dhuhr. " + model.getDateString() + ", " + model.getDhuhrString());
                }


                boolean notTooLateForAsr = getCurrentTimeInSeconds() <= model.getAsrEpoch();
                boolean isAsrActive = model.getAsrSilent();
                boolean isAsrGood2Go = notTooLateForAsr && isAsrActive && notFridayDnd;
                boolean asrDeactivated = mAsrAlarmActivated && !notFridayDnd || mAsrAlarmActivated && !isAsrActive;

                if (!mAsrAlarmActivated && isAsrGood2Go) {
                    setAlarmManager(model.getAsrEpoch(), ASR_ALARM, true);
                    mAsrAlarmActivated = true;
                    Log.d(TAG, "Alarm set on asr. " + model.getDateString() + ", " + model.getAsrString());
                }
                if (asrDeactivated) {
                    setAlarmManager(model.getAsrEpoch(), ASR_ALARM, false);
                    Log.d(TAG, "Alarm canceled on asr. " + model.getDateString() + ", " + model.getAsrString());
                }


                boolean notTooLateForMaghrib = getCurrentTimeInSeconds() <= model.getMaghribEpoch();
                boolean isMaghribActive = model.getMaghribSilent();
                boolean isMaghribGood2Go = notTooLateForMaghrib && isMaghribActive && notFridayDnd;
                boolean maghribDeactivated = mMaghribAlarmActivated && !notFridayDnd || mMaghribAlarmActivated && !isMaghribActive;

                if (!mMaghribAlarmActivated && isMaghribGood2Go) {
                    setAlarmManager(model.getMaghribEpoch(), MAGHRIB_ALARM, true);
                    mMaghribAlarmActivated = true;
                    Log.d(TAG, "Alarm set on maghrib. " + model.getDateString() + ", " + model.getMaghribString());
                }
                if (maghribDeactivated) {
                    setAlarmManager(model.getMaghribEpoch(), MAGHRIB_ALARM, false);
                    Log.d(TAG, "Alarm canceled on maghrib. " + model.getDateString() + ", " + model.getMaghribString());
                }


                boolean notTooLateForIsha = getCurrentTimeInSeconds() <= model.getIshaEpoch();
                boolean isIshaActive = model.getIshaSilent();
                boolean isIshaGood2Go = notTooLateForIsha && isIshaActive && notFridayDnd;
                boolean ishaDeactivated = mIshaAlarmActivated && !notFridayDnd || mIshaAlarmActivated && !isIshaActive;

                if (!mIshaAlarmActivated && isIshaGood2Go) {
                    setAlarmManager(model.getIshaEpoch(), ISHA_ALARM, true);
                    mIshaAlarmActivated = true;
                    Log.d(TAG, "Alarm set on isha. " + model.getDateString() + ", " + model.getIshaString());
                }
                if (ishaDeactivated) {
                    setAlarmManager(model.getIshaEpoch(), ISHA_ALARM, false);
                    Log.d(TAG, "Alarm canceled on isha. " + model.getDateString() + ", " + model.getIshaString());
                }

                if (!mPrefHelper.getDndOnFridaysOnly() && getCurrentTimeInSeconds() > model.getIshaEpoch()) {
                    setAlarmForRegularDay(lifecycleOwner, day.plusDays(ONE_DAY));
                    Log.d(TAG, "setObserverOnTargetDay: observer set on the next day");
                }

            }
        });

    }

    public void setAlarmForNextFriday(LifecycleOwner lifecycleOwner, LocalDate date) {
        LocalDate targetDay;
        if (date.getDayOfWeek() != DayOfWeek.FRIDAY) {
            targetDay = date.with(TemporalAdjusters.next(DayOfWeek.FRIDAY));
        } else {
            targetDay = date;
        }
        Long targetEpoch = targetDay.atStartOfDay(ZoneId.systemDefault()).toEpochSecond();


        mRepository.getFridayEntry(targetEpoch).observe(lifecycleOwner, fridayEntry -> {
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
        });
    }

    public void setAlarmManager(Long triggerAtSeconds, int requestCode, boolean status) {

        Intent intent = new Intent(mContext, ForegroundService.class);
        intent.setAction(DND_ON);
        PendingIntent dndOnIntent = PendingIntent.getService(mContext, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        if (status) {
            mAlarmManager.setAlarmClock(new AlarmManager.AlarmClockInfo(triggerAtSeconds * 1000, null), dndOnIntent);
            Log.d(TAG, "AlarmManager activated on " + triggerAtSeconds * 1000);
        }
        if (!status) {
            mAlarmManager.cancel(dndOnIntent);
            Log.d(TAG, "AlarmManager canceled on " + triggerAtSeconds * 1000);
        }
    }

    public String getNextAlarmTime() {
        if (mAlarmManager.getNextAlarmClock() != null) {
            Date date = new Date(mAlarmManager.getNextAlarmClock().getTriggerTime());
            DateFormat onlyTimeFormat = new SimpleDateFormat("hh:mm a");
            if (date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().getDayOfMonth() == LocalDate.now().getDayOfMonth()) {
                return "Today at " + onlyTimeFormat.format(date);
            }
            DateFormat onlyDayFormat = new SimpleDateFormat("dd MMM");
            Log.d(TAG, "getNextAlarmTime: " + onlyDayFormat.format(date) + " at " + onlyTimeFormat.format(date));
            return "Next DND on " + onlyDayFormat.format(date) + " at " + onlyTimeFormat.format(date);
        } else
            return "DND hasn't set";
    }


    @SuppressLint("CheckResult")
    public void turnDndOn() {
        Log.d(TAG, "turnDndOn: ");
        mAudioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
        Log.d(TAG, "setRingerMode: SILENT");

        Intent intent = new Intent(mContext, ForegroundService.class);
        intent.setAction(DND_OFF);
        PendingIntent dndOffIntent = PendingIntent.getService(mContext, 999, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        long delayTime = (getCurrentTimeInSeconds() * 1000) + (mPrefHelper.getDndPeriod() * 60 * 1000);
        mAlarmManager.setExact(AlarmManager.RTC, delayTime, dndOffIntent);
    }

    public void turnDndOff() {
        mAudioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
        Log.d(TAG, "setRingerMode: UNSILENT");
    }
}
