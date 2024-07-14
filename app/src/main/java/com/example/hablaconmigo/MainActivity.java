package com.example.hablaconmigo;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AccelerateInterpolator;

import androidx.core.splashscreen.SplashScreen;


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.hablaconmigo.database.AppDataBase;
import com.example.hablaconmigo.entities.Usuario;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private SharedPreferences userSessionPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SplashScreen splashScreen = SplashScreen.installSplashScreen(this);
        splashScreen.setOnExitAnimationListener((splashScreenView) -> {
            ObjectAnimator fadeOut = ObjectAnimator.ofFloat(splashScreenView.getView(), View.ALPHA, 1f, 0f);
            fadeOut.setDuration(1000L);
            fadeOut.setInterpolator(new AccelerateInterpolator());
            fadeOut.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    splashScreenView.remove();
                }
            });
            fadeOut.start();
        });
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        userSessionPreferences = getSharedPreferences("UserSessionPreferences", MODE_PRIVATE);

    }

    @Override
    protected void onStart() {
        super.onStart();

        // Espera de 2 segundos antes de verificar la sesión del usuario
        new Handler().postDelayed(() -> {
            long userId = userSessionPreferences.getLong("userId", -1);
            if (userId != -1) { // El usuario ha iniciado sesión
                AppDataBase appDataBase = MyApplication.dataBase;
                ExecutorService executorService = Executors.newSingleThreadExecutor();
                executorService.execute(() -> {
                    Usuario usuario = appDataBase.daoUsuarios().findUserById(userId);
                    boolean isProfileCompleted = false;
                    if (usuario != null) {
                        isProfileCompleted = usuario.getProfileCompleted();
                    }
                    Intent intent = getRedirectIntent(isProfileCompleted);
                    startActivity(intent);
                    finish();
                });
            } else {
                // El usuario no ha iniciado sesión, redirige a LoginActivity
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        }, 2000); // Espera de 2000 milisegundos (2 segundos)
    }

    private Intent getRedirectIntent(boolean isProfileCompleted) {
        if (!isProfileCompleted) {
            // El perfil del usuario no está completo, redirige a CompleteProfileActivity
            return new Intent(MainActivity.this, CompleteProfileActivity.class);
        } else {
            // El perfil del usuario está completo, redirige a MainMenuNavigationActivity
            return new Intent(MainActivity.this, MainMenuNavigationActivity.class);
        }
    }
}




