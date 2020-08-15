
package nz.co.redice.azansilenttime.repo.remote.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ApiResponse {


    @SerializedName("data")
    @Expose
    public Data data;


}
