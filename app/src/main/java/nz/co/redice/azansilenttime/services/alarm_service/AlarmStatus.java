package nz.co.redice.azansilenttime.services.alarm_service;

import java.util.concurrent.atomic.AtomicBoolean;

import javax.inject.Inject;

public class AlarmStatus {

    private AtomicBoolean alarmActive;
    private Long epochSeconds;


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

    public synchronized long getEpochSeconds() {

        return epochSeconds;
    }

    public synchronized void setEpochSeconds(long alarmTiming) {
        this.epochSeconds = alarmTiming;
    }
}
