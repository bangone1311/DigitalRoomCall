package com.misentek.digitalroomcall.ui.home;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.misentek.digitalroomcall.Jadwal;
import com.misentek.digitalroomcall.JadwalAdapter;
import com.misentek.digitalroomcall.MainActivity;
import com.misentek.digitalroomcall.R;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.Handshakedata;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import static com.misentek.digitalroomcall.MainActivity.activity;
import static com.misentek.digitalroomcall.MainActivity.btnSemuaRuangan;
import static com.misentek.digitalroomcall.MainActivity.connectWebSocket;
import static com.misentek.digitalroomcall.MainActivity.currentActiveFragment;
import static com.misentek.digitalroomcall.MainActivity.currentHari;
import static com.misentek.digitalroomcall.MainActivity.currentHariF;
import static com.misentek.digitalroomcall.MainActivity.mWebSocketClient;
import static com.misentek.digitalroomcall.MainActivity.materialButtonToggleGroup;
import static com.misentek.digitalroomcall.MainActivity.materialButtonToggleGroupHari;
import static com.misentek.digitalroomcall.MainActivity.progress;
import static com.misentek.digitalroomcall.MainActivity.recyclerViewJadwal;
import static com.misentek.digitalroomcall.MainActivity.sendMessage;
import static com.misentek.digitalroomcall.MainActivity.showProgress;
import static com.misentek.digitalroomcall.MainActivity.tryToConnect;
import static com.misentek.digitalroomcall.MainActivity.txtStatus;
import static com.misentek.digitalroomcall.MainActivity.updateMenu;

public class HomeFragment extends Fragment {
    public static JadwalAdapter jadwalAdapter;
    public static ArrayList<Jadwal> jadwalArrayList;
    static TextView txtEmpty;
    private HomeViewModel homeViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        recyclerViewJadwal = root.findViewById(R.id.recycler_view);
        materialButtonToggleGroupHari = root.findViewById(R.id.toggleButtonHari);
        txtEmpty = root.findViewById(R.id.txt_empty);
        currentActiveFragment="home";
        showProgress("Mengambil data jadwal");
        sendMessage("get_jadwal");
        updateMenu();

        //final TextView textView = root.findViewById(R.id.text_home);
        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                //textView.setText(s);
            }
        });
        return root;
    }

    public static void callBack(){
        if (currentActiveFragment.equals("home")){
            ArrayList<View> viewArrayList=new ArrayList<>();
            materialButtonToggleGroupHari.findViewsWithText(viewArrayList,currentHari,View.FIND_VIEWS_WITH_TEXT);
            if (viewArrayList.size()>0){
                materialButtonToggleGroupHari.check(viewArrayList.get(0).getId());
            }
        }
        materialButtonToggleGroupHari.addOnButtonCheckedListener(new MaterialButtonToggleGroup.OnButtonCheckedListener() {
            @Override
            public void onButtonChecked(MaterialButtonToggleGroup group, int checkedId, boolean isChecked) {
                ArrayList<Jadwal> jadwalArrayList1=new ArrayList<>();
                int id = materialButtonToggleGroupHari.getCheckedButtonId();
                Button button = materialButtonToggleGroupHari.findViewById(id);
                currentHariF = button.getText().toString();
                //Log.d("fak",currentHari);
                for (int i=0;i<jadwalArrayList.size();i++){
                    if (jadwalArrayList.get(i).getHari().toLowerCase().contains(currentHariF.toLowerCase())){
                        jadwalArrayList1.add(jadwalArrayList.get(i));
                    }
                }
                if (jadwalArrayList1.size()>0){
                    Collections.sort(jadwalArrayList1, new Comparator<Jadwal>() {
                        @Override
                        public int compare(Jadwal lhs, Jadwal rhs) {
                            return lhs.getJam().compareTo(rhs.getJam());
                        }
                    });
                    jadwalAdapter = new JadwalAdapter(jadwalArrayList1);
                    recyclerViewJadwal.setAdapter(jadwalAdapter);

                    recyclerViewJadwal.setVisibility(View.VISIBLE);
                    txtEmpty.setVisibility(View.GONE);
                }else{
                    recyclerViewJadwal.setVisibility(View.GONE);
                    txtEmpty.setVisibility(View.VISIBLE);
                }
            }
        });
    }
}