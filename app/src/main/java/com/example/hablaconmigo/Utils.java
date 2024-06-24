package com.example.hablaconmigo;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.text.method.PasswordTransformationMethod;
import android.util.Base64;

import android.view.View;

import android.widget.EditText;

import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.material.snackbar.Snackbar;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public final class Utils {
    public static void setupPasswordVisibilityToggle(final EditText passwordEditText, final ImageButton togglePasswordVisibilityButton) {
        togglePasswordVisibilityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (passwordEditText.getTransformationMethod() instanceof PasswordTransformationMethod) {
                    // Si la contraseña está actualmente oculta, la mostramos y cambiamos el icono a 'ojo con línea'
                    passwordEditText.setTransformationMethod(null);
                    togglePasswordVisibilityButton.setImageResource(R.drawable.eye_off);
                } else {
                    // Si la contraseña está actualmente visible, la ocultamos y cambiamos el icono a 'ojo abierto'
                    passwordEditText.setTransformationMethod(new PasswordTransformationMethod());
                    togglePasswordVisibilityButton.setImageResource(R.drawable.eye);
                }

                passwordEditText.setSelection(passwordEditText.getText().length());
            }
        });
    }

    //Método para hashear la contraseña y evitar guardarla en texto plano
    public static String hashPassword(@NonNull String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            return Base64.encodeToString(hash, Base64.DEFAULT);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error al hashear la contraseña", e);
        }
    }
    public static boolean validateEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    @SuppressLint("SetTextI18n")
    public static void showSnackBarError(View view, String message) {
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
        snackbar.setBackgroundTint(Color.RED);
        View snackbarView = snackbar.getView();
        TextView textView = snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);
        snackbar.show();
    }

    public static void showSnackBarSuccess(View view, String message) {
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
        snackbar.setBackgroundTint(Color.GREEN);
        View snackbarView = snackbar.getView();
        TextView textView = snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);
        snackbar.show();
    }

    public static void showSnackBarAlert(View view, String message) {
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
        snackbar.setBackgroundTint(Color.YELLOW);
        View snackbarView = snackbar.getView();
        TextView textView = snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setTextColor(Color.BLACK);

        snackbar.show();
    }
}

