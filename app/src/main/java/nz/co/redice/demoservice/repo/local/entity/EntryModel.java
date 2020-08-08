package nz.co.redice.demoservice.repo.local.entity;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import nz.co.redice.demoservice.view.presentation.Converters;


@Entity(tableName = "regular_table")
public class EntryModel {

    @PrimaryKey
    private Long date;

    private Boolean isFajrSilent = true;
    private Boolean isDhuhrSilent = true;
    private Boolean isAsrSilent = true;
    private Boolean isMaghribSilent = true;
    private Boolean isIshaSilent = true;

    private Long fajrEpoch;
    private Long dhuhrEpoch;
    private Long asrEpoch;
    private Long maghribEpoch;
    private Long ishaEpoch;

    @Ignore private String dateString;
    @Ignore private String fajrString;
    @Ignore private String dhuhrString;
    @Ignore private String asrString;
    @Ignore private String maghribString;
    @Ignore private String ishaString;


    public EntryModel() {
    }

    @Ignore
    public EntryModel(Long date, Long fajrEpoch, Long dhuhrEpoch, Long asrEpoch, Long maghribEpoch, Long ishaEpoch) {
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

    public String getFajrString() {
        return Converters.setTimeFromLong(fajrEpoch);
    }

    public String getDhuhrString() {
        return Converters.setTimeFromLong(dhuhrEpoch);
    }

    public String getAsrString() {
        return Converters.setTimeFromLong(asrEpoch);
    }

    public String getMaghribString() {
        return Converters.setTimeFromLong(maghribEpoch);
    }

    public String getIshaString() {
        return Converters.setTimeFromLong(ishaEpoch);
    }

    public String getDateString() {
        return Converters.setDateFromLong(date);
    }

}
