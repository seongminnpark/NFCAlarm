package com.main.seongmin.nfcalarm;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by seongmin on 12/22/16.
 */
public class TabPagerAdapter extends FragmentPagerAdapter {

    public TabPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        Fragment fragment;
        switch (i) {
            case  0: fragment = new AlarmListFragment(); break;
            case  1: fragment = new NFCListFragment();   break;
            default: fragment = new AlarmListFragment(); break;
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        CharSequence title;
        switch (position) {
            case  0: title = "Alarms"; break;
            case  1: title = "NFC";    break;
            default: title = "Error"; break;
        }
        return title;
    }

}
