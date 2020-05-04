package com.mau.chorely.activities.centralFragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mau.chorely.R;
import com.mau.chorely.activities.CentralActivity;
import com.mau.chorely.activities.CreateChoreActivity;
import com.mau.chorely.activities.centralFragments.utils.*;
import com.mau.chorely.activities.utils.BridgeInstances;


import java.util.ArrayList;

import shared.transferable.Chore;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentChores#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentChores extends Fragment implements View.OnClickListener {
    private static ArrayList<ListItemCentral> itemList = new ArrayList<>();
    private RecyclerView recyclerView;
    private static CentralActivityRecyclerViewAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    private ArrayList<Chore> listOfChores = new ArrayList<>();

    public FragmentChores() {
        // Required empty public constructor
    }


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment FragmentChores.
     */
    public static FragmentChores newInstance(ArrayList<Chore> chores) {
        FragmentChores fragment = new FragmentChores();
        Bundle args = new Bundle();
        args.putSerializable("CHORES", chores);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            ArrayList<Chore> chores = (ArrayList<Chore>) getArguments().getSerializable("CHORES");
            validateAndUpdateListData(chores);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chores, container, false);
        recyclerView = view.findViewById(R.id.fragment_chores_recyclerView);
        FloatingActionButton createButton = view.findViewById(R.id.fragment_chores_createNewChoreButton);
        createButton.setOnClickListener(this);
        buildRecyclerView();
        return view;
    }

    private static void validateAndUpdateListData(ArrayList<Chore> chores) {
        for (Chore chore : chores) {
            boolean foundItem = false;
            for (int i = 0; i < itemList.size(); i++) {
                if (itemList.get(i).equals(chore)) {
                    foundItem = true;
                    if (!itemList.get(i).allIsEqual(chore)) {
                        itemList.get(i).updateItem(chore);
                    }
                }
            }
            if (!foundItem) {
                itemList.add(new ListItemCentral(chore));
            }
        }
    }

    public static void updateList(ArrayList<Chore> chores) {
        validateAndUpdateListData(chores);
        adapter.notifyDataSetChanged();
    }

    private void buildRecyclerView() {
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        adapter = new CentralActivityRecyclerViewAdapter(itemList);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        adapter.setOnclickListener(new CentralActivityRecyclerViewAdapter.OnitemClickListener() {
            @Override
            public void onItemClick(int position) {
                // TODO: 2020-04-23 gå till aktiviteten för ändring av chore.
                Intent intent = new Intent(getContext(), CreateChoreActivity.class);
                intent.putExtra("chore", listOfChores.get(position));
                startActivity(intent);

            }
        });
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(getContext(), CreateChoreActivity.class);
        startActivity(intent);
    }
}
