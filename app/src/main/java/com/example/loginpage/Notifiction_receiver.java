package com.example.loginpage;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

import java.util.Random;

public class Notifiction_receiver  extends BroadcastReceiver {
    Random random = new Random();
    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManager notificationManager =  (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent repeating_intent = new Intent(context, MainActivity.class);
        repeating_intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        String[] content = {"Been Shopping Yet? Come and update your materials!", "Track and Trigger to the rescue! Update your stock",
                "Forgetting something? Track your current stock", "Save your earnings! Head to Track and Trigger"};


        PendingIntent pendingIntent = PendingIntent.getActivity(context, 1500, repeating_intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.logox)
                .setContentText(content[random.nextInt(3 - 0 + 1) + 0])
                .setAutoCancel(true)
                .setContentTitle("Track and Trigger");

        if(intent.getAction().equals("MY_NOTIFICATION_MESSAGE")){
            notificationManager.notify(1500, builder.build());
        }

    }
}
