package com.example.hablaconmigo.ui.shortcuts;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import com.example.hablaconmigo.databinding.FragmentShortCutBinding;

import java.util.ArrayList;

public class ShortCutFragment extends Fragment {
    private FragmentShortCutBinding binding;
    private AtajosAdapter adapter;
    private ShortCutViewModel shortCutViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        shortCutViewModel = new ViewModelProvider(this).get(ShortCutViewModel.class);
        binding = FragmentShortCutBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Configurar RecyclerView
        adapter = new AtajosAdapter(new ArrayList<>());
        RecyclerView recyclerView = binding.recyclerShortcut;
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerView.setAdapter(adapter);

        // Observar cambios en los datos del ViewModel
        shortCutViewModel.getCarpetasLiveData().observe(getViewLifecycleOwner(), listaCarpetas -> {
            adapter.setCarpetas(listaCarpetas);
            adapter.notifyDataSetChanged();
        });

        // Configurar SearchView
        SearchView searchView = binding.searchView;
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Filtrar datos (pendiente de implementación)
                return false;
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
