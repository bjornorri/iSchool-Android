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
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.ListFragment;
import android.widget.ListView;
import android.widget.TextView;

import com.orangejam.ischool.activities.MainActivity;
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
    private MainActivity mActivity;
    private Context mContext;
    private TextView mEmptyLabel;

    private ArrayList<Assignment> mAssignments = new ArrayList<Assignment>();

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
        View rootView = inflater.inflate(R.layout.fragment_assignments_list, container, false);

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
                    DataStore.getInstance(getActivity().getApplicationContext()).fetchAssignmentsAndGrades();
                }

            }
        });
        mSwipeLayout.setColorSchemeResources(android.R.color.holo_red_light,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_blue_dark);

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
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActivity = (MainActivity) getActivity();
        mContext = mActivity.getApplicationContext();
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

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Assignment assignment = DataStore.getInstance(mContext).getAssignments().get(position);
        mActivity.showAssignmentDetails(assignment);
    }
}
