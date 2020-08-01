package nz.co.redice.demoservice.repo.local.entity;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import nz.co.redice.demoservice.view.presentation.Converters;
import nz.co.redice.demoservice.view.presentation.ReadableTimings;


@Entity(tableName = "data_table")
public class EntryModel {

    @PrimaryKey
    private Long date;

    private Boolean fajrMuteOn = true;
    private Boolean dhuhrMuteOn = true;
    private Boolean asrMuteOn = true;
    private Boolean maghribMuteOn = true;
    private Boolean ishaMuteOn = true;

    private Long fajr;
    private Long dhuhr;
    private Long asr;
    private Long maghrib;
    private Long isha;

    @Ignore private String dateString;

    @Ignore private String fajrString;
    @Ignore private String dhuhrString;
    @Ignore private String asrString;
    @Ignore private String maghribString;
    @Ignore private String ishaString;


    public EntryModel() {
    }

    @Ignore
    public EntryModel(Long date, Long fajr, Long dhuhr, Long asr, Long maghrib, Long isha) {
        this.date = date;
        this.fajr = fajr;
        this.dhuhr = dhuhr;
        this.asr = asr;
        this.maghrib = maghrib;
        this.isha = isha;
    }


    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    public Long getFajr() {
        return fajr;
    }

    public void setFajr(Long fajr) {
        this.fajr = fajr;
    }

    public Long getDhuhr() {
        return dhuhr;
    }

    public void setDhuhr(Long dhuhr) {
        this.dhuhr = dhuhr;
    }

    public Long getAsr() {
        return asr;
    }

    public void setAsr(Long asr) {
        this.asr = asr;
    }

    public Long getMaghrib() {
        return maghrib;
    }

    public void setMaghrib(Long maghrib) {
        this.maghrib = maghrib;
    }

    public Long getIsha() {
        return isha;
    }

    public void setIsha(Long isha) {
        this.isha = isha;
    }

    public Boolean getFajrMuteOn() {
        return fajrMuteOn;
    }

    public void setFajrMuteOn(Boolean fajrMuteOn) {
        this.fajrMuteOn = fajrMuteOn;
    }

    public Boolean getDhuhrMuteOn() {
        return dhuhrMuteOn;
    }

    public void setDhuhrMuteOn(Boolean dhuhrMuteOn) {
        this.dhuhrMuteOn = dhuhrMuteOn;
    }

    public Boolean getAsrMuteOn() {
        return asrMuteOn;
    }

    public void setAsrMuteOn(Boolean asrMuteOn) {
        this.asrMuteOn = asrMuteOn;
    }

    public Boolean getMaghribMuteOn() {
        return maghribMuteOn;
    }

    public void setMaghribMuteOn(Boolean maghribMuteOn) {
        this.maghribMuteOn = maghribMuteOn;
    }

    public Boolean getIshaMuteOn() {
        return ishaMuteOn;
    }

    public void setIshaMuteOn(Boolean ishaMuteOn) {
        this.ishaMuteOn = ishaMuteOn;
    }

    public String getFajrString() {
        return Converters.setTimeFromLong(fajr);
    }

    public String getDhuhrString() {
        return Converters.setTimeFromLong(dhuhr);
    }

    public String getAsrString() {
        return Converters.setTimeFromLong(asr);
    }

    public String getMaghribString() {
        return Converters.setTimeFromLong(maghrib);
    }

    public String getIshaString() {
        return Converters.setTimeFromLong(isha);
    }

    public String getDateString() {
        return Converters.setDateFromLong(date);
    }

    public ReadableTimings toReadableTimings() {
        ReadableTimings readableTimings = new ReadableTimings();
        readableTimings.setAsr(Converters.setTimeFromLong(asr));
        readableTimings.setFajr(Converters.setTimeFromLong(fajr));
        readableTimings.setDhuhr(Converters.setTimeFromLong(dhuhr));
        readableTimings.setMaghrib(Converters.setTimeFromLong(maghrib));
        readableTimings.setIsha(Converters.setTimeFromLong(isha));

        readableTimings.setFajrMuteOn(true);
        readableTimings.setDhuhrMuteOn(true);
        readableTimings.setMaghribMuteOn(true);
        readableTimings.setIshaMuteOn(true);
        readableTimings.setAsrMuteOn(true);

        readableTimings.setDate(Converters.setDateFromLong(date));

        return readableTimings;
    }

}
