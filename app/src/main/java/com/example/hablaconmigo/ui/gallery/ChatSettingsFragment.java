package com.example.hablaconmigo.ui.gallery;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.Toast;

import com.example.hablaconmigo.R;
import com.example.hablaconmigo.databinding.FragmentChatSettingsBinding;
import com.google.android.material.materialswitch.MaterialSwitch;


public class ChatSettingsFragment extends Fragment {

    FragmentChatSettingsBinding binding;

    public ChatSettingsFragment() {
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
        binding = FragmentChatSettingsBinding.inflate(inflater, container, false);
        SeekBar seekBar = binding.seekBarVekReproduccion;
        MaterialSwitch switchReproducirTraduccion = binding.switchRepTraduccion;

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Toast.makeText(getContext(), "Esta funcionalidad aún está en desarrollo", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        switchReproducirTraduccion.setOnCheckedChangeListener(new MaterialSwitch.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Muestra un Toast con un mensaje
                Toast.makeText(getContext(), "Esta funcionalidad aún está en desarrollo", Toast.LENGTH_SHORT).show();
            }
        });

        return binding.getRoot();
    }

}