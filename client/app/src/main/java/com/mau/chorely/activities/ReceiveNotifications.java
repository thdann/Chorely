package com.mau.chorely.activities;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.mau.chorely.R;
import com.mau.chorely.activities.interfaces.UpdatableActivity;
import com.mau.chorely.model.Model;

public class ReceiveNotifications extends Activity {

    private NotificationManager notificationManager;
    private int counter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("onCreate");
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }

    public void receiveNotification() {
        System.out.println("start");
        System.out.println(counter);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                counter++;
                System.out.println("run");
                NotificationCompat.Builder builder = new NotificationCompat.Builder(ReceiveNotifications.this, "MemberAdded")
                        .setSmallIcon(R.drawable.logo)
                        .setContentTitle("Member Added")
                        .setContentText("lastSearchedUser.getUsername()" + " was added to " + "selectedGroup.getName()")
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);
                notificationManager.notify(2, builder.build());
                System.out.println("end");
            }
        });
        System.out.println("counter: " + counter);

    }


}
