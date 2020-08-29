package nz.co.redice.azansilenttime.repo.local;


import androidx.databinding.ObservableList;
import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import nz.co.redice.azansilenttime.repo.local.entity.FridayEntry;
import nz.co.redice.azansilenttime.repo.local.entity.RegularEntry;

import static androidx.room.OnConflictStrategy.REPLACE;


@Dao
public interface EventDao {

    @Insert(onConflict = REPLACE)
    void insertEntry(RegularEntry regularEntry);

    @Update(onConflict = REPLACE)
    Completable updateEntry(RegularEntry regularEntry);

    @Insert(onConflict = REPLACE)
    void insertFridayEntry(FridayEntry fridayEntry);

    @Update(onConflict = REPLACE)
    void updateFridayEntry(FridayEntry fridayEntry);

    @Query("SELECT * FROM friday_table WHERE date = :selectedDate ")
    FridayEntry getSelectedFridayEntry(Long selectedDate);

    @Query("SELECT * FROM regular_table WHERE date = :selectedDate ")
    RegularEntry getSelectedRegularEntry(Long selectedDate);

    @Query("SELECT * FROM regular_table WHERE date = :selectedDate ")
    LiveData<RegularEntry> getSelectedRegularLiveData(Long selectedDate);

    @Query("SELECT * FROM friday_table WHERE date = :selectedDate ")
    LiveData<FridayEntry> getSelectedFridayLiveData(Long selectedDate);

    @Query("DELETE FROM regular_table")
    void deleteCalendar();

    @Query("SELECT * FROM regular_table WHERE date BETWEEN :startDate AND :endDate" )
    Observable<List<RegularEntry>> getTwoDaysForAlarmSetting(Long startDate, Long endDate);





}
