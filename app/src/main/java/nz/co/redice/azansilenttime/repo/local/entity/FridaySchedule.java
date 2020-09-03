package nz.co.redice.azansilenttime.repo.local.entity;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import nz.co.redice.azansilenttime.ui.presentation.Converters;

@Entity(tableName = "friday_table")
public class FridaySchedule {

    @PrimaryKey
    private Long date;
    private Boolean silent = true;
    private Long epochSecond;
    private String blank;

    public FridaySchedule() {
    }

    @Ignore
    public FridaySchedule(Long date) {
        this.date = date;
        this.silent = true;
        this.blank = "Select";
    }

    @Ignore
    public FridaySchedule(Long date, Boolean isFridaySilent, Long epochSecond) {
        this.date = date;
        this.silent = isFridaySilent;
        this.epochSecond = epochSecond;
    }


    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    public Boolean isSilent() {
        return silent;
    }

    public void setSilent(Boolean fridaySilent) {
        silent = fridaySilent;
    }

    public Long getEpochSecond() {
        return epochSecond;
    }

    public void setEpochSecond(Long epochSecond) {
        this.epochSecond = epochSecond;
    }

    public String getTimeText() {
        return Converters.convertEpochIntoTextTime(epochSecond);
    }


    public String getDateString() {
        return Converters.convertEpochIntoTextDate(date);
    }

    public String getBlank() {
        return blank;
    }

    public void setBlank(String blank) {
        this.blank = blank;
    }
}
