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

public class ListViewAdapter extends ArrayAdapter<User> {

    public ListViewAdapter(Context context, ArrayList<User> memberList) {
        super(context, 0, memberList);
    }

    private View initView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.activity_list_view_adapter, parent, false
            );
        }
        TextView userName = convertView.findViewById(R.id.list_view_members);
        User currentItem = getItem(position);
        if (currentItem != null) {
            userName.setText(currentItem.getUsername());
        }
        return convertView;
    }


    /**
     * System callback, returns initview.
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }


}
