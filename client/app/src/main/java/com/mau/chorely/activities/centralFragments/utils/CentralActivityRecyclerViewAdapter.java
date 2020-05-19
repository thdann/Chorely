package com.mau.chorely.activities.centralFragments.utils;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mau.chorely.R;

import java.util.ArrayList;

public class CentralActivityRecyclerViewAdapter extends RecyclerView.Adapter<CentralActivityRecyclerViewAdapter.RecyclerViewHolder> {

    private ArrayList<ListItemCentral> itemList;
    private OnitemClickListener clickListener;
    private int customLayoutId = -1;
    private boolean hasCustomLayout = false;

    public interface OnitemClickListener {
        void onItemClick(int position);
    }

    public CentralActivityRecyclerViewAdapter(ArrayList<ListItemCentral> itemList) {
        this.itemList = itemList;
    }

    public CentralActivityRecyclerViewAdapter(ArrayList<ListItemCentral> itemList, int customLayoutId) {
        this.customLayoutId = customLayoutId;
        this.itemList = itemList;
        hasCustomLayout = true;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;


        if (hasCustomLayout) {
            view = LayoutInflater.from(parent.getContext()).inflate(
                    customLayoutId, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.list_item_central_activity, parent, false);
        }
        return new RecyclerViewHolder(view, clickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {
        ListItemCentral item = itemList.get(position);
        if (hasCustomLayout) {
            holder.textViewTitle.setText(item.getTitle());
            holder.textViewPoints.setText(item.getPoints());
        } else {
            holder.textViewTitle.setText(item.getTitle());
            holder.textViewDescription.setText(item.getDescription());
            holder.textViewPoints.setText(item.getPoints());
            holder.textViewLastDoneBy.setText(item.getDoneBy());
            holder.textViewUserName.setText(item.getUsername());
        }
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public void setOnclickListener(OnitemClickListener listener) {
        this.clickListener = listener;
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewTitle;
        private TextView textViewDescription;
        private TextView textViewPoints;
        private TextView textViewUserName;
        private TextView textViewLastDoneBy;

        private RecyclerViewHolder(View itemView, final OnitemClickListener clickListener) {
            super(itemView);
            if (hasCustomLayout) {
                textViewTitle = itemView.findViewById(R.id.scoreboard_item_titleText);
                textViewPoints = itemView.findViewById(R.id.scoreboard_item_scoreText);
            } else {
                textViewTitle = itemView.findViewById(R.id.central_list_titleText);
                textViewDescription = itemView.findViewById(R.id.central_list_description);
                textViewPoints = itemView.findViewById(R.id.central_list_pointText);
                textViewUserName = itemView.findViewById(R.id.central_list_lastDoneByName);
                textViewLastDoneBy = itemView.findViewById(R.id.central_list_lastDoneBy);
            }

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (clickListener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            clickListener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }

}
