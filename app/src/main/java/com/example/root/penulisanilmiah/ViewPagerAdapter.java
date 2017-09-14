package com.example.root.penulisanilmiah;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class ViewPagerAdapter extends FragmentPagerAdapter{
    private List<Fragment> mFragments =  new ArrayList<>();
    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }
    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }
    @Override
    public int getCount() {
        return mFragments.size();
    }
    void addFragment(Fragment fragment){
        mFragments.add(fragment);
    }
}
