package com.orangejam.ischool.fragments;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.ListFragment;

import com.orangejam.ischool.adapters.GradeAdapter;
import com.orangejam.ischool.model.*;
import com.orangejam.ischool.model.Class;
import com.orangejam.ischool.R;

import com.orangejam.ischool.modules.Constants;
import com.orangejam.ischool.modules.DataStore;
import com.orangejam.ischool.model.Assignment;

import java.util.ArrayList;

/**
 * Created by arnorymir on 14/10/14.
 */
public class GradeFragment extends ListFragment {

    private SwipeRefreshLayout mSwipeLayout;
    private GradeAdapter mAdapter;
    private Context mContext;

    private ArrayList<Grade> mGrades = new ArrayList<Grade>();

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("GradeFragment", "Received!!!" + intent.getAction());
            mSwipeLayout.setRefreshing(false);
            if(intent.getAction().equals(Constants.GradesNotification)) {
                Log.d("GradeFragment", "Updating list view");
                mGrades.clear();
                mGrades.addAll(DataStore.getInstance(context).getGrades());
                mAdapter.notifyDataSetChanged();
            }
        }
    };


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the view
        View rootView = inflater.inflate(R.layout.fragment_timetable, container, false);

        mContext = getActivity().getApplicationContext();

        // Configure SwipeRefreshLayout.
        mSwipeLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_container);
        mSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.d("GradeFragment", "Refreshing");
                DataStore.getInstance(getActivity().getApplicationContext()).fetchAssignmentsAndGrades();

            }
        });
        mSwipeLayout.setColorSchemeResources(android.R.color.holo_red_light,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_blue_bright);

        ArrayList<Grade> grades = DataStore.getInstance(mContext).getGrades();
        if(grades != null) {
            mGrades = grades;
        }


        mAdapter = new GradeAdapter(getActivity(), R.layout.grade_cell, mGrades);
        setListAdapter(mAdapter);

        return rootView;

    }

    @Override
    public void onResume() {
        super.onResume();

        // Register broadcast receiver.
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.GradesNotification);
        filter.addAction(Constants.InvalidCredentialsNotification);
        filter.addAction(Constants.NetworkErrorNotification);
        LocalBroadcastManager.getInstance(mContext).registerReceiver(mBroadcastReceiver, filter);
        DataStore.getInstance(mContext).fetchAssignmentsAndGrades();
        Log.d("gradeFragment", "Registered receiver");
    }

    @Override
    public void onPause() {
        super.onPause();

        // Unregister broadcast receiver.
        LocalBroadcastManager.getInstance(mContext).unregisterReceiver(mBroadcastReceiver);
        Log.d("GradeFragment", "Unregistered receiver");
    }



}