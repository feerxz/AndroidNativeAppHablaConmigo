package com.example.hablaconmigo;



import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;


import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.activity.result.contract.ActivityResultContracts;

import com.example.hablaconmigo.database.AppDataBase;
import com.example.hablaconmigo.entities.Usuario;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;



import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoginActivity extends AppCompatActivity {

    // Crear una instancia de ActivityResultLauncher
    private ActivityResultLauncher<Intent> signUpActivityResultLauncher;
    private ExecutorService executorService;
    private SharedPreferences sharedpreferences;
    private static final int RC_SIGN_IN = 1;
    GoogleSignInClient mGoogleSignInClient;
    FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        executorService = Executors.newSingleThreadExecutor();
        sharedpreferences = getSharedPreferences("UserSessionPreferences", MODE_PRIVATE);

        mAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        final EditText passwordEditText = findViewById(R.id.password);
        final ImageButton togglePasswordVisibilityButton = findViewById(R.id.imageButtoneye);
        Utils.setupPasswordVisibilityToggle(passwordEditText, togglePasswordVisibilityButton);

        // Inicializar signUpActivityResultLauncher
        signUpActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // Si el registro se ha completado, , cierra la actividad de inicio de sesión
                        finish();
                    }
                }
        );
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    public void signIn( View view) {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                //Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Toast.makeText(this, "Error al iniciar sesión con Google: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                //Log.w(TAG, "Google sign in failed", e);
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            //Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            SharedPreferences.Editor editor = sharedpreferences.edit();
                            editor.putBoolean("justLoggedIn", true); // Añade esta línea
                            editor.apply();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            //Log.w(TAG, "signInWithCredential:failure", task.getException());
                            updateUI(null);
                            String errorMessage = "Error al iniciar sesión con Google: ";
                            if (task.getException() != null) {
                                errorMessage += task.getException().toString();
                            }
                            Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void updateUI(FirebaseUser user) {
        if (user != null){
            AppDataBase appDataBase = MyApplication.dataBase;
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    Usuario usuario = appDataBase.daoUsuarios().findUserByEmail(user.getEmail());
                    if (usuario == null){
                        //Si el usuario no existe en la base de datos, se crea
                        long userId = appDataBase.daoUsuarios().insert(new Usuario("", "", user.getEmail(), null, user.getUid(),0, "", " ", false));
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(LoginActivity.this, "¡Cuenta creada correctamente!", Toast.LENGTH_SHORT).show();
                            }
                        });
                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.putLong("userId", userId);
                        editor.apply();
                        navigateToCompleteProfile(userId);
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // Solo muestra el mensaje si el usuario acaba de iniciar sesión
                                if (sharedpreferences.getBoolean("justLoggedIn", false)) {
                                    Toast.makeText(LoginActivity.this, "¡Bienvenido de vuelta!", Toast.LENGTH_SHORT).show();
                                    // Establece el valor en false para la próxima vez
                                    SharedPreferences.Editor editor = sharedpreferences.edit();
                                    editor.putBoolean("justLoggedIn", false);
                                    editor.apply();
                                }
                            }
                        });
                        //Como el usuario existe, se verifica si el perfil está completo
                        if (usuario.getProfileCompleted()){
                            //Si el perfil está completo, se redirige al menú principal
                            navigateToMainMenu();
                        } else {
                            //Si el perfil no está completo, se redirige a CompleteProfileActivity
                            navigateToCompleteProfile(usuario.getId());
                        }
                    }
                }
            });
        }
    }



    public void navigateToSignUp(View view) {
        Intent intent = new Intent(this, SignUpActivity.class);
        signUpActivityResultLauncher.launch(intent);
    }

    public void login(View view) {
        // Recuperar el correo y la contraseña ingresados por el usuario
        String email, password;
        EditText emailEditText = findViewById(R.id.etEmailLogin);
        EditText passwordEditText = findViewById(R.id.password);
        email = emailEditText.getText().toString().trim().toLowerCase();
        password = passwordEditText.getText().toString();

        emailEditText.setError(null);
        passwordEditText.setError(null);

        // Verificar si los campos no están vacíos
        if (email.isEmpty()) {
            emailEditText.setError("Por favor, completa este campo");
            return;
        } else if (password.isEmpty()) {
            passwordEditText.setError("Por favor, completa este campo");
            return;
        } else {
            // Hashear la contraseña para compararla con la almacenada en la base de datos
            String hashedPassword = Utils.hashPassword(password);
            // Buscar en la base de datos si existe un usuario con ese correo
            AppDataBase appDataBase = MyApplication.dataBase;
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    Usuario usuario = appDataBase.daoUsuarios().findUserByEmail(email);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (usuario != null) {
                                // Si existe un usuario con ese correo, verificar si tiene una contraseña almacenada
                                if (usuario.getContrasena() == null) {
                                    // Si no tiene contraseña almacenada, mostrar un mensaje de que esa cuenta se ha registrado con Google
                                    Utils.showSnackBarAlert(view, "Este usuario se ha registrado con Google, por favor inicia sesión con Google");
                                } else {
                                    // Si tiene contraseña almacenada, verificar si coincide con la contraseña ingresada por el usuario
                                    if (usuario.getContrasena().equals(hashedPassword)) {
                                        // Si la contraseña coincide, almacenar el id del usuario en las preferencias compartidas
                                        SharedPreferences.Editor editor = sharedpreferences.edit();
                                        editor.putLong("userId", usuario.getId());
                                        editor.apply();
                                        // Verificar si el perfil del usuario está completo
                                        if (usuario.getProfileCompleted()) {
                                            // Si el perfil está completo, redirigir al menú principal
                                            navigateToMainMenu();
                                        } else {
                                            // Si el perfil no está completo, redirigir a CompleteProfileActivity
                                            navigateToCompleteProfile(usuario.getId());
                                        }
                                    } else {
                                        // Si la contraseña no coincide, mostrar un mensaje de error
                                        Utils.showSnackBarError(view, "Contraseña incorrecta");
                                    }
                                }
                            } else {
                                // Si no existe un usuario con ese correo en la base de datos, buscar si existe en firebase
                                Utils.showSnackBarError(view, "No existe una cuenta asociada a ese correo electrónico");
                            }
                        }
                    });
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

    private void navigateToMainMenu() {
        Intent intent = new Intent(this, MainMenuNavigationActivity.class);
        startActivity(intent);
        finish();
    }

    private void navigateToCompleteProfile( long userId) {
        Intent intent = new Intent(this, CompleteProfileActivity.class);
        intent.putExtra("userId", userId);
        startActivity(intent);
        finish();
    }
}