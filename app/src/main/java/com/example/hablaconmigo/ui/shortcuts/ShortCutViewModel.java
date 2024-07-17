package com.example.hablaconmigo.ui.shortcuts;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.hablaconmigo.R;

import java.util.ArrayList;
import java.util.List;

public class ShortCutViewModel extends ViewModel {

    private final MutableLiveData<List<AtajosAdapter.Carpeta>> carpetasLiveData;

    public ShortCutViewModel() {
        carpetasLiveData = new MutableLiveData<>();
        cargarDatosIniciales();
    }

    public MutableLiveData<List<AtajosAdapter.Carpeta>> getCarpetasLiveData() {
        return carpetasLiveData;
    }

    private void cargarDatosIniciales() {
        List<AtajosAdapter.Carpeta> listaCarpetas = new ArrayList<>();
        listaCarpetas.add(new AtajosAdapter.Carpeta("Comidas", R.drawable.ic_comida));
        listaCarpetas.add(new AtajosAdapter.Carpeta("Ubicaciones", R.drawable.ic_ubicaciones));
        listaCarpetas.add(new AtajosAdapter.Carpeta("Acciones", R.drawable.ic_acciones));
        listaCarpetas.add(new AtajosAdapter.Carpeta("Estados", R.drawable.ic_estados));
        listaCarpetas.add(new AtajosAdapter.Carpeta("Mis Datos", R.drawable.ic_mis_datos));
        carpetasLiveData.setValue(listaCarpetas);
    }

    public void agregarCarpeta(AtajosAdapter.Carpeta carpeta) {
        List<AtajosAdapter.Carpeta> listaActual = carpetasLiveData.getValue();
        if (listaActual != null) {
            listaActual.add(carpeta);
            carpetasLiveData.setValue(listaActual);
        }
    }
}
