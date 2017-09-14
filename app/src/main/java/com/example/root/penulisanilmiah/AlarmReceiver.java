package com.example.root.penulisanilmiah;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
//import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver{
    public static final int REQUEST_CODE = 12345;
    @Override
    public void onReceive(Context context, Intent intent) {
//        Log.d("Mulai1", "Mulai");
        Intent ini = new Intent(context, ServiceDevice.class);
        ini.putExtra("campur", intent.getStringArrayExtra("campur"));
        ini.putExtra("cahayamin", intent.getIntExtra("cahayamin", 0));
        ini.putExtra("cahayamax", intent.getIntExtra("cahayamax", 0));
        ini.putExtra("id_var", intent.getStringExtra("id_var"));
        context.startService(ini);
    }
}
