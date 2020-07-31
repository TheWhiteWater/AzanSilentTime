package nz.co.redice.demoservice.repo.local;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import io.reactivex.Completable;
import nz.co.redice.demoservice.repo.local.entity.EntryModel;


@Dao
public interface EventDao {


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertEntry(EntryModel entryModel);

    @Query("SELECT * FROM data_table WHERE date = :selectedDate ")
    LiveData<EntryModel> getAzanTimesForDate(Long selectedDate);


    @Query("SELECT COUNT(date) FROM data_table")
    LiveData<Integer> getRowCount();

}
