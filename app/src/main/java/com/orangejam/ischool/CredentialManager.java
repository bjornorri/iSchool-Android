package com.orangejam.ischool;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by bjornorri on 07/10/14.
 */
public class CredentialManager {

    public static String getUsername(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("com.orangejam.ischool", Context.MODE_PRIVATE);
        return sharedPreferences.getString("username", null);
    }

    public static String getPassword(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("com.orangejam.ischool", Context.MODE_PRIVATE);
        return sharedPreferences.getString("password", null);
    }

    public static void storeCredentials(Context context, String username, String password) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("com.orangejam.ischool", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("username", username);
        editor.putString("password", password);
        editor.commit();
    }

    public static void clearCredentials(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("com.orangejam.ischool", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("username");
        editor.remove("password");
    }
}
