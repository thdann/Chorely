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

import shared.transferable.Message;
import shared.transferable.NetCommands;
import shared.transferable.Reward;
import shared.transferable.Transferable;

/**
 * Creates a new Reward
 *
 * @author Emma Svensson
 * v. 1.0 2020-04-28
 */
public class CreateRewardActivity extends AppCompatActivity implements UpdatableActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_reward);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_create_reward, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.create_reward_menu_saveChanges) {

            if (controlTextFields()) {
                Message msg = createMessageNewReward();
                Model model = BridgeInstances.getModel(getFilesDir());
                model.handleTask(msg);
                finish();
            }

        }

        return super.onOptionsItemSelected(item);
    }

    public Reward createNewReward() {
        String name = ((EditText) findViewById(R.id.activity_register_editText_nameReward)).getText().toString();
        String description = ((EditText) findViewById(R.id.activity_register_editText_descriptionReward)).getText().toString();
        int points = 0;
        try {
            points = Integer.parseInt(((EditText) findViewById(R.id.activity_register_editText_pointsReward)).getText().toString());

        } catch (NumberFormatException e) {
            doToast("Du måste fylla i poäng med siffor");
        }

        Reward reward = new Reward(name, points, description);
        return reward;
    }

    public Message createMessageNewReward() {
        Model model = BridgeInstances.getModel(getFilesDir());
        ArrayList<Transferable> data = new ArrayList<>();
        data.add(createNewReward());
        Message message = new Message(NetCommands.addNewReward, model.getUser(), data);
        return message;
    }

    public boolean controlTextFields() {
        String nameField = ((EditText) findViewById(R.id.activity_register_editText_nameReward)).getText().toString();
        String descriptionField = ((EditText) findViewById(R.id.activity_register_editText_descriptionReward)).getText().toString();
        String pointsField = ((EditText) findViewById(R.id.activity_register_editText_pointsReward)).getText().toString();

        if (nameField == "") {
            doToast("Du måste fylla i namn på belöningen");
            return false;
        } else if (descriptionField == "") {
            doToast("Du måste fylla i en beskrivning av belöningen");
            return false;
        } else if (pointsField == "") {
            doToast("Du måste fylla i antal poäng belöningen kostar");
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
                Toast.makeText(CreateRewardActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });

    }

}
