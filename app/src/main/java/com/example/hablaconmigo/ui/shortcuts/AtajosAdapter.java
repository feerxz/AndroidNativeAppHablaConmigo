package com.example.hablaconmigo.ui.shortcuts;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import com.example.hablaconmigo.R;
import com.example.hablaconmigo.entities.Carpeta;

import java.util.ArrayList;
import java.util.List;

public class AtajosAdapter extends RecyclerView.Adapter<AtajosAdapter.CarpetaViewHolder> {
    private List<Carpeta> listaCarpetas;
    private List<Carpeta> listaCarpetasFiltradas;

    private OnItemClickListener onItemClickListener;
    private boolean editMode = false;

    public AtajosAdapter(List<Carpeta> listaCarpetas, OnItemClickListener onItemClickListener) {
        this.listaCarpetas = new ArrayList<>(listaCarpetas);
        this.listaCarpetasFiltradas = new ArrayList<>(listaCarpetas);
        this.onItemClickListener = onItemClickListener;
    }

    public void setCarpetas(List<Carpeta> carpetas) {
        this.listaCarpetas = new ArrayList<>(carpetas);
        this.listaCarpetasFiltradas = new ArrayList<>(carpetas);
        notifyDataSetChanged();
    }

    public void setEditMode(boolean editMode) {
        this.editMode = editMode;
        notifyDataSetChanged();
    }

    public void filtrar(String query) {
        listaCarpetasFiltradas.clear();
        if (query.isEmpty()) {
            listaCarpetasFiltradas.addAll(listaCarpetas);
        } else {
            String lowerCaseQuery = query.toLowerCase();
            for (Carpeta carpeta : listaCarpetas) {
                if (carpeta.getNombreCarpeta().toLowerCase().contains(lowerCaseQuery)) {
                    listaCarpetasFiltradas.add(carpeta);
                }
            }
        }
        notifyDataSetChanged();
    }

    public boolean isEditMode() {
        return editMode;
    }

    @NonNull
    @Override
    public CarpetaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_folder, parent, false);
        return new CarpetaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CarpetaViewHolder holder, int position) {
        Carpeta carpeta = listaCarpetasFiltradas.get(position);
        holder.carpetaTitle.setText(carpeta.getNombreCarpeta());
        holder.deleteIcon.setVisibility(editMode ? View.VISIBLE : View.GONE);



        holder.itemView.setOnClickListener(v -> {
            if (!editMode && onItemClickListener != null) {
                onItemClickListener.onItemClick(carpeta, v);
            }
        });

        holder.deleteIcon.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onDeleteClick(carpeta, position);
            }
        });

        try {
            Uri uri = Uri.parse(carpeta.getImagen());
            holder.carpetaImg.setImageURI(uri);
        } catch (Exception e) {
            holder.carpetaImg.setImageResource(R.drawable.ic_folder);  // Imagen por defecto si ocurre un error
        }
    }

    @Override
    public int getItemCount() {
        return listaCarpetasFiltradas.size();
    }


    public void eliminarCarpeta(int position) {
        if (position >= 0 && position < listaCarpetas.size()) {
            Carpeta carpetaEliminada = listaCarpetas.get(position);
            listaCarpetas.remove(position);
            listaCarpetasFiltradas.remove(carpetaEliminada);
            notifyItemRemoved(position);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Carpeta carpeta, View view);
        void onDeleteClick(Carpeta carpeta, int position);
    }


    public static class CarpetaViewHolder extends RecyclerView.ViewHolder {
        TextView carpetaTitle;
        ImageView carpetaImg;
        ImageView deleteIcon;

        public CarpetaViewHolder(@NonNull View itemView) {
            super(itemView);
            carpetaTitle = itemView.findViewById(R.id.carpetaTitle);
            carpetaImg = itemView.findViewById(R.id.carpetaImg);
            deleteIcon = itemView.findViewById(R.id.deleteIcon);
        }
    }
}
