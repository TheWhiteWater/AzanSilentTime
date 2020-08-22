package nz.co.redice.azansilenttime.repo.local;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import io.reactivex.Completable;
import nz.co.redice.azansilenttime.repo.local.entity.RegularEntry;
import nz.co.redice.azansilenttime.repo.local.entity.FridayEntry;

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
    LiveData<FridayEntry> getSelectedFridayEntry(Long selectedDate);


    @Query("SELECT * FROM regular_table WHERE date = :selectedDate ")
    LiveData<RegularEntry> getSelectedEntry(Long selectedDate);

    @Query("SELECT * FROM regular_table WHERE date = :selectedDate ")
    RegularEntry getSelectedEntrySynchronously(Long selectedDate);

    @Query("SELECT COUNT(date) FROM regular_table")
    LiveData<Integer> getRowCount();

    @Query("SELECT COUNT(date) FROM friday_table")
    LiveData<Integer> getFridaysCount();

    @Query("DELETE FROM regular_table")
    public void deleteCalendar();

    @Query("DELETE FROM friday_table")
    public void deleteAllFridayTable();
}
