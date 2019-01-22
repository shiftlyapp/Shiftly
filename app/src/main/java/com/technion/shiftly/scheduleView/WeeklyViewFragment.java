package com.technion.shiftly.scheduleView;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alamkanak.weekview.MonthLoader;
import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewEvent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.technion.shiftly.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WeeklyViewFragment extends Fragment{

    private String groupId;
    private WeekView mWeekView;
    private List<WeekViewEvent> events;
    private MonthLoader.MonthChangeListener mMonthChangeListener;
    private Long numOfShifts;
    private List<String> employeeNamesList;
    private Long numOfEmployeesPerShifts;
    private int initHour;
    private Long duration;
    private List<WeekViewEvent> eventsMonth;
    private Map<String,Integer> employeeColors;
    private int counter = 0;
    private int[] mColors = { Color.RED, Color.YELLOW, Color.BLUE, Color.GREEN , Color.LTGRAY };

    protected void createEvent(int id, String employeeName, int newYear, int newMonth, int day,
                               int hour, int duration) {
        // create the event and add it to the calendar
        Calendar startTime = Calendar.getInstance();
        startTime.set(Calendar.HOUR_OF_DAY, hour);
        startTime.set(Calendar.MINUTE, 0);
        startTime.set(Calendar.DAY_OF_WEEK, day);
        startTime.set(Calendar.MONTH, newMonth);
        startTime.set(Calendar.YEAR, newYear);

        Calendar endTime = (Calendar)startTime.clone();
        endTime.set(Calendar.HOUR_OF_DAY, hour + duration);
        endTime.set(Calendar.MINUTE, 0);
        endTime.set(Calendar.DAY_OF_WEEK, day);
        endTime.set(Calendar.MONTH, newMonth);
        endTime.set(Calendar.YEAR, newYear);

        WeekViewEvent event = new WeekViewEvent(id, employeeName, startTime, endTime);
        event.setColor(employeeColors.get(employeeName));
        events.add(event);
        if (counter ==0) {
            mWeekView.notifyDatasetChanged();
            counter++;
        }
    }

    private void pullDatabaseData(final int newYear, final int newMonth) {
        // Pull the ids of the scheduled employees from DB
        DatabaseReference mGroupDatabase = FirebaseDatabase.getInstance()
                .getReference("Groups").child(groupId); // a reference the the group

        mGroupDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Load the weekly schedule parameters
                numOfShifts = (Long) dataSnapshot.child("shifts_per_day").getValue();
                numOfEmployeesPerShifts = (Long) dataSnapshot.child("employees_per_shift").getValue();
                initHour = Integer.parseInt(dataSnapshot.child("starting_time").getValue().toString());
                duration = ((Long)dataSnapshot.child("shift_length").getValue());
                // Load the ids of the employees from the schedule
                employeeNamesList = new ArrayList<>();
                employeeColors = new HashMap<>();
                int x = 0;
                for (DataSnapshot current_employee : dataSnapshot.child("schedule").getChildren()) {
                    String employeeId = current_employee.getValue(String.class);
                    // Load the employees names based on their ids from DB
                    String employeeName = dataSnapshot.child("members").child(employeeId).getValue().toString();
                    employeeNamesList.add(employeeName);
                    employeeColors.put(employeeName,mColors[x++ % mColors.length]);
                }
                getEvents(newYear, newMonth);
            }
        });
    }

    public void getEvents(int newYear, int newMonth) {
        // Initialize the parameters required for the calendar
        int init_hour = initHour;
        int duration_time = duration.intValue();
        int count = 0;
        int currHour = init_hour;
        int currDay = 1;

        for (String employeeName : employeeNamesList) {
            // Check if we maxed out on the employees in one shift
            if (count % numOfEmployeesPerShifts.intValue() == 0) {
                currHour += duration_time;
            }
            // Check if we maxed out on the employees in one day
            if (count % numOfShifts.intValue() == 0) {
                currHour = init_hour;
                currDay++;
            }
            createEvent(count, employeeName, newYear, newMonth, currDay,
                    currHour, duration_time);
            // add the event to the calendar.
            count++;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Get a reference for the week view in the layout.
        View v = inflater.inflate(R.layout.fragment_weekly_view, container, false);
        groupId = getActivity().getIntent().getExtras().getString("GROUP_ID");
        mWeekView = (WeekView) v.findViewById(R.id.weekView);
        events = new ArrayList<>();
        eventsMonth = new ArrayList<>();

        mMonthChangeListener = new MonthLoader.MonthChangeListener() {
            @Override
            public List<? extends WeekViewEvent> onMonthChange(int newYear, int newMonth) {
                pullDatabaseData(newYear, newMonth);
//                for (int i = 0; i < events.size(); ++i) {
//                    if (events.get(i).getStartTime().get(Calendar.MONTH) == newMonth) {
//                        eventsMonth.add(events.get(i));
//                    }
//                }
                return events;
            }
        };
        mWeekView.setMonthChangeListener(mMonthChangeListener);

        //  ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getResources().getString(R.string.weekly_label));

        return v;
    }
}