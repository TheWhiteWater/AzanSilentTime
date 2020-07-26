
package nz.co.redice.demoservice.repo.local.models;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ResponseModel {

    @SerializedName("code")
    @Expose
    public Integer code;
    @SerializedName("status")
    @Expose
    public String status;
    @SerializedName("data")
    @Expose
    public List<Datum> data = null;

}
