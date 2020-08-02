package nz.co.redice.demoservice.repo;

import android.util.Log;

import androidx.lifecycle.LiveData;

import org.jetbrains.annotations.NotNull;

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

    public void requestStandardAnnualCalendar(Float lat, Float lon) {
        mAzanService.requestStandardAnnualTimeTable(lat, lon, MUSLIM_WORLD_LEAGUE_METHOD,
                Calendar.getInstance().get(Calendar.YEAR), true).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(@NotNull Call<ApiResponse> call, @NotNull Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    for (Day day : response.body().data.getAnnualList()) {
                        Completable.fromAction(() -> {
                            EntryModel newEntryModel = day.toEntry();
                            mDao.insertEntry(newEntryModel);
                        })
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribeOn(Schedulers.io())
                                .subscribe();
                    }
                }
            }

            @Override
            public void onFailure(@NotNull Call<ApiResponse> call, @NotNull Throwable t) {
                Log.d(TAG, "onFailure: standard request" + t.getMessage());
            }
        });
    }

    public LiveData<EntryModel> getSelectedDate(Long value) {
        return mDao.getSelectedEntry(value);
    }

    public LiveData<Integer> getDatabaseSize() {
         return mDao.getRowCount();
    }

    public void updateSelectedEntry(EntryModel model){
        mDao.updateEntry(model)
                .subscribeOn(Schedulers.io())
                .subscribe();
    }


}
