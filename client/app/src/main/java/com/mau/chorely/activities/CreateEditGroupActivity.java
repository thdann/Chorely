package com.mau.chorely.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.mau.chorely.R;
import com.mau.chorely.activities.utils.BridgeInstances;
import com.mau.chorely.activities.utils.SpinnerAdapterMembers;
import com.mau.chorely.model.Model;

import java.util.ArrayList;

import shared.transferable.Group;
import shared.transferable.Message;
import shared.transferable.NetCommands;
import shared.transferable.Transferable;
import shared.transferable.User;

public class CreateEditGroupActivity extends AppCompatActivity {
    Group selectedGroup;
    SpinnerAdapterMembers spinnerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_edit_group);


    }

    @Override
    protected void onStart() {
        super.onStart();
        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            selectedGroup = (Group) bundle.get("SELECTED_GROUP");
        }
        initActivity();
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

            EditText groupDescription = (EditText)findViewById(R.id.edit_group_edit_description_text);
            groupDescription.setText(selectedGroup.getDescription());
            groupDescription.setFocusable(false);
            if (spinnerAdapter == null) {
                initSpinner();
            }
        }
    }


    public void removeMemberFromGroup(View view){

        // TODO: 2020-04-12 POPUP DIALOG ask for confirmation
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





}
