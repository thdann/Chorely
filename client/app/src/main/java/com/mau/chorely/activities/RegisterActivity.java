package com.mau.chorely.activities;


import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.mau.chorely.R;
import com.mau.chorely.activities.interfaces.UpdatableActivity;
import com.mau.chorely.activities.utils.BridgeInstances;

import java.util.ArrayList;

import pl.droidsonroids.gif.GifImageView;
import shared.transferable.Message;
import shared.transferable.NetCommands;
import shared.transferable.Transferable;
import shared.transferable.User;

public class RegisterActivity extends AppCompatActivity implements UpdatableActivity {
    GifImageView gifImageViewWorking;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        setContentView(R.layout.activity_register);

        gifImageViewWorking = findViewById(R.id.gifImageViewWorking);
        gifImageViewWorking.setVisibility(GifImageView.INVISIBLE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        BridgeInstances.getPresenter().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        BridgeInstances.getPresenter().deregisterForUpdates(this);
    }

    public void register(View view) {
        EditText user = findViewById(R.id.username);
        EditText pass = findViewById(R.id.password);
        String username = user.getText().toString();
        String password = pass.getText().toString();
        User userToRegister = new User(username, password);
        System.out.println(userToRegister);

        Message registerMsg = new Message(NetCommands.registerUser, userToRegister, new ArrayList<Transferable>());
        BridgeInstances.getModel().handleTask(registerMsg);

        user.setVisibility(View.INVISIBLE);
        pass.setVisibility(View.INVISIBLE);
        Button buttonRegister = findViewById(R.id.register);
        buttonRegister.setVisibility(Button.INVISIBLE);
        gifImageViewWorking.setVisibility(View.VISIBLE);





    }

    @Override
    public void updateActivity() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(BridgeInstances.getModel().isConnected()){
                    if(BridgeInstances.getModel().isLoggedIn()) {
                        Intent intent = new Intent(RegisterActivity.this, ManageGroupsActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    else {
                        findViewById(R.id.username).setVisibility(View.VISIBLE);
                        findViewById(R.id.password).setVisibility(View.VISIBLE);
                        findViewById(R.id.register).setVisibility(View.VISIBLE);
                        gifImageViewWorking.setVisibility(View.INVISIBLE);
                        ((EditText)findViewById(R.id.username)).setText("");
                        ((EditText)findViewById(R.id.password)).setText("");
                    }
                }
                else{
                    Intent intent = new Intent(getApplicationContext(), ConnectActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    public void doToast(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG);
                toast.show();
            }
        });


    }

//    private class RegisterUser extends AsyncTask<User, Void, NetCommands> {
//
//        @Override
//        protected NetCommands doInBackground(User... users) {
//            User user = users[0];
//            System.out.println(user);
//
//            ArrayList<Transferable> registration = new ArrayList<>();
//            registration.add(NetCommands.register);
//            registration.add(new GenericID());
//            registration.add(user);
//
//            Model model = BridgeInstances.getModel();
//            NetCommands command = model.notifyForResponse(registration);
//
//            return command;
//        }
//
//        protected void onPostExecute(NetCommands command) {
//            switch (command) {
//                case internalClientError:
//                    System.out.println("internal client error.");
//                    System.out.println(BridgeInstances.getModel().getErrorMessage());
//                    break;
//                case registrationOk:
//                    System.out.println("registration ok!!!");
//                    Intent intent = new Intent(RegisterActivity.this, CreateGroupActivity.class);
//                    startActivity(intent);
//                    break;
//                case registrationDenied:
//                    System.out.println("registration denied");
//                    break;
//                default:
//                    throw new RuntimeException("Shouldn't get here. Very bad!");
//
//            }
//        }
//    }


}
