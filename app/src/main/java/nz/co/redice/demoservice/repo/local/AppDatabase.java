package nz.co.redice.demoservice.repo.local;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import nz.co.redice.demoservice.repo.local.entity.EntryModel;


@Database(entities = {EntryModel.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract EventDao getDao();
}