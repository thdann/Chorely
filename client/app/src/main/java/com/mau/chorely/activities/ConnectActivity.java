package com.mau.chorely.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.mau.chorely.R;
import com.mau.chorely.activities.interfaces.UpdatableActivity;
import com.mau.chorely.activities.utils.BridgeInstances;

/**
 * This activity gets invoked if the client looses its connection to the server.
 * The user is kept here until the connection is re-established, and is then sent to the
 * previous activity in the backstack.
 * @author Timothy denison
 */
public class ConnectActivity extends AppCompatActivity implements UpdatableActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();
        BridgeInstances.getPresenter().register(this);
        setContentView(R.layout.activity_connect2);
        updateActivity();
    }

    @Override
    protected void onStop() {
        super.onStop();
        BridgeInstances.getPresenter().deregisterForUpdates(this);
    }

    @Override
    public void updateActivity() {
        if (BridgeInstances.getModel(getFilesDir()).isConnected()) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (BridgeInstances.getModel(getFilesDir()).isConnected()) {
                      finish();
                    }
                }
            });
        }
    }

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
}
