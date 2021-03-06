package com.orangejam.ischool.activities;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.orangejam.ischool.R;
import com.orangejam.ischool.adapters.TabsPagerAdapter;
import com.orangejam.ischool.fragments.WebFragment;
import com.orangejam.ischool.model.Assignment;
import com.orangejam.ischool.model.Grade;
import com.orangejam.ischool.modules.CredentialManager;
import com.orangejam.ischool.modules.DataStore;

import java.util.Locale;


public class MainActivity extends FragmentActivity implements ActionBar.TabListener {

    private ViewPager mViewPager;
    private TabsPagerAdapter mAdapter;
    private ActionBar mActionBar;

    // Tab titles.
    private String[] mTabs;

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String language = getSharedPreferences("is.orangejam.ischool", Context.MODE_PRIVATE).getString("Language", null);
        if(language != null) {
            Locale locale = new Locale(language);
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.locale = locale;
            getApplicationContext().getResources().updateConfiguration(config,
                    getApplicationContext().getResources().getDisplayMetrics());
        }

        if(!isNetworkAvailable()){
            Resources res = getResources();
            new AlertDialog.Builder(this)
                    .setTitle(res.getString(R.string.NetworkError))
                    .setMessage(res.getString(R.string.NetworkMessage))
                    .setCancelable(false)
                    .setPositiveButton((res.getString(R.string.refresh)), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent newIntent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(newIntent);
                            finish();
                        }
                    }).create().show();

        }
        else if(CredentialManager.getUsername(getApplicationContext()) == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
        else if (! DataStore.getInstance(getApplicationContext()).isDataLoaded()){
            Intent intent = new Intent(this, SplashActivity.class);
            startActivity(intent);
            finish();
        }
        else {

            //CredentialManager.clearCredentials(getApplicationContext());
            setContentView(R.layout.activity_main);
            // Initialization.
            mViewPager = (ViewPager) findViewById(R.id.pager);
            mActionBar = getActionBar();
            mAdapter = new TabsPagerAdapter(getSupportFragmentManager(), getApplicationContext());

            mViewPager.setAdapter(mAdapter);
            // Is this needed? mActionBar.setHomeButtonEnabled(false);
            mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

            // Adding Tabs
            Resources res = getResources();
            mTabs = new String[]{res.getString(R.string.Timetable), res.getString(R.string.Assignments), res.getString(R.string.Grades)};
            for (String tab_name : mTabs) {
                mActionBar.addTab(mActionBar.newTab().setText(tab_name).setTabListener(this));
            }

            // Select the appropriate tab when swiped between.
            mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

                @Override
                public void onPageSelected(int position) {
                    // on changing the page
                    // make respected tab selected
                    mActionBar.setSelectedNavigationItem(position);
                }

                @Override
                public void onPageScrolled(int arg0, float arg1, int arg2) {
                }

                @Override
                public void onPageScrollStateChanged(int arg0) {
                }
            });

            DataStore.getInstance(getApplicationContext()).fetchClasses();
            DataStore.getInstance(getApplicationContext()).fetchAssignmentsAndGrades();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if(id == R.id.action_language) {
            Locale current = getResources().getConfiguration().locale;
            String language;
            if(current.getLanguage().equals("en")) {
                language = "is";
            } else {
                language = "en";
            }
            Locale locale = new Locale(language);
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.locale = locale;
            getApplicationContext().getResources().updateConfiguration(config,
                    getApplicationContext().getResources().getDisplayMetrics());
            SharedPreferences prefs = getSharedPreferences("is.orangejam.ischool", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("Language", language);
            editor.apply();
            // Restart the activity.
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
            return true;
        } else if(id == R.id.action_logout) {
            DataStore.getInstance(getApplicationContext()).clearData();
            CredentialManager.clearCredentials(getApplicationContext());
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    public void showAssignmentDetails(Assignment assignment) {
        boolean isTablet = getResources().getBoolean(R.bool.isTablet);
        if(isTablet) {
            FragmentManager manager = getSupportFragmentManager();
            WebFragment webFragment = (WebFragment) manager.findFragmentById(R.id.detailAssignmentFragment);
            String baseURL = "https://myschool.ru.is/myschool/";
            webFragment.loadURL(baseURL + assignment.URL);
        } else {
            Intent intent = new Intent(getApplicationContext(), WebActivity.class);
            intent.putExtra("URL", assignment.URL);
            startActivity(intent);
        }
    }

    public void showGradeDetails(Grade grade) {
        boolean isTablet = getResources().getBoolean(R.bool.isTablet);
        if(isTablet) {
            FragmentManager manager = getSupportFragmentManager();
            WebFragment webFragment = (WebFragment) manager.findFragmentById(R.id.detailGradeFragment);
            String baseURL = "https://myschool.ru.is/myschool/";
            webFragment.loadURL(baseURL + grade.URL);
        } else {
            Intent intent = new Intent(getApplicationContext(), WebActivity.class);
            intent.putExtra("URL", grade.URL);
            startActivity(intent);
        }
    }
}
