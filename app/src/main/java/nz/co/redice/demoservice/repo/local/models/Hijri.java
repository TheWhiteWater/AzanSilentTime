
package nz.co.redice.demoservice.repo.local.models;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Hijri {

    @SerializedName("date")
    @Expose
    public String date;
    @SerializedName("format")
    @Expose
    public String format;
    @SerializedName("day")
    @Expose
    public String day;
    @SerializedName("weekday")
    @Expose
    public Weekday_ weekday;
    @SerializedName("month")
    @Expose
    public Month_ month;
    @SerializedName("year")
    @Expose
    public String year;
    @SerializedName("designation")
    @Expose
    public Designation_ designation;
    @SerializedName("holidays")
    @Expose
    public List<Object> holidays = null;

}
