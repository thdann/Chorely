package com.mau.chorely.activities.centralFragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mau.chorely.R;
import com.mau.chorely.activities.centralFragments.utils.CentralActivityRecyclerViewAdapter;
import com.mau.chorely.activities.centralFragments.utils.ListItemCentral;

import java.util.ArrayList;

import shared.transferable.Reward;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentRewards#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentRewards extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static ArrayList<ListItemCentral> itemList = new ArrayList<>();

    private RecyclerView recyclerView;
    private CentralActivityRecyclerViewAdapter adapter;
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
    // TODO: Rename and change types and number of parameters
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

            for(Reward reward : rewards){
                itemList.add(new ListItemCentral(reward));
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_rewards, container, false);
    }


    private void buildRecyclerView(){
        if(getActivity() != null) {
            recyclerView = getActivity().findViewById(R.id.fragment_chores_recyclerView);
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

    }
}
