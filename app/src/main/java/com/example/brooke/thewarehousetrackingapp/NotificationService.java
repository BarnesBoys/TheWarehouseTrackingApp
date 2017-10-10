package com.example.brooke.thewarehousetrackingapp;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.NotificationCompat;
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


        NotificationCompat.Builder notification =
                new NotificationCompat.Builder(this);
        //set up the notification
        /*Notification notification = new NotificationCompat.Builder(this)

                .build();
                */
        //hard coded intent, to be extracted from info later
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        //set the activity
        notification.setContentIntent(pendingIntent);

        //build the notification with the incoming data
        notification.setContentTitle(remoteMessage.getNotification().getTitle());
        notification.setContentText(remoteMessage.getNotification().getBody());
        notification.setSmallIcon(R.mipmap.ic_launcher);

        //Get an instance of the Notifications Manager
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(123, notification.build());

        //NotificationManagerCompat manager = NotificationManagerCompat.from(getApplicationContext());
        //manager.notify(001, notification);

    }
}
