package com.misentek.digitalroomcall;

public class Ruangan {

    private String ruangan;
    private Boolean selected;
    

    public Ruangan(String ruangan, Boolean selected) {
        this.ruangan = ruangan;
        this.selected=selected;
    }
    public String getRuangan() {
        return ruangan;
    }

    public void setRuangan(String ruangan) {
        this.ruangan = ruangan;
    }

    public Boolean getSelected(){return selected;}

    public void setSelected(Boolean selected){this.selected=selected;}


}
