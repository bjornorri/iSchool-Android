package com.orangejam.ischool;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.test.ApplicationTestCase;
import android.util.Log;

import junit.framework.Assert;

/**
 * Created by bjornorri on 07/10/14.
 */
public class CredentialManagerTests extends ApplicationTestCase<Application> {

    private String username = "user";
    private String password = "lamepassword";

    public CredentialManagerTests() {
        super(Application.class);
    }

    public void testStoreCredentials() {
        // Clear the credentials to get a clean test.
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("com.orangejam.ischool", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("username");
        editor.remove("password");
        editor.commit();

        // Store the credentials.
        CredentialManager.storeCredentials(getContext(), username, password);

        // Assert that the stored credentials are the ones the function received.
        assertEquals(sharedPreferences.getString("username", null), username);
        assertEquals(sharedPreferences.getString("password", null), password);

        // Clear the credentials again.
        editor.remove("username");
        editor.remove("password");
        editor.commit();
    }

    public void testGetCredentials() {
        // Store the username and password.
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("com.orangejam.ischool", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("username", username);
        editor.putString("password", password);
        editor.commit();

        // Retrieve the credentials.
        String storedUsername = CredentialManager.getUsername(getContext());
        String storedPassword = CredentialManager.getPassword(getContext());

        // Assert that the retrieved credentials are the ones that were stored.
        assertEquals(storedUsername, username);
        assertEquals(storedPassword, password);

        // Clear the credentials.
        editor.remove("username");
        editor.remove("password");
        editor.commit();
    }

    public void testClearCredentials() {
        // Store the username and password.
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("com.orangejam.ischool", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("username", username);
        editor.putString("password", password);
        editor.commit();

        // Clear the credentials.
        CredentialManager.clearCredentials(getContext());

        // Retrieve the credentials.
        String storedUsername = CredentialManager.getUsername(getContext());
        String storedPassword = CredentialManager.getPassword(getContext());

        // Assert that the username and password values are null.
        assertNull(storedUsername);
        assertNull(storedPassword);
    }
}
