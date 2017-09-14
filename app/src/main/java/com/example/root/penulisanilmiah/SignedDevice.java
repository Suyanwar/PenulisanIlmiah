package com.example.root.penulisanilmiah;

import android.content.Context;
import android.content.SharedPreferences;

public class SignedDevice {
    public static boolean isFirst(Context ctx, String nama){
        SharedPreferences pref = ctx.getSharedPreferences("isFirst", Context.MODE_PRIVATE);
        boolean first = pref.getBoolean("is_first_"+nama, true);
        if (first){
            SharedPreferences.Editor editor = pref.edit();
            editor.putBoolean("is_first_"+nama, false);
            editor.commit();
        }
        return first;
    }
}
