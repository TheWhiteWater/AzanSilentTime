package nz.co.redice.azansilenttime.services.alarm_service;


import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

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
import nz.co.redice.azansilenttime.ui.presentation.Converters;
import nz.co.redice.azansilenttime.utils.SharedPreferencesHelper;

@Singleton
public class AlarmServiceImpl implements AlarmService {

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


    /**
     * Observes 2 RegularSchedule for further processing.
     *
     * @param day - start date of 2 day range.
     */
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

    /**
     * Processes passed in schedule list and schedules alarm to launch PendingIntent,
     * which is switching the phone into Do Not Disturb mode.
     *
     * @param regularSchedules -  RegularSchedule list to be processed.
     */
    private void processRegularSchedules(List<RegularSchedule> regularSchedules) {
        List<Schedule> schedules = new ArrayList<>();
        for (RegularSchedule s : regularSchedules) {
            schedules.add(new Schedule(s.getFajrEpochSecond(), s.isFajrMute()));
            schedules.add(new Schedule(s.getDhuhrEpochSecond(), s.isDhuhrMute()));
            schedules.add(new Schedule(s.getAsrEpochSecond(), s.isAsrMute()));
            schedules.add(new Schedule(s.getMaghribEpochSecond(), s.isMaghribMute()));
            schedules.add(new Schedule(s.getIshaEpochSecond(), s.isIshaMute()));
        }
        scheduleRegularAlarm(getEarliestSchedule(schedules));
    }


    /**
     * Schedules Regular Alarm
     *
     * @param earliestSchedule - alarm to be scheduled
     */
    public void scheduleRegularAlarm(Schedule earliestSchedule) {
        if (earliestSchedule == null) {
            if (mScheduledAlarm.isActive())
                cancelScheduledAlarm(REGULAR_ALARM);
            mAlarmListener.notifyNewAlarmScheduled(null);
        } else {
            boolean isFridayModeActive = mSharedPreferencesHelper.isFridaysOnlyModeActive();
            boolean isEarlierTimingDetected = mScheduledAlarm.isActive() && mScheduledAlarm.getEpochSeconds() != earliestSchedule.epochSecond;
            boolean isNewTimingSameAsOld = mScheduledAlarm.isActive() && mScheduledAlarm.getEpochSeconds() == earliestSchedule.epochSecond;

            if (!isFridayModeActive) {
                if (!mScheduledAlarm.isActive())
                    setNewAlarm(earliestSchedule.epochSecond, REGULAR_ALARM);

                if (isNewTimingSameAsOld) {
                    return;
                }
                if (isEarlierTimingDetected) {
                    if (mScheduledAlarm.getEpochSeconds() != getCurrentEpochSeconds())
                        cancelScheduledAlarm(REGULAR_ALARM);
                    setNewAlarm(earliestSchedule.epochSecond, REGULAR_ALARM);
                }
            } else {
                if (mScheduledAlarm.isActive())
                    cancelScheduledAlarm(REGULAR_ALARM);
            }
        }

    }

    /**
     * Creates new alarm
     *
     * @param timing      - time in milliseconds;
     * @param requestCode - alarm id.
     */
    public void setNewAlarm(Long timing, int requestCode) {
        Intent intent = new Intent(mContext, ForegroundService.class);
        intent.setAction(DO_NOT_DISTURB_ON);
        PendingIntent dndOnIntent = PendingIntent.getService(mContext, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mAlarmManager.setAlarmClock(new AlarmManager.AlarmClockInfo(Converters.convertEpochSecondsIntoTimeMillis(timing), null), dndOnIntent);
        mScheduledAlarm.setAlarmStatus(true);
        mScheduledAlarm.setEpochSeconds(timing);
        mAlarmListener.notifyNewAlarmScheduled(Converters.convertEpochSecondsIntoTimeMillis(timing));
    }

    /**
     * Cancels previously scheduled alarm
     *
     * @param requestCode - alarm id.
     */
    public void cancelScheduledAlarm(int requestCode) {
        Intent intent = new Intent(mContext, ForegroundService.class);
        intent.setAction(DO_NOT_DISTURB_OFF);
        PendingIntent dndIntent = PendingIntent.getService(mContext, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mAlarmManager.cancel(dndIntent);
        mScheduledAlarm.setAlarmStatus(false);
    }

    /**
     * Sorts Schedule list.
     *
     * @param unsortedList - list to be sorted;
     * @return - Schedule which is having the earliest time against current time and being active.
     */
    public Schedule getEarliestSchedule(List<Schedule> unsortedList) {
        Schedule earliestSchedule;
        ArrayList<Schedule> sortedList = unsortedList
                .stream()
                .sorted(Schedule::compareTo)
                .filter(schedule -> schedule.isActive)
                .filter(schedule -> schedule.epochSecond > getCurrentEpochSeconds())
                .collect(Collectors.toCollection(ArrayList::new));
        if (sortedList.size() > 0) {
            earliestSchedule = sortedList.get(0);
            return earliestSchedule;
        } else
            return null;
    }

    /**
     * Observes 2 FridaySchedule for further processing.
     *
     * @param day - start date of 2 day range.
     */
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


    /**
     * Processes passed in schedule list and schedules alarm to launch PendingIntent,
     * which is switching the phone into Do Not Disturb mode.
     *
     * @param fridayEntries - FridaySchedule list to be process.
     */
    private void processFridaySchedules(List<FridaySchedule> fridayEntries) {
        List<Schedule> schedules = new ArrayList<>();
        for (FridaySchedule s : fridayEntries) {
            schedules.add(new Schedule(s.getEpochSecond(), s.isMute()));
        }
        scheduleFridayAlarm(getEarliestSchedule(schedules));
    }


    /**
     * Schedules Friday Alarms
     *
     * @param earliestSchedule - alarm to be scheduled
     */
    private void scheduleFridayAlarm(Schedule earliestSchedule) {
        if (earliestSchedule == null) {
            if (mScheduledAlarm.isActive())
                cancelScheduledAlarm(FRIDAY_ALARM);
            mAlarmListener.notifyNewAlarmScheduled(null);
        } else {
            boolean isFridayModeActive = mSharedPreferencesHelper.isFridaysOnlyModeActive();
            boolean isEarlierTimingDetected = mScheduledAlarm.isActive() && mScheduledAlarm.getEpochSeconds() != earliestSchedule.epochSecond;
            boolean isNewTimingSameAsOld = mScheduledAlarm.isActive() && mScheduledAlarm.getEpochSeconds() == earliestSchedule.epochSecond;

            if (isFridayModeActive) {
                if (!mScheduledAlarm.isActive())
                    setNewAlarm(earliestSchedule.epochSecond, FRIDAY_ALARM);

                if (isNewTimingSameAsOld) {
                    return;
                }
                if (isEarlierTimingDetected) {
                    if (mScheduledAlarm.getEpochSeconds() != getCurrentEpochSeconds())
                        cancelScheduledAlarm(FRIDAY_ALARM);
                    setNewAlarm(earliestSchedule.epochSecond, FRIDAY_ALARM);
                }
            } else {
                if (mScheduledAlarm.isActive())
                    cancelScheduledAlarm(FRIDAY_ALARM);
            }
        }

    }

    /**
     * Sets post DND alarm to return phone back to normal
     */
    @Override
    public void setPostAlarmWakeUp() {
        Intent intent = new Intent(mContext, ForegroundService.class);
        intent.setAction(DO_NOT_DISTURB_OFF);
        PendingIntent dndOffIntent = PendingIntent.getService(mContext, 999, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        long delayTime = (System.currentTimeMillis() + (mSharedPreferencesHelper.getDndPeriod() * 60 * 1000));
        mAlarmManager.setExact(AlarmManager.RTC, delayTime, dndOffIntent);
    }

    @Override
    public void registerAlarmListener(OnNewAlarmListener onNewAlarmListener) {
        mAlarmListener = onNewAlarmListener;
    }


    /**
     * Returns next friday
     *
     * @param day - date of the week, target Friday belongs to.
     * @return next friday date
     */
    private LocalDate getNextFriday(LocalDate day) {
        return day.with(TemporalAdjusters.next(DayOfWeek.FRIDAY));
    }


}
