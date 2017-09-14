package com.example.root.penulisanilmiah;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class NotifFragment extends Fragment {
    View v1;
    ListView lv_notif;
    TextView tv_null_notif;
    public String [] campur;
    public ArrayList<Notif> arr_notif;
    public Notif nf;
    public NotifFragment() {
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v1 = inflater.inflate(R.layout.fragment_notif, container, false);
        Bundle i = getArguments();
        campur = i.getStringArray("campur");
        lv_notif = (ListView)v1.findViewById(R.id.list_notif);
        tv_null_notif = (TextView)v1.findViewById(R.id.tv_notif_null);
        DatabaseReference mDb = FirebaseDatabase.getInstance().getReference().child("Devices")
                .child(campur[0]).child("Notif");
        mDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() > 0){
                    arr_notif = new ArrayList<>();
                    for (DataSnapshot ds2: dataSnapshot.getChildren()){
                        nf = ds2.getValue(Notif.class);
                        if (nf != null){
                            arr_notif.add(nf);
                        }
                    }
                    Collections.reverse(arr_notif);
                    CustomListAdapter2 cs = new CustomListAdapter2(v1.getContext(), arr_notif);
                    lv_notif.setAdapter(cs);
                    tv_null_notif.setVisibility(View.GONE);
                    lv_notif.setVisibility(View.VISIBLE);
                }else{
                    lv_notif.setVisibility(View.GONE);
                    tv_null_notif.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        return v1;
    }
    private class CustomListAdapter2 extends ArrayAdapter<Notif> {
        TextView device, waktu, isi;
        CustomListAdapter2(@NonNull Context context, @NonNull ArrayList<Notif> objects) {
            super(context, 0, objects);
        }
        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            Notif ntf = getItem(position);
            if (convertView == null){
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.notif_item, parent, false);
            }
            device = (TextView)convertView.findViewById(R.id.device_notif);
            waktu = (TextView)convertView.findViewById(R.id.waktu_notif);
            isi = (TextView)convertView.findViewById(R.id.isi_notif);
            if (ntf.getDevice() != null){
                device.setText(ntf.getDevice());
            }
            if (ntf.getWaktu() != null){
                waktu.setText(ntf.getWaktu().substring(11));
            }
            if (ntf.getIsi() != null){
                isi.setText(ntf.getIsi());
            }
            return convertView;
        }
    }
}
