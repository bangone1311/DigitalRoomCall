package com.misentek.digitalroomcall.ui.ruangan;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.misentek.digitalroomcall.R;
import com.misentek.digitalroomcall.RuanganAdapter;
import com.misentek.digitalroomcall.SetRuangan;

import java.util.ArrayList;

import static com.misentek.digitalroomcall.MainActivity.activity;
import static com.misentek.digitalroomcall.MainActivity.btnBatalRuangan;
import static com.misentek.digitalroomcall.MainActivity.btnSimpanRuangan;
import static com.misentek.digitalroomcall.MainActivity.currentActiveButton;
import static com.misentek.digitalroomcall.MainActivity.currentActiveFragment;
import static com.misentek.digitalroomcall.MainActivity.initRuangan;
import static com.misentek.digitalroomcall.MainActivity.mWebSocketClient;
import static com.misentek.digitalroomcall.MainActivity.materialButtonToggleGroup;
import static com.misentek.digitalroomcall.MainActivity.progress;
import static com.misentek.digitalroomcall.MainActivity.recyclerViewRuangan;
import static com.misentek.digitalroomcall.MainActivity.ruanganArrayList;
import static com.misentek.digitalroomcall.MainActivity.sendMessage;
import static com.misentek.digitalroomcall.MainActivity.setRuanganArrayList;
import static com.misentek.digitalroomcall.MainActivity.showProgress;
import static com.misentek.digitalroomcall.MainActivity.tryToConnect;
import static com.misentek.digitalroomcall.MainActivity.txtStatus;
import static com.misentek.digitalroomcall.MainActivity.updateMenu;

public class RuanganFragment extends Fragment {

    public static RuanganAdapter ruanganAdapter;
    private RuanganViewModel slideshowViewModel;
    public static Button btnHapusRuangan;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        slideshowViewModel =
                new ViewModelProvider(this).get(RuanganViewModel.class);
        View root = inflater.inflate(R.layout.fragment_ruangan, container, false);
        currentActiveFragment="ruangan";
        recyclerViewRuangan = root.findViewById(R.id.recycler_view_ruangan);
        btnHapusRuangan = root.findViewById(R.id.btn_hapus_ruangan);
        materialButtonToggleGroup = root.findViewById(R.id.toggleButtons);
        btnSimpanRuangan = root.findViewById(R.id.btn_simpan_ruangan);
        btnBatalRuangan = root.findViewById(R.id.btn_batal_ruangan);
        updateMenu();
        initRuangan(false);
        btnHapusRuangan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog dialog = new AlertDialog.Builder(activity)
                        .setTitle("Hapus Ruangan")
                        .setMessage("Anda yakin ingin menghapus ruangan ini?")
                        .setPositiveButton("Hapus", (dialog1, which) -> {
                            sendMessage("deleteRooms_"+currentActiveButton);
                        })
                        .setNegativeButton("Cancel", null)
                        .create();
                dialog.show();
            }
        });
        btnSimpanRuangan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnSimpanRuangan.requestFocus();
                String str="";
                for (int i=0;i<32;i++){
                    String state="_off";
                    if (ruanganArrayList.get(i).getSelected()){
                        state="_on";
                    }
                    str+="speaker_"+i+state+",";
                }
                String finalStr = str.substring(0,str.length()-1);
                //Log.d("fak", currentActiveButton);
                if (currentActiveButton.equals("Semua Ruangan")){
                    //Log.d("fak", "aktif " + currentActiveButton);
                }else if(currentActiveButton.equals("Tambah Set Ruangan")){
                    String commands= "addRooms_";
                    final EditText taskEditText = new EditText(getContext());
                    AlertDialog dialog = new AlertDialog.Builder(getContext())
                            .setTitle("Set Ruangan Baru")
                            .setMessage("Beri nama set ruangan baru")
                            .setView(taskEditText)
                            .setPositiveButton("Simpan", (dialog1, which) -> {
                                String task = String.valueOf(taskEditText.getText());
                                if (task.equals("")){
                                    Toast.makeText(getContext(),"Nama tidak boleh kosong",Toast.LENGTH_SHORT).show();
                                }else{
                                   // Log.d("fak","button : " + finalStr);
                                    Boolean lolos=true;
                                    for (int x=0;x<setRuanganArrayList.size();x++){
                                        if (setRuanganArrayList.get(x).getNamaSet().toLowerCase().equals(task.toLowerCase())){
                                            lolos=false;
                                        }
                                    }
                                    if (lolos){
                                        sendMessage(commands+"rooms:"+task+"~"+finalStr+"\n");
                                    }else{
                                        Toast.makeText(getContext(),"Nama set ruangan " + task + " sudah ada, silahkan gunakan nama lain",Toast.LENGTH_SHORT).show();
                                    }


                                }
                            })
                            .setNegativeButton("Cancel", null)
                            .create();
                    dialog.show();

                }else if (currentActiveButton.equals("Ubah Nama Ruangan")){

                    String data = "";
                    for (int x=0;x<32;x++){
                        String namaRuangan = ruanganArrayList.get(x).getRuangan();
                        data += "speaker_"+x+":"+namaRuangan+",";
                    }
                    data=data.substring(0,data.length()-1);

                    String msg="updateNamaRooms_"+data+"\n";
                    //Log.d("fak",msg);
                    sendMessage(msg);


                }else{

                    String msg ="updateRooms_rooms:"+currentActiveButton+"~"+finalStr+"\n";
                    //Log.d("fak",msg);
                    sendMessage(msg);
                }




            }
        });
        btnBatalRuangan.setOnClickListener(view->{
            showProgress("Mengambil data ruangan");
            sendMessage("get_data_ruangan");
        });
        //progress.show();
        showProgress("Mengambil data ruangan");
        sendMessage("get_data_ruangan");
        slideshowViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
               // textView.setText(s);
            }
        });
        return root;
    }


}