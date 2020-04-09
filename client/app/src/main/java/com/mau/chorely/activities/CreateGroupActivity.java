package com.mau.chorely.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;

import com.mau.chorely.R;
import com.mau.chorely.activities.interfaces.UpdatableActivity;
import com.mau.chorely.activities.utils.ListItem;
import com.mau.chorely.activities.utils.RecyclerViewAdapter;
import com.mau.chorely.model.Model;
import com.mau.chorely.activities.utils.BridgeInstances;

import java.util.ArrayList;

import shared.transferable.Group;
import shared.transferable.Message;
import shared.transferable.NetCommands;
import shared.transferable.GenericID;
import shared.transferable.TransferList;

public class CreateGroupActivity extends AppCompatActivity implements UpdatableActivity {
    private RecyclerView mRecyclerView;
    private RecyclerViewAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    ArrayList<ListItem> groupList = new ArrayList<>();
    ArrayList<Group> updatedGroups = new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);
        buildRecyclerView();

    }

    private void buildRecyclerView(){
        groupList.add(new ListItem(R.drawable.ic_group_black_24dp, "Blabla", "Blaaaslfkasflkasfadssss"));
        groupList.add(new ListItem(R.drawable.ic_group_black_24dp, "Blabla", "asdasdasd"));
        groupList.add(new ListItem(R.drawable.ic_group_black_24dp, "Blabla", "Blaaaslfkasflkasf"));
        mRecyclerView = findViewById(R.id.recyclerViewGroups);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new RecyclerViewAdapter(groupList);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new RecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {

                //Message message = new Message();


            }
        });


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


    @Override
    public void doToast(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // TODO: 2020-04-07 Implementera toasts
            }
        });
    }

    @Override
    public void UpdateActivity() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //TODO update
            }
        });
    }

    private void updateGroups(Group[] updatedGroups){
        if(updatedGroups != null){
            for (Group group : updatedGroups){
                groupList.add(new ListItem(R.drawable.ic_group_black_24dp, group.getName(), group.getDescription()));
                //TODO lägg till textfält för användare i xml-fil för recyclerciew.
                // ta ut alla users och lägg till namn.
                // kanske aktiviteten måste hålla en kopia av arrayen med grupper, för att möjliggöra
                // kontroll om denna blivit uppdaterad sedan sist.
            }
            mAdapter.notifyDataSetChanged();
        }
    }

/*


    private class GetGroups extends AsyncTask<NetCommands, Void, NetCommands> {
        @Override
        protected NetCommands doInBackground(NetCommands... netCommands) {
            NetCommands command = netCommands[0];
            Model model = BridgeInstances.getModel();
            return model.notifyForResponse(new TransferList(command, new GenericID()));
        }

        @Override
        protected void onPostExecute(NetCommands netCommands) {

            switch (netCommands){
                case userHasNoGroup:
                    //Todo notify user.
                    break;
                case userHasGroupUpdate:
                    updateGroups(BridgeInstances.getModel().getGroups());
                    break;
                default:


            }

        }
    }





 */

}
