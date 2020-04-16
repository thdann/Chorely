package com.mau.chorely.activities.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.mau.chorely.R;

import java.util.ArrayList;

import shared.transferable.User;

/**
 * This class handles the displaying of members inside the spinner in the CreateEditGroupActivity.
 * @author Timothy Denison
 */
public class SpinnerAdapterMembers extends ArrayAdapter<User> {
    public SpinnerAdapterMembers(Context context, ArrayList<User> memberList){
        super(context, 0, memberList);
    }

    /**
     * This method sets the layout-resource for each item in the spinner.
     * @param position position of the current item
     * @param convertView layout of the current item.
     * @param parent unknown. Probably the spinner itself, or the activity containing it.
     * @return returns formatted view.
     */
    private View initView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.member_spinner_items, parent, false
            );
        }
        TextView userName = convertView.findViewById(R.id.spinnerText);
        User currentItem = getItem(position);
        if(currentItem != null){
            userName.setText(currentItem.getUsername());
        }
        return convertView;
    }

    /**
     *System callback, returns initview.
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    /**
     *System callback, returns initview.
     */
    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }
}
