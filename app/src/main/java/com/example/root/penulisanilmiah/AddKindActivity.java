package com.example.root.penulisanilmiah;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.florescu.android.rangeseekbar.RangeSeekBar;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class AddKindActivity extends AppCompatActivity {
    public String id_jenis, userChoosenTask, image_name, nama;
    public String [] campur;
    TextView judul, null_image;
    EditText et_jenis;
    ImageView add_image;
    RangeSeekBar<Integer> cahaya;
    FrameLayout layout;
    Button btnAddKind;
    ProgressDialog mProgress;
    CharSequence[] options = {"Take Photo", "Choose from Gallery"};
    public Uri imageUri = null;
    public static final int IMAGE_GALLERY = 0, TAKE_CAMERA = 1;
    public DatabaseReference mDb;
    public StorageReference mStorage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_kind);
        Intent i = getIntent();
        id_jenis = i.getStringExtra("id_jenis");
        campur = i.getStringArrayExtra("campur");
        judul = (TextView)findViewById(R.id.judul);
        null_image = (TextView)findViewById(R.id.tv_null_image);
        et_jenis =(EditText)findViewById(R.id.tipe_tanaman);
        add_image = (ImageView)findViewById(R.id.add_kind_photo);
        cahaya = new RangeSeekBar<>(this);
        cahaya.setRangeValues(0, 1000);
        cahaya.setTextAboveThumbsColorResource(android.R.color.white);
        layout = (FrameLayout)findViewById(R.id.cahayaideal);
        layout.addView(cahaya);
        btnAddKind = (Button)findViewById(R.id.btnAddKind);
        mProgress = new ProgressDialog(this);
        add_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
        if (!id_jenis.equals("Baru")){
            mDb = FirebaseDatabase.getInstance().getReference().child("Jenis").child(id_jenis);
            mDb.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child("namaimg").exists()){
                        nama = dataSnapshot.child("namaimg").getValue().toString();
                    }
                    if (dataSnapshot.child("link_image").exists()){
                        Picasso.with(getApplicationContext()).load(Uri.parse(dataSnapshot.child("link_image").getValue().toString())).into(add_image);
                        null_image.setVisibility(View.GONE);
                    }
                    if (dataSnapshot.child("jenis").exists()){
                        et_jenis.setText(dataSnapshot.child("jenis").getValue().toString());
                    }
                    et_jenis.setFocusable(false);
                    cahaya.setSelectedMinValue(Integer.parseInt(dataSnapshot.child("cahayamin").getValue().toString()));
                    cahaya.setSelectedMaxValue(Integer.parseInt(dataSnapshot.child("cahayamax").getValue().toString()));
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
            btnAddKind.setText("Update Jenis");
            judul.setText("Update Kind");
        }
        btnAddKind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (id_jenis.equals("Baru")){
                    if (imageUri != null && !et_jenis.getText().toString().isEmpty()){
                        mDb = FirebaseDatabase.getInstance().getReference().child("Jenis").push();
                        image_name = et_jenis.getText().toString()+"-"+imageUri.getLastPathSegment();
                        mStorage = FirebaseStorage.getInstance().getReference().child(image_name);
                        mProgress.setMessage("Adding...");
                        mProgress.show();
                        addkind();
                    }else{
                        Toast.makeText(AddKindActivity.this, "All fields are required!", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    mDb = FirebaseDatabase.getInstance().getReference().child("Jenis").child(id_jenis);
                    mProgress.setMessage("Updating...");
                    mProgress.show();
                    editkind();
                }
            }
        });
    }
    private void editkind() {
        if (imageUri != null){
            image_name = et_jenis.getText().toString()+"-"+imageUri.getLastPathSegment();
            mStorage = FirebaseStorage.getInstance().getReference().child(image_name);
            StorageReference mDelete = FirebaseStorage.getInstance().getReference().child(nama);
            mDelete.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                }
            });
            mStorage.putFile(Uri.fromFile(new File(compressImage(imageUri.toString())))).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    @SuppressWarnings("VisibleForTests") Uri dl_img = taskSnapshot.getDownloadUrl();
                    mDb.child("namaimg").setValue(image_name);
                    mDb.child("link_image").setValue(dl_img.toString());
                    mDb.child("jenis").setValue(et_jenis.getText().toString());
                    mDb.child("cahayamin").setValue(cahaya.getSelectedMinValue());
                    mDb.child("cahayamax").setValue(cahaya.getSelectedMaxValue());
                    mProgress.dismiss();
                    Toast.makeText(AddKindActivity.this, "Kind of plant has been updated!", Toast.LENGTH_SHORT).show();
                    updateActivity();
                }
            });
        }else{
            mDb.child("jenis").setValue(et_jenis.getText().toString());
            mDb.child("cahayamin").setValue(cahaya.getSelectedMinValue());
            mDb.child("cahayamax").setValue(cahaya.getSelectedMaxValue());
            mProgress.dismiss();
            Toast.makeText(AddKindActivity.this, "Kind of plant has been updated!", Toast.LENGTH_SHORT).show();
            updateActivity();
        }
    }
    private void updateActivity() {
        Intent x = new Intent(AddKindActivity.this, ListKindActivity.class);
        x.putExtra("campur", campur);
        finish();
        startActivity(x);
    }
    private void addkind() {
        mStorage.putFile(Uri.fromFile(new File(compressImage(imageUri.toString())))).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                @SuppressWarnings("VisibleForTests") Uri dl_img = taskSnapshot.getDownloadUrl();
                mDb.child("namaimg").setValue(image_name);
                mDb.child("link_image").setValue(dl_img.toString());
                mDb.child("jenis").setValue(et_jenis.getText().toString());
                mDb.child("cahayamin").setValue(cahaya.getSelectedMinValue());
                mDb.child("cahayamax").setValue(cahaya.getSelectedMaxValue());
                mProgress.dismiss();
                Toast.makeText(AddKindActivity.this, "Kind of plant has been added!", Toast.LENGTH_SHORT).show();
                updateActivity();
            }
        });
    }
    private void selectImage() {
        AlertDialog.Builder builder = new AlertDialog.Builder(AddKindActivity.this);
        builder.setTitle("Add Image");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result=Utility.checkPermission(AddKindActivity.this);
                if (options[item].equals("Take Photo")) {
                    userChoosenTask ="Take Photo";
                    if(result)
                        cameraIntent();
                } else if (options[item].equals("Choose from Gallery")) {
                    userChoosenTask ="Choose from Gallery";
                    if(result)
                        galleryIntent();
                }
            }
        });
        builder.show();
    }
    private void galleryIntent() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), IMAGE_GALLERY);
    }
    private void cameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        imageUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "tmp_"+String.valueOf(System.currentTimeMillis())+".jpg"));
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        } else {
            File file = new File(imageUri.getPath());
            Uri photoUri = FileProvider.getUriForFile(getApplicationContext(), getApplicationContext().getPackageName() + ".provider", file);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
        }
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        if (intent.resolveActivity(getApplicationContext().getPackageManager()) != null) {
            startActivityForResult(intent, TAKE_CAMERA);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == TAKE_CAMERA && resultCode == RESULT_OK) {
            add_image.setImageURI(Uri.fromFile(new File(compressImage(imageUri.toString()))));
        } else if (requestCode == IMAGE_GALLERY && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            add_image.setImageURI(Uri.fromFile(new File(compressImage(imageUri.toString()))));
        }
        null_image.setVisibility(View.GONE);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case Utility.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if(userChoosenTask.equals("Take Photo"))
                        cameraIntent();
                    else if(userChoosenTask.equals("Choose from Gallery"))
                        galleryIntent();
                }
                break;
        }
    }
    @Override
    public void onBackPressed() {
        updateActivity();
    }
    public String compressImage(String imageUri) {
        String filePath = getRealPathFromURI(imageUri);
        Bitmap scaledBitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(filePath, options);
        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;
        float maxHeight = 816.0f;
        float maxWidth = 612.0f;
        float imgRatio = actualWidth / actualHeight;
        float maxRatio = maxWidth / maxHeight;
        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) maxHeight;
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;
            }
        }
        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);
        options.inJustDecodeBounds = false;
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[64 * 1024];
        try {
            bmp = BitmapFactory.decodeFile(filePath, options);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }
        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;
        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);
        if (scaledBitmap != null){
            Canvas canvas = new Canvas(scaledBitmap);
            canvas.setMatrix(scaleMatrix);
            canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));
        }
        ExifInterface exif;
        try {
            exif = new ExifInterface(filePath);
            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, 0);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
            } else if (orientation == 3) {
                matrix.postRotate(180);
            } else if (orientation == 8) {
                matrix.postRotate(270);
            }
            if (scaledBitmap != null){
                scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        FileOutputStream out = null;
        String filename = getFilename();
        try {
            out = new FileOutputStream(filename);
            if (scaledBitmap != null){
                scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return filename;
    }
    public String getFilename() {
        File file = new File(Environment.getExternalStorageDirectory().getPath(), "MyFolder/Images");
        if (!file.exists()) {
            file.mkdirs();
        }
        String uriSting = (file.getAbsolutePath() + "/" + System.currentTimeMillis() + ".jpg");
        return uriSting;
    }
    private String getRealPathFromURI(String contentURI) {
        Uri contentUri = Uri.parse(contentURI);
        Cursor cursor = getContentResolver().query(contentUri, null, null, null, null);
        if (cursor == null) {
            return contentUri.getPath();
        } else {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(index);
        }
    }
    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }
        return inSampleSize;
    }
}
