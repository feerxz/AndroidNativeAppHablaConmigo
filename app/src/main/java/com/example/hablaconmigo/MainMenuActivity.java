package com.example.hablaconmigo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;

public class MainMenuActivity extends AppCompatActivity {
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main_menu);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        mAuth = FirebaseAuth.getInstance();
    }

    /*@Override
    protected void onStart() {
        super.onStart();
        // Verificar si el usuario está autenticado
        if (mAuth.getCurrentUser() == null) {
            // Redirigir a la pantalla de inicio de sesión
            goToLogin();
            finish();
        }
    }*/

    public void logout(View view) {
        // Cerrar sesión en Firebase
        mAuth.signOut();

        // Cerrar sesión en Google SignIn
        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, GoogleSignInOptions.DEFAULT_SIGN_IN);
        mGoogleSignInClient.signOut();

        // Obtener las preferencias compartidas
        SharedPreferences sharedpreferences = getSharedPreferences("UserSessionPreferences", MODE_PRIVATE);
        // Eliminar userId de las preferencias compartidas
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.remove("userId");
        editor.apply();
        // Redirigir a la pantalla de inicio de sesión
        goToLogin();
        finish();
    }

    private void goToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}