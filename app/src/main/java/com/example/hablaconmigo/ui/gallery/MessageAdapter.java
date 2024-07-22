package com.example.hablaconmigo.ui.gallery;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.cybozu.labs.langdetect.Detector;
import com.cybozu.labs.langdetect.DetectorFactory;
import com.example.hablaconmigo.R;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private List<Message> messages;
    private TextToSpeech textToSpeech;
    private Context context;
    public MessageAdapter(Context context, List<Message> messages) {
        this.messages = messages;
        this.context = context;
        textToSpeech = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(Locale.getDefault());
                }
            }
        });
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == 0) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_received, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, parent, false);
        }
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message message = messages.get(position);
        holder.bind(message);
        holder.messageTextView.setText(message.getContenido());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Detector detector = DetectorFactory.create();
                    detector.append(message.getContenido());
                    String language = detector.detect();

                    Locale locale = Locale.forLanguageTag(language);
                    if (textToSpeech.isLanguageAvailable(locale) != TextToSpeech.LANG_NOT_SUPPORTED) {
                        textToSpeech.setLanguage(locale);
                        textToSpeech.speak(message.getContenido(), TextToSpeech.QUEUE_FLUSH, null, null);
                    } else {
                        Toast.makeText(context, "Idioma no soportado", Toast.LENGTH_SHORT).show();
                        textToSpeech.setLanguage(new Locale("es", "ES"));
                        textToSpeech.speak("No se pudo reproducir el mensaje", TextToSpeech.QUEUE_FLUSH, null, null);
                    }
                } catch (Exception e) {
                    Toast.makeText(context, "Ocurrió un problema al reproducir el mensaje", Toast.LENGTH_SHORT).show();
                    Log.e("MessageAdapter", "Error al detectar el idioma", e);
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public void addMessage(Message message) {
        messages.add(message);
        notifyItemInserted(messages.size() - 1);
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messages.get(position);
        // Aquí se decide qué tipo de vista usar en función del tipo de mensaje
        if (message.isSent()) {
            return 1;
        } else {
            return 0;
        }
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder {

        TextView messageTextView;
        //TextView translationTextView;
        TextView senderTextView;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            messageTextView = itemView.findViewById(R.id.messageTextView);
            senderTextView = itemView.findViewById(R.id.senderTextView);
            //translationTextView = itemView.findViewById(R.id.txtViewTraduccion);
        }

        public void bind(Message message) {
            messageTextView.setText(message.getContenido());
            // Aquíi cambiar la alineación del texto dependiendo de si el mensaje fue enviado o recibido
            if (message.isSent()) {
                messageTextView.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
                senderTextView.setText(R.string.remitente_tu);
            } else {
                messageTextView.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
                senderTextView.setText(R.string.remitente_otro);
            }
        }
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
    }


    @SuppressLint("NotifyDataSetChanged")
    public void clearMessages() {
        this.messages.clear();
        notifyDataSetChanged();
    }

}
