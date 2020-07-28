package nz.co.redice.demoservice.repo.local;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import nz.co.redice.demoservice.repo.local.models.Azan;


@Dao
public interface EventDao {

    @Query("SELECT * FROM data_table")
    LiveData<List<Azan>> getAnnualCalendar ();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertEntry(Azan entry);

    @Query("SELECT * FROM data_table WHERE date = :selectedDate ")
    LiveData<Azan> getAzanTimesForDate(Long selectedDate);

}
