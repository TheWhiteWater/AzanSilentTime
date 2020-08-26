package nz.co.redice.azansilenttime.repo.remote;

import io.reactivex.Single;
import nz.co.redice.azansilenttime.repo.remote.models.ApiResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface AzanService {

    @GET("calendar")
    Single<ApiResponse> requestRegularCalendar(
            @Query("latitude") Float latitude,
            @Query("longitude") Float longitude,
            @Query("method") Integer method,
            @Query("school") Integer school,
            @Query("midnightMode") Integer midnightMode,
            @Query("year") Integer year,
            @Query("annual") Boolean annual);

}
