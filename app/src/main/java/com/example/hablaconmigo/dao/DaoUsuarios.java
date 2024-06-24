package com.example.hablaconmigo.dao;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.hablaconmigo.entities.Usuario;

import java.util.List;

@Dao
public interface DaoUsuarios {
    @Query("SELECT * FROM usuarios")
    List<Usuario> getAllUsers();
    @Query("SELECT * FROM usuarios WHERE correo_electronico = :correoElectronico AND contrasena = :contrasena")
    Usuario login(String correoElectronico, String contrasena);
    @Query("SELECT * FROM usuarios WHERE correo_electronico = :correoElectronico")
    Usuario findUserByEmail(String correoElectronico);
    @Query("SELECT * FROM usuarios WHERE id = :id")
    Usuario findUserById(long id);

    @Query("SELECT * FROM usuarios WHERE uidFirebase = :uidFirebase")
    Usuario findUserByUid(String uidFirebase);
    @Insert
    long insert(Usuario usuario);
    @Query("DELETE FROM usuarios WHERE id = :id")
    void delete(int id);
    @Update
    void update(Usuario usuario);

}
