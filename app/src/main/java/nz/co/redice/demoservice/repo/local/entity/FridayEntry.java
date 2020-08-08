package nz.co.redice.demoservice.repo.local.entity;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import nz.co.redice.demoservice.view.presentation.Converters;

@Entity(tableName = "friday_table")
public class FridayEntry {

    @PrimaryKey
    private Long date;
    private Boolean isFridaySilent = true;
    private Long fridayEpoch;
    private String fridayTimeString;
    private String fridayDateString;
    private String blank;

    public FridayEntry() {
    }

    @Ignore
    public FridayEntry(Long date) {
        this.date = date;
        this.isFridaySilent = true;
        this.fridayTimeString = Converters.setTimeFromLong(fridayEpoch);
        this.fridayDateString = Converters.setDateFromLong(date);
        this.blank = "Select";
    }

    @Ignore
    public FridayEntry(Long date, Boolean isFridaySilent, Long fridayEpoch) {
        this.date = date;
        this.isFridaySilent = isFridaySilent;
        this.fridayEpoch = fridayEpoch;
        this.fridayTimeString = Converters.setTimeFromLong(fridayEpoch);
        this.fridayDateString = Converters.setDateFromLong(date);
    }


    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    public Boolean getFridaySilent() {
        return isFridaySilent;
    }

    public void setFridaySilent(Boolean fridaySilent) {
        isFridaySilent = fridaySilent;
    }

    public Long getFridayEpoch() {
        return fridayEpoch;
    }

    public void setFridayEpoch(Long fridayEpoch) {
        this.fridayEpoch = fridayEpoch;
    }

    public String getFridayTimeString() {
        return fridayTimeString;
    }

    public void setFridayTimeString(String fridayString) {
        this.fridayTimeString = fridayString;
    }

    public String getFridayDateString() {
        return fridayDateString;
    }

    public void setFridayDateString(String fridayDateString) {
        this.fridayDateString = fridayDateString;
    }

    public String getBlank() {
        return blank;
    }

    public void setBlank(String blank) {
        this.blank = blank;
    }
}
