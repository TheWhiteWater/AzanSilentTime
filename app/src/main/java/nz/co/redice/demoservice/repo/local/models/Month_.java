
package nz.co.redice.demoservice.repo.local.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Month_ {

    @SerializedName("number")
    @Expose
    public Integer number;
    @SerializedName("en")
    @Expose
    public String en;
    @SerializedName("ar")
    @Expose
    public String ar;

}
