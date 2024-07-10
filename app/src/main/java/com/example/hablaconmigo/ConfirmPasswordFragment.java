package com.example.hablaconmigo;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.hablaconmigo.database.AppDataBase;
import com.example.hablaconmigo.entities.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class ConfirmPasswordFragment extends Fragment {
    String email;
    private ExecutorService executorService;

    public ConfirmPasswordFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_confirm_password, container, false);
        Bundle bundle = getArguments();
        if (bundle != null) {
            email = bundle.getString("email");
        }
        MaterialButton btnValidate = view.findViewById(R.id.validarbtn);
        btnValidate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextInputLayout textInputLayout = view.findViewById(R.id.textInputLayoutpassword);
                TextInputEditText password = view.findViewById(R.id.confirmpasswordreset);
                if (password != null){
                    validatePassword(v, password, textInputLayout);
                }
            }
        });
        return view;
    }

    private void validatePassword(View view, TextInputEditText password, TextInputLayout textInputLayout) {
        String passwordText = password.getText().toString();
        if (passwordText.isEmpty()) {
            textInputLayout.setError("Introduce la contraseña.");
            return;
        } else {
            textInputLayout.setError(null);
            FirebaseAuth auth = FirebaseAuth.getInstance();
            auth.signInWithEmailAndPassword(email, passwordText)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // La autenticación fue exitosa (existe un usuario con esas credenciales), acutalizamos la contraseña en la db local
                                auth.signOut();
                                AppDataBase appDataBase = MyApplication.dataBase;
                                executorService = Executors.newSingleThreadExecutor();
                                executorService.execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        Usuario usuario = appDataBase.daoUsuarios().findUserByEmail(email);
                                        usuario.setContrasena(Utils.hashPassword(passwordText));
                                        appDataBase.daoUsuarios().update(usuario);
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                // Mostrar un mensaje al usuario
                                                Toast.makeText(getActivity(), "Contraseña actualizada. Por favor inicie sesión con la nueva contraseña.", Toast.LENGTH_LONG).show();
                                                // Navegar de nuevo a la actividad de inicio de sesión
                                                Intent intent = new Intent(getActivity(), LoginActivity.class);
                                                startActivity(intent);
                                                getActivity().finish();
                                            }
                                        });
                                    }

                                });

                            } else {
                                // La autenticación falló, no existe una cuenta con estas credenciales
                                Toast.makeText(getActivity(), "La contraseña proporcionada no coincide.", Toast.LENGTH_LONG).show();
                            }
                        }
                    });



        }
    }


}