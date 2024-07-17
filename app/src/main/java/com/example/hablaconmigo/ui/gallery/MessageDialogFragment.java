package com.example.hablaconmigo.ui.gallery;

import android.app.Dialog;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.cybozu.labs.langdetect.Detector;
import com.cybozu.labs.langdetect.DetectorFactory;

import com.cybozu.labs.langdetect.LangDetectException;
import com.example.hablaconmigo.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MessageDialogFragment extends DialogFragment {

    private TextInputLayout messageTextInputLayout;
    private FloatingActionButton sendFloatingActionButton;
    private MessageAdapter messageAdapter;
    private GalleryViewModel galleryViewModel;
    private TextToSpeech textToSpeech;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    // Obtén una lista de todos los archivos de perfil en la carpeta 'assets/profiles'
                    String[] profiles = getContext().getAssets().list("profiles");

                    if (profiles != null) {
                        // Crea una lista para almacenar los perfiles
                        List<String> jsonProfiles = new ArrayList<>();

                        // Para cada archivo de perfil, lee el contenido del archivo y añádelo a la lista
                        for (String profile : profiles) {
                            InputStream is = getContext().getAssets().open("profiles/" + profile);
                            int size = is.available();
                            byte[] buffer = new byte[size];
                            is.read(buffer);
                            is.close();
                            String jsonProfile = new String(buffer, "UTF-8");
                            jsonProfiles.add(jsonProfile);
                        }

                        // Carga los perfiles en DetectorFactory
                        DetectorFactory.loadProfile(jsonProfiles);
                    }
                } catch (Exception e) {
                    Log.e("MessageDialogFragment", "Error al cargar los perfiles de idioma", e);
                }
            }
        });
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        galleryViewModel = new ViewModelProvider(requireActivity()).get(GalleryViewModel.class);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_message, null);

        messageTextInputLayout = view.findViewById(R.id.messageBox);
        sendFloatingActionButton = view.findViewById(R.id.sendButton);

        textToSpeech = new TextToSpeech(getActivity(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(Locale.US); // Puedes cambiar esto al idioma que prefieras
                }
            }
        });

        textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String utteranceId) {
                // El habla ha comenzado
            }

            @Override
            public void onDone(String utteranceId) {
                // El habla ha terminado
                if (getDialog() != null && getDialog().isShowing()) {
                    dismiss();
                }
            }

            @Override
            public void onError(String utteranceId) {
                // Hubo un error
                if (getDialog() != null && getDialog().isShowing()) {
                    dismiss();
                }
            }
        });

        sendFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editText = messageTextInputLayout.getEditText();
                if (editText != null) {
                    String messageContent = editText.getText().toString();
                    if (!messageContent.isEmpty()) {
                        boolean isSent = true;
                        Message newMessage = new Message(messageContent, isSent);
                        galleryViewModel.getMessages().add(newMessage);
                        messageAdapter.addMessage(newMessage);
                        try {
                            Detector detector = DetectorFactory.create();
                            detector.append(messageContent);
                            String language = detector.detect();
                            Log.d("El idioma fue: ", language);

                            if (language.equals("en")) {
                                textToSpeech.setLanguage(Locale.ENGLISH);
                            } else if (language.equals("es")) {
                                textToSpeech.setLanguage(new Locale("es", "ES"));
                            } else {
                                Toast.makeText(getContext(), "Idioma no soportado", Toast.LENGTH_SHORT).show();
                            }
                            textToSpeech.speak(messageContent, TextToSpeech.QUEUE_FLUSH, null, "MessageUtteranceId");
                        } catch (Exception e) {
                            Log.e("MessageDialogFragment", "Error al detectar el idioma del mensaje", e);
                            Toast.makeText(getContext(), "Ocurrió un problema al reproducir el mensaje", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // Maneja el caso en que el EditText está vacío
                        if (getDialog() != null && getDialog().isShowing()) {
                            dismiss();
                        }
                    }
                } else {
                    // Maneja el caso en que el EditText es nulo
                    if (getDialog() != null && getDialog().isShowing()) {
                        dismiss();
                    }
                }
                
            }
        });

        builder.setView(view);
        return builder.create();
    }

    public void setMessageAdapter(MessageAdapter messageAdapter) {
        this.messageAdapter = messageAdapter;
    }

    @Override
    public void onDestroy() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
    }

}
