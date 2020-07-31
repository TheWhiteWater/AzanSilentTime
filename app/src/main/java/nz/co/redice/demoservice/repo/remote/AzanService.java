package nz.co.redice.demoservice.repo.remote;

import java.util.List;

import io.reactivex.Observable;
import nz.co.redice.demoservice.repo.remote.models.ApiResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface AzanService {

    @GET("calendar")
    Observable<ApiResponse> requestAnnualTimeTable(
            @Query("latitude") Double latitude,
            @Query("longitude") Double longitude,
            @Query("method") Integer method,
            @Query("year") Integer year,
            @Query("annual") Boolean annual);

    @GET("calendar")
    Call<ApiResponse> requestStandardAnnualTimeTable(
            @Query("latitude") Double latitude,
            @Query("longitude") Double longitude,
            @Query("method") Integer method,
            @Query("year") Integer year,
            @Query("annual") Boolean annual);

}
