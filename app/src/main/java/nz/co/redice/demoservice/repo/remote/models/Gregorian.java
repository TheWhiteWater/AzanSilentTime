
package nz.co.redice.demoservice.repo.remote.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Gregorian {

    @SerializedName("date")
    @Expose
    public String date;
    @SerializedName("format")
    @Expose
    public String format;
}
