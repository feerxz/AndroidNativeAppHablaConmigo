package com.example.hablaconmigo.dao;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.hablaconmigo.entities.Tarjeta;

import java.util.List;

@Dao
public interface DaoTarjetas {
    //INSERTAR UNA NUEVA TARJETA
    @Insert
    void insert(Tarjeta tarjeta);

    //ACTUALIZAR UNA TARJETA
    @Update
    void update(Tarjeta tarjeta);

    //SELECCIONAR UNA TARJETA POR ID
    @Query("SELECT * FROM tarjetas WHERE id = :id")
    Tarjeta findCardById(long id);

    //SELECCIONAR TODAS LAS TARJETAS DE UNA CARPETA
    @Query("SELECT * FROM tarjetas WHERE carpeta_id = :carpetaId")
    LiveData<List<Tarjeta>> findCardsByFolderId(long carpetaId);

    //ELIMINAR UNA TARJETA
    @Query("DELETE FROM tarjetas WHERE id = :id")
    void delete(long id);
}
