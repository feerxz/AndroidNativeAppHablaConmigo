package com.example.hablaconmigo.ui.gallery;

public class Message {
    private String contenido;
    private boolean isSent;

    public Message(String contenido, boolean isSent) {
        this.contenido = contenido;
        this.isSent = isSent;
    }

    public String getContenido() {
        return contenido;
    }

    public boolean isSent() {
        return isSent;
    }
}
