package com.technion.shiftly;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ExpandableListView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


public class TimeslotsConfigActivity extends AppCompatActivity {

    // Private fields of TimeslotsConfigActivity

    private List<String> days_data;
    private HashMap<String, List<CheckBox>> timeslots_data;

    List<List<Boolean>> checked_items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeslots_config);

        Toolbar mainToolbar = findViewById(R.id.timeslot_toolbar);
        setSupportActionBar(mainToolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.timeslots_configure));

        ExpandableListView expandableListView = findViewById(R.id.ts_config_list);
        populate_data_into_lists();
        CustomExpandableAdapter expandableListAdapter = new CustomExpandableAdapter(this, days_data, timeslots_data, checked_items);
        expandableListView.setAdapter(expandableListAdapter);

        expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                    if (parent.isGroupExpanded(groupPosition)) {
                        parent.collapseGroup(groupPosition);
                    } else {
                        parent.expandGroup(groupPosition);
                    }
                    return true;
            }
        });

        // TODO: bug - this doesn't get called when a child is being clicked on
        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                TextView child = v.findViewById(R.id.expandable_child_items);

                boolean child_is_checked = ((CheckBox)child).isChecked();

                if (child_is_checked) {
                    checked_items.get(groupPosition).set(childPosition, true);
                } else {
                    checked_items.get(groupPosition).set(childPosition, false);
                }
                return false;
            }
        });

    }

    private void populate_data_into_lists() {

        List<String> days = Arrays.asList(getResources().getStringArray(R.array.week_days));
        days_data = new ArrayList<>(days);
        timeslots_data = new HashMap<>();
        checked_items = new ArrayList<>();

        for (int i = 0; i < days_data.size(); i++) {
            List<CheckBox> shifts = new ArrayList<>();

            CheckBox cb = new CheckBox(getApplicationContext());
            cb.setText("12AM - 8AM");
            CheckBox cb2 = new CheckBox(getApplicationContext());
            cb2.setText("8AM - 4PM");
            CheckBox cb3 = new CheckBox(getApplicationContext());
            cb3.setText("4PM - 12AM");
            shifts.add(cb);
            shifts.add(cb2);
            shifts.add(cb3);

            timeslots_data.put(days_data.get(i), shifts);

            List<Boolean> shifts_init = new ArrayList<>();
            for (int j = 0; j < 3; j++) {
                shifts_init.add(false);
            }
            checked_items.add(shifts_init);
        }

    }

}
