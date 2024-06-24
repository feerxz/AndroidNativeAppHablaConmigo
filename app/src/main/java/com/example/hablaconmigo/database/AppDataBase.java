package com.example.hablaconmigo.database;
import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import com.example.hablaconmigo.dao.DaoUsuarios;
import com.example.hablaconmigo.entities.Usuario;

@Database(entities = {Usuario.class}, version = 1)
public abstract class AppDataBase extends RoomDatabase {
    public abstract DaoUsuarios daoUsuarios();

}
