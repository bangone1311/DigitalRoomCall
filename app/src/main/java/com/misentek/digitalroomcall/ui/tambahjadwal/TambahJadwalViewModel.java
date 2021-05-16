package com.misentek.digitalroomcall.ui.tambahjadwal;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class TambahJadwalViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public TambahJadwalViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is slideshow fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}