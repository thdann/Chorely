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
import com.mau.chorely.model.Model;

import java.nio.file.FileSystemNotFoundException;
import java.util.ArrayList;

import shared.transferable.Chore;
import shared.transferable.Group;
import shared.transferable.Reward;
import shared.transferable.Transferable;
import shared.transferable.User;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentRewards#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentRewards extends Fragment implements View.OnClickListener {
    private static ArrayList<ListItemCentral> itemList = new ArrayList<>();
    private RecyclerView recyclerView;
    private static CentralActivityRecyclerViewAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private int selectedItem;

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
            validateAndUpdateListData(rewards);
        }


    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rewards, container, false);
        recyclerView = view.findViewById(R.id.fragment_rewards_recyclerView);
        FloatingActionButton createButton = (FloatingActionButton) view.findViewById(R.id.fragment_rewards_addNewRewardButton);
        FloatingActionButton buyRewardButton = view.findViewById(R.id.fragment_reward_buyRewardButton);
        FloatingActionButton editRewardButton = view.findViewById(R.id.fragment_reward_changeRewardButton);
        createButton.setOnClickListener(this);
        buyRewardButton.setOnClickListener(this);
        editRewardButton.setOnClickListener(this);
        buildRecyclerView();

        return view;
    }


    private static void validateAndUpdateListData(ArrayList<Reward> rewards){
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
    }

    public static void updateList(ArrayList<Reward> rewards){
        validateAndUpdateListData(rewards);
        adapter.notifyDataSetChanged();
    }
    //TODO: Dubbelkolla gärna att OK, var ej helt med på vad jag gjorde utan utgick från FragmentChores för nedan 2 metoder
    private void buildRecyclerView() {
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        adapter = new CentralActivityRecyclerViewAdapter(itemList);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        adapter.setOnclickListener(new CentralActivityRecyclerViewAdapter.OnitemClickListener() {
            @Override
            public void onItemClick(int position) {
                selectedItem = position;
                View selectedView = recyclerView.getChildAt(position);

                for (int i = 0; i < itemList.size(); i++) {
                    if (i == selectedItem) {
                        selectedView.findViewById(R.id.central_list_layout).setBackgroundColor(getResources().getColor(R.color.backgroundLight));
                    } else {
                        View unselectedView = recyclerView.getChildAt(i);
                        unselectedView.findViewById(R.id.central_list_layout).setBackgroundColor(getResources().getColor(R.color.background));
                    }
                }

                System.out.println(selectedView.toString());
                System.out.println(selectedView.getRootView().toString());

                getView().findViewById(R.id.fragment_reward_buyRewardButton).setVisibility(View.VISIBLE);
                getView().findViewById(R.id.fragment_reward_changeRewardButton).setVisibility(View.VISIBLE);
            }
        });
    }


    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.fragment_rewards_addNewRewardButton) {
            Intent intent = new Intent(getContext(), CreateRewardActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.fragment_reward_buyRewardButton) {

            int points = Integer.parseInt(itemList.get(selectedItem).getPoints());
            // Uppdatera poängen för användaren i selected group:
            Model model = BridgeInstances.getModel(getActivity().getFilesDir());
            Group group = model.getSelectedGroup();
            User currentUser = model.getUser();
            ArrayList<Transferable> data = new ArrayList<>();
            data.add(group);
//            group.modifyUserPoints(model.getUser(), points);
//            Message message = new Message(NetCommands.clientInternalGroupUpdate, currentUser, data);
//            model.handleTask(message);

        } else if (v.getId() == R.id.fragment_reward_changeRewardButton) {
            Intent intent = new Intent(getContext(), CreateRewardActivity.class);
            intent.putExtra("reward", itemList.get(selectedItem).getReward());
            startActivity(intent);
        }
    }

}
