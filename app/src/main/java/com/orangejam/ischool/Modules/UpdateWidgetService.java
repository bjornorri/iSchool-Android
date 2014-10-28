package com.orangejam.ischool.modules;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;

import com.orangejam.ischool.R;
import com.orangejam.ischool.model.*;
import com.orangejam.ischool.model.Class;

/**
 * Created by bjornorri on 27/10/14.
 */
public class UpdateWidgetService extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        updateWidget(intent);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void updateWidget(Intent intent) {
        Log.i("UpdateWidgetService", "Service updating widget");
        RemoteViews view = new RemoteViews(getPackageName(), R.layout.app_widget);

        view.setTextViewText(R.id.widgetLabel, intent.getStringExtra("When"));
        view.setTextViewText(R.id.courseLabel, intent.getStringExtra("CourseName"));
        view.setTextViewText(R.id.typeLabel, intent.getStringExtra("Type"));
        view.setTextViewText(R.id.locationLabel, intent.getStringExtra("Location"));
        view.setTextViewText(R.id.startLabel, intent.getStringExtra("StartTime"));
        view.setTextViewText(R.id.endLabel, intent.getStringExtra("EndTime"));

        String type = intent.getStringExtra("Type");

        if (type.equals(com.orangejam.ischool.model.Class.Lecture)) {
            view.setImageViewResource(R.id.typeImage, R.drawable.lecture);
        } else if (type.equals(Class.Discussion)) {
            view.setImageViewResource(R.id.typeImage, R.drawable.discussion);
        } else if (type.equals(Class.Assistance)) {
            view.setImageViewResource(R.id.typeImage, R.drawable.assistance);
        } else {
            view.setImageViewResource(R.id.typeImage, R.drawable.rulogo);
        }

        ComponentName thisWidget = new ComponentName(this, iSchoolAppWidgetProvider.class);
        AppWidgetManager manager = AppWidgetManager.getInstance(this);
        manager.updateAppWidget(thisWidget, view);
    }
}
