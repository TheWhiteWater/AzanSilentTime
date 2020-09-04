package nz.co.redice.azansilenttime.repo.local;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;
import nz.co.redice.azansilenttime.repo.local.entity.FridaySchedule;
import nz.co.redice.azansilenttime.repo.local.entity.RegularSchedule;

import static androidx.room.OnConflictStrategy.REPLACE;


@Dao
public interface EventDao {

    @Insert(onConflict = REPLACE)
    void insertEntry(RegularSchedule regularSchedule);

    @Update(onConflict = REPLACE)
    Completable updateEntry(RegularSchedule regularSchedule);

    @Insert(onConflict = REPLACE)
    void insertFridayEntry(FridaySchedule fridaySchedule);

    @Update(onConflict = REPLACE)
    void updateFridayEntry(FridaySchedule fridaySchedule);

    @Query("SELECT * FROM friday_table WHERE date = :selectedDate ")
    FridaySchedule getSelectedFridayEntry(Long selectedDate);

    @Query("SELECT * FROM regular_table WHERE date = :selectedDate ")
    RegularSchedule getSelectedRegularEntry(Long selectedDate);

    @Query("DELETE FROM regular_table")
    void deleteCalendar();

    @Query("SELECT * FROM regular_table WHERE date BETWEEN :startDate AND :endDate" )
    io.reactivex.Observable<List<RegularSchedule>> getTwoDaysForAlarmSetting(Long startDate, Long endDate);


    @Query("SELECT * FROM friday_table WHERE date BETWEEN :startDate AND :endDate" )
    Observable<List<FridaySchedule>> getTwoFridaysForAlarmSetting(Long startDate, Long endDate);





}
