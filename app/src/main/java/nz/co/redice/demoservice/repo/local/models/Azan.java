package nz.co.redice.demoservice.repo.local.models;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverter;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import javax.inject.Inject;

import nz.co.redice.demoservice.utils.PrefHelper;


@Entity(tableName = "data_table")
public class Azan {

    @PrimaryKey
    private Long date;
    private String timeZone;
    private Long fajr;
    private Long dhuhr;
    private Long asr;
    private Long maghrib;
    private Long isha;


    public Azan() {
    }

    @Ignore
    public Azan(String date, String timeZone, String fajr, String dhuhr, String asr, String maghrib, String isha) {
        this.timeZone = timeZone;
        ZoneId zoneId = ZoneId.of(timeZone);
        this.date = setDate(date, zoneId);
        this.fajr = setTime(date, fajr, zoneId);
        this.isha = setTime(date, isha, zoneId);
        this.dhuhr = setTime(date, dhuhr, zoneId);
        this.asr = setTime(date, asr, zoneId);
        this.maghrib = setTime(date, maghrib, zoneId);
    }


    private Long setDate(String date, ZoneId zoneId) {
        return LocalDate.parse(date, DateTimeFormatter.ofPattern("dd-MM-yyyy"))
                .atStartOfDay(zoneId)
                .toEpochSecond();
    }

    private Long setTime(String date, String time, ZoneId zoneId) {
        String[] mytime = time.split(" ");
        return LocalDateTime.parse(date + " " + mytime[0], DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"))
                .atZone(zoneId)
                .toEpochSecond();
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

    public LocalDate getLocalDate() {
        return getLocalDateFromLong(date);
    }

    public LocalDateTime getFajrTime() {
        return getLocalDateTimeFromLong(fajr);
    }

    public LocalDateTime getDhuhrTime() {
        return getLocalDateTimeFromLong(dhuhr);
    }

    public LocalDateTime getAsrTime() {
        return getLocalDateTimeFromLong(asr);
    }

    public LocalDateTime getMaghribTime() {
        return getLocalDateTimeFromLong(maghrib);
    }

    public LocalDateTime getIshaTime() {
        return getLocalDateTimeFromLong(isha);
    }


    private LocalDate getLocalDateFromLong(Long longValue) {
        return Instant.ofEpochSecond(longValue).atZone(ZoneId.of(this.timeZone)).toLocalDate();
    }

    private LocalDateTime getLocalDateTimeFromLong(Long longValue) {
        return LocalDateTime.ofInstant(Instant.ofEpochSecond(longValue), ZoneId.of(this.timeZone));
    }
}
