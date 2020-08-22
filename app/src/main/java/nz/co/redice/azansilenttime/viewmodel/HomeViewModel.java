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

import java.time.LocalDate;
import java.time.ZoneId;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import nz.co.redice.azansilenttime.repo.Repository;
import nz.co.redice.azansilenttime.repo.local.entity.RegularEntry;
import nz.co.redice.azansilenttime.utils.PrefHelper;

public class HomeViewModel extends AndroidViewModel {

    private static final String TAG = "App HomeViewModel";
    private final SavedStateHandle savedStateHandle;
    //    public LiveData<FridayEntry> nextFriday;
    private MutableLiveData<RegularEntry> mObservableLiveEntry;

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


    public LiveData<RegularEntry> getObservableLiveEntry() {
        if (mObservableLiveEntry == null) {
            mObservableLiveEntry = new MutableLiveData<>();
            selectNewObservableLiveEntry(LocalDate.now());
        }
        return mObservableLiveEntry;
    }

    private void updateObservableLiveEntry(RegularEntry entry) {
        Log.d(TAG, "updateLiveEntry: ");
        mObservableLiveEntry.setValue(entry);
    }

    @SuppressLint("CheckResult")
    public void selectNewObservableLiveEntry(LocalDate date) {
        Log.d(TAG, "selectNewLiveEntry: ");
        Long target = LocalDate.from(date).atStartOfDay(ZoneId.systemDefault()).toEpochSecond();
        final RegularEntry[] result = new RegularEntry[1];
        Observable.just(target)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        s -> {
                            result[0] = mRepository.getRegularEntry(s);
                            updateObservableLiveEntry(result[0]);
                        }, e -> Log.d(TAG, "selectNewLiveEntry: " + e.getMessage()));

    }

    public void requestPrayerCalendar() {
        mRepository.requestPrayerCalendar();
    }

//    public void updateRegularEntry(RegularEntry entry) {
//        Log.d(TAG, "updateRegularEntry: ");
//        mRepository.updateRegularEntry(entry);
//    }


//    @SuppressLint("CheckResult")
//    public void populateFridayTable() {
//        LocalDate targetDay = LocalDate.now().minusDays(1);
//        LocalDate endOfTheYear = LocalDate.of(Calendar.getInstance().get(Calendar.YEAR), 12, 31);
//
//        while (targetDay.isBefore(endOfTheYear)) {
//            targetDay = calcNextFriday(targetDay);
//
//            Long date = targetDay.atStartOfDay(ZoneId.systemDefault()).toEpochSecond();
//            Long time = targetDay.atStartOfDay(ZoneId.systemDefault()).toEpochSecond();
//
//            Observable.just(new FridayEntry(date, true, time))
//                    .subscribeOn(Schedulers.io())
//                    .subscribe(s -> mRepository.insertFridayEntry(s));
//
//        }
//
//        nextFriday = getNextFridayEntry(LocalDate.now());
//    }

//    public LiveData<FridayEntry> getNextFridayEntry(LocalDate selectedDate) {
//        Long nextFriday = calcNextFriday(selectedDate.minusDays(1)).atStartOfDay(ZoneId.systemDefault()).toEpochSecond();
//        return mRepository.getFridayEntry(nextFriday);
//    }

//    private LocalDate calcNextFriday(LocalDate day) {
//        return day.with(TemporalAdjusters.next(DayOfWeek.FRIDAY));
//    }
//
//    public LiveData<Integer> getFridayTableCount() {
//        return mRepository.getFridayTableSize();
//    }


//    @SuppressLint("CheckResult")
//    public void updateFridaysTable(int hourOfDay, int minute) {
//        LocalDate targetDay = LocalDate.now().minusDays(1);
//        LocalDate endOfTheYear = LocalDate.of(Calendar.getInstance().get(Calendar.YEAR), 12, 31);
//
//        while (targetDay.isBefore(endOfTheYear)) {
//            targetDay = calcNextFriday(targetDay);
//            Long date = targetDay.atStartOfDay(ZoneId.systemDefault()).toEpochSecond();
//            Long time = targetDay.atTime(LocalTime.of(hourOfDay, minute)).atZone(ZoneId.systemDefault()).toEpochSecond();
//            mRepository.updateFridayEntry(new FridayEntry(date, true, time));
//        }
//    }

//    @SuppressLint("CheckResult")
//    public void updateFridayEntry(FridayEntry fridayEntry) {
//        mRepository.updateFridayEntry(fridayEntry);
//    }
}