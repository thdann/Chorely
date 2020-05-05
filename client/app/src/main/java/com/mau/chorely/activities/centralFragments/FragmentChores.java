package com.mau.chorely.activities.centralFragments;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
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
import com.mau.chorely.model.Model;


import java.util.ArrayList;

import shared.transferable.Chore;
import shared.transferable.Group;
import shared.transferable.Message;
import shared.transferable.NetCommands;
import shared.transferable.Transferable;
import shared.transferable.User;

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
    private int selectedItem;


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
        // Create references to buttons in view
        FloatingActionButton createButton = view.findViewById(R.id.fragment_chores_createNewChoreButton);
        FloatingActionButton claimChoreButton = view.findViewById(R.id.fragment_chores_claimChoreButton);
        FloatingActionButton editChoreButton = view.findViewById(R.id.fragment_chores_editChoreButton);
        // Adding listener
        createButton.setOnClickListener(this);
        claimChoreButton.setOnClickListener(this);
        editChoreButton.setOnClickListener(this);
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

                selectedItem = position;
                View view = recyclerView.getChildAt(position);
                view.setBackgroundColor(getResources().getColor(R.color.backgroundLight));
                view.setSelected(true);

                getView().findViewById(R.id.fragment_chores_claimChoreButton).setVisibility(View.VISIBLE);
                getView().findViewById(R.id.fragment_chores_editChoreButton).setVisibility(View.VISIBLE);

            }
        });
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.fragment_chores_createNewChoreButton) {
            Intent intent = new Intent(getContext(), CreateChoreActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.fragment_chores_claimChoreButton) {

            int points = Integer.parseInt(itemList.get(selectedItem).getPoints());
            // Uppdatera poängen för användaren i selected goup:
            Model model = BridgeInstances.getModel(getActivity().getFilesDir());
            Group group = model.getSelectedGroup();
            User currentUser = model.getUser();
            ArrayList<Transferable> data = new ArrayList<>();
            data.add(group);
            group.modifyUserPoints(model.getUser(), points);
            Message message = new Message(NetCommands.clientInternalGroupUpdate, currentUser, data);
            model.handleTask(message);

        } else if (v.getId() == R.id.fragment_chores_editChoreButton) {
            Intent intent = new Intent(getContext(), CreateChoreActivity.class);
            intent.putExtra("chore", itemList.get(selectedItem).getChore());
            startActivity(intent);
        }
    }
}
