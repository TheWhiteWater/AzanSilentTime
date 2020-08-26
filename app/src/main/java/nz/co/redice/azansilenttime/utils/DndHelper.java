package nz.co.redice.azansilenttime.utils;


import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.util.Log;
import android.widget.Toast;

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
    private ArrayList<Long> mActivatedAlarmList = new ArrayList<>();
    private FajrAlarmStatus mFajrAlarmStatus;
    private DhurAlarmStatus mDhurAlarmStatus;
    private IshaAlarmStatus mIshaAlarmStatus;
    private MaghribAlarmStatus mMaghribAlarmStatus;
    private AsrAlarmStatus mAsrAlarmStatus;
    private FridayAlarmStatus mFridayAlarmStatus;


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
        mFajrAlarmStatus = new FajrAlarmStatus();
        mDhurAlarmStatus = new DhurAlarmStatus();
        mAsrAlarmStatus = new AsrAlarmStatus();
        mIshaAlarmStatus = new IshaAlarmStatus();
        mMaghribAlarmStatus = new MaghribAlarmStatus();
        mFridayAlarmStatus = new FridayAlarmStatus();
    }

    private long getCurrentTimeInSeconds() {
        return Calendar.getInstance().getTime().getTime() / 1000;
    }


    public void setObserverForRegularDay(LifecycleOwner lifecycleOwner, LocalDate day) {
        Long targetDayEpoch = day.atStartOfDay(ZoneId.systemDefault()).toEpochSecond();
        mRepository.getSelectedRegularLiveData(targetDayEpoch).observe(lifecycleOwner, model -> {
            if (model != null) {
                if (getCurrentTimeInSeconds() <= model.getFajrEpoch())
                    processRegularTiming(model.getFajrEpoch(), model.getFajrSilent(), FAJR_ALARM, mFajrAlarmStatus);
                if (getCurrentTimeInSeconds() <= model.getDhuhrEpoch())
                    processRegularTiming(model.getDhuhrEpoch(), model.getDhuhrSilent(), DHUR_ALARM, mDhurAlarmStatus);
                if (getCurrentTimeInSeconds() <= model.getAsrEpoch())
                    processRegularTiming(model.getAsrEpoch(), model.getAsrSilent(), ASR_ALARM, mAsrAlarmStatus);
                if (getCurrentTimeInSeconds() <= model.getMaghribEpoch())
                    processRegularTiming(model.getMaghribEpoch(), model.getMaghribSilent(), MAGHRIB_ALARM, mMaghribAlarmStatus);
                if (getCurrentTimeInSeconds() <= model.getIshaEpoch())
                    processRegularTiming(model.getIshaEpoch(), model.getIshaSilent(), ISHA_ALARM, mIshaAlarmStatus);


                if (!mFajrAlarmStatus.isAlarmActive() && !mDhurAlarmStatus.isAlarmActive() && !mAsrAlarmStatus.isAlarmActive() &&
                        !mMaghribAlarmStatus.isAlarmActive() && !mIshaAlarmStatus.isAlarmActive()
                        && !mPrefHelper.getDndForFridaysOnly() && !mNextDayAlarmActivated) {
                    Log.d(TAG, "launching observer for the next day");
                    setObserverForRegularDay(lifecycleOwner, LocalDate.now().plusDays(ONE_DAY));
                    mNextDayAlarmActivated = true;
                }
                if (mNextDayAlarmActivated && !mPrefHelper.getDndForFridaysOnly()) {
                    setObserverForRegularDay(lifecycleOwner, LocalDate.now().plusDays(ONE_DAY));
                    mNextDayAlarmActivated = false;
                }

            }
        });


    }

    private void processRegularTiming(Long timing, boolean isTimingOn, int prayerId, AlarmStatus alarmStatus) {
        boolean notTooLateForTiming = getCurrentTimeInSeconds() <= timing;
        boolean notFridayDnd = !mPrefHelper.getDndForFridaysOnly();
        boolean isTimingGood2Go = notTooLateForTiming && isTimingOn && notFridayDnd;
        boolean timingToBeCanceled = alarmStatus.isAlarmActive() && !notFridayDnd || alarmStatus.isAlarmActive() && !isTimingOn
                || alarmStatus.isAlarmActive() && timing < alarmStatus.getAlarmTiming();

        Log.d(TAG, "doTiming: ===========");
        Log.d(TAG, "doTiming: epoch " + Converters.getDateFromLong(timing) + ", " + Converters.setTimeFromLong(timing));
        Log.d(TAG, "doTiming: notTooLateForTiming " + notTooLateForTiming);
        Log.d(TAG, "doTiming: isTimingActive " + isTimingOn);
        Log.d(TAG, "doTiming: notFridayDnd " + notFridayDnd);
        Log.d(TAG, "doTiming: isTimingGood2Go " + isTimingGood2Go);
        Log.d(TAG, "doTiming: timingToBeDeactivated " + timingToBeCanceled);


        if (timingToBeCanceled) {
            setAlarmManager(alarmStatus.getAlarmTiming(), prayerId, false);
            alarmStatus.setAlarmActive(false);
            String message = String.format("Silent mode OFF for %s", Converters.setTimeFromLong(timing));
            Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
        }
        if (isTimingGood2Go) {
            setAlarmManager(timing, prayerId, true);
            alarmStatus.setAlarmActive(true);
            alarmStatus.setAlarmTiming(timing);
            String message = String.format("Silent mode ON for %s", Converters.setTimeFromLong(timing));
            Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
        }
    }


//    private boolean doFriday(Long epoch, boolean active, int requestCode, boolean dispatcher) {
//        boolean notTooLateForTiming = getCurrentTimeInSeconds() <= epoch;
//        boolean isTimingActive = active;
//        boolean isFridayDnd = mPrefHelper.getDndForFridaysOnly();
//        boolean isTimingGood2Go = notTooLateForTiming && isTimingActive && isFridayDnd;
//        boolean timingToBeDeactivated = dispatcher && !isFridayDnd || dispatcher && !isTimingActive;
////        boolean timingToBeDeactivated = dispatcher ;
//
//        Log.d(TAG, "doFriday: notTooLateForTiming " + notTooLateForTiming);
//        Log.d(TAG, "doFriday: isTimingActive " + isTimingActive);
//        Log.d(TAG, "doFriday: isFridayDnd " + isFridayDnd);
//        Log.d(TAG, "doFriday: isTimingGood2Go " + isTimingGood2Go);
//        Log.d(TAG, "doFriday: timingToBeDeactivated " + timingToBeDeactivated);
//
//        if (!dispatcher && isTimingGood2Go || dispatcher && timingToBeDeactivated) {
//            setAlarmManager(epoch, requestCode, true);
//            dispatcher = true;
////            String message = "Alarm set on " + Converters.setDateFromLong(epoch) + ", " + Converters.setTimeFromLong(epoch);
////            Toast.makeText(mContext, message , Toast.LENGTH_SHORT).show();
//        }
//        if (timingToBeDeactivated) {
//            setAlarmManager(epoch, requestCode, false);
//            dispatcher = false;
////            String message = "Alarm canceled on . " + Converters.setDateFromLong(epoch) + ", " + Converters.setTimeFromLong(epoch);
////            Toast.makeText(mContext, message , Toast.LENGTH_SHORT).show();
//        }
//
//        return dispatcher;
//    }

//    public void setObserverForNextFriday(LifecycleOwner lifecycleOwner, LocalDate date) {
//        LocalDate targetDay = date.minusDays(1).with(TemporalAdjusters.next(DayOfWeek.FRIDAY));
//        Long targetEpoch = targetDay.atStartOfDay(ZoneId.systemDefault()).toEpochSecond();
//
//        mRepository.getSelectedFridayLiveData(targetEpoch).observe(lifecycleOwner, fridayEntry -> {
//            if (fridayEntry != null) {
//                mFridayAlarmActivated = doFriday(fridayEntry.getTimeEpoch(), fridayEntry.getSilent(), FRIDAY_ALARM, mFridayAlarmActivated);
//
//                if (!mFridayAlarmActivated && mPrefHelper.getDndForFridaysOnly()) {
////                    setObserverForRegularDay(lifecycleOwner, targetDay.with(TemporalAdjusters.next(DayOfWeek.FRIDAY)));
//                    mNextFridayAlarmActivated = true;
//                }
//                if (mNextFridayAlarmActivated && !mPrefHelper.getDndForFridaysOnly()) {
////                    setObserverForRegularDay(lifecycleOwner, targetDay.with(TemporalAdjusters.next(DayOfWeek.FRIDAY)));
//                    mNextFridayAlarmActivated = false;
//                }
//            }
//        });
//    }

    public void setAlarmManager(Long timing, int requestCode, boolean toBeActivated) {
        Intent intent = new Intent(mContext, ForegroundService.class);
        intent.setAction(DND_ON);
        PendingIntent dndOnIntent = PendingIntent.getService(mContext, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        if (toBeActivated) {
            mAlarmManager.setAlarmClock(new AlarmManager.AlarmClockInfo(timing * 1000, null), dndOnIntent);
            mActivatedAlarmList.add(timing * 1000);
            Log.d(TAG, "AlarmManager activated on " + timing * 1000);
        } else {
            mAlarmManager.cancel(dndOnIntent);
            mActivatedAlarmList.remove(timing * 1000);
            Log.d(TAG, "AlarmManager canceled on " + timing * 1000);
        }

    }

    public String getNextAlarmTime() {
        if (mActivatedAlarmList.size() > 0) {
            Collections.sort(mActivatedAlarmList);
            Date date = new Date(mActivatedAlarmList.get(0));

            LocalDateTime localDateTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

            DateTimeFormatter onlyTimeFormat = DateTimeFormatter.ofPattern("HH:mm a", Locale.getDefault());
            DateTimeFormatter onlyDayFormat = DateTimeFormatter.ofPattern("dd MMM", Locale.getDefault());

            if (localDateTime.getDayOfMonth() == LocalDate.now().getDayOfMonth()) {
                return "Next DND today at " + onlyTimeFormat.format(localDateTime);
            }
            Log.d(TAG, "getNextAlarmTime: " + onlyDayFormat.format(localDateTime) + " at " + onlyTimeFormat.format(localDateTime));
            Log.d(TAG, "getNextAlarmTime: value " + mActivatedAlarmList.get(0));
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


    class FajrAlarmStatus extends AlarmStatus {
        private boolean alarmActive;
        private long alarmTiming;

        @Override
        public boolean isAlarmActive() {
            return alarmActive;
        }

        @Override
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


    class DhurAlarmStatus extends AlarmStatus {
        private boolean alarmActive;
        private long alarmTiming;

        @Override
        public boolean isAlarmActive() {
            return alarmActive;
        }

        @Override
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


    class MaghribAlarmStatus extends AlarmStatus {
        private boolean alarmActive;
        private long alarmTiming;

        @Override
        public boolean isAlarmActive() {
            return alarmActive;
        }

        @Override
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


    class AsrAlarmStatus extends AlarmStatus {
        private boolean alarmActive;
        private long alarmTiming;

        @Override
        public boolean isAlarmActive() {
            return alarmActive;
        }

        @Override
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


    class IshaAlarmStatus extends AlarmStatus {
        private boolean alarmActive;
        private long alarmTiming;

        @Override
        public boolean isAlarmActive() {
            return alarmActive;
        }

        @Override
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


    class FridayAlarmStatus extends AlarmStatus {
        private boolean alarmActive;
        private long alarmTiming;

        @Override
        public boolean isAlarmActive() {
            return alarmActive;
        }

        @Override
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


    abstract class AlarmStatus {
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
