package nz.co.redice.azansilenttime.repo.local;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import io.reactivex.Completable;
import io.reactivex.Observable;
import nz.co.redice.azansilenttime.repo.local.entity.FridayEntry;
import nz.co.redice.azansilenttime.repo.local.entity.RegularEntry;

import static androidx.room.OnConflictStrategy.REPLACE;


@Dao
public interface EventDao {

    @Query("SELECT COUNT(date) FROM regular_table")
    LiveData <Integer> getRegularTableRowCount();

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

    @Query("DELETE FROM regular_table")
    void deleteCalendar();

    @Query("SELECT COUNT(date) FROM friday_table")
    LiveData <Integer> getFridaysRowCount();


    @Query("DELETE FROM friday_table")
    void deleteAllFridayTable();
}
