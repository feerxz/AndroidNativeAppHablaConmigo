package com.example.hablaconmigo.ui.shortcuts;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ShortCutViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public ShortCutViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is shortcuts fragment");
    }

    public MutableLiveData<String> getText() {
        return mText;
    }
}
