package com.example.root.penulisanilmiah;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SettingActivity extends AppCompatActivity {
    ImageView ic_info;
    TextView tv_judul;
    public String [] campur;
    RadioGroup rg;
    RadioButton rb;
    DatabaseReference mDb, mDel;
    public String choice, hasil, hasil1, choice1, lights;
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    Map<String, Integer> params, params1;
    Switch tombollampu;
    OkHttpClient client;
    Button btnDel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        Intent data = getIntent();
        campur = data.getStringArrayExtra("campur");
        tv_judul = (TextView)findViewById(R.id.judul_toolbar);
        ic_info = (ImageView)findViewById(R.id.info_toolbar);
        tv_judul.setTypeface(Typeface.createFromAsset(SettingActivity.this.getAssets(), "fonts/Quango.ttf"));
        tv_judul.setText(campur[0]);
        ic_info.setVisibility(View.GONE);
        rg = (RadioGroup)findViewById(R.id.radio_mode);
        mDb = FirebaseDatabase.getInstance().getReference().child("Devices");
        new GetModeValue().execute();
        tombollampu = (Switch) findViewById(R.id.switch1);
        new GetLightValue().execute();
        tombollampu.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                client = new OkHttpClient();
                params1 = new HashMap<>();
                if (isChecked){
                    params1.put("status-lampu", 1);
                }else{
                    params1.put("status-lampu", 0);
                }
                JSONObject parameter = new JSONObject(params1);
                RequestBody body = RequestBody.create(JSON, parameter.toString());
                Request req = new Request.Builder().url("http://things.ubidots.com/api/v1.6/devices/my-sensor/?token="+campur[1])
                        .post(body)
                        .addHeader("Content-Type", "application/json")
                        .build();
                client.newCall(req).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                    }
                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                    }
                });
            }
        });
        btnDel = (Button)findViewById(R.id.btn_delete);
        btnDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder al = new AlertDialog.Builder(SettingActivity.this);
                al.setTitle("Confirm Delete...").setMessage("Are you sure want to delete this plant?")
                        .setIcon(R.drawable.ic_remove).setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mDel = mDb.child(campur[0]);
                        mDel.removeValue();
                        Intent back2 = new Intent(SettingActivity.this, DeviceActivity.class);
                        finish();
                        startActivity(back2);
                        Toast.makeText(getApplicationContext(), "Deleted...", Toast.LENGTH_SHORT).show();
                    }
                });
                al.show();
            }
        });
    }
    @Override
    public void onBackPressed() {
        Intent back = new Intent(SettingActivity.this, MainActivity.class);
        back.putExtra("key", campur[0]);
        back.putExtra("token", campur[1]);
        back.putExtra("id_device", campur[2]);
        finish();
        startActivity(back);
    }
    public void selectOption(View v){
        int radiobuttonid = rg.getCheckedRadioButtonId();
        rb = (RadioButton)findViewById(radiobuttonid);
        choice1 = rb.getText().toString();
        client = new OkHttpClient();
        params = new HashMap<>();
        if (choice1.equals("Manual")){
            params.put("mode-sensor", 1);
            tombollampu.setEnabled(true);
        }else if (choice1.equals("Otomatis")){
            params.put("mode-sensor", 0);
            tombollampu.setEnabled(false);
        }else{
            Toast.makeText(getApplicationContext(), "Pilih yang benar!", Toast.LENGTH_SHORT).show();
        }
        JSONObject parameter = new JSONObject(params);
        RequestBody body = RequestBody.create(JSON, parameter.toString());
        Request req = new Request.Builder().url("http://things.ubidots.com/api/v1.6/devices/my-sensor/?token="+campur[1])
                .post(body)
                .addHeader("Content-Type", "application/json")
                .build();
        client.newCall(req).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
            }
        });
    }
    private class GetModeValue extends AsyncTask<String, Void, String>{
        @Override
        protected String doInBackground(String... params) {
            client = new OkHttpClient();
            Request req = new Request.Builder().url("http://things.ubidots.com/api/v1.6/variables/5908433776254279e1bfa347/values?token="+campur[1]).build();
            try {
                Response respone = client.newCall(req).execute();
                if (respone.isSuccessful()){
                    hasil = respone.body().string();
                    JSONObject pertama = new JSONObject(hasil);
                    JSONArray kedua = pertama.getJSONArray("results");
                    JSONObject ketiga = kedua.getJSONObject(0);
                    choice = ketiga.getString("value");
                }else{
                    hasil = "Null";
                    choice = "2";
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return choice;
        }
        @Override
        protected void onPostExecute(String s) {
            if (s.equals("0.0")){
                rg.check(R.id.radio_mode_auto);
                tombollampu.setEnabled(false);
            }else if (s.equals("1.0")){
                rg.check(R.id.radio_mode_manual);
                tombollampu.setEnabled(true);
            }else{
                rg.clearCheck();
                Toast.makeText(getApplicationContext(), "Gagal menerima request", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private class GetLightValue extends AsyncTask<String, Void, String>{
        @Override
        protected String doInBackground(String... params) {
            client = new OkHttpClient();
            Request req = new Request.Builder().url("http://things.ubidots.com/api/v1.6/variables/5908432d76254279e0703f1a/values?token="+campur[1]).build();
            try {
                Response respone = client.newCall(req).execute();
                if (respone.isSuccessful()){
                    hasil1 = respone.body().string();
                    JSONObject pertama = new JSONObject(hasil1);
                    JSONArray kedua = pertama.getJSONArray("results");
                    JSONObject ketiga = kedua.getJSONObject(0);
                    lights = ketiga.getString("value");
                }else{
                    hasil1 = "Null";
                    lights = "2";
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return lights;
        }
        @Override
        protected void onPostExecute(String s1) {
            if (s1.equals("0.0")){
                tombollampu.setChecked(false);
            }else if (s1.equals("1.0")){
                tombollampu.setChecked(true);
            }else{
                Toast.makeText(getApplicationContext(), "Gagal menerima request", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
