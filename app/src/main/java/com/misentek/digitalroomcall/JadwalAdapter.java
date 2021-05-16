package com.misentek.digitalroomcall;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.transition.Hold;
import com.misentek.digitalroomcall.ui.tambahjadwal.TambahJadwalFragment;

import java.util.ArrayList;

import static com.misentek.digitalroomcall.ui.tambahjadwal.TambahJadwalFragment.txtNamaJadwal;

public class JadwalAdapter extends RecyclerView.Adapter<com.misentek.digitalroomcall.JadwalAdapter.JadwalViewHolder> {

    private ArrayList<Jadwal> dataList;

    public JadwalAdapter(ArrayList<Jadwal> dataList) {
        this.dataList = dataList;
    }
    @NonNull
    @Override
    public com.misentek.digitalroomcall.JadwalAdapter.JadwalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.row_jadwal, parent, false);
        return new JadwalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull com.misentek.digitalroomcall.JadwalAdapter.JadwalViewHolder holder, int position) {
        String nama = dataList.get(position).getNama();

        String hari=dataList.get(position).getHari().toLowerCase();       // holder.txtHari.setText(dataList.get(position).getHari());
        String jam = dataList.get(position).getJam();
        String nada = dataList.get(position).getNada();
        String ruangan = dataList.get(position).getRuangan();
        String aktif = dataList.get(position).getAktif();
        holder.txtNama.setText(nama);
        String[] arrhari={"senin","selasa","rabu","kamis","jumat","sabtu","minggu"};
        String strParamHari = "";
        TextView[] textViews={holder.senin,holder.selasa, holder.rabu,holder.kamis,holder.jumat,holder.sabtu,holder.minggu};
        for (int x=0;x<7;x++){
            if (hari.contains(arrhari[x])){
                textViews[x].setTextColor(Color.BLACK);
                strParamHari+="chk"+arrhari[x]+"_on,";
            }else{
                textViews[x].setTextColor(Color.parseColor("#9e9e9e"));
                strParamHari+="chk"+arrhari[x]+"_off,";
            }
        }
        strParamHari=strParamHari.substring(0,strParamHari.length()-1);
        holder.txtJam.setText(jam);
        holder.txtNada.setText(nada);
        holder.btnRuangan.setText(ruangan);
        holder.btnRuangan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ModalRuangan cdd=new ModalRuangan(MainActivity.activity, ruangan);
                cdd.show();
            }
        });
        String param = nama+";"+dataList.get(position).getHari()+";"+jam+";"+nada+";"+ruangan;
        holder.itemView.setOnClickListener(view -> {
            MainActivity.currentActiveJadwal=param;
            Navigation.findNavController(holder.itemView).navigate(R.id.nav_tambah_jadwal);
        });
        //Log.d("fak",nama+ aktif);
        if (aktif.trim().toLowerCase().equals("on")){
            //Log.d("fak",nama+ "enabled");
            holder.switchEnable.setChecked(true);
        }else{
            //Log.d("fak",nama+ "disabled");
            holder.switchEnable.setChecked(false);
        }
        String finalStrParamHari = strParamHari;
        holder.switchEnable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                String aktif=";off";
                if (b){
                    aktif=";on";
                }
                String msg = "update_"+nama+";"+ finalStrParamHari +";"+jam+";"+nada+";"+ruangan+aktif;
                //Log.d("fak",msg);
                MainActivity.sendMessage(msg);
            }
        });
    }


    @Override
    public int getItemCount() {
        return (dataList != null) ? dataList.size() : 0;
    }

    public class JadwalViewHolder extends RecyclerView.ViewHolder{
        private TextView txtNama,senin, selasa, rabu, kamis, jumat, sabtu, minggu, txtJam, txtNada;
        private Button btnRuangan;
        private Switch switchEnable;
        public JadwalViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNama = itemView.findViewById(R.id.txt_nama);
            senin = itemView.findViewById(R.id.txt_hari_senin);
            selasa = itemView.findViewById(R.id.txt_hari_selasa);
            rabu = itemView.findViewById(R.id.txt_hari_rabu);
            kamis = itemView.findViewById(R.id.txt_hari_kamis);
            jumat = itemView.findViewById(R.id.txt_hari_jumat);
            sabtu = itemView.findViewById(R.id.txt_hari_sabtu);
            minggu = itemView.findViewById(R.id.txt_hari_minggu);
            txtJam = itemView.findViewById(R.id.txt_jam);
            txtNada = itemView.findViewById(R.id.txt_nada);
            btnRuangan = itemView.findViewById(R.id.btn_ruangan);
            switchEnable = itemView.findViewById(R.id.switchEnabled);

        }
    }
}
