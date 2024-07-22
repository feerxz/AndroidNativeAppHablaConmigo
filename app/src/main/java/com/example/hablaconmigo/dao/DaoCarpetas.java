package com.example.hablaconmigo.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.hablaconmigo.entities.Carpeta;

import java.util.List;

@Dao
public interface DaoCarpetas {
    //ICREAR UNA NUEVA CARPETA
    @Insert
    long insert(Carpeta carpeta);

    //ACTUALIZAR UNA CARPETA
    @Insert
    void update(Carpeta carpeta);

    //SELECCIONAR UNA CARPETA POR ID
    @Query("SELECT * FROM carpetas WHERE id = :id")
    Carpeta findFolderById(long id);

    //SELECCIONAR TODAS LAS CARPETAS DE UN USUARIO
    @Query("SELECT * FROM carpetas WHERE usuario_id = :usuarioId")
    LiveData<List<Carpeta>> findFoldersByUserId(long usuarioId);

    //ELIMINAR UNA CARPETA
    @Query("DELETE FROM carpetas WHERE id = :id")
    void delete(long id);




}
