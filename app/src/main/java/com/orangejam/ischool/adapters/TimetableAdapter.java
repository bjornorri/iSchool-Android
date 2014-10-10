package com.orangejam.ischool.adapters;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.orangejam.ischool.model.Class;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bjornorri on 10/10/14.
 */
public class TimetableAdapter extends ArrayAdapter {

    ArrayList<Class> mClasses;

    public TimetableAdapter(Context context, int resource, ArrayList<Class> data) {
        super(context, resource, data);
        mClasses = data;
    }

    @Override
    public int getCount() {
        if(mClasses == null) {
            return 0;
        }
        return mClasses.size();
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return super.getView(position, convertView, parent);
    }
}
