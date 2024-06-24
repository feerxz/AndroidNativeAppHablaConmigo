package com.example.hablaconmigo;
import android.app.Application;

import androidx.room.Room;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;
import com.example.hablaconmigo.database.AppDataBase;

public class MyApplication extends Application {
    public static AppDataBase dataBase;

    @Override
    public void onCreate() {
        super.onCreate();
        dataBase = Room.databaseBuilder(this, AppDataBase.class,"dbHC")
                .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                .build();
    }

    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE usuarios ADD COLUMN profile_completed INTEGER NOT NULL DEFAULT 0");
        }
    };

    static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            // Crear una nueva tabla con la restricción de unicidad
            database.execSQL("CREATE TABLE usuarios_new (id INTEGER PRIMARY KEY NOT NULL, nombre TEXT NOT NULL, apellido TEXT NOT NULL, correo_electronico TEXT NOT NULL, contrasena TEXT, uidFirebase TEXT, edad INTEGER NOT NULL, genero TEXT NOT NULL, direccion TEXT NOT NULL, profile_completed INTEGER NOT NULL, UNIQUE(correo_electronico))");

            // Copiar los datos de la tabla antigua a la nueva
            database.execSQL("INSERT INTO usuarios_new (id, nombre, apellido, correo_electronico, contrasena, uidFirebase, edad, genero, direccion, profile_completed) SELECT id, nombre, apellido, correo_electronico, contrasena, uidFirebase, edad, genero, direccion, profile_completed FROM usuarios");

            // Eliminar la tabla antigua
            database.execSQL("DROP TABLE usuarios");

            // Cambiar el nombre de la tabla nueva a usuarios
            database.execSQL("ALTER TABLE usuarios_new RENAME TO usuarios");
        }
    };
}
