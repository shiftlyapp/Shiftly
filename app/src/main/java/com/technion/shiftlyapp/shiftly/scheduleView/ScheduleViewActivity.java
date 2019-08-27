package com.technion.shiftlyapp.shiftly.scheduleView;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.technion.shiftlyapp.shiftly.R;
import com.technion.shiftlyapp.shiftly.algorithm.ShiftSchedulingSolver;
import com.technion.shiftlyapp.shiftly.options.OptionsListActivity;
import com.technion.shiftlyapp.shiftly.optionsView.OptionsViewActivity;
import com.technion.shiftlyapp.shiftly.utility.CustomSnackbar;
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

    private int starting_time;
    private int shift_length;
    private int shifts_per_day;
    private int days_num;
    private int workers_in_shift;

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

    private LinkedHashMap<String, String> getGroupOptions(@NonNull DataSnapshot dataSnapshot) {
        // Get the members in the group
        LinkedHashMap<String, String> group_options = new LinkedHashMap<>();
        if (!dataSnapshot.child("options").exists()) {
            // Case no options are recorded
            return null;
        } else {
            // For each member, get the options
            for (DataSnapshot postSnapshot : dataSnapshot.child("options").getChildren()) {
                group_options.put(postSnapshot.getKey(), postSnapshot.getValue().toString());
            }
        }
        return group_options;
    }

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
        final FloatingActionButton viewOptionsFab = findViewById(R.id.view_options_fab);

        final FloatingActionsMenu menuFab = findViewById(R.id.menu_fab);
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
        final TooltipView view_options_tooltip = findViewById(R.id.view_options_tooltip);
        view_options_tooltip.setOnClickListener(new View.OnClickListener() {
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
                    navigationView.getMenu().removeItem(R.id.navigation_agenda);

                    scheduleFab.setVisibility(View.VISIBLE);
                    scheduleFab.setIcon(R.drawable.ic_generate_schedule_fab);
                    scheduleFab.setTitle("Create schedule");

                    viewOptionsFab.setVisibility(View.VISIBLE);
                    viewOptionsFab.setIcon(R.drawable.ic_view_options);
                    viewOptionsFab.setTitle("View options");

                    menuFab.setVisibility(View.VISIBLE);

                    schedule_tooltip.setVisibility(View.VISIBLE);
                    view_options_tooltip.setVisibility(View.VISIBLE);

                } else {
                    optionsFab.setVisibility(View.VISIBLE);
                    optionsFab.setIcon(R.drawable.ic_edit_timeslots_fab);
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

        viewOptionsFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                // Pull data from db
                databaseRef = FirebaseDatabase.getInstance().getReference().child("Groups").child(group_id);
                databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        LinkedHashMap<String, String> group_options = getGroupOptions(dataSnapshot);
                        if (group_options == null) {
                            // Case no options are recorded
                            mSnackbar.show(ScheduleViewActivity.this, mLayout,
                                    getResources().getString(R.string.view_options_no_options),
                                    CustomSnackbar.SNACKBAR_ERROR, Snackbar.LENGTH_SHORT);
                        } else {
                            starting_time = Integer.parseInt(dataSnapshot.child("starting_time").getValue().toString());
                            shift_length = Integer.parseInt(dataSnapshot.child("shift_length").getValue().toString());
                            shifts_per_day = Integer.parseInt(dataSnapshot.child("shifts_per_day").getValue().toString());
                            days_num = Integer.parseInt(dataSnapshot.child("days_num").getValue().toString());
                            workers_in_shift = Integer.parseInt(dataSnapshot.child("employees_per_shift").getValue().toString());
                            String group_name = dataSnapshot.child("group_name").getValue().toString();

                            // Collect the group members map
                            LinkedHashMap<String, String> group_members = new LinkedHashMap<>();
                            // Collect the members that are yet to send options
                            String workers_without_options = "";

                            for (DataSnapshot postSnapshot : dataSnapshot.child("members").getChildren()) {
                                group_members.put(postSnapshot.getKey(), postSnapshot.getValue().toString());
                                if (!group_options.containsKey(postSnapshot.getKey().toString())) {
                                    workers_without_options += (postSnapshot.getValue().toString() + "\n");
                                }
                            }

                            // Switch uuids with names
                            HashMap<String, String> options = new HashMap<>();
                            for (LinkedHashMap.Entry<String, String> entry : group_options.entrySet()) {
                                options.put(group_members.get(entry.getKey()), entry.getValue());
                            }

                            // Pass the member's related options to the next activity
                            Intent intent = new Intent(view.getContext(), OptionsViewActivity.class);

                            intent.putExtra("GROUP_NAME", group_name);
                            intent.putExtra("STARTING_TIME", starting_time);
                            intent.putExtra("SHIFT_LENGTH", shift_length);
                            intent.putExtra("SHIFTS_PER_DAY", shifts_per_day);
                            intent.putExtra("DAYS_NUM", days_num);
                            intent.putExtra("WORKERS_IN_SHIFT", workers_in_shift);
                            intent.putExtra("WORKERS_WITHOUT_OPTIONS", workers_without_options);

                            intent.putExtra("OPTIONS", options);
                            startActivity(intent);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

        scheduleFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Confirmation dialog before generating
                AlertDialog.Builder builder = new AlertDialog.Builder(ScheduleViewActivity.this, R.style.CustomAlertDialog);
                builder.setMessage(R.string.generate_schedule_dialog);
                builder.setCancelable(true);
                builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        generateSchedule();
                    }
                });
                builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                AlertDialog generate_schedule_dialog = builder.create();
                generate_schedule_dialog.show();
            }
        });

        navigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        launchFragment(new DailyViewFragment());
    }

    private void generateSchedule() {
        // Pull data from db
        databaseRef = FirebaseDatabase.getInstance().getReference().child("Groups").child(group_id);
        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                LinkedHashMap<String, String> group_options = getGroupOptions(dataSnapshot);
                if (group_options == null ||
                        group_options.size() < (Long)dataSnapshot.child("members_count").getValue()) {
                    mSnackbar.show(ScheduleViewActivity.this, mLayout, getResources().getString(R.string.schedule_no_options), CustomSnackbar.SNACKBAR_ERROR, Snackbar.LENGTH_SHORT);
                } else {
                    String employees_per_shift = (dataSnapshot.child("employees_per_shift").getValue()).toString();
                    // Run scheduling algorithm
                    ShiftSchedulingSolver solver = new ShiftSchedulingSolver(group_options, Integer.parseInt(employees_per_shift));
                    Boolean result = solver.solve();
                    // result is always true

                    List<String> generated_schedule = solver.getFinal_schedule();
                    System.out.println(generated_schedule.size());
                    Map<String, Object> schedule_map = new HashMap<>();
                    schedule_map.put("schedule", generated_schedule);
                    databaseRef.updateChildren(schedule_map);

                    if (generated_schedule.contains("null")) {
                        // Present a snackbar of "A full schedule could not be created" (warning)
                        mSnackbar.show(ScheduleViewActivity.this, mLayout, getResources().getString(R.string.schedule_generation_error), CustomSnackbar.SNACKBAR_ERROR, Snackbar.LENGTH_SHORT);

                    } else {
                        // Present a snackbar of "Schedule generated!" (success)
                        mSnackbar.show(ScheduleViewActivity.this, mLayout, getResources().getString(R.string.schedule_generation_success), CustomSnackbar.SNACKBAR_SUCCESS, Snackbar.LENGTH_SHORT);
                        // Upload schedule to DB
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private boolean launchFragment(Fragment fragment) {
        if (fragment == null) {
            return false;
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
        return true;
    }
}