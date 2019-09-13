package com.technion.shiftlyapp.shiftly.scheduleView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
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
    private List<String> mEmployeesNames;
    private List<String> mEmployeesList;
    private LayoutInflater mInflater;
    private Context context;
    private ShiftsListAdapter.ItemClickListener shifts_listener;
    private ArrayAdapter<String> employees_name_spinner_adapter;

    // data is passed into the constructor
    ShiftsListAdapter(Context context, List<String> names, List<String> start_times, List<String> end_times, List<String> employees_names, List<String> employees_list) {
        this.mInflater = LayoutInflater.from(context);
        this.mDaysNames = names;
        this.mStartTimes = start_times;
        this.mEndTimes = end_times;
        this.mEmployeesNames = employees_names;
        this.mEmployeesList = employees_list;

        employees_name_spinner_adapter = new ArrayAdapter<>(context, R.layout.custom_spinner_item, employees_list);
        employees_name_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
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

        if (mEmployeesList.isEmpty()) {
            holder.mEmployeeNameSpinner.setVisibility(View.GONE);
        } else {
            String employeeName = String.format("%s", mEmployeesNames.get(position));
            holder.mEmployeeNameSpinner.setAdapter(employees_name_spinner_adapter);
            holder.mEmployeeNameSpinner.setSelection(getEmployeePosition(employeeName));
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

    private int getEmployeePosition(String employeeName) {
        return mEmployeesList.indexOf(employeeName);
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView mDayNameView;
        TextView mStartHourView;
        TextView mEndHourView;
        Spinner mEmployeeNameSpinner;

        ViewHolder(View itemView) {
            super(itemView);
            mDayNameView = itemView.findViewById(R.id.shift_day);
            mStartHourView = itemView.findViewById(R.id.shift_starting_hour);
            mEndHourView = itemView.findViewById(R.id.shift_ending_hour);
            mEmployeeNameSpinner = itemView.findViewById(R.id.employee_name_spinner);
            context = itemView.getContext();
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {


        }
    }


    void setClickListener(ShiftsListAdapter.ItemClickListener itemClickListener) {
        this.shifts_listener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }


    // total number of rows
    @Override
    public int getItemCount() {
        if (mDaysNames == null) return 0;
        return mDaysNames.size();
    }
}
