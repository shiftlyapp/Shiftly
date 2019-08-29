package com.technion.shiftlyapp.shiftly.scheduleView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.technion.shiftlyapp.shiftly.R;

import java.util.List;

class ShiftsListAdapter extends RecyclerView.Adapter<ShiftsListAdapter.ViewHolder> {

    private List<String> mDaysNames;
    private List<String> mStartTimes;
    private List<String> mEndTimes;
    private LayoutInflater mInflater;
    private Context context;

    // data is passed into the constructor
    ShiftsListAdapter(Context context, List<String> names, List<String> start_times, List<String> end_times) {
        this.mInflater = LayoutInflater.from(context);
        this.mDaysNames = names;
        this.mStartTimes = start_times;
        this.mEndTimes = end_times;
    }

    // inflates the row layout from xml when needed
    @Override @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.agenda_recycleview_list_item, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(@NonNull final ShiftsListAdapter.ViewHolder holder, int position) {
        String dayName = mDaysNames.get(position);
        holder.mDayNameView.setText(dayName);

        String startingHour = String.format("%s", mStartTimes.get(position));
        if (!startingHour.equals("")) startingHour = context.getResources().getString(R.string.start_time) + " " + startingHour;
        holder.mStartHourView.setText(startingHour);

        String endingHour = String.format("%s", mEndTimes.get(position));
        if (!endingHour.equals("")) endingHour = context.getResources().getString(R.string.end_time) + " " + endingHour;
        holder.mEndHourView.setText(endingHour);

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

    // total number of rows
    @Override
    public int getItemCount() {
        if (mDaysNames == null) return 0;
        return mDaysNames.size();
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView mDayNameView;
        TextView mStartHourView;
        TextView mEndHourView;

        ViewHolder(View itemView) {
            super(itemView);
            mDayNameView = itemView.findViewById(R.id.shift_day);
            mStartHourView = itemView.findViewById(R.id.shift_starting_hour);
            mEndHourView = itemView.findViewById(R.id.shift_ending_hour);
            context = itemView.getContext();
        }
    }

}
