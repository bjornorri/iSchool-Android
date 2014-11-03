package com.orangejam.ischool.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.orangejam.ischool.R;

import java.util.Calendar;

/**
 * Created by bjornorri on 03/11/14.
 */
public class FragmentContainer extends Fragment {

    private int mTab;

    public void setTabIndex(int tab) {
        mTab = tab;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("", "Container created");
        return inflater.inflate(R.layout.fragment_container, container, false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(mTab == 0) {
            Log.d("", "Adding fragment");
            int day = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
            Bundle bundle = new Bundle();
            bundle.putInt("Day", day);

            // Create the fragment.
            TimetableFragment fragment = new TimetableFragment();
            fragment.setArguments(bundle);
            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            transaction.add(R.id.fragment_container, fragment);
            transaction.commit();
        } else if(mTab == 1) {
            AssignmentTableFragment fragment = new AssignmentTableFragment();
            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            transaction.add(R.id.fragment_container, fragment);
            transaction.commit();
        } else if(mTab == 2) {
            GradeFragment fragment = new GradeFragment();
            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            transaction.add(R.id.fragment_container, fragment);
            transaction.commit();
        }
    }
}
