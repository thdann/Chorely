package com.mau.chorely.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.mau.chorely.R;
import com.mau.chorely.activities.interfaces.UpdatableActivity;
import com.mau.chorely.activities.utils.BridgeInstances;

public class MainActivity extends AppCompatActivity implements UpdatableActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BridgeInstances.getPresenter().register(this);
        BridgeInstances.instantiateModel(getFilesDir()); // startar modellen.
        //ModelInstances.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();
        BridgeInstances.getPresenter().register(this);
        updateActivity();
    }

    @Override
    protected void onStop() {
        super.onStop();
        BridgeInstances.getPresenter().deregisterForUpdates(this);
    }

    @Override
    public void updateActivity() {
        if (BridgeInstances.getModel().isConnected()) {
            if (BridgeInstances.getModel().isLoggedIn()) {
                Intent intent = new Intent(this, ManageGroupsActivity.class);
                startActivity(intent);
                finish();
            }
        } else {
            Intent intent = new Intent(this, ConnectActivity.class);
            startActivity(intent);
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

    public void register(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);

        startActivity(intent);
    }

    public void login(View view) {
    }


}
