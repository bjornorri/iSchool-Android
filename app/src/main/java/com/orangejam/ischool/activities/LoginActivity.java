package com.orangejam.ischool.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.orangejam.ischool.R;
import com.orangejam.ischool.modules.Constants;
import com.orangejam.ischool.modules.CredentialManager;
import com.orangejam.ischool.modules.DataStore;

public class LoginActivity extends ActionBarActivity {

    private EditText mUsernameField;
    private EditText mPasswordField;
    private Button mLoginButton;
    private TextView mInvalidLabel;
    private ProgressBar mSpinner;

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {

        private boolean classesLoaded = false;
        private boolean assignmentsLoaded = false;
        private boolean gradesLoaded = false;

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
                mSpinner.setVisibility(View.GONE);
                Toast.makeText(context, R.string.NetworkError, Toast.LENGTH_SHORT).show();
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mUsernameField = (EditText) findViewById(R.id.usernameField);
        mPasswordField = (EditText) findViewById(R.id.passwordField);
        mLoginButton = (Button) findViewById(R.id.loginButton);
        mInvalidLabel = (TextView) findViewById(R.id.invalidLabel);
        mSpinner = (ProgressBar) findViewById(R.id.spinner);

        // Fix look of password field. Stupid Android.
        mPasswordField.setTypeface(Typeface.DEFAULT);
        mPasswordField.setTransformationMethod(new PasswordTransformationMethod());

        // Set onClick listener for login button.
        OnClickListener listener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                didPressLoginButton(v);
            }
        };
        mLoginButton.setOnClickListener(listener);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void didPressLoginButton(View view) {
        mInvalidLabel.setVisibility(View.INVISIBLE);
        mSpinner.setVisibility(View.VISIBLE);
        String username = mUsernameField.getText().toString();
        String password = mPasswordField.getText().toString();
        CredentialManager.storeCredentials(getApplicationContext(), username, password);

        DataStore.getInstance(getApplicationContext()).fetchClasses();
        DataStore.getInstance((getApplicationContext())).fetchAssignmentsAndGrades();
    }
}
