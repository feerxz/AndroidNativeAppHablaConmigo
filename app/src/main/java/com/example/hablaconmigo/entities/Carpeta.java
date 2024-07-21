package com.example.hablaconmigo.entities;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;



@Entity(tableName = "carpetas")
public class Carpeta {

    @PrimaryKey(autoGenerate = true)
    private long id;

    @NonNull
    @ColumnInfo(name = "nombre_carpeta")
    private String nombreCarpeta;

    @Nullable
    @ColumnInfo(name = "imagen")
    private String imagen;

    @ColumnInfo(name = "usuario_id")
    private long usuarioId;

    public Carpeta(@NonNull String nombreCarpeta, @Nullable String imagen, long usuarioId) {
        this.nombreCarpeta = nombreCarpeta;
        this.imagen = imagen;
        this.usuarioId = usuarioId;
    }

    //Getters and Setters

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @NonNull
    public String getNombreCarpeta() {
        return nombreCarpeta;
    }

    public void setNombreCarpeta(@NonNull String nombreCarpeta) {
        this.nombreCarpeta = nombreCarpeta;
    }

    @Nullable
    public String getImagen() {
        return imagen;
    }

    public void setImagen(@Nullable String imagen) {
        this.imagen = imagen;
    }

    public long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(long usuarioId) {
        this.usuarioId = usuarioId;
    }

}
