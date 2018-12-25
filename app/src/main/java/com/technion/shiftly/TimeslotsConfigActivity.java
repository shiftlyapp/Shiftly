package com.technion.shiftly;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ExpandableListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.view.ViewGroup.FOCUS_BLOCK_DESCENDANTS;


public class TimeslotsConfigActivity extends AppCompatActivity {

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    List<String> days_data;
    List<Map<String, CheckBox>> timeslots_data;
    List<String> days;
    List<Map<String,Boolean>> checked_items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeslots_config);
        Bundle extras = getIntent().getExtras();
        String group_name = extras.getString("GROUP_NAME");

        Toolbar mainToolbar = findViewById(R.id.timeslot_toolbar);
        setSupportActionBar(mainToolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.group_create_label));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        days = Arrays.asList(getResources().getStringArray(R.array.week_days));
        ExpandableListView expandableListView = findViewById(R.id.ts_config_list);
        expandableListView.setDescendantFocusability(FOCUS_BLOCK_DESCENDANTS);
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
                CheckBox child = v.findViewById(R.id.expandable_child_items);
                boolean child_is_checked = child.isChecked();

                if (child_is_checked) {
                    checked_items.get(groupPosition).put(getShiftDayAndLetter(groupPosition,childPosition), true);
                } else {
                    checked_items.get(groupPosition).put(getShiftDayAndLetter(groupPosition,childPosition), false);
                }
                return false;
            }
        });

        Button confirm_button = findViewById(R.id.configure_button);
        confirm_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), GroupCreationActivityConfirm.class); //TODO: change to GroupCreationConfirmActivity
                String group_name = getIntent().getExtras().getString("GROUP_NAME");
                intent.putExtra("GROUP_NAME", group_name);
                intent.putExtra("TIMESLOTS_ARRAY", (ArrayList<Map<String,Boolean>>)checked_items);
                startActivity(intent);
                finish();
            }
        });

    }

    private void populate_data_into_lists() {

        days_data = new ArrayList<>(days); // 7 days array
        timeslots_data = new ArrayList<>();
        checked_items = new ArrayList<>();

        for (int i = 0; i < days_data.size(); i++) {
            Map<String,CheckBox> m = new HashMap<>();
            CheckBox cb = new CheckBox(getApplicationContext());
            //cb.setText("00:00 - 8:00");
            cb.setFocusable(false);
            cb.setClickable(false);
            m.put(getShiftDayAndLetter(i,0),cb);
            CheckBox cb2 = new CheckBox(getApplicationContext());
           // cb2.setText("8:00 - 16:00");
            cb2.setFocusable(false);
            cb2.setClickable(false);
            m.put(getShiftDayAndLetter(i,1),cb2);
            CheckBox cb3 = new CheckBox(getApplicationContext());
           // cb3.setText("16:00 - 00:00");
            cb3.setFocusable(false);
            cb3.setClickable(false);
            m.put(getShiftDayAndLetter(i,2),cb3);
            timeslots_data.add(m);

            Map<String,Boolean> shifts_init = new HashMap<>();
            for (int j = 0; j < 3; j++) {
                shifts_init.put(getShiftDayAndLetter(i,j), false);
            }
            checked_items.add(shifts_init);
        }

    }

    private String getShiftDayAndLetter(int group_position, int child_position) {
        String day = null,letter = null;
        switch (group_position) {
            case 0: day = "Sunday_"; break;
            case 1: day = "Monday_"; break;
            case 2: day = "Tuesday_"; break;
            case 3: day = "Wednesday_"; break;
            case 4: day = "Thursday_"; break;
            case 5: day = "Friday_"; break;
            case 6: day = "Saturday_"; break;
        }
        switch (child_position) {
            case 0: letter = "a"; break;
            case 1: letter = "b"; break;
            case 2: letter = "c"; break;
        }
        return day + letter;
    }

}
