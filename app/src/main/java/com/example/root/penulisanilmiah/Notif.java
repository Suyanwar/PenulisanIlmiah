package com.example.root.penulisanilmiah;
public class Notif {
    public String isi, device, waktu;
    public Notif(String isi, String device, String waktu) {
        this.isi = isi;
        this.device = device;
        this.waktu = waktu;
    }
    public Notif() {
    }
    public String getIsi() {
        return isi;
    }
    public void setIsi(String isi) {
        this.isi = isi;
    }
    public String getDevice() {
        return device;
    }
    public void setDevice(String device) {
        this.device = device;
    }
    public String getWaktu() {
        return waktu;
    }
    public void setWaktu(String waktu) {
        this.waktu = waktu;
    }
}
