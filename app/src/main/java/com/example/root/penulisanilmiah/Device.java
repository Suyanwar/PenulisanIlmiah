package com.example.root.penulisanilmiah;
public class Device {
    public String nm_dv, nm_tnmn_a;
    public Device(String nm_dv, String nm_tnmn_a) {
        this.nm_dv = nm_dv;
        this.nm_tnmn_a = nm_tnmn_a;
    }
    public Device() {
    }
    public String getNm_dv() {
        return nm_dv;
    }
    public void setNm_dv(String nm_dv) {
        this.nm_dv = nm_dv;
    }
    public String getNm_tnmn_a() {
        return nm_tnmn_a;
    }
    public void setNm_tnmn_a(String nm_tnmn_a) {
        this.nm_tnmn_a = nm_tnmn_a;
    }
}
