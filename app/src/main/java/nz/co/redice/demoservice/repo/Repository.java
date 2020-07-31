package nz.co.redice.demoservice.repo;

import android.util.Log;

import androidx.lifecycle.LiveData;

import java.util.Calendar;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import nz.co.redice.demoservice.repo.local.EventDao;
import nz.co.redice.demoservice.repo.local.entity.EntryModel;
import nz.co.redice.demoservice.repo.remote.AzanService;
import nz.co.redice.demoservice.repo.remote.models.ApiResponse;
import nz.co.redice.demoservice.repo.remote.models.Day;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Repository {

    private static final String TAG = "Repo";
    private static final int MUSLIM_WORLD_LEAGUE_METHOD = 3;
    private final AzanService mAzanService;
    private final EventDao mDao;

    @Inject
    public Repository(AzanService newsService, EventDao dao) {
        mAzanService = newsService;
        mDao = dao;
    }

//    public void requestAnnualCalendar(Double latitude, Double longitude) {
//        mAzanService.requestAnnualTimeTable(latitude, longitude,
//                MUSLIM_WORLD_LEAGUE_METHOD,
//                Calendar.getInstance().get(Calendar.YEAR), true)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .flatMap(s -> Observable.fromIterable(s.data._7))
//                .map(s -> s.toEntry())
//                .subscribe(s -> {
//                            mDao.insertEntry(s);
//                        },
//                        error -> {
//                            Log.d(TAG, "requestAnnualCalendar: " + error.getMessage());
//                            Log.d(TAG, "requestAnnualCalendar: " + error.getStackTrace());
//                            Log.d(TAG, "requestAnnualCalendar: " + error.getCause());
//                        });
//    }

    public void requestStandardAnnualCalendar(Double lat, Double lon) {
        mAzanService.requestStandardAnnualTimeTable(lat, lon, MUSLIM_WORLD_LEAGUE_METHOD,
                Calendar.getInstance().get(Calendar.YEAR), true).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    for (Day day : response.body().data._7) {
                        Completable.fromAction(() -> {
                            EntryModel newEntryModel = day.toEntry();
                            Log.d(TAG, "onResponse: " + newEntryModel.toString());
                            mDao.insertEntry(newEntryModel);
                            Log.d(TAG, "onResponse: " + mDao.getRowCount().getValue());
                        })
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribeOn(Schedulers.io())
                                .subscribe();
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Log.d(TAG, "onFailure: standard request" + t.getMessage());
            }
        });
    }

    public LiveData<EntryModel> getTimesForSelectedDate(Long value) {
        return mDao.getAzanTimesForDate(value);
    }

    public LiveData<Integer> getDatabaseSize() {
        return mDao.getRowCount();
    }


    public void testInsert() {
        Completable.fromAction(() -> mDao.insertEntry(new EntryModel()))
                .subscribeOn(Schedulers.io())
                .subscribe();


    }


}
