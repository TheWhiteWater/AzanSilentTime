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
    private static final int AZAN_ALARM = 7777;
    private static final long ONE_DAY = 1;
    public BehaviorSubject<String> mNextAlarmTime;
    @Inject PrefHelper mPrefHelper;
    @Inject Repository mRepository;
    private ArrayList<Long> mActivatedAlarmList = new ArrayList<>();
    private AzanAlarmStatus mAzanAlarmStatus;
    private boolean mNextDayAlarmActivated;
    private boolean mNextFridayAlarmActivated;

    private Context mContext;
    private AlarmManager mAlarmManager;
    private AudioManager mAudioManager;


    @Inject
    public DndHelper(@ApplicationContext Context context) {
        mContext = context;
        mAlarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        mAzanAlarmStatus = new AzanAlarmStatus();

        mNextAlarmTime = BehaviorSubject.create();
    }

    private long getCurrentTimeInSeconds() {
        return Calendar.getInstance().getTime().getTime() / 1000;
    }


    public void setObserverForRegularDay(LifecycleOwner lifecycleOwner, LocalDate day) {
        Long targetDayEpoch = day.atStartOfDay(ZoneId.systemDefault()).toEpochSecond();
        mRepository.getSelectedRegularLiveData(targetDayEpoch).observe(lifecycleOwner, entry -> {
            if (entry != null) {

                if (getCurrentTimeInSeconds() <= entry.getFajrEpoch())
                    processRegularTiming(entry.getFajrEpoch(), entry.getFajrSilent(), AZAN_ALARM, mAzanAlarmStatus);
                if (getCurrentTimeInSeconds() <= entry.getDhuhrEpoch())
                    processRegularTiming(entry.getDhuhrEpoch(), entry.getDhuhrSilent(), AZAN_ALARM, mAzanAlarmStatus);
                if (getCurrentTimeInSeconds() <= entry.getAsrEpoch())
                    processRegularTiming(entry.getAsrEpoch(), entry.getAsrSilent(), AZAN_ALARM, mAzanAlarmStatus);
                if (getCurrentTimeInSeconds() <= entry.getMaghribEpoch())
                    processRegularTiming(entry.getMaghribEpoch(), entry.getMaghribSilent(), AZAN_ALARM, mAzanAlarmStatus);
                if (getCurrentTimeInSeconds() <= entry.getIshaEpoch())
                    processRegularTiming(entry.getIshaEpoch(), entry.getIshaSilent(), AZAN_ALARM, mAzanAlarmStatus);


                if (!mAzanAlarmStatus.isAlarmActive() && !mNextDayAlarmActivated) {
                    Log.d(TAG, "launching observer for the next day");
                    mNextDayAlarmActivated = true;
                    setObserverForRegularDay(lifecycleOwner, day.plusDays(ONE_DAY));
                }

            }
        });


    }

    private void processRegularTiming(Long timing, boolean isSilentFlagOn, int prayerId, AzanAlarmStatus alarmStatus) {
        boolean timingIsOutdated = getCurrentTimeInSeconds() >= timing || alarmStatus.isAlarmActive() && timing < alarmStatus.getAlarmTiming();
        boolean fridaysOnlyActive = mPrefHelper.getDndForFridaysOnly();
        boolean timingIsGood2Go = !timingIsOutdated && isSilentFlagOn && !fridaysOnlyActive && !alarmStatus.isAlarmActive();
        boolean timingToBeCanceled = alarmStatus.isAlarmActive() &&
                (
                        fridaysOnlyActive
                                || timing < alarmStatus.getAlarmTiming()
                                || !isSilentFlagOn
                );
        Log.d(TAG, "processRegularTiming: isSilentFlagOn before " + isSilentFlagOn);

        if (timingIsGood2Go) {
            setAlarmManager(timing, prayerId, true);
            alarmStatus.setAlarmActive(true);
            alarmStatus.setAlarmTiming(timing);
            Log.d(TAG, "processRegularTiming: isSilentFlagOn " + isSilentFlagOn);
        }

        if (timingToBeCanceled) {
            setAlarmManager(alarmStatus.getAlarmTiming(), prayerId, false);
            alarmStatus.setAlarmActive(false);
            Log.d(TAG, "processRegularTiming: isSilentFlagOn after " + isSilentFlagOn);
        }
        Log.d(TAG, "processRegularTiming: isSilentFlagOn after " + isSilentFlagOn);
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
        setNextAlarmTime();
    }

    public void setNextAlarmTime() {
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



    static class AzanAlarmStatus  {
        private boolean alarmActive;
        private long alarmTiming;

        public boolean isAlarmActive() {
            return alarmActive;
        }

        public void setAlarmActive(boolean alarmActive) {
            this.alarmActive = alarmActive;
        }

        public long getAlarmTiming() {
            return alarmTiming;
        }

        public void setAlarmTiming(long alarmTiming) {
            this.alarmTiming = alarmTiming;
        }
    }

}
