package nz.co.redice.azansilenttime.repo.local;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import nz.co.redice.azansilenttime.repo.local.entity.AlarmSchedule;
import nz.co.redice.azansilenttime.repo.local.entity.AzanTimings;


@Database(entities = {AzanTimings.class, AlarmSchedule.class}, version = 2, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract EventDao getDao();
}