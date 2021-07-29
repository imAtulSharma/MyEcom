package com.streamliners.admin_app.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.streamliners.admin_app.MainActivity;
import com.streamliners.admin_app.R;

import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Random;

public class FCMReceiverService extends FirebaseMessagingService {
    private final static String CHANNEL_ID = "new-orders";

    @Override
    public void onMessageReceived(@NonNull @NotNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        createNotificationChannel();

        RemoteMessage.Notification notification = remoteMessage.getNotification();
        notify(notification.getTitle(), notification.getBody());

        workingWithData(remoteMessage.getData());
    }

    /**
     * Get the data and work on it
     * @param data data in the message
     */
    private void workingWithData(Map<String, String> data) {
        String orderId = data.get("orderId");

        Log.e("NotificationTag", "Order ID: " + orderId);
    }

    /**
     * To notify the admin using notification
     * @param title title of the message
     * @param body body of the message
     */
    private void notify(String title, String body) {
        // Making the pending intent
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Building the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_order)
                .setContentTitle(title)
                .setContentText(body)
                .setColor(getResources().getColor(R.color.teal_200))
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(body));

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.notify(new Random().nextInt(10000), builder.build());
    }

    /**
     * Creating a notification channel
     */
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            String name = "New order notification";
            String description = "This is the main channel for notifications";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            mChannel.setDescription(description);

            mChannel.setVibrationPattern(new long[]{0, 500, 1000});
            AudioAttributes.Builder attrs = new AudioAttributes.Builder();
            attrs.setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION);
            attrs.setUsage(AudioAttributes.USAGE_ALARM);
            mChannel.setSound(Settings.System.DEFAULT_NOTIFICATION_URI, attrs.build());

            NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(mChannel);
        }
    }
}
