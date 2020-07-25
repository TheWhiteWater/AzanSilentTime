
package nz.co.redice.demoservice.repo.local.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Timings {

    @SerializedName("Fajr")
    @Expose
    public String fajr;
    @SerializedName("Sunrise")
    @Expose
    public String sunrise;
    @SerializedName("Dhuhr")
    @Expose
    public String dhuhr;
    @SerializedName("Asr")
    @Expose
    public String asr;
    @SerializedName("Sunset")
    @Expose
    public String sunset;
    @SerializedName("Maghrib")
    @Expose
    public String maghrib;
    @SerializedName("Isha")
    @Expose
    public String isha;
    @SerializedName("Imsak")
    @Expose
    public String imsak;
    @SerializedName("Midnight")
    @Expose
    public String midnight;

}
