package com.example.hablaconmigo;
import android.app.Application;
import android.util.Log;

import androidx.room.Room;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.cybozu.labs.langdetect.DetectorFactory;
import com.example.hablaconmigo.database.AppDataBase;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MyApplication extends Application {
    public static AppDataBase dataBase;

    @Override
    public void onCreate() {
        super.onCreate();
        dataBase = Room.databaseBuilder(this, AppDataBase.class,"dbHC").build();
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    // Se obtiene una lista de todos los archivos de perfil en la carpeta 'assets/profiles'
                    String[] profiles = getAssets().list("profiles");

                    if (profiles != null) {
                        // Se crea una lista para almacenar los perfiles
                        List<String> jsonProfiles = new ArrayList<>();

                        // Para cada archivo de perfil, lee el contenido del archivo y se lo añade a la lista
                        for (String profile : profiles) {
                            InputStream is = getAssets().open("profiles/" + profile);
                            int size = is.available();
                            byte[] buffer = new byte[size];
                            is.read(buffer);
                            is.close();
                            String jsonProfile = new String(buffer, "UTF-8");
                            jsonProfiles.add(jsonProfile);
                        }

                        // Se carga los perfiles en DetectorFactory
                        DetectorFactory.loadProfile(jsonProfiles);
                    }
                } catch (Exception e) {
                    Log.e("MyApplication", "Error al cargar los perfiles de idioma", e);
                }
            }
        });
    }

    /*static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE tarjetas_new (id INTEGER PRIMARY KEY NOT NULL, nombre_tarjeta TEXT NOT NULL, frase_tarjeta TEXT NOT NULL, imagen TEXT, usuario_id INTEGER NOT NULL, carpeta_id INTEGER NOT NULL)");
            database.execSQL("DROP TABLE tarjetas");
            database.execSQL("ALTER TABLE tarjetas_new RENAME TO tarjetas");
        }
    };*/

}
