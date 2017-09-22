package com.example.brooke.thewarehousetrackingapp;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class MyFirebaseInstanceIdService extends FirebaseInstanceIdService {
    private static final String TAG = "MyFirebaseIIDService";
    private String REFRESHED_TOKEN = null;
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        REFRESHED_TOKEN = refreshedToken;
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        //     sendRegistrationToServer(refreshedToken);
    }

    /**
     * Gets the Refreshed Token
     * @return Returns the token as a string
     */
    public String getREFRESHED_TOKEN() {
        return REFRESHED_TOKEN;
    }
}
