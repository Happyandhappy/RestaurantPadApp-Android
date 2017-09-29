package com.example.fleissig.restaurantapp;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.example.fleissig.restaurantapp.order.WaitingOrderActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    public static final String RESULT = "com.example.fleissig.restaurantapp.MyFirebaseMessagingService.REQUEST_PROCESSED";
    private static final String TAG = MyFirebaseInstanceIDService.class.getSimpleName();

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // TODO(developer): Handle FCM messages here.
        // If the application is in the foreground handle both data and notification messages here.
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        String message = remoteMessage.getData().get("message");
        Log.d(TAG, "Message: " + message);
        RemoteMessage.Notification notification = remoteMessage.getNotification();
        if (notification != null)
            Log.d(TAG, "Notification Message Body: " + notification.getBody());

        sendResult(message);
    }

    public void sendResult(String message) {
        Intent intent = new Intent(RESULT);
        intent.putExtra(WaitingOrderActivity.MESSAGE, message);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}
