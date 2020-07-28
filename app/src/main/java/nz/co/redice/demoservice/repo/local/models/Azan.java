package nz.co.redice.demoservice.repo.local.models;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import nz.co.redice.demoservice.utils.Converters;


@Entity(tableName = "data_table")
public class Azan {

    @PrimaryKey
    private Long date;
    private Long fajr;
    private Long dhuhr;
    private Long asr;
    private Long maghrib;
    private Long isha;
    private String timeZone;


    public Azan() {
    }

    @Ignore
    public Azan(String date, String timeZone, String fajr, String dhuhr, String asr, String maghrib, String isha) {
        this.timeZone = timeZone;
        ZoneId zoneId = ZoneId.of(timeZone);
        this.date = Converters.getLocalDateFromStringIntoLong(date, zoneId);
        this.fajr = Converters.getLocalDateTimeFromStringIntoLong(date, fajr, zoneId);
        this.isha = Converters.getLocalDateTimeFromStringIntoLong(date, isha, zoneId);
        this.dhuhr = Converters.getLocalDateTimeFromStringIntoLong(date, dhuhr, zoneId);
        this.asr = Converters.getLocalDateTimeFromStringIntoLong(date, asr, zoneId);
        this.maghrib = Converters.getLocalDateTimeFromStringIntoLong(date, maghrib, zoneId);

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

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

}
