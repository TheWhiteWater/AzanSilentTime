package nz.co.redice.azansilenttime.viewmodel;

import android.annotation.SuppressLint;
import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.hilt.Assisted;
import androidx.hilt.lifecycle.ViewModelInject;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;

import org.jetbrains.annotations.NotNull;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.Calendar;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import nz.co.redice.azansilenttime.repo.Repository;
import nz.co.redice.azansilenttime.repo.local.entity.FridayEntry;
import nz.co.redice.azansilenttime.repo.local.entity.RegularEntry;
import nz.co.redice.azansilenttime.repo.remote.models.ApiResponse;
import nz.co.redice.azansilenttime.repo.remote.models.Day;
import nz.co.redice.azansilenttime.utils.PrefHelper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeViewModel extends AndroidViewModel {

    private static final String TAG = "App HomeViewModel";
    private final SavedStateHandle savedStateHandle;
    private MutableLiveData<RegularEntry> mRegularEntry = new MutableLiveData<>();
    private MutableLiveData<FridayEntry> mFridayEntry = new MutableLiveData<>();

    private Repository mRepository;
    private PrefHelper mPrefHelper;


    @SuppressLint("CheckResult")
    @ViewModelInject
    public HomeViewModel(@NonNull Application application,
                         Repository repository, PrefHelper prefHelper,
                         @Assisted SavedStateHandle savedStateHandle) {
        super(application);
        mRepository = repository;
        this.savedStateHandle = savedStateHandle;
        mPrefHelper = prefHelper;

    }

    public void checkDatabase() {
        if (mPrefHelper.isDatabaseNeedsUpdate())
            updatePrayerCalendar();
    }

    public LiveData<RegularEntry> getRegularObservable() {
        return mRegularEntry;
    }

    public void setRegularObservable(RegularEntry observableLiveEntry) {
        mRegularEntry.postValue(observableLiveEntry);
    }


    public LiveData<FridayEntry> getFridayEntry() {
        if (mFridayEntry == null)
            selectNewFridayEntry(LocalDate.now());
        return mFridayEntry;
    }

    public void setFridayObservable(FridayEntry fridayEntry) {
        mFridayEntry.postValue(fridayEntry);
    }

    public LiveData<Integer> getRegularBaseSize() {
        return mRepository.getRegularBaseSize();
    }

    @SuppressLint("CheckResult")
    public void selectNewEntry(LocalDate date) {
        Log.d(TAG, "selectNewEntry: selecting entry from database ...");
        Long target = LocalDate.from(date).atStartOfDay(ZoneId.systemDefault()).toEpochSecond();
        Observable.fromCallable(() -> mRepository.getRegularEntry(target))
                .subscribeOn(Schedulers.io())
                .subscribe(this::setRegularObservable, throwable -> Log.d(TAG, throwable.getMessage(), throwable));
    }


    @SuppressLint("CheckResult")
    public void selectNewFridayEntry(LocalDate date) {
        Log.d(TAG, "selectNewFridayEntry: selecting entry from database ...");
        Long nextFriday = calcNextFriday(date.minusDays(1)).atStartOfDay(ZoneId.systemDefault()).toEpochSecond();
        Observable.fromCallable(() -> mRepository.getFridayEntry(nextFriday))
                .subscribeOn(Schedulers.io())
                .subscribe(this::setFridayObservable, throwable -> {
                    Log.d(TAG, throwable.getMessage(), throwable);
                });
    }

    @SuppressLint("CheckResult")
    public void selectNewFridayEntry(Long value) {
        Log.d(TAG, "selectNewFridayEntry: selecting entry from database ...");
        Observable.fromCallable(() -> mRepository.getFridayEntry(value))
                .subscribeOn(Schedulers.io())
                .subscribe(this::setFridayObservable, throwable -> {
                    Log.d(TAG, throwable.getMessage(), throwable);
                });
    }


    public void updateEntry(RegularEntry entry) {
        Log.d(TAG, "updateRegularEntry: ");
        mRepository.updateRegularEntry(entry);
    }


    public void updatePrayerCalendar() {
        mRepository.getAzanService().requestStandardAnnualTimeTable(
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
                                .subscribe(s -> mRepository.insertRegularEntry(day.toEntry()));
                    }
                    selectNewEntry(LocalDate.now());
                    mPrefHelper.setDatabaseNeedsUpdate(false);
                }
            }

            @Override
            public void onFailure(@NotNull Call<ApiResponse> call, @NotNull Throwable t) {
                Log.d(TAG, "onFailure: standard request" + t.getMessage());
            }
        });
    }


    @SuppressLint("CheckResult")
    public void populateFridayTable() {
        LocalDate targetDay = LocalDate.now().minusDays(1);
        LocalDate endOfTheYear = LocalDate.of(Calendar.getInstance().get(Calendar.YEAR), 12, 31);

        while (targetDay.isBefore(endOfTheYear)) {
            targetDay = calcNextFriday(targetDay);

            Long date = targetDay.atStartOfDay(ZoneId.systemDefault()).toEpochSecond();
            Long time = targetDay.atStartOfDay(ZoneId.systemDefault()).toEpochSecond();
            FridayEntry fridayEntry = new FridayEntry(date, true, time);
            Log.d(TAG, "populateFridayTable: " + fridayEntry.getDateString());
            mRepository.insertFridayEntry(fridayEntry);
        }
    }

    private LocalDate calcNextFriday(LocalDate day) {
        return day.with(TemporalAdjusters.next(DayOfWeek.FRIDAY));
    }

    @SuppressLint("CheckResult")
    public void updateFridaysTable(int hourOfDay, int minute) {
        LocalDate targetDay = LocalDate.now().minusDays(1);
        LocalDate endOfTheYear = LocalDate.of(Calendar.getInstance().get(Calendar.YEAR), 12, 31);

        while (targetDay.isBefore(endOfTheYear)) {
            targetDay = calcNextFriday(targetDay);
            Long date = targetDay.atStartOfDay(ZoneId.systemDefault()).toEpochSecond();
            Long time = targetDay.atTime(LocalTime.of(hourOfDay, minute)).atZone(ZoneId.systemDefault()).toEpochSecond();
            mRepository.updateFridayEntry(new FridayEntry(date, true, time));
        }
    }

    @SuppressLint("CheckResult")
    public void updateFridayEntry(FridayEntry fridayEntry) {
        mRepository.updateFridayEntry(fridayEntry);
    }

    public LiveData<Integer> getFridayTableCount() {
        return mRepository.getFridayTableRowCount();
    }

}