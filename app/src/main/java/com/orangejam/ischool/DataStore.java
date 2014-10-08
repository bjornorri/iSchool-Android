package com.orangejam.ischool;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;

import com.orangejam.ischool.model.Class;
import com.orangejam.ischool.model.Assignment;
import com.orangejam.ischool.model.Grade;

import java.util.ArrayList;

/**
 * Created by bjornorri on 08/10/14.
 */
public class DataStore {

    private static DataStore mInstance;
    private Context mContext;

    private static ArrayList<Class> mClasses;
    private static ArrayList<Assignment> mAssignments;
    private static ArrayList<Grade> mGrades;

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

    /* Functions that fetch the data. */
    public void fetchClasses() {
        DataFetcher dataFetcher = new DataFetcher();
        dataFetcher.setContext(mContext);
        dataFetcher.execute(NetworkClient.Timetable);
    }

    public void fetchAssignmentsAndGrades() {
        DataFetcher dataFetcher = new DataFetcher();
        dataFetcher.setContext(mContext);
        dataFetcher.execute(NetworkClient.Assignments);
    }

    /* AsyncTask that fetches a page and parses it. */
    private class DataFetcher extends AsyncTask<String, Void, Void> {

        private Context mContext;
        private String mURL;

        public void setContext(Context context) {
            mContext = context;
        }

        @Override
        protected Void doInBackground(String... params) {
            mURL = params[0];
            if(mURL.equals(NetworkClient.Timetable)) {
                String html = NetworkClient.fetchPage(mContext, mURL);
                ArrayList<Class> classes = Parser.parseClasses(html);
                mClasses = classes;
            } else if(mURL.equals(NetworkClient.Assignments)) {
                String html = NetworkClient.fetchPage(mContext, mURL);
                ArrayList<Assignment> assignments = Parser.parseAssignments(html);
                ArrayList<Grade> grades = Parser.parseGrades(html);
                mAssignments = assignments;
                mGrades = grades;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(mURL.equals(NetworkClient.Timetable)) {
                // Broadcast a notification that the data store has finished loading the classes.
                Intent intent = new Intent();
                intent.setAction("DataStoreDidFinishLoadingClassesNotification");
                LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
            } else if(mURL.equals(NetworkClient.Assignments)) {
                // Broadcast notifications that the data store has finished loading the assignments and grades.
                Intent assignmentsIntent = new Intent();
                Intent gradesIntent = new Intent();
                assignmentsIntent.setAction("DataStoreDidFinishLoadingAssignmentsNotification");
                gradesIntent.setAction("DataStoreDidFinishLoadingGradesNotification");
                LocalBroadcastManager.getInstance(mContext).sendBroadcast(assignmentsIntent);
                LocalBroadcastManager.getInstance(mContext).sendBroadcast(gradesIntent);
            }
        }
    }
}
