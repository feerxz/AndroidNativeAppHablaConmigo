package com.example.hablaconmigo;
import android.app.Application;

import androidx.room.Room;

import com.example.hablaconmigo.database.AppDataBase;

public class MyApplication extends Application {
    public static AppDataBase dataBase;

    @Override
    public void onCreate() {
        super.onCreate();
        dataBase = Room.databaseBuilder(this, AppDataBase.class,"dbHC")
                .build();
    }

}
