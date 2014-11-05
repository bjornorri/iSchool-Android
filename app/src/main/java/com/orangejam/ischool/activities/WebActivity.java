package com.orangejam.ischool.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.AttributeSet;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.HttpAuthHandler;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.orangejam.ischool.R;
import com.orangejam.ischool.fragments.WebFragment;
import com.orangejam.ischool.modules.CredentialManager;

import java.util.HashMap;
import java.util.Map;

public class WebActivity extends FragmentActivity {

    private WebFragment mWebFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        Log.d("", "onPostCreate");
        super.onPostCreate(savedInstanceState);
        mWebFragment = (WebFragment)getSupportFragmentManager().findFragmentById(R.id.web_fragment);
        String baseURL = "https://myschool.ru.is/myschool/";
        String URL = baseURL + getIntent().getExtras().getString("URL");
        mWebFragment.loadURL(URL);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
    }

    @Nullable
    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        return super.onCreateView(name, context, attrs);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.web, menu);
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
        } else if(id == android.R.id.home){
            finish();
        }
        return true;
        //return super.onOptionsItemSelected(item);
    }
}
