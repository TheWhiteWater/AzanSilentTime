package nz.co.redice.azansilenttime.repo.local;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import nz.co.redice.azansilenttime.repo.local.entity.RegularSchedule;
import nz.co.redice.azansilenttime.repo.local.entity.FridayEntry;


@Database(entities = {RegularSchedule.class, FridayEntry.class}, version = 2, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract EventDao getDao();
}