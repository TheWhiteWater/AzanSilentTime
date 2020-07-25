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

//    @Query("SELECT * FROM Azan")
//    LiveData<List<Azan>> getAnnualCalendar ();

//    @Query("SELECT * FROM CalendarDay WHERE eventId = :eventId")
//    CalendarDay getEventById (int eventId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertEntry(Azan entry);

//    @Query("SELECT * FROM Azan WHERE mDate BETWEEN :from AND :to")
//    LiveData<List<Azan>> findAzanTimesBetweenDates(Long from, Long to);

}
