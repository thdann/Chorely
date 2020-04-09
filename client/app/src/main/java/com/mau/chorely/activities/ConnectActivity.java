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

public class ConnectActivity extends AppCompatActivity  implements UpdatableActivity {
    TextView status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    protected void onStart() {
        super.onStart();

        setContentView(R.layout.activity_connect2);
        status = findViewById(R.id.textView2);

        BridgeInstances.getPresenter().register(this);
        BridgeInstances.getModel(); // startar modellen.
    }

    @Override
    protected void onStop() {
        super.onStop();
        BridgeInstances.getPresenter().deregisterForUpdates(this);
    }

    @Override
    public void UpdateActivity() {
        if(BridgeInstances.getModel().isConnected()){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (BridgeInstances.getModel().isConnected()) {
                        Intent intent = new Intent(ConnectActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                    else{

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

    /*

    private class Connect extends AsyncTask<NetCommands, Void , NetCommands>{

        @Override
        protected NetCommands doInBackground(NetCommands... netCommands) {
            NetCommands command = netCommands[0];
            Model model = BridgeInstances.getModel();
            TransferList transferees = new TransferList(command, new GenericID());

        }

        @Override
        protected void onPostExecute(NetCommands netCommand) {
           switch (netCommand){
               case connected:

                   break;
               case notConnected:
                   status.setText(R.string.retrying);
                   new Connect().execute(NetCommands.connectionStatus);

           }
        }
    }

     */
}
