package nz.co.redice.azansilenttime.repo.local.entity;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import nz.co.redice.azansilenttime.view.presentation.Converters;


@Entity(tableName = "regular_table")
public class RegularEntry {

    @PrimaryKey
    private Long date;

    private boolean isFajrSilent = true;
    private boolean isDhuhrSilent = true;
    private boolean isAsrSilent = true;
    private boolean isMaghribSilent = true;
    private boolean isIshaSilent = true;

    private Long fajrEpoch;
    private Long dhuhrEpoch;
    private Long asrEpoch;
    private Long maghribEpoch;
    private Long ishaEpoch;

    public RegularEntry() {
    }

    @Ignore
    public RegularEntry(Long date, Long fajrEpoch, Long dhuhrEpoch, Long asrEpoch, Long maghribEpoch, Long ishaEpoch) {
        this.date = date;
        this.fajrEpoch = fajrEpoch;
        this.dhuhrEpoch = dhuhrEpoch;
        this.asrEpoch = asrEpoch;
        this.maghribEpoch = maghribEpoch;
        this.ishaEpoch = ishaEpoch;
    }


    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    public Boolean getFajrSilent() {
        return isFajrSilent;
    }

    public void setFajrSilent(Boolean fajrSilent) {
        isFajrSilent = fajrSilent;
    }

    public Boolean getDhuhrSilent() {
        return isDhuhrSilent;
    }

    public void setDhuhrSilent(Boolean dhuhrSilent) {
        isDhuhrSilent = dhuhrSilent;
    }

    public Boolean getAsrSilent() {
        return isAsrSilent;
    }

    public void setAsrSilent(Boolean asrSilent) {
        isAsrSilent = asrSilent;
    }

    public Boolean getMaghribSilent() {
        return isMaghribSilent;
    }

    public void setMaghribSilent(Boolean maghribSilent) {
        isMaghribSilent = maghribSilent;
    }

    public Boolean getIshaSilent() {
        return isIshaSilent;
    }

    public void setIshaSilent(Boolean ishaSilent) {
        isIshaSilent = ishaSilent;
    }

    public Long getFajrEpoch() {
        return fajrEpoch;
    }

    public void setFajrEpoch(Long fajrEpoch) {
        this.fajrEpoch = fajrEpoch;
    }

    public Long getDhuhrEpoch() {
        return dhuhrEpoch;
    }

    public void setDhuhrEpoch(Long dhuhrEpoch) {
        this.dhuhrEpoch = dhuhrEpoch;
    }

    public Long getAsrEpoch() {
        return asrEpoch;
    }

    public void setAsrEpoch(Long asrEpoch) {
        this.asrEpoch = asrEpoch;
    }

    public Long getMaghribEpoch() {
        return maghribEpoch;
    }

    public void setMaghribEpoch(Long maghribEpoch) {
        this.maghribEpoch = maghribEpoch;
    }

    public Long getIshaEpoch() {
        return ishaEpoch;
    }

    public void setIshaEpoch(Long ishaEpoch) {
        this.ishaEpoch = ishaEpoch;
    }

    public String getFajrTimeText() {
        return Converters.convertEpochIntoTextTime(fajrEpoch);
    }

    public String getDhuhrTimeText() {
        return Converters.convertEpochIntoTextTime(dhuhrEpoch);
    }

    public String getAsrTimeText() {
        return Converters.convertEpochIntoTextTime(asrEpoch);
    }

    public String getMaghribTimeText() {
        return Converters.convertEpochIntoTextTime(maghribEpoch);
    }

    public String getIshaTimeText() {
        return Converters.convertEpochIntoTextTime(ishaEpoch);
    }

    public String getDateText() {
        return Converters.convertEpochIntoTextDate(date);
    }

}
