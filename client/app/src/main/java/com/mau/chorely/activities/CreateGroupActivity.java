package com.mau.chorely.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;

import com.mau.chorely.R;
import com.mau.chorely.model.Model;
import com.mau.chorely.model.ModelInstances;

import java.security.acl.Group;
import java.util.ArrayList;

import shared.transferable.NetCommands;
import shared.transferable.RequestID;
import shared.transferable.TransferList;

public class CreateGroupActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);


        ArrayList<ListItem> groupList = new ArrayList<>();
        groupList.add(new ListItem(R.drawable.ic_group_black_24dp, "Blabla", "Blaaaslfkasflkasf"));
        groupList.add(new ListItem(R.drawable.ic_group_black_24dp, "Blabla", "asdasdasd"));
        groupList.add(new ListItem(R.drawable.ic_group_black_24dp, "Blabla", "Blaaaslfkasflkasf"));

        mRecyclerView = findViewById(R.id.recyclerViewGroups);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new RecyclerViewAdapter(groupList);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

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
