package com.technion.shiftly;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ExpandableListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class TimeslotsConfigActivity extends AppCompatActivity {

    public enum Day{

    }

    // Private fields of TimeslotsConfigActivity

    private List<String> days_data;
    private HashMap<String, List<CheckBox>> timeslots_data;
    List<String> days;

    List<Map<String,Boolean>> checked_items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeslots_config);

        Toolbar mainToolbar = findViewById(R.id.timeslot_toolbar);
        setSupportActionBar(mainToolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.group_create_label));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        days = Arrays.asList(getResources().getStringArray(R.array.week_days));
        ExpandableListView expandableListView = findViewById(R.id.ts_config_list);
        populate_data_into_lists();
        CustomExpandableAdapter expandableListAdapter = new CustomExpandableAdapter(this, days_data, timeslots_data, checked_items);
        expandableListView.setAdapter(expandableListAdapter);

        expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                    if (parent.isGroupExpanded(groupPosition)) {
                        parent.collapseGroup(groupPosition);
                        Log.v("GROUP COLLAPSE", "A");
                    } else {
                        parent.expandGroup(groupPosition);
                        Log.v("GROUP EXPAND", "A");
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
                    checked_items.get(groupPosition).put(days.get(groupPosition) + "_" + (char)(childPosition + (int)('a')), true);
                    Log.v("CHILD PUT TRUE", "A");
                } else {
                    checked_items.get(groupPosition).put(days.get(groupPosition) + "_" + (char)(childPosition + (int)('a')), false);
                    Log.v("CHILD PUT FALSE", "A");
                }
                return true;
            }
        });

        Button create_button = findViewById(R.id.configure_button);
        create_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String group_name = getIntent().getExtras().getString("group_code");

                Intent intent;
                if (group_name.isEmpty()) {
                    intent = new Intent(getApplicationContext(), ScheduleViewActivity.class);
                } else {
                    intent = new Intent(getApplicationContext(), GroupCreationActivityConfirm.class);
                    intent.putExtra("group_name", group_name);
                }
                startActivity(intent);

            }
        });

    }

    private void populate_data_into_lists() {

        days_data = new ArrayList<>(days);
        timeslots_data = new HashMap<>();
        checked_items = new ArrayList<>();

        for (int i = 0; i < days_data.size(); i++) {
            List<CheckBox> shifts = new ArrayList<>();

            CheckBox cb = new CheckBox(getApplicationContext());
            cb.setText("00:00 - 8:00");
            CheckBox cb2 = new CheckBox(getApplicationContext());
            cb2.setText("8:00 - 16:00");
            CheckBox cb3 = new CheckBox(getApplicationContext());
            cb3.setText("16:00 - 00:00");
            shifts.add(cb);
            shifts.add(cb2);
            shifts.add(cb3);

            timeslots_data.put(days_data.get(i), shifts);

            Map<String,Boolean> shifts_init = new HashMap<>();
            for (int j = 0; j < 3; j++) {
                shifts_init.put(days.get(i) + "_" + (char)(j + (int)('a')), false);
            }
            checked_items.add(shifts_init);
        }

    }

}
