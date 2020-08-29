package nz.co.redice.azansilenttime.utils;

import java.util.concurrent.atomic.AtomicBoolean;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class AlarmStatus {

    private AtomicBoolean alarmActive;
    private Long alarmTiming;

    @Inject
    public AlarmStatus() {
        alarmActive = new AtomicBoolean();
        this.alarmActive.set(false);
    }

    public synchronized boolean isAlarmActive() {
        return alarmActive.get();
    }

    public synchronized void setAlarmActive(boolean alarmActive) {
        this.alarmActive.set(alarmActive);
    }

    public synchronized long getAlarmTiming() {

        return alarmTiming;
    }

    public synchronized void setAlarmTiming(long alarmTiming) {
        this.alarmTiming = alarmTiming;
    }
}
