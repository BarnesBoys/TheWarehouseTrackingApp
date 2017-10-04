package com.example.brooke.thewarehousetrackingapp;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class NotificationService extends FirebaseMessagingService {
    private static final String TAG = "CustomFirebaseMsg";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // TODO: Handle FCM messages here.
        // If the application is in the foreground handle both data and notification messages here.
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated.

        String from = "From: " + remoteMessage.getFrom();
        String title = "Title: " + remoteMessage.getNotification().getTitle();

        Log.d(TAG, from);
        Log.d(TAG, title);

        System.out.println(from);
        System.out.println(title);

        if(remoteMessage.getNotification() != null) {
            String message = "Notification Message Body: " + remoteMessage.getNotification().getBody();
            Log.d(TAG, message);
            System.out.println(message);
        }

    }
}
