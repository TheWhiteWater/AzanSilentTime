package nz.co.redice.demoservice.repo.remote;

import io.reactivex.Single;
import nz.co.redice.demoservice.repo.local.models.ResponseModel;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface AzanService {


    @GET("calendar")
    Single<ResponseModel> requestAnnualTimeTable(
            @Query("latitude") Double latitude,
            @Query("longitude") Double longitude,
            @Query("year") Integer year,
            @Query("annual") Boolean annual,
            @Query("method") Integer method);


    @GET("calendar")
    Single<ResponseModel> requestTimeTableForMonth(
            @Query("latitude") Double latitude,
            @Query("longitude") Double longitude,
            @Query("year") Integer year,
            @Query("method") Integer method);
}
