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

    private static PendingIntent service = null;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.i("iSchoolAppWidgetProvider", "Updating widget");
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        final AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        // Let's try a simple approach first, then implement the cleaver updates.
        final Calendar TIME = Calendar.getInstance();
        TIME.set(Calendar.MINUTE, 0);
        TIME.set(Calendar.SECOND, 0);
        TIME.set(Calendar.MILLISECOND, 0);

        final Intent i = new Intent(context, UpdateWidgetService.class);
        if (service == null)
        {
            service = PendingIntent.getService(context, 0, i, PendingIntent.FLAG_CANCEL_CURRENT);
        }
        alarmManager.setRepeating(AlarmManager.RTC, TIME.getTime().getTime(), 1000 * 60, service);

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
            service = null;
        }
    }
}
