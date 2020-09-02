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
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.hilt.android.qualifiers.ApplicationContext;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import nz.co.redice.azansilenttime.repo.Repository;
import nz.co.redice.azansilenttime.repo.local.entity.FridaySchedule;
import nz.co.redice.azansilenttime.repo.local.entity.RegularSchedule;
import nz.co.redice.azansilenttime.services.foreground_service.ForegroundService;
import nz.co.redice.azansilenttime.utils.SharedPreferencesHelper;
import nz.co.redice.azansilenttime.view.presentation.Converters;

@Singleton
public class AlarmServiceImpl implements AlarmService {

    private static final String TAG = "App AlarmService";
    private static final int REGULAR_ALARM = 123;
    private static final int FRIDAY_ALARM = 321;
    private static final long ONE_DAY = 1;
    private SharedPreferencesHelper mSharedPreferencesHelper;
    private Repository mRepository;
    private AlarmStatus mScheduledAlarm;
    private Context mContext;
    private AlarmManager mAlarmManager;
    private OnNewAlarmListener mAlarmListener;


    @Inject
    public AlarmServiceImpl(@ApplicationContext Context context,
                            SharedPreferencesHelper sharedPreferencesHelper,
                            Repository repository, AlarmStatus alarmStatus) {
        mContext = context;
        mAlarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        mSharedPreferencesHelper = sharedPreferencesHelper;
        mScheduledAlarm = alarmStatus;
        mRepository = repository;
    }

    private long getCurrentEpochSeconds() {
        return System.currentTimeMillis() / 1000;
    }

    @Override
    @SuppressLint("CheckResult")
    public void getSchedulesFromNextTwoDaysStartingFrom(LocalDate day) {
        Long startDayEpoch = day.atStartOfDay(ZoneId.systemDefault()).toEpochSecond();
        Long endDayEpoch = day.plusDays(ONE_DAY).atStartOfDay(ZoneId.systemDefault()).toEpochSecond();

        mRepository.selectTwoDaysForAlarmSetting(startDayEpoch, endDayEpoch)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::processRegularSchedules);
    }

    private void processRegularSchedules(List<RegularSchedule> regularSchedules) {
        List<Schedule> schedules = new ArrayList<>();
        for (RegularSchedule s : regularSchedules) {
            schedules.add(new Schedule(s.getFajrEpochSecond(), s.isFajrMute()));
            schedules.add(new Schedule(s.getDhuhrEpochSecond(), s.isDhuhrMute()));
            schedules.add(new Schedule(s.getAsrEpochSecond(), s.isAsrMute()));
            schedules.add(new Schedule(s.getMaghribEpochSecond(), s.isMaghribMute()));
            schedules.add(new Schedule(s.getIshaEpochSecond(), s.isIshaMute()));
        }
        setRegularSchedule(getEarliestSchedule(schedules));
    }

    public void setRegularSchedule(Schedule earliestSchedule) {

        if (earliestSchedule == null) {
            if (mScheduledAlarm.isActive())
                cancelScheduledAlarm(mScheduledAlarm.getTiming(), REGULAR_ALARM);
            mAlarmListener.onAlarmScheduled(null);
        } else {
            boolean isFridayModeActive = mSharedPreferencesHelper.isFridaysOnlyModeActive();
            boolean isEarlierTimingDetected = mScheduledAlarm.isActive() && mScheduledAlarm.getTiming() != earliestSchedule.epochSecond;
            boolean isNewTimingSameAsOld = mScheduledAlarm.isActive() && mScheduledAlarm.getTiming() == earliestSchedule.epochSecond;

            if (!isFridayModeActive) {
                if (!mScheduledAlarm.isActive())
                    scheduleAlarm(earliestSchedule.epochSecond, REGULAR_ALARM);

                if (isNewTimingSameAsOld) {
                    return;
                }
                if (isEarlierTimingDetected) {
                    cancelScheduledAlarm(mScheduledAlarm.getTiming(), REGULAR_ALARM);
                    scheduleAlarm(earliestSchedule.epochSecond, REGULAR_ALARM);
                }
            } else {
                if (mScheduledAlarm.isActive())
                    cancelScheduledAlarm(mScheduledAlarm.getTiming(), REGULAR_ALARM);
            }
        }

    }


    public void scheduleAlarm(Long timing, int requestCode) {
        Intent intent = new Intent(mContext, ForegroundService.class);
        intent.setAction(DND_ON);
        PendingIntent dndOnIntent = PendingIntent.getService(mContext, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        mAlarmManager.setAlarmClock(new AlarmManager.AlarmClockInfo(timing * 1000, null), dndOnIntent);
        Log.d(TAG, "AlarmManager activated on " + timing * 1000 + ": "
                + Converters.convertEpochIntoTextDate(timing) + ", " + Converters.convertEpochIntoTextTime(timing));

        mScheduledAlarm.setAlarmStatus(true);
        mScheduledAlarm.setAlarmTiming(timing);
        mAlarmListener.onAlarmScheduled(timing);
    }


    public void cancelScheduledAlarm(Long timing, int requestCode) {
        Intent intent = new Intent(mContext, ForegroundService.class);
        intent.setAction(DND_OFF);
        PendingIntent dndIntent = PendingIntent.getService(mContext, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        mAlarmManager.cancel(dndIntent);
        mScheduledAlarm.setAlarmStatus(false);
        Log.d(TAG, "AlarmManager canceled on " + timing * 1000 + ": "
                + Converters.convertEpochIntoTextDate(timing) + ", " + Converters.convertEpochIntoTextTime(timing));

    }


    @Override
    public synchronized Schedule getEarliestSchedule(List<Schedule> unsortedList) {
        Schedule earliestSchedule;
        ArrayList<Schedule> sortedList = unsortedList
                .stream()
                .sorted(Schedule::compareTo)
                .filter(schedule -> schedule.isActive)
                .filter(schedule -> schedule.epochSecond > getCurrentEpochSeconds())
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

        mRepository.selectTwoFridaysForAlarmSetting(startDayEpoch, endDayEpoch)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::processFridaySchedules);
    }

    private void processFridaySchedules(List<FridaySchedule> fridayEntries) {
        List<Schedule> schedules = new ArrayList<>();
        for (FridaySchedule s : fridayEntries) {
            schedules.add(new Schedule(s.getEpochSecond(), s.isSilent()));
        }
        setFridaySchedule(getEarliestSchedule(schedules));
    }

    private void setFridaySchedule(Schedule earliestSchedule) {
        if (earliestSchedule == null) {
            if (mScheduledAlarm.isActive())
                cancelScheduledAlarm(mScheduledAlarm.getTiming(), FRIDAY_ALARM);
            mAlarmListener.onAlarmScheduled(null);
        } else {
            boolean isFridayModeActive = mSharedPreferencesHelper.isFridaysOnlyModeActive();
            boolean isEarlierTimingDetected = mScheduledAlarm.isActive() && mScheduledAlarm.getTiming() != earliestSchedule.epochSecond;
            boolean isNewTimingSameAsOld = mScheduledAlarm.isActive() && mScheduledAlarm.getTiming() == earliestSchedule.epochSecond;

            if (isFridayModeActive) {
                if (!mScheduledAlarm.isActive())
                    scheduleAlarm(earliestSchedule.epochSecond, FRIDAY_ALARM);

                if (isNewTimingSameAsOld) {
                    return;
                }
                if (isEarlierTimingDetected) {
                    cancelScheduledAlarm(mScheduledAlarm.getTiming(), FRIDAY_ALARM);
                    scheduleAlarm(earliestSchedule.epochSecond, FRIDAY_ALARM);
                }
            } else {
                if (mScheduledAlarm.isActive())
                    cancelScheduledAlarm(mScheduledAlarm.getTiming(), FRIDAY_ALARM);
            }
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
    public Long getAlarmTimestamp() {
        if (mScheduledAlarm != null) {
            return mScheduledAlarm.getTiming();
        } else
            return null;
    }

    @Override
    public void registerAlarmListener(OnNewAlarmListener onNewAlarmListener) {
        mAlarmListener = onNewAlarmListener;
    }

    private LocalDate getNextFriday(LocalDate day) {
        return day.with(TemporalAdjusters.next(DayOfWeek.FRIDAY));
    }


}
