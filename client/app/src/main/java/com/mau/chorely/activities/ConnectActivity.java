package com.mau.chorely.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.mau.chorely.R;
import com.mau.chorely.activities.interfaces.UpdatableActivity;
import com.mau.chorely.model.Model;
import com.mau.chorely.activities.utils.BridgeInstances;

import shared.transferable.NetCommands;
import shared.transferable.GenericID;
import shared.transferable.TransferList;

public class ConnectActivity extends AppCompatActivity implements UpdatableActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BridgeInstances.getPresenter().register(this);
        BridgeInstances.instantiateModel(getFilesDir()); // startar modellen.
    }

    @Override
    protected void onStart() {
        super.onStart();
        //setContentView(R.layout.create_edit_group);
        setContentView(R.layout.activity_connect2);
    }

    @Override
    protected void onStop() {
        super.onStop();
        BridgeInstances.getPresenter().deregisterForUpdates(this);
    }

    @Override
    public void UpdateActivity() {
        if (BridgeInstances.getModel().isConnected()) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (BridgeInstances.getModel().isConnected()) {
                        if(BridgeInstances.getModel().isLoggedIn()){
                            startActivity(new Intent(ConnectActivity.this, CreateGroupActivity.class));
                        } else {
                            Intent intent = new Intent(ConnectActivity.this, MainActivity.class);
                            startActivity(intent);
                        }
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
