package nz.co.redice.azansilenttime.repo.local;


import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;
import nz.co.redice.azansilenttime.repo.local.entity.AlarmSchedule;
import nz.co.redice.azansilenttime.repo.local.entity.AzanTimings;

import static androidx.room.OnConflictStrategy.REPLACE;


@Dao
public interface EventDao {

    //Timings

    @Insert(onConflict = REPLACE)
    void insertAzanTimings(AzanTimings azanTimings);

    @Query("SELECT * FROM AzanTimings WHERE date = :selectedDate ")
    AzanTimings getAzanTimingOnSelectedDay(Long selectedDate);

    @Query("DELETE FROM AzanTimings")
    void deleteCalendar();

    //Alarms

    @Insert(onConflict = REPLACE)
    void insertAlarmSchedule(AlarmSchedule alarmSchedule);

    @Update(onConflict = REPLACE)
    Completable updateAlarmSchedule(AlarmSchedule alarmSchedule);

    @Query("SELECT * FROM AlarmSchedule")
    AlarmSchedule getAlarmSchedule();

}