package com.example.hablaconmigo;



import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;

import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

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

public class SignUpActivity extends AppCompatActivity {

    private ExecutorService executorService;
    private SharedPreferences sharedpreferences;
    private static final int RC_SIGN_IN = 1;
    GoogleSignInClient mGoogleSignInClient;
    FirebaseAuth mAuth;
    AppDataBase appDataBase = MyApplication.dataBase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

         //Añadir subrayado a texto "Inicia Sesión"
        TextView goToLogin = findViewById(R.id.goToLogin);
        SpannableString content = new SpannableString(goToLogin.getText());
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        goToLogin.setText(content);

        //Funcionalidad para mostrar u ocultar contraseña cuando se presiona el icono del ojo
        final EditText passwordEditText = findViewById(R.id.password1);
        final EditText confirmPasswordEditText = findViewById(R.id.confirmpassword);
        final ImageButton togglePasswordVisibilityButton1 = findViewById(R.id.imageBtnEye);
        final ImageButton togglePasswordVisibilityButton2 = findViewById(R.id.imageBtnEye2);
        Utils.setupPasswordVisibilityToggle(passwordEditText, togglePasswordVisibilityButton1);
        Utils.setupPasswordVisibilityToggle(confirmPasswordEditText, togglePasswordVisibilityButton2);

        executorService = Executors.newSingleThreadExecutor();
        sharedpreferences = getSharedPreferences("UserSessionPreferences", MODE_PRIVATE);

        mAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
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
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            //Log.w(TAG, "signInWithCredential:failure", task.getException());

                            String errorMessage = "Error al iniciar sesión con Google: ";
                            if (task.getException() != null) {
                                errorMessage += task.getException().toString();
                            }
                            Toast.makeText(SignUpActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                            updateUI(null);
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
                        long userId = appDataBase.daoUsuarios().insert(new Usuario("", "", user.getEmail(), null, user.getUid(),0, "", " ", false, null));
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(SignUpActivity.this, "¡Cuenta creada correctamente!", Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(SignUpActivity.this, "¡Bienvenido de vuelta!", Toast.LENGTH_SHORT).show();
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


    public void returnToLogin(View view) {
        finish();
    }

    public void createAccount(View view) {
        String email, password1, password2;
        EditText emailEditText = findViewById(R.id.edtemail);
        EditText passwordEditText1 = findViewById(R.id.password1);
        EditText passwordEditText2 = findViewById(R.id.confirmpassword);

        email = emailEditText.getText().toString().trim().toLowerCase();
        password1 = passwordEditText1.getText().toString();
        password2 = passwordEditText2.getText().toString();

        // Reset errors
        emailEditText.setError(null);
        passwordEditText1.setError(null);
        passwordEditText2.setError(null);

        if (email.isEmpty()) {
            emailEditText.setError("Por favor, completa este campo");
        } else if (!Utils.validateEmail(email)) {
            emailEditText.setError("Por favor, introduce un email válido");
        } else if (password1.isEmpty()) {
            passwordEditText1.setError("Por favor, completa este campo");
        } else if (password2.isEmpty()) {
            passwordEditText2.setError("Por favor, completa este campo");
        } else if (!password1.equals(password2)) {
            passwordEditText2.setError("Las contraseñas no coinciden");
        } else if (password1.length() < 6) {
            passwordEditText1.setError("La contraseña debe tener al menos 6 caracteres");
        } else {
            //Se hashea la contraseña antes de guardarla
            String hashedPassword = Utils.hashPassword(password1);
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    Usuario existingUser = appDataBase.daoUsuarios().findUserByEmail(email);
                    if (existingUser != null) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Utils.showSnackBarAlert(view, "Ya existe una cuenta asociada con este email");
                            }
                        });
                    } else {
                        mAuth.createUserWithEmailAndPassword(email, password1)
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            // El usuario se creó con éxito en Firebase, ahora se lo guarda en la db local
                                            executorService.execute(new Runnable() {
                                                @Override
                                                public void run() {
                                                    long userId = appDataBase.daoUsuarios().insert(new Usuario("", "", email, hashedPassword, null, 0, "", " ", false, null));
                                                    runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            Toast.makeText(SignUpActivity.this, "¡Cuenta creada correctamente!", Toast.LENGTH_SHORT).show();
                                                            SharedPreferences.Editor editor = sharedpreferences.edit();
                                                            editor.putLong("userId", userId);
                                                            editor.apply();
                                                            // Navegar a CompleteProfileActivity
                                                            Intent intent = new Intent(SignUpActivity.this, CompleteProfileActivity.class);
                                                            intent.putExtra("userId", userId);
                                                            setResult(RESULT_OK);
                                                            startActivity(intent);
                                                            finish();
                                                        }
                                                    });
                                                }
                                            });
                                        } else {
                                            Utils.showSnackBarAlert(view, "Ya existe una cuenta asociada con este email");
                                            updateUI(null);
                                        }
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

    private void navigateToMainMenu() {
        Intent intent = new Intent(this, MainMenuNavigationActivity.class);
        startActivity(intent);
        finishAffinity();
    }

    private void navigateToCompleteProfile( long userId) {
        Intent intent = new Intent(this, CompleteProfileActivity.class);
        intent.putExtra("userId", userId);
        startActivity(intent);
        finishAffinity();
    }

}