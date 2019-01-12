package com.technion.shiftly;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.util.List;


public class OptionsListAdapter extends RecyclerView.Adapter<OptionsListAdapter.OptionsViewHolder> {

    private List<Pair<String, String>> options; // First = day, Second = time
    private LayoutInflater options_inflater;
    private ItemClickListener options_clicklistener;
    RelativeLayout options_layout;

    public void setClickListener(ItemClickListener listener) {
        this.options_clicklistener = listener;
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class OptionsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView day_textview;
        TextView shift_textview;
        RelativeLayout options_layout;

        OptionsViewHolder(View itemView) {
            super(itemView);
            day_textview = itemView.findViewById(R.id.options_day);
            shift_textview = itemView.findViewById(R.id.options_shift);

            options_layout = itemView.findViewById(R.id.options_list_item);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (options_clicklistener != null)
                options_clicklistener.onItemClick(view, getAdapterPosition());
        }
    }

    public OptionsListAdapter(Context context, List<Pair<String, String>> options) {
        this.options_inflater = LayoutInflater.from(context);
        this.options = options;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public OptionsListAdapter.OptionsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View view = options_inflater.inflate(R.layout.options_recyclerview_list_item, parent, false);
        return new OptionsViewHolder(view);

    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(OptionsViewHolder holder, int position) {


        // - get element from dataset at this position
        String day = options.get(position).first;
        String time = options.get(position).second;

        // - replace the contents of the view with that element
        holder.day_textview.setText(day);
        holder.shift_textview.setText(time);

        (holder.options_layout).setBackgroundColor(Color.WHITE);

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return options.size();
    }

    public static class ItemClickListener {
        public void onItemClick(View view, int adapterPosition) {
            System.out.print("SUCCESS!!!");
        }
    }
}

