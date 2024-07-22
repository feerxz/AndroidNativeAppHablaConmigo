package com.example.hablaconmigo.ui.slideshow;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.lifecycle.LifecycleOwner;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hablaconmigo.R;
import com.example.hablaconmigo.entities.Carpeta;

import java.util.List;

public class FolderAdapter extends RecyclerView.Adapter<FolderAdapter.FolderViewHolder> {
    private List<Carpeta> carpetas;
    private SlideshowViewModel slideshowViewModel;
    private LifecycleOwner lifecycleOwner;

    public FolderAdapter(List<Carpeta> carpetas, SlideshowViewModel slideshowViewModel, LifecycleOwner lifecycleOwner) {
        this.carpetas = carpetas;
        this.slideshowViewModel = slideshowViewModel;
        this.lifecycleOwner = lifecycleOwner;
    }

    @Override
    public FolderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_carpeta, parent, false);
        return new FolderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FolderViewHolder holder, int position) {
        Carpeta carpeta = carpetas.get(position);
        slideshowViewModel.getTarjetasByFolderId(carpeta.getId()).observe(lifecycleOwner, tarjetas -> {
            holder.tarjetasRecyclerView.setLayoutManager(new LinearLayoutManager(holder.tarjetasRecyclerView.getContext(), LinearLayoutManager.HORIZONTAL, false));
            holder.nombreCarpeta.setText(carpeta.getNombreCarpeta());
            holder.messageCardAdapter = new MessageCardAdapter(tarjetas, slideshowViewModel);
            holder.tarjetasRecyclerView.setAdapter(holder.messageCardAdapter);
        });


        holder.editarCarpeta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MessageCardAdapter adapter = (MessageCardAdapter) holder.tarjetasRecyclerView.getAdapter();
                if (adapter != null) {
                    adapter.toggleDeleteButtonsVisibility();
                }
            }
        });

        holder.createTarjeta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = v.getContext().getSharedPreferences("UserSessionPreferences", v.getContext().MODE_PRIVATE);
                long userId = sharedPreferences.getLong("userId", -1);
                // Se crea un Bundle para pasar el nombre de la carpeta, el id de la carpeta y el id del usuario
                Bundle bundle = new Bundle();
                bundle.putString("folderName", holder.nombreCarpeta.getText().toString());
                bundle.putLong("folderId", carpeta.getId());
                bundle.putLong("userId", userId);
                // Se navega al nuevo fragmento para crear una nueva tarjeta
                Navigation.findNavController(v).navigate(R.id.createMessageCardFragment, bundle);
            }
        });

    }

    @Override
    public int getItemCount() {
        return carpetas.size();
    }

    static class FolderViewHolder extends RecyclerView.ViewHolder{
        RecyclerView tarjetasRecyclerView;
        TextView nombreCarpeta;
        ImageButton editarCarpeta;
        ImageButton createTarjeta;
        MessageCardAdapter messageCardAdapter;

        public FolderViewHolder(View itemView) {
            super(itemView);
            nombreCarpeta = itemView.findViewById(R.id.textViewNombreCarpeta);
            tarjetasRecyclerView = itemView.findViewById(R.id.recyclerViewTarjetas);
            editarCarpeta = itemView.findViewById(R.id.imageButtonEditCarpeta);
            createTarjeta = itemView.findViewById(R.id.imageBtnCreateCard);
        }
    }
}
