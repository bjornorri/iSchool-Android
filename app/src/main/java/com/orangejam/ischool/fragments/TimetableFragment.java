package com.orangejam.ischool.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.orangejam.ischool.R;
import com.orangejam.ischool.adapters.TimetableAdapter;
import com.orangejam.ischool.modules.Constants;
import com.orangejam.ischool.modules.DataStore;

public class TimetableFragment extends ListFragment {

    private SwipeRefreshLayout mSwipeLayout;
    private TimetableAdapter mAdapter;
    private Context mContext;
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("", "Received!!!");
            mAdapter.notifyDataSetChanged();
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_timetable, container, false);

        // Get context.
        mContext = getActivity().getApplicationContext();

        // Configure SwipeRefreshLayout.
        mSwipeLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_container);
        mSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.d("TimetableFragment", "Refreshing");
                DataStore.getInstance(getActivity().getApplicationContext()).fetchClasses();
            }
        });
        mSwipeLayout.setColorSchemeResources(android.R.color.holo_red_light,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_blue_bright);

        // Set list view adapter.
        mAdapter = new TimetableAdapter(mContext, R.layout.class_cell, DataStore.getInstance(mContext).getClasses());
        setListAdapter(mAdapter);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        // Register broadcast receiver.
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.TimetableNotification);
        LocalBroadcastManager.getInstance(mContext).registerReceiver(mBroadcastReceiver, filter);

        DataStore.getInstance(mContext).fetchClasses();
    }

    @Override
    public void onPause() {
        super.onPause();

        // Unregister broadcast receiver.
        LocalBroadcastManager.getInstance(mContext).unregisterReceiver(mBroadcastReceiver);
    }
}