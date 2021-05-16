package com.misentek.digitalroomcall.ui.jadwal;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.misentek.digitalroomcall.R;
import com.misentek.digitalroomcall.RuanganAdapter;
import com.misentek.digitalroomcall.SetRuangan;

import java.util.ArrayList;

import static com.misentek.digitalroomcall.MainActivity.currentActiveFragment;
import static com.misentek.digitalroomcall.MainActivity.currentActiveJadwal;
import static com.misentek.digitalroomcall.MainActivity.mWebSocketClient;
import static com.misentek.digitalroomcall.MainActivity.progress;
import static com.misentek.digitalroomcall.MainActivity.recyclerViewJadwal;
import static com.misentek.digitalroomcall.MainActivity.sendMessage;
import static com.misentek.digitalroomcall.MainActivity.showProgress;
import static com.misentek.digitalroomcall.MainActivity.tryToConnect;
import static com.misentek.digitalroomcall.MainActivity.updateMenu;

public class JadwalFragment extends Fragment {

    public static RuanganAdapter ruanganAdapter;
    public static ArrayList<SetRuangan> setRuanganArrayList;
    private JadwalViewModel slideshowViewModel;
    private Button btnTambahJadwal;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        slideshowViewModel =
                new ViewModelProvider(this).get(JadwalViewModel.class);
        View root = inflater.inflate(R.layout.fragment_jadwal, container, false);
        recyclerViewJadwal = root.findViewById(R.id.recycler_view_jadwal);
        btnTambahJadwal = root.findViewById(R.id.btn_tambah_jadwal);
        currentActiveFragment="jadwal";
        btnTambahJadwal.setOnClickListener(view -> {
            currentActiveJadwal="";
            Navigation.findNavController(root).navigate(R.id.nav_tambah_jadwal);
        });
        showProgress("Mengambil jadwal");
        sendMessage("get_jadwal");
        updateMenu();
        return root;
    }
}