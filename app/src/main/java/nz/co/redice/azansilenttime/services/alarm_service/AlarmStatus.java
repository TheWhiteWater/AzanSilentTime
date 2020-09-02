package nz.co.redice.azansilenttime.services.alarm_service;

import java.util.concurrent.atomic.AtomicBoolean;

import javax.inject.Inject;

public class AlarmStatus {

    private AtomicBoolean alarmActive;
    private Long alarmTiming;


    @Inject
    public AlarmStatus() {
        alarmActive = new AtomicBoolean();
        this.alarmActive.set(false);
    }

    public synchronized boolean isActive() {
        return alarmActive.get();
    }

    public synchronized void setAlarmStatus(boolean alarmActive) {
        this.alarmActive.set(alarmActive);
    }

    public synchronized long getTiming() {

        return alarmTiming;
    }

    public synchronized void setAlarmTiming(long alarmTiming) {
        this.alarmTiming = alarmTiming;
    }
}
