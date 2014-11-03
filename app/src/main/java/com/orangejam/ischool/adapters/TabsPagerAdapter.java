package com.orangejam.ischool.adapters;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.orangejam.ischool.fragments.*;

import java.util.Calendar;


/**
 * Created by bjornorri on 09/10/14.
 */
public class TabsPagerAdapter extends FragmentPagerAdapter {

    public TabsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        FragmentContainer container = new FragmentContainer();
        switch(i) {
            case 0:
                container.setTabIndex(0);
                return container;
            case 1:
                container.setTabIndex(1);
                return container;
            case 2:
                container.setTabIndex(2);
                return container;
        }
        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }
}
