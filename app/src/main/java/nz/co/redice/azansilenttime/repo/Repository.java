package nz.co.redice.azansilenttime.repo;

import android.annotation.SuppressLint;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import nz.co.redice.azansilenttime.repo.local.EventDao;
import nz.co.redice.azansilenttime.repo.local.entity.AlarmSchedule;
import nz.co.redice.azansilenttime.repo.local.entity.AzanTimings;
import nz.co.redice.azansilenttime.repo.remote.AzanService;

public class Repository {

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

    public void insertRegularEntry(AzanTimings entry) {
        mDao.insertAzanTimings(entry);
    }

    public void deletePrayerCalendar() {
        Completable.fromAction(mDao::deleteCalendar)
                .subscribeOn(Schedulers.io())
                .subscribe();
    }

    public AzanTimings getRegularEntry(Long value) {
        return mDao.getAzanTimingOnSelectedDay(value);
    }

    @SuppressLint("CheckResult")
    public void updateAlarmSchedule(AlarmSchedule schedule) {
        Observable.just(schedule)
                .subscribeOn(Schedulers.computation())
                .subscribe(s -> mDao.updateAlarmSchedule(schedule));
    }


    public AlarmSchedule getAlarmSchedule() {
        return mDao.getAlarmSchedule();
    }


    @SuppressLint("CheckResult")
    public void insertAlarmSchedule(AlarmSchedule alarmSchedule) {
        Observable.just(alarmSchedule)
                .subscribeOn(Schedulers.io())
                .subscribe(mDao::insertAlarmSchedule);
    }

}