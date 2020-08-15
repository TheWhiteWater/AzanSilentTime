package nz.co.redice.azansilenttime.repo.local.entity;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import nz.co.redice.azansilenttime.view.presentation.Converters;

@Entity(tableName = "friday_table")
public class FridayEntry {

    @PrimaryKey
    private Long date;
    private Boolean isSilent = true;
    private Long timeEpoch;
    private String TimeString;
    private String DateString;
    private String blank;

    public FridayEntry() {
    }

    @Ignore
    public FridayEntry(Long date) {
        this.date = date;
        this.isSilent = true;
        this.TimeString = Converters.setTimeFromLong(timeEpoch);
        this.DateString = Converters.setDateFromLong(date);
        this.blank = "Select";
    }

    @Ignore
    public FridayEntry(Long date, Boolean isFridaySilent, Long timeEpoch) {
        this.date = date;
        this.isSilent = isFridaySilent;
        this.timeEpoch = timeEpoch;
        this.TimeString = Converters.setTimeFromLong(timeEpoch);
        this.DateString = Converters.setDateFromLong(date);
    }


    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    public Boolean getSilent() {
        return isSilent;
    }

    public void setSilent(Boolean fridaySilent) {
        isSilent = fridaySilent;
    }

    public Long getTimeEpoch() {
        return timeEpoch;
    }

    public void setTimeEpoch(Long timeEpoch) {
        this.timeEpoch = timeEpoch;
    }

    public String getTimeString() {
        return TimeString;
    }

    public void setTimeString(String fridayTimeString) {
        this.TimeString = fridayTimeString;
    }

    public String getDateString() {
        return DateString;
    }

    public void setDateString(String fridayDateString) {
        this.DateString = fridayDateString;
    }

    public String getBlank() {
        return blank;
    }

    public void setBlank(String blank) {
        this.blank = blank;
    }
}
