package com.technion.shiftlyapp.shiftly.scheduleView;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.technion.shiftlyapp.shiftly.R;
import com.technion.shiftlyapp.shiftly.utility.Constants;
import com.technion.shiftlyapp.shiftly.utility.CustomSnackbar;
import com.venmo.view.TooltipView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScheduleEditActivity extends AppCompatActivity implements OnSpinnerChangeListener {

    private RecyclerView mRecyclerView;
    private ShiftsListAdapter mAdapter;
    private Context context;
    private List<Long> shift_nums;
    private List<String> days;
    private List<String> start_times;
    private List<String> end_times;
    private List<String> employees_names;
    private List<String> employees_list;
    private String groupId;
    private String employeeId;
    private DatabaseReference groupsRef;
    private CustomSnackbar mSnackbar;
    private DatabaseReference databaseRef;
    private Map<String, String> employeeIdByName;
    private OnSpinnerChangeListener onSpinnerChangeListener;


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

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

        databaseRef = FirebaseDatabase.getInstance().getReference().child("Groups").child(groupId);

        // Configuring RecyclerView with A LinearLayout and adding dividers
        initializeRecyclerAnimation();

        shift_nums = new ArrayList<>();
        days = new ArrayList<>();
        start_times = new ArrayList<>();
        end_times = new ArrayList<>();
        employees_names = new ArrayList<>();
        employees_list = new ArrayList<>();
        employeeIdByName = new HashMap<>();

        // interface method that gets called when a spinner is changed in the adapter
        onSpinnerChangeListener = (index, employee_name) -> employees_names.set(index, employee_name);

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

        // Future change: call the agenda's readData method
        readData((employees_per_shift, days_num, shift_length, shifts_per_day, starting_time) -> {
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
                employees_list.add("");
            }

            mAdapter = new ShiftsListAdapter(context, days, start_times, end_times, employees_names, employees_list, onSpinnerChangeListener);
            mRecyclerView.setAdapter(mAdapter);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
            mAdapter.notifyDataSetChanged();


        });

        // Saving the changed schedule and uploading it to the DB
        Button saveButton = findViewById(R.id.save_button);
        saveButton.setOnClickListener(view -> {
            // Push schedule to DB

            databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Map<String, Object> schedule_map = new HashMap<>();
                    int i = 0;
                    for (String updated_employee_name : employees_names) {
                        schedule_map.put(Integer.toString(i), getEmployeeIdByName(updated_employee_name));
                        i++;
                    }
                    Map<String, Object> schedule_map_of_db = new HashMap<>();
                    schedule_map_of_db.put("schedule", schedule_map);

                    databaseRef.updateChildren(schedule_map_of_db);

                    Toast.makeText(ScheduleEditActivity.this, R.string.schedule_updated_text, Toast.LENGTH_LONG).show();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            finish();

        });

        // Canceling the changed schedule and discarding the changes
        Button cancelButton = findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(view -> {
            // Cancel action
            finish();

        });

    }

    private String getEmployeeIdByName(String updated_employee_name) {
        return employeeIdByName.get(updated_employee_name);
    }

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

                for (DataSnapshot postSnapshot : dataSnapshot.child(groupId).child("members").getChildren()) {
                    employees_list.add((String) postSnapshot.getValue());
                    employeeIdByName.put((String) postSnapshot.getValue(), postSnapshot.getKey());
                }
                // Adding the N/A as a member in the group
                employeeIdByName.put(Constants.NA, Constants.NA);

                employees_list.add(Constants.NA);

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

    @Override
    public void onSpinnerChange(int index, String employee_name) {
        return;
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
        return String.valueOf((Long.valueOf(starting_time) + (shift_length * shift) + shift_length) % 24L);
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





