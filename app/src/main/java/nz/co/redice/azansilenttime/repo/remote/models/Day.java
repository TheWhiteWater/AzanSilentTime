package nz.co.redice.azansilenttime.repo.remote.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import nz.co.redice.azansilenttime.repo.local.entity.AzanTimings;
import nz.co.redice.azansilenttime.ui.presentation.Converters;

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


    public AzanTimings toEntry() {
        return new AzanTimings(
                Converters.convertTextDateIntoEpoch(date.gregorian.date),
                Converters.convertTextDateAndTextTimeIntoEpoch(date.gregorian.date, timings.fajr),
                Converters.convertTextDateAndTextTimeIntoEpoch(date.gregorian.date, timings.dhuhr),
                Converters.convertTextDateAndTextTimeIntoEpoch(date.gregorian.date, timings.asr),
                Converters.convertTextDateAndTextTimeIntoEpoch(date.gregorian.date, timings.maghrib),
                Converters.convertTextDateAndTextTimeIntoEpoch(date.gregorian.date, timings.isha)
        );
    }
}
