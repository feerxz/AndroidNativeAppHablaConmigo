package com.example.hablaconmigo.ui.shortcuts;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hablaconmigo.R;

import java.util.ArrayList;
import java.util.List;

public class FrasesAdapter extends RecyclerView.Adapter<FrasesAdapter.ViewHolder> {

    private List<String> frases;
    private boolean isEditMode = false;

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

        // Actualizar la visibilidad de deleteIcon según isEditMode
        holder.deleteIcon.setVisibility(isEditMode ? View.VISIBLE : View.GONE);

        holder.txtFrase.setOnClickListener(v -> {
            Toast.makeText(v.getContext(), "En desarrollo", Toast.LENGTH_SHORT).show();
        });

        holder.deleteIcon.setOnClickListener(v -> {
            // Lógica para eliminar el ítem
            frases.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, frases.size());
        });
    }

    @Override
    public int getItemCount() {
        // Retorna el número de ítems
        return frases.size();
    }

    public void setEditMode(boolean editMode) {
        isEditMode = editMode;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtFrase;
        ImageView deleteIcon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtFrase = itemView.findViewById(R.id.txtFrase);
            deleteIcon = itemView.findViewById(R.id.deleteIcon);
        }
    }
}
