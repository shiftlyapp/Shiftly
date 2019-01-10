package com.technion.shiftly;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

// An adapter for "Group I belong" and "Groups I manage" lists
public class GroupsListAdapter extends RecyclerView.Adapter<GroupsListAdapter.ViewHolder> {

    private List<String> mNames;
    private List<Long> mCounts;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    // data is passed into the constructor
    GroupsListAdapter(Context context, List<String> names, List<Long> counts) {
        this.mInflater = LayoutInflater.from(context);
        this.mNames = names;
        this.mCounts = counts;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.groups_recycleview_list_item, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        String name = mNames.get(position);
        String count = Long.toString(mCounts.get(position));
        holder.myNameView.setText(name);
        holder.myCountView.setText(String.format(Constants.MEMBERS_COUNT, count));
    }

    // total number of rows
    @Override
    public int getItemCount() {
        if (mNames == null) return 0;
        return mNames.size();
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView myNameView;
        TextView myCountView;

        ViewHolder(View itemView) {
            super(itemView);
            myNameView = itemView.findViewById(R.id.group_name);
            myCountView = itemView.findViewById(R.id.group_count);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null)
                mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    String getItem(int id) {
        return mNames.get(id);
    }

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}
