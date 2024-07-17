package com.example.hablaconmigo.ui.gallery;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hablaconmigo.R;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private List<Message> messages;

    public MessageAdapter(List<Message> messages) {
        this.messages = messages;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message message = messages.get(position);
        holder.bind(message);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public void addMessage(Message message) {
        messages.add(message);
        notifyItemInserted(messages.size() - 1);
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder {

        TextView messageTextView;
        TextView senderTextView;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            messageTextView = itemView.findViewById(R.id.messageTextView);
            senderTextView = itemView.findViewById(R.id.senderTextView);
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
}
