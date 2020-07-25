package nz.co.redice.demoservice.repo.local.models;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.time.LocalDateTime;

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


    public Azan() {
    }

    @Ignore

    public Azan(String date, String fajr, String dhuhr, String asr, String maghrib, String isha) {
        this.date = Converters.setDate(date);
        this.fajr = Converters.setTime(date, fajr);
        this.dhuhr = Converters.setTime(date, dhuhr);
        this.asr = Converters.setTime(date, asr);
        this.maghrib = Converters.setTime(date, maghrib);
        this.isha = Converters.setTime(date, isha);
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
}
