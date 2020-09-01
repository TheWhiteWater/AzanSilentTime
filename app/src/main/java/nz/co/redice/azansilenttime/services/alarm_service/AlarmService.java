package nz.co.redice.azansilenttime.services.alarm_service;

import android.annotation.SuppressLint;

import java.time.LocalDate;
import java.util.List;

public interface AlarmService {
    String DND_ON = "dnd_on";
    String DND_OFF = "dnd_off";

    @SuppressLint("CheckResult")
    void getSchedulesFromNextTwoDaysStartingFrom(LocalDate day);

    Schedule getEarliestSchedule(List<Schedule> unsortedList);

    @SuppressLint("CheckResult")
    void getSchedulesFromNextTwoFridaysStartingFrom(LocalDate day);

    void scheduleMuteAlarm(Long timing, int requestCode);

    void cancelScheduledMuteAlarm(Long timing, int requestCode);

    void scheduleWakeUpAlarm();

    Long getAlarmTimestamp();

    void registerAlarmListener(OnNewAlarmListener onNewAlarmListener);

    void processRegularSchedule(Schedule earliestSchedule);

    void processFridaySchedule(Schedule earliestSchedule);
}
