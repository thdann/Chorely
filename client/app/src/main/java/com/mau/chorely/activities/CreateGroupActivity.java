package com.mau.chorely.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import com.mau.chorely.R;
import com.mau.chorely.activities.interfaces.UpdatableActivity;
import com.mau.chorely.activities.utils.ListItem;
import com.mau.chorely.activities.utils.RecyclerViewAdapter;
import com.mau.chorely.model.Model;
import com.mau.chorely.activities.utils.BridgeInstances;

import java.util.ArrayList;

import shared.transferable.Chore;
import shared.transferable.Group;
import shared.transferable.Message;
import shared.transferable.NetCommands;
import shared.transferable.GenericID;
import shared.transferable.TransferList;
import shared.transferable.Transferable;
import shared.transferable.User;

public class CreateGroupActivity extends AppCompatActivity implements UpdatableActivity {
    private RecyclerView mRecyclerView;
    private RecyclerViewAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    ArrayList<Group> groupList = new ArrayList<>();
    ArrayList<Group> updatedGroups = new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);
        buildRecyclerView();
        //mRecyclerView.setVisibility(View.INVISIBLE);

    }

    @Override
    protected void onStart() {
        super.onStart();
        BridgeInstances.getPresenter().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        BridgeInstances.getPresenter().deregisterForUpdates(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void buildRecyclerView(){
        /*
        groupList.add(new ListItem(R.drawable.ic_group_black_24dp, "Blabla", "Blaaaslfka\nflkasfadssss" , 3));
        groupList.add(new ListItem(R.drawable.ic_group_black_24dp, "Blabla", "asdasdasd", 3));
        groupList.add(new ListItem(R.drawable.ic_group_black_24dp, "Blabla", "Blaaaslfkasflkasf", 13));
         */

        User user = new User("Tim", "asdasd");
        //Chore chore = new Chore("Testchore", 2);
        Group group = new Group("Min Grupp", "Detta är min fina grupp där jag lagt mina sysslor");
        group.addUser(user);
        groupList.add(group);


        mRecyclerView = findViewById(R.id.recyclerViewGroups);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new RecyclerViewAdapter(groupList);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new RecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                ArrayList<Transferable> sendList = new ArrayList<>();
                sendList.add(groupList.get(position));
                Message message = new Message(NetCommands.addUserToGroup, BridgeInstances.getModel().getUser(),sendList);
                BridgeInstances.getModel().handleTask(message);
                mRecyclerView.setVisibility(View.INVISIBLE);
            }
        });
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
    private void addGroupToRecyclerView(Group group){
        groupList.add(new ListItem(R.drawable.ic_group_black_24dp, group.getName(), group.getDescription() , group.size()));
    }
     */

    @Override
    public void UpdateActivity() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(BridgeInstances.getModel().isConnected()){
                     updatedGroups = BridgeInstances.getModel().getGroups();
                     updateGroups();

                }
                else{
                    Intent intent = new Intent(CreateGroupActivity.this, ConnectActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    private void updateGroups(){
        if(updatedGroups != null){
            for (int i = 0; i< updatedGroups.size(); i++){
                Group group = updatedGroups.get(i);
                if(groupList.contains(group)){
                    int shownGroupIndex = groupList.indexOf(group);
                    if(!groupList.get(shownGroupIndex).allIsEqual(group)){
                        groupList.remove(shownGroupIndex);
                        groupList.add(shownGroupIndex, group);
                        mAdapter.notifyDataSetChanged();
                    }
                }
                else{
                    groupList.add(group);
                    mAdapter.notifyDataSetChanged();
                }
            }

            if(updatedGroups.size() < groupList.size()){
                for (Group group : groupList){
                    if (!updatedGroups.contains(group)){
                        groupList.remove(group);
                    }
                }
            }
        }
    }
}
