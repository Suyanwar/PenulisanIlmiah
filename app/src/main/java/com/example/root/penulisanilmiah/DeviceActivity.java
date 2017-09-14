package com.example.root.penulisanilmiah;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class DeviceActivity extends AppCompatActivity{
    public ViewPager vp;
    public TabLayout tab;
    ViewPagerAdapter vpa;
    public int [] icons = {R.drawable.ic_device, R.drawable.ic_tab_dashboard};
    private static final int TIME_DELAY = 2000;
    private static long back_pressed;
    TextView tv_judul;
    ImageView ic_info;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);
        vp = (ViewPager)findViewById(R.id.pager_device);
        tab = (TabLayout)findViewById(R.id.tab_device);
        tv_judul = (TextView)findViewById(R.id.judul_toolbar);
        ic_info = (ImageView)findViewById(R.id.info_toolbar);
        tv_judul.setTypeface(Typeface.createFromAsset(DeviceActivity.this.getAssets(), "fonts/Quango.ttf"));
        vpa = new ViewPagerAdapter(getSupportFragmentManager());
        vpa.addFragment(new DeviceFragment());
        vpa.addFragment(new DashBoardFragment());
        vp.setAdapter(vpa);
        tab.setupWithViewPager(vp);
        for (int i = 0; i<2; i++){
            tab.getTabAt(i).setIcon(icons[i]);
        }
        ic_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder al = new AlertDialog.Builder(DeviceActivity.this);
                al.setTitle("LUX STABILIZER").setMessage("This is your personal app which can keep the lux on ideal condition.").setIcon(R.drawable.ic_info)
                .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                al.show();
            }
        });
    }
    @Override
    public void onBackPressed() {
        if (back_pressed + TIME_DELAY > System.currentTimeMillis()) {
            super.onBackPressed();
        } else {
            Toast.makeText(getBaseContext(), "Press once again to exit!", Toast.LENGTH_SHORT).show();
        }
        back_pressed = System.currentTimeMillis();
    }
}
