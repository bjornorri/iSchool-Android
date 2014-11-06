package com.orangejam.ischool.fragments;



import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.HttpAuthHandler;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.orangejam.ischool.R;
import com.orangejam.ischool.modules.CredentialManager;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class WebFragment extends Fragment {

    private WebView mWebView;
    private ProgressBar mProgressBar;
    private Activity mActivity;
    private Context mContext;
    private WebViewClient mWebViewClient = new WebViewClient() {

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            Log.d("", "onPageStarted");
            super.onPageStarted(view, url, favicon);
            mProgressBar.setVisibility(View.VISIBLE);
            mWebView.setVisibility(View.GONE);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            Log.d("", "onPageFinished");
            super.onPageFinished(view, url);
            if(url.startsWith("https://myschool.ru.is")) {
                // Execute javascript to hide unnecessary elements on page
                String js = "javascript: (function(){$('.ruHeader img').hide();$('.ruHeader tr:not(:last-child)').hide();$('.ruLeft').hide();$('.ruRight').hide();$('.ruFooter').hide();$('#headersearch').hide();$('.level1').hide();$('#ruTabsNewcontainer').hide();$('.resetSize').click();$('.increaseSize').click();$('.increaseSize').click();}());";
                mWebView.loadUrl(js);
            }
            mWebView.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.GONE);
        }

        @Override
        public void onReceivedHttpAuthRequest(WebView view, HttpAuthHandler handler, String host, String realm) {
            handler.proceed(CredentialManager.getUsername(mContext), CredentialManager.getPassword(mContext));
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            handler.proceed();
        }
    };

    public WebFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("", "OnCreateView");
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_web, container, false);
        mWebView = (WebView) rootView.findViewById(R.id.webView);
        mProgressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);

        // Configure settings for mWebView
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setBuiltInZoomControls(true);
        mWebView.getSettings().setSupportZoom(true);
        mWebView.getSettings().setDisplayZoomControls(false);
        mWebView.getSettings().setLoadWithOverviewMode(true);
        mWebView.getSettings().setUseWideViewPort(true);

        mWebView.setWebChromeClient(new WebChromeClient());
        mWebView.setWebViewClient(mWebViewClient);
        Log.d("", "onCreateView finished");
        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d("", "OnCreate");
        super.onCreate(savedInstanceState);
        mActivity = getActivity();
        mContext = mActivity.getApplicationContext();
    }

    public void loadURL(String URL) {
        mWebView.loadUrl(URL);
    }
}
