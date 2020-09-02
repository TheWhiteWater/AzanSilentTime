package nz.co.redice.azansilenttime.repo.local;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import nz.co.redice.azansilenttime.repo.local.entity.RegularSchedule;
import nz.co.redice.azansilenttime.repo.local.entity.FridaySchedule;


@Database(entities = {RegularSchedule.class, FridaySchedule.class}, version = 2, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract EventDao getDao();
}