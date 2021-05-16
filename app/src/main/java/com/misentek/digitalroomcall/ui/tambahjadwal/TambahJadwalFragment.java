package com.misentek.digitalroomcall.ui.tambahjadwal;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.misentek.digitalroomcall.NadaAdpter;
import com.misentek.digitalroomcall.R;
import com.misentek.digitalroomcall.RuanganAdapter;
import com.misentek.digitalroomcall.SetRuangan;
import com.misentek.digitalroomcall.UploadFileAsync;

import java.util.ArrayList;
import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

import static android.app.Activity.RESULT_OK;
import static com.misentek.digitalroomcall.MainActivity.activity;
import static com.misentek.digitalroomcall.MainActivity.btnSemuaRuangan;
import static com.misentek.digitalroomcall.MainActivity.currentActiveButton;
import static com.misentek.digitalroomcall.MainActivity.currentActiveFragment;
import static com.misentek.digitalroomcall.MainActivity.currentActiveJadwal;
import static com.misentek.digitalroomcall.MainActivity.initRuangan;
import static com.misentek.digitalroomcall.MainActivity.mWebSocketClient;
import static com.misentek.digitalroomcall.MainActivity.materialButtonToggleGroup;
import static com.misentek.digitalroomcall.MainActivity.nadaAdpter;
import static com.misentek.digitalroomcall.MainActivity.nadaList;
import static com.misentek.digitalroomcall.MainActivity.progress;
import static com.misentek.digitalroomcall.MainActivity.recyclerViewNada;
import static com.misentek.digitalroomcall.MainActivity.recyclerViewRuangan;
import static com.misentek.digitalroomcall.MainActivity.sendMessage;
import static com.misentek.digitalroomcall.MainActivity.showProgress;
import static com.misentek.digitalroomcall.MainActivity.toolbar;
import static com.misentek.digitalroomcall.MainActivity.tryToConnect;
import static com.misentek.digitalroomcall.MainActivity.upload;
import static com.misentek.digitalroomcall.ui.home.HomeFragment.jadwalArrayList;

public class TambahJadwalFragment extends Fragment {

    public static RuanganAdapter ruanganAdapter;
    public static ArrayList<SetRuangan> setRuanganArrayList;
    private TambahJadwalViewModel slideshowViewModel;
    private TimePicker timePicker;
    private Button btnCancel;
    private Button btnSimpan;
    private MaterialButtonToggleGroup materialButtonToggleGroupHari;
    private Button btnPilihNada;
    private Button btnHapusJadwal;
    private TextView txtNada;
    public static TextView txtNamaJadwal;
    static String ruangan;
    public static MaterialButtonToggleGroup btnPilihRuangan;

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && requestCode == 12){
            Uri uriSound=data.getData();

            upload(uriSound);
            //play(this, uriSound);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        slideshowViewModel =
                new ViewModelProvider(this).get(TambahJadwalViewModel.class);
        View root = inflater.inflate(R.layout.fragment_tambah_jadwal, container, false);
        btnCancel = root.findViewById(R.id.btn_cancel);
        btnSimpan = root.findViewById(R.id.btn_simpan_jadwal);
        timePicker = root.findViewById(R.id.time_picker);
        txtNada = root.findViewById(R.id.txt_nada);
        txtNamaJadwal = root.findViewById(R.id.txt_nama_jadwal);
        btnPilihNada = root.findViewById(R.id.btn_nada_jadwal);
        btnHapusJadwal = root.findViewById(R.id.btn_hapus_jadwal);
        materialButtonToggleGroupHari = root.findViewById(R.id.toggleButtonHari);
        materialButtonToggleGroup = root.findViewById(R.id.toggleButtonRuangan);
        materialButtonToggleGroup.setSelectionRequired(true);
        currentActiveFragment="tambahjadwal";
        recyclerViewRuangan = root.findViewById(R.id.recycler_view_ruangans);
        initRuangan(true);
        showProgress("Mengambil data");
        if (nadaList!=null){
            if (nadaList.size()>0){
                sendMessage("get_data_ruangan");
                sendMessage("getSpeakerState_");
            }
        }else{

            sendMessage("get_data_bel_manual");
        }
        if (!currentActiveJadwal.isEmpty()){
            String[] params=currentActiveJadwal.split(";");
            String nama = params[0];
            String hari = params[1];
            String jam = params[2];
            String nada = params[3];
            ruangan = params[4];



            String[] tag = hari.split(",");
            ArrayList<View> materialButtonArrayList = new ArrayList<>();
            for (int x=0;x<tag.length;x++){
                String namahari=tag[x].split("_")[0].trim().substring(0,3);
                materialButtonToggleGroupHari.findViewsWithText(materialButtonArrayList,namahari,View.FIND_VIEWS_WITH_TEXT);

            }
            for (int i=0;i<materialButtonArrayList.size();i++){
                materialButtonToggleGroupHari.check(materialButtonArrayList.get(i).getId());
            }

            timePicker.setHour(Integer.parseInt(jam.split(":")[0]));
            timePicker.setMinute(Integer.parseInt(jam.split(":")[1]));
            txtNada.setText(nada);
            txtNamaJadwal.setText(nama);
            txtNamaJadwal.setEnabled(false);

            btnHapusJadwal.setVisibility(View.VISIBLE);
            toolbar.setTitle("Ubah jadwal");


        }else{
            btnHapusJadwal.setVisibility(View.GONE);
        }
        timePicker.setIs24HourView(true);
        btnCancel.setOnClickListener(view -> {
            activity.onBackPressed();
        });
        btnSimpan.setOnClickListener(view -> {
            String h = "0" + timePicker.getHour();
            h=h.substring(h.length()-2);
            String m = "0" + timePicker.getMinute();
            m=m.substring(m.length()-2);
            String jam = h+":"+m;

            List<Integer> checkedButtonIds = materialButtonToggleGroupHari.getCheckedButtonIds();
            List<String> listHari = new ArrayList<>();
            listHari.add("chksenin_off");listHari.add("chkselasa_off");listHari.add("chkrabu_off");listHari.add("chkkamis_off");
            listHari.add("chkjumat_off");listHari.add("chksabtu_off");listHari.add("chkminggu_off");
            Boolean hariChecked=false;
            for (int x=0;x<checkedButtonIds.size();x++){
                MaterialButton button = materialButtonToggleGroupHari.findViewById(checkedButtonIds.get(x));
                for (int i=0;i<listHari.size();i++){
                    if (listHari.get(i).startsWith(button.getTag().toString())){
                        hariChecked=true;
                        listHari.set(i,listHari.get(i).replace("_off","_on"));
                    }
                }
            }

            String hari = TextUtils.join(",",listHari);
            String nama = txtNamaJadwal.getText().toString();
            String nada = txtNada.getText().toString();
            String ruangan =currentActiveButton;

            boolean lolosNama = true;
            boolean lolosJamHari = true;
            String errMsg="";
            for (int x=0;x<jadwalArrayList.size();x++){
                if (nama.equals(jadwalArrayList.get(x).getNama())){
                    lolosNama=false;
                    errMsg="Jadwal dengan nama " + nama + " sudah ada";
                }

                String[] arrHariJadwal = jadwalArrayList.get(x).getHari().split(",");
                if (jam.equals(jadwalArrayList.get(x).getJam())){
                    String[] dd = hari.split(",");
                    for (int y=0;y<dd.length;y++){
                        if (dd[y].endsWith("_on")){
                            for (int i=0;i<arrHariJadwal.length;i++){
                                Log.d("fak", hari + " = " + arrHariJadwal[i]);
                                if (dd[y].toLowerCase().contains(arrHariJadwal[i].toLowerCase())){
                                    lolosJamHari=false;
                                    errMsg = "Jadwal di hari " + arrHariJadwal[i] + ", pukul " + jam + " sudah ada";
                                }

                            }
                        }
                    }

                }

            }

            if (!hariChecked){
                Toast.makeText(getContext(),"Silahkan pilih hari",Toast.LENGTH_SHORT).show();
                materialButtonToggleGroupHari.requestFocus();
                return;
            }
            if (nama.isEmpty()){
                Toast.makeText(getContext(),"Silahkan isi nama jadwal",Toast.LENGTH_SHORT).show();
                txtNamaJadwal.requestFocus();
                return;
            }
            if (nada.isEmpty()){
                Toast.makeText(getContext(),"Silahkan pilih atau upload nada",Toast.LENGTH_SHORT).show();
                btnPilihNada.requestFocus();
                return;
            }

            if (!lolosJamHari){
                materialButtonToggleGroupHari.requestFocus();
                Toast.makeText(getContext(),errMsg,Toast.LENGTH_SHORT).show();
                return;
            }
            String state = "save_";
            if (!currentActiveJadwal.isEmpty()){
                state="update_";
            }else{
                if (!lolosNama){
                    txtNamaJadwal.requestFocus();
                    Toast.makeText(getContext(),errMsg,Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            String msg = state+nama +";"+hari+";"+jam+";"+nada+";"+ruangan+";on";
            sendMessage(msg);
            //activity.onBackPressed();
        });

        btnHapusJadwal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String namaJadwal = txtNamaJadwal.getText().toString();
                AlertDialog dialog = new AlertDialog.Builder(getContext())
                        .setTitle("Hapus Jadwal")
                        .setMessage("Anda yakin ingin menghapus jadwal ini?")
                        .setPositiveButton("Hapus", (dialog1, which) -> {
                            sendMessage("delete_"+namaJadwal+";");
                            activity.onBackPressed();
                        })
                        .setNegativeButton("Cancel", null)
                        .create();
                dialog.show();
            }
        });
        //progress.show();

        btnPilihNada.setOnClickListener(view -> {
            List<CharSequence> itemss = new ArrayList<>();
            itemss.add("Upload nada baru");
            itemss.addAll(nadaList);


            CharSequence[] items=itemss.toArray(new CharSequence[itemss.size()]);
            // ArrayList<String> items=new ArrayList<>();
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Pilih Nada");
            builder.setItems(items, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if (i==0){
                        String[] PERMISSIONS_STORAGE = {
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE
                        };
                        if (!EasyPermissions.hasPermissions(getActivity(), PERMISSIONS_STORAGE)) {
                            EasyPermissions.requestPermissions(getActivity(), "Kami membutuhkan izin untuk melihat file mp3", 123,PERMISSIONS_STORAGE);
                        }else{
                            Intent in = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(Intent.createChooser(in,"select file"), 12);
                        }

                    }else{
                        String nada =items[i].toString();
                        txtNada.setText(nada);
                        if (txtNamaJadwal.getText().toString().isEmpty()){
                            txtNamaJadwal.setText(nada.replace(".mp3","").replaceAll("/","").replace("Bel_",""));
                        }


                    }
                    //Log.d("fak","terpilih  " + items[i].toString());
                }
            }).show();
        });
        return root;
    }

    public static void checkButton(){
        materialButtonToggleGroup.check(btnSemuaRuangan.getId());
        if (ruangan!=null && !ruangan.equals("")){

            ArrayList<View> viewArrayList = new ArrayList<>();
            materialButtonToggleGroup.findViewsWithText(viewArrayList,ruangan,View.FIND_VIEWS_WITH_TEXT);

            if (viewArrayList.size()>0){
                for (int x=0;x<viewArrayList.size();x++){
                    int id = viewArrayList.get(x).getId();
                    Button button = materialButtonToggleGroup.findViewById(id);
                    if (button.getText().toString().equals(ruangan)){
                        materialButtonToggleGroup.check(id);
                    }
                }

            }
        }
    }
    public static String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
}