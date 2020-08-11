package nz.co.redice.demoservice.repo.local;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import io.reactivex.Completable;
import nz.co.redice.demoservice.repo.local.entity.EntryModel;
import nz.co.redice.demoservice.repo.local.entity.FridayEntry;

import static androidx.room.OnConflictStrategy.REPLACE;


@Dao
public interface EventDao {

    @Insert(onConflict = REPLACE)
    void insertEntry(EntryModel entryModel);

    @Update(onConflict = REPLACE)
    Completable updateEntry(EntryModel entryModel);

    @Insert(onConflict = REPLACE)
    void insertFridayEntry(FridayEntry fridayEntry);

    @Update(onConflict = REPLACE)
    void updateFridayEntry(FridayEntry fridayEntry);

    @Query("SELECT * FROM friday_table WHERE date = :selectedDate ")
    LiveData<FridayEntry> getSelectedFridayEntry(Long selectedDate);


    @Query("SELECT * FROM regular_table WHERE date = :selectedDate ")
    LiveData<EntryModel> getSelectedEntry(Long selectedDate);

    @Query("SELECT * FROM regular_table WHERE date = :selectedDate ")
    EntryModel getSelectedEntrySynchronously(Long selectedDate);

    @Query("SELECT COUNT(date) FROM regular_table")
    LiveData<Integer> getRowCount();

    @Query("SELECT COUNT(date) FROM friday_table")
    LiveData<Integer> getFridaysCount();

    @Query("DELETE FROM regular_table")
    public void deleteCalendar();
}
