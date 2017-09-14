package com.example.root.penulisanilmiah;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;

public class AddDeviceActivity extends AppCompatActivity {
    SurfaceView cameraView;
    BarcodeDetector barcode;
    CameraSource cameraSource;
    SurfaceHolder holder;
    EditText et_dv_name;
    String nama_device;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_device);
        cameraView = (SurfaceView)findViewById(R.id.cameraView);
        et_dv_name = (EditText)findViewById(R.id.dv_name);
        cameraView.setZOrderMediaOverlay(true);
        holder = cameraView.getHolder();
        barcode = new BarcodeDetector.Builder(this).setBarcodeFormats(Barcode.QR_CODE).build();
        if (!barcode.isOperational()){
            Toast.makeText(getApplicationContext(), "Sorry, couldn't set up detector", Toast.LENGTH_SHORT).show();
            this.finish();
        }
        cameraSource = new CameraSource.Builder(this, barcode)
                .setRequestedPreviewSize(1600, 1024)
                .setAutoFocusEnabled(true)
                .build();
        cameraView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                if (ContextCompat.checkSelfPermission(AddDeviceActivity.this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
                    try {
                        cameraSource.start(cameraView.getHolder());
                    } catch (IOException e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }
            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
            }
        });
        barcode.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
            }
            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                SparseArray<Barcode> barcodes = detections.getDetectedItems();
                if (barcodes.size() > 0){
                    if (!TextUtils.isEmpty(et_dv_name.getText().toString())){
                        nama_device = et_dv_name.getText().toString();
                    }else{
                        Toast.makeText(getApplicationContext(), "Nama device belum diisi", Toast.LENGTH_SHORT).show();
                    }
                    Intent i = new Intent(AddDeviceActivity.this, DeviceActivity.class);
                    i.putExtra("nama", nama_device);
                    i.putExtra("barcode", barcodes.valueAt(0));
                    setResult(RESULT_OK, i);
                    finish();
                }
            }
        });
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraSource.release();
        barcode.release();
    }
}
