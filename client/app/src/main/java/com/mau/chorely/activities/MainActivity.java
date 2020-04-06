package com.mau.chorely.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.mau.chorely.R;
import com.mau.chorely.model.Model;
import com.mau.chorely.model.ModelInstances;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //ModelInstances.getInstance();

    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    public void register(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);

        startActivity(intent);
    }

    public void login(View view) {
    }



}
