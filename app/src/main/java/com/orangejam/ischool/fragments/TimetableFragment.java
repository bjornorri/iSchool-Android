package com.orangejam.ischool.fragments;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.orangejam.ischool.R;
import com.orangejam.ischool.adapters.TimetableAdapter;
import com.orangejam.ischool.modules.Constants;
import com.orangejam.ischool.modules.DataStore;
import com.orangejam.ischool.model.Class;
import com.orangejam.ischool.modules.RefreshLayout;

import java.util.ArrayList;

public class TimetableFragment extends ListFragment {

    private RefreshLayout mSwipeLayout;
    private TimetableAdapter mAdapter;
    private Context mContext;
    private TextView mEmptyLabel;

    private int mDay;
    private ArrayList<Class> mClasses = new ArrayList<Class>();

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) mContext.getSystemService(mContext.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mSwipeLayout.setRefreshing(false);
            if(intent.getAction().equals(Constants.TimetableNotification)) {
                mClasses.clear();
                mClasses.addAll(DataStore.getInstance(context).getClassesForDay(mDay));
                if(mClasses.isEmpty()) {
                    mEmptyLabel.setVisibility(View.VISIBLE);
                } else {
                    mEmptyLabel.setVisibility(View.GONE);
                }
                mAdapter.notifyDataSetChanged();
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDay = getArguments().getInt("Day");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_timetable, container, false);

        // Get context.
        mContext = getActivity().getApplicationContext();

        // Get the empty label.
        mEmptyLabel = (TextView) rootView.findViewById(R.id.emptyLabel);

        // Configure SwipeRefreshLayout.
        mSwipeLayout = (RefreshLayout) rootView.findViewById(R.id.swipe_container);
        mSwipeLayout.setChildView((ListView) rootView.findViewById(android.R.id.list));
        mSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(!isNetworkAvailable()){
                    mSwipeLayout.setRefreshing(false);
                    Resources res = getResources();
                    new AlertDialog.Builder(getActivity())
                            .setTitle(res.getString(R.string.NetworkError))
                            .setMessage(res.getString(R.string.NetworkMessage))
                            .setCancelable(true)
                            .setNegativeButton((res.getString(R.string.close)), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            }).create().show();
                } else {
                    DataStore.getInstance(getActivity().getApplicationContext()).fetchClasses();
                }

            }
        });
        mSwipeLayout.setColorSchemeResources(android.R.color.holo_red_light,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_blue_dark);

        // If there are classes in the data store, load them.
        ArrayList<Class> classes = DataStore.getInstance(mContext).getClassesForDay(mDay);
        if(classes != null) {
            mClasses = classes;
            if(mClasses.isEmpty()) {
                mEmptyLabel.setVisibility(View.VISIBLE);
            }
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