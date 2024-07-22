package com.example.hablaconmigo.ui.slideshow;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.ViewGroup;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.hablaconmigo.R;
import com.example.hablaconmigo.Utils;
import com.example.hablaconmigo.entities.Tarjeta;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MessageCardAdapter extends RecyclerView.Adapter<MessageCardAdapter.MessageCardViewHolder> {
    private List<Tarjeta> tarjetas;
    private List<MessageCardViewHolder> viewHolders = new ArrayList<>(); // Lista para mantener los ViewHolder
    private boolean isDeleteVisible = false; // Nuevo estado para la visibilidad del botón de eliminar
    private SlideshowViewModel viewModel;
    public MessageCardAdapter(List<Tarjeta> tarjetas, SlideshowViewModel viewModel) {
        this.tarjetas = tarjetas;
        this.viewModel = viewModel;
    }

    @Override
    public MessageCardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tarjeta, parent, false);
        MessageCardViewHolder viewHolder = new MessageCardViewHolder(view);
        viewHolders.add(viewHolder); // Agrega el ViewHolder a la lista
        return new MessageCardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MessageCardViewHolder holder, int position) {
        Tarjeta tarjeta = tarjetas.get(position);
        holder.setViewModel(viewModel);
        holder.setTarjeta(tarjeta);
        holder.tarjetaTitle.setText(tarjeta.getNombreTarjeta());
        String imagen = tarjeta.getImagen();
        int resourceId = holder.tarjetaImage.getContext().getResources().getIdentifier(imagen, "drawable", holder.tarjetaImage.getContext().getPackageName());
        if (resourceId != 0) {
            // La imagen es un recurso drawable
            Glide.with(holder.tarjetaImage.getContext())
                    .load(resourceId)
                    .centerCrop()
                    .into(holder.tarjetaImage);
        } else {
            // La imagen es un archivo en el almacenamiento interno
            Glide.with(holder.tarjetaImage.getContext())
                    .load(new File(imagen))
                    .centerCrop()
                    .into(holder.tarjetaImage);
        }
        holder.deleteButton.setVisibility(isDeleteVisible ? View.VISIBLE : View.GONE);
    }

    @Override
    public int getItemCount() {
        return tarjetas.size();
    }

    static class MessageCardViewHolder extends RecyclerView.ViewHolder {
        TextView tarjetaTitle;
        ImageView tarjetaImage;
        ImageButton deleteButton;
        SlideshowViewModel viewModel;
        Tarjeta tarjeta;

        public MessageCardViewHolder(View itemView) {
            super(itemView);
            tarjetaTitle = itemView.findViewById(R.id.txtvTituloTarjeta);
            tarjetaImage = itemView.findViewById(R.id.imageViewImagenTarjeta);
            deleteButton = itemView.findViewById(R.id.imageButtonDeleteIcon);

            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(v.getContext())
                            .setTitle("Confirmar eliminación")
                            .setMessage("¿Estás seguro de que quieres eliminar esta tarjeta?")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // Continúa con la eliminación de la tarjeta
                                    long tarjetaId = tarjeta.getId();
                                    viewModel.deleteTarjeta(tarjetaId);
                                    deleteButton.setVisibility(View.GONE);
                                    Utils.showSnackBarSuccess(v, "Tarjeta eliminada correctamente");
                                }
                            })
                            .setNegativeButton(android.R.string.no, null)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }
            });
        }
        public void setViewModel(SlideshowViewModel viewModel) {
            this.viewModel = viewModel;
        }

        public void setTarjeta(Tarjeta tarjeta) {
            this.tarjeta = tarjeta;
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void toggleDeleteButtonsVisibility() {
        isDeleteVisible = !isDeleteVisible; // Cambia el estado de la visibilidad del botón de eliminar
        notifyDataSetChanged(); // Notifica al adaptador que los datos han cambiado para que se vuelva a llamar a onBindViewHolder
    }



}
