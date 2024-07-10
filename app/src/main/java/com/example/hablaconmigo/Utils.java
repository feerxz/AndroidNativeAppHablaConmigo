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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;


import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;



public final class Utils {
    // Método para configurar el botón de visibilidad de la contraseña
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

    // Método para validar el email
    public static boolean validateEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    // Método para mostrar un SnackBar de error
    @SuppressLint("SetTextI18n")
    public static void showSnackBarError(View view, String message) {
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
        snackbar.setBackgroundTint(Color.RED);
        View snackbarView = snackbar.getView();
        TextView textView = snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);
        snackbar.show();
    }

    // Método para mostrar un SnackBar de éxito
    public static void showSnackBarSuccess(View view, String message) {
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
        snackbar.setBackgroundTint(Color.GREEN);
        View snackbarView = snackbar.getView();
        TextView textView = snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);
        snackbar.show();
    }

    // Método para mostrar un SnackBar de alerta
    public static void showSnackBarAlert(View view, String message) {
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
        snackbar.setBackgroundTint(Color.YELLOW);
        View snackbarView = snackbar.getView();
        TextView textView = snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setTextColor(Color.BLACK);
        snackbar.show();
    }

    static class UserExistsResult {
        private final boolean exists;
        private final Exception exception;

        public UserExistsResult(boolean exists, Exception exception) {
            this.exists = exists;
            this.exception = exception;
        }

        public boolean userExists() {
            return exists;
        }

        public Exception getException() {
            return exception;
        }
    }


    public interface UserExistsCallback {
        void onCompleted(UserExistsResult result);
    }

    // Método para verificar si un usuario ya existe en Firebase
    public static void checkIfUserExistsInFirebase(String email, UserExistsCallback callback) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.createUserWithEmailAndPassword(email, "password")
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Si la tarea es exitosa, el usuario se creó correctamente y no existía previamente
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            user.delete()
                                    .addOnCompleteListener(deleteTask -> {
                                        if (deleteTask.isSuccessful()) {
                                            callback.onCompleted(new UserExistsResult(false, null));
                                        }
                                    });
                        }
                    } else {
                        Exception exception = task.getException();
                        if (exception instanceof FirebaseAuthException) {
                            FirebaseAuthException e = (FirebaseAuthException) exception;
                            // Si el error es que el email ya está en uso, significa que el usuario sí existe
                            callback.onCompleted(new UserExistsResult(e.getErrorCode().equals("ERROR_EMAIL_ALREADY_IN_USE"), e));
                        } else {
                            // Otro tipo de errores
                            callback.onCompleted(new UserExistsResult(false, exception));
                        }
                    }
                });
    }



}

