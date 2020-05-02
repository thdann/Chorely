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
        args.putSerializable("CHORES" , chores);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            ArrayList<Chore> chores = (ArrayList<Chore>) getArguments().getSerializable("CHORES");
            for(Chore chore : chores){
                itemList.add(new ListItemCentral(chore));
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
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

    public static void updateList(ArrayList<Chore> chores){
        for(Chore c : chores){
            if(itemList.contains(c)){
                int index = itemList.indexOf(c);
                ListItemCentral item = itemList.get(index);
                if(!itemList.get(index).allIsEqual(c)){
                    item.updateItem(c);
                }
            } else{
                itemList.add(new ListItemCentral(c));
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void buildRecyclerView(){
            recyclerView.setHasFixedSize(true);
            layoutManager = new LinearLayoutManager(getActivity());
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
        Intent intent = new Intent(getContext(), CreateChoreActivity.class);
        startActivity(intent);
    }
}
