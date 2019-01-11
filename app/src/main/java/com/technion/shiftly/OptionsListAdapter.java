package com.technion.shiftly;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.List;


public class OptionsListAdapter extends RecyclerView.Adapter<OptionsListAdapter.TimeslotsViewHolder> {

    private List<Pair<String, String>> timeslots; // First = day, Second = time
    private LayoutInflater options_inflater;
    private ItemClickListener options_clicklistener;


    public void setClickListener(ItemClickListener listener) {
        this.options_clicklistener = listener;
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class TimeslotsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView day_textview;
        TextView hour_textview;

        TimeslotsViewHolder(View itemView) {
            super(itemView);
//            day_textview = itemView.findViewById(R.id.timeslot_day);
//            hour_textview = itemView.findViewById(R.id.timeslots_hour);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (options_clicklistener != null)
                options_clicklistener.onItemClick(view, getAdapterPosition());
        }
    }

    public OptionsListAdapter(Context context, List<Pair<String, String>> timeslots) {
        this.options_inflater = LayoutInflater.from(context);
        this.timeslots = timeslots;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public OptionsListAdapter.TimeslotsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View view = options_inflater.inflate(R.layout.options_recycleview_list_item, parent, false);
        return new TimeslotsViewHolder(view);

    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(TimeslotsViewHolder holder, int position) {

        // - get element from dataset at this position
//        String day = timeslots.get(position).first;
//        String time = timeslots.get(position).second;

        // - replace the contents of the view with that element
//        holder.day_textview.setText(day);
//        holder.hour_textview.setText(time);

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return timeslots.size();
    }

    public static class ItemClickListener {
        public void onItemClick(View view, int adapterPosition) {
            System.out.print("SUCCESS!!!");
        }
    }
}

