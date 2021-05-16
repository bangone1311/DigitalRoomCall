package com.misentek.digitalroomcall.ui.ruangan;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class RuanganViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public RuanganViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is slideshow fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}