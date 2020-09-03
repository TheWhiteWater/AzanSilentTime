package nz.co.redice.azansilenttime.repo.local.entity;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import nz.co.redice.azansilenttime.ui.presentation.Converters;


@Entity(tableName = "regular_table")
public class RegularSchedule {

    @PrimaryKey
    private Long date;

    private boolean isFajrMute = true;
    private boolean isDhuhrMute = true;
    private boolean isAsrMute = true;
    private boolean isMaghribMute = true;
    private boolean isIshaMute = true;

    private Long fajrEpochSecond;
    private Long dhuhrEpochSecond;
    private Long asrEpochSecond;
    private Long maghribEpochSecond;
    private Long ishaEpochSecond;

    public RegularSchedule() {
    }

    @Ignore
    public RegularSchedule(Long date, Long fajrEpochSecond, Long dhuhrEpochSecond, Long asrEpochSecond, Long maghribEpochSecond, Long ishaEpochSecond) {
        this.date = date;
        this.fajrEpochSecond = fajrEpochSecond;
        this.dhuhrEpochSecond = dhuhrEpochSecond;
        this.asrEpochSecond = asrEpochSecond;
        this.maghribEpochSecond = maghribEpochSecond;
        this.ishaEpochSecond = ishaEpochSecond;
    }


    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    public boolean isFajrMute() {
        return isFajrMute;
    }

    public void setFajrMute(boolean fajrMute) {
        isFajrMute = fajrMute;
    }

    public boolean isDhuhrMute() {
        return isDhuhrMute;
    }

    public void setDhuhrMute(boolean dhuhrMute) {
        isDhuhrMute = dhuhrMute;
    }

    public boolean isAsrMute() {
        return isAsrMute;
    }

    public void setAsrMute(boolean asrMute) {
        isAsrMute = asrMute;
    }

    public boolean isMaghribMute() {
        return isMaghribMute;
    }

    public void setMaghribMute(boolean maghribMute) {
        isMaghribMute = maghribMute;
    }

    public boolean isIshaMute() {
        return isIshaMute;
    }

    public void setIshaMute(boolean ishaMute) {
        isIshaMute = ishaMute;
    }

    public Long getFajrEpochSecond() {
        return fajrEpochSecond;
    }

    public void setFajrEpochSecond(Long fajrEpochSecond) {
        this.fajrEpochSecond = fajrEpochSecond;
    }

    public Long getDhuhrEpochSecond() {
        return dhuhrEpochSecond;
    }

    public void setDhuhrEpochSecond(Long dhuhrEpochSecond) {
        this.dhuhrEpochSecond = dhuhrEpochSecond;
    }

    public Long getAsrEpochSecond() {
        return asrEpochSecond;
    }

    public void setAsrEpochSecond(Long asrEpochSecond) {
        this.asrEpochSecond = asrEpochSecond;
    }

    public Long getMaghribEpochSecond() {
        return maghribEpochSecond;
    }

    public void setMaghribEpochSecond(Long maghribEpochSecond) {
        this.maghribEpochSecond = maghribEpochSecond;
    }

    public Long getIshaEpochSecond() {
        return ishaEpochSecond;
    }

    public void setIshaEpochSecond(Long ishaEpochSecond) {
        this.ishaEpochSecond = ishaEpochSecond;
    }

    public String getFajrTimeText() {
        return Converters.convertEpochIntoTextTime(fajrEpochSecond);
    }

    public String getDhuhrTimeText() {
        return Converters.convertEpochIntoTextTime(dhuhrEpochSecond);
    }

    public String getAsrTimeText() {
        return Converters.convertEpochIntoTextTime(asrEpochSecond);
    }

    public String getMaghribTimeText() {
        return Converters.convertEpochIntoTextTime(maghribEpochSecond);
    }

    public String getIshaTimeText() {
        return Converters.convertEpochIntoTextTime(ishaEpochSecond);
    }

    public String getDateText() {
        return Converters.convertEpochIntoTextDate(date);
    }

}
