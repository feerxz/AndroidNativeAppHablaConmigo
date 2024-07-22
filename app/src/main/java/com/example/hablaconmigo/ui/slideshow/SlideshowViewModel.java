package com.example.hablaconmigo.ui.slideshow;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.hablaconmigo.MyApplication;
import com.example.hablaconmigo.dao.DaoCarpetas;
import com.example.hablaconmigo.dao.DaoTarjetas;
import com.example.hablaconmigo.database.AppDataBase;
import com.example.hablaconmigo.entities.Carpeta;
import com.example.hablaconmigo.entities.Tarjeta;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SlideshowViewModel extends ViewModel {
    private final DaoCarpetas daoCarpetas;
    private final DaoTarjetas daoTarjetas;
    private final ExecutorService executorService;

    public SlideshowViewModel() {
        AppDataBase appDataBase = MyApplication.dataBase;
        daoCarpetas = appDataBase.daoCarpetas();
        daoTarjetas = appDataBase.daoTarjetas();
        executorService = Executors.newSingleThreadExecutor();
    }

    public LiveData<List<Carpeta>> getCarpetasByUserId(long userId) {
        return daoCarpetas.findFoldersByUserId(userId);
    }

    public LiveData<List<Tarjeta>> getTarjetasByFolderId(long folderId) {
        return daoTarjetas.findCardsByFolderId(folderId);
    }

    public void deleteTarjeta(long tarjetaId) {
        executorService.execute(() -> daoTarjetas.delete(tarjetaId));
    }

    public void createTarjeta(Tarjeta tarjeta) {
        executorService.execute(() -> daoTarjetas.insert(tarjeta));
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        executorService.shutdown();
    }


}