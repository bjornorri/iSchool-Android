package com.orangejam.ischool.adapters;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.orangejam.ischool.R;
import com.orangejam.ischool.model.Class;
import com.orangejam.ischool.modules.Constants;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by bjornorri on 10/10/14.
 */
public class TimetableAdapter extends ArrayAdapter {

    private ArrayList<Class> mClasses;
    private int mResourceId;
    private Context mActivity;
    private Context mContext;

    public TimetableAdapter(Context context, int resource, ArrayList<Class> data) {
        super(context, resource, data);
        mActivity = context;
        mContext = mActivity.getApplicationContext();
        mResourceId = resource;
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
        View row = convertView;
        ClassHolder holder = null;
        if(row == null) {
            LayoutInflater inflater = ((Activity) mActivity).getLayoutInflater();
            row = inflater.inflate(mResourceId, parent, false);

            holder = new ClassHolder();
            holder.typeImage =(ImageView)row.findViewById(R.id.typeImage);
            holder.courseLabel = (TextView)row.findViewById(R.id.courseLabel);
            holder.typeLabel = (TextView)row.findViewById(R.id.typeLabel);
            holder.locationLabel = (TextView)row.findViewById(R.id.locationLabel);
            holder.startLabel = (TextView)row.findViewById(R.id.startLabel);
            holder.endLabel = (TextView)row.findViewById(R.id.endLabel);

            row.setTag(holder);
        } else {
            holder = (ClassHolder)row.getTag();
        }
        Class c = mClasses.get(position);

        if(c.type.equals(Class.Lecture))
        {
            holder.typeImage.setImageResource(R.drawable.lecture);
        }
        else if(c.type.equals(Class.Discussion))
        {
            holder.typeImage.setImageResource(R.drawable.discussion);
        }
        else if(c.type.equals(Class.Assistance))
        {
            holder.typeImage.setImageResource(R.drawable.assistance);
        }
        else
        {
            holder.typeImage.setImageResource(R.drawable.rulogo);
        }

        // Create a date formatter to convert dates to strings.
        DateFormat formatter = new SimpleDateFormat("HH:mm");

        holder.courseLabel.setText(c.courseName);
        holder.typeLabel.setText(c.type);
        holder.locationLabel.setText(c.location);
        holder.startLabel.setText(formatter.format(c.startDate.getTime()));
        holder.endLabel.setText(formatter.format(c.endDate.getTime()));

        // Set alpha to indicate that the class is over
        if(c.isOver())
        {
            row.setAlpha((float) 0.3);
        }
        else
        {
            row.setAlpha((float) 1.0);
        }

        // Implement this later?
		/*if(cls.isNow())
		{
			View line = new View(this.context);
			line.setBackgroundColor(Color.RED);
		}*/

        return row;
    }

    static class ClassHolder
    {
        ImageView typeImage;
        TextView courseLabel;
        TextView typeLabel;
        TextView locationLabel;
        TextView startLabel;
        TextView endLabel;
    }
}
