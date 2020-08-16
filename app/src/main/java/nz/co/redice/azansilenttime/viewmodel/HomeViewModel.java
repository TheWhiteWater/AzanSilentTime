package nz.co.redice.azansilenttime.viewmodel;

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
import java.util.Calendar;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import nz.co.redice.azansilenttime.repo.Repository;
import nz.co.redice.azansilenttime.repo.local.entity.EntryModel;
import nz.co.redice.azansilenttime.repo.local.entity.FridayEntry;
import nz.co.redice.azansilenttime.utils.PrefHelper;

public class HomeViewModel extends AndroidViewModel {

    private final SavedStateHandle savedStateHandle;
    public LiveData<FridayEntry> nextFriday;
    private Repository mRepository;
    private PrefHelper mPrefHelper;


    @ViewModelInject
    public HomeViewModel(@NonNull Application application,
                         Repository repository, PrefHelper prefHelper,
                         @Assisted SavedStateHandle savedStateHandle) {
        super(application);
        mRepository = repository;
        this.savedStateHandle = savedStateHandle;
        mPrefHelper = prefHelper;
    }


    public LiveData<EntryModel> getSelectedEntry(Long date) {
        return mRepository.getRegularEntry(date);
    }


    public LiveData<Integer> getRegularDatabaseSize() {
        return mRepository.getRegularTableSize();
    }

    public void requestPrayerCalendar() {
        mRepository.requestPrayerCalendar();
    }

    public LiveData<EntryModel> updateRegularEntry(EntryModel model) {
        mRepository.updateRegularEntry(model);
        return mRepository.getRegularEntry(model.getDate());
    }

    @SuppressLint("CheckResult")
    public void populateFridayTable() {
        LocalDate targetDay = LocalDate.now().minusDays(1);
        // TODO: 15.08.2020 link to current year
        LocalDate endOfTheYear = LocalDate.of(Calendar.getInstance().get(Calendar.YEAR), 12, 31);

        while (targetDay.isBefore(endOfTheYear)) {
            targetDay = calcNextFriday(targetDay);

            Long date = targetDay.atStartOfDay(ZoneId.systemDefault()).toEpochSecond();
            Long time = targetDay.atStartOfDay(ZoneId.systemDefault()).toEpochSecond();

            Observable.just(new FridayEntry(date, true, time))
                    .subscribeOn(Schedulers.io())
                    .subscribe(s -> mRepository.insertFridayEntry(s));

        }

        nextFriday = getNextFridayEntry(LocalDate.now());
    }

    public LiveData<FridayEntry> getNextFridayEntry(LocalDate selectedDate) {
        Long nextFriday = calcNextFriday(selectedDate.minusDays(1)).atStartOfDay(ZoneId.systemDefault()).toEpochSecond();
        return mRepository.getFridayEntry(nextFriday);
    }

    private LocalDate calcNextFriday(LocalDate day) {
        return day.with(TemporalAdjusters.next(DayOfWeek.FRIDAY));
    }

    public LiveData<Integer> getFridayTableCount() {
        return mRepository.getFridayTableSize();
    }


    @SuppressLint("CheckResult")
    public void updateFridaysTable(int hourOfDay, int minute) {
        LocalDate targetDay = LocalDate.now().minusDays(1);
        // TODO: 15.08.2020 link to current year
        LocalDate endOfTheYear = LocalDate.of(Calendar.getInstance().get(Calendar.YEAR), 12, 31);

        while (targetDay.isBefore(endOfTheYear)) {
            targetDay = calcNextFriday(targetDay);
            Long date = targetDay.atStartOfDay(ZoneId.systemDefault()).toEpochSecond();
            Long time = targetDay.atTime(LocalTime.of(hourOfDay, minute)).atZone(ZoneId.systemDefault()).toEpochSecond();

            Observable.just(new FridayEntry(date, true, time))
                    .subscribeOn(Schedulers.io())
                    .subscribe(s -> mRepository.updateFridayEntry(s));
        }
    }
}