package com.example.hablaconmigo.ui.shortcuts;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.hablaconmigo.R;
import java.util.List;

public class AtajosAdapter extends RecyclerView.Adapter<AtajosAdapter.CarpetaViewHolder> {
    private List<Carpeta> listaCarpetas;

    public AtajosAdapter(List<Carpeta> listaCarpetas) {
        this.listaCarpetas = listaCarpetas;
    }

    public void setCarpetas(List<Carpeta> carpetas) {
        this.listaCarpetas = carpetas;
    }

    @NonNull
    @Override
    public CarpetaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_folder, parent, false);
        return new CarpetaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CarpetaViewHolder holder, int position) {
        Carpeta carpeta = listaCarpetas.get(position);
        holder.carpetaTitle.setText(carpeta.getTitulo());
        holder.carpetaImg.setImageResource(carpeta.getImagen());
    }

    @Override
    public int getItemCount() {
        return listaCarpetas.size();
    }

    public static class CarpetaViewHolder extends RecyclerView.ViewHolder {
        TextView carpetaTitle;
        ImageView carpetaImg;

        public CarpetaViewHolder(@NonNull View itemView) {
            super(itemView);
            carpetaTitle = itemView.findViewById(R.id.carpetaTitle);
            carpetaImg = itemView.findViewById(R.id.carpetaImg);
        }
    }

    public static class Carpeta {
        private String titulo;
        private int imagen;

        public Carpeta(String titulo, int imagen) {
            this.titulo = titulo;
            this.imagen = imagen;
        }

        public String getTitulo() {
            return titulo;
        }

        public int getImagen() {
            return imagen;
        }
    }
}
