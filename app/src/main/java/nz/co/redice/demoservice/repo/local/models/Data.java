
package nz.co.redice.demoservice.repo.local.models;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Data {

    @SerializedName("1")
    @Expose
    public List<Entry> entries = null;

}
