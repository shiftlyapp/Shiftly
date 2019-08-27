package com.technion.shiftlyapp.shiftly.options;

import android.content.Context;
import android.graphics.Color;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Pair;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.technion.shiftlyapp.shiftly.R;

import java.util.List;

public class OptionsListAdapter extends RecyclerView.Adapter<OptionsListAdapter.OptionsViewHolder> {

    @NonNull
    private List<Pair<String, String>> options; // First = day, Second = time
    private LayoutInflater options_inflater;
    private OptionsListAdapter.ItemClickListener options_listener;
    private SparseBooleanArray state_array = new SparseBooleanArray();

    public OptionsListAdapter(Context context, List<Pair<String, String>> options) {
        this.options_inflater = LayoutInflater.from(context);
        this.options = options;
    }


    @Override
    public void onBindViewHolder(@NonNull final OptionsViewHolder holder, int position) {
        // - get element from dataset at this position
        String day = options.get(position).first;
        String time = options.get(position).second;

        // - replace the contents of the view with that element
        holder.day_textview.setText(day);
        holder.shift_textview.setText(time);
        holder.checkbox.setChecked(state_array.get(position));

        (holder.options_layout).setBackgroundColor(Color.WHITE);

    }

    public class OptionsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // These are the elements of the list item
        CheckBox checkbox;
        TextView day_textview;
        TextView shift_textview;
        RelativeLayout options_layout;

        OptionsViewHolder(View itemView) {
            super(itemView);

            day_textview = itemView.findViewById(R.id.options_day);
            shift_textview = itemView.findViewById(R.id.options_shift);
            checkbox = itemView.findViewById(R.id.options_checkbox);
            options_layout = itemView.findViewById(R.id.options_list_item);

            itemView.setOnClickListener(this);
            checkbox.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {

            if (options_listener != null)
                options_listener.onItemClick(view, getAdapterPosition());

            int adapterPosition = getAdapterPosition();
            if (!state_array.get(adapterPosition, false)) {
                checkbox.setChecked(true);
                state_array.put(adapterPosition, true);
            } else {
                checkbox.setChecked(false);
                state_array.put(adapterPosition, false);
            }

        }
    }

    // Create new views (invoked by the layout manager)
    @Override
    public OptionsListAdapter.OptionsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View view = options_inflater.inflate(R.layout.options_recyclerview_list_item, parent, false);
        return new OptionsViewHolder(view);

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        if (options == null) return 0;
        return options.size();
    }


    void setClickListener(OptionsListAdapter.ItemClickListener itemClickListener) {
        this.options_listener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

}
