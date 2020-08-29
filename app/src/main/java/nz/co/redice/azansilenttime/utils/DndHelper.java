package nz.co.redice.azansilenttime.utils;


import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.util.Log;

import androidx.lifecycle.LifecycleOwner;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.hilt.android.qualifiers.ApplicationContext;
import io.reactivex.subjects.BehaviorSubject;
import nz.co.redice.azansilenttime.repo.Repository;
import nz.co.redice.azansilenttime.services.ForegroundService;
import nz.co.redice.azansilenttime.view.presentation.Converters;

@Singleton
public class DndHelper {

    public static final String DND_ON = "dnd_on";
    public static final String DND_OFF = "dnd_off";
    private static final String TAG = "App DndHelper";
    private static final int ALARM = 7777;
    private static final long ONE_DAY = 1;
    public BehaviorSubject<String> mNextAlarmTime;
    @Inject PrefHelper mPrefHelper;
    @Inject Repository mRepository;
    @Inject AlarmStatus mCurrentAlarmStatus;
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
    }

    private long getCurrentTimeInSeconds() {
        return Calendar.getInstance().getTime().getTime() / 1000;
    }


    public void setObserverForRegularDay(LifecycleOwner lifecycleOwner, LocalDate day) {
        Long targetDayEpoch = day.atStartOfDay(ZoneId.systemDefault()).toEpochSecond();
        mTimings = new ArrayList<>();

        mRepository.getSelectedRegularLiveData(targetDayEpoch).observe(lifecycleOwner, entry -> {
            if (entry != null) {
                mTimings.add(new SubEntry(entry.getFajrEpoch(), entry.getFajrSilent()));
                mTimings.add(new SubEntry(entry.getDhuhrEpoch(), entry.getDhuhrSilent()));
                mTimings.add(new SubEntry(entry.getAsrEpoch(), entry.getAsrSilent()));
                mTimings.add(new SubEntry(entry.getMaghribEpoch(), entry.getMaghribSilent()));
                mTimings.add(new SubEntry(entry.getIshaEpoch(), entry.getIshaSilent()));
                processRegularTiming(mTimings);

                if (!mCurrentAlarmStatus.isAlarmActive()) {
                    setObserverForRegularDay(lifecycleOwner, day.plusDays(ONE_DAY));
                }

            }
        });

    }

    private void processRegularTiming(ArrayList<SubEntry> timings) {
        Collections.sort(timings);

        for (int i = 0; i < timings.size(); i++) {

            if (
//                    !timings.get(i).activeStatus ||
                            timings.get(i).timing < getCurrentTimeInSeconds())
                Log.d(TAG, "processRegularTiming: removing " + Converters.setTimeFromLong(timings.get(i).timing));
                timings.remove(i);
        }

//        for (SubEntry s: timings) {
//            if (!s.activeStatus || s.timing < getCurrentTimeInSeconds())
//                timings.remove(s);
//        }

//        mTimings = timings;


        if (timings.size() > 0) {
            SubEntry earliestSubEntry = timings.get(0);
            Log.d(TAG, "processRegularTiming: earliest timing is " + Converters.setTimeFromLong(earliestSubEntry.timing) + " " + Converters.getDateFromLong(earliestSubEntry.timing)
                    + " activeStatus " + earliestSubEntry.activeStatus);
        } else {
            Log.d(TAG, "processRegularTiming: Opsy the array seems  empty");
        }


//        boolean isFridaysOnlyActive = mPrefHelper.isDndForFridaysOnly();
//
//        if (!isFridaysOnlyActive && earliestSubEntry.activeStatus) {
//            if (mCurrentAlarmStatus.isAlarmActive()) {
//                if (mCurrentAlarmStatus.getAlarmTiming() == earliestSubEntry.timing) {
//                    return;
//                }
//
//                if (mCurrentAlarmStatus.getAlarmTiming() != earliestSubEntry.timing) {
//                    setAlarmManager(mCurrentAlarmStatus.getAlarmTiming(), ALARM, false);
//                    mCurrentAlarmStatus.setAlarmActive(false);
//                }
//            }
//
//            setAlarmManager(earliestSubEntry.timing, ALARM, true);
//            mCurrentAlarmStatus.setAlarmActive(true);
//            mCurrentAlarmStatus.setAlarmTiming(earliestSubEntry.timing);
//        }

        mCurrentAlarmStatus.setAlarmActive(true);
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

            LocalDateTime localDateTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm a", Locale.getDefault());
            DateTimeFormatter dayFormatter = DateTimeFormatter.ofPattern("dd MMM", Locale.getDefault());

            if (localDateTime.getDayOfMonth() == LocalDate.now().getDayOfMonth()) {
                mNextAlarmTime.onNext("Next Do Not Disturb today at " + timeFormatter.format(localDateTime));
            }
            mNextAlarmTime.onNext("Next Do Not Disturb on " + dayFormatter.format(localDateTime) + " at " + timeFormatter.format(localDateTime));
        } else
            mNextAlarmTime.onNext("Next day");
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
            }
            if (this.timing < subEntry.timing) {
                return 0;
            } else {
                return -1;
            }
        }
    }

}
