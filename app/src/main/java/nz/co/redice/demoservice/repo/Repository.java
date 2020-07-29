package nz.co.redice.demoservice.repo;

import androidx.lifecycle.LiveData;

import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import nz.co.redice.demoservice.repo.local.EventDao;
import nz.co.redice.demoservice.repo.local.models.Azan;
import nz.co.redice.demoservice.repo.remote.AzanService;
import nz.co.redice.demoservice.utils.PreferencesHelper;

public class Repository {

    private static final String TAG = "Repo";
    private static final int MUSLIM_WORLD_LEAGUE_METHOD = 3;
    private final AzanService mAzanService;
    private final EventDao mDao;
    @Inject PreferencesHelper mPreferencesHelper;

    @Inject
    public Repository(AzanService newsService, EventDao dao) {
        mAzanService = newsService;
        mDao = dao;
    }

    public void requestAnnualCalendar(Double latitude, Double longitude) {
        mAzanService.requestAnnualTimeTable(latitude, longitude,
                Calendar.getInstance().get(Calendar.YEAR), false,
                MUSLIM_WORLD_LEAGUE_METHOD)
                .subscribeOn(Schedulers.io())
                .toObservable()
                .flatMap(s -> Observable.fromIterable(s.data))
                .map(s -> {
                    mPreferencesHelper.setLocalTimeZone(s.meta.timezone);
                    return new Azan(
                            s.date.gregorian.date,
                            mPreferencesHelper.getTimeZone(),
                            s.timings.fajr,
                            s.timings.dhuhr,
                            s.timings.asr,
                            s.timings.maghrib,
                            s.timings.isha);

                })
                .subscribe(s -> mDao.insertEntry(s),
                        error -> System.err.println("" + error.getStackTrace()));
    }

    public LiveData<List<Azan>> getAnnualTables() {
        return mDao.getAnnualCalendar();
    }

    public LiveData<Azan> getTimesForSelectedDate(Long value) {
        return mDao.getAzanTimesForDate(value);
    }

}
