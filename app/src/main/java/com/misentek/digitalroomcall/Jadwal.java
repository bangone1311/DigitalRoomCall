package com.misentek.digitalroomcall;

public class Jadwal {

    private String nama;
    private String hari;
    private String jam;
    private String nada;
    private String ruangan;
    private String aktif;

    public Jadwal(String nama, String hari, String jam, String nada, String ruangan, String aktif) {
        this.nama = nama;
        this.hari = hari;
        this.jam = jam;
        this.nada = nada;
        this.ruangan= ruangan;
        this.aktif= aktif;

    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getHari() {
        return hari;
    }

    public void setHari(String hari) {
        this.hari = hari;
    }

    public String getJam() {
        return jam;
    }

    public void setJam(String jam) {
        this.jam = jam;
    }
    public String getNada() {
        return nada;
    }

    public void setNada(String nada) {
        this.nada = nada;
    }
    public String getRuangan() {
        return ruangan;
    }

    public void setRuangan(String ruangan) {
        this.ruangan = ruangan;
    }

    public String getAktif() {
        return aktif;
    }

    public void setAktif(String aktif) {
        this.aktif = aktif;
    }
}
