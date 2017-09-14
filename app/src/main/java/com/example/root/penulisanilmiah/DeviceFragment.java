package com.example.root.penulisanilmiah;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.barcode.Barcode;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

import static android.app.Activity.RESULT_OK;


public class DeviceFragment extends Fragment {
    View v0;
    TextView tvnull;
    ListView lvDevice;
    FloatingActionButton add_dev;
    public DatabaseReference mDb;
    public ArrayList<Device> dev_list;
    public ArrayList<String> key_device, id_device, token_device;
    public Device dvc;
    public static final int REQUEST_CODE = 100;
    public static final int PERMISSION_REQUEST = 200;
    public DeviceFragment() {
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v0 = inflater.inflate(R.layout.fragment_device, container, false);
        tvnull = (TextView)v0.findViewById(R.id.tv_null);
        lvDevice = (ListView)v0.findViewById(R.id.list_device);
        add_dev = (FloatingActionButton)v0.findViewById(R.id.add_device);
        mDb = FirebaseDatabase.getInstance().getReference().child("Devices");
        mDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() > 0){
                    dev_list = new ArrayList<>();
                    key_device = new ArrayList<>();
                    id_device = new ArrayList<>();
                    token_device = new ArrayList<>();
                    for (DataSnapshot ds1: dataSnapshot.getChildren()){
                        key_device.add(ds1.getKey());
                        dvc = ds1.getValue(Device.class);
                        if (dvc != null){
                            dev_list.add(dvc);
                        }
                        if (ds1.child("id_device").exists()){
                            id_device.add(ds1.child("id_device").getValue().toString());
                        }
                        if (ds1.child("token").exists()){
                            token_device.add(ds1.child("token").getValue().toString());
                        }
                    }
                    Collections.reverse(dev_list);
                    Collections.reverse(key_device);
                    Collections.reverse(id_device);
                    Collections.reverse(token_device);
                    CustomListAdapter adapter = new CustomListAdapter(v0.getContext(), dev_list);
                    lvDevice.setAdapter(adapter);
                    tvnull.setVisibility(View.GONE);
                    lvDevice.setVisibility(View.VISIBLE);
                    lvDevice.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Intent i = new Intent(getActivity(), MainActivity.class);
                            i.putExtra("key", key_device.get(position));
                            i.putExtra("id_device", id_device.get(position));
                            i.putExtra("token", token_device.get(position));
                            getActivity().finish();
                            startActivity(i);
                        }
                    });
                }else{
                    tvnull.setVisibility(View.VISIBLE);
                    lvDevice.setVisibility(View.GONE);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        add_dev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ii = new Intent(getActivity(), AddDeviceActivity.class);
                if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.CAMERA}, PERMISSION_REQUEST);
                }
                startActivityForResult(ii, REQUEST_CODE);
            }
        });
        return v0;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK){
            if (data != null){
                Barcode barcode = data.getParcelableExtra("barcode");
                String nama = data.getStringExtra("nama");
                String dv_info = barcode.displayValue;
                String [] info = dv_info.split("\\|");
                DatabaseReference mAdd = FirebaseDatabase.getInstance().getReference().child("Devices").child(info[0]);
                mAdd.child("nm_dv").setValue(nama);
                mAdd.child("nm_tnmn_a").setValue("-");
                mAdd.child("id_device").setValue(info[1]);
                mAdd.child("token").setValue(info[2]);
                Toast.makeText(getContext(), "Succeed", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(getContext(), "Failed", Toast.LENGTH_SHORT).show();
            }
        }
    }
    public class CustomListAdapter extends ArrayAdapter<Device>{
        TextView nm_dv, nm_tnmn_active;
        public CustomListAdapter(@NonNull Context context, @NonNull ArrayList<Device> objects) {
            super(context, 0, objects);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            Device device = getItem(position);
            if (convertView == null){
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.device_item, parent, false);
            }
            nm_dv = (TextView)convertView.findViewById(R.id.nama_device);
            nm_tnmn_active = (TextView)convertView.findViewById(R.id.nama_tanaman_active);
            nm_dv.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/Quango.ttf"));
            if (device.getNm_dv() != null){
                nm_dv.setText(device.getNm_dv());
            }
            if (device.getNm_tnmn_a() != null){
                nm_tnmn_active.setText("Tanaman aktif: "+device.getNm_tnmn_a());
            }
            return convertView;
        }
    }
}
