package com.misentek.digitalroomcall.ui.nada;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class NadaViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public NadaViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is nada fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}