package com.misentek.digitalroomcall.ui.nada;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.arthenica.mobileffmpeg.Config;
import com.arthenica.mobileffmpeg.ExecuteCallback;
import com.arthenica.mobileffmpeg.FFmpeg;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.misentek.digitalroomcall.MainActivity;
import com.misentek.digitalroomcall.NadaAdpter;
import com.misentek.digitalroomcall.R;
import com.misentek.digitalroomcall.Ruangan;
import com.misentek.digitalroomcall.UploadFileAsync;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.app.Activity.RESULT_OK;
import static android.provider.ContactsContract.CommonDataKinds.Website.URL;
import static com.arthenica.mobileffmpeg.Config.RETURN_CODE_CANCEL;
import static com.arthenica.mobileffmpeg.Config.RETURN_CODE_SUCCESS;
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
import static com.misentek.digitalroomcall.MainActivity.upload;
import static com.misentek.digitalroomcall.ui.tambahjadwal.TambahJadwalFragment.getRealPathFromURI;

public class NadaFragment extends Fragment {

    private NadaViewModel nadaViewModel;

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode == RESULT_OK && requestCode == 12) {
            Uri uriSound = data.getData();
            upload(uriSound);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        nadaViewModel =
                new ViewModelProvider(this).get(NadaViewModel.class);
        View root = inflater.inflate(R.layout.fragment_nada, container, false);

        Button btnTambahNada = root.findViewById(R.id.btn_tambah_nada);
        btnTambahNada.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(Intent.createChooser(in,"Pilih nada"), 12);
            }
        });
        recyclerViewNada = root.findViewById(R.id.recycler_view);
        currentActiveFragment="Nada";
        if (nadaList!=null){
            if (nadaList.size()>0){
                nadaAdpter = new NadaAdpter(nadaList);
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(activity);
                recyclerViewNada.setLayoutManager(layoutManager);
                recyclerViewNada.setAdapter(nadaAdpter);
                //Log.d("fak", "ada list : " + nadaList.size());
            }
        }else{
            showProgress("Mengambil nada dari Digital Room Call");
            sendMessage("get_data_bel_manual");
        }
        updateMenu();
        nadaViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                //textView.setText(s);
            }
        });
        return root;
    }

}