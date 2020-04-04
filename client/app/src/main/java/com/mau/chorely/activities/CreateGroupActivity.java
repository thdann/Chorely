package com.mau.chorely.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;

import com.mau.chorely.R;
import com.mau.chorely.model.Model;
import com.mau.chorely.model.ModelInstances;

import java.security.acl.Group;

import shared.transferable.NetCommands;
import shared.transferable.RequestID;
import shared.transferable.TransferList;

public class CreateGroupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);
        ActionBar ab = getSupportActionBar();
        // R.menu.mymenu is a reference to an xml file named mymenu.xml which should be inside your res/menu directory.
        // If you don't have res/menu, just create a directory named "menu" inside res



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onStart() {
        super.onStart();
        new GetGroups().execute(NetCommands.requestUserGroups);
    }

    private void updateGroups(Group[] updatedGroups){
        if(updatedGroups != null){






        }
    }

    private class GetGroups extends AsyncTask<NetCommands, Void, NetCommands> {
        @Override
        protected NetCommands doInBackground(NetCommands... netCommands) {
            NetCommands command = netCommands[0];
            Model model = ModelInstances.getInstance();
            return model.notifyForResponse(new TransferList(command, new RequestID()));
        }

        @Override
        protected void onPostExecute(NetCommands netCommands) {

            switch (netCommands){
                case userHasNoGroup:
                    //Todo notify user.
                    break;
                case userHasGroupUpdate:
                    updateGroups(ModelInstances.getInstance().getGroups());
                    break;

            }

        }
    }



}
