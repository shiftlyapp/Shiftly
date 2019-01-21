package com.technion.shiftly.scheduleView;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ScheduleRetriever {
//    CountDownLatch done;
    DatabaseReference databasefer;
    private int initHour; //TODO: pull from db
    private int duration; //TODO: pull from db
    private Long numOfShifts;
    private Long numOfEmployeesPerShifts;
    private List<String> employeeNamesList;
    private String groupId;

    public ScheduleRetriever(final String groupId) {
//        this.done = done;
        employeeNamesList = new ArrayList<>();
        this.groupId = groupId;
        this.initHour = 8;

        // Pull the ids of the scheduled employees from DB
        final DatabaseReference mGroupDatabase = FirebaseDatabase.getInstance()
                .getReference("Groups").child(groupId); // a reference the the group

        mGroupDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Load the weekly schedule parameters

                numOfShifts = (Long)dataSnapshot.child("shifts_per_day").getValue();
                duration = 24/numOfShifts.intValue();
                numOfEmployeesPerShifts = (Long)dataSnapshot.child("employees_per_shift").getValue();

                // Load the ids of the employees from the schedule
                final List<String> employeeIdList = new ArrayList<>(); // Id's list ordered by the order of the shifts
                for (DataSnapshot current_employee : dataSnapshot.child("schedule").getChildren()) {
                    String employeeId = current_employee.getValue(String.class);
                    employeeIdList.add(employeeId); // employeeIdList is filled with the ids

//                    databasefer = FirebaseDatabase.getInstance().getReference("Users").child(employeeId);
//                    String fullName =
//                            databasefer.child("firstname").
//                                    + databasefer.child("lastname").toString();
//                    employeeNamesList.add(fullName); // employeeNamesList is filled with the names
                }
//
//
//                for (DataSnapshot currentUser : dataSnapshot.child("Users").getChildren() ) {
//                    if (employeeIdList.contains( currentUser.getKey() )) { //TODO: check if correct
//                        String fullName =
//                                currentUser.child("firstname").getValue().toString()
//                                        + currentUser.child("lastname").getValue().toString();
//                        employeeNamesList.add(fullName); // employeeNamesList is filled with the names
//                    }
//                }
//                Log.v("tag", "done");
//                System.out.print("kaka");

//                //  Load the employees names based on their ids from DB
//                DatabaseReference mUserDatabase = FirebaseDatabase.getInstance().getReference()
//                        .child("Users");
////                        .getReference("Users"); // a reference the the users
////                mGroupDatabase.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
//                mUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                        // Load the employees names
//                        Log.v("tag", "somthing");
//                        for (DataSnapshot currentUser : dataSnapshot.getChildren()) {
//                            if (employeeIdList.contains( currentUser.getKey() )) { //TODO: check if correct
//                                String fullName =
//                                        currentUser.child("firstname").getValue().toString()
//                                                + currentUser.child("lastname").getValue().toString();
//                                employeeNamesList.add(fullName); // employeeNamesList is filled with the names
//
//                            }
//                        }
////                        done.countDown();
//
//                    }
//
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                    }
//
//                });
                Log.v("tag", "done");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public int getInitHour() {
        return initHour;
    }

    public int getDuration() {
        return duration;
    }

    public Long getNumOfShifts() {
        return numOfShifts;
    }

    public Long getNumOfEmployeesPerShifts() {
        return numOfEmployeesPerShifts;
    }

    public List<String> getEmployeeNamesList() {
        return employeeNamesList;
    }

    public String getGroupId() {
        return groupId;
    }

    public void getData() {
    }
}
