
package nz.co.redice.demoservice.repo.local.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Params {

    @SerializedName("Fajr")
    @Expose
    public Integer fajr;
    @SerializedName("Isha")
    @Expose
    public Integer isha;

}
