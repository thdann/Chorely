package com.mau.chorely.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.mau.chorely.R;
import com.mau.chorely.activities.centralFragments.FragmentChores;
import com.mau.chorely.activities.centralFragments.FragmentRewards;
import com.mau.chorely.activities.interfaces.UpdatableActivity;
import com.mau.chorely.activities.utils.BridgeInstances;
import com.mau.chorely.activities.utils.SectionsPageAdapter;

import java.util.ArrayList;

import shared.transferable.Chore;
import shared.transferable.Group;
import shared.transferable.Reward;

import static androidx.fragment.app.FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT;

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_central);


        ViewPager viewPager = findViewById(R.id.viewPager);
        setupViewPager(viewPager);
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);
        selectedGroup = BridgeInstances.getModel(getFilesDir()).getSelectedGroup();
        setTitle(selectedGroup.getName());
    }

    @Override
    protected void onStart() {
        super.onStart();

        BridgeInstances.getPresenter().register(this);
        System.out.println("CENTRAL REGISTRED FOR UPDATES");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BridgeInstances.getPresenter().deregisterForUpdates(this);
    }

    @Override
    public void updateActivity() {
        final Group updatedGroup = BridgeInstances.getModel(getFilesDir()).getSelectedGroup();
        System.out.println("UPDATING FRAGMENTS OUTSIDE IF");
        if(!selectedGroup.allIsEqual(updatedGroup)){
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


        // Test data to make it compile.
        ArrayList<Chore> chores = BridgeInstances.getModel(getFilesDir()).getSelectedGroup().getChores();
        ArrayList<Reward> rewards = BridgeInstances.getModel(getFilesDir()).getSelectedGroup().getRewards();

        adapter.addFragment(FragmentChores.newInstance(chores), "Sysslor");
        adapter.addFragment(FragmentRewards.newInstance(rewards), "Belöningar");
        viewPager.setAdapter(adapter);
    }
}
