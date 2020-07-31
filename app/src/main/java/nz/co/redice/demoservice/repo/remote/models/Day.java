package nz.co.redice.demoservice.repo.remote.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import nz.co.redice.demoservice.repo.local.entity.EntryModel;
import nz.co.redice.demoservice.utils.Converters;

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


    public EntryModel toEntry() {
        return new EntryModel(
                Converters.getLocalDateIntoLong(date.gregorian.date),
                Converters.getLocalTimeIntoLong(date.gregorian.date, timings.fajr),
                Converters.getLocalTimeIntoLong(date.gregorian.date, timings.dhuhr),
                Converters.getLocalTimeIntoLong(date.gregorian.date, timings.asr),
                Converters.getLocalTimeIntoLong(date.gregorian.date, timings.maghrib),
                Converters.getLocalTimeIntoLong(date.gregorian.date, timings.isha)
        );
    }
}
