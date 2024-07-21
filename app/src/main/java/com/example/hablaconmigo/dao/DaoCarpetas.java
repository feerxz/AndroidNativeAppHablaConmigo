package com.example.hablaconmigo.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.hablaconmigo.entities.Carpeta;

import java.util.List;
@Dao
public interface DaoCarpetas {
    @Insert
    void insertarCarpeta(Carpeta carpeta);

    @Query("SELECT * FROM carpetas")
    List<Carpeta> obtenerTodasLasCarpetas();

    @Delete
    void eliminarCarpeta(Carpeta carpeta);

}
