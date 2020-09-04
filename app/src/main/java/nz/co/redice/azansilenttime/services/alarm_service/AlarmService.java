package nz.co.redice.azansilenttime.services.alarm_service;

import java.time.LocalDate;
import java.util.List;

public interface AlarmService {
    String DO_NOT_DISTURB_ON = "dnd_on";
    String DO_NOT_DISTURB_OFF = "dnd_off";

    void getSchedulesFromNextTwoDaysStartingFrom(LocalDate day);

    void getSchedulesFromNextTwoFridaysStartingFrom(LocalDate day);

    void setPostAlarmWakeUp();

    void registerAlarmListener(OnNewAlarmListener onNewAlarmListener);

}
