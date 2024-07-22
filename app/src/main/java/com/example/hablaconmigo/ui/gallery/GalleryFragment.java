package com.example.hablaconmigo.ui.gallery;

import static android.app.Activity.RESULT_OK;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.widget.Toast;

import com.example.hablaconmigo.R;
import com.example.hablaconmigo.databinding.FragmentGalleryBinding;

import java.util.ArrayList;
import java.util.Locale;

public class GalleryFragment extends Fragment {

    private FragmentGalleryBinding binding;
    private Button writeButton;
    private Button listenButton;
    private ImageView clearButton;
    private ImageView chatSettings;
    private AutoCompleteTextView autoCompleteTextViewLanguage;
    private MessageAdapter messageAdapter;
    private static final int REQUEST_CODE_SPEECH_INPUT = 1000;
    private static final int REQUEST_PERMISSION_RECORD_AUDIO = 2000;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        GalleryViewModel galleryViewModel =
                new ViewModelProvider(this).get(GalleryViewModel.class);

        binding = FragmentGalleryBinding.inflate(inflater, container, false);

        RecyclerView recyclerView = binding.recyclerViewMensajes;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        messageAdapter = new MessageAdapter(getContext(), galleryViewModel.getMessages());
        recyclerView.setAdapter(messageAdapter);

        writeButton = binding.btnEscribir;
        listenButton = binding.btnEscuchar;
        clearButton = binding.imageViewTrash;
        chatSettings = binding.imageViewChatSettings;
        autoCompleteTextViewLanguage = binding.autoCompleteTextViewLanguage;

        writeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MessageDialogFragment messageDialog = new MessageDialogFragment();
                messageDialog.setMessageAdapter(messageAdapter);
                messageDialog.show(getParentFragmentManager(), "messageDialog");
            }
        });

        listenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                    // Si se concede el permiso, inicia el reconocimiento de voz
                    Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                    intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Habla ahora");

                    try {
                        startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    // Si no se concede el permiso, se lo solicita al usuario
                    requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_PERMISSION_RECORD_AUDIO);
                }
            }
        });

        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConfirmationDialog();
            }
        });

        chatSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavController navController = Navigation.findNavController(v);
                navController.navigate(R.id.chatSettingsFragment);
            }
        });

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        //Definir el arreglo de idiomas
        String[] languages = getResources().getStringArray(R.array.languages);
        // Crea el adaptador y lo asigna al AutoCompleteTextView
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), R.layout.dropdown_language_item, languages);
        autoCompleteTextViewLanguage.setAdapter(adapter);

        autoCompleteTextViewLanguage.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getContext(), "Las funciones de traducción aún están en desarrollo", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSION_RECORD_AUDIO) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // El permiso fue concedido
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Habla ahora");

                try {
                    startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(getContext(), "Se necesito el permiso de audio para usar esta función", Toast.LENGTH_SHORT).show();

            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_SPEECH_INPUT && resultCode == RESULT_OK) {
            ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (result != null && !result.isEmpty()) {
                String spokenText = result.get(0);
                // Crea un nuevo mensaje con el texto hablado y lo añade al adaptador
                Message message = new Message(spokenText, false);
                messageAdapter.addMessage(message);
            }
        }
    }

    private void showConfirmationDialog() {
        new AlertDialog.Builder(getContext())
                .setTitle("Confirmación")
                .setMessage("¿Está seguro de que desea eliminar la conversación?")
                .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Continuar con la eliminación de los mensajes
                        clearMessages();
                    }
                })
                .setNegativeButton("No", null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void clearMessages() {
        messageAdapter.clearMessages();
    }


}