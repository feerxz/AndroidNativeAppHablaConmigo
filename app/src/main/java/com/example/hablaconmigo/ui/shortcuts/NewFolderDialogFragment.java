package com.example.hablaconmigo.ui.shortcuts;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.hablaconmigo.R;
import com.example.hablaconmigo.entities.Carpeta;

public class NewFolderDialogFragment extends DialogFragment {

    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView imgUpload;
    private EditText inputFolderName;
    private Uri selectedImageUri;
    private ShortCutViewModel shortCutViewModel;

    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == getActivity().RESULT_OK && result.getData() != null) {
                    Uri imageUri = result.getData().getData();
                    imgUpload.setImageURI(imageUri); // Mostrar la imagen seleccionada en imgUpload
                    selectedImageUri = imageUri;
                }
            });
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflar el diseño para este fragmento
        View view = inflater.inflate(R.layout.fragment_new_folder, container, false);
        ImageView imgClose = view.findViewById(R.id.imgClose);
        Button btnSaveFolder = view.findViewById(R.id.btnSaveFolder);
        inputFolderName = view.findViewById(R.id.inputFolderName);
        imgUpload = view.findViewById(R.id.imgUpload);

        imgClose.setOnClickListener(v -> dismiss());

        FrameLayout imgSelectedContainer = view.findViewById(R.id.imgSelectedContainer);

        imgSelectedContainer.setOnClickListener(v -> openImageSelector());

        btnSaveFolder.setOnClickListener(v -> guardarCarpeta());

        return view;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setCanceledOnTouchOutside(false); // Prevenir que se cierre al tocar fuera
        return dialog;
    }

    public void show(FragmentManager fragmentManager) {
        show(fragmentManager, "NewFolderDialog");
    }

    private void openImageSelector() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }

    private void guardarCarpeta() {
        String folderName = inputFolderName.getText().toString().trim();

        if (folderName.isEmpty()) {
            Toast.makeText(getActivity(), "Asignar un título", Toast.LENGTH_SHORT).show();
            return;
        }

        String imagePath = selectedImageUri != null ? selectedImageUri.toString() : null;
        Carpeta nuevaCarpeta = new Carpeta(folderName, imagePath, 0); // Cambiar el valor de usuarioId si es necesario

        if (shortCutViewModel != null) {
            shortCutViewModel.agregarCarpeta(nuevaCarpeta);
        } else {
            Toast.makeText(getActivity(), "Error al guardar la carpeta", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(getActivity(), "Carpeta guardada con éxito", Toast.LENGTH_SHORT).show();

        // Cerrar el DialogFragment
        dismiss();
    }

    public void setShortCutViewModel(ShortCutViewModel viewModel) {
        this.shortCutViewModel = viewModel;
    }
}

