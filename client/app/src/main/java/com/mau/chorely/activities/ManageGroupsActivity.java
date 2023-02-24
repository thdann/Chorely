package com.mau.chorely.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.mau.chorely.R;
import com.mau.chorely.activities.interfaces.UpdatableActivity;
import com.mau.chorely.activities.utils.Presenter;
import com.mau.chorely.activities.utils.RecyclerViewAdapter;
import com.mau.chorely.model.Model;

import java.util.ArrayList;

import shared.transferable.Group;
import shared.transferable.Message;
import shared.transferable.NetCommands;
import shared.transferable.Transferable;

/**
 * This is the activity to overview current groups and initiate creation of new ones.
 *
 * @author Timothy Denison, Emma Svensson
 */
public class ManageGroupsActivity extends AppCompatActivity implements UpdatableActivity {
    private RecyclerView mRecyclerView;
    private RecyclerViewAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private static final Object lockObjectGroupList = new Object();
    ArrayList<Group> groupList = new ArrayList<>();
    ArrayList<Group> updatedGroups = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_groups);
        buildRecyclerView();
        updatedGroups = Model.getInstance(getFilesDir(),this).getGroups();
        updateGroupsList();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Presenter.getInstance().register(this);
        updateActivity();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Presenter.getInstance().deregisterForUpdates(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Presenter.getInstance().deregisterForUpdates(this);
    }

    /**
     * Method to set menu layout file.
     *
     * @param menu reference to system created menu.
     * @return Returns super response.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Method to initiate the recyclerView.
     */
    private void buildRecyclerView() {
        mRecyclerView = findViewById(R.id.recyclerViewGroups);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new RecyclerViewAdapter(groupList);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new RecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Model.getInstance(getFilesDir(),getApplicationContext()).setSelectedGroup(groupList.get(position));
                Intent intent = new Intent(ManageGroupsActivity.this, CentralActivity.class);
                startActivity(intent);
            }
        });
    }


    public void newGroupClick(View v) {
        startActivity(new Intent(this, CreateEditGroupActivity.class));
    }


    /**
     * Method to handle action bar events.
     *
     * @param item Item that initiated event.
     * @return Returns super response.
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.logOut) {
        logOut();
        }
       return super.onContextItemSelected(item);

    }

    public String logOut() {
        Model model = Model.getInstance(getFilesDir(),this);
        Message logOutMsg = new Message(NetCommands.logout, model.getUser(), new ArrayList<Transferable>());
        model.handleTask(logOutMsg);
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
        return "logged out";
    }
    /**
     * Interface method to toast activity.
     *
     * @param message Message to toast
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
     * Interface method to update activity.
     */
    @Override
    public void updateActivity() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (Model.getInstance(getFilesDir(), getApplicationContext()).isConnected()) {
                    synchronized (lockObjectGroupList) {
                        updatedGroups = Model.getInstance(getFilesDir(),getApplicationContext()).getGroups();
                        updateGroupsList();
                        updateGroupText();
                    }
                } else {
                    Intent intent = new Intent(ManageGroupsActivity.this, ConnectActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    private String updateGroupText() {
        if (groupList.size() > 0) {
            ((TextView) findViewById(R.id.manage_groups_textViewYourGroups)).setText(R.string.groups);
            return "";
        } else {
            ((TextView) findViewById(R.id.manage_groups_textViewYourGroups)).setText(R.string.noGroup);
            return "You are not a member of any group";
        }
    }

    /**
     * Method to check if there are any updates to the client list of groups
     * and if there is set them to the recyclerview.
     */
    private void updateGroupsList() {
        if (updatedGroups != null) {
            synchronized (lockObjectGroupList) {
                for (int i = 0; i < updatedGroups.size(); i++) {
                    Group group = updatedGroups.get(i);
                    if (groupList.contains(group)) {
                        int shownGroupIndex = groupList.indexOf(group);
                        if (!groupList.get(shownGroupIndex).allIsEqual(group)) {
                            groupList.remove(shownGroupIndex);
                            groupList.add(group);
                            mAdapter.notifyDataSetChanged();
                        }
                    } else {
                        groupList.add(group);
                        mAdapter.notifyDataSetChanged();
                    }
                }

                if (updatedGroups.size() < groupList.size()) {
                    for (int i = 0; i < groupList.size(); i++) {
                        Group group = groupList.get(i);
                        if (!updatedGroups.contains(group)) {
                            groupList.remove(group);
                            mAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }
        }
    }
}
