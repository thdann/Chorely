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

/**
 * This activity handles all creations of groups and editing to already existing groups.
 * @author Timothy Denison
 */
public class CreateEditGroupActivity extends AppCompatActivity implements UpdatableActivity {
    private Group selectedGroup;
    private SpinnerAdapterMembers spinnerAdapter;
    private User lastSearchedUser;
    private boolean newGroup = false;

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
        if (bundle != null) {
            selectedGroup = (Group) bundle.get("SELECTED_GROUP");
        }
        initActivity();
    }

    @Override
    protected void onStop() {
        BridgeInstances.getPresenter().deregisterForUpdates(this);
        super.onStop();
    }

    /**
     * Method to set menu recource file.
     * @param menu system set action-bar.
     * @return super response.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_create_edit, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * interface method to update activity.
     */
    @Override
    public void updateActivity() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Model model = BridgeInstances.getModel();
                if (model.isConnected()) {
                    if (lastSearchedUser == null) {
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
                } else {
                    startActivity(new Intent(CreateEditGroupActivity.this, ConnectActivity.class));
                }
            }
        });

    }

    /**
     * Interface method to toast user.
     * @param message message to toast.
     */
    @Override
    public void doToast(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(CreateEditGroupActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Callback for menu items
     * @param item item clicked.
     * @return super response.
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.edit_group_menu_saveChanges) {
            saveGroup();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Method to initiate spinner of group members.
     */
    private void initSpinner() {
        Spinner memberSpinner = findViewById(R.id.spinnerMembers);
        spinnerAdapter = new SpinnerAdapterMembers(this, selectedGroup.getUsers());
        memberSpinner.setAdapter(spinnerAdapter);
    }

    /**
     * Method to put the activity in different initial states depending on if the user was sent here
     * by selecting a group to edit, or by creating a new group.
     */
    private void initActivity() {
        if (selectedGroup != null) {
            EditText groupName = (EditText) findViewById(R.id.edit_group_current_name);
            groupName.setText(selectedGroup.getName());
            groupName.setFocusable(false);
            groupName.setFocusableInTouchMode(false);
            EditText groupDescription = (EditText) findViewById(R.id.edit_group_edit_description_text);
            groupDescription.setText(selectedGroup.getDescription());
            groupDescription.setFocusable(false);
            if (spinnerAdapter == null) {
                initSpinner();
            }
        } else {
            findViewById(R.id.edit_group_edit_name_button).setVisibility(View.INVISIBLE);
            findViewById(R.id.edit_group_edit_description_button).setVisibility(View.INVISIBLE);
            selectedGroup = new Group();
            newGroup = true;
            selectedGroup.addUser(BridgeInstances.getModel().getUser());
            initSpinner();
        }
    }

    /**
     * Method to save group. Invoked by menu button.
     * If either description or name is empty the method will invoke a toast instead of saving.
     */
    public void saveGroup() {
        String groupName = ((EditText) findViewById(R.id.edit_group_current_name)).getText().toString();
        String groupDescription = ((EditText) findViewById(R.id.edit_group_edit_description_text)).getText().toString();
        if (!groupName.equals("")) {
            if (!groupDescription.equals("")) {
                NetCommands command;
                if(newGroup){
                    command = NetCommands.registerNewGroup;
                } else{
                    command = NetCommands.clientInternalGroupUpdate;
                }
                Model model = BridgeInstances.getModel();
                ArrayList<Transferable> data = new ArrayList<>();
                selectedGroup.setName(groupName);
                selectedGroup.setDescription(groupDescription);
                data.add(selectedGroup);
                Message message = new Message(command, model.getUser(), data);
                model.handleTask(message);
                newGroup = false;
                finish();
            } else {
                doToast("Fyll i beskrivning till din grupp.");
            }
        } else {
            doToast("Fyll i ett namn till gruppen.");
        }
    }

    /**
     * Method to remove a user from a group.
     * @param view button pressed.
     */
    public void removeMemberFromGroup(View view) {
        // TODO: 2020-04-12 POPUP DIALOG ask for confirmation?
        if (spinnerAdapter.getCount() > 1) {
            int selectedUserIndex = ((Spinner) findViewById(R.id.spinnerMembers)).getSelectedItemPosition();
            selectedGroup.getUsers().remove(selectedUserIndex);
            spinnerAdapter.notifyDataSetChanged();
        } else {
            Toast.makeText(this, "Varje grupp måste ha minst en användare. Om du vill " +
                            "radera gruppen kan du göra det i menyn.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Method to handle search events.
     * puts activity in a searching state in wait for reply.
     * @param view Button clicked.
     */
    public void searchForMember(View view) {
        String searchString = ((EditText)findViewById(R.id.edit_group_memberSearchText)).getText().toString();
        if (!searchString.equals("")) {
            findViewById(R.id.edit_group_searchMemberButton).setVisibility(View.INVISIBLE);
            findViewById(R.id.edit_group_memberSearchWorkingGif).setVisibility(View.VISIBLE);
            Model model = BridgeInstances.getModel();
            User user = new User(searchString, "");
            ArrayList<Transferable> data = new ArrayList<>();
            data.add(user);
            Message message = new Message(NetCommands.searchForUser, model.getUser(), data);
            model.handleTask(message);
        } else {
            Toast.makeText(this, "Du har inte fyllt i något användarnamn", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Method to return activity to searchable state. Invoked by a clock on the cancel button.
     * @param view button clicked.
     */
    public void cancelFoundMember(View view) {
        lastSearchedUser = null;
        findViewById(R.id.edit_group_memberSearchCancelButton).setVisibility(View.INVISIBLE);
        findViewById(R.id.edit_group_addMemberButton).setVisibility(View.INVISIBLE);
        findViewById(R.id.edit_group_memberSearchWorkingGif).setVisibility(View.INVISIBLE);
        findViewById(R.id.edit_group_searchMemberButton).setVisibility(View.VISIBLE);
        findViewById(R.id.edit_group_memberSearchText).setFocusable(true);
        ((EditText) findViewById(R.id.edit_group_memberSearchText)).setText("");

    }

    /**
     * Method to handle clicks on add button.
     * @param view
     */
    public void addMember(View view) {
        selectedGroup.addUser(lastSearchedUser);
        spinnerAdapter.notifyDataSetChanged();
        cancelFoundMember(null);
    }

    /**
     * Method to handle click on edit name button.
     * @param view Button klicked.
     */
    public void editGroupName(View view) {
        EditText groupName = findViewById(R.id.edit_group_current_name);
        groupName.setFocusableInTouchMode(true);
        groupName.setFocusable(true);
        groupName.requestFocus();
    }

    /**
     * Method to handle clicks on edit button for group description.
     * @param view view clicked.
     */
    public void editGroupDescription(View view) {
        EditText groupDescription = findViewById(R.id.edit_group_edit_description_text);
        groupDescription.setFocusableInTouchMode(true);
        groupDescription.setFocusable(true);
        groupDescription.requestFocus();
    }
}
