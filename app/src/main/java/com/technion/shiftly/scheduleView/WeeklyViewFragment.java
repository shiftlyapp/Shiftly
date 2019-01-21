package com.technion.shiftly.scheduleView;

import android.graphics.RectF;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
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
import java.util.List;

public class WeeklyViewFragment extends Fragment {

    private String groupId;
//    protected View v;
//    protected WeekView mWeekView;
    private List<WeekViewEvent> events;

    private DatabaseReference datatbaseref;


    protected void createEvent(int id, String employeeName, int newYear, int newMonth, int day,
                               int hour, int duration) {
        // create the event and add it to the calendar
        Calendar startTime = Calendar.getInstance();
        startTime.set(Calendar.HOUR_OF_DAY, hour);
        startTime.set(Calendar.MINUTE, 0);
        startTime.set(Calendar.DAY_OF_WEEK, day);
        startTime.set(Calendar.MONTH, newMonth - 1);
        startTime.set(Calendar.YEAR, newYear);
        Calendar endTime = (Calendar) startTime.clone();
        endTime.add(Calendar.HOUR, duration);
        endTime.set(Calendar.DAY_OF_WEEK, day);
        endTime.set(Calendar.MONTH, newMonth - 1);
        WeekViewEvent event = new WeekViewEvent(id, employeeName, startTime, endTime);
        event.setColor(getResources().getColor(R.color.orange_color));
        events.add(event);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Get a reference for the week view in the layout.
        events = new ArrayList<>();
        View v = inflater.inflate(R.layout.fragment_weekly_view, container, false);
        WeekView mWeekView = (WeekView)v.findViewById(R.id.weekView);
//        v = inflater.inflate(R.layout.fragment_weekly_view, container, false);
//        mWeekView = (WeekView)v.findViewById(R.id.weekView);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getResources().getString(R.string.weekly_label));

        // gets the passed group id from parent
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            groupId = bundle.getString("group_id");
        }


        // Set an action when any event is clicked.
        WeekView.EventClickListener mEventClickListener = new WeekView.EventClickListener() {
            @Override
            public void onEventClick(WeekViewEvent event, RectF eventRect) {

            }
        };


        // now that we have the names of the workers of this week,
        // we can fill the calendar:
        mWeekView = (WeekView)v.findViewById(R.id.weekView);

        MonthLoader.MonthChangeListener mMonthChangeListener = new MonthLoader.MonthChangeListener() {
            @Override
            public List<WeekViewEvent> onMonthChange(int newYear, int newMonth) {
                // Initialize the parameters required for the calendar
//                CountDownLatch done = new CountDownLatch(1);
//                ScheduleRetriever retriever = new ScheduleRetriever(groupId);
//
////                retriever.getData();
////                int initHour = 8; //TODO: pull from db
////                int duration = 24/retriever.getNumOfShifts().intValue(); //TODO: pull from db
//                int count = 0;
//                int currHour = retriever.getInitHour();
//                int currDay = 0;
//
//                for (String employeeName : retriever.getEmployeeNamesList()) {
//                    // Check if we maxed out on the employees in one shift
//                    if (count % retriever.getNumOfEmployeesPerShifts().intValue() == 0) {
//                        currHour += retriever.getDuration();
//                    }
//                    // Check if we maxed out on the employees in one day
//                    if (count % retriever.getNumOfShifts().intValue() == 0) {
//                        currHour = retriever.getInitHour();
//                        currDay++;
//                    }
//
//                    createEvent(count, employeeName, newYear, newMonth, currDay,
//                            currHour, retriever.getDuration()); // add the event to the calendar.
//                }
                boolean result = false;
                getEvenets(newYear, newMonth);

                while (!result) {
                    if (events.size() > 0) {
                        result = true;
                    }
                }
                return events;
            }
        };

        mWeekView.setMonthChangeListener(mMonthChangeListener);


        return v;
    }

    private void getEvenets(final int newYear, final int newMonth) {
//        final List<WeekViewEvent> mNewEvenets = new ArrayList<>();

        datatbaseref = FirebaseDatabase.getInstance().getReference();

        datatbaseref.child("Groups").child(groupId).child("schedule").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot employee : dataSnapshot.getChildren()) {
                    Calendar startTime = Calendar.getInstance();
                    startTime.set(Calendar.HOUR_OF_DAY, 8);
                    startTime.set(Calendar.MINUTE, 0);
                    startTime.set(Calendar.DAY_OF_WEEK, 1);
                    startTime.set(Calendar.MONTH, newMonth);
                    startTime.set(Calendar.YEAR, newYear);

                    Calendar endTime = Calendar.getInstance();
                    endTime.set(Calendar.HOUR_OF_DAY, 10);
                    endTime.set(Calendar.MINUTE, 0);
                    endTime.set(Calendar.DAY_OF_WEEK, 1);
                    endTime.set(Calendar.MONTH, newMonth);
                    endTime.set(Calendar.YEAR, newYear);
//        Calendar endTime = (Calendar) startTime.clone();
//        endTime.add(Calendar.HOUR, 3);
//        endTime.set(Calendar.DAY_OF_WEEK, 1);
//        endTime.set(Calendar.MONTH, newMonth - 1);

                    WeekViewEvent event = new WeekViewEvent(1, "Yakir", startTime, endTime);

                    events.add(event);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

//        return mNewEvenets;



    }
}
