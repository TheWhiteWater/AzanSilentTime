
package nz.co.redice.demoservice.repo.local.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Designation_ {

    @SerializedName("abbreviated")
    @Expose
    public String abbreviated;
    @SerializedName("expanded")
    @Expose
    public String expanded;

}