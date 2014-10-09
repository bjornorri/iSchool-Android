package com.orangejam.ischool.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.orangejam.ischool.fragments.TimetableFragment;

/**
 * Created by bjornorri on 09/10/14.
 */
public class TabsPagerAdapter extends FragmentPagerAdapter {

    public TabsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        switch(i) {
            case 0:
                return new TimetableFragment();
            case 1:
                return new TimetableFragment();
            case 2:
                return new TimetableFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }
}
