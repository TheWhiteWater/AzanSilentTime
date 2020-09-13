package nz.co.redice.azansilenttime.repo.local.entity;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import nz.co.redice.azansilenttime.ui.presentation.Converters;

@Entity()
public class AzanTimings {

    @PrimaryKey
    private Long date;
    private Long fajrEpochSecond;
    private Long dhuhrEpochSecond;
    private Long asrEpochSecond;
    private Long maghribEpochSecond;
    private Long ishaEpochSecond;

    public AzanTimings() {
    }

    @Ignore
    public AzanTimings(Long date, Long fajrEpochSecond, Long dhuhrEpochSecond, Long asrEpochSecond, Long maghribEpochSecond, Long ishaEpochSecond) {
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

    public String getFajrTimeInText() {
        return Converters.convertEpochIntoTextTime(fajrEpochSecond);
    }

    public String getDhuhrTimeInText() {
        return Converters.convertEpochIntoTextTime(dhuhrEpochSecond);
    }

    public String getAsrTimeInText() {
        return Converters.convertEpochIntoTextTime(asrEpochSecond);
    }

    public String getMaghribTimeInText() {
        return Converters.convertEpochIntoTextTime(maghribEpochSecond);
    }

    public String getIshaTimeInText() {
        return Converters.convertEpochIntoTextTime(ishaEpochSecond);
    }

    public String getDateInText() {
        return Converters.convertEpochIntoTextDate(date);
    }

}
