package com.mau.chorely.activities.utils;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mau.chorely.R;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.RecyclerViewHolder> {
    private ArrayList<ListItem> mItemList;
    private OnItemClickListener mListener;
    public interface OnItemClickListener{
        void onItemClick(int position);

    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }

    public static class RecyclerViewHolder extends RecyclerView.ViewHolder{
        private ImageView mImageView;
        private TextView textViewTitle;
        private TextView textViewDetails;
        private TextView textViewMembersNum;

        private RecyclerViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.imageViewGroupList);
            textViewTitle = itemView.findViewById(R.id.textViewGroupTitle);
            textViewDetails = itemView.findViewById(R.id.textViewGroupDetails);
            textViewMembersNum = itemView.findViewById(R.id.textViewMembersNumber);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null){
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION){
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }


    public RecyclerViewAdapter (ArrayList<ListItem> itemList){
        this.mItemList = itemList;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_groups, parent, false);
        return new RecyclerViewHolder(v, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {
        ListItem currentItem = mItemList.get(position);

        holder.mImageView.setImageResource(currentItem.getmImageResource());
        holder.textViewTitle.setText(currentItem.getText1());
        holder.textViewDetails.setText(currentItem.getText2());
        holder.textViewMembersNum.setText(currentItem.getText3());
    }

    @Override
    public int getItemCount() {
        return mItemList.size();
    }
}
