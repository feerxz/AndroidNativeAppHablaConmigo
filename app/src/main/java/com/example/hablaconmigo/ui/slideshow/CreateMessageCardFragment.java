package com.example.hablaconmigo.ui.slideshow;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hablaconmigo.R;
import com.example.hablaconmigo.Utils;
import com.example.hablaconmigo.databinding.FragmentCreateMessageCardBinding;
import com.example.hablaconmigo.entities.Tarjeta;
import com.google.android.material.textfield.TextInputLayout;

import java.io.File;
import java.io.IOException;


public class CreateMessageCardFragment extends Fragment {

    private SlideshowViewModel viewModel;

    FragmentCreateMessageCardBinding binding;
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private static final int REQUEST_WRITE_STORAGE_PERMISSION = 201;
    private static final int REQUEST_READ_EXTERNAL_STORAGE_PERMISSION = 202;
    private static final int REQUEST_IMAGE_PICK = 202;
    private ImageButton closeCreateMessageCardFragment;
    private TextView nombreCarpetaActual;
    private TextInputLayout textInputLayoutNombreTarjeta;
    private TextInputLayout textInputLayoutFraseTarjeta;
    private ImageButton grabarAudioTarjeta;
    private MediaRecorder recorder;
    private String audioFilePath;
    private String imagePath;
    private ImageButton seleccionarImagenTarjeta;
    private Button guardarTarjeta;

    public CreateMessageCardFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(SlideshowViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentCreateMessageCardBinding.inflate(inflater, container, false);
        closeCreateMessageCardFragment = binding.imgButtonCloseAddCardFragment;
        nombreCarpetaActual = binding.txtvNombreCarpetaContenedora;
        textInputLayoutNombreTarjeta = binding.textInputLayoutTituloTarjeta;
        textInputLayoutFraseTarjeta = binding.textinputLayoutFraseTarjeta;
        guardarTarjeta = binding.btnGuardarNuevaTarjeta;
        grabarAudioTarjeta = binding.imgBtnGrabarAudio;
        seleccionarImagenTarjeta = binding.imageBtnCargarImagenTarjeta;

        String folderName = getArguments().getString("folderName");
        long userId = getArguments().getLong("userId");
        long folderId = getArguments().getLong("folderId");
        nombreCarpetaActual.setText(folderName);

        closeCreateMessageCardFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Cierra el fragmento
                requireActivity().getSupportFragmentManager().popBackStack();
            }
        });

        grabarAudioTarjeta.setOnClickListener(new View.OnClickListener() {
            boolean isRecording = false;
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED ||
                        ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    // Si no se han concedido los permisos, se los solicita
                    requestPermissions();
                } else {
                    // Si los permisos ya han sido concedidos, se procede a grabar
                    if (isRecording) {
                        stopRecording();
                        isRecording = false;
                        grabarAudioTarjeta.setImageResource(R.drawable.voice_recorder_off); // Cambia al icono de micrófono inactivo
                    } else {
                        startRecording();
                        isRecording = true;
                        grabarAudioTarjeta.setImageResource(R.drawable.voice_recorder); // Cambia al icono de micrófono activo
                    }
                }
            }
        });

        seleccionarImagenTarjeta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQUEST_IMAGE_PICK);
            }
        });

        guardarTarjeta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Comprobamos si los campos están vacíos
                String nombreTarjeta = "";
                String fraseTarjeta = "";
                if (textInputLayoutNombreTarjeta.getEditText() != null) {
                    nombreTarjeta = textInputLayoutNombreTarjeta.getEditText().getText().toString();
                }
                if (textInputLayoutFraseTarjeta.getEditText() != null) {
                    fraseTarjeta = textInputLayoutFraseTarjeta.getEditText().getText().toString();
                }

                if (nombreTarjeta.isEmpty()) {
                    textInputLayoutNombreTarjeta.setError("Completa este campo");
                    return;
                } else if (fraseTarjeta.isEmpty()) {
                    textInputLayoutFraseTarjeta.setError("Completa este campo");
                    return;
                } else {
                    textInputLayoutNombreTarjeta.setError(null);
                    textInputLayoutFraseTarjeta.setError(null);
                    // Se crea un objeto Tarjeta con los datos introducidos
                    Tarjeta tarjeta = new Tarjeta(nombreTarjeta, fraseTarjeta, imagePath == null ? "default_card_image" : imagePath, audioFilePath == null ? null : audioFilePath, userId, folderId);
                    // Se inserta la tarjeta en la base de datos
                    viewModel.createTarjeta(tarjeta);
                    Utils.showSnackBarSuccess(v, "Tarjeta creada correctamente");
                    // Se cierra el fragmento
                    requireActivity().getSupportFragmentManager().popBackStack();
                }
            }
        });
        return binding.getRoot();
    }
    private void requestPermissions() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_RECORD_AUDIO_PERMISSION);
        }

        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_STORAGE_PERMISSION);
        }

        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_READ_EXTERNAL_STORAGE_PERMISSION);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_RECORD_AUDIO_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permiso de grabación de audio concedido
                    Toast.makeText(getContext(), "Permiso de grabación de audio concedido.", Toast.LENGTH_SHORT).show();
                } else {
                    // Permiso de grabación de audio denegado
                    Toast.makeText(getContext(), "Permiso de grabación de audio necesario para grabar audio.", Toast.LENGTH_SHORT).show();
                }
                break;
            case REQUEST_WRITE_STORAGE_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permiso de escritura en almacenamiento concedido
                    Toast.makeText(getContext(), "Permiso de escritura en almacenamiento concedido.", Toast.LENGTH_SHORT).show();
                } else {
                    // Permiso de escritura en almacenamiento denegado
                    Toast.makeText(getContext(), "Permiso de escritura en almacenamiento necesario para guardar el audio.", Toast.LENGTH_SHORT).show();
                }
                break;
            case REQUEST_READ_EXTERNAL_STORAGE_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permiso de lectura en almacenamiento externo concedido
                    Toast.makeText(getContext(), "Permiso de lectura en almacenamiento externo concedido.", Toast.LENGTH_SHORT).show();
                } else {
                    // Permiso de lectura en almacenamiento externo denegado
                    Toast.makeText(getContext(), "Permiso de lectura en almacenamiento externo necesario para seleccionar una imagen.", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_PICK && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            if (selectedImageUri != null) {
                imagePath = getPathFromUri(selectedImageUri);
                seleccionarImagenTarjeta.setImageURI(selectedImageUri);

            }
        }
    }
    private String getPathFromUri(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = getActivity().getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String path = cursor.getString(column_index);
            cursor.close();
            return path;
        }
        return null;
    }
    public void startRecording() {
        // Crea un nuevo MediaRecorder
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        // Se crea un archivo en el almacenamiento interno para guardar el audio
        // Se usa la marca de tiempo actual en milisegundos como parte del nombre del archivo
        String timeStamp = String.valueOf(System.currentTimeMillis());
        File audioFile = new File(getActivity().getFilesDir(), "audio_" + timeStamp + ".3gp");
        audioFilePath = audioFile.getAbsolutePath();
        recorder.setOutputFile(audioFilePath);

        try {
            recorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Inicia la grabación
        recorder.start();
    }

    public void stopRecording() {
        // Se detiene la grabación y se liberan los recursos
        recorder.stop();
        recorder.release();
        recorder = null;
    }
}