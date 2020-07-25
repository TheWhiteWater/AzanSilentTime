package nz.co.redice.demoservice.repo;

import androidx.lifecycle.LiveData;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import nz.co.redice.demoservice.repo.local.EventDao;
import nz.co.redice.demoservice.repo.local.models.Azan;
import nz.co.redice.demoservice.repo.remote.AzanService;

public class Repository {

    private static final String TAG = "Repo";

    private final AzanService mAzanService;
    private final EventDao mDao;
    private static final int MUSLIM_WORLD_LEAGUE_METHOD = 3;

    @Inject
    public Repository(AzanService newsService, EventDao dao) {
        mAzanService = newsService;
        mDao = dao;
    }

    public void requestAnnualCalendar(Double latitude, Double longitude) {
        mAzanService.requestAnnualTimeTable(latitude, longitude,
                Calendar.getInstance().get(Calendar.YEAR), true,
                MUSLIM_WORLD_LEAGUE_METHOD)
                .subscribeOn(Schedulers.io())
                .toObservable()
                .flatMap(s -> Observable.fromIterable(s.data.entries))
                .map(s -> new Azan(
                        s.date.gregorian.date,
                        s.timings.fajr,
                        s.timings.dhuhr,
                        s.timings.asr,
                        s.timings.maghrib,
                        s.timings.isha))
                .subscribe(s -> mDao.insertEntry(s),
                        error -> System.err.println("" + error.getMessage()));

    }

//    public LiveData<List<Azan>> getTimeTableFor(Date day) {
//        Date startDay = day;
//        startDay.setHours(0);
//        startDay.setMinutes(1);
//        Date finishDay = day;
//        finishDay.setHours(24);
//        finishDay.setMinutes(00);
//        return mDao.findAzanTimesBetweenDates(startDay.getTime(), finishDay.getTime());
//    }
//
//    public LiveData<List<Azan>> getTimeTableForCurrentDay() {
//        Date startDay = Calendar.getInstance().getTime();
//        startDay.setHours(0);
//        startDay.setMinutes(1);
//        Date finishDay = Calendar.getInstance().getTime();
//        finishDay.setHours(24);
//        finishDay.setMinutes(00);
//        return mDao.findAzanTimesBetweenDates(startDay.getTime(), finishDay.getTime());
//    }
//
//    public LiveData<List<Azan>> getAnnualTables() {
//        return mDao.getAnnualCalendar();
//    }

}
