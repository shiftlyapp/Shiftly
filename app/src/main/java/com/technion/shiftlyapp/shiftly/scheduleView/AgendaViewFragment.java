package com.technion.shiftlyapp.shiftly.scheduleView;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.technion.shiftlyapp.shiftly.R;

import java.util.ArrayList;
import java.util.List;

public class AgendaViewFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private ShiftsListAdapter mAdapter;
    private Context context;
    private List<Long> shift_nums;
    private List<String> days;
    private List<String> start_times;
    private List<String> end_times;
    private List<String> employees_ids;
    private List<String> employees_list;
    private String groupId;
    private String employeeId;
    private DatabaseReference groupsRef;
    private OnSpinnerChangeListener onSpinnerChangeListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_agenda_view, container, false);
        context = inflater.getContext();

        mRecyclerView = view.findViewById(R.id.users_shifts);

        groupId = getActivity().getIntent().getExtras().getString("GROUP_ID");
        employeeId = getActivity().getIntent().getExtras().getString("EMPLOYEE_ID");

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getResources().getString(R.string.agenda_label));

        // Configuring RecyclerView with A LinearLayout and adding dividers
        initializeRecyclerAnimation();

        shift_nums = new ArrayList<>();
        days = new ArrayList<>();
        start_times = new ArrayList<>();
        end_times = new ArrayList<>();
        employees_ids = new ArrayList<>();
        employees_list = new ArrayList<>();

        onSpinnerChangeListener = new OnSpinnerChangeListener() {
            @Override
            public void onSpinnerChange(int index, String employee_name) {
                return;
            }
        };

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference firebaseRootRef = firebaseDatabase.getReference();
        groupsRef = firebaseRootRef.child("Groups");

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
                mAdapter = new ShiftsListAdapter(context, days, start_times, end_times, employees_ids, employees_list, onSpinnerChangeListener);
                mAdapter.notifyDataSetChanged();
                mRecyclerView.setAdapter(mAdapter);
                mRecyclerView.setLayoutManager(new LinearLayoutManager(context));

            }
        });

        return view;

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
                    if (postSnapshot.getValue().equals(employeeId)) {
                        shift_nums.add(counter);
                    }
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





