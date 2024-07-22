package com.example.hablaconmigo.ui.shortcuts;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hablaconmigo.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class ListShortCutsFragment extends Fragment {

    private TextView titleList;
    private ImageView editIconList;
    private FloatingActionButton fabAddList;
    private RecyclerView recyclerShortcuts;
    private FrasesAdapter adapter;
    private boolean isEditMode = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_short_cuts, container, false);

        titleList = view.findViewById(R.id.titleList);
        editIconList = view.findViewById(R.id.editIconList);
        fabAddList = view.findViewById(R.id.fabAddList);
        recyclerShortcuts = view.findViewById(R.id.recyclerShortCutList);

        // Configurar RecyclerView
        recyclerShortcuts.setLayoutManager(new LinearLayoutManager(getContext()));

        // Crear y configurar el adaptador
        List<String> frases = new ArrayList<>();
        frases.add("Frase de prueba 1");
        frases.add("Frase de prueba 2");
        frases.add("Frase de prueba 3");
        frases.add("Frase de prueba 4");
        frases.add("Frase de prueba 5");
        frases.add("Frase de prueba 6");
        frases.add("Frase de prueba 7");
        frases.add("Frase de prueba 8");
        frases.add("Frase de prueba 9");
        adapter = new FrasesAdapter(frases);
        recyclerShortcuts.setAdapter(adapter);

        // Obtener el título de la carpeta desde los argumentos
        if (getArguments() != null) {
            String carpetaTitulo = getArguments().getString("carpetaTitulo");
            titleList.setText(carpetaTitulo);
            requireActivity().setTitle(carpetaTitulo);
        } else {
            titleList.setText("Frases");
            requireActivity().setTitle("Frases");
        }

        editIconList.setOnClickListener(v -> {
            // Alternar modo de edición
            isEditMode = !isEditMode;
            adapter.setEditMode(isEditMode);
        });

        fabAddList.setOnClickListener(v -> {
            // Lógica para añadir un nuevo ítem
            Toast.makeText(getContext(), "En desarrollo", Toast.LENGTH_SHORT).show();
        });

        return view;
    }
}
