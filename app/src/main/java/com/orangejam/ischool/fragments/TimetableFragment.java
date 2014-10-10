package com.orangejam.ischool.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.orangejam.ischool.R;
import com.orangejam.ischool.adapters.TimetableAdapter;
import com.orangejam.ischool.modules.Constants;
import com.orangejam.ischool.modules.DataStore;
import com.orangejam.ischool.model.Class;

import java.util.ArrayList;

public class TimetableFragment extends ListFragment {

    private SwipeRefreshLayout mSwipeLayout;
    private TimetableAdapter mAdapter;
    private Context mContext;

    private int mDay;
    private ArrayList<Class> mClasses = new ArrayList<Class>();

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("TimetableFragment", "Received!!!");
            mSwipeLayout.setRefreshing(false);
            if(intent.getAction().equals(Constants.TimetableNotification)) {
                Log.d("TimetableFragment", "Updating list view");
                mClasses.clear();
                mClasses.addAll(DataStore.getInstance(context).getClasses());
                mAdapter.notifyDataSetChanged();
            }
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

        // If there are classes in the data store, load them.
        ArrayList<Class> classes = DataStore.getInstance(mContext).getClasses();
        if(classes != null) {
            mClasses = classes;
        }
        // Set list view adapter.
        mAdapter = new TimetableAdapter(getActivity(), R.layout.class_cell, mClasses);
        setListAdapter(mAdapter);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        // Register broadcast receiver.
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.TimetableNotification);
        filter.addAction(Constants.InvalidCredentialsNotification);
        filter.addAction(Constants.NetworkErrorNotification);
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