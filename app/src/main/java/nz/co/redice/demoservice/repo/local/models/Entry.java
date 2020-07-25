
package nz.co.redice.demoservice.repo.local.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Entry {

    @SerializedName("timings")
    @Expose
    public Timings timings;
    @SerializedName("date")
    @Expose
    public Date date;

}
