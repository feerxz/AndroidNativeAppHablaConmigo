package com.example.hablaconmigo.ui.shortcuts;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.hablaconmigo.R;
import com.example.hablaconmigo.databinding.FragmentShortCutBinding;


public class ShortCutFragment extends Fragment {
    private FragmentShortCutBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ShortCutViewModel shortCutViewModel = new ViewModelProvider(this).get(ShortCutViewModel.class);
        binding = FragmentShortCutBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textShortcut;
        shortCutViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}