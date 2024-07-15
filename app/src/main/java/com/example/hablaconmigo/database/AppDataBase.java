package com.example.hablaconmigo.database;


import androidx.room.Database;

import androidx.room.RoomDatabase;

import com.example.hablaconmigo.dao.DaoCarpetas;
import com.example.hablaconmigo.dao.DaoTarjetas;
import com.example.hablaconmigo.dao.DaoUsuarios;
import com.example.hablaconmigo.entities.Carpeta;
import com.example.hablaconmigo.entities.Tarjeta;
import com.example.hablaconmigo.entities.Usuario;

@Database(entities = {Usuario.class, Tarjeta.class, Carpeta.class}, version = 1)
public abstract class AppDataBase extends RoomDatabase {
    public abstract DaoUsuarios daoUsuarios();
    public abstract DaoTarjetas daoTarjetas();
    public abstract DaoCarpetas daoCarpetas();

}
