package com.example.root.penulisanilmiah;
public class Tanaman {
    public String link_image, nm_tnmn, kondisi, activate, jenis, comparison, id_var;
    public Tanaman(String link_image, String nm_tnmn, String kondisi, String activate, String jenis, String comparison, String id_var) {
        this.link_image = link_image;
        this.nm_tnmn = nm_tnmn;
        this.kondisi = kondisi;
        this.activate = activate;
        this.jenis = jenis;
        this.comparison = comparison;
        this.id_var = id_var;
    }
    public Tanaman() {
    }
    public String getLink_image() {
        return link_image;
    }
    public void setLink_image(String link_image) {
        this.link_image = link_image;
    }
    public String getNm_tnmn() {
        return nm_tnmn;
    }
    public void setNm_tnmn(String nm_tnmn) {
        this.nm_tnmn = nm_tnmn;
    }
    public String getKondisi() {
        return kondisi;
    }
    public void setKondisi(String kondisi) {
        this.kondisi = kondisi;
    }
    public String getActivate() {
        return activate;
    }
    public void setActivate(String activate) {
        this.activate = activate;
    }
    public String getJenis() {
        return jenis;
    }
    public void setJenis(String jenis) {
        this.jenis = jenis;
    }
    public String getComparison() {
        return comparison;
    }
    public void setComparison(String comparison) {
        this.comparison = comparison;
    }
    public String getId_var() {
        return id_var;
    }
    public void setId_var(String id_var) {
        this.id_var = id_var;
    }
}
