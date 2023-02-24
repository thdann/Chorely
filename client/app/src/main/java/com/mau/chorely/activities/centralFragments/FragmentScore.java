package com.mau.chorely.activities.centralFragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mau.chorely.R;
import com.mau.chorely.activities.centralFragments.utils.CentralActivityRecyclerViewAdapter;
import com.mau.chorely.activities.centralFragments.utils.ListItemCentral;
import com.mau.chorely.model.Model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import shared.transferable.Group;
import shared.transferable.User;

/**
 * Fragment containing the scoreboard.
 * @author Timothy Denison, Angelica Asplund.
 */
public class FragmentScore extends Fragment {
    private static HashMap<User, Integer> scoreMap;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private static ArrayList<ListItemCentral> itemList = new ArrayList<>();
    private static CentralActivityRecyclerViewAdapter adapter = new CentralActivityRecyclerViewAdapter(itemList);

    public static Fragment newInstance(HashMap<User, Integer> scoreMap, Group selectedGroup) {
        FragmentScore fragment = new FragmentScore();
        Bundle args = new Bundle();
        args.putSerializable("SCORE", scoreMap);
        args.putSerializable("SELECTED GROUP", selectedGroup);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        itemList = new ArrayList<>();
        if (getArguments() != null) {
            scoreMap = (HashMap<User, Integer>) getArguments().getSerializable("SCORE");

        }
        initList();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_score, container, false);
        recyclerView = view.findViewById(R.id.fragment_score_recyclerView);
        buildRecyclerView();

        adapter.notifyDataSetChanged();
        return view;


    }


    public void buildRecyclerView() {
        recyclerView.hasFixedSize();
        layoutManager = new LinearLayoutManager(getActivity());
        adapter = new CentralActivityRecyclerViewAdapter(itemList, R.layout.scoreboard_item);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    /**
     * Method to validate and update the list of scores in the group.
     *
     * @param newMap new hashmap of scores and users.
     */
    public static void updateList(HashMap<User, Integer> newMap) {
        System.out.println("UPDATING SCOREBOARD OUTSIDE IF");
        scoreMap = newMap;
        if (adapter != null) {

            System.out.println("UPDATING SCOREBOARD INSIDE IF");
            boolean updated = false;
            boolean foundEntry = false;
            int size = itemList.size();
            for (Map.Entry<User, Integer> entry : scoreMap.entrySet()) {
                for (int i = 0; i < size; i++) {
                    System.out.println(itemList.get(i).getTitle() + " ?= " + entry.getKey().getUsername());
                    if (itemList.get(i).getTitle().equals(entry.getKey().getUsername())) {
                        System.out.println(itemList.get(i).getPointsInt() + " ?= " + entry.getValue());
                        if (itemList.get(i).getPointsInt() != entry.getValue()) {
                            System.out.println("UPDATING SCORE!!!!");
                            itemList.remove(i);
                            itemList.add(new ListItemCentral(entry.getKey().getUsername(), null, entry.getValue()));
                            updated = true;
                        }
                        foundEntry = true;
                        break;
                    }
                }
                if (!foundEntry) {
                    itemList.add(new ListItemCentral(entry.getKey().getUsername(), null, entry.getValue()));
                    updated = true;
                }
            }

            if (scoreMap.size() > itemList.size()) {
                for (int i = 0; i < itemList.size(); i++) {
                    foundEntry = false;
                    for (Map.Entry<User, Integer> entry : scoreMap.entrySet()) {
                        if (entry.getKey().getUsername().equals(itemList.get(i).getTitle())) {
                            foundEntry = true;
                        }
                    }
                    if (!foundEntry) {
                        itemList.remove(i);
                    }
                }
            }

            if (updated) {
                adapter.notifyDataSetChanged();

            }
            System.out.println("UPDATED SCOREBOARD: Size: " + itemList.size());
            adapter.notifyDataSetChanged();
        }
    }

    /**
     * Method to initiate the list.
     */
    private static void initList() {

        for (Map.Entry<User, Integer> entry : scoreMap.entrySet()) {

            User user = entry.getKey();
            int points = entry.getValue();
            System.out.println("ADDED VALUES TO SCOREBOARD: Name:" + user.getUsername() + " Points: " + points);
            ListItemCentral item = new ListItemCentral(user.getUsername(), "", points);
            itemList.add(item);
        }
    }
}
