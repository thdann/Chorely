package com.mau.chorely.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.mau.chorely.R;
import com.mau.chorely.activities.interfaces.UpdatableActivity;
import com.mau.chorely.activities.utils.BridgeInstances;
import com.mau.chorely.model.Model;

import java.util.ArrayList;

import shared.transferable.Chore;
import shared.transferable.Message;
import shared.transferable.NetCommands;
import shared.transferable.Transferable;

/**
 * Creates a new Chore
 *
 * @author Theresa Dannberg
 * v. 1.0 2020-04-23
 */
public class CreateChoreActivity extends AppCompatActivity implements UpdatableActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_chore);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_create_chore, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.create_chore_menu_saveChanges) {

            if (controlTextFields()) {
                Message msg = createMessageNewChore();
                Model model = BridgeInstances.getModel(getFilesDir());
                model.handleTask(msg);
                finish();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Creates a new Chore-object from the user-input
     */
    public Chore createNewChore() {
        String name = ((EditText) findViewById(R.id.activity_register_editText_nameChore)).getText().toString();
        String description = ((EditText) findViewById(R.id.activity_register_editText_descriptionChore)).getText().toString();
        int points = 0;
        try {
            points = Integer.parseInt(((EditText) findViewById(R.id.activity_register_editText_setPointsChore)).getText().toString());
        } catch (NumberFormatException e) {
            doToast("Du måste fylla i poäng med siffror");
        }

        Chore chore = new Chore(name, points, description);
        return chore;
    }

    public Message createMessageNewChore() {
        Model model = BridgeInstances.getModel(getFilesDir());
        ArrayList<Transferable> data = new ArrayList<>();
        data.add(createNewChore());
        Message message = new Message(NetCommands.addNewChore, model.getUser(), data);
        return message;
    }

    public boolean controlTextFields() {
        String nameField = ((EditText) findViewById(R.id.activity_register_editText_nameChore)).getText().toString();
        String descriptionField = ((EditText) findViewById(R.id.activity_register_editText_descriptionChore)).getText().toString();
        String pointsField = ((EditText) findViewById(R.id.activity_register_editText_setPointsChore)).getText().toString();

        if (nameField == "") {
            doToast("Du måste fylla i namn");
            return false;
        } else if (descriptionField == "") {
            doToast("Du måste fylla i beskrivning");
            return false;
        } else if (pointsField == "") {
            doToast("Du måste fylla i poäng");
            return false;
        }
        return true;
    }

    @Override
    public void updateActivity() {

    }

    @Override
    public void doToast(final String message) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(CreateChoreActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });

    }
}
