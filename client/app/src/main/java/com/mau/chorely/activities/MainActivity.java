package com.mau.chorely.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.mau.chorely.R;
import com.mau.chorely.activities.interfaces.UpdatableActivity;
import com.mau.chorely.activities.utils.Presenter;
import com.mau.chorely.model.Model;

/**
 * This is the main activity of the application. It launches the program and sends the user to
 * the appropriate activity depending on the state of the application.
 * @author Timothy Denidson, Fredrik Jeppson.
 */
public class MainActivity extends AppCompatActivity implements UpdatableActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Presenter.getInstance().register(this);
        Model.getInstance(getFilesDir(),this); // startar modellen.
        createNotificationChannel();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Presenter.getInstance().register(this);
        updateActivity();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Presenter.getInstance().deregisterForUpdates(this);
    }

    /**
     * Interface method to update activity.
     */
    @Override
    public  void updateActivity() {
        if (Model.getInstance(getFilesDir(),this).isConnected() ) {
            if (Model.getInstance(getFilesDir(),this).hasStoredUser()) {
                if(Model.getInstance(getFilesDir(),this).getSelectedGroup() != null ){
                    Intent intent = new Intent(this, CentralActivity.class);
                    startActivity(intent);
                    finish();
                } else if(Model.getInstance(getFilesDir(),this).isLoggedIn()){
                    Intent intent = new Intent(this, ManageGroupsActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        } else {
            Intent intent = new Intent(this, ConnectActivity.class);
            startActivity(intent);
        }
    }

    /**
     * Interface method
     * @param message message to toast
     */
    @Override
    public void doToast(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG);
                toast.show();
            }
        });
    }

    /**
     * Method for handling clicks on register button.
     * @param view an object containing the view that called the method.
     */
    public void register(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    public void login(View view) {
        Intent intent = new Intent(this, LogInActivity.class);
        startActivity(intent);
    }

    /**
     * Author: Johan och Måns
     * Creates a notification channel for the application.
     * This method creates a notification channel with the ID "Notifications",
     * a name taken from the string resources, and a default importance.
     * @return The description of the notification channel or null.
     */
    public String createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.textAddMembers);
            String description = "Notification Channel";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("Notifications", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
            return description;
        }
        return null;
    }


}
