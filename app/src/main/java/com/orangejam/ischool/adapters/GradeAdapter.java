package com.orangejam.ischool.adapters;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.orangejam.ischool.R;
import com.orangejam.ischool.model.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by arnorymir on 14/10/14.
 */
public class GradeAdapter extends ArrayAdapter {

    private ArrayList<Grade> mGrades;
    private int mResourceId;
    private Context mActivity;
    private Context mContext;
    private Map<Integer, Grade> map = new HashMap<Integer, Grade>();

    public GradeAdapter(Context context, int resource, ArrayList<Grade> data) {
        super(context, resource, data);
        mActivity = context;
        mContext = mActivity.getApplicationContext();
        mResourceId = resource;
        mGrades = data;
    }

    @Override
    public int getCount() {

        String courseName = "";
        int counter = 0;
        for(Grade g : mGrades) {

            if(courseName != g.courseName){
                Log.i("","counter in now : "+ counter);
                counter ++;
                courseName = g.courseName;
            }
            map.put(counter, g);
            counter ++;
        }

        Log.i("","counter is: " + counter);
        return counter;

    }


    @Override
    public boolean isEnabled(int position) {
        return false;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View row = convertView;
       // Grade g = map.get(position);
        Grade g = mGrades.get(0);

        if(g == null) {
            CourseHolder holder = null;
            if(row == null)
            {
                LayoutInflater inflater = ((Activity) mActivity).getLayoutInflater();
                row = inflater.inflate(R.layout.grade_header, parent, false);

                holder = new CourseHolder();
                holder.nameLabel = (TextView)row.findViewById(R.id.gradeHeader);
                row.setTag(holder);
            }
            else
            {
                holder = (CourseHolder)row.getTag();
            }

            holder.nameLabel.setText(mGrades.get(0).courseName);

        } else {


            GradeHolder holder = null;
            if(row == null) {

                LayoutInflater inflater = ((Activity) mActivity).getLayoutInflater();
                row = inflater.inflate(mResourceId, parent, false);

                holder = new GradeHolder();
                holder.gradeLabel = (TextView)row.findViewById(R.id.gradeGradeLabel);
                holder.nameLabel = (TextView)row.findViewById(R.id.gradeNameLabel);
                holder.rankLabel = (TextView)row.findViewById(R.id.gradeRankLabel);
                row.setTag(holder);

            } else {
                holder = (GradeHolder)row.getTag();
            }

           // Grade g = mGrades.get(0);

            String rank ="";
            String grade = "";


            if(g.lastRank != null && g.firstRank != null ) {
                rank = g.firstRank + "-" + g.lastRank;
            }
            if(g.grade != null) {
                grade = g.grade.toString();
            }

            holder.nameLabel.setText(g.assignmentName);
            holder.rankLabel.setText(rank);
            holder.gradeLabel.setText(grade);


          }
        return row;
    }


    static class GradeHolder {
        TextView nameLabel;
        TextView rankLabel;
        TextView gradeLabel;
    }

    static class CourseHolder {
        TextView nameLabel;
    }
}
