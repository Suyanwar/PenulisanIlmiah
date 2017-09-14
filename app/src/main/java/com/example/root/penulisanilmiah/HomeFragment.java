package com.example.root.penulisanilmiah;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
//import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HomeFragment extends Fragment{
    View v2;
    TextView tvnull_plant;
    ListView lvPlant;
    FloatingActionButton add_plant;
    public DatabaseReference mDb, mBanding, mDel, mLights;
    public String [] campur;
    public String jenis_tanaman;
    public ArrayList<Tanaman> list_plant;
    public ArrayList<String> key_plant;
    public Tanaman tm, tanaman, selected;
    public GetLatestValue banding;
    public int cahayamin, cahayamax, cahayamin1, cahayamax1;
    CharSequence menu[] = new CharSequence[]{"Enable", "Delete"};
    OkHttpClient client;
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    Map<String, Integer> params;
    public HomeFragment() {
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v2 = inflater.inflate(R.layout.fragment_home, container, false);
        Bundle i = getArguments();
        campur = i.getStringArray("campur");
        tvnull_plant = (TextView)v2.findViewById(R.id.tv_null_plant);
        lvPlant = (ListView)v2.findViewById(R.id.list_plant);
        add_plant = (FloatingActionButton)v2.findViewById(R.id.add_plant);
        mDb = FirebaseDatabase.getInstance().getReference().child("Devices").child(campur[0]).child("Tanaman");
        mDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() > 0){
                    list_plant = new ArrayList<>();
                    key_plant = new ArrayList<>();
                    for (DataSnapshot ds2: dataSnapshot.getChildren()){
                        key_plant.add(ds2.getKey());
                        tm = ds2.getValue(Tanaman.class);
                        if (tm != null){
                            list_plant.add(tm);
                        }
                    }
                    Collections.reverse(list_plant);
                    Collections.reverse(key_plant);
                    CustomListAdapter1 adapter1 = new CustomListAdapter1(v2.getContext(), list_plant);
                    lvPlant.setAdapter(adapter1);
                    lvPlant.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            selected = list_plant.get(position);
                            Intent iq = new Intent(getActivity(), StatisticActivity.class);
                            iq.putExtra("api", campur[1]);
                            iq.putExtra("varID", selected.getId_var());
                            iq.putExtra("campur", campur);
                            iq.putExtra("cahayamin", cahayamin);
                            iq.putExtra("cahayamax", cahayamax);
                            getActivity().finish();
                            startActivity(iq);
                        }
                    });
                    lvPlant.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                        @Override
                        public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                            selected = list_plant.get(position);
                            if (selected.getActivate().equals("1")){
                                menu[0] = "Disable";
                            }
                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                            builder.setTitle("Customize...")
                                    .setItems(menu, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            switch (which){
                                                case 0:
                                                    mDel = FirebaseDatabase.getInstance().getReference().child("Devices").child(campur[0]);
                                                    mLights = FirebaseDatabase.getInstance().getReference().child("Jenis");
                                                    if (menu[which].equals("Enable")){
                                                        for(DataSnapshot ds33: dataSnapshot.getChildren()){
                                                            if (ds33.child("activate").getValue().toString().equals("1")){
                                                                mDb.child(ds33.getKey()).child("activate").setValue("0");
                                                            }
                                                        }
                                                        jenis_tanaman = dataSnapshot.child(key_plant.get(position)).child("jenis").getValue().toString();
                                                        mDb.child(key_plant.get(position)).child("activate").setValue("1");
                                                        mDel.child("nm_tnmn_a").setValue(dataSnapshot.child(key_plant.get(position)).child("nm_tnmn").getValue().toString());
                                                        mLights.addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(DataSnapshot dataSnapshot1) {
                                                                cahayamin1 = Integer.parseInt(dataSnapshot1.child(jenis_tanaman).child("cahayamin").getValue().toString());
                                                                cahayamax1 = Integer.parseInt(dataSnapshot1.child(jenis_tanaman).child("cahayamax").getValue().toString());
                                                                if (SignedDevice.isFirst(getContext(), key_plant.get(position))){
                                                                    stopAlarm();
                                                                    scheduleAlarm(campur, cahayamin1, cahayamax1, key_plant.get(position));
                                                                }
                                                                client = new OkHttpClient();
                                                                params = new HashMap<>();
                                                                params.put("threshold", cahayamin1);
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
                                                            @Override
                                                            public void onCancelled(DatabaseError databaseError) {
                                                            }
                                                        });
                                                        Toast.makeText(getContext(), "Enabled...", Toast.LENGTH_SHORT).show();
                                                    }else{
                                                        mDb.child(key_plant.get(position)).child("activate").setValue("0");
                                                        mDel.child("nm_tnmn_a").setValue("-");
                                                        stopAlarm();
                                                        menu[0] = "Enable";
                                                    }
                                                    break;
                                                case 1:
                                                    android.app.AlertDialog.Builder al = new android.app.AlertDialog.Builder(getActivity());
                                                    al.setTitle("Confirm Delete...").setMessage("Are you sure want to delete this plant?")
                                                            .setIcon(R.drawable.ic_remove).setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            dialog.cancel();
                                                        }
                                                    }).setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            mDel = mDb.child(key_plant.get(position));
                                                            mDel.removeValue();
                                                            Toast.makeText(getContext(), "Deleted...", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                                    al.show();
                                                    break;
                                                default:
                                                    Toast.makeText(getContext(), "Tidak Ada", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                            builder.show();
                        return true;
                        }
                    });
                    tvnull_plant.setVisibility(View.GONE);
                    lvPlant.setVisibility(View.VISIBLE);
                }else{
                    lvPlant.setVisibility(View.GONE);
                    tvnull_plant.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        add_plant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent z = new Intent(getActivity(), AddPlantActivity.class);
                z.putExtra("campur", campur);
                getActivity().finish();
                startActivity(z);
            }
        });
        return v2;
    }
    public class CustomListAdapter1 extends ArrayAdapter<Tanaman>{
        ImageView gambar_tanaman, ic_warning, ic_active, sk1, sk2, sk3, sk4, sk5;
        TextView nm_tnmn, kondisi;
        public CustomListAdapter1(@NonNull Context context, @NonNull ArrayList<Tanaman> objects) {
            super(context, 0, objects);
        }

        @NonNull
        @Override
        public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            tanaman = getItem(position);
            if (convertView == null){
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.plant_item, parent, false);
            }
            gambar_tanaman = (ImageView)convertView.findViewById(R.id.gambar_tanaman);
            ic_warning = (ImageView)convertView.findViewById(R.id.warning);
            ic_active = (ImageView)convertView.findViewById(R.id.actived_plant);
            sk1 = (ImageView)convertView.findViewById(R.id.bar_1);
            sk2 = (ImageView)convertView.findViewById(R.id.bar_2);
            sk3 = (ImageView)convertView.findViewById(R.id.bar_3);
            sk4 = (ImageView)convertView.findViewById(R.id.bar_4);
            sk5 = (ImageView)convertView.findViewById(R.id.bar_5);
            nm_tnmn = (TextView)convertView.findViewById(R.id.nama_tanaman);
            kondisi = (TextView)convertView.findViewById(R.id.kondisi_tanaman);
            nm_tnmn.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/Quango.ttf"));
            if (tanaman.getLink_image() != null){
                Picasso.with(getContext()).load(Uri.parse(tanaman.getLink_image())).into(gambar_tanaman);
            }
            if (tanaman.getNm_tnmn() != null){
                nm_tnmn.setText(tanaman.getNm_tnmn());
            }
            if (tanaman.getActivate() != null){
                if (tanaman.getActivate().equals("1")){
                    ic_active.setVisibility(View.VISIBLE);
                    if (tanaman.getJenis() != null){
                        mBanding = FirebaseDatabase.getInstance().getReference().child("Jenis").child(tanaman.getJenis());
                        mBanding.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                cahayamin = Integer.parseInt(dataSnapshot.child("cahayamin").getValue().toString());
                                cahayamax = Integer.parseInt(dataSnapshot.child("cahayamax").getValue().toString());
                                banding = new GetLatestValue(key_plant.get(position), cahayamin, cahayamax);
                                banding.execute(campur);
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                            }
                        });
                    }
                }else{
                    ic_active.setVisibility(View.GONE);
                }
            }
            if (tanaman.getComparison() != null){
                if (Float.parseFloat(tanaman.getComparison()) < 0){
                    kondisi.setText("Kurang cahaya sebanyak "+tanaman.getComparison().substring(1));
                    ic_warning.setVisibility(View.VISIBLE);
                    sk1.setVisibility(View.VISIBLE);
                    if (Float.parseFloat(tanaman.getComparison().substring(1)) < 100){
                        sk2.setVisibility(View.VISIBLE);
                    }
                }else if (Float.parseFloat(tanaman.getComparison()) > 0){
                    kondisi.setText("Lebih cahaya sebanyak "+tanaman.getComparison());
                    ic_warning.setVisibility(View.VISIBLE);
                    sk1.setVisibility(View.VISIBLE);
                    sk2.setVisibility(View.VISIBLE);
                    sk3.setVisibility(View.VISIBLE);
                    sk4.setVisibility(View.VISIBLE);
                    if (Float.parseFloat(tanaman.getComparison()) > 100){
                        sk5.setVisibility(View.VISIBLE);
                    }
                }else{
                    kondisi.setText("Cahaya tanaman sudah cukup");
                    sk1.setVisibility(View.VISIBLE);
                    sk2.setVisibility(View.VISIBLE);
                    sk3.setVisibility(View.VISIBLE);
                }
            }
            return convertView;
        }
    }
    private void scheduleAlarm(String[] campur, int cahayamin, int cahayamax, String id_var) {
//        Log.d("Mulai", "Mulai");
        Intent inm = new Intent(getContext(), AlarmReceiver.class);
        inm.putExtra("campur", campur);
        inm.putExtra("cahayamin", cahayamin);
        inm.putExtra("cahayamax", cahayamax);
        inm.putExtra("id_var", id_var);
        PendingIntent pi = PendingIntent.getBroadcast(getActivity(), AlarmReceiver.REQUEST_CODE,
                inm, PendingIntent.FLAG_UPDATE_CURRENT);
        long firstMillis = System.currentTimeMillis();
        AlarmManager am = (AlarmManager)getActivity().getSystemService(Context.ALARM_SERVICE);
        am.setInexactRepeating(AlarmManager.RTC_WAKEUP, firstMillis, 60*1000, pi);
    }
    private void stopAlarm(){
        Intent intentstop = new Intent(getContext(), AlarmReceiver.class);
        PendingIntent senderstop = PendingIntent.getBroadcast(getActivity(),
                12345, intentstop, 0);
        AlarmManager alarmManagerstop = (AlarmManager)getActivity().getSystemService(Context.ALARM_SERVICE);
        alarmManagerstop.cancel(senderstop);
//        Log.d("Hasilnya", "Alarm Stopped");
    }
}
