package nz.co.redice.azansilenttime.repo.local.entity;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import nz.co.redice.azansilenttime.ui.presentation.Converters;

@Entity(tableName = "friday_table")
public class FridaySchedule {

    @PrimaryKey
    private Long date;
    private Boolean mute = true;
    private Long epochSecond;

    public FridaySchedule() {
    }

    @Ignore
    public FridaySchedule(Long date) {
        this.date = date;
        this.mute = true;
    }

    @Ignore
    public FridaySchedule(Long date, Boolean isFridaySilent, Long epochSecond) {
        this.date = date;
        this.mute = isFridaySilent;
        this.epochSecond = epochSecond;
    }


    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    public Boolean isMute() {
        return mute;
    }

    public void setMute(Boolean fridaySilent) {
        mute = fridaySilent;
    }

    public Long getEpochSecond() {
        return epochSecond;
    }

    public void setEpochSecond(Long epochSecond) {
        this.epochSecond = epochSecond;
    }

    public String getTimeString() {
        return Converters.convertEpochIntoTextTime(epochSecond);
    }

    public String getDateString() {
        return Converters.convertEpochIntoTextDate(date);
    }

}
