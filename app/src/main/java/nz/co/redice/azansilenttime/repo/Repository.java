package nz.co.redice.azansilenttime.repo;

import android.annotation.SuppressLint;

import androidx.lifecycle.LiveData;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import nz.co.redice.azansilenttime.repo.local.EventDao;
import nz.co.redice.azansilenttime.repo.local.entity.FridayEntry;
import nz.co.redice.azansilenttime.repo.local.entity.RegularEntry;
import nz.co.redice.azansilenttime.repo.remote.AzanService;

public class Repository {

    private static final String TAG = "App Repo";
    private final EventDao mDao;
    private AzanService mAzanService;

    @Inject
    public Repository(AzanService newsService, EventDao dao) {
        mAzanService = newsService;
        mDao = dao;

    }

    public AzanService getAzanService() {
        return mAzanService;
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

    @SuppressLint("CheckResult")
    public void insertFridayEntry(FridayEntry fridayEntry) {
        Observable.just(fridayEntry)
                .subscribeOn(Schedulers.io())
                .subscribe(mDao::insertFridayEntry);
    }

    public LiveData<RegularEntry> getSelectedRegularLiveData(Long value) {
        return mDao.getSelectedRegularLiveData(value);
    }

    public LiveData<FridayEntry> getSelectedFridayLiveData(Long value) {
        return mDao.getSelectedFridayLiveData(value);
    }

    public Observable<List<RegularEntry>> selectTwoDaysForAlarmSetting(Long startDate, Long endDate) {
        return mDao.getTwoDaysForAlarmSetting(startDate, endDate);
    }

    public Observable<List<FridayEntry>> selectTwoFridaysForAlarmSetting(Long startDate, Long endDate) {
        return mDao.getTwoFridaysForAlarmSetting(startDate, endDate);
    }

}