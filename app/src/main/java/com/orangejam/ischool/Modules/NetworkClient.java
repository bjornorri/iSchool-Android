package com.orangejam.ischool.modules;

import android.content.Context;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.IOException;


/**
 * Created by bjornorri on 04/10/14.
 */

public class NetworkClient {

    /* Fetches a HTML page from a given URL. Returns a HttpResponse object.
     * It is the callers responsibility to do this asynchronously. */
    public static HttpResponse fetchPage(Context context, String pageURL) {
        HttpResponse response = null;
        // Fetch the stored credentials.
        String username = CredentialManager.getUsername(context);
        String password = CredentialManager.getPassword(context);
        // Don't do anything if the credentials are missing.
        if(username == null || password == null) {
            username = "";
            password = "";
        }
        // Create a CredentialsProvider with the stored credentials.
        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT),
                new UsernamePasswordCredentials(username, password));

        // Set a timeout for the request.
        int timeout = 10 * 1000;
        HttpParams params = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(params, timeout);

        // Create the HTTP client.
        DefaultHttpClient httpClient = new DefaultHttpClient(params);
        httpClient.setCredentialsProvider(credentialsProvider);
        // Craft the request.
        HttpGet getRequest = new HttpGet(pageURL);
        try {
            // Send the request and get the response.
            response = httpClient.execute(getRequest);
        } catch(ClientProtocolException e) {
            Log.d("NetworkClient", "Client protocol exception", e);
        } catch(IOException e) {
            Log.d("NetworkClient", "IOException", e);
        }
        return response;
    }
}
