package com.misentek.digitalroomcall.ui.gallery;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButtonToggleGroup;
import com.misentek.digitalroomcall.MainActivity;
import com.misentek.digitalroomcall.NadaAdpter;
import com.misentek.digitalroomcall.R;
import com.misentek.digitalroomcall.Ruangan;

import java.util.ArrayList;
import java.util.List;

import static com.misentek.digitalroomcall.MainActivity.activity;
import static com.misentek.digitalroomcall.MainActivity.currentActiveFragment;
import static com.misentek.digitalroomcall.MainActivity.mWebSocketClient;
import static com.misentek.digitalroomcall.MainActivity.materialButtonToggleGroup;
import static com.misentek.digitalroomcall.MainActivity.nadaAdpter;
import static com.misentek.digitalroomcall.MainActivity.nadaList;
import static com.misentek.digitalroomcall.MainActivity.progress;
import static com.misentek.digitalroomcall.MainActivity.recyclerViewNada;
import static com.misentek.digitalroomcall.MainActivity.recyclerViewRuangan;
import static com.misentek.digitalroomcall.MainActivity.sendMessage;
import static com.misentek.digitalroomcall.MainActivity.showProgress;
import static com.misentek.digitalroomcall.MainActivity.tryToConnect;
import static com.misentek.digitalroomcall.MainActivity.updateMenu;

public class GalleryFragment extends Fragment {

    private GalleryViewModel galleryViewModel;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        galleryViewModel =
                new ViewModelProvider(this).get(GalleryViewModel.class);
        View root = inflater.inflate(R.layout.fragment_gallery, container, false);
        currentActiveFragment="Bel Manual";
        recyclerViewRuangan=root.findViewById(R.id.recycler_view_ruangan);
        recyclerViewNada = root.findViewById(R.id.recycler_view);
        materialButtonToggleGroup = root.findViewById(R.id.toggleButtons);

        if (nadaList!=null){
            if (nadaList.size()>0){
                nadaAdpter = new NadaAdpter(nadaList);
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(activity);
                recyclerViewNada.setLayoutManager(layoutManager);
                recyclerViewNada.setAdapter(nadaAdpter);
                Log.d("fak", "ada list : " + nadaList.size());
                sendMessage("get_data_ruangan");
                sendMessage("getSpeakerState_");
            }
        }else{
            showProgress("Mengambil nada dari server");
            sendMessage("get_data_bel_manual");
        }

        updateMenu();
        galleryViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                //textView.setText(s);
            }
        });
        return root;
    }
}