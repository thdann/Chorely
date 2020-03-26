package com.mau.chorely.activities;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.mau.chorely.R;
import com.mau.chorely.model.Model;
import com.mau.chorely.model.ModelInstances;
import com.mau.chorely.model.transferrable.NetCommands;
import com.mau.chorely.model.transferrable.Transferable;
import com.mau.chorely.model.transferrable.User;

import java.util.ArrayList;

public class RegisterActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
    }


    public void register(View view) {
        EditText user = findViewById(R.id.username);
        EditText pass = findViewById(R.id.password);
        String username = user.getText().toString();
        String password = pass.getText().toString();
        User userToRegister = new User(username, password);
        System.out.println(userToRegister);

        new RegisterUser().execute(userToRegister);
    }

    private class RegisterUser extends AsyncTask<User, Void, NetCommands> {

        @Override
        protected NetCommands doInBackground(User... users) {
            User user = users[0];
            System.out.println(user);

            ArrayList<Transferable> registration = new ArrayList<>();
            registration.add(NetCommands.register);
            registration.add(user);

//            Model model = ModelInstances.getInstance();
//            NetCommands command = model.notifyForResult(registration);

            return NetCommands.registrationOk;
        }

        protected void onPostExecute(NetCommands command) {
            switch (command) {
                case internalClientError:
                    System.out.println("internal client error.");
                    break;
                case registrationOk:
                    System.out.println("registration ok!!!");
                    Intent intent = new Intent(RegisterActivity.this, CreateGroupActivity.class);
                    startActivity(intent);
                    break;
                case registrationDenied:
                    System.out.println("registration denied");
                    break;
                default:
                    throw new RuntimeException("Shouldn't get here. Very bad!");

            }
        }
    }


}