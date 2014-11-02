package com.orangejam.ischool.modules;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.orangejam.ischool.model.Class;
import com.orangejam.ischool.model.Assignment;
import com.orangejam.ischool.model.Grade;


import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by bjornorri on 08/10/14.
 */

public class DataStore {

    private static DataStore mInstance;
    private Context mContext;

    private static ArrayList<Class> mClasses;
    private static ArrayList<Assignment> mAssignments;
    private static ArrayList<Grade> mGrades;

    private boolean loadClesses = false;
    private boolean loadAssignmentsAndGrades = false;



    public DataStore() {
        mClasses = null;
        mAssignments = null;
        mGrades = null;
    }

    public static DataStore getInstance(Context context) {
        if(mInstance == null) {
            mInstance = new DataStore();
        }
        mInstance.mContext = context;
        return mInstance;
    }

    /* Functions to clear the data. */
    public void clearClasses() {
        mClasses = null;
    }

    public void clearAssignments() {
        mAssignments = null;
    }

    public void clearGrades() {
        mGrades = null;
    }

    public void clearData() {
        clearClasses();
        clearAssignments();
        clearGrades();
    }

    /* Getters for the data. */
    public ArrayList<Class> getClasses() {
        return mClasses;
    }

    public ArrayList<Assignment> getAssignments() {
        return mAssignments;
    }

    public ArrayList<Grade> getGrades() {
        return mGrades;
    }

    public boolean isDataLoaded() {
        return loadAssignmentsAndGrades && loadClesses;
    }

    /* Convenience methods to get classes by day. */
    public ArrayList<Class> getClassesForDay(int weekDay) {
        ArrayList<Class> classes = new ArrayList<Class>();
        if(mClasses != null) {
            for(Class c : mClasses) {
                if(c.startDate.get(Calendar.DAY_OF_WEEK) == weekDay) {
                    classes.add(c);
                }
            }
        }
        return classes;
    }

    public ArrayList<Class> getClassesForToday() {
        int today = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        return getClassesForDay(today);
    }

    /* Functions that fetch the data. */
    public void fetchClasses() {
        DataFetcher dataFetcher = new DataFetcher();
        dataFetcher.setContext(mContext);
        dataFetcher.execute(Constants.TimetableURL);
    }

    public void fetchAssignmentsAndGrades() {
        DataFetcher dataFetcher = new DataFetcher();
        dataFetcher.setContext(mContext);
        dataFetcher.execute(Constants.AssignmentsURL);
    }

    private void broadCastNotification(String action) {
        Intent intent = new Intent();
        intent.setAction(action);
        LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
    }

    /* AsyncTask that fetches a page and parses it. */
    private class DataFetcher extends AsyncTask<String, Void, Integer> {

        private Context mContext;
        private String mURL;

        public void setContext(Context context) {
            mContext = context;
        }

        @Override
        protected Integer doInBackground(String... params) {
            // Fetch the page.
            mURL = params[0];
            HttpResponse response = NetworkClient.fetchPage(mContext, mURL);
            if(response == null) {
                return -1;
            }
            // Check the status code.
            int statusCode = response.getStatusLine().getStatusCode();
            if(statusCode == 200) {
                try {
                    String html = EntityUtils.toString(response.getEntity());
                    if(mURL.equals(Constants.TimetableURL)) {
                        mClasses = Parser.parseClasses(html);
                    } else if(mURL.equals(Constants.AssignmentsURL)) {
                        mAssignments = Parser.parseAssignments(html);
                        mGrades = Parser.parseGrades(html);
                    }
                } catch(IOException e) {
                    Log.d("DataStore", "IOException", e);
                }
            }
            return statusCode;
        }

        @Override
        protected void onPostExecute(Integer statusCode) {
            super.onPostExecute(statusCode);
            if(statusCode.equals(200)) {
                if(mURL.equals(Constants.TimetableURL)) {
                    // Broadcast a notification that the data store has finished loading the classes.
                    broadCastNotification(Constants.TimetableNotification);
                    loadClesses = true;
                } else if(mURL.equals(Constants.AssignmentsURL)) {
                    // Broadcast notifications that the data store has finished loading the assignments and grades.
                    broadCastNotification(Constants.AssignmentsNotification);
                    broadCastNotification(Constants.GradesNotification);
                    loadAssignmentsAndGrades = true;
                }
            } else if(statusCode.equals(401)) {
                broadCastNotification(Constants.InvalidCredentialsNotification);
            } else {
                broadCastNotification(Constants.NetworkErrorNotification);
            }
        }
    }
}
