package com.mau.chorely.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;

import com.mau.chorely.R;
import com.mau.chorely.model.Model;
import com.mau.chorely.activities.utils.BridgeInstances;

import shared.transferable.NetCommands;
import shared.transferable.GenericID;
import shared.transferable.TransferList;

public class ConnectActivity extends AppCompatActivity {
    TextView status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();
        setContentView(R.layout.activity_connect2);
        new Connect().execute(NetCommands.connectionStatus);
        status = findViewById(R.id.textView2);
    }

    private class Connect extends AsyncTask<NetCommands, Void , NetCommands>{

        @Override
        protected NetCommands doInBackground(NetCommands... netCommands) {
            NetCommands command = netCommands[0];
            Model model = BridgeInstances.getModel();
            TransferList transferees = new TransferList(command, new GenericID());
            return model.notifyForResponse(transferees);
        }

        @Override
        protected void onPostExecute(NetCommands netCommand) {
           switch (netCommand){
               case connected:
                   Intent intent = new Intent(ConnectActivity.this, MainActivity.class);
                   startActivity(intent);
                   break;
               case notConnected:
                   status.setText(R.string.retrying);
                   new Connect().execute(NetCommands.connectionStatus);

           }
        }
    }
}
