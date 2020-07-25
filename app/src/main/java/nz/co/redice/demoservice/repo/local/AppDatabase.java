package nz.co.redice.demoservice.repo.local;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import nz.co.redice.demoservice.repo.local.models.Azan;
import nz.co.redice.demoservice.utils.Converters;


@Database(entities = {Azan.class}, version = 1, exportSchema = false)
//@TypeConverters(Converters.class)
public abstract class AppDatabase extends RoomDatabase {
    public abstract EventDao getDao();
}