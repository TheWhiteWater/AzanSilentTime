package nz.co.redice.demoservice.repo.local;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import nz.co.redice.demoservice.repo.local.entity.EntryModel;

import static androidx.room.OnConflictStrategy.REPLACE;


@Dao
public interface EventDao {


    @Insert(onConflict = REPLACE)
    void insertEntry(EntryModel entryModel);

    @Query("SELECT * FROM data_table WHERE date = :selectedDate ")
    LiveData<EntryModel> getSelectedEntry(Long selectedDate);

    @Query("SELECT * FROM data_table WHERE date = :selectedDate ")
    EntryModel getSelectedEntrySynchronously(Long selectedDate);

    @Query("SELECT COUNT(date) FROM data_table")
    LiveData<Integer> getRowCount();

    @Update(onConflict = REPLACE)
    Completable updateEntry(EntryModel entryModel);

}
