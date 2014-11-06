package com.orangejam.ischool.fragments;



import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.orangejam.ischool.R;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class AssignmentMasterDetailFragment extends Fragment {

    private View view;

    public AssignmentMasterDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null)
                parent.removeView(view);
        }
        try {
            view = inflater.inflate(R.layout.fragment_assignment_master_detail, container, false);
        } catch (InflateException e) {
        /* view is already there, just return view as it is */
        }
        return view;
    }
}
