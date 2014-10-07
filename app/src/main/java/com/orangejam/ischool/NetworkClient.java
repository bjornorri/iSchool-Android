package com.orangejam.ischool;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;


/**
 * Created by bjornorri on 04/10/14.
 */

public class NetworkClient {

    public static final String Assignments = "https://myschool.ru.is/myschool/?Page=Exe&ID=1.12";
    public static final String Timetable = "https://myschool.ru.is/myschool/?Page=Exe&ID=3.2";
    public static final String Canteen = "http://malid.ru.is";

    /* Fetches a HTML page from a given URL asynchronously.
     * The caller must provide a success handler and an error handler that get called on success or error. */
    public static void fetchPage(final String pageURL, final Handler successHandler, final Handler errorHandler) {

        new Thread(new Runnable() {

            Handler mSuccessHandler = successHandler;
            Handler mErrorHandler = errorHandler;

            @Override
            public void run() {
                Connection.Response response;
                try {
                    response = Jsoup.connect(pageURL).execute();
                    int statusCode = response.statusCode();
                    if(statusCode == 200) {
                        Document page = response.parse();
                        Message message = Message.obtain();
                        message.obj = page;
                        mSuccessHandler.sendMessage(message);
                    } else {
                        mErrorHandler.sendEmptyMessage(response.statusCode());
                    }
                } catch (IOException e) {
                    mErrorHandler.sendEmptyMessage(-1);
                }
            }
        }).start();
    }
}
