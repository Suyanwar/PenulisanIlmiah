package com.example.root.penulisanilmiah;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AddPlantActivity extends AppCompatActivity {
    EditText et_plant_name;
    Spinner sp_jenis;
    Button add_plant, move_kind;
    DatabaseReference mDb, mAdd, mImage;
    ArrayList<String> list_jenis;
    public String [] campur;
    public String jenis, link_image, id_jenis;
    public ProgressDialog mProgress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_plant);
        Intent z = getIntent();
        campur = z.getStringArrayExtra("campur");
        et_plant_name = (EditText)findViewById(R.id.plant_name);
        sp_jenis = (Spinner)findViewById(R.id.plant_jenis);
        add_plant = (Button)findViewById(R.id.btnAddPlant);
        move_kind = (Button)findViewById(R.id.moveAddKind);
        mProgress = new ProgressDialog(this);
        move_kind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent y = new Intent(AddPlantActivity.this, ListKindActivity.class);
                y.putExtra("campur", campur);
                finish();
                startActivity(y);
            }
        });
        mDb = FirebaseDatabase.getInstance().getReference().child("Jenis");
        mDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                list_jenis = new ArrayList<>();
                list_jenis.add("Pilih jenis tanaman");
                if (dataSnapshot.getChildrenCount() > 0){
                    for (DataSnapshot ds: dataSnapshot.getChildren()){
                        if (ds.child("jenis").exists()){
                            list_jenis.add(ds.child("jenis").getValue().toString());
                        }
                    }
                }
                ArrayAdapter adapter = new ArrayAdapter<>(AddPlantActivity.this, R.layout.kind_item, R.id.spinner_item, list_jenis);
                sp_jenis.setAdapter(adapter);
                sp_jenis.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if (!parent.getItemAtPosition(position).toString().equals("Pilih jenis tanaman")){
                            jenis = parent.getItemAtPosition(position).toString();
                        }else{
                            jenis = "Pilih jenis tanaman";
                        }
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        jenis = "Pilih jenis tanaman";
                    }
                });
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        add_plant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!et_plant_name.getText().toString().isEmpty() && !jenis.equals("Pilih jenis tanaman")){
                    mAdd = FirebaseDatabase.getInstance().getReference().child("Devices").child(campur[0])
                            .child("Tanaman").push();
                    mImage = FirebaseDatabase.getInstance().getReference().child("Jenis");
                    mProgress.setMessage("Adding...");
                    mProgress.show();
                    mImage.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot ds3: dataSnapshot.getChildren()){
                                if (ds3.child("jenis").getValue().toString().equals(jenis)){
                                    link_image = ds3.child("link_image").getValue().toString();
                                    id_jenis = ds3.getKey();
                                }
                            }
                            mAdd.child("nm_tnmn").setValue(et_plant_name.getText().toString());
                            mAdd.child("link_image").setValue(link_image);
                            mAdd.child("jenis").setValue(id_jenis);
                            mAdd.child("activate").setValue("0");
                            mAdd.child("comparison").setValue("0");
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
                    mProgress.dismiss();
                    Toast.makeText(AddPlantActivity.this, "Add plant is succeed!", Toast.LENGTH_SHORT).show();
                    Intent ii = new Intent(AddPlantActivity.this, MainActivity.class);
                    ii.putExtra("key", campur[0]);
                    ii.putExtra("id_device", campur[2]);
                    ii.putExtra("token", campur[1]);
                    finish();
                    startActivity(ii);
                }else{
                    Toast.makeText(AddPlantActivity.this, "All fields are required!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    @Override
    public void onBackPressed() {
        Intent ii = new Intent(AddPlantActivity.this, MainActivity.class);
        ii.putExtra("key", campur[0]);
        ii.putExtra("id_device", campur[2]);
        ii.putExtra("token", campur[1]);
        finish();
        startActivity(ii);
    }
}
