package com.mau.chorely.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.mau.chorely.R;
import com.mau.chorely.activities.interfaces.UpdatableActivity;
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
    /**
     * The method fills the textfields with info of a selected Reward if the user is
     * navigated to the activity from the selection of "Edit Reward" in the Reward fragment- activity
     */
    @Override
    protected void onStart() {
        super.onStart();
        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            Reward reward = (Reward) bundle.get("reward");
            setTitle("Redigera belöning");
            ((EditText) (findViewById(R.id.activity_register_editText_nameReward))).setText(reward.getName());
            ((EditText) (findViewById(R.id.activity_register_editText_descriptionReward))).setText(reward.getDescription());
            ((EditText) (findViewById(R.id.activity_register_editText_pointsReward))).setText("" + reward.getRewardPrice());
        }
    }

    /**
     * Sets the menu to the activity
     * @param menu the menu
     * @return the activity with the menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_create_reward, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Checks which menu item is clicked, in this case only saveChanges
     * @param item the menu item for save changes
     * @return the selection
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        hideKeyboard();
        int id = item.getItemId();
        if (id == R.id.create_reward_menu_saveChanges) {

            if (controlTextFields()) {
                finish();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Creates a new Reward-object from the user-input
     */
    public Reward createNewReward(String name, String desc, int points) {
        Reward reward = new Reward(name, points, desc);
        return reward;
    }

    /**
     * Creates a message containing the new Reward and sends it
     * @param newReward the new reward
     * @return true if executed correctly.
     */
    public boolean createMessageNewReward(Reward newReward) {
        Model model = Model.getInstance(getFilesDir(),this);
        ArrayList<Transferable> data = new ArrayList<>();
        data.add(newReward);
        Message message = new Message(NetCommands.addNewReward, model.getUser(), data);
        model.handleTask(message);
        return true;
    }

    /**
     * The method checks that no textfields are empty and that the points-filed contains
     * integers.
     *
     * @return true if everything is correct, otherwiser false.
     */
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

    /**
     * Shows a toast
     * @param message the text with information to the user
     */
    @Override
    public void doToast(final String message) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(CreateRewardActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });

    }
    public void hideKeyboard() {
        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);

        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);

    }
}
