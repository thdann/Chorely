package com.mau.chorely.activities;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.mau.chorely.R;
import com.mau.chorely.activities.interfaces.UpdatableActivity;
import com.mau.chorely.activities.utils.BridgeInstances;
import com.mau.chorely.activities.utils.SpinnerAdapterMembers;
import com.mau.chorely.model.Model;

import java.util.ArrayList;

import shared.transferable.Group;
import shared.transferable.Message;
import shared.transferable.NetCommands;
import shared.transferable.Transferable;
import shared.transferable.User;

public class CreateEditGroupActivity extends AppCompatActivity implements UpdatableActivity {
    private Group selectedGroup;
    private SpinnerAdapterMembers spinnerAdapter;
    private User lastSearchedUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_edit_group);
    }

    @Override
    protected void onStart() {
        super.onStart();
        BridgeInstances.getPresenter().register(this);
        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            selectedGroup = (Group) bundle.get("SELECTED_GROUP");
        }
        initActivity();
    }

    @Override
    protected void onStop() {
        BridgeInstances.getPresenter().deregisterForUpdates(this);
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_create_edit, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void updateActivity() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Model model = BridgeInstances.getModel();
                if(model.isConnected()){
                    if(lastSearchedUser == null) {
                        lastSearchedUser = model.removeLastSearchedUser();
                        if (lastSearchedUser != null) {
                            findViewById(R.id.edit_group_memberSearchCancelButton).setVisibility(View.VISIBLE);
                            findViewById(R.id.edit_group_addMemberButton).setVisibility(View.VISIBLE);
                            findViewById(R.id.edit_group_memberSearchWorkingGif).setVisibility(View.INVISIBLE);
                            findViewById(R.id.edit_group_memberSearchText).setFocusable(false);
                            findViewById(R.id.edit_group_memberSearchText).setFocusableInTouchMode(false);
                        } else {
                            findViewById(R.id.edit_group_memberSearchCancelButton).setVisibility(View.INVISIBLE);
                            findViewById(R.id.edit_group_addMemberButton).setVisibility(View.INVISIBLE);
                            findViewById(R.id.edit_group_memberSearchWorkingGif).setVisibility(View.INVISIBLE);
                            findViewById(R.id.edit_group_searchMemberButton).setVisibility(View.VISIBLE);
                            findViewById(R.id.edit_group_memberSearchText).setFocusable(true);
                            findViewById(R.id.edit_group_memberSearchText).setFocusableInTouchMode(true);
                        }
                    }
                } else{
                    startActivity(new Intent(CreateEditGroupActivity.this, ConnectActivity.class));
                }
            }
        });

    }

    @Override
    public void doToast(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(CreateEditGroupActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.edit_group_menu_saveChanges){
            saveGroup();
        }
        return super.onOptionsItemSelected(item);
    }

    private void initSpinner(){
        Spinner memberSpinner = findViewById(R.id.spinnerMembers);
        spinnerAdapter = new SpinnerAdapterMembers(this, selectedGroup.getUsers());
        memberSpinner.setAdapter(spinnerAdapter);
    }

    private void initActivity(){
        if(selectedGroup != null) {
            EditText groupName = (EditText)findViewById(R.id.edit_group_current_name);
            groupName.setText(selectedGroup.getName());
            groupName.setFocusable(false);
            groupName.setFocusableInTouchMode(false);
            EditText groupDescription = (EditText)findViewById(R.id.edit_group_edit_description_text);
            groupDescription.setText(selectedGroup.getDescription());
            groupDescription.setFocusable(false);
            if (spinnerAdapter == null) {
                initSpinner();
            }
        } else{
            findViewById(R.id.edit_group_edit_name_button).setVisibility(View.INVISIBLE);
            findViewById(R.id.edit_group_edit_description_button).setVisibility(View.INVISIBLE);
            selectedGroup = new Group();
            initSpinner();
        }
    }

    public void saveGroup(){
        Model model = BridgeInstances.getModel();
        ArrayList<Transferable> data = new ArrayList<>();
        data.add(selectedGroup);
        Message message = new Message(NetCommands.updateGroup, model.getUser(), data);
        model.handleTask(message);
        finish();
    }

    public void removeMemberFromGroup(View view){
        // TODO: 2020-04-12 POPUP DIALOG ask for confirmation?
        if(spinnerAdapter.getCount() > 0) {
            int selectedUserIndex = ((Spinner) findViewById(R.id.spinnerMembers)).getSelectedItemPosition();
            User removedUser = selectedGroup.getUsers().remove(selectedUserIndex);
            Model model = BridgeInstances.getModel();
            ArrayList<Transferable> data = new ArrayList<>();
            data.add(removedUser);
            data.add(selectedGroup.getGroupID());
            Message message = new Message(NetCommands.removeUserFromGroup, model.getUser(), data);
            model.handleTask(message);
            spinnerAdapter.notifyDataSetChanged();
        }
        else{
            Toast.makeText(this, "Det finns ingen användare att ta bort.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    public void searchForMember(View view){
        String searchString = ((EditText)findViewById(R.id.edit_group_memberSearchText)).getText().toString();
        if(!searchString.equals("")) {
            findViewById(R.id.edit_group_searchMemberButton).setVisibility(View.INVISIBLE);
            findViewById(R.id.edit_group_memberSearchWorkingGif).setVisibility(View.VISIBLE);
            Model model = BridgeInstances.getModel();
            User user = new User(searchString, "");
            ArrayList<Transferable> data = new ArrayList<>();
            data.add(user);
            Message message = new Message(NetCommands.searchForUser, model.getUser(), data);
            model.handleTask(message);
        } else{
            Toast.makeText(this, "Du har inte fyllt i något användarnamn", Toast.LENGTH_SHORT).show();
        }
    }

    public void cancelFoundMember(View view){
        lastSearchedUser = null;
        findViewById(R.id.edit_group_memberSearchCancelButton).setVisibility(View.INVISIBLE);
        findViewById(R.id.edit_group_addMemberButton).setVisibility(View.INVISIBLE);
        findViewById(R.id.edit_group_memberSearchWorkingGif).setVisibility(View.INVISIBLE);
        findViewById(R.id.edit_group_searchMemberButton).setVisibility(View.VISIBLE);
        findViewById(R.id.edit_group_memberSearchText).setFocusable(true);
        ((EditText)findViewById(R.id.edit_group_memberSearchText)).setText("");

    }

    public void addMember(View view){
        selectedGroup.addUser(lastSearchedUser);
        spinnerAdapter.notifyDataSetChanged();
        cancelFoundMember(null);
    }


    public void editGroupName(View view){
        EditText groupName = findViewById(R.id.edit_group_current_name);
        groupName.setFocusableInTouchMode(true);
        groupName.setFocusable(true);
        groupName.requestFocus();
    }

    public void editGroupDescription(View view){
        EditText groupDescription = findViewById(R.id.edit_group_edit_description_text);
        groupDescription.setFocusableInTouchMode(true);
        groupDescription.setFocusable(true);
        groupDescription.requestFocus();
    }
}
