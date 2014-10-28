package com.orangejam.ischool.modules;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.util.Log;

import com.orangejam.ischool.R;
import com.orangejam.ischool.model.Class;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by bjornorri on 27/10/14.
 */
public class iSchoolAppWidgetProvider extends AppWidgetProvider {

    private PendingIntent service = null;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.i("iSchoolAppWidgetProvider", "Updating widget");
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        final AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Class c = getClassForWidget(context);

        // Let's try a simple approach first, then implement the cleaver updates.
        final Calendar TIME = Calendar.getInstance();
        TIME.set(Calendar.MINUTE, 0);
        TIME.set(Calendar.SECOND, 0);
        TIME.set(Calendar.MILLISECOND, 0);

        final Intent i = new Intent(context, UpdateWidgetService.class);

        // Create a date formatter to convert dates to strings.
        DateFormat formatter = new SimpleDateFormat("HH:mm");

        i.putExtra("CourseName", c.courseName); // Beware, c can be null.
        i.putExtra("Type", c.type);
        i.putExtra("Location", c.location);
        i.putExtra("StartTime", formatter.format(c.startDate.getTime()));
        i.putExtra("EndTime", formatter.format(c.endDate.getTime()));
        String when;
        Resources res = context.getResources();
        if(c.isNow()) {
            when = res.getString(R.string.Now);
        } else if(c.isToday()) {
            when = res.getString(R.string.Today);
        } else {
            when = res.getString(R.string.Tomorrow);
        }
        i.putExtra("When", when);

        if (service == null)
        {
            service = PendingIntent.getService(context, 0, i, PendingIntent.FLAG_CANCEL_CURRENT);
        }
        alarmManager.setRepeating(AlarmManager.RTC, TIME.getTime().getTime(), 1000 * 1, service);

        /*
        if(c != null) {
            // Find the time at which the widget should be updated next.
            Calendar time;
            if(c.isNow()) {
                time = c.endDate;
            } else {
                time = c.startDate;
            }
            final Intent intent = new Intent(context, UpdateWidgetService.class);
            if (service == null)
            {
                service = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
            }
            alarmManager.set(AlarmManager.RTC, time.getTime().getTime(), service);
        } else {
            // TODO: Handle this.
        }*/
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
        if(service != null) {
            final AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            alarmManager.cancel(service);
        }
    }

    private Class getClassForWidget(Context context) {
        Class c = null;
        ArrayList<Class> classes = DataStore.getInstance(context.getApplicationContext()).getClassesForToday();
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
            classes = DataStore.getInstance(context.getApplicationContext()).getClassesForDay(tomorrow);
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
