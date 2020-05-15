package com.mau.chorely.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.mau.chorely.R;
import com.mau.chorely.activities.centralFragments.FragmentChores;
import com.mau.chorely.activities.centralFragments.FragmentRewards;
import com.mau.chorely.activities.interfaces.UpdatableActivity;
import com.mau.chorely.activities.utils.Presenter;
import com.mau.chorely.activities.utils.SectionsPageAdapter;
import com.mau.chorely.model.Model;

import java.util.ArrayList;

import shared.transferable.Chore;
import shared.transferable.Group;
import shared.transferable.Message;
import shared.transferable.NetCommands;
import shared.transferable.Reward;
import shared.transferable.Transferable;
import shared.transferable.User;

import static androidx.fragment.app.FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT;

/**
 * This is the activity to...
 *
 * @author Timothy Denison, Emma Svensson
 */
public class CentralActivity extends AppCompatActivity implements UpdatableActivity {
    private Group selectedGroup;
    SectionsPageAdapter adapter = new SectionsPageAdapter(getSupportFragmentManager(),
            BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_central_activity, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_central_edit) {
            Intent intent = new Intent(this, CreateEditGroupActivity.class);
            intent.putExtra("SELECTED_GROUP", selectedGroup);
            startActivity(intent);
        } else if (item.getItemId() == R.id.logOut) {
            logOut();
        } else if(item.getItemId() == R.id.edit_group_menu_deleteGroup) {
            deleteGroup();
        }
        return super.onOptionsItemSelected(item);
    }

    public void logOut() {
        Model model = Model.getInstance(getFilesDir());

        Message logOutMsg = new Message(NetCommands.logout, model.getUser(), new ArrayList<Transferable>());
        model.handleTask(logOutMsg);
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();

    }


    private void deleteGroup() {
        Model model = Model.getInstance(getFilesDir());
        ArrayList<Transferable> data = new ArrayList<>();
        data.add(model.getSelectedGroup());
        Message deleteGroupMsg = new Message(NetCommands.deleteGroup, model.getUser(), data);
        model.handleTask(deleteGroupMsg);
        finish();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_central);

        ViewPager viewPager = findViewById(R.id.viewPager);
        setupViewPager(viewPager);
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);
        selectedGroup = Model.getInstance(getFilesDir()).getSelectedGroup();
        setTitle(selectedGroup.getName());
    }

    @Override
    protected void onStart() {
        super.onStart();
        Presenter.getInstance().register(this);
        updateActivity();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Presenter.getInstance().deregisterForUpdates(this);
    }

    @Override
    public void updateActivity() {
        final Group updatedGroup = Model.getInstance(getFilesDir()).getSelectedGroup();
        System.out.println("UPDATING FRAGMENTS OUTSIDE IF");
        if (!selectedGroup.allIsEqual(updatedGroup)) {
            selectedGroup = updatedGroup;
            System.out.println("UPDATING FRAGMENT LISTS");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    FragmentChores.updateList(selectedGroup.getChores());
                    FragmentRewards.updateList(selectedGroup.getRewards());

                }
            });
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updateUserPoints();
            }
        });
    }

    @Override
    public void doToast(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(CentralActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void setupViewPager(ViewPager viewPager) {
        ArrayList<Chore> chores = Model.getInstance(getFilesDir()).getSelectedGroup().getChores();
        ArrayList<Reward> rewards = Model.getInstance(getFilesDir()).getSelectedGroup().getRewards();

        adapter.addFragment(FragmentChores.newInstance(chores), "Sysslor");
        adapter.addFragment(FragmentRewards.newInstance(rewards), "Belöningar");
        viewPager.setAdapter(adapter);
    }

    private void updateUserPoints() {
        User user = Model.getInstance(getFilesDir()).getUser();
        int points = selectedGroup.getUserPoints(user);

        ((TextView) findViewById(R.id.nameAndPointsLayout_userName)).setText(user.getUsername());
        ((TextView) findViewById(R.id.nameAndPointsLayout_userPoints)).setText("" + points);

    }
}
