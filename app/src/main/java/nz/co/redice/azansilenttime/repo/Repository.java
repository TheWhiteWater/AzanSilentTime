package nz.co.redice.azansilenttime.repo;

import android.annotation.SuppressLint;
import android.util.Log;

import androidx.lifecycle.LiveData;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import nz.co.redice.azansilenttime.repo.local.EventDao;
import nz.co.redice.azansilenttime.repo.local.entity.FridayEntry;
import nz.co.redice.azansilenttime.repo.local.entity.RegularEntry;
import nz.co.redice.azansilenttime.repo.remote.AzanService;
import nz.co.redice.azansilenttime.utils.PrefHelper;

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