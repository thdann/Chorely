package com.mau.chorely.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.mau.chorely.R;
import com.mau.chorely.activities.interfaces.UpdatableActivity;
import com.mau.chorely.activities.utils.ListViewAdapter;
import com.mau.chorely.activities.utils.Presenter;
import com.mau.chorely.model.Model;

import java.util.ArrayList;

import shared.transferable.Group;
import shared.transferable.Message;
import shared.transferable.NetCommands;
import shared.transferable.Transferable;
import shared.transferable.User;

/**
 * This activity handles all creations of groups and editing to already existing groups.
 *
 * @author Timothy Denison
 */
public class CreateEditGroupActivity extends AppCompatActivity implements UpdatableActivity {
    private Group selectedGroup;
    private User lastSearchedUser;
    private boolean newGroup = false;
    private ListView lv;
    private ArrayAdapter adapter;
    int selectedMemberIndex;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_edit_group);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Presenter.getInstance().register(this);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            //returns an empty group object
            selectedGroup = (Group) bundle.get("SELECTED_GROUP");
        }
        initActivity();
    }

    @Override
    protected void onStop() {
        Presenter.getInstance().deregisterForUpdates(this);
        super.onStop();
    }

    /**
     * Method to set menu recource file.
     *
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
                Model model = Model.getInstance(getFilesDir(),getApplicationContext());
                if (model.isConnected()) {
                    if (lastSearchedUser == null) {
                        lastSearchedUser = model.removeLastSearchedUser();
                        if (lastSearchedUser != null) {
                            findViewById(R.id.edit_group_memberSearchCancelButton).setVisibility(View.VISIBLE);
                            findViewById(R.id.edit_group_addMemberButton).setVisibility(View.VISIBLE);
                            findViewById(R.id.edit_group_memberSearchText).setFocusable(false);
                            findViewById(R.id.edit_group_memberSearchText).setFocusableInTouchMode(false);
                        } else {
                            findViewById(R.id.edit_group_memberSearchCancelButton).setVisibility(View.INVISIBLE);
                            findViewById(R.id.edit_group_addMemberButton).setVisibility(View.INVISIBLE);
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
     *
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
     *
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

    private void initListView() {
        lv = (ListView) findViewById(R.id.my_listView);
        adapter = new ListViewAdapter(this, selectedGroup.getUsers());
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedMemberIndex = position;
                ImageButton deleteButton = findViewById(R.id.edit_group_deleteMemberButton);
                deleteButton.setVisibility(View.VISIBLE);
            }
        });
        lv.setAdapter(adapter);
    }

    /**
     * Method to put the activity in different initial states depending on if the user was sent here
     * by selecting a group to edit, or by creating a new group.
     */
    private void initActivity() {
        if (selectedGroup != null) {
            //update an existing group
            setTitle("Redigera grupp");
            EditText groupName = (EditText) findViewById(R.id.edit_group_current_name);
            groupName.setText(selectedGroup.getName());
            groupName.setBackground(getDrawable(R.color.background));
            groupName.setFocusable(false);
            groupName.setFocusableInTouchMode(false);
            EditText groupDescription = (EditText) findViewById(R.id.edit_group_edit_description_text);
            groupDescription.setText(selectedGroup.getDescription());
            if (adapter == null) {
                initListView();
            }
        } else {
            //register a new group
            selectedGroup = new Group();
            newGroup = true;
            selectedGroup.setOwner(Model.getInstance(getFilesDir(),this).getUser().getUsername());
            selectedGroup.addMember(Model.getInstance(getFilesDir(),this).getUser());
            initListView();
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
                if (newGroup) {
                    command = NetCommands.registerNewGroup;
                } else {
                    command = NetCommands.clientInternalGroupUpdate;
                }
                Model model = Model.getInstance(getFilesDir(),this);
                ArrayList<Transferable> data = new ArrayList<>();
                selectedGroup.setName(groupName);
                selectedGroup.setDescription(groupDescription);
                selectedGroup.setOwner(model.getUser().getUsername());
                selectedGroup.addMember(model.getUser());
                selectedGroup.addToLeaderboard(model.getUser(), 0);
                System.out.println(selectedGroup.getUsers());
                if(newGroup) {
                    System.out.println("New group");
                } else {
                    System.out.println("Users in updated group");
                }

                data.add(selectedGroup);
                Message message = new Message(command, model.getUser(), data);
                model.handleTask(message);
                newGroup = false;
                selectedGroup = null;
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
     *
     * @param view button pressed.
     * @return
     */
    public boolean removeMemberFromGroup(View view) {
        if (adapter.getCount() > 1) {
            System.out.println(selectedMemberIndex);
            selectedGroup.getUsers().remove(selectedMemberIndex);
            adapter.notifyDataSetChanged();
            return true;
        } else {
            Toast.makeText(this, "Varje grupp måste ha minst en användare. Om du vill " +
                            "radera gruppen kan du göra det i menyn.",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    /**
     * Method to handle search events.
     * puts activity in a searching state in wait for reply.
     *
     * @param view Button clicked.
     * @return
     */
    public String searchForMember(View view) {
        hideKeyboard();
        String searchString = ((EditText) findViewById(R.id.edit_group_memberSearchText)).getText().toString();
        if (!searchString.equals("")) {
            if (!selectedGroup.getUsers().contains(new User(searchString))) {
                findViewById(R.id.edit_group_searchMemberButton).setVisibility(View.INVISIBLE);
                Model model = Model.getInstance(getFilesDir(),this);
                User user = new User(searchString, "");
                ArrayList<Transferable> data = new ArrayList<>();
                data.add(user);
                Message message = new Message(NetCommands.searchForUser, model.getUser(), data);
                model.handleTask(message);
                return "Found user searched for";
            } else {
                doToast("Den här användaren finns redan i gruppen");
                return "User found but already in the group";
            }
        } else {
            Toast.makeText(this, "Du har inte fyllt i något användarnamn", Toast.LENGTH_SHORT).show();
            return "Username not entered";
        }

    }

    /**
     * Method to return activity to searchable state. Invoked by a clock on the cancel button.
     *
     * @param view button clicked.
     * @return
     */
    public String cancelFoundMember(View view) {
        lastSearchedUser = null;
        findViewById(R.id.edit_group_memberSearchCancelButton).setVisibility(View.INVISIBLE);
        findViewById(R.id.edit_group_addMemberButton).setVisibility(View.INVISIBLE);
        findViewById(R.id.edit_group_searchMemberButton).setVisibility(View.VISIBLE);
        findViewById(R.id.edit_group_memberSearchText).setFocusable(true);
        findViewById(R.id.edit_group_memberSearchText).setFocusableInTouchMode(true);
        ((EditText) findViewById(R.id.edit_group_memberSearchText)).setText("");
        return "Reset complete";
    }

    /**
     * Method to handle clicks on add button.
     *
     * @param view
     */
    public void addMember(View view) {
        selectedGroup.addMember(lastSearchedUser);
        adapter.notifyDataSetChanged();
        memberAddedNotification();     //@Author Johan, Måns
        cancelFoundMember(null);
    }

    /**
     * @Author Johan, Måns
     * Sends a notification to the server indicating that a new member has been added to a group.
     *
     * This method creates a `NetCommands.notificationSent` message and
     * populates it with the current user and the selected group along with its updated members.
     * The message is then sent to the server via the handleTask method.
     *
     * @return The message containing the notification information, including the updated group and its members.
     */
    public Message memberAddedNotification() {
        NetCommands netCommands = NetCommands.notificationSent;
        Model model = Model.getInstance(getFilesDir(),this);

        ArrayList<Transferable> data = new ArrayList<>();
        for (int i = 0; i < selectedGroup.size(); i++) {
            if (selectedGroup.getUsers().get(i) != lastSearchedUser) {
                data.add(selectedGroup.getUsers().get(i));
            }
        }
        data.add(selectedGroup);
        Message message = new Message(netCommands, model.getUser(), data);
        model.handleTask(message);
        return message;
    }
    public void hideKeyboard() {
        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);

        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);

    }
}
