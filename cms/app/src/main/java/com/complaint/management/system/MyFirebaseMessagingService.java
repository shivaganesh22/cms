package com.complaint.management.system;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

//public class MyFirebaseMessagingService extends FirebaseMessagingService {
//    public static final String FCM_CHANNEL_ID="FCM_CHANNEL_ID";
//    @Override
//    public void onMessageReceived(@NonNull RemoteMessage message) {
//        super.onMessageReceived(message);
//        if(message.getNotification()!=null){
//            String title=message.getNotification().getTitle();
//            String body=message.getNotification().getBody();
//            Notification notification=new NotificationCompat.Builder(this,FCM_CHANNEL_ID)
//                    .setSmallIcon(R.drawable.ic_baseline_notifications_24)
//                    .setContentTitle(title).setColor(Color.GREEN)
//                    .setContentText(body).build();
//            NotificationManager manager= (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//            manager.notify(1002,notification);
//        }
//        if(message.getData().size() >0){
//
//        }
//
//    }
//
//    @Override
//    public void onDeletedMessages() {
//        super.onDeletedMessages();
//    }
//
//    @Override
//    public void onNewToken(@NonNull String token) {
//        super.onNewToken(token);
//    }
//}

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    public static final String FCM_CHANNEL_ID = "FCM_CHANNEL_ID";

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);

        if (message.getNotification() != null) {
            String title = message.getNotification().getTitle();
            String body = message.getNotification().getBody();

            // Create a separate thread for handling notifications
            new Handler(Looper.getMainLooper()).post(() -> {
                NotificationCompat.Builder builder = new NotificationCompat.Builder(this, FCM_CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_baseline_notifications_24)
                        .setContentTitle(title)
                        .setColor(Color.GREEN)
                        .setContentText(body);

                NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

                // Use a unique notification ID
                int notificationId = (int) System.currentTimeMillis();

                try {
                    // Check the version of the Android OS before creating notification channels
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        NotificationChannel channel = new NotificationChannel(
                                FCM_CHANNEL_ID,
                                "Firebase Cloud Messaging",
                                NotificationManager.IMPORTANCE_HIGH
                        );
                        manager.createNotificationChannel(channel);
                        builder.setChannelId(FCM_CHANNEL_ID);
                    }

                    Notification notification = builder.build();
                    manager.notify(notificationId, notification);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }

    @Override
    public void onDeletedMessages() {
        super.onDeletedMessages();
    }

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
    }
}

