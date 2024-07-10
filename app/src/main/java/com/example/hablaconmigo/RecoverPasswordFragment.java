package com.example.hablaconmigo;

import android.app.Activity;
import android.content.Context;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;


import com.example.hablaconmigo.database.AppDataBase;
import com.example.hablaconmigo.entities.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuth;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RecoverPasswordFragment extends Fragment {
    private ExecutorService executorService;
    AppDataBase appDataBase = MyApplication.dataBase;
    FirebaseAuth mAuth;
    Activity activity; // Variable de instancia para la Activity

    public RecoverPasswordFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof Activity){
            activity = (Activity) context; // Asignar la Activity cuando el Fragment se adjunta
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        activity = null; // Limpiar la referencia a la Activity cuando el Fragment se desadjunta
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        executorService = Executors.newSingleThreadExecutor();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_recover_password, container, false);
        ImageButton back = view.findViewById(R.id.backbutton);
        Button recoverAccountButton = view.findViewById(R.id.btnrecoveraccount);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (activity != null) {
                    // Hacer un cast a AppCompatActivity para poder usar getSupportFragmentManager()
                    AppCompatActivity appCompatActivity = (AppCompatActivity) activity;
                    // Obtener una referencia al fragmento de recuperación de contraseña
                    RecoverPasswordFragment recoverPasswordFragment = (RecoverPasswordFragment) appCompatActivity.getSupportFragmentManager().findFragmentById(R.id.fragmentPasswordReset_container);
                    // Verificar si el fragmento está visible
                    if (recoverPasswordFragment != null && recoverPasswordFragment.isVisible()) {
                        // Si el fragmento está visible, ocultarlo y hacer visible el contenedor de vistas principales
                        appCompatActivity.getSupportFragmentManager().beginTransaction().remove(recoverPasswordFragment).commit();
                        ConstraintLayout mainViewsContainer = appCompatActivity.findViewById(R.id.mainViewsContainer);
                        mainViewsContainer.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
        recoverAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendRecoverPasswordEmail(v);
            }
        });

        return view;
    }
    private void sendRecoverPasswordEmail(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(R.layout.layout_loading_dialog); // layout_loading_dialog es tu layout personalizado para el diálogo de carga
        AlertDialog dialog = builder.create();
        dialog.show();

        View fragmentView = getView();
        if (fragmentView != null){
            EditText emailEditText = fragmentView.findViewById(R.id.editxtemailrecover);
            String email = emailEditText.getText().toString().trim().toLowerCase();
            if (email.isEmpty()){
                emailEditText.setError("Introduce el correo por favor");
                emailEditText.requestFocus();
                return;
            } else if (!Utils.validateEmail(email)){
                emailEditText.setError("Por favor, introduce un email válido");
                emailEditText.requestFocus();
                return;
            } else {
                executorService.execute(new Runnable() {
                    @Override
                    public void run() {
                        //Se verifica que exista un usuario en la base de datos con el correo electrónico proporcionado
                        Usuario existingUser = appDataBase.daoUsuarios().findUserByEmail(email);
                        if (existingUser == null) {
                            //Verificar ahora si el usuario existe en firebase (porque se pudieron haber borrado los datos de la app, por lo tanto la db local queda vacía)
                            Utils.checkIfUserExistsInFirebase(email, new Utils.UserExistsCallback() {
                                @Override
                                public void onCompleted(Utils.UserExistsResult result) {
                                    if (result.userExists()) {
                                        // El usuario existe en Firebase, se envía el email de recuperación de contraseña
                                        mAuth.sendPasswordResetEmail(email)
                                                .addOnCompleteListener(task -> {
                                                    dialog.dismiss();
                                                    if (task.isSuccessful()) {
                                                        if (activity != null) {
                                                            activity.runOnUiThread(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    Utils.showSnackBarSuccess(view, "Correo de recuperación de contraseña enviado");
                                                                    new Handler().postDelayed(new Runnable() {
                                                                        @Override
                                                                        public void run() {
                                                                            Bundle bundle = new Bundle();
                                                                            bundle.putString("email", email);
                                                                            ConfirmPasswordFragment confirmPasswordFragment = new ConfirmPasswordFragment();
                                                                            confirmPasswordFragment.setArguments(bundle);
                                                                            getParentFragmentManager().beginTransaction().replace(R.id.fragmentPasswordReset_container, confirmPasswordFragment).commit();
                                                                        }
                                                                    }, 2000); // Esperar 2 segundos antes de ejecutar el código dentro de run()
                                                                }
                                                            });
                                                        }
                                                    } else {
                                                        if (activity != null) {
                                                            activity.runOnUiThread(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    Utils.showSnackBarError(view, "Error al enviar el correo de recuperación de contraseña");
                                                                }
                                                            });
                                                        }
                                                    }
                                                });
                                    } else {
                                        //Mostrar un mensaje detallando el problema
                                        dialog.dismiss();
                                        if (result.getException() instanceof FirebaseNetworkException) {
                                            Utils.showSnackBarError(view, "No se pudo procesar la solicitud debido a un fallo de red, vuelve a intentarlo cuando tengas conexión");
                                        } else {
                                            Utils.showSnackBarError(view, "No existe una cuenta asociada a ese correo electrónico");
                                        }
                                    }
                                }
                            });
                        } else {
                            if (existingUser.getUidFirebase() == null || existingUser.getUidFirebase().isEmpty()){
                                mAuth.sendPasswordResetEmail(email)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                dialog.dismiss();
                                                if (task.isSuccessful()) {
                                                    if (activity != null) {
                                                        activity.runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                Utils.showSnackBarSuccess(view, "Correo de recuperación de contraseña enviado");
                                                                new Handler().postDelayed(new Runnable() {
                                                                    @Override
                                                                    public void run() {
                                                                        Bundle bundle = new Bundle();
                                                                        bundle.putString("email", email);
                                                                        ConfirmPasswordFragment confirmPasswordFragment = new ConfirmPasswordFragment();
                                                                        confirmPasswordFragment.setArguments(bundle);
                                                                        getParentFragmentManager().beginTransaction().replace(R.id.fragmentPasswordReset_container, confirmPasswordFragment).commit();
                                                                    }
                                                                }, 2000); // Esperar 2 segundos antes de ejecutar el código dentro de run()
                                                            }
                                                        });
                                                    }
                                                } else {
                                                    if (activity != null) {
                                                        activity.runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                Utils.showSnackBarError(view, "Error al enviar el correo de recuperación de contraseña");
                                                            }
                                                        });
                                                    }
                                                }
                                            }
                                        });
                            } else {
                                if (activity != null) {
                                    activity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            new Handler().postDelayed(new Runnable(){
                                                @Override
                                                public void run(){
                                                    dialog.dismiss();
                                                    Utils.showSnackBarAlert(view, "La cuenta está asociada con un proveedor social, por favor inicia sesión con Google");
                                                }
                                            }, 1000);
                                        }
                                    });
                                }
                            }

                        }
                    }
                });
            }
        }
    }

}