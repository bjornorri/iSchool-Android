package com.orangejam.ischool;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
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

    public static final String Assignments = "https://myschool.ru.is/myschool/?Page=Exe&ID=1.12";
    public static final String Timetable = "https://myschool.ru.is/myschool/?Page=Exe&ID=3.2";
    public static final String Canteen = "http://malid.ru.is";

    /* Fetches a HTML page from a given URL asynchronously.
     * The caller must provide a success handler and an error handler that get called on success and error. */
    public static void fetchPage(final String pageURL, final Handler successHandler, final Handler errorHandler, final Context context) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                // Fetch the stored credentials.
                String username = CredentialManager.getUsername(context);
                String password = CredentialManager.getPassword(context);
                // Don't do anything if the credentials are missing.
                if(username == null || password == null) {
                    return;
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
                    // If the status code is 200 the request was successful.
                    if(statusCode == 200) {
                        String html = EntityUtils.toString(response.getEntity());
                        Message message = Message.obtain();
                        message.obj = html;
                        successHandler.sendMessage(message);
                    }
                    // If the request was not successful send the error handler a message containing the status code.
                    else {
                        errorHandler.sendEmptyMessage(statusCode);
                    }
                    Log.d("NetworkClient", "This is what we get back:" + response.getStatusLine().toString() + ", " + response.getEntity().toString());
                } catch(ClientProtocolException e) {
                    errorHandler.sendEmptyMessage(-1);
                    Log.d("NetworkClient", "Client protocol exception", e);
                } catch(IOException e) {
                    errorHandler.sendEmptyMessage(-1);
                    Log.d("NetworkClient", "IOException", e);
                }
            }
        }).start();
    }
}
