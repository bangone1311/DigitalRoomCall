package com.misentek.digitalroomcall;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import static com.misentek.digitalroomcall.MainActivity.activity;
import static com.misentek.digitalroomcall.MainActivity.currentActiveFragment;
import static com.misentek.digitalroomcall.MainActivity.sendMessage;
import static com.misentek.digitalroomcall.MainActivity.setRuanganArrayList;

class ModalRuangan extends Dialog implements
        android.view.View.OnClickListener {

    public Activity c;
    public String s;
    public Dialog d;
    public Button no;

    public ModalRuangan(Activity a, String s) {
        super(a);
        // TODO Auto-generated constructor stub
        this.c = a;
        this.s = s;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.modal_ruangan);
        no = findViewById(R.id.btn_no);
        no.setOnClickListener(this);

        RecyclerView recyclerViewRuangans = findViewById(R.id.recycler_view_ruangans);

        s=s.trim().toLowerCase();
        ArrayList<Ruangan> ruangans=new ArrayList<>();
        //Log.d("fak", String.valueOf(setRuanganArrayList.size()));
        if (s!=null && !s.equals("")){
            for (int x=0;x<setRuanganArrayList.size();x++){
                //
                //
               // Log.d("fak",setRuanganArrayList.get(x).getNamaSet() + "=" + s) ;
               if (setRuanganArrayList.get(x).getNamaSet().trim().toLowerCase().equals(s.trim().toLowerCase())){
                   if (s.trim().toLowerCase().equals("semua ruangan")){
                       if (currentActiveFragment.equals("Bel Manual")){
                           ruangans = new ArrayList<>(setRuanganArrayList.get(0).getRuanganArrayList());
                       }else{
                           ruangans = new ArrayList<>(setRuanganArrayList.get(1).getRuanganArrayList());
                       }
                   }else{
                       ruangans = new ArrayList<>(setRuanganArrayList.get(x).getRuanganArrayList());
                   }

               }
            }
            if (currentActiveFragment.equals("Bel Manual")){
                for (int i=0;i<ruangans.size();i++){
                    if (ruangans.get(i).getSelected()){
                        sendMessage("speaker_"+i+"_on");
                    }else {
                        sendMessage("speaker_"+i+"_off");
                    }
                }
            }

        }
        RuanganAdapter adapter=new RuanganAdapter(ruangans);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(activity, 8);
        recyclerViewRuangans.setLayoutManager(layoutManager);
        recyclerViewRuangans.setAdapter(adapter);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_no:
                dismiss();
                break;
            default:
                break;
        }
        dismiss();
    }
}