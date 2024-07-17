package com.example.hablaconmigo.ui.gallery;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hablaconmigo.R;
import com.example.hablaconmigo.databinding.FragmentGalleryBinding;

import java.util.ArrayList;

public class GalleryFragment extends Fragment {

    private FragmentGalleryBinding binding;
    private Button writeButton;
    private AutoCompleteTextView autoCompleteTextViewLanguage;
    private MessageAdapter messageAdapter;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        GalleryViewModel galleryViewModel =
                new ViewModelProvider(this).get(GalleryViewModel.class);

        binding = FragmentGalleryBinding.inflate(inflater, container, false);

        RecyclerView recyclerView = binding.recyclerViewMensajes;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        messageAdapter = new MessageAdapter(galleryViewModel.getMessages());
        recyclerView.setAdapter(messageAdapter);

        writeButton = binding.btnEscribir;
        autoCompleteTextViewLanguage = binding.autoCompleteTextViewLanguage;

        writeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MessageDialogFragment messageDialog = new MessageDialogFragment();
                messageDialog.setMessageAdapter(messageAdapter);
                messageDialog.show(getParentFragmentManager(), "messageDialog");
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
    }

}