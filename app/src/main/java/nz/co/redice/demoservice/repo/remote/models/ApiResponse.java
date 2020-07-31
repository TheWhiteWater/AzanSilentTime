
package nz.co.redice.demoservice.repo.remote.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ApiResponse {

    @SerializedName("code")
    @Expose
    public Integer code;
    @SerializedName("data")
    @Expose
    public Data data;
    @SerializedName("status")
    @Expose
    public String status;

}
