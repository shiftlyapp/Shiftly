package com.technion.shiftly.scheduleView;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.technion.shiftly.R;
import com.technion.shiftly.algorithm.ShiftSchedulingSolver;
import com.technion.shiftly.options.OptionsListActivity;
import com.technion.shiftly.utility.CustomSnackbar;
import com.venmo.view.TooltipView;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ScheduleViewActivity extends AppCompatActivity {

    private ConstraintLayout mLayout;
    private DatabaseReference databaseRef;
    private CustomSnackbar mSnackbar;
    private FirebaseAuth mAuth;
    private String group_id;
    private BottomNavigationView navigationView;

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
                case R.id.navigation_agenda:
                    fragment = new AgendaViewFragment();
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.schedule_menu, menu);
        return true;
    }

    private void copyGroupIDToClipboard() {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("group code", group_id);
        clipboard.setPrimaryClip(clipData);
        Toast.makeText(ScheduleViewActivity.this, R.string.copy_group_code_text, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_view);
        Toolbar schedule_view_toolbar = (Toolbar) findViewById(R.id.schedule_view_toolbar);
        setSupportActionBar(schedule_view_toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        navigationView = findViewById(R.id.bottom_navigation_schedule);
        schedule_view_toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch(item.getItemId()) {
                    case R.id.copy_to_clipboard_item:
                        copyGroupIDToClipboard();
                        break;
                }
                return true;
            }
        });
        mLayout = (ConstraintLayout) findViewById(R.id.container_schedule_view);
        mSnackbar = new CustomSnackbar(CustomSnackbar.SNACKBAR_DEFAULT_TEXT_SIZE);
        mAuth = FirebaseAuth.getInstance();

        group_id = getIntent().getExtras().getString("GROUP_ID");
        databaseRef = FirebaseDatabase.getInstance().getReference().child("Groups").child(group_id);
        final FloatingActionButton optionsFab = findViewById(R.id.options_fab);
        final FloatingActionButton scheduleFab = findViewById(R.id.schedule_fab);
        final TooltipView options_tooltip = findViewById(R.id.options_tooltip);
        options_tooltip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.setVisibility(View.GONE);
            }
        });
        final TooltipView schedule_tooltip = findViewById(R.id.schedule_tooltip);
        schedule_tooltip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.setVisibility(View.GONE);
            }
        });

        databaseRef.child("admin").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String admin_uuid = dataSnapshot.getValue().toString();
                String logged_in_user_uuid = mAuth.getCurrentUser().getUid();

                if (admin_uuid.equals(logged_in_user_uuid)) {
                    scheduleFab.show();
                    schedule_tooltip.setVisibility(View.VISIBLE);
                    navigationView.getMenu().removeItem(R.id.navigation_agenda);
                } else {
                    optionsFab.show();
                    options_tooltip.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        optionsFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), OptionsListActivity.class);
                intent.putExtra("GROUP_ID", group_id);
                startActivity(intent);
            }
        });

        scheduleFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Pull data from db
                databaseRef = FirebaseDatabase.getInstance().getReference().child("Groups").child(group_id);
                databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        LinkedHashMap<String, String> group_options = new LinkedHashMap<>();
                        if (!dataSnapshot.child("options").exists()
                                || dataSnapshot.child("options").getChildrenCount() < (Long)dataSnapshot.child("members_count").getValue()) {
                            mSnackbar.show(ScheduleViewActivity.this, mLayout, getResources().getString(R.string.schedule_no_options), CustomSnackbar.SNACKBAR_ERROR, Snackbar.LENGTH_SHORT);
                        } else {
                            for (DataSnapshot postSnapshot : dataSnapshot.child("options").getChildren()) {
                                group_options.put(postSnapshot.getKey(), postSnapshot.getValue().toString());
                            }

                            String employees_per_shift = (dataSnapshot.child("employees_per_shift").getValue()).toString();
                            // Run scheduling algorithm
                            ShiftSchedulingSolver solver = new ShiftSchedulingSolver(group_options, Integer.parseInt(employees_per_shift));
                            Boolean result = solver.solve();
                            if (result) {

                                // Present a snackbar of "Schedule generated!" (success)
                                mSnackbar.show(ScheduleViewActivity.this, mLayout, getResources().getString(R.string.schedule_generation_success), CustomSnackbar.SNACKBAR_SUCCESS, Snackbar.LENGTH_SHORT);
                                // Upload schedule to DB
                                List<String> generated_schedule = solver.getFinal_schedule();
                                Map<String, Object> schedule_map = new HashMap<>();
                                schedule_map.put("schedule", generated_schedule);
                                databaseRef.updateChildren(schedule_map);

                            } else {
                                // Present a snackbar of "No schedule could be generated" (error)
                                mSnackbar.show(ScheduleViewActivity.this, mLayout, getResources().getString(R.string.schedule_generation_error), CustomSnackbar.SNACKBAR_ERROR, Snackbar.LENGTH_SHORT);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

        navigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
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