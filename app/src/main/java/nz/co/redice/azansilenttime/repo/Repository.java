package nz.co.redice.azansilenttime.repo;

import android.annotation.SuppressLint;
import android.util.Log;

import androidx.lifecycle.LiveData;

import org.jetbrains.annotations.NotNull;

import java.util.Calendar;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import nz.co.redice.azansilenttime.repo.local.EventDao;
import nz.co.redice.azansilenttime.repo.local.entity.FridayEntry;
import nz.co.redice.azansilenttime.repo.local.entity.RegularEntry;
import nz.co.redice.azansilenttime.repo.remote.AzanService;
import nz.co.redice.azansilenttime.repo.remote.models.ApiResponse;
import nz.co.redice.azansilenttime.repo.remote.models.Day;
import nz.co.redice.azansilenttime.utils.PrefHelper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Repository {

    private static final String TAG = "App Repo";
    private final EventDao mDao;
    private AzanService mAzanService;
    private PrefHelper mPrefHelper;


    @Inject
    public Repository(AzanService newsService, EventDao dao, PrefHelper prefHelper) {
        mAzanService = newsService;
        mDao = dao;
        mPrefHelper = prefHelper;

    }


    public void requestPrayerCalendar() {
        getAzanService().requestStandardAnnualTimeTable(
                mPrefHelper.getLatitude(),
                mPrefHelper.getLongitude(),
                mPrefHelper.getCalculationMethod(),
                mPrefHelper.getCalculationSchool(),
                mPrefHelper.getMidnightMode(),
                Calendar.getInstance().get(Calendar.YEAR),
                true
        ).enqueue(new Callback<ApiResponse>() {
            @SuppressLint("CheckResult")
            @Override
            public void onResponse(@NotNull Call<ApiResponse> call, @NotNull Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    for (Day day : response.body().data.getAnnualList()) {
                        Observable.just(day)
                                .subscribeOn(Schedulers.io())
                                .subscribe(s -> mDao.insertEntry(day.toEntry()));
                    }
                }
            }

            @Override
            public void onFailure(@NotNull Call<ApiResponse> call, @NotNull Throwable t) {
                Log.d(TAG, "onFailure: standard request" + t.getMessage());
            }
        });
    }


    public AzanService getAzanService() {
        return mAzanService;
    }

    public LiveData<Integer> getRegularBaseSize() {
        return mDao.getRegularTableRowCount();
    }

    public void insertRegularEntry(RegularEntry entry) {
        mDao.insertEntry(entry);
    }

    public void deletePrayerCalendar() {
        Completable.fromAction(mDao::deleteCalendar)
                .subscribeOn(Schedulers.io())
                .subscribe();
    }

    public RegularEntry getRegularEntry(Long value) {
        return mDao.getSelectedRegularEntry(value);
    }

    public void updateRegularEntry(RegularEntry model) {
        mDao.updateEntry(model)
                .subscribeOn(Schedulers.computation())
                .subscribe();
        Log.d(TAG, "updateRegularEntry: updated");
    }


    public FridayEntry getFridayEntry(Long value) {
        return mDao.getSelectedFridayEntry(value);
    }

    @SuppressLint("CheckResult")
    public void updateFridayEntry(FridayEntry fridayEntry) {
        Observable.just(fridayEntry)
                .subscribeOn(Schedulers.io())
                .subscribe(s -> mDao.updateFridayEntry(fridayEntry));
    }

    public void deleteFridayTable() {
        mDao.deleteAllFridayTable();
    }


    @SuppressLint("CheckResult")
    public void insertFridayEntry(FridayEntry fridayEntry) {
        Observable.just(fridayEntry)
                .subscribeOn(Schedulers.io())
                .subscribe(mDao::insertFridayEntry);
    }

    public LiveData<Integer> getFridayTableRowCount() {
        return mDao.getFridaysRowCount();
    }

}