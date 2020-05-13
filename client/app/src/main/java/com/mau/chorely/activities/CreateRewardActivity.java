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

import shared.transferable.Group;
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
    private Group selectedGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_reward);
        selectedGroup = Model.getInstance(getFilesDir()).getSelectedGroup();
        setTitle(selectedGroup.getName());
    }

    @Override
    protected void onStart() {
        super.onStart();
        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            Reward reward = (Reward) bundle.get("reward");
            ((EditText) (findViewById(R.id.activity_register_editText_nameReward))).setText(reward.getName());
            ((EditText) (findViewById(R.id.activity_register_editText_descriptionReward))).setText(reward.getDescription());
            ((EditText) (findViewById(R.id.activity_register_editText_pointsReward))).setText("" + reward.getRewardPrice());
        }
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
                finish();
            }
        }

        return super.onOptionsItemSelected(item);
    }


  /*  public Reward createNewReward() {
        String name = ((EditText) findViewById(R.id.activity_register_editText_nameReward)).getText().toString();
        String description = ((EditText) findViewById(R.id.activity_register_editText_descriptionReward)).getText().toString();
        int points = Integer.parseInt(((EditText) findViewById(R.id.activity_register_editText_pointsReward)).getText().toString());
        Reward reward = new Reward(name, points, description);
        return reward;
    } */

    public Reward createNewReward(String name, String desc, int points) {
        Reward reward = new Reward(name, points, desc);
        return reward;
    }

    public boolean createMessageNewReward(Reward newReward) {
        Model model = Model.getInstance(getFilesDir());
        ArrayList<Transferable> data = new ArrayList<>();
        data.add(newReward);
        Message message = new Message(NetCommands.addNewReward, model.getUser(), data);
        model.handleTask(message);
        return true;
    }

    public boolean controlTextFields() {
        String nameField = ((EditText) findViewById(R.id.activity_register_editText_nameReward)).getText().toString();
        String descriptionField = ((EditText) findViewById(R.id.activity_register_editText_descriptionReward)).getText().toString();
        String pointsField = ((EditText) findViewById(R.id.activity_register_editText_pointsReward)).getText().toString();

        if (nameField.equals("")) {
            doToast("Du måste fylla i namn på belöningen");
            return false;
        } else if (descriptionField.equals("")) {
            doToast("Du måste fylla i en beskrivning av belöningen");
            return false;
        } else if (pointsField.equals("")) {
            doToast("Du måste fylla i antal poäng belöningen kostar");
            return false;
        } else
            try {
                int points = Integer.parseInt(pointsField);
                createMessageNewReward(createNewReward(nameField, descriptionField, points));
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
                Toast.makeText(CreateRewardActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });

    }

}
