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
import java.util.Calendar;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import nz.co.redice.azansilenttime.repo.Repository;
import nz.co.redice.azansilenttime.repo.local.entity.AlarmSchedule;
import nz.co.redice.azansilenttime.repo.local.entity.AzanTimings;
import nz.co.redice.azansilenttime.repo.remote.models.Day;
import nz.co.redice.azansilenttime.utils.SharedPreferencesHelper;

public class HomeViewModel extends AndroidViewModel {

    private static final String TAG = "App HomeViewModel";
    private MutableLiveData<AzanTimings> mAzanTimingsMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<AlarmSchedule> mAlarmScheduleMutableLiveData = new MutableLiveData<>();

    private Repository mRepository;
    private SharedPreferencesHelper mSharedPreferencesHelper;


    @SuppressLint("CheckResult")
    @ViewModelInject
    public HomeViewModel(@NonNull Application application,
                         Repository repository, SharedPreferencesHelper sharedPreferencesHelper,
                         @Assisted SavedStateHandle savedStateHandle) {
        super(application);
        mRepository = repository;
        mSharedPreferencesHelper = sharedPreferencesHelper;

    }

    public LiveData<AzanTimings> getAzanTimingsLiveData() {
        return mAzanTimingsMutableLiveData;
    }

    public void setAzanTimingsLiveData(AzanTimings azanTimings) {
        mAzanTimingsMutableLiveData.postValue(azanTimings);
    }


    public LiveData<AlarmSchedule> getAlarmSchedules() {
        if (mAlarmScheduleMutableLiveData == null)
            getAlarmSchedule();
        return mAlarmScheduleMutableLiveData;
    }

    public void setAlarmSchedule(AlarmSchedule alarmSchedule) {
        mAlarmScheduleMutableLiveData.postValue(alarmSchedule);
    }

    @SuppressLint("CheckResult")
    public void selectAzanTimings(LocalDate date) {
        Long target = LocalDate.from(date).atStartOfDay(ZoneId.systemDefault()).toEpochSecond();
        Observable.fromCallable(() -> mRepository.getRegularEntry(target))
                .subscribeOn(Schedulers.io())
                .subscribe(this::setAzanTimingsLiveData, throwable -> Log.d(TAG, throwable.getMessage(), throwable));
    }


    @SuppressLint("CheckResult")
    public void getAlarmSchedule() {
        Observable.fromCallable(() -> mRepository.getAlarmSchedule())
                .subscribeOn(Schedulers.io())
                .subscribe(this::setAlarmSchedule, throwable -> Log.d(TAG, throwable.getMessage(), throwable));
    }

    public void updateAlarmSchedule(AlarmSchedule alarmSchedule) {
        mRepository.updateAlarmSchedule(alarmSchedule);
    }


    @SuppressLint("CheckResult")
    public void populateAzanTimings() {
        mRepository.getAzanService().requestRegularCalendar(
                mSharedPreferencesHelper.getLatitude(),
                mSharedPreferencesHelper.getLongitude(),
                mSharedPreferencesHelper.getCalculationMethod(),
                mSharedPreferencesHelper.getCalculationSchool(),
                mSharedPreferencesHelper.getMidnightMode(),
                Calendar.getInstance().get(Calendar.YEAR),
                true)
                .subscribeOn(Schedulers.io())
                .toObservable()
                .flatMap(s -> Observable.fromIterable(s.data.getAnnualList()))
                .map(Day::toEntry)
                .doOnComplete(() -> {
                    selectAzanTimings(LocalDate.now());
                    mSharedPreferencesHelper.setRegularTableToBePopulated(false);
                })
                .subscribe(s -> mRepository.insertRegularEntry(s),
                        error -> Log.e(TAG, "populateRegularTable: " + error.getMessage()));

    }


    @SuppressLint("CheckResult")
    public void createAlarmSchedules() {
        mRepository.insertAlarmSchedule(new AlarmSchedule());
        mSharedPreferencesHelper.setAlarmSchedulesToBeCreated(false);
        getAlarmSchedule();
    }


    @SuppressLint("DefaultLocale")
    public void saveFridayAlarmSchedule(int hourOfDay, int minute) {
        AlarmSchedule alarmSchedule = new AlarmSchedule();
        String hour;
        String minutes;

        if (hourOfDay>10)
            hour = String.format("0%d", hourOfDay);
        else
            hour = String.valueOf(hourOfDay);

        if (hourOfDay>10)
            minutes = String.format("0%d", minute);
        else
            minutes = String.valueOf(hourOfDay);

        alarmSchedule.setFridayAlarmHour(hour);
        alarmSchedule.setFridayAlarmMinute(minutes);
        mRepository.updateAlarmSchedule(alarmSchedule);
    }
}