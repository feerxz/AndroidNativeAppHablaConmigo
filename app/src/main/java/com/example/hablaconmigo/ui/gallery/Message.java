package com.example.hablaconmigo.ui.gallery;

public class Message {
    private String contenido;
    private boolean isSent;
    private String translation;

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

    public String getTranslation() {
        return translation;
    }

    public void setTranslation(String translation) {
        this.translation = translation;
    }
}
