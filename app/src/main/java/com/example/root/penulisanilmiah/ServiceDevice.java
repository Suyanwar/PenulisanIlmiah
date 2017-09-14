package com.example.root.penulisanilmiah;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
//import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ServiceDevice extends IntentService{
    public int cahayamin, cahayamax;
    public float sekarang, selisih;
    public String hasil, id_tanaman, id_var;
    public DatabaseReference mDbn, mDbn1;
    public String [] campur;
    NotificationCompat.Builder notif;
    public ServiceDevice(){ super("Testing"); }
    public ServiceDevice(String name) {
        super(name);
    }
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
//        Log.d("Mulai2", "Mulai");
        campur = intent.getStringArrayExtra("campur");
        cahayamax = intent.getIntExtra("cahayamax", 0);
        cahayamin = intent.getIntExtra("cahayamin", 0);
        id_tanaman = intent.getStringExtra("id_var");
        notif = new NotificationCompat.Builder(this)
                .setAutoCancel(true)
                .setContentTitle("Penulisan Ilmiah");
        mDbn = FirebaseDatabase.getInstance().getReference().child("Devices").child(campur[0])
                .child("Tanaman").child(id_tanaman);
        OkHttpClient client = new OkHttpClient();
        Request req = new Request.Builder().addHeader("X-Auth-Token", campur[1]).url("http://things.ubidots.com/api/v1.6/datasources/"+campur[2]+"/variables?token="+campur[1]).build();
        try {
            Response response = client.newCall(req).execute();
            if (response.isSuccessful()){
                hasil = response.body().string();
            }else{
                hasil = "Failed";
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            JSONObject pertama = new JSONObject(hasil);
            JSONArray kedua = pertama.getJSONArray("results");
            JSONObject ketiga = kedua.getJSONObject(3);
            id_var = ketiga.getString("id");
            JSONObject keempat = ketiga.getJSONObject("last_value");
            sekarang = (float)keempat.getDouble("value");
            if (sekarang > cahayamax){
                selisih = sekarang - cahayamax;
                kasihnotif("Lebih");
            }else if (sekarang < cahayamin){
                selisih = sekarang- cahayamin;
                kasihnotif("Kurang");
            }else{
                selisih = 0;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
//        Log.d("Mulai3", String.valueOf(selisih));
        mDbn.child("comparison").setValue(String.valueOf(selisih));
        mDbn.child("id_var").setValue(id_var);
    }
    private void kasihnotif(String keterangan) {
//        Log.d("Mulai3", "Mulai");
        notif.setSmallIcon(R.mipmap.ic_launcher);
        notif.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setVibrate(new long[] { 1000, 1000})
                .setLights(Color.RED, 3000, 3000);
        notif.setWhen(System.currentTimeMillis());
        Intent not = new Intent(this, MainActivity.class);
        not.putExtra("key", campur[0]);
        not.putExtra("token", campur[1]);
        not.putExtra("id_device", campur[2]);
        PendingIntent pi = PendingIntent.getActivity(this, 0, not, PendingIntent.FLAG_UPDATE_CURRENT);
        notif.setContentIntent(pi);
        mDbn1 = FirebaseDatabase.getInstance().getReference().child("Devices").child(campur[0]).child("Notif").push();
        mDbn1.child("device").setValue(campur[0]);
        mDbn1.child("waktu").setValue(DateFormat.getDateTimeInstance().format(new Date()));
        if (keterangan.equals("Lebih")){
            notif.setContentText(campur[0]+": Kelebihan "+String.valueOf(selisih));
            mDbn1.child("isi").setValue(campur[0]+": Kelebihan "+String.valueOf(selisih));
        }else{
            notif.setContentText(campur[0]+": Kekurangan "+String.valueOf(selisih).substring(1));
            mDbn1.child("isi").setValue(campur[0]+": Kekurangan "+String.valueOf(selisih).substring(1));
        }
        NotificationManager nm = (NotificationManager)this.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(12345, notif.build());
    }
}
