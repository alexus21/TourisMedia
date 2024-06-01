package ues.alexus21.travelingapp;

import android.content.Context;

import androidx.room.Room;

import ues.alexus21.travelingapp.localstorage.AppDatabase;

public class DatabaseSingleton {
    private static AppDatabase appDatabase;

    public static AppDatabase getDatabase(Context context) {
        if (appDatabase == null) {
            appDatabase = Room.databaseBuilder(
                    context.getApplicationContext(),
                    AppDatabase.class,
                    "localstorage"
            ).allowMainThreadQueries().build();
        }
        return appDatabase;
    }
}