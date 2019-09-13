package com.technion.shiftlyapp.shiftly.scheduleView;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.technion.shiftlyapp.shiftly.R;
import com.technion.shiftlyapp.shiftly.utility.CustomSnackbar;
import com.venmo.view.TooltipView;

import java.util.ArrayList;
import java.util.List;

public class ScheduleEditActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private ShiftsListAdapter mAdapter;
    private Context context;
    private List<Long> shift_nums;
    private List<String> days;
    private List<String> start_times;
    private List<String> end_times;
    private List<String> employees_names;
    private String groupId;
    private String employeeId;
    private DatabaseReference groupsRef;
    private CustomSnackbar mSnackbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_edit);

        Toolbar schedule_edit_toolbar = findViewById(R.id.schedule_edit_toolbar);

        setSupportActionBar(schedule_edit_toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.edit_schedule_label));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        context = getApplicationContext();

        mSnackbar = new CustomSnackbar(CustomSnackbar.SNACKBAR_DEFAULT_TEXT_SIZE);

        mRecyclerView = findViewById(R.id.group_shifts);

        groupId = getIntent().getExtras().getString("GROUP_ID");


        // Configuring RecyclerView with A LinearLayout and adding dividers
        initializeRecyclerAnimation();

        shift_nums = new ArrayList<>();
        days = new ArrayList<>();
        start_times = new ArrayList<>();
        end_times = new ArrayList<>();
        employees_names = new ArrayList<>();

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference firebaseRootRef = firebaseDatabase.getReference();
        groupsRef = firebaseRootRef.child("Groups");


        final TooltipView schedule_tooltip = findViewById(R.id.schedule_edit_shifts_tooltip);
        schedule_tooltip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick (View view){
                view.setVisibility(View.GONE);
            }
        });

        readData(new FirebaseCallback() {
            @Override
            public void populateShiftsLists(Long employees_per_shift, Long days_num, Long shift_length, Long shifts_per_day, String starting_time) {
                for (int day = 0; day < days_num; day++) {

                    for (int shift = 0; shift < shifts_per_day; shift++) {

                        for (int sub_shift = 0; sub_shift < employees_per_shift; sub_shift++) {

                            Long current_shift_num = day * (shifts_per_day * employees_per_shift) + (shift * employees_per_shift) + sub_shift;

                            if (shift_nums.contains(current_shift_num)) {
                                days.add(num_to_day(day));
                                start_times.add(num_to_start_time(shift, shift_length, starting_time));
                                end_times.add(num_to_end_time(shift, shift_length, starting_time));
                            }
                        }
                    }
                }

                if (days.isEmpty()) {
                    days.add(context.getResources().getString(R.string.agenda_no_shifts_message));
                    start_times.add("");
                    end_times.add("");
                }
                mAdapter = new ShiftsListAdapter(context, days, start_times, end_times, employees_names);
                mAdapter.notifyDataSetChanged();
                mRecyclerView.setAdapter(mAdapter);
                mRecyclerView.setLayoutManager(new LinearLayoutManager(context));

            }
        });


        FloatingActionButton saveFab = findViewById(R.id.save_fab);
        saveFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Push schedule to DB

//                databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                        Map<String, Object> options_map = new HashMap<>();
//                        for (DataSnapshot postSnapshot : dataSnapshot.child("schedule").getChildren()) {
//                            options_map.put(postSnapshot.getKey(), postSnapshot.getValue());
//                        }
//                        String streched = strech_string_by_num_of_employees();
//                        schedule_map.put(currentUser.getUid(), streched);
//
//                        Map<String, Object> schedule_map_of_db = new HashMap<>();
//                        schedule_map_of_db.put("options", options_map);
//
//                        databaseRef.updateChildren(schedule_map_of_db);
//
//                        Toast.makeText(ScheduleEditActivity.this, R.string.schedule_updated_text, Toast.LENGTH_LONG).show();
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                    }
//                });
            }
        });

        FloatingActionButton cancelFab = findViewById(R.id.cancel_fab);
        cancelFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Cancel action
                finish();

            }
        });

    }

    // TODO leave only this
    private void readData(final FirebaseCallback firebaseCallback) {
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Long employees_per_shift = dataSnapshot.child(groupId).child("employees_per_shift").getValue(Long.class);
                Long days_num = dataSnapshot.child(groupId).child("days_num").getValue(Long.class);
                Long shift_length = dataSnapshot.child(groupId).child("shift_length").getValue(Long.class);
                Long shifts_per_day = dataSnapshot.child(groupId).child("shifts_per_day").getValue(Long.class);
                String starting_time = dataSnapshot.child(groupId).child("starting_time").getValue(String.class);

                Long counter = 0L;
                for (DataSnapshot postSnapshot : dataSnapshot.child(groupId).child("schedule").getChildren()) {
                    shift_nums.add(counter);
                    String employee_id = ((String) postSnapshot.getValue());
                    employees_names.add((String) dataSnapshot.child(groupId).child("members").child(employee_id).getValue());
                    counter++;
                }

                firebaseCallback.populateShiftsLists(employees_per_shift, days_num, shift_length,
                        shifts_per_day, starting_time);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("Error", databaseError.getMessage());
            }
        };

        groupsRef.addListenerForSingleValueEvent(valueEventListener);
    }

    private interface FirebaseCallback {
        void populateShiftsLists(Long employees_per_shift, Long days_num, Long shift_length,
                                 Long shifts_per_day, String starting_time);
    }

    private void initializeRecyclerAnimation() {
        final LayoutAnimationController controller =
                AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_fall_down);
        mRecyclerView.setLayoutAnimation(controller);
    }

    private String num_to_start_time(int shift, Long shift_length, String starting_time) {
        return String.valueOf(Long.valueOf(starting_time) + (shift_length * shift));
    }

    private String num_to_end_time(int shift, Long shift_length, String starting_time) {
        return String.valueOf(Long.valueOf(starting_time) + (shift_length * shift) + shift_length);
    }

    private String num_to_day(int day) {
        switch (day) {
            case 0: return context.getResources().getString(R.string.sunday);
            case 1: return context.getResources().getString(R.string.monday);
            case 2: return context.getResources().getString(R.string.tuesday);
            case 3: return context.getResources().getString(R.string.wednesday);
            case 4: return context.getResources().getString(R.string.thursday);
            case 5: return context.getResources().getString(R.string.friday);
            case 6: return context.getResources().getString(R.string.saturday);
            default: return "";
        }
    }
}





