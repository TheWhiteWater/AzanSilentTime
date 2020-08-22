package nz.co.redice.azansilenttime.repo.remote.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import nz.co.redice.azansilenttime.repo.local.entity.RegularEntry;
import nz.co.redice.azansilenttime.view.presentation.Converters;

public class Day {

    @SerializedName("timings")
    @Expose
    public Timings timings;
    @SerializedName("date")
    @Expose
    public Date date;
    @SerializedName("meta")
    @Expose
    public Meta meta;


    public RegularEntry toEntry() {
        return new RegularEntry(
                Converters.getLocalDateIntoLong(date.gregorian.date),
                Converters.getLocalTimeIntoLong(date.gregorian.date, timings.fajr),
                Converters.getLocalTimeIntoLong(date.gregorian.date, timings.dhuhr),
                Converters.getLocalTimeIntoLong(date.gregorian.date, timings.asr),
                Converters.getLocalTimeIntoLong(date.gregorian.date, timings.maghrib),
                Converters.getLocalTimeIntoLong(date.gregorian.date, timings.isha)
        );
    }
}
