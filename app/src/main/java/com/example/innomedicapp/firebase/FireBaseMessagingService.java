package com.example.innomedicapp.firebase;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.example.innomedicapp.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class FireBaseMessagingService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived( remoteMessage );

        if(remoteMessage.getNotification() != null) {
            System.out.println("Notification: " + remoteMessage.getNotification().getBody());

            try {

                Notification notification = new NotificationCompat.Builder(this)
                        .setContentTitle(remoteMessage.getNotification().getTitle())
                        .setContentText(remoteMessage.getNotification().getBody())
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .build();
                NotificationManagerCompat manager = NotificationManagerCompat.from(getApplicationContext());
                manager.notify(123, notification);

            }catch (Exception e) {

            }


        }
    }
}
