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
import io.reactivex.schedulers.Schedulers;
import nz.co.redice.azansilenttime.repo.Repository;
import nz.co.redice.azansilenttime.repo.local.entity.RegularEntry;
import nz.co.redice.azansilenttime.utils.PrefHelper;

public class HomeViewModel extends AndroidViewModel {

    private static final String TAG = "App HomeViewModel";
    private final SavedStateHandle savedStateHandle;
    private MutableLiveData<RegularEntry> mObservableLiveEntry = new MutableLiveData<>();

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


    public LiveData<RegularEntry> getObservable() {
        return mObservableLiveEntry;
    }

    public void setObservable(RegularEntry observableLiveEntry) {
        mObservableLiveEntry.postValue(observableLiveEntry);
    }

    @SuppressLint("CheckResult")
    public void selectNewEntry(LocalDate date) {
        Log.d(TAG, "selectNewEntry: selecting entry from database ...");
        Long target = LocalDate.from(date).atStartOfDay(ZoneId.systemDefault()).toEpochSecond();
        Observable.fromCallable(() -> mRepository.getRegularEntry(target))
                .subscribeOn(Schedulers.io())
                .subscribe(
                        s -> setObservable(s),
                        e -> Log.d(TAG, "selectNewEntry: " + e.getMessage()));
    }


    public LiveData<RegularEntry> selectLiveDataEntry(LocalDate date) {
        Long target = LocalDate.from(date).atStartOfDay(ZoneId.systemDefault()).toEpochSecond();
        return mRepository.getLiveDataEntryByDate(target);
    }

    public void updateEntry(RegularEntry entry) {
        Log.d(TAG, "updateRegularEntry: ");
        mRepository.updateRegularEntry(entry);
    }


}