package nz.co.redice.azansilenttime.repo;

import android.util.Log;

import androidx.lifecycle.LiveData;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import nz.co.redice.azansilenttime.repo.local.EventDao;
import nz.co.redice.azansilenttime.repo.local.entity.EntryModel;
import nz.co.redice.azansilenttime.repo.local.entity.FridayEntry;
import nz.co.redice.azansilenttime.repo.remote.AzanService;
import nz.co.redice.azansilenttime.repo.remote.models.ApiResponse;
import nz.co.redice.azansilenttime.repo.remote.models.Day;
import nz.co.redice.azansilenttime.utils.PrefHelper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Repository {

    private static final String TAG = "Repo";
    private static final int MUSLIM_WORLD_LEAGUE_METHOD = 3;
    private final AzanService mAzanService;
    private final EventDao mDao;
    private PrefHelper mPrefHelper;

    @Inject
    public Repository(AzanService newsService, EventDao dao, PrefHelper prefHelper) {
        mAzanService = newsService;
        mDao = dao;
        mPrefHelper = prefHelper;
    }

    public void requestPrayerCalendar() {
        mAzanService.requestStandardAnnualTimeTable(
                mPrefHelper.getLatitude(),
                mPrefHelper.getLongitude(),
                mPrefHelper.getCalculationMethod(),
                mPrefHelper.getCalculationSchool(),
                mPrefHelper.getMidnightMode(),
                Calendar.getInstance().get(Calendar.YEAR),
                true
        ).enqueue(new Callback<ApiResponse>() {
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

    public void deletePrayerCalendar() {
        Completable.fromAction(() -> mDao.deleteCalendar())
                .subscribeOn(Schedulers.io())
                .subscribe();
    }

    public LiveData<EntryModel> getRegularEntry(Long value) {
        LocalDate.now().atStartOfDay(ZoneId.systemDefault());
        Log.d(TAG, "getRegularEntry: ");
        return mDao.getSelectedEntry(value);
    }

    public LiveData<Integer> getRegularTableSize() {
        return mDao.getRowCount();
    }

    public LiveData<Integer> getFridayTableSize() {
        return mDao.getFridaysCount();
    }

    public void updateRegularEntry(EntryModel model) {
        mDao.updateEntry(model)
                .subscribeOn(Schedulers.io())
                .subscribe();
    }

    public void insertFridayEntry(FridayEntry fridayEntry) {
        mDao.insertFridayEntry(fridayEntry);
    }

    public LiveData<FridayEntry> getFridayEntry(Long value) {
        return mDao.getSelectedFridayEntry(value);
    }

    public void updateFridayEntry(FridayEntry fridayEntry) {
        mDao.updateFridayEntry(fridayEntry);
    }

    public void deleteFridayTable() {
        mDao.deleteAllFridayTable();
    }

}
