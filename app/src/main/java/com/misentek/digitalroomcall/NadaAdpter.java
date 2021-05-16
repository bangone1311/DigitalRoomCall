package com.misentek.digitalroomcall;

import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import static com.misentek.digitalroomcall.MainActivity.activity;
import static com.misentek.digitalroomcall.MainActivity.changeState;
import static com.misentek.digitalroomcall.MainActivity.currentActiveFragment;
import static com.misentek.digitalroomcall.MainActivity.sendMessage;
import static com.misentek.digitalroomcall.MainActivity.txtStatus;

public class NadaAdpter extends RecyclerView.Adapter<NadaAdpter.NadaViewHolder> {

    private List<String> dataList;
    public static Button prevS,prevP;
    public NadaAdpter(List<String>dataList) {
        this.dataList = dataList;
    }
    @NonNull
    @Override
    public NadaAdpter.NadaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.row_nada, parent, false);
        return new NadaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NadaAdpter.NadaViewHolder holder, int position) {

        holder.txtNada.setText(dataList.get(position));
        if (currentActiveFragment.equals("Bel Manual")){
            holder.btnDelete.setVisibility(View.GONE);
        }


        holder.btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (prevS!=null && prevS!=holder.btnStop){
                    prevS.setVisibility(View.GONE);
                    prevP.setVisibility(View.VISIBLE);
                }
                holder.btnPlay.setVisibility(View.GONE);
                holder.btnStop.setVisibility(View.VISIBLE);
                prevS=holder.btnStop;
                prevP=holder.btnPlay;
                sendMessage("play_" + dataList.get(position));
                changeState("Memainkan "+ dataList.get(position));
            }
        });
        holder.btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.btnPlay.setVisibility(View.VISIBLE);
                holder.btnStop.setVisibility(View.GONE);
                sendMessage("stop_");
            }
        });
        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog dialog = new AlertDialog.Builder(activity)
                        .setTitle("Hapus Nada")
                        .setMessage("Anda yakin ingin menghapus nada ini?")
                        .setPositiveButton("Hapus", (dialog1, which) -> {
                            sendMessage("deleteSongs_"+dataList.get(position));
                        })
                        .setNegativeButton("Cancel", null)
                        .create();
                dialog.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return (dataList != null) ? dataList.size() : 0;
    }

    public class NadaViewHolder extends RecyclerView.ViewHolder{
        private TextView txtNada;
        private Button btnDelete;
        private Button btnPlay;
        private Button btnStop;
        public NadaViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNada = itemView.findViewById(R.id.txt_nada);
            btnPlay = itemView.findViewById(R.id.btn_play);
            btnDelete = itemView.findViewById(R.id.btn_hapus_nada);
            btnStop = itemView.findViewById(R.id.btn_stop);
        }
    }
}
