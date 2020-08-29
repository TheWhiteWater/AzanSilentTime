package nz.co.redice.azansilenttime.utils;


import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.util.Log;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.hilt.android.qualifiers.ApplicationContext;
import io.reactivex.subjects.BehaviorSubject;
import nz.co.redice.azansilenttime.repo.Repository;
import nz.co.redice.azansilenttime.repo.local.entity.FridayEntry;
import nz.co.redice.azansilenttime.repo.local.entity.RegularEntry;
import nz.co.redice.azansilenttime.services.ForegroundService;
import nz.co.redice.azansilenttime.view.presentation.Converters;

@Singleton
public class DndHelper {

    public static final String DND_ON = "dnd_on";
    public static final String DND_OFF = "dnd_off";
    private static final String TAG = "App DndHelper";
    private static final int REGULAR_ALARM = 123;
    private static final int FRIDAY_ALARM = 321;
    private static final long ONE_DAY = 1;
    public BehaviorSubject<String> mNextAlarmTime;
    @Inject PrefHelper mPrefHelper;
    @Inject Repository mRepository;
    @Inject AlarmStatus mCurrentAlarmCash;
    private ArrayList<Long> mActivatedAlarmList = new ArrayList<>();
    private ArrayList<SubEntry> mTimings;

    private Context mContext;
    private AlarmManager mAlarmManager;
    private AudioManager mAudioManager;


    @Inject
    public DndHelper(@ApplicationContext Context context) {
        mContext = context;
        mAlarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        mNextAlarmTime = BehaviorSubject.create();
        mTimings = new ArrayList<>();
    }

    private long getCurrentTimeInSeconds() {
        return System.currentTimeMillis() / 1000;
    }


    @SuppressLint("CheckResult")
    public void setObserverForRegularDay(LocalDate day) {
        Long startDayEpoch = day.atStartOfDay(ZoneId.systemDefault()).toEpochSecond();
        Long endDayEpoch = day.plusDays(ONE_DAY).atStartOfDay(ZoneId.systemDefault()).toEpochSecond();

        mRepository.selectTwoDaysForAlarmSetting(startDayEpoch, endDayEpoch)
                .subscribe(x -> {
                        mTimings.clear();
                        for (RegularEntry s : x) {
                            mTimings.add(new SubEntry(s.getFajrEpoch(), s.getFajrSilent()));
                            mTimings.add(new SubEntry(s.getDhuhrEpoch(), s.getDhuhrSilent()));
                            mTimings.add(new SubEntry(s.getAsrEpoch(), s.getAsrSilent()));
                            mTimings.add(new SubEntry(s.getMaghribEpoch(), s.getMaghribSilent()));
                            mTimings.add(new SubEntry(s.getIshaEpoch(), s.getIshaSilent()));
                        }
                        processRegularTiming(mTimings);
                });

    }

    private void processRegularTiming(ArrayList<SubEntry> timings) {
        Collections.sort(timings);
        ArrayList<SubEntry> newList = mTimings.stream().sorted(SubEntry::compareTo)
                .filter(subEntry -> (subEntry.timing > getCurrentTimeInSeconds()) && subEntry.activeStatus)
                .collect(Collectors.toCollection(ArrayList::new));

        SubEntry earliestSubEntry = null;
        if (newList.size() > 0) {
            earliestSubEntry = newList.get(0);
            Log.d(TAG, "processRegularTiming: earliest timing is " + Converters.setTimeFromLong(earliestSubEntry.timing)
                    + " " + Converters.getDateFromLong(earliestSubEntry.timing));
        }


        boolean isFridaysOnlyActive = mPrefHelper.isDndForFridaysOnly();

        if (!isFridaysOnlyActive && earliestSubEntry != null) {
            if (mCurrentAlarmCash.isAlarmActive()) {
                if (mCurrentAlarmCash.getAlarmTiming() == earliestSubEntry.timing) {
                    return;
                }

                if (mCurrentAlarmCash.getAlarmTiming() != earliestSubEntry.timing) {
                    setAlarmManager(mCurrentAlarmCash.getAlarmTiming(), REGULAR_ALARM, false);
                    mCurrentAlarmCash.setAlarmActive(false);
                }
            }

            setAlarmManager(earliestSubEntry.timing, REGULAR_ALARM, true);
            mCurrentAlarmCash.setAlarmActive(true);
            mCurrentAlarmCash.setAlarmTiming(earliestSubEntry.timing);
        }
        if (isFridaysOnlyActive && mCurrentAlarmCash.isAlarmActive()) {
            setAlarmManager(mCurrentAlarmCash.getAlarmTiming(), REGULAR_ALARM, false);
            mCurrentAlarmCash.setAlarmActive(false);
        }
    }


    @SuppressLint("CheckResult")
    public void setObserverForNextFriday(LocalDate day) {

        LocalDate startFriday = calcNextFriday(day.minusDays(ONE_DAY));
        LocalDate endFriday = calcNextFriday(startFriday);
        Long startDayEpoch = startFriday.atStartOfDay(ZoneId.systemDefault()).toEpochSecond();
        Long endDayEpoch = endFriday.atStartOfDay(ZoneId.systemDefault()).toEpochSecond();

        mRepository.selectTwoFridaysForAlarmSetting(startDayEpoch, endDayEpoch)
                .subscribe(x -> {
                        mTimings.clear();
                        for (FridayEntry s : x) {
                            mTimings.add(new SubEntry(s.getTimeEpoch(), s.getSilent()));
                        }
                        processFridayTiming(mTimings);
                });
    }

    private void processFridayTiming(ArrayList<SubEntry> timings) {
        Collections.sort(timings);
        ArrayList<SubEntry> newList = mTimings.stream().sorted(SubEntry::compareTo)
                .filter(subEntry -> (subEntry.timing > getCurrentTimeInSeconds()) && subEntry.activeStatus)
                .collect(Collectors.toCollection(ArrayList::new));

        SubEntry earliestSubEntry = null;
        if (newList.size() > 0) {
            earliestSubEntry = newList.get(0);
            Log.d(TAG, "processFRIDAYTiming: earliest timing is " + Converters.setTimeFromLong(earliestSubEntry.timing)
                    + " " + Converters.getDateFromLong(earliestSubEntry.timing));
        }


        boolean isFridaysOnlyActive = mPrefHelper.isDndForFridaysOnly();

        if (isFridaysOnlyActive && earliestSubEntry != null) {
            if (mCurrentAlarmCash.isAlarmActive()) {
                if (mCurrentAlarmCash.getAlarmTiming() == earliestSubEntry.timing) {
                    return;
                }

                if (mCurrentAlarmCash.getAlarmTiming() != earliestSubEntry.timing) {
                    setAlarmManager(mCurrentAlarmCash.getAlarmTiming(), FRIDAY_ALARM, false);
                    mCurrentAlarmCash.setAlarmActive(false);
                }
            }

            setAlarmManager(earliestSubEntry.timing, FRIDAY_ALARM, true);
            mCurrentAlarmCash.setAlarmActive(true);
            mCurrentAlarmCash.setAlarmTiming(earliestSubEntry.timing);
        }
        if (!isFridaysOnlyActive && mCurrentAlarmCash.isAlarmActive()) {
            setAlarmManager(mCurrentAlarmCash.getAlarmTiming(), FRIDAY_ALARM, false);
            mCurrentAlarmCash.setAlarmActive(false);
        }
    }


    public void setAlarmManager(Long timing, int requestCode, boolean toBeActivated) {
        Intent intent = new Intent(mContext, ForegroundService.class);
        intent.setAction(DND_ON);
        PendingIntent dndOnIntent = PendingIntent.getService(mContext, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        if (toBeActivated) {
            mAlarmManager.setAlarmClock(new AlarmManager.AlarmClockInfo(timing * 1000, null), dndOnIntent);
            if (!mActivatedAlarmList.contains(timing * 1000))
                mActivatedAlarmList.add(timing * 1000);

            Log.d(TAG, "AlarmManager activated on " + timing * 1000 + ": " + Converters.getDateFromLong(timing) + ", " + Converters.setTimeFromLong(timing));
        } else {
            mAlarmManager.cancel(dndOnIntent);
            mActivatedAlarmList.remove(timing * 1000);
            Log.d(TAG, "AlarmManager canceled on " + timing * 1000 + ": " + Converters.getDateFromLong(timing) + ", " + Converters.setTimeFromLong(timing));
        }
        updateNotificationContext();
    }

    public void updateNotificationContext() {
        Collections.sort(mActivatedAlarmList);
        if (mActivatedAlarmList.size() > 0) {
            Date date = new Date(mActivatedAlarmList.get(0));

            LocalDateTime startMuteTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            LocalDateTime endMuteTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().plusMinutes(mPrefHelper.getDndPeriod());

            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm a", Locale.getDefault());
            DateTimeFormatter dayFormatter = DateTimeFormatter.ofPattern("dd MMM", Locale.getDefault());

            if (startMuteTime.getDayOfMonth() == LocalDate.now().getDayOfMonth())
                mNextAlarmTime.onNext("today between " + timeFormatter.format(startMuteTime)
                        + " - " + timeFormatter.format(endMuteTime));
            else
                mNextAlarmTime.onNext(dayFormatter.format(startMuteTime) + " between "
                        + timeFormatter.format(startMuteTime) + " - " + timeFormatter.format(endMuteTime));
        }
    }

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

    private LocalDate calcNextFriday(LocalDate day) {
        return day.with(TemporalAdjusters.next(DayOfWeek.FRIDAY));
    }

    class SubEntry implements Comparable<SubEntry> {
        final Long timing;
        final Boolean activeStatus;

        public SubEntry(Long timing, Boolean activeStatus) {
            this.timing = timing;
            this.activeStatus = activeStatus;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            SubEntry subEntry = (SubEntry) o;

            if (!timing.equals(subEntry.timing)) return false;
            return activeStatus.equals(subEntry.activeStatus);
        }

        @Override
        public int hashCode() {
            int result = timing.hashCode();
            result = 31 * result + activeStatus.hashCode();
            return result;
        }

        @Override
        public int compareTo(SubEntry subEntry) {
            if (this.timing > subEntry.timing) {
                return 1;
            } else if (this.timing < subEntry.timing) {
                return -1;
            } else {
                return 0;
            }
        }
    }

}
