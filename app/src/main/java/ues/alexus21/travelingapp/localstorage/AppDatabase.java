package ues.alexus21.travelingapp.localstorage;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {LocalUserModel.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract ILocalUserDAO localUserDAO();
}
