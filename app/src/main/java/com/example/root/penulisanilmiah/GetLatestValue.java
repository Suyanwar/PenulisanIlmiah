package com.example.root.penulisanilmiah;

import android.os.AsyncTask;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class GetLatestValue extends AsyncTask<String, Void, String>{
    public int cahayamin, cahayamax;
    public float sekarang, selisih;
    public DatabaseReference mCompare;
    public String id_tanaman, id_var;
    GetLatestValue(String id_tanaman, int cahayamin, int cahayamax){
        this.id_tanaman = id_tanaman;
        this.cahayamin = cahayamin;
        this.cahayamax = cahayamax;
    }
    @Override
    protected String doInBackground(String... params) {
        mCompare = FirebaseDatabase.getInstance().getReference().child("Devices").child(params[0])
                .child("Tanaman").child(id_tanaman);
//        Log.d("Nama Device", params[0]);
//        Log.d("Token", params[1]);
//        Log.d("Device ID", params[2]);
        OkHttpClient client = new OkHttpClient();
        Request req = new Request.Builder().addHeader("X-Auth-Token", params[1]).url("http://things.ubidots.com/api/v1.6/datasources/"+params[2]+"/variables?token="+params[1]).build();
        try {
            Response response = client.newCall(req).execute();
            if (response.isSuccessful()){
                return response.body().string();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Failed";
    }
    @Override
    protected void onPostExecute(String s) {
        try {
            JSONObject pertama = new JSONObject(s);
            JSONArray kedua = pertama.getJSONArray("results");
            JSONObject ketiga = kedua.getJSONObject(3);
            id_var = ketiga.getString("id");
            JSONObject keempat = ketiga.getJSONObject("last_value");
            sekarang = (float)keempat.getDouble("value");
            if (sekarang > cahayamax){
                selisih = sekarang - cahayamax;
            }else if (sekarang < cahayamin){
                selisih = sekarang- cahayamin;
            }else{
                selisih = 0;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mCompare.child("comparison").setValue(String.valueOf(selisih));
        mCompare.child("id_var").setValue(id_var);
    }
}
