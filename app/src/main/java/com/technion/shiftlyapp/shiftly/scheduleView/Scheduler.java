package com.technion.shiftlyapp.shiftly.scheduleView;

import androidx.annotation.NonNull;

import com.alamkanak.weekview.MonthLoader;
import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewEvent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Scheduler {

    private String groupId;
    private WeekView mWeekView;
    private List<WeekViewEvent> events;
    private Long numOfShifts;
    private List<String> employeeNamesList;
    private Long numOfEmployeesPerShifts;
    private int initHour;
    private Long duration;
    private Map<String, Integer> employeeColors;
    private int steps = 0;

    private int[] mColors;

    public WeekView getmWeekView() {
        return mWeekView;
    }

    public void setmWeekView(WeekView mWeekView) {
        this.mWeekView = mWeekView;
    }

    public List<WeekViewEvent> getEvents() {
        return events;
    }

    public void setEvents(List<WeekViewEvent> events) {
        this.events = events;
    }

    Scheduler(WeekView mWeekView, String groupId, int[] mColors, int num_of_days_to_show) {
        this.mWeekView = mWeekView;
        this.mWeekView.setNumberOfVisibleDays(num_of_days_to_show);
        this.groupId = groupId;
        this.mColors = mColors;
        this.events = new ArrayList<>();

        Calendar cal = Calendar.getInstance();
        int current_year = cal.get(Calendar.YEAR);
        int current_month = cal.get(Calendar.MONTH);
        // Creating the events only once for the current week upon loading
        pullDatabaseData(current_year, current_month);

        MonthLoader.MonthChangeListener listener = new MonthLoader.MonthChangeListener() {
            @Override
            public List<WeekViewEvent> onMonthChange(int newYear, int newMonth) {
                List<WeekViewEvent> matchedEvents = new ArrayList<>();
                for (WeekViewEvent event : events) {
                    if (eventMatches(event, newYear, newMonth)) {
                        matchedEvents.add(event);
                    }
                }
                return matchedEvents;
            }
        };
        this.mWeekView.setMonthChangeListener(listener);
    }

    private void pullDatabaseData(final int newYear, final int newMonth) {
        // Pull the ids of the scheduled employees from DB
        DatabaseReference mGroupDatabase = FirebaseDatabase.getInstance()
                .getReference("Groups").child(groupId); // a reference the the group

        mGroupDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Load the weekly schedule parameters
                numOfShifts = (Long) dataSnapshot.child("shifts_per_day").getValue();
                numOfEmployeesPerShifts = (Long) dataSnapshot.child("employees_per_shift").getValue();
                initHour = Integer.parseInt(dataSnapshot.child("starting_time").getValue().toString());
                duration = ((Long) dataSnapshot.child("shift_length").getValue());
                // Load the ids of the employees from the schedule
                employeeNamesList = new ArrayList<>();
                employeeColors = new HashMap<>();
                int x = 0;
                for (DataSnapshot current_employee : dataSnapshot.child("schedule").getChildren()) {
                    String employeeId = current_employee.getValue(String.class);
                    String employeeName = (employeeId.equals("null")) ? "N/A" : dataSnapshot.child("members").child(employeeId).getValue().toString();
                    employeeNamesList.add(employeeName);
                    employeeColors.put(employeeName, mColors[x++ % mColors.length]);
                }
                getEvents(newYear, newMonth);
            }
        });
    }

    private void getEvents(int newYear, int newMonth) {
        // Initialize the parameters required for the calendar
        int init_hour = initHour;
        int count = 0;
        int duration_time = duration.intValue();
        int currHour = init_hour;
        int currDay = 0;

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

    private void createEvent(int id, String employeeName, int newYear, int newMonth, int day,
                             int hour, int duration) {

        // create the event and add it to the calendar
        Calendar startTime = Calendar.getInstance();
        startTime.set(Calendar.HOUR_OF_DAY, hour);
        startTime.set(Calendar.MINUTE, 0);
        startTime.set(Calendar.DAY_OF_WEEK, day);
        startTime.set(Calendar.MONTH, newMonth);
        startTime.set(Calendar.YEAR, newYear);

        Calendar endTime = (Calendar) startTime.clone();
        endTime.set(Calendar.HOUR_OF_DAY, hour + duration);
        endTime.set(Calendar.MINUTE, 0);
        endTime.set(Calendar.DAY_OF_WEEK, day);
        endTime.set(Calendar.MONTH, newMonth);
        endTime.set(Calendar.YEAR, newYear);

        WeekViewEvent event = new WeekViewEvent(id, employeeName, startTime, endTime);
        event.setColor(employeeColors.get(employeeName));
        events.add(event);
        if (steps == 0) {
            mWeekView.notifyDatasetChanged();
            steps++;
        }
    }

    private boolean eventMatches(WeekViewEvent event, int year, int month) {
        return (event.getStartTime().get(Calendar.YEAR) == year && event.getStartTime().get(Calendar.MONTH) == month) || (event.getEndTime().get(Calendar.YEAR) == year && event.getEndTime().get(Calendar.MONTH) == month);
    }
}
