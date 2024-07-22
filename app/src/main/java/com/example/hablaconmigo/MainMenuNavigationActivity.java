package com.example.hablaconmigo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.example.hablaconmigo.database.AppDataBase;
import com.example.hablaconmigo.databinding.NavHeaderMainMenuNavigationBinding;
import com.example.hablaconmigo.entities.Usuario;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;


import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.example.hablaconmigo.databinding.ActivityMainMenuNavigationBinding;
import com.google.firebase.auth.FirebaseAuth;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainMenuNavigationActivity extends AppCompatActivity  {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainMenuNavigationBinding binding;
    private NavHeaderMainMenuNavigationBinding navHeaderBinding;
    AppDataBase appDataBase = MyApplication.dataBase;
    private ExecutorService executorService;

    private SharedPreferences sharedpreferences;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainMenuNavigationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMainMenuNavigation.toolbar);
        /*binding.appBarMainMenuNavigation.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null)
                        .setAnchorView(R.id.fab).show();
            }
        });*/
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        View headerView = navigationView.getHeaderView(0); // Obtener la vista del encabezado del menú
        navHeaderBinding = NavHeaderMainMenuNavigationBinding.bind(headerView); // Vincular la vista del encabezado del menú
        executorService = Executors.newSingleThreadExecutor();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                sharedpreferences = getSharedPreferences("UserSessionPreferences", MODE_PRIVATE);
                long userId = sharedpreferences.getLong("userId", -1);
                Usuario usuario = appDataBase.daoUsuarios().findUserById(userId);
                // Acceder a los TextViews para setear su contenido
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TextView navName = navHeaderBinding.navheadertxtvNombre;
                        TextView navEmail = navHeaderBinding.navheadertxtvCorreo;
                        ImageView navImage = navHeaderBinding.imageViewProfilePicture;
                        if (usuario != null) {
                            String fullName = usuario.getNombre() + " " + usuario.getApellido();
                            String pictureSource = usuario.getRutaImagenPerfil();
                            if (pictureSource != null){
                                Glide.with(MainMenuNavigationActivity.this).load(new File(pictureSource)).into(navImage);
                            }
                            navName.setText(fullName);
                            navEmail.setText(usuario.getCorreoElectronico());
                        } else {
                            navName.setText("Usuario Anónimo");
                            navEmail.setText("xxxxxxxxxx");
                        }
                    }
                });
            }
        });



        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_chat, R.id.nav_tarjetas, R.id.nav_atajos)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main_menu_navigation);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        mAuth = FirebaseAuth.getInstance();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu_navigation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_logout) {
            logout();
            return true;
        } else if (id == R.id.action_settings) {
            Toast.makeText(this, "Configuraciones aún en desarrollo", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void logout() {
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
    }

    private void goToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main_menu_navigation);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}