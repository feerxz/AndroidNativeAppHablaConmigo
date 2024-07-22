package com.example.hablaconmigo.ui.gallery;

import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

public class GalleryViewModel extends ViewModel {

    private final List<Message> messages = new ArrayList<>();
    private MessageAdapter messageAdapter;

    public List<Message> getMessages() {
        return messages;
    }

}