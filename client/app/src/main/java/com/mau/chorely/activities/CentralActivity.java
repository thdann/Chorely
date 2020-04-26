package com.mau.chorely.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;
import com.mau.chorely.R;
import com.mau.chorely.activities.centralFragments.FragmentChores;
import com.mau.chorely.activities.centralFragments.FragmentRewards;
import com.mau.chorely.activities.utils.SectionsPageAdapter;

import java.util.ArrayList;

import shared.transferable.Chore;
import shared.transferable.Reward;

import static androidx.fragment.app.FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT;

public class CentralActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_central);

        ViewPager viewPager = findViewById(R.id.viewPager);
        setupViewPager(viewPager);
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setupViewPager(ViewPager viewPager) {
        SectionsPageAdapter adapter = new SectionsPageAdapter(getSupportFragmentManager(),
                BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);

        // Test data to make it compile.
        ArrayList<Chore> chores = new ArrayList<>();
        ArrayList<Reward> rewards = new ArrayList<>();
        chores.add(new Chore("test chore 1", 123));
        chores.add(new Chore("test chore 2", 456));
        rewards.add(new Reward("test reward 1", 23));
        rewards.add(new Reward("test reward 2", 45));

        adapter.addFragment(FragmentChores.newInstance(chores), "Chores");
        adapter.addFragment(FragmentRewards.newInstance(rewards), "Rewards");
        viewPager.setAdapter(adapter);
    }
}
