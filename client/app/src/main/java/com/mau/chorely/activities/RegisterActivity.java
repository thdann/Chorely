package com.mau.chorely.activities;


import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.mau.chorely.R;

public class RegisterActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
    }


    public void register(View view) {
        EditText user = findViewById(R.id.usernameField);
        EditText pass = findViewById(R.id.password);

        String username = user.getText().toString();
        String password = pass.getText().toString();

        System.out.println("Do something with the network");
        System.out.println("Username: " + username + " Password: " + password);

//        User user = new User(username, password);
//
//        NetConnection connection = NetConnection.getInstance();


    }


}
