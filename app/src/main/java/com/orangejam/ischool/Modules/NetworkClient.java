package com.orangejam.ischool.modules;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;


/**
 * Created by bjornorri on 04/10/14.
 */

public class NetworkClient {

    /* Fetches a HTML page from a given URL.
     * It is the callers responsibility to do this asynchronously. */
    public static String fetchPage(Context context, String pageURL) {
        String html = null;
        // Fetch the stored credentials.
        String username = CredentialManager.getUsername(context);
        String password = CredentialManager.getPassword(context);
        // Don't do anything if the credentials are missing.
        if(username == null || password == null) {
            return null;
        }
        // Create a CredentialsProvider with the stored credentials.
        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT),
                new UsernamePasswordCredentials(username, password));
        // Create the HTTP client.
        DefaultHttpClient httpClient = new DefaultHttpClient();
        httpClient.setCredentialsProvider(credentialsProvider);
        // Craft the request.
        HttpGet getRequest = new HttpGet(pageURL);
        try {
            // Send the request and get the response.
            HttpResponse response = httpClient.execute(getRequest);
            int statusCode = response.getStatusLine().getStatusCode();
            Log.d("NetworkClient", "Status Code: " + statusCode);
            // If the status code is 200 the request was successful.
            if(statusCode == 200) {
                html = EntityUtils.toString(response.getEntity());
            }
            // If the request was not successful send the error handler a message containing the status code.
            else {
                html = null;
                // 401 Unauthorized
                if(statusCode == 401) {
                    // Send a broadcast message.
                    Intent intent = new Intent();
                    intent.setAction(Constants.InvalidCredentialsNotification);
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                } else {
                    Intent intent = new Intent();
                    intent.setAction(Constants.NetworkErrorNotification);
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                }
            }
        } catch(ClientProtocolException e) {
            Log.d("NetworkClient", "Client protocol exception", e);
        } catch(IOException e) {
            Log.d("NetworkClient", "IOException", e);
        }
        return html;
    }
}
