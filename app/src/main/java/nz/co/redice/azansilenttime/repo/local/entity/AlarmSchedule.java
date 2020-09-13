package nz.co.redice.azansilenttime.repo.local.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import nz.co.redice.azansilenttime.ui.presentation.Converters;

@Entity()
public class AlarmSchedule {

    @PrimaryKey
    private boolean fridayMute = true;
    private boolean fajrMute = true;
    private boolean dhuhrMute = true;
    private boolean asrMute = true;
    private boolean maghribMute = true;
    private boolean ishaMute = true;

    private int fridayMutePeriodInMinutes;
    private int fajrMutePeriodInMinutes;
    private int dhuhrMutePeriodInMinutes;
    private int asrMutePeriodInMinutes;
    private int maghribMutePeriodInMinutes;
    private int ishaMutePeriodInMinutes;


    private String fridayAlarmHour;
    private String fridayAlarmMinute;
    private Long fajrAlarmTime;
    private Long dhuhrAlarmTime;
    private Long asrAlarmTime;
    private Long maghribAlarmTime;
    private Long ishaAlarmTime;

    private boolean fridayAlarmScheduled = false;
    private boolean fajrAlarmTimeScheduled = false;
    private boolean dhuhrAlarmTimeScheduled = false;
    private boolean asrAlarmTimeScheduled = false;
    private boolean maghribAlarmTimeScheduled = false;
    private boolean ishaAlarmTimeScheduled = false;


    public AlarmSchedule() {
        fridayMutePeriodInMinutes = 10;
        fajrMutePeriodInMinutes = 10;
        dhuhrMutePeriodInMinutes = 10;
        asrMutePeriodInMinutes = 10;
        maghribMutePeriodInMinutes = 10;
        ishaMutePeriodInMinutes = 10;
    }

    public boolean isFridayMute() {
        return fridayMute;
    }

    public void setFridayMute(boolean fridayMute) {
        this.fridayMute = fridayMute;
    }

    public boolean isFajrMute() {
        return fajrMute;
    }

    public void setFajrMute(boolean fajrMute) {
        this.fajrMute = fajrMute;
    }

    public boolean isDhuhrMute() {
        return dhuhrMute;
    }

    public void setDhuhrMute(boolean dhuhrMute) {
        this.dhuhrMute = dhuhrMute;
    }

    public boolean isAsrMute() {
        return asrMute;
    }

    public void setAsrMute(boolean asrMute) {
        this.asrMute = asrMute;
    }

    public boolean isMaghribMute() {
        return maghribMute;
    }

    public void setMaghribMute(boolean maghribMute) {
        this.maghribMute = maghribMute;
    }

    public boolean isIshaMute() {
        return ishaMute;
    }

    public void setIshaMute(boolean ishaMute) {
        this.ishaMute = ishaMute;
    }

    public Long getFajrAlarmTime() {
        return fajrAlarmTime;
    }

    public void setFajrAlarmTime(Long fajrAlarmTime) {
        this.fajrAlarmTime = fajrAlarmTime;
    }

    public Long getDhuhrAlarmTime() {
        return dhuhrAlarmTime;
    }

    public void setDhuhrAlarmTime(Long dhuhrAlarmTime) {
        this.dhuhrAlarmTime = dhuhrAlarmTime;
    }

    public Long getAsrAlarmTime() {
        return asrAlarmTime;
    }

    public void setAsrAlarmTime(Long asrAlarmTime) {
        this.asrAlarmTime = asrAlarmTime;
    }

    public Long getMaghribAlarmTime() {
        return maghribAlarmTime;
    }

    public void setMaghribAlarmTime(Long maghribAlarmTime) {
        this.maghribAlarmTime = maghribAlarmTime;
    }

    public Long getIshaAlarmTime() {
        return ishaAlarmTime;
    }

    public void setIshaAlarmTime(Long ishaAlarmTime) {
        this.ishaAlarmTime = ishaAlarmTime;
    }

    public boolean isFridayAlarmScheduled() {
        return fridayAlarmScheduled;
    }

    public void setFridayAlarmScheduled(boolean fridayAlarmScheduled) {
        this.fridayAlarmScheduled = fridayAlarmScheduled;
    }

    public boolean isFajrAlarmTimeScheduled() {
        return fajrAlarmTimeScheduled;
    }

    public void setFajrAlarmTimeScheduled(boolean fajrAlarmTimeScheduled) {
        this.fajrAlarmTimeScheduled = fajrAlarmTimeScheduled;
    }

    public boolean isDhuhrAlarmTimeScheduled() {
        return dhuhrAlarmTimeScheduled;
    }

    public void setDhuhrAlarmTimeScheduled(boolean dhuhrAlarmTimeScheduled) {
        this.dhuhrAlarmTimeScheduled = dhuhrAlarmTimeScheduled;
    }

    public boolean isAsrAlarmTimeScheduled() {
        return asrAlarmTimeScheduled;
    }

    public void setAsrAlarmTimeScheduled(boolean asrAlarmTimeScheduled) {
        this.asrAlarmTimeScheduled = asrAlarmTimeScheduled;
    }

    public boolean isMaghribAlarmTimeScheduled() {
        return maghribAlarmTimeScheduled;
    }

    public void setMaghribAlarmTimeScheduled(boolean maghribAlarmTimeScheduled) {
        this.maghribAlarmTimeScheduled = maghribAlarmTimeScheduled;
    }

    public boolean isIshaAlarmTimeScheduled() {
        return ishaAlarmTimeScheduled;
    }

    public void setIshaAlarmTimeScheduled(boolean ishaAlarmTimeScheduled) {
        this.ishaAlarmTimeScheduled = ishaAlarmTimeScheduled;
    }

    public int getFridayMutePeriodInMinutes() {
        return fridayMutePeriodInMinutes;
    }

    public void setFridayMutePeriodInMinutes(int fridayMutePeriodInMinutes) {
        this.fridayMutePeriodInMinutes = fridayMutePeriodInMinutes;
    }

    public int getFajrMutePeriodInMinutes() {
        return fajrMutePeriodInMinutes;
    }

    public void setFajrMutePeriodInMinutes(int fajrMutePeriodInMinutes) {
        this.fajrMutePeriodInMinutes = fajrMutePeriodInMinutes;
    }

    public int getDhuhrMutePeriodInMinutes() {
        return dhuhrMutePeriodInMinutes;
    }

    public void setDhuhrMutePeriodInMinutes(int dhuhrMutePeriodInMinutes) {
        this.dhuhrMutePeriodInMinutes = dhuhrMutePeriodInMinutes;
    }

    public int getAsrMutePeriodInMinutes() {
        return asrMutePeriodInMinutes;
    }

    public void setAsrMutePeriodInMinutes(int asrMutePeriodInMinutes) {
        this.asrMutePeriodInMinutes = asrMutePeriodInMinutes;
    }

    public int getMaghribMutePeriodInMinutes() {
        return maghribMutePeriodInMinutes;
    }

    public void setMaghribMutePeriodInMinutes(int maghribMutePeriodInMinutes) {
        this.maghribMutePeriodInMinutes = maghribMutePeriodInMinutes;
    }

    public int getIshaMutePeriodInMinutes() {
        return ishaMutePeriodInMinutes;
    }

    public void setIshaMutePeriodInMinutes(int ishaMutePeriodInMinutes) {
        this.ishaMutePeriodInMinutes = ishaMutePeriodInMinutes;
    }

    public String getFridayAlarmTimeInText() {
        return String.format("%s:%s", fridayAlarmHour, fridayAlarmMinute);
    }

    public String getFridayAlarmHour() {
        return fridayAlarmHour;
    }

    public void setFridayAlarmHour(String fridayAlarmHour) {
        this.fridayAlarmHour = fridayAlarmHour;
    }

    public String getFridayAlarmMinute() {
        return fridayAlarmMinute;
    }

    public void setFridayAlarmMinute(String fridayAlarmMinute) {
        this.fridayAlarmMinute = fridayAlarmMinute;
    }

    public String getFajrAlarmTimeInText() {
        return Converters.convertEpochIntoTextTime(fajrAlarmTime);
    }

    public String getDhuhrAlarmTimeInText() {
        return Converters.convertEpochIntoTextTime(dhuhrAlarmTime);
    }

    public String getAsrAlarmTimeInText() {
        return Converters.convertEpochIntoTextTime(asrAlarmTime);
    }

    public String getMaghribAlarmTimeInText() {
        return Converters.convertEpochIntoTextTime(maghribAlarmTime);
    }

    public String getIshaAlarmTimeInText() {
        return Converters.convertEpochIntoTextTime(ishaAlarmTime);
    }


    public String getFridayMutePeriodInText() {
        return Converters.convertDndPeriodIntoText(fridayMutePeriodInMinutes);
    }

    public String getFajrMutePeriodInText() {
        return Converters.convertDndPeriodIntoText(fajrMutePeriodInMinutes);
    }

    public String getDhuhrMutePeriodInText() {
        return Converters.convertDndPeriodIntoText(dhuhrMutePeriodInMinutes);
    }

    public String getAsrMutePeriodInText() {
        return Converters.convertDndPeriodIntoText(asrMutePeriodInMinutes);
    }

    public String getMaghribMutePeriodInText() {
        return Converters.convertDndPeriodIntoText(maghribMutePeriodInMinutes);
    }

    public String getIshaMutePeriodInText() {
        return Converters.convertDndPeriodIntoText(ishaMutePeriodInMinutes);
    }
}
