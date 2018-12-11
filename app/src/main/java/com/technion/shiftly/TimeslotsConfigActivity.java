package com.technion.shiftly;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.SimpleExpandableListAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class TimeslotsConfigActivity extends AppCompatActivity {

    // Private properties of TimeslotsConfigActivity

    private ExpandableListView expandableListView;
    private SimpleExpandableListAdapter expandableListAdapter;

    private List<String> days_data;
    private HashMap<String, List<String>> timeslots_data;

    private List<HashMap<String, String>> days_titles_text;
    private List<List<HashMap<String, String>>> timeslots_titles_text;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeslots_config);

        Toolbar mainToolbar = findViewById(R.id.timeslot_toolbar);
        setSupportActionBar(mainToolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.timeslots_configure));

        expandableListView = findViewById(R.id.ts_config_list);

        populate_data_into_lists();

        String[] groupFrom = {"Day_Name"};
        int groupTo[] = {R.id.expandable_group_items};
        String[] childFrom = {"Time"};
        int childTo[] = {R.id.expandable_child_items};


        expandableListAdapter = new SimpleExpandableListAdapter(this, days_titles_text, R.layout.group_items_layout,
                groupFrom, groupTo, timeslots_titles_text, R.layout.child_items_layout, childFrom, childTo);

        expandableListView.setAdapter(expandableListAdapter);

        expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                expandableListView.expandGroup(groupPosition);
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
        for (int i = 0; i < 7; i++) {
            List<String> shifts = new ArrayList<>();
            shifts.add("12AM - 8AM");
            shifts.add("8AM - 4PM");
            shifts.add("4PM - 12AM");
            timeslots_data.put(days_data.get(i), shifts);
        }

        days_titles_text = new ArrayList<>();
        timeslots_titles_text = new ArrayList<>();

        for (int i = 0; i < days_data.size(); i++) {
            // Creating a map for each day
            HashMap<String, String> one_day_map = new HashMap<>();
            one_day_map.put("Day_Name", days_data.get(i));
            days_titles_text.add(one_day_map);


            // Creating the list of 3 timeslots for one day
            List<HashMap<String, String>> one_day_timeslots = new ArrayList<>();
            for (int j = 0; j < 3; j++) {
                // Creating a map for every child (timeslot) in this day
                HashMap<String, String> one_timeslot = new HashMap<>();
                one_timeslot.put("Time", timeslots_data.get(days_data.get(i)).get(j));
                one_day_timeslots.add(one_timeslot);
            }

            // Adding the timeslots of one day to the titles
            timeslots_titles_text.add(one_day_timeslots);

        }


    }
}
