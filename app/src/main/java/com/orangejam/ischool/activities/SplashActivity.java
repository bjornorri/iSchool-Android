package com.orangejam.ischool.activities;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.orangejam.ischool.R;
import com.orangejam.ischool.modules.Constants;
import com.orangejam.ischool.modules.CredentialManager;
import com.orangejam.ischool.modules.DataStore;

/**
 * Created by arnorymir on 02/11/14.
 */
public class SplashActivity extends Activity {
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {

        private boolean classesLoaded = false;
        private boolean assignmentsLoaded = false;
        private boolean gradesLoaded = false;

        private TextView mInvalidLabel;
        private ProgressBar mSpinner;

        private void reset() {
            classesLoaded = false;
            assignmentsLoaded = false;
            gradesLoaded = false;
        }

        private boolean dataIsLoaded() {
            return classesLoaded && assignmentsLoaded && gradesLoaded;
        }

        private void startMainActivity() {
            if(dataIsLoaded()){
                Log.i("", "start main in spalsh");
                Intent newIntent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(newIntent);
                finish();
            }
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals(Constants.InvalidCredentialsNotification)) {
                reset();
                CredentialManager.clearCredentials(context);
                mInvalidLabel.setVisibility(View.VISIBLE);
                mSpinner.setVisibility(View.GONE);
            } else if(action.equals(Constants.NetworkErrorNotification)) {
                reset();
                CredentialManager.clearCredentials(context);

                Toast.makeText(context, R.string.networkError, Toast.LENGTH_SHORT).show();
            } else if(action.equals(Constants.TimetableNotification)) {
                classesLoaded = true;
                startMainActivity();
            }
            else if(action.equals(Constants.AssignmentsNotification)){
                assignmentsLoaded = true;
                startMainActivity();
            }
            else if(action.equals(Constants.GradesNotification)){
                gradesLoaded = true;
                startMainActivity();
            }
        }
    };
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        Log.i("","onCreate in splash");

        DataStore.getInstance(getApplicationContext()).fetchClasses();
        DataStore.getInstance(getApplicationContext()).fetchAssignmentsAndGrades();
    }
    @Override
    protected void onResume() {
        super.onResume();

        // Register broadcast receiver to receive notifications.
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.InvalidCredentialsNotification);
        filter.addAction(Constants.NetworkErrorNotification);
        filter.addAction(Constants.TimetableNotification);
        filter.addAction(Constants.AssignmentsNotification);
        filter.addAction(Constants.GradesNotification);
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(mBroadcastReceiver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Unregister broadcast receiver.
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(mBroadcastReceiver);
    }


}
