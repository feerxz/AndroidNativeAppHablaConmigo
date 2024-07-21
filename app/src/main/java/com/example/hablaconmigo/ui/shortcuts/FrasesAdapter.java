package com.example.hablaconmigo.ui.shortcuts;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hablaconmigo.R;

import java.util.ArrayList;
import java.util.List;

public class FrasesAdapter extends RecyclerView.Adapter<FrasesAdapter.ViewHolder> {

    private List<String> frases;



    public FrasesAdapter(List<String> frases) {
        // Asegúrate de que la lista no sea null
        this.frases = frases != null ? frases : new ArrayList<>();
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_frase, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Configurar el contenido del ítem aquí
        String frase = frases.get(position);
        holder.txtFrase.setText(frase);

        holder.txtFrase.setOnClickListener(v -> {
            Toast.makeText(v.getContext(), "En desarrollo", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        // Retorna el número de ítems
        return frases.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtFrase;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtFrase = itemView.findViewById(R.id.txtFrase);
        }
    }
}

