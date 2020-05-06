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

import shared.transferable.Group;

/**
 * This class handles all adaptation of group objects to the recyclerView.
 * @author Timothy Denison
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.RecyclerViewHolder> {
    private ArrayList<Group> mItemList;
    private OnItemClickListener mListener;
    public interface OnItemClickListener{
        void onItemClick(int position);

    }

    /**
     * Method to set the listener of the recyclerView.
     * @param listener listener to set.
     */
    public void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }

    /**
     * Private class handling the viewHolder of the recyclerview.
     */
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

    public RecyclerViewAdapter (ArrayList<Group> itemList){
        this.mItemList = itemList;
    }

    /**
     * Method to set the layout of the recyclerview.
     * @param parent Unknown, probably the activity containing the view.
     * @param viewType unknown.
     * @return Returns a newly created viewholder with the set listener and layout.
     */
    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.recyclerview_groups, parent, false);
        return new RecyclerViewHolder(v, mListener);
    }

    /**
     * Method to handle creation of new items in the recyclerView.
     * @param holder viewholder of the item.
     * @param position Position of the item
     */
    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {
        Group currentGroup = mItemList.get(position);
        String groupSize = "" + currentGroup.size();
        holder.mImageView.setImageResource(R.drawable.ic_group_black_24dp);
        holder.textViewTitle.setText(currentGroup.getName());
        holder.textViewDetails.setText(currentGroup.getDescription());
        holder.textViewMembersNum.setText(groupSize);
    }

    /**
     * Getter of the size of the list.
     * @return returns the size of the arraylist containing the data of the recyclerview.
     */
    @Override
    public int getItemCount() {
        return mItemList.size();
    }
}
