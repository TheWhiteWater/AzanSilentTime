
package nz.co.redice.demoservice.repo.local.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.time.Month;

public class Gregorian {

    @SerializedName("date")
    @Expose
    public String date;
    @SerializedName("format")
    @Expose
    public String format;
    @SerializedName("day")
    @Expose
    public String day;


}
