package com.mau.chorely.activities.centralFragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mau.chorely.R;
import com.mau.chorely.activities.CentralActivity;
import com.mau.chorely.activities.CreateRewardActivity;
import com.mau.chorely.activities.centralFragments.utils.CentralActivityRecyclerViewAdapter;
import com.mau.chorely.activities.centralFragments.utils.ListItemCentral;
import com.mau.chorely.activities.utils.BridgeInstances;

import java.nio.file.FileSystemNotFoundException;
import java.util.ArrayList;

import shared.transferable.Reward;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentRewards#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentRewards extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static ArrayList<ListItemCentral> itemList = new ArrayList<>();
    private RecyclerView recyclerView;
    private static CentralActivityRecyclerViewAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    public FragmentRewards() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment FragmentRewards.
     */
    public static FragmentRewards newInstance(ArrayList<Reward> rewards) {
        FragmentRewards fragment = new FragmentRewards();
        Bundle args = new Bundle();
        args.putSerializable("REWARDS", rewards);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            ArrayList<Reward> rewards = (ArrayList<Reward>) getArguments().getSerializable("REWARDS");
            updateList(rewards);
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rewards, container, false);
        recyclerView = view.findViewById(R.id.fragment_rewards_recyclerView);
        FloatingActionButton createButton = (FloatingActionButton) view.findViewById(R.id.fragment_rewards_addNewRewardButton);
        createButton.setOnClickListener(this);
        buildRecyclerView();
        System.out.println("VIEW CREATED!!!!!!!!!");
        return view;
    }

    public static void updateList(ArrayList<Reward> rewards){
        for(Reward reward : rewards){
            boolean foundItem = false;
            for(int i = 0; i < itemList.size(); i++){
                if(itemList.get(i).equals(reward)){
                    foundItem = true;
                    if(!itemList.get(i).allIsEqual(reward)){
                        itemList.get(i).updateItem(reward);
                    }
                }
            }
            if(!foundItem){
                itemList.add(new ListItemCentral(reward));
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void buildRecyclerView() {
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        adapter = new CentralActivityRecyclerViewAdapter(itemList);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        adapter.setOnclickListener(new CentralActivityRecyclerViewAdapter.OnitemClickListener() {
            @Override
            public void onItemClick(int position) {
                // TODO: 2020-04-23 gå till aktiviteten för ändring av chore.
            }
        });
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(getContext(), CreateRewardActivity.class);
        startActivity(intent);
    }
}
