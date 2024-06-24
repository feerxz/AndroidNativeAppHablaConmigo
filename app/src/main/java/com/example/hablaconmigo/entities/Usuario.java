package com.example.hablaconmigo.entities;
import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
@Entity(tableName = "usuarios", indices = {@Index(value = {"correo_electronico"}, unique = true)})

public class Usuario {
    @PrimaryKey(autoGenerate = true)
    private long id;
    @NonNull
    @ColumnInfo(name = "nombre")
    private String nombre;

    @NonNull
    @ColumnInfo(name = "apellido")
    private String apellido;

    @NonNull
    @ColumnInfo(name = "correo_electronico")
    private String correoElectronico;

    @Nullable
    @ColumnInfo(name = "contrasena")
    private String contrasena;

    @Nullable
    @ColumnInfo(name = "uidFirebase")
    private String uidFirebase;

    @ColumnInfo(name = "edad")
    private int edad;

    @NonNull
    @ColumnInfo(name = "genero")
    private String genero;

    @NonNull
    @ColumnInfo(name = "direccion")
    private String direccion;

    @ColumnInfo(name = "profile_completed")
    private boolean profileCompleted;

    public Usuario(@NonNull String nombre, @NonNull String apellido, @NonNull String correoElectronico, @Nullable String contrasena,
                    @Nullable String uidFirebase,
                   int edad, @NonNull String genero, @NonNull String direccion, boolean profileCompleted) {

        this.nombre = nombre;
        this.apellido = apellido;
        this.correoElectronico = correoElectronico;
        this.contrasena = contrasena;
        this.edad = edad;
        this.genero = genero;
        this.direccion = direccion;
        this.uidFirebase = uidFirebase;
        this.profileCompleted = profileCompleted;
    }

    //Getters and Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @NonNull
    public String getNombre() {
        return nombre;
    }

    public void setNombre(@NonNull String nombre) {
        this.nombre = nombre;
    }

    @NonNull
    public String getApellido() {
        return apellido;
    }

    public void setApellido(@NonNull String apellido) {
        this.apellido = apellido;
    }

    @NonNull
    public String getCorreoElectronico() {
        return correoElectronico;
    }

    public void setCorreoElectronico(@NonNull String correoElectronico) {
        this.correoElectronico = correoElectronico;
    }

    @Nullable
    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(@NonNull String contrasena) {
        this.contrasena = contrasena;
    }
    @Nullable
    public String getUidFirebase() {
        return uidFirebase;
    }

    public void setUidFirebase(@Nullable String uidFirebase) {
        this.uidFirebase = uidFirebase;
    }


    public int getEdad() {
        return edad;
    }

    public void setEdad( int edad) {
        this.edad = edad;
    }

    @NonNull
    public String getGenero() {
        return genero;
    }

    public void setGenero(@NonNull String genero) {
        this.genero = genero;
    }

    @NonNull
    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(@NonNull String direccion) {
        this.direccion = direccion;
    }

    public boolean getProfileCompleted() {
        return profileCompleted;
    }

    public void setProfileCompleted(boolean profileCompleted) {
        this.profileCompleted = profileCompleted;
    }
}
