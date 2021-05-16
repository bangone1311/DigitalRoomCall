package com.misentek.digitalroomcall;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import static android.text.InputType.TYPE_CLASS_TEXT;
import static android.text.InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS;
import static com.misentek.digitalroomcall.MainActivity.activity;
import static com.misentek.digitalroomcall.MainActivity.currentActiveButton;
import static com.misentek.digitalroomcall.MainActivity.currentActiveFragment;
import static com.misentek.digitalroomcall.MainActivity.currentActiveJadwal;
import static com.misentek.digitalroomcall.MainActivity.sendMessage;

public class RuanganAdapter extends RecyclerView.Adapter<RuanganAdapter.RuanganViewHolder> {

    private ArrayList<Ruangan> dataList;
    public static String strButton;

    public RuanganAdapter(ArrayList<Ruangan>dataList) {
        this.dataList = dataList;
        strButton="";
    }

    @NonNull
    @Override
    public RuanganAdapter.RuanganViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.row_ruangan, parent, false);
        return new RuanganViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RuanganAdapter.RuanganViewHolder holder, int position) {
        final String[] state = {"_off"};
        if (dataList.get(position).getSelected()){
            state[0] ="_on";
        }else{
            state[0] ="_off";
        }

        holder.containerRuangan.setOnClickListener(view -> {
            holder.btnRuangan.performClick();
        });
        holder.txtNama.setText(dataList.get(position).getRuangan());
        holder.txtEditNama.setText(dataList.get(position).getRuangan());
        holder.btnRuangan.setChecked (dataList.get(position).getSelected());
        holder.btnRuangan.setTag("speaker_"+position+ state[0]);
        holder.btnRuangan.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b){
                state[0] ="_on";
            }else{
                state[0] ="_off";
            }
            dataList.get(position).setSelected(b);


            if (currentActiveFragment.equals("Bel Manual")){
                sendMessage("speaker_"+position+state[0]);
            }

        });
        holder.txtEditNama.setSelectAllOnFocus(true);
        holder.txtEditNama.setInputType(TYPE_CLASS_TEXT| TYPE_TEXT_FLAG_CAP_CHARACTERS);
        holder.txtEditNama.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                dataList.get(position).setRuangan(holder.txtEditNama.getText().toString());
            }
        });

        if (currentActiveButton.equals("Ubah Nama Ruangan")){
            holder.txtNama.setVisibility(View.GONE);
            holder.btnRuangan.setVisibility(View.GONE);
            holder.txtEditNama.setVisibility(View.VISIBLE);
            holder.txtEditNama.setHint(dataList.get(position).getRuangan());
        }
        strButton+="speaker_"+position+ state[0] +",";
    }

    @Override
    public int getItemCount() {
        return (dataList != null) ? dataList.size() : 0;
    }

    public class RuanganViewHolder extends RecyclerView.ViewHolder{
        private Switch btnRuangan;
        private TextView txtNama;
        private EditText txtEditNama;
        private LinearLayout containerRuangan;
        public RuanganViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNama = itemView.findViewById(R.id.txt_nama);
            btnRuangan = itemView.findViewById(R.id.btn_ruangan);
            txtEditNama=itemView.findViewById(R.id.txt_edit_nama);
            containerRuangan = itemView.findViewById(R.id.container_ruangan);
        }
    }
}
