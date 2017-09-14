package com.example.root.penulisanilmiah;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ListKindActivity extends AppCompatActivity {
    Button addKind;
    ListView lv_jenis;
    TextView tv_null_jenis;
    DatabaseReference mDb;
    ArrayList<String> list_jenis, id_jenis;
    public String [] campur;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_kind);
        Intent y = getIntent();
        campur = y.getStringArrayExtra("campur");
        addKind = (Button)findViewById(R.id.tambah_jenis);
        lv_jenis = (ListView)findViewById(R.id.list_kind);
        tv_null_jenis = (TextView)findViewById(R.id.tv_null_kind);
        mDb = FirebaseDatabase.getInstance().getReference().child("Jenis");
        mDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() > 0){
                    list_jenis = new ArrayList<>();
                    id_jenis = new ArrayList<>();
                    for (DataSnapshot ds: dataSnapshot.getChildren()){
                        id_jenis.add(ds.getKey());
                        if (ds.child("jenis").exists()){
                            list_jenis.add(ds.child("jenis").getValue().toString());
                        }
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(ListKindActivity.this, R.layout.kind_item, R.id.spinner_item, list_jenis);
                    lv_jenis.setAdapter(adapter);
                    lv_jenis.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Intent i = new Intent(ListKindActivity.this, AddKindActivity.class);
                            i.putExtra("id_jenis", id_jenis.get(position));
                            i.putExtra("campur", campur);
                            finish();
                            startActivity(i);
                        }
                    });
                    lv_jenis.setVisibility(View.VISIBLE);
                    tv_null_jenis.setVisibility(View.GONE);
                }else{
                    lv_jenis.setVisibility(View.GONE);
                    tv_null_jenis.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        addKind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ii = new Intent(ListKindActivity.this, AddKindActivity.class);
                ii.putExtra("id_jenis", "Baru");
                ii.putExtra("campur", campur);
                finish();
                startActivity(ii);
            }
        });
    }
    @Override
    public void onBackPressed() {
        Intent iii = new Intent(ListKindActivity.this, AddPlantActivity.class);
        iii.putExtra("campur", campur);
        finish();
        startActivity(iii);
    }
}
