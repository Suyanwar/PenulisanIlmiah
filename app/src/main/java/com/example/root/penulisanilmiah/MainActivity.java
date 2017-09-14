package com.example.root.penulisanilmiah;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
public class MainActivity extends AppCompatActivity {
    public ViewPager vp1;
    public TabLayout tab1;
    public ViewPagerAdapter vpa1;
    public int [] icons1 = {R.drawable.ic_home, R.drawable.ic_notif};
    TextView tv_judul;
    ImageView ic_info;
    public String [] campur = new String[3];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent ii = getIntent();
        campur[0] = ii.getStringExtra("key");
        campur[1] = ii.getStringExtra("token");
        campur[2] = ii.getStringExtra("id_device");
        vp1 = (ViewPager)findViewById(R.id.pager_plant);
        tab1 = (TabLayout)findViewById(R.id.tab_plant);
        tv_judul = (TextView)findViewById(R.id.judul_toolbar);
        ic_info = (ImageView)findViewById(R.id.info_toolbar);
        tv_judul.setTypeface(Typeface.createFromAsset(MainActivity.this.getAssets(), "fonts/Quango.ttf"));
        tv_judul.setText(campur[0]);
        ic_info.setImageResource(R.drawable.ic_setting);
        vpa1 = new ViewPagerAdapter(getSupportFragmentManager());
        vpa1.addFragment(home(campur));
        vpa1.addFragment(notif(campur));
        vp1.setAdapter(vpa1);
        tab1.setupWithViewPager(vp1);
        for (int i = 0; i<2; i++){
            tab1.getTabAt(i).setIcon(icons1[i]);
        }
        ic_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent setting = new Intent(MainActivity.this, SettingActivity.class);
                setting.putExtra("campur", campur);
                finish();
                startActivity(setting);
            }
        });
    }
    public Fragment home(String [] campur){
        Bundle a = new Bundle();
        a.putStringArray("campur", campur);
        HomeFragment hf = new HomeFragment();
        hf.setArguments(a);
        return hf;
    }
    public Fragment notif(String [] campur){
        Bundle aa = new Bundle();
        aa.putStringArray("campur", campur);
        NotifFragment nf = new NotifFragment();
        nf.setArguments(aa);
        return nf;
    }
    @Override
    public void onBackPressed() {
        Intent i = new Intent(MainActivity.this, DeviceActivity.class);
        finish();
        startActivity(i);
    }
}
