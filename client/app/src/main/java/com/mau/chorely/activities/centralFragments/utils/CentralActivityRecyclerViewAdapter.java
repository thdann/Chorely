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
    public interface OnitemClickListener {
        void onItemClick(int position);
    }



    public CentralActivityRecyclerViewAdapter(ArrayList<ListItemCentral> itemList){
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.list_item_central_activity, parent, false);

        return new RecyclerViewHolder(view, clickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {
        ListItemCentral item = itemList.get(position);
        holder.textViewTitle.setText(item.getTitle());
        holder.textViewDescription.setText(item.getDescription());
        holder.textViewPoints.setText(item.getPoints());
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public void setOnclickListener(OnitemClickListener listener){
        this.clickListener = listener;
    }

    public static class RecyclerViewHolder extends RecyclerView.ViewHolder{
        private TextView textViewTitle;
        private TextView textViewDescription;
        private TextView textViewPoints;
        private RecyclerViewHolder(View itemView, final OnitemClickListener clickListener){
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.central_list_titleText);
            textViewDescription = itemView.findViewById(R.id.central_list_description);
            textViewPoints = itemView.findViewById(R.id.central_list_pointText);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(clickListener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            clickListener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }

}
