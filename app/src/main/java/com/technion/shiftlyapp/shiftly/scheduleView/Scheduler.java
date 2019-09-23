package com.technion.shiftlyapp.shiftly.scheduleView;

import android.content.Context;

import com.alamkanak.weekview.MonthLoader;
import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewEvent;
import com.technion.shiftlyapp.shiftly.dataAccessLayer.DataAccess;
import com.technion.shiftlyapp.shiftly.dataTypes.Group;
import com.technion.shiftlyapp.shiftly.utility.Constants;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Scheduler {
    private String groupId;
    private WeekView mWeekView;
    private List<WeekViewEvent> events;
    private List<String> employeeNamesList;
    private Map<String, Integer> employeeColors;
    private int steps = 0;
    private Context context;
    private DataAccess dataAccess = new DataAccess();
    private Group group;

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

    Scheduler(WeekView mWeekView, String groupId, int[] mColors, int num_of_days_to_show, Context context) {
        this.mWeekView = mWeekView;
        this.mWeekView.setNumberOfVisibleDays(num_of_days_to_show);
        this.groupId = groupId;
        this.mColors = mColors;
        this.events = new ArrayList<>();
        this.context = context;
        this.group = new Group();

        Calendar cal = Calendar.getInstance();
        int current_year = cal.get(Calendar.YEAR);
        int current_month = cal.get(Calendar.MONTH);
        // Creating the events only once for the current week upon loading
        getEmployeeNamesAndColors(current_year, current_month);

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

    private void getEmployeeNamesAndColors(final int newYear, final int newMonth) {
        // Pull the ids of the scheduled employees from DB
        dataAccess.getGroup(groupId, new DataAccess.DataAccessCallback<Group>() {
            @Override
            public void onCallBack(Group g) {
                group = new Group(g);
                employeeNamesList = new ArrayList<>();
                employeeColors = new HashMap<>();
                int i = 0;
                for (String employeeId : group.getSchedule()) {
                    String employeeName = (employeeId.equals(Constants.NA)) ?
                            Constants.NA : group.getMembers().get(employeeId);
                    employeeNamesList.add(employeeName);
                    employeeColors.put(employeeName, mColors[i++ % mColors.length]);
                }
                getEvents(newYear, newMonth);
            }
        });
    }

    private void getEvents(int newYear, int newMonth) {
        // Initialize the parameters required for the calendar
        int init_hour = Integer.parseInt(group.getStarting_time());
        int count = 0;
        int duration_time = group.getShift_length().intValue();
        int currHour = init_hour;
        int currDay = 0;

        for (String employeeName : employeeNamesList) {
            // Check if we maxed out on the employees in one shift
            if (count % group.getEmployees_per_shift().intValue() == 0) {
                currHour += duration_time;
            }
            // Check if we maxed out on the employees in one day
            if (count % group.getShifts_per_day().intValue() == 0) {
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
        return (event.getStartTime().get(Calendar.YEAR) == year && event.getStartTime().get(Calendar.MONTH) == month)
                || (event.getEndTime().get(Calendar.YEAR) == year && event.getEndTime().get(Calendar.MONTH) == month);
    }
}
