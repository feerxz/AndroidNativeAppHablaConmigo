package com.example.hablaconmigo.ui.shortcuts;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.hablaconmigo.database.AppDataBase;
import com.example.hablaconmigo.entities.Carpeta;
import com.example.hablaconmigo.R;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

public class ShortCutViewModel extends AndroidViewModel {

    private final MutableLiveData<List<Carpeta>> carpetasLiveData;
    private final AppDataBase database;

    public ShortCutViewModel(@NonNull Application application) {
        super(application);
        database = AppDataBase.getDatabase(application);
        carpetasLiveData = new MutableLiveData<>();
        cargarDatosIniciales();
    }

    public MutableLiveData<List<Carpeta>> getCarpetasLiveData() {
        return carpetasLiveData;
    }

    private void cargarDatosIniciales() {
        // Cargar las carpetas de la base de datos en un hilo separado
        new Thread(() -> {
            List<Carpeta> listaCarpetas = database.daoCarpetas().obtenerTodasLasCarpetas();
            if (listaCarpetas.isEmpty()) {
                // Si no hay carpetas, agregar las carpetas por defecto
                agregarCarpeta(new Carpeta("Ubicaciones", "android.resource://" + getApplication().getPackageName() + "/" + R.drawable.ic_ubicaciones, 0));
                agregarCarpeta(new Carpeta("Acciones", "android.resource://" + getApplication().getPackageName() + "/" + R.drawable.ic_acciones, 0));
                agregarCarpeta(new Carpeta("Estados", "android.resource://" + getApplication().getPackageName() + "/" + R.drawable.ic_estados, 0));
                agregarCarpeta(new Carpeta("Mis Datos", "android.resource://" + getApplication().getPackageName() + "/" + R.drawable.ic_mis_datos, 0));
                listaCarpetas = database.daoCarpetas().obtenerTodasLasCarpetas();
            }
            carpetasLiveData.postValue(listaCarpetas);
        }).start();
    }

    private Uri getUriForResource(int resourceId) {
        return Uri.parse("android.resource://" + getApplication().getPackageName() + "/" + resourceId);
    }

    public void agregarCarpeta(Carpeta carpeta) {
        new Thread(() -> {
            database.daoCarpetas().insertarCarpeta(carpeta);
            // Actualizar la lista después de agregar
            List<Carpeta> listaCarpetas = database.daoCarpetas().obtenerTodasLasCarpetas();
            carpetasLiveData.postValue(listaCarpetas);
        }).start();
    }

    public void eliminarCarpeta(Carpeta carpeta) {
        new Thread(() -> {
            database.daoCarpetas().eliminarCarpeta(carpeta);
            // Actualizar la lista después de eliminar
            List<Carpeta> listaCarpetas = database.daoCarpetas().obtenerTodasLasCarpetas();
            carpetasLiveData.postValue(listaCarpetas);
        }).start();
    }
}
