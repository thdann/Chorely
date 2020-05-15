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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import shared.transferable.User;

public class FragmentScore extends Fragment {
    private static HashMap<User, Integer> scoreMap;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private static CentralActivityRecyclerViewAdapter adapter;
    private static ArrayList<ListItemCentral> itemList = new ArrayList<>();

    public static Fragment newInstance(HashMap<User, Integer> scoreMap) {
        FragmentScore fragment = new FragmentScore();
        Bundle args = new Bundle();
        args.putSerializable("SCORE", scoreMap);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        itemList = new ArrayList<>();
        if (getArguments() != null) {
            scoreMap = (HashMap<User, Integer>) getArguments().getSerializable("SCORE");
            initList();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_score, container, false);
        recyclerView = view.findViewById(R.id.fragment_score_recyclerView);
        buildRecyclerView();
        return view;


    }

    public void buildRecyclerView() {
        recyclerView.hasFixedSize();
        layoutManager = new LinearLayoutManager(getActivity());
        adapter = new CentralActivityRecyclerViewAdapter(itemList, R.layout.scoreboard_item);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    public static void updateList(HashMap<User, Integer> newMap) {
        if (adapter != null) {
            scoreMap = newMap;
            itemList = new ArrayList<>();
            initList();
            adapter.notifyDataSetChanged();
            System.out.println("SCORELIST UPDATED");
        }
    }

    private static void initList() {

        for (Map.Entry<User, Integer> entry : scoreMap.entrySet()) {

            User user = entry.getKey();
            int points = entry.getValue();
            ListItemCentral item = new ListItemCentral(user.getUsername(), "", points);
            itemList.add(item);
        }
    }
}
