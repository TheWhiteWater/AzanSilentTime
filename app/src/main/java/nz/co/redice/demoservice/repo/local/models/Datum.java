
package nz.co.redice.demoservice.repo.local.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Datum {

    @SerializedName("timings")
    @Expose
    public Timings timings;
    @SerializedName("date")
    @Expose
    public Date date;
    @SerializedName("meta")
    @Expose
    public Meta meta;

}
