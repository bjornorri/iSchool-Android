package com.orangejam.ischool.adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.orangejam.ischool.R;
import com.orangejam.ischool.fragments.*;

import java.util.Calendar;


/**
 * Created by bjornorri on 09/10/14.
 */
public class TabsPagerAdapter extends FragmentPagerAdapter {

    private Context mContext;

    public TabsPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int i) {
        boolean isTablet = mContext.getResources().getBoolean(R.bool.isTablet);
        switch(i) {
            case 0:
                int day = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
                Bundle bundle = new Bundle();
                bundle.putInt("Day", day);
                TimetableFragment fragment = new TimetableFragment();
                fragment.setArguments(bundle);
                return fragment;
            case 1:
                if(isTablet) {
                    return new AssignmentMasterDetailFragment();
                } else {
                    return new AssignmentTableFragment();
                }
            case 2:
                if(isTablet) {
                    return new GradeMasterDetailFragment();
                } else {
                    return new GradeFragment();
                }
        }
        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }
}
