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
import com.mau.chorely.model.Model;

import java.util.ArrayList;

import shared.transferable.Chore;
import shared.transferable.Group;
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
    private Group selectedGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_chore);
        selectedGroup = Model.getInstance(getFilesDir()).getSelectedGroup();
        setTitle(selectedGroup.getName());
    }

    @Override
    protected void onStart() {
        super.onStart();
        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            Chore chore = (Chore) bundle.get("chore");
            ((EditText) (findViewById(R.id.activity_register_editText_nameChore))).setText(chore.getName());
            findViewById(R.id.activity_register_editText_nameChore).setBackground(getDrawable(R.color.background));
            findViewById(R.id.activity_register_editText_nameChore).setFocusable(false);
            findViewById(R.id.activity_register_editText_nameChore).setFocusableInTouchMode(false);

            ((EditText) (findViewById(R.id.activity_register_editText_descriptionChore))).setText(chore.getDescription());
            ((EditText) (findViewById(R.id.activity_register_editText_setPointsChore))).setText("" + chore.getScore());
        }
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
                finish();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Creates a new Chore-object from the user-input
     */
    public Chore createNewChore(String name, String desc, int points) {
        Chore chore = new Chore(name, points, desc);
        return chore;
    }

    public boolean createMessageNewChore(Chore newChore) {
        Model model = Model.getInstance(getFilesDir());
        ArrayList<Transferable> data = new ArrayList<>();
        data.add(newChore);
        Message message = new Message(NetCommands.addNewChore, model.getUser(), data);
        model.handleTask(message);
        return true;
    }

    public boolean controlTextFields() {
        String nameField = ((EditText) findViewById(R.id.activity_register_editText_nameChore)).getText().toString();
        String descriptionField = ((EditText) findViewById(R.id.activity_register_editText_descriptionChore)).getText().toString();
        String pointsField = ((EditText) findViewById(R.id.activity_register_editText_setPointsChore)).getText().toString();

        if (nameField.equals("")) {
            doToast("Du måste fylla i namn");
            return false;
        } else if (descriptionField.equals("")) {
            doToast("Du måste fylla i beskrivning");
            return false;
        } else if (pointsField.equals("")) {
            doToast("Du måste fylla i poäng");
            return false;
        } else
            try {
                int points = Integer.parseInt(pointsField);
                createMessageNewChore(createNewChore(nameField, descriptionField, points));
                return true;
            } catch (NumberFormatException e) {
                doToast("Du måste fylla i poäng med siffor");
                return false;
            }
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
