package com.mau.chorely.activities.fragments.CreateGroupFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.mau.chorely.R;

import shared.transferable.Group;

public class CreateNewGroupFragment extends Fragment {
    Group selectedGroup;
    SpinnerAdapterMembers spinnerAdapter;

    private CreateNewGroupFragment(Group selectedGroup){
        this.selectedGroup = selectedGroup;
    }

    public static Fragment getInstance(Group selectedGroup){
        return new CreateNewGroupFragment(selectedGroup);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initSpinner();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.create_edit_group, container, false);
    }

    private void initSpinner(){
        Spinner memberSpinner = getActivity().findViewById(R.id.spinnerMembers);
        spinnerAdapter = new SpinnerAdapterMembers(getActivity(), selectedGroup.getUsers());
        memberSpinner.setAdapter(spinnerAdapter);

    }

}
