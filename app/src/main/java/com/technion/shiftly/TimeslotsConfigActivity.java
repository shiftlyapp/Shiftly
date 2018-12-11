package com.technion.shiftly;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class TimeslotsConfigActivity extends AppCompatActivity {

    // Private properties of TimeslotsConfigActivity

    private ExpandableListView expandableListView;
    private CustomExpandableAdapter expandableListAdapter;

    private List<String> days_data;
    private HashMap<String, List<String>> timeslots_data;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeslots_config);

        Toolbar mainToolbar = findViewById(R.id.timeslot_toolbar);
        setSupportActionBar(mainToolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.timeslots_configure));

        expandableListView = findViewById(R.id.ts_config_list);

        populate_data_into_lists();

        expandableListAdapter = new CustomExpandableAdapter(this, days_data, timeslots_data);

        expandableListView.setAdapter(expandableListAdapter);

        expandableListView.setOnGroupClickListener(new android.widget.ExpandableListView.OnGroupClickListener() {
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

        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                /* TODO: mark/unmark checkbox near the child option and add/remove it from a list that later will be sent to the DB */
                return false;
            }
        });


        Button configure_button = findViewById(R.id.configure_button);
        configure_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), GroupCreationActivity.class);
                startActivity(intent);
            }
        });
    }

    private void populate_data_into_lists() {

        List<String> days = Arrays.asList(getResources().getStringArray(R.array.week_days));
        days_data = new ArrayList<>(days);

        timeslots_data = new HashMap<>();

        for (int i = 0; i < days_data.size(); i++) {
            List<String> shifts = new ArrayList<>();
            shifts.add("12AM - 8AM");
            shifts.add("8AM - 4PM");
            shifts.add("4PM - 12AM");
            timeslots_data.put(days_data.get(i), shifts);
        }

    }
}
