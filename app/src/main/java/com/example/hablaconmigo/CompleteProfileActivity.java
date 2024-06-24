package com.example.hablaconmigo;




import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.hablaconmigo.database.AppDataBase;
import com.example.hablaconmigo.entities.Usuario;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CompleteProfileActivity extends AppCompatActivity {

    private ExecutorService executorService;
    private long userId;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_complete_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Spinner spinnerGenero = findViewById(R.id.spinnerGenero);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.genero_array, R.layout.spinner_selected_item);
        adapter.setDropDownViewResource(R.layout.spinner_item);
        spinnerGenero.setAdapter(adapter);

        Intent intent = getIntent();
        userId = intent.getLongExtra("userId", -1);
        // Si userId no está presente en el Intent, se intenta obtenerlo de las preferencias compartidas
        if (userId == -1) {
            SharedPreferences sharedpreferences = getSharedPreferences("UserSessionPreferences", MODE_PRIVATE);
            userId = sharedpreferences.getLong("userId", -1);
        }

        mAuth = FirebaseAuth.getInstance();

    }

    public void saveCompleteProfile(View view) {
        String name, lastname, gender, address, ageString;
        int age;
        name = ((EditText) findViewById(R.id.nombre)).getText().toString();
        lastname = ((EditText) findViewById(R.id.apellido)).getText().toString();
        ageString = ((EditText) findViewById(R.id.edad)).getText().toString();
        gender = ((Spinner) findViewById(R.id.spinnerGenero)).getSelectedItem().toString();
        address = ((EditText) findViewById(R.id.direccion)).getText().toString();

        if (name.isEmpty() || lastname.isEmpty() || ageString.isEmpty() || gender.isEmpty() || address.isEmpty()) {
            Utils.showSnackBarAlert(view, "Por favor, completa todos los campos");
        } else {
            age = Integer.parseInt(ageString);
            AppDataBase appDataBase = MyApplication.dataBase;
            executorService = Executors.newSingleThreadExecutor();

            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    Usuario usuario = appDataBase.daoUsuarios().findUserById(userId);
                    if (usuario != null) { //Se verifica si se recuperó el usuario válido para poder hacer la actualizacion de datos
                        usuario.setNombre(name);
                        usuario.setApellido(lastname);
                        usuario.setEdad(age);
                        usuario.setGenero(gender);
                        usuario.setDireccion(address);
                        usuario.setProfileCompleted(true);
                        appDataBase.daoUsuarios().update(usuario);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(CompleteProfileActivity.this, "Datos guardados correctamente", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(CompleteProfileActivity.this, MainMenuNavigationActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(CompleteProfileActivity.this, "Error al guardar los datos", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null) {
            executorService.shutdown();
        }
    }

}

