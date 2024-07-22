package com.example.hablaconmigo.ui.slideshow;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hablaconmigo.databinding.FragmentSlideshowBinding;
import com.example.hablaconmigo.entities.Carpeta;

import java.util.List;

public class SlideshowFragment extends Fragment {

    private FragmentSlideshowBinding binding;
    private long userId;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        SlideshowViewModel slideshowViewModel =
                new ViewModelProvider(this).get(SlideshowViewModel.class);
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("UserSessionPreferences", Context.MODE_PRIVATE);
        userId = sharedPreferences.getLong("userId", -1);

        binding = FragmentSlideshowBinding.inflate(inflater, container, false);
        RecyclerView recyclerView = binding.recyclerViewContenedorCarpetas;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        if (userId != -1) {
            slideshowViewModel.getCarpetasByUserId(userId).observe(getViewLifecycleOwner(), carpetas -> {
                recyclerView.setAdapter(new FolderAdapter(carpetas, slideshowViewModel, getViewLifecycleOwner()));
            });
        }

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}