package com.technion.shiftlyapp.shiftly.groupsList;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.abdularis.civ.CircleImageView;
import com.squareup.picasso.Picasso;
import com.technion.shiftlyapp.shiftly.R;

import java.util.List;

// An adapter for "Group I belong" and "Groups I manage" lists
public class GroupsListAdapter extends RecyclerView.Adapter<GroupsListAdapter.ViewHolder> {

    private List<String> mNames;
    private List<Long> mCounts;
    private List<String> mIconsUrls;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private ItemLongClickListener mLongClickListener;
    private Context context;

    // data is passed into the constructor
    GroupsListAdapter(Context context, List<String> names, List<Long> counts, List<String> icons) {
        this.mInflater = LayoutInflater.from(context);
        this.mNames = names;
        this.mCounts = counts;
        this.mIconsUrls = icons;
    }

    // inflates the row layout from xml when needed
    @Override @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.groups_recycleview_list_item, parent, false);
        return new ViewHolder(view);
    }

    public List<String> getmIconsUrls() {
        return mIconsUrls;
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        String name = mNames.get(position);
        String count = Long.toString(mCounts.get(position));
        String url = mIconsUrls.get(position);
        holder.myNameView.setText(name);
        String membersCount = String.format("%s", count) + " " + context.getResources().getString(R.string.members);
        holder.myCountView.setText(membersCount);
        if (!url.equals("none")) {
            Picasso.get().load(url).noFade().placeholder(R.drawable.group).into(holder.myIconView);
        } else {
            Picasso.get().load(R.drawable.group).noFade().into(holder.myIconView);
        }
        if (position == 0) { // Do this if first item (Remove top line)
            ViewGroup.MarginLayoutParams marginLayoutParams =
                    (ViewGroup.MarginLayoutParams) holder.itemView.getLayoutParams();
            marginLayoutParams.setMargins(0, 0, 0, 0);
            holder.itemView.setLayoutParams(marginLayoutParams);
        }
        if (position < getItemCount()-1) {
            holder.itemView.setBackground(ResourcesCompat.getDrawable(holder.itemView.getResources(), R.drawable.list_item_bg, null));
        } else {
            holder.itemView.setBackground(ResourcesCompat.getDrawable(holder.itemView.getResources(), R.drawable.list_item_bg_bottom, null));
        }
    }

    public void remove_item(int position) {
        mNames.remove(position);
        mCounts.remove(position);
        mIconsUrls.remove(position);
        notifyItemRemoved(position);
    }

    // total number of rows
    @Override
    public int getItemCount() {
        if (mNames == null) return 0;
        return mNames.size();
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        TextView myNameView;
        TextView myCountView;
        CircleImageView myIconView;

        ViewHolder(View itemView) {
            super(itemView);
            myNameView = itemView.findViewById(R.id.group_name);
            myCountView = itemView.findViewById(R.id.group_count);
            myIconView = itemView.findViewById(R.id.group_icon);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            context = itemView.getContext();
        }


        @Override
        public boolean onLongClick(View view) {
            if (mLongClickListener != null)
                mLongClickListener.onItemLongClick(view,getAdapterPosition());
            return true;
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

    // allows long clicks events to be caught
    void setLongClickListener(ItemLongClickListener itemLongClickListener) {
        this.mLongClickListener = itemLongClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    public interface ItemLongClickListener {
        void onItemLongClick(View view, int position);
    }
}
