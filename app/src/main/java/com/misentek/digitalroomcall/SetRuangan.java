package com.misentek.digitalroomcall;

import java.util.ArrayList;

public class SetRuangan {

    private String namaSet;
    private ArrayList<Ruangan> ruanganArrayList;
    private Boolean selected;

    public SetRuangan(String namaSet, ArrayList<Ruangan> ruanganArrayList, Boolean selected) {
        this.namaSet = namaSet;
        this.ruanganArrayList=ruanganArrayList;
        this.selected=selected;
    }
    public String getNamaSet() {
        return namaSet;
    }

    public void setNamaSet(String namaSet) {
        this.namaSet = namaSet;
    }

    public ArrayList<Ruangan> getRuanganArrayList(){return ruanganArrayList;}

    public void setRuanganArrayList(ArrayList<Ruangan> ruanganArrayList){this.ruanganArrayList=ruanganArrayList;}

    public Boolean getSelected(){return selected;}

    public void setSelected(Boolean selected){this.selected=selected;}


}
