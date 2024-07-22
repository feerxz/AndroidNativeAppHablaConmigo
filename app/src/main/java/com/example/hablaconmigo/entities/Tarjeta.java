package com.example.hablaconmigo.entities;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "tarjetas")
public class Tarjeta {

    @PrimaryKey(autoGenerate = true)
    private long id;

    @NonNull
    @ColumnInfo(name = "nombre_tarjeta")
    private String  nombreTarjeta;

    @NonNull
    @ColumnInfo(name = "frase_tarjeta")
    private String fraseTarjeta;

    @Nullable
    @ColumnInfo(name = "imagen")
    private String imagen;

    @Nullable
    @ColumnInfo(name = "ruta_audio")
    private String rutaAudio;
    @ColumnInfo(name = "usuario_id")
    private long usuarioId;

    @ColumnInfo(name = "carpeta_id")
    private long carpetaId;

    public Tarjeta(@NonNull String nombreTarjeta, @NonNull String fraseTarjeta, @Nullable String imagen, @Nullable String rutaAudio, long usuarioId, long carpetaId) {
        this.nombreTarjeta = nombreTarjeta;
        this.fraseTarjeta = fraseTarjeta;
        this.imagen = imagen;
        this.rutaAudio = rutaAudio;
        this.usuarioId = usuarioId;
        this.carpetaId = carpetaId;
    }

    //Getters and Setters

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @NonNull
    public String getNombreTarjeta() {
        return nombreTarjeta;
    }

    public void setNombreTarjeta(@NonNull String nombreTarjeta) {
        this.nombreTarjeta = nombreTarjeta;
    }

    @NonNull
    public String getFraseTarjeta() {
        return fraseTarjeta;
    }

    public void setFraseTarjeta(@NonNull String fraseTarjeta) {
        this.fraseTarjeta = fraseTarjeta;
    }

    @Nullable
    public String getImagen() {
        return imagen;
    }

    public void setImagen(@Nullable String imagen) {
        this.imagen = imagen;
    }

    @Nullable
    public String getRutaAudio() {
        return rutaAudio;
    }

    public void setRutaAudio(@Nullable String rutaAudio) {
        this.rutaAudio = rutaAudio;
    }

    public long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public long getCarpetaId() {
        return carpetaId;
    }

    public void setCarpetaId(long carpetaId) {
        this.carpetaId = carpetaId;
    }

}
