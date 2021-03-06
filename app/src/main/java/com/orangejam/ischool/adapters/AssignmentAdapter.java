package com.orangejam.ischool.adapters;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.orangejam.ischool.R;
import com.orangejam.ischool.model.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by arnorymir on 14/10/14.
 */
public class AssignmentAdapter extends ArrayAdapter {

    private ArrayList<Assignment> mAssignments;
    private int mResourceId;
    private Context mActivity;
    private Context mContext;



    public AssignmentAdapter(Context context, int resource, ArrayList<Assignment> data) {
        super(context, resource, data);
        mActivity = context;
        mContext = mActivity.getApplicationContext();
        mResourceId = resource;
        mAssignments = data;
    }

    @Override
    public int getCount() {
        if(mAssignments == null) {
            return 0;
        }
        return mAssignments.size();
    }


    @Override
    public boolean isEnabled(int position) {
        return true;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        View row = convertView;
        AssignmentHolder holder = null;
        if(row == null) {
            LayoutInflater inflater = ((Activity) mActivity).getLayoutInflater();
            row = inflater.inflate(mResourceId, parent, false);

            holder = new AssignmentHolder();
            holder.checkImage = (ImageView)row.findViewById(R.id.assignmentCheckImage);
            holder.nameLabel = (TextView)row.findViewById(R.id.assignmentNameLabel);
            holder.courseLabel = (TextView)row.findViewById(R.id.assignmentCourseLabel);
            holder.dateLabel = (TextView)row.findViewById(R.id.assignmentDateLabel);

            row.setTag(holder);


        } else {
            holder = (AssignmentHolder)row.getTag();
        }

        Assignment a = mAssignments.get(position);

        if(a.handedIn)
        {
            holder.checkImage.setImageResource(R.drawable.checked);
        }
        else
        {
            holder.checkImage.setImageResource(R.drawable.unchecked);
        }

        DateFormat formatter = new SimpleDateFormat("dd.MM");

        holder.nameLabel.setText(a.name);
        holder.courseLabel.setText(a.courseName);
        holder.dateLabel.setText(formatter.format(a.dueDate.getTime()));


        return row;
    }


    static class AssignmentHolder
    {
        ImageView checkImage;
        TextView nameLabel;
        TextView courseLabel;
        TextView dateLabel;
    }
}
