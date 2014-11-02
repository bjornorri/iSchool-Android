package com.orangejam.ischool.activities;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.orangejam.ischool.R;
import com.orangejam.ischool.adapters.TabsPagerAdapter;
import com.orangejam.ischool.modules.CredentialManager;
import com.orangejam.ischool.modules.DataStore;


public class MainActivity extends FragmentActivity implements ActionBar.TabListener {

    private ViewPager mViewPager;
    private TabsPagerAdapter mAdapter;
    private ActionBar mActionBar;

    // Tab titles.
    private String[] mTabs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(CredentialManager.getUsername(getApplicationContext()) == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
        else if (! DataStore.getInstance(getApplicationContext()).loadData()){

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
            mAdapter = new TabsPagerAdapter(getSupportFragmentManager());

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
        if (id == R.id.action_settings) {
            return true;
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

}
