package com.carlv.medicaldeliverydoctor.Helpers;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;

import com.carlv.medicaldeliverydoctor.R;

public class NotificationService {
    Activity activity;
    int NOTIFICATION_ID = 234;
    String CHANNEL_ID;
    CharSequence name;
    String description;
    NotificationManager notificationManager;
    public NotificationService(Activity activity) {
        this.activity = activity;
        notificationManager = (NotificationManager) activity.getBaseContext().getSystemService(activity.getBaseContext().NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            CHANNEL_ID = "my_channel_01";
            name = "my_channel";
            description = "This is my channel";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            mChannel.setDescription(description);
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.RED);
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            mChannel.setShowBadge(false);
            notificationManager.createNotificationChannel(mChannel);

        }
    }
    public void sendNotification(String title, String msg) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(activity.getApplicationContext(), CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(msg);

        Intent resultIntent = new Intent(activity.getApplicationContext(), activity.getClass());
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(activity.getApplicationContext());
        stackBuilder.addParentStack(activity.getClass());
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setContentIntent(resultPendingIntent);

        notificationManager.notify(NOTIFICATION_ID, builder.build());

    }
}
