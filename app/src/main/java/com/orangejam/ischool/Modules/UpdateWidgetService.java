package com.orangejam.ischool.modules;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;

import com.orangejam.ischool.R;
import com.orangejam.ischool.model.*;
import com.orangejam.ischool.model.Class;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by bjornorri on 27/10/14.
 */
public class UpdateWidgetService extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("", "Service started");
        updateWidget(intent);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void updateWidget(Intent intent) {
        Log.i("UpdateWidgetService", "Service updating widget");

        ComponentName thisWidget = new ComponentName(this, iSchoolAppWidgetProvider.class);
        AppWidgetManager manager = AppWidgetManager.getInstance(this);
        int[] ids = manager.getAppWidgetIds(thisWidget);
        Resources res = getApplicationContext().getResources();
        Class c = getClassForWidget();
        RemoteViews view = new RemoteViews(getPackageName(), R.layout.app_widget);
        if(c != null) {
            // Create a date formatter to convert dates to strings.
            DateFormat formatter = new SimpleDateFormat("HH:mm");

            String when;
            if(c.isNow()) {
                when = res.getString(R.string.Now);
            } else if(c.isToday()) {
                when = res.getString(R.string.Today);
            } else {
                when = res.getString(R.string.Tomorrow);
            }

            view.setTextViewText(R.id.widgetLabel, when);
            view.setTextViewText(R.id.courseLabel, c.courseName);
            view.setTextViewText(R.id.typeLabel, c.type);
            view.setTextViewText(R.id.locationLabel, c.location);
            view.setTextViewText(R.id.startLabel, formatter.format(c.startDate.getTime()));
            view.setTextViewText(R.id.endLabel, formatter.format(c.endDate.getTime()));


            if (c.type.equals(com.orangejam.ischool.model.Class.Lecture)) {
                view.setImageViewResource(R.id.typeImage, R.drawable.lecture);
            } else if (c.type.equals(Class.Discussion)) {
                view.setImageViewResource(R.id.typeImage, R.drawable.discussion);
            } else if (c.type.equals(Class.Assistance)) {
                view.setImageViewResource(R.id.typeImage, R.drawable.assistance);
            } else {
                view.setImageViewResource(R.id.typeImage, R.drawable.rulogo);
            }
        } else {
            view.setTextViewText(R.id.widgetLabel, res.getString(R.string.NoUpcomingClasses));
            view.setTextViewText(R.id.courseLabel, "");
            view.setTextViewText(R.id.typeLabel, "");
            view.setTextViewText(R.id.locationLabel, "");
            view.setTextViewText(R.id.startLabel, "");
            view.setTextViewText(R.id.endLabel, "");
            view.setImageViewResource(R.id.typeImage, R.drawable.rulogo);
        }
        for(int id : ids) {
            manager.updateAppWidget(id, view);
        }
        stopSelf();
    }

    private Class getClassForWidget() {
        Class c = null;
        ArrayList<Class> classes = DataStore.getInstance(getApplicationContext()).getClassesForToday();
        // Try to find a class today that is not over.
        for(Class aClass : classes) {
            if(!aClass.isOver()) {
                c = aClass;
                break;
            }
        }
        // If there were no such classes, check if there is a class tomorrow.
        if(c == null) {
            int tomorrow = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) % 7 + 1;
            classes = DataStore.getInstance(getApplicationContext()).getClassesForDay(tomorrow);
            for(Class aClass : classes) {
                if(!aClass.isOver()) {
                    c = aClass;
                    break;
                }
            }
        }
        return c;
    }
}
