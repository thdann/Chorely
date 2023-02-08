package com.mau.chorely.activities.centralFragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Display;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mau.chorely.R;
import com.mau.chorely.activities.CreateChoreActivity;
import com.mau.chorely.activities.centralFragments.utils.*;
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
 * @author Timothy Denison
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

    /**
     * Fills the list with chores if there are any registered
     * @param savedInstanceState TODO: hur beskriva detta?
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        itemList = new ArrayList<>();
        if (getArguments() != null) {
            ArrayList<Chore> chores = (ArrayList<Chore>) getArguments().getSerializable("CHORES");
            validateAndUpdateListData(chores);
        }
    }

    /**
     * Adds components to activity, views, buttons and listeners to buttons
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return the view
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chores, container, false);
        recyclerView = view.findViewById(R.id.fragment_chores_recyclerView);
        // Create references to buttons in view
        FloatingActionButton createButton = view.findViewById(R.id.fragment_chores_createNewChoreButton);
        FloatingActionButton claimChoreButton = view.findViewById(R.id.fragment_chores_claimChoreButton);
        FloatingActionButton editChoreButton = view.findViewById(R.id.fragment_chores_editChoreButton);
        FloatingActionButton deleteChoreButton = view.findViewById(R.id.fragment_chores_deleteChoreButton);
        // Adding listener
        createButton.setOnClickListener(this);
        claimChoreButton.setOnClickListener(this);
        editChoreButton.setOnClickListener(this);
        deleteChoreButton.setOnClickListener(this);
        buildRecyclerView();
        return view;
    }

    /**
     * Checks for duplicates among registered chores and removes them if
     * two chores are exactly the same.
     * @param chores
     */
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

        if (itemList.size() > chores.size()) {
            for (int i = 0; i < itemList.size(); i++) {
                ListItemCentral item = itemList.get(i);
                if (!chores.contains(item)) {
                    itemList.remove(i);

                }
            }
        }
    }

    /**
     * Updates the list of chores if something is changed in a chore
     * @param chores list of chores
     */
    public static void updateList(ArrayList<Chore> chores) {
        validateAndUpdateListData(chores);
        adapter.notifyDataSetChanged();
    }

    /**
     * Makes buttons visible when a chore is selected in the list, and adds listeners to the buttons.
     * Sets backgroundcolor of the selected item in the list.
     */
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
                        selectedView.findViewById(R.id.central_list_layout).setBackground(getResources().getDrawable(R.drawable.edit_text_background));
                    } else {
                        View unselectedView = recyclerView.getChildAt(i);
                        unselectedView.findViewById(R.id.central_list_layout).setBackgroundColor(getResources().getColor(R.color.background));
                    }
                }

                getView().findViewById(R.id.fragment_chores_claimChoreButton).setVisibility(View.VISIBLE);
                getView().findViewById(R.id.fragment_chores_editChoreButton).setVisibility(View.VISIBLE);
                getView().findViewById(R.id.fragment_chores_deleteChoreButton).setVisibility(View.VISIBLE);

            }
        });
    }

    /**
     * Actions for the different buttons - create, claim, edit and delete chore.
     * @param v the view
     */
    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.fragment_chores_createNewChoreButton) {
            Intent intent = new Intent(getContext(), CreateChoreActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.fragment_chores_claimChoreButton) {

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("Har du utfört sysslan?")
                    .setPositiveButton("JA", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            int points = Integer.parseInt(itemList.get(selectedItem).getPoints());
                            // Uppdatera poängen för användaren i selected group:
                            Model model = Model.getInstance(getActivity().getFilesDir(),getContext());
                            Group group = model.getSelectedGroup();
                            User currentUser = model.getUser();
                            group.getChores().get(selectedItem).setLastDoneByUser(currentUser.getUsername());
                            System.out.println(group.getChores().get(selectedItem).getLastDoneByUser());
                            ArrayList<Transferable> data = new ArrayList<>();
                            data.add(group);
                            group.modifyUserPoints(model.getUser(), points);
                            Message message = new Message(NetCommands.clientInternalGroupUpdate, currentUser, data);
                            model.handleTask(message);
                        }
                    }).setNegativeButton("NEJ", null);

            AlertDialog alert = builder.create();
            alert.show();
            alert.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.button_background));
            alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.background));
            alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.background));

        } else if (v.getId() == R.id.fragment_chores_editChoreButton) {
            Intent intent = new Intent(getContext(), CreateChoreActivity.class);
            intent.putExtra("chore", itemList.get(selectedItem).getChore());
            startActivity(intent);
        } else if (v.getId() == R.id.fragment_chores_deleteChoreButton) {
            deleteChore();
        }
    }

    /**
     * Removes selection
     */
    private void resetSelected(){
        getView().findViewById(R.id.fragment_chores_claimChoreButton).setVisibility(View.INVISIBLE);
        getView().findViewById(R.id.fragment_chores_editChoreButton).setVisibility(View.INVISIBLE);
        getView().findViewById(R.id.fragment_chores_deleteChoreButton).setVisibility(View.INVISIBLE);

        for(int i = 0; i < recyclerView.getChildCount(); i++){
            View unselectedView = recyclerView.getChildAt(i);
            unselectedView.findViewById(R.id.central_list_layout).setBackgroundColor(getResources().getColor(R.color.background));
        }
    }

    /**
     * Deletes selected chore
     */
    private void deleteChore() {
        Model model = Model.getInstance(getActivity().getFilesDir(),getContext());
        Group selectedGroup = model.getSelectedGroup();
        Chore chore = selectedGroup.getSingleChore(selectedItem);
        selectedGroup.deleteChore(chore);
        ArrayList<Transferable> data = new ArrayList<>();
        data.add(selectedGroup);
        Message message = new Message(NetCommands.clientInternalGroupUpdate, model.getUser(), data);
        model.handleTask(message);
        resetSelected();
    }

}
