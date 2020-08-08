package nz.co.redice.demoservice.viewmodel;

import android.annotation.SuppressLint;
import android.app.Application;

import androidx.annotation.NonNull;
import androidx.hilt.Assisted;
import androidx.hilt.lifecycle.ViewModelInject;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.SavedStateHandle;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import nz.co.redice.demoservice.repo.Repository;
import nz.co.redice.demoservice.repo.local.entity.EntryModel;
import nz.co.redice.demoservice.repo.local.entity.FridayEntry;
import nz.co.redice.demoservice.utils.PrefHelper;
import nz.co.redice.demoservice.utils.ServiceHelper;

public class HomeViewModel extends AndroidViewModel {

    private final SavedStateHandle savedStateHandle;
    public LiveData<FridayEntry> nextFriday;
    private Repository mRepository;
    private ServiceHelper mServiceHelper;
    private PrefHelper mPrefHelper;


    @ViewModelInject
    public HomeViewModel(@NonNull Application application,
                         Repository repository, ServiceHelper serviceHelper,
                         PrefHelper prefHelper,
                         @Assisted SavedStateHandle savedStateHandle) {
        super(application);
        mRepository = repository;
        this.savedStateHandle = savedStateHandle;
        mServiceHelper = serviceHelper;
        mServiceHelper.startService(application);
        mServiceHelper.doBindService(application);
        mPrefHelper = prefHelper;
    }


    public LiveData<EntryModel> getSelectedEntry(Long date) {
        return mRepository.getRegularEntry(date);
    }


    @Override
    protected void onCleared() {
        super.onCleared();
        mServiceHelper.doUnbindService(getApplication());
    }

    public LiveData<Integer> getRegularDatabaseSize() {
        return mRepository.getRegularTableSize();
    }

    public void requestPrayerCalendar() {
        mRepository.requestPrayerCalendar(mPrefHelper.getLatitude(), mPrefHelper.getLongitude());
    }

    public LiveData<EntryModel> updateRegularEntry(EntryModel model) {
        mRepository.updateRegularEntry(model);
        return mRepository.getRegularEntry(model.getDate());
    }

    @SuppressLint("CheckResult")
    public void populateFridayTable() {
        LocalDate currentDay = LocalDate.now();
        LocalDate endOfTheYear = LocalDate.of(2020, 12, 31);

        while (currentDay.isBefore(endOfTheYear)) {
            currentDay = calcNextFriday(currentDay);
            Observable.just(new FridayEntry(currentDay.atStartOfDay(ZoneId.systemDefault()).toEpochSecond()))
                    .subscribeOn(Schedulers.io())
                    .subscribe(s -> {
                        mRepository.insertFridayEntry(s);
                    });
        }
    }

    public LiveData<FridayEntry> getNextFridayEntry(LocalDate selectedDate) {
        Long nextFriday = calcNextFriday(selectedDate).atStartOfDay(ZoneId.systemDefault()).toEpochSecond();
        return mRepository.getFridayEntry(nextFriday);
    }

    private LocalDate calcNextFriday(LocalDate d) {
        return d.with(TemporalAdjusters.next(DayOfWeek.FRIDAY));
    }

    public LiveData<Integer> getFridayTableCount() {
        return mRepository.getFridayTableSize();
    }


    @SuppressLint("CheckResult")
    public void updateFridaysTable(int hourOfDay, int minute) {
        LocalDate targetDay = LocalDate.now();
        LocalDate endOfTheYear = LocalDate.of(2020, 12, 31);

        while (targetDay.isBefore(endOfTheYear)) {
            targetDay = calcNextFriday(targetDay);
            Long date = targetDay.atStartOfDay(ZoneId.systemDefault()).toEpochSecond();
            Long time = targetDay.atTime(LocalTime.of(hourOfDay, minute)).atZone(ZoneId.systemDefault()).toEpochSecond();

            Observable.just(new FridayEntry(date, true, time))
                    .subscribeOn(Schedulers.io())
                    .subscribe(s -> {
                        mRepository.updateFridayEntry(new FridayEntry(date, true, time));
                    });
        }
    }
}
