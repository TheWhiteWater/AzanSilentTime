package nz.co.redice.demoservice.repo.local;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import nz.co.redice.demoservice.repo.local.models.Azan;


@Database(entities = {Azan.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract EventDao getDao();
}