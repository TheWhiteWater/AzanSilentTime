package nz.co.redice.azansilenttime.services.alarm_service;


import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.hilt.android.qualifiers.ApplicationContext;
import nz.co.redice.azansilenttime.services.foreground_service.ForegroundService;
import nz.co.redice.azansilenttime.repo.Repository;
import nz.co.redice.azansilenttime.repo.local.entity.FridayEntry;
import nz.co.redice.azansilenttime.repo.local.entity.RegularSchedule;
import nz.co.redice.azansilenttime.utils.SharedPreferencesHelper;
import nz.co.redice.azansilenttime.view.presentation.Converters;

@Singleton
public class AlarmServiceImpl implements AlarmService {

    private static final String TAG = "App DndHelper";
    private static final int REGULAR_ALARM = 123;
    private static final int FRIDAY_ALARM = 321;
    private static final long ONE_DAY = 1;
    boolean isFridaysOnlyModeActive;
    private SharedPreferencesHelper mSharedPreferencesHelper;
    private Repository mRepository;
    private AlarmStatus mCurrentAlarmCash;
    private ArrayList<Long> mActivatedAlarmList;
    private Context mContext;
    private AlarmManager mAlarmManager;
    private OnNewAlarmListener mAlarmListener;


    @Inject
    public AlarmServiceImpl(@ApplicationContext Context context,
                            SharedPreferencesHelper sharedPreferencesHelper,
                            Repository repository, AlarmStatus alarmStatus) {
        mContext = context;
        mAlarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        mActivatedAlarmList = new ArrayList<>();
        mSharedPreferencesHelper = sharedPreferencesHelper;
        mCurrentAlarmCash = alarmStatus;
        mRepository = repository;
        isFridaysOnlyModeActive = mSharedPreferencesHelper.isFridaysOnlyModeActive();
    }

    private long getCurrentEpochSeconds() {
        return System.currentTimeMillis() / 1000;
    }

    public ArrayList<Long> getActivatedAlarmList() {
        return mActivatedAlarmList;
    }

    @Override
    @SuppressLint("CheckResult")
    public void getSchedulesFromNextTwoDaysStartingFrom(LocalDate day) {
        Long startDayEpoch = day.atStartOfDay(ZoneId.systemDefault()).toEpochSecond();
        Long endDayEpoch = day.plusDays(ONE_DAY).atStartOfDay(ZoneId.systemDefault()).toEpochSecond();
        ArrayList<Schedule> schedules = new ArrayList<>();

        mRepository.selectTwoDaysForAlarmSetting(startDayEpoch, endDayEpoch)
                .subscribe(x -> {
                    for (RegularSchedule s : x) {
                        schedules.add(new Schedule(s.getFajrEpochSecond(), s.isFajrMute()));
                        schedules.add(new Schedule(s.getDhuhrEpochSecond(), s.isDhuhrMute()));
                        schedules.add(new Schedule(s.getAsrEpochSecond(), s.isAsrMute()));
                        schedules.add(new Schedule(s.getMaghribEpochSecond(), s.isMaghribMute()));
                        schedules.add(new Schedule(s.getIshaEpochSecond(), s.isIshaMute()));
                    }
                    processRegularSchedule(getEarliestSchedule(schedules));
                }, e -> {
                    Log.d(TAG, "setObserverForRegularDay: " + e.getMessage());
                    Log.d(TAG, "setObserverForRegularDay: " + Arrays.toString(e.getStackTrace()));
                });
    }



    public void processRegularSchedule(Schedule earliestSchedule) {
        if (!isFridaysOnlyModeActive && earliestSchedule != null) {
            if (mCurrentAlarmCash.isAlarmActive()) {
                if (mCurrentAlarmCash.getAlarmTiming() == earliestSchedule.epochSecond) {
                    return;
                }
                if (mCurrentAlarmCash.getAlarmTiming() != earliestSchedule.epochSecond) {
                    cancelScheduledMuteAlarm(mCurrentAlarmCash.getAlarmTiming(), REGULAR_ALARM);
                    mCurrentAlarmCash.setAlarmStatus(false);
                }
            } else {
                scheduleMuteAlarm(earliestSchedule.epochSecond, REGULAR_ALARM);
                mCurrentAlarmCash.setAlarmStatus(true);
                mCurrentAlarmCash.setAlarmTiming(earliestSchedule.epochSecond);
            }
        } else
            return;
        if (isFridaysOnlyModeActive && mCurrentAlarmCash.isAlarmActive()) {
            cancelScheduledMuteAlarm(mCurrentAlarmCash.getAlarmTiming(), REGULAR_ALARM);
            mCurrentAlarmCash.setAlarmStatus(false);
        }
    }

    @Override
    public Schedule getEarliestSchedule(List<Schedule> unsortedList) {
        Schedule earliestSchedule;
        ArrayList<Schedule> sortedList = unsortedList
                .stream()
                .sorted(Schedule::compareTo)
                .filter(schedule -> (schedule.epochSecond > getCurrentEpochSeconds()) && schedule.isActive)
                .collect(Collectors.toCollection(ArrayList::new));
        if (sortedList.size() > 0) {
            earliestSchedule = sortedList.get(0);
            Log.d(TAG, "The earliest timing is " + Converters.convertEpochIntoTextTime(earliestSchedule.epochSecond)
                    + " " + Converters.convertEpochIntoTextDate(earliestSchedule.epochSecond));
            return earliestSchedule;
        } else
            return null;
    }


    @Override
    @SuppressLint("CheckResult")
    public void getSchedulesFromNextTwoFridaysStartingFrom(LocalDate day) {
        LocalDate startFriday = getNextFriday(day.minusDays(ONE_DAY));
        LocalDate endFriday = getNextFriday(startFriday);
        Long startDayEpoch = startFriday.atStartOfDay(ZoneId.systemDefault()).toEpochSecond();
        Long endDayEpoch = endFriday.atStartOfDay(ZoneId.systemDefault()).toEpochSecond();
        ArrayList<Schedule> schedules = new ArrayList<>();

        mRepository.selectTwoFridaysForAlarmSetting(startDayEpoch, endDayEpoch)
                .subscribe(x -> {
                    for (FridayEntry s : x) {
                        schedules.add(new Schedule(s.getTimeEpoch(), s.getSilent()));
                    }
                    processFridaySchedule(getEarliestSchedule(schedules));
                }, e -> {
                    Log.d(TAG, "setObserverForRegularDay: " + e.getMessage());
                    Log.d(TAG, "setObserverForRegularDay: " + Arrays.toString(e.getStackTrace()));
                });
    }

    public void processFridaySchedule(Schedule earliestSchedule) {
        if (isFridaysOnlyModeActive && earliestSchedule != null) {
            if (mCurrentAlarmCash.isAlarmActive()) {
                if (mCurrentAlarmCash.getAlarmTiming() == earliestSchedule.epochSecond) {
                    return;
                }
                if (mCurrentAlarmCash.getAlarmTiming() != earliestSchedule.epochSecond) {
                    cancelScheduledMuteAlarm(mCurrentAlarmCash.getAlarmTiming(), FRIDAY_ALARM);
                    mCurrentAlarmCash.setAlarmStatus(false);
                }
            } else {
                scheduleMuteAlarm(earliestSchedule.epochSecond, FRIDAY_ALARM);
                mCurrentAlarmCash.setAlarmStatus(true);
                mCurrentAlarmCash.setAlarmTiming(earliestSchedule.epochSecond);
            }
        }
        if (!isFridaysOnlyModeActive && mCurrentAlarmCash.isAlarmActive()) {
            cancelScheduledMuteAlarm(mCurrentAlarmCash.getAlarmTiming(), FRIDAY_ALARM);
            mCurrentAlarmCash.setAlarmStatus(false);
        }
    }


    @Override
    public void scheduleMuteAlarm(Long timing, int requestCode) {
        if (!mActivatedAlarmList.contains(timing * 1000)) {
            Intent intent = new Intent(mContext, ForegroundService.class);
            intent.setAction(DND_ON);
            PendingIntent dndOnIntent = PendingIntent.getService(mContext, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            mAlarmManager.setAlarmClock(new AlarmManager.AlarmClockInfo(timing * 1000, null), dndOnIntent);
            mActivatedAlarmList.add(timing * 1000);
            mAlarmListener.onNewAlarmSet(getEarliestAlarmTimestamp());
            Log.d(TAG, "AlarmManager activated on " + timing * 1000 + ": "
                    + Converters.convertEpochIntoTextDate(timing) + ", " + Converters.convertEpochIntoTextTime(timing));
        }
    }

    @Override
    public void cancelScheduledMuteAlarm(Long timing, int requestCode) {
        if (mActivatedAlarmList.contains(timing * 1000)) {
            Intent intent = new Intent(mContext, ForegroundService.class);
            intent.setAction(DND_OFF);
            PendingIntent dndOnIntent = PendingIntent.getService(mContext, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            mAlarmManager.cancel(dndOnIntent);
            mActivatedAlarmList.remove(timing * 1000);
            mAlarmListener.onNewAlarmSet(getEarliestAlarmTimestamp());
            Log.d(TAG, "AlarmManager canceled on " + timing * 1000 + ": "
                    + Converters.convertEpochIntoTextDate(timing) + ", " + Converters.convertEpochIntoTextTime(timing));
        }
    }

    @Override
    public void scheduleWakeUpAlarm() {
        Intent intent = new Intent(mContext, ForegroundService.class);
        intent.setAction(DND_OFF);
        PendingIntent dndOffIntent = PendingIntent.getService(mContext, 999, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        long delayTime = (getCurrentEpochSeconds() * 1000) + (mSharedPreferencesHelper.getDndPeriod() * 60 * 1000);
        mAlarmManager.setExact(AlarmManager.RTC, delayTime, dndOffIntent);
    }

    @Override
    public Long getEarliestAlarmTimestamp() {
        if (mActivatedAlarmList.size() > 0) {
            return mActivatedAlarmList.get(0);
        } else
            return null;
    }

    @Override
    public void registerNewAlarmListener(OnNewAlarmListener onNewAlarmListener) {
        mAlarmListener = onNewAlarmListener;
    }

    private LocalDate getNextFriday(LocalDate day) {
        return day.with(TemporalAdjusters.next(DayOfWeek.FRIDAY));
    }


}
