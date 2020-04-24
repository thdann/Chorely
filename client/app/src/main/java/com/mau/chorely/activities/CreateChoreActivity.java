package com.mau.chorely.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.mau.chorely.R;

import shared.transferable.Chore;

/**
 * Creates a new Chore
 * @author Theresa Dannberg
 * v. 1.0 2020-04-23
 */

public class CreateChoreActivity extends AppCompatActivity {

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
            //Skriv vad som händer när man klickar på knappen med disketten
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Creates a new Chore-object from the user-input
     */

    public void createNewChore() {

        String name = ((EditText) findViewById(R.id.activity_register_editText_nameChore)).getText().toString();
        String description = ((EditText) findViewById(R.id.activity_register_editText_descriptionChore)).getText().toString();
        int points = Integer.parseInt(((EditText) findViewById(R.id.activity_register_editText_setPointsChore)).getText().toString());
        Chore chore = new Chore(name, points, description);

    }


}
