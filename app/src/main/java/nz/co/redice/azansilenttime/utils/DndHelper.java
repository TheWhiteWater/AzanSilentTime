package nz.co.redice.azansilenttime.utils;


import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.util.Log;

import androidx.lifecycle.LifecycleOwner;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.hilt.android.qualifiers.ApplicationContext;
import nz.co.redice.azansilenttime.repo.Repository;
import nz.co.redice.azansilenttime.services.ForegroundService;
import nz.co.redice.azansilenttime.view.presentation.Converters;

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
    private ArrayList<Long> mActiveAlarmlist = new ArrayList<>();
    private boolean mFajrAlarmActivated;
    private boolean mDhuhrAlarmActivated;
    private boolean mAsrAlarmActivated;
    private boolean mMaghribAlarmActivated;
    private boolean mIshaAlarmActivated;
    private boolean mFridayAlarmActivated;
    private boolean mNextDayAlarmActivated;
    private Context mContext;
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


    public void setObserverForRegularDay(LifecycleOwner lifecycleOwner, LocalDate day) {
        Long targetDayEpoch = day.atStartOfDay(ZoneId.systemDefault()).toEpochSecond();
        mRepository.getRegularEntry(targetDayEpoch).observe(lifecycleOwner, model -> {
            if (model != null) {
                mFajrAlarmActivated = doTiming(model.getFajrEpoch(), model.getFajrSilent(), FAJR_ALARM, mFajrAlarmActivated);
                mDhuhrAlarmActivated = doTiming(model.getDhuhrEpoch(), model.getDhuhrSilent(), DHUR_ALARM, mDhuhrAlarmActivated);
                mMaghribAlarmActivated = doTiming(model.getMaghribEpoch(), model.getMaghribSilent(), MAGHRIB_ALARM, mMaghribAlarmActivated);
                mAsrAlarmActivated = doTiming(model.getAsrEpoch(), model.getAsrSilent(), ASR_ALARM, mAsrAlarmActivated);
                mIshaAlarmActivated = doTiming(model.getIshaEpoch(), model.getIshaSilent(), ISHA_ALARM, mIshaAlarmActivated);

                if (!mFajrAlarmActivated && !mDhuhrAlarmActivated && !mAsrAlarmActivated && !mMaghribAlarmActivated && !mIshaAlarmActivated
                        && !mPrefHelper.getDndOnFridaysOnly()) {
                    Log.d(TAG, "launching observer for the next day");
                    setObserverForRegularDay(lifecycleOwner, LocalDate.now().plusDays(ONE_DAY));
                    mNextDayAlarmActivated = true;
                }
                if (mNextDayAlarmActivated && !mPrefHelper.getDndOnFridaysOnly()) {
                    setObserverForRegularDay(lifecycleOwner, LocalDate.now().plusDays(ONE_DAY));
                }

            }
        });


    }

    private boolean doTiming(Long epoch, boolean active, int requestCode, boolean dispatcher) {
        boolean notTooLateForTiming = getCurrentTimeInSeconds() <= epoch;
        boolean isTimingActive = active;
        boolean notFridayDnd = !mPrefHelper.getDndOnFridaysOnly();
        boolean isTimingGood2Go = notTooLateForTiming && isTimingActive && notFridayDnd;
        boolean timingToBeDeactivated = dispatcher && !notFridayDnd || dispatcher && !isTimingActive;

        if (!dispatcher && isTimingGood2Go) {
            setAlarmManager(epoch, requestCode, true);
            dispatcher = true;
            Log.d(TAG, "Alarm set on . " + Converters.setDateFromLong(epoch) + ", " + Converters.setTimeFromLong(epoch));
        }
        if (timingToBeDeactivated) {
            setAlarmManager(epoch, requestCode, false);
            dispatcher = false;
            Log.d(TAG, "Alarm canceled on . " + Converters.setDateFromLong(epoch) + ", " + Converters.setTimeFromLong(epoch));
        }
        return dispatcher;
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
                    if (!fridayEntry.getSilent() && mFridayAlarmActivated
                            || mFridayAlarmActivated && !mPrefHelper.getDndOnFridaysOnly()) {
                        setAlarmManager(fridayEntry.getTimeEpoch(), FRIDAY_ALARM, false);
                        Log.d(TAG, "Alarm canceled for friday. " + fridayEntry.getDateString() + ", " + fridayEntry.getTimeString());
                    }
                }
            }
        });
    }

    public boolean setAlarmManager(Long timing, int requestCode, boolean status) {

        Intent intent = new Intent(mContext, ForegroundService.class);
        intent.setAction(DND_ON);
        PendingIntent dndOnIntent = PendingIntent.getService(mContext, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        if (status) {
            mAlarmManager.setAlarmClock(new AlarmManager.AlarmClockInfo(timing * 1000, null), dndOnIntent);
            mActiveAlarmlist.add(timing * 1000);
            Log.d(TAG, "AlarmManager activated on " + timing * 1000);
            return true;
        }

        mAlarmManager.cancel(dndOnIntent);
        mActiveAlarmlist.remove(timing * 1000);
        Log.d(TAG, "AlarmManager canceled on " + timing * 1000);
        return true;
    }

    public String getNextAlarmTime() {
        if (mActiveAlarmlist.size() > 0) {
            Collections.sort(mActiveAlarmlist);
            Date date = new Date(mActiveAlarmlist.get(0));

            LocalDateTime localDateTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

            DateTimeFormatter onlyTimeFormat = DateTimeFormatter.ofPattern("HH:mm a", Locale.getDefault());
            DateTimeFormatter onlyDayFormat = DateTimeFormatter.ofPattern("dd MMM", Locale.getDefault());

            if (localDateTime.getDayOfMonth() == LocalDate.now().getDayOfMonth()) {
                return "Today at " + onlyTimeFormat.format(localDateTime);
            }
            Log.d(TAG, "getNextAlarmTime: " + onlyDayFormat.format(localDateTime) + " at " + onlyTimeFormat.format(localDateTime));
            Log.d(TAG, "getNextAlarmTime: value " + mActiveAlarmlist.get(0));
            return "Next DND on " + onlyDayFormat.format(localDateTime) + " at " + onlyTimeFormat.format(localDateTime);
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
