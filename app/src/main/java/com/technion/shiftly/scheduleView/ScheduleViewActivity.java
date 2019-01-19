package com.technion.shiftly.scheduleView;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.technion.shiftly.R;
import com.technion.shiftly.algorithm.ShiftSchedulingSolver;
import com.technion.shiftly.options.OptionsListActivity;
import com.technion.shiftly.utility.CustomSnackbar;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ScheduleViewActivity extends AppCompatActivity {

    private ConstraintLayout mLayout;
    private DatabaseReference databaseRef;
    private CustomSnackbar mSnackbar;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment = null;
            switch (item.getItemId()) {
                case R.id.navigation_daily:
                    fragment = new DailyViewFragment();
                    break;
                case R.id.navigation_weekly:
                    fragment = new WeeklyViewFragment();
                    break;
            }
            return launchFragment(fragment);
        }
    };

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_view);
        Toolbar schedule_view_toolbar = (Toolbar) findViewById(R.id.schedule_view_toolbar);
        setSupportActionBar(schedule_view_toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mLayout = (ConstraintLayout) findViewById(R.id.container_schedule_view);
        mSnackbar = new CustomSnackbar(CustomSnackbar.SNACKBAR_DEFAULT_TEXT_SIZE);
        final String group_id = getIntent().getExtras().getString("GROUP_ID");

        // TODO: Make available only if the user is not the admin, otherwise don't present the button
        // TODO: or make it present a toast saying "you are not an employee"
        com.getbase.floatingactionbutton.FloatingActionButton optionsFab = findViewById(R.id.options_fab);
        optionsFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), OptionsListActivity.class);
                intent.putExtra("GROUP_ID", group_id);
                startActivity(intent);
            }
        });

        com.getbase.floatingactionbutton.FloatingActionButton scheduleFab = findViewById(R.id.schedule_fab);
        scheduleFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Pull data from db
                databaseRef = FirebaseDatabase.getInstance().getReference().child("Groups").child(group_id);
                databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        LinkedHashMap<String, String> group_options = new LinkedHashMap<>();
                        for (DataSnapshot postSnapshot : dataSnapshot.child("options").getChildren()) {
                            group_options.put(postSnapshot.getKey(), postSnapshot.getValue().toString());
                        }

                        String employees_per_shift = (dataSnapshot.child("employees_per_shift").getValue()).toString();
                        // Run scheduling algorithm
                        ShiftSchedulingSolver solver = new ShiftSchedulingSolver(group_options);
                        Boolean result = solver.solve();
                        if (result) {

                            // Present a snackbar of "Schedule generated!" (success)
                            mSnackbar.show(ScheduleViewActivity.this, mLayout, getResources().getString(R.string.schedule_generation_success), CustomSnackbar.SNACKBAR_SUCCESS,Snackbar.LENGTH_SHORT);

                            // Upload schedule to DB
                            List<String> generated_schedule = solver.getFinal_schedule();
                            Map<String, Object> schedule_map = new HashMap<>();
                            schedule_map.put("schedule", generated_schedule);
                            databaseRef.updateChildren(schedule_map);

                        } else {
                            // Present a snackbar of "No schedule could be generated" (error)
                            mSnackbar.show(ScheduleViewActivity.this, mLayout, getResources().getString(R.string.schedule_generation_error), CustomSnackbar.SNACKBAR_ERROR,Snackbar.LENGTH_SHORT);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        launchFragment(new DailyViewFragment());
    }

    private boolean launchFragment(Fragment fragment) {
        if (fragment == null) {
            return false;
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
        return true;
    }
}