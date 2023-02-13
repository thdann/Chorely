package com.mau.chorely.activities;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.mau.chorely.R;
import com.mau.chorely.activities.interfaces.UpdatableActivity;
import com.mau.chorely.activities.utils.Presenter;
import com.mau.chorely.model.Model;

import java.util.ArrayList;

import pl.droidsonroids.gif.GifImageView;
import shared.transferable.Message;
import shared.transferable.NetCommands;
import shared.transferable.Transferable;
import shared.transferable.User;

/**
 * This is the activity to handle registrations of users.
 * The activity then sends the user to the next activity provided that the registration was successful.
 *
 * @author Timothy Denison, Fredrik Jeppson
 */
public class RegisterActivity extends AppCompatActivity implements UpdatableActivity {
    GifImageView gifImageViewWorking;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_register);

        gifImageViewWorking = findViewById(R.id.gifImageViewWorking);
        gifImageViewWorking.setVisibility(GifImageView.INVISIBLE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Presenter.getInstance().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Presenter.getInstance().deregisterForUpdates(this);
    }

    /**
     * Method to handle  clicks to register button.
     *
     * @param view Button clicked.
     */
    public void register(View view) {
        EditText user = findViewById(R.id.username);
        EditText pass = findViewById(R.id.password);
        String username = user.getText().toString();
        String password = pass.getText().toString();
        User userToRegister = new User(username, password);

        if (!username.equals("") && !password.equals("")) {
            Message registerMsg = new Message(NetCommands.registerUser, userToRegister, new ArrayList<Transferable>());
            Model.getInstance(getFilesDir(),this).handleTask(registerMsg);
            user.setVisibility(View.INVISIBLE);
            pass.setVisibility(View.INVISIBLE);
            Button buttonRegister = findViewById(R.id.register);
            buttonRegister.setVisibility(Button.INVISIBLE);
            gifImageViewWorking.setVisibility(View.VISIBLE);
        } else {
        doToast("Fyll i både användarnamn och lösenord");
        }
    }

    /**
     * Interface method to update activity.
     */
    @Override
    public void updateActivity() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (Model.getInstance(getFilesDir(),getApplicationContext()).isConnected()) {
                    if (Model.getInstance(getFilesDir(),getApplicationContext()).isLoggedIn()) {
                        Intent intent = new Intent(RegisterActivity.this, ManageGroupsActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        findViewById(R.id.username).setVisibility(View.VISIBLE);
                        findViewById(R.id.password).setVisibility(View.VISIBLE);
                        findViewById(R.id.register).setVisibility(View.VISIBLE);
                        gifImageViewWorking.setVisibility(View.INVISIBLE);
                        ((EditText) findViewById(R.id.username)).setText("");
                        ((EditText) findViewById(R.id.password)).setText("");
                    }
                } else {
                    Intent intent = new Intent(getApplicationContext(), ConnectActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    /**
     * Interface method to toast user.
     *
     * @param message Message to toast.
     */
    @Override
    public void doToast(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }
}
