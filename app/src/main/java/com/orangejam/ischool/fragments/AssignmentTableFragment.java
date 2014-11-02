package com.orangejam.ischool.fragments;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.ListFragment;
import android.widget.ListView;
import android.widget.TextView;

import com.orangejam.ischool.adapters.AssignmentAdapter;
import com.orangejam.ischool.R;
import com.orangejam.ischool.modules.Constants;
import com.orangejam.ischool.modules.DataStore;
import com.orangejam.ischool.model.Assignment;
import com.orangejam.ischool.modules.RefreshLayout;

import java.util.ArrayList;

/**
 * Created by arnorymir on 14/10/14.
 */
public class AssignmentTableFragment extends ListFragment {

    private RefreshLayout mSwipeLayout;
    private AssignmentAdapter mAdapter;
    private Context mContext;
    private TextView mEmptyLabel;

    private ArrayList<Assignment> mAssignments = new ArrayList<Assignment>();

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mSwipeLayout.setRefreshing(false);
            if(intent.getAction().equals(Constants.AssignmentsNotification)) {
                mAssignments.clear();
                mAssignments.addAll(DataStore.getInstance(context).getAssignments());
                if(mAssignments.isEmpty()) {
                    mEmptyLabel.setVisibility(View.VISIBLE);
                } else {
                    mEmptyLabel.setVisibility(View.GONE);
                }
                mAdapter.notifyDataSetChanged();
            }
        }
    };


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the view
        View rootView = inflater.inflate(R.layout.fragment_assignments, container, false);

        if(mAssignments.size() <= 0){
            Log.i("","assignemnt 0");
        }

        mContext = getActivity().getApplicationContext();

        // Get the empty label.
        mEmptyLabel = (TextView) rootView.findViewById(R.id.emptyLabel);

        // Configure SwipeRefreshLayout.
        mSwipeLayout = (RefreshLayout) rootView.findViewById(R.id.swipe_container);
        mSwipeLayout.setChildView((ListView) rootView.findViewById(android.R.id.list));
        mSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                DataStore.getInstance(getActivity().getApplicationContext()).fetchAssignmentsAndGrades();

            }
        });
        mSwipeLayout.setColorSchemeResources(android.R.color.holo_red_light,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_blue_bright);

        ArrayList<Assignment> assignments = DataStore.getInstance(mContext).getAssignments();
        if(assignments != null) {
            mAssignments = assignments;
            if(mAssignments.isEmpty()) {
                mEmptyLabel.setVisibility(View.VISIBLE);
            } else {
                mEmptyLabel.setVisibility(View.GONE);
            }
        }
        mAdapter = new AssignmentAdapter(getActivity(), R.layout.assignment_cell, mAssignments);
        setListAdapter(mAdapter);

        return rootView;

    }

    @Override
    public void onResume() {
        super.onResume();

        // Register broadcast receiver.
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.AssignmentsNotification);
        filter.addAction(Constants.InvalidCredentialsNotification);
        filter.addAction(Constants.NetworkErrorNotification);
        LocalBroadcastManager.getInstance(mContext).registerReceiver(mBroadcastReceiver, filter);
        DataStore.getInstance(mContext).fetchAssignmentsAndGrades();
    }

    @Override
    public void onPause() {
        super.onPause();

        // Unregister broadcast receiver.
        LocalBroadcastManager.getInstance(mContext).unregisterReceiver(mBroadcastReceiver);
    }



}
