package com.technion.shiftlyapp.shiftly.scheduleView;

import android.os.Bundle;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.technion.shiftlyapp.shiftly.R;

import java.util.ArrayList;

// Creates an activity, which contains the current schedule
public class ScheduleTableCreator extends AppCompatActivity {

    TableLayout scheduleTable;
    private Long numOfShifts;
    private Long numOfEmployeesPerShifts;
    private int initHour;
    private Long duration;
    private Long days_num;
    private String group_name;
    private ArrayList<String> employeeNamesList;
    String groupId;

    private void fetchSchedule() {
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
                days_num = ((Long) dataSnapshot.child("days_num").getValue());
                group_name = dataSnapshot.child("group_name").getValue().toString();
                // Load the ids of the employees from the schedule
                employeeNamesList = new ArrayList<>();
                for (DataSnapshot current_employee : dataSnapshot.child("schedule").getChildren()) {
                    String employeeId = current_employee.getValue(String.class);
                    String employeeName = (employeeId.equals("null")) ? "N/A" : dataSnapshot.child("members").child(employeeId).getValue().toString();

                    employeeNamesList.add(employeeName);
                }
            }
        });
    }

    /* --------------- Table Creation --------------- */
    private void createHeader(int rowLength) {
        TableRow header = new TableRow(ScheduleTableCreator.this);
        String[] days = getResources().getStringArray(R.array.week_days);
        for (int i=0 ; i<rowLength ; i++) {
            TextView text = new TextView(ScheduleTableCreator.this);
            if (i == 0) {
                text.setText("");
            } else {
                text.setText(days[i-1]);
            }
            text.setPadding(16, 8, 16, 8);
            text.setTextSize(20);
            TableRow.LayoutParams params = (TableRow.LayoutParams)text.getLayoutParams();
            // span the header to the correct number of shifts per day
            params.span = numOfEmployeesPerShifts.intValue();
            text.setLayoutParams(params);

            header.addView(text);
        }
        header.setBackground(getDrawable(R.drawable.table_cell_shape));
        scheduleTable.addView(header);
    }

    ArrayList<ArrayList<String>> parseScheduleToTable(int shifts_per_day, int days_num,
                                                      int workers_in_shift) {
        // Sets up the schedule list
        ArrayList<ArrayList<String>> scheduleByRows = new ArrayList<>();
        for (int i=0 ; i<shifts_per_day ; i++) {
            ArrayList<String> rowList = new ArrayList<>();
            for (int j=0; j<days_num ; j++) {
                rowList.add("");
            }
            scheduleByRows.add(rowList);
        }
        // Iterate over the schedule map and convert it to a list arranged by rows
        for (String entry : employeeNamesList) {
            int day;
            int shiftInDay;
            for (int i = 0 ; i < days_num*shifts_per_day ; i++) {

                // i'th shift => shift of the day is i%shifts_per_day
                shiftInDay = i%shifts_per_day*workers_in_shift;

                // day is i/shifts_per_day
                day = i/shifts_per_day*workers_in_shift;

                // Append the worker's name to the list of name
                scheduleByRows.get(shiftInDay).set(day, scheduleByRows.get(shiftInDay).get(day) + entry + "\n");

            }
        }
        return scheduleByRows;
    }


    private void createRow(ArrayList<String> arrayRow, int rowLength, int shiftStartingTime, int shiftLength) {
        TableRow row = new TableRow(ScheduleTableCreator.this);
        for (int i=0 ; i<rowLength ; i++) {
            TextView text = new TextView(ScheduleTableCreator.this);
            if (i == 0) {
                text.setText(String.format(getString(R.string.hour_format), shiftStartingTime < 10 ?
                        ("0" + shiftStartingTime) : (shiftStartingTime), ((shiftLength + shiftStartingTime) % 24) < 10 ?
                        "0" + ((shiftLength + shiftStartingTime) % 24) : ((shiftLength + shiftStartingTime) % 24)));
            } else {
                text.setText(arrayRow.get(i-1));
            }
            text.setPadding(16, 8, 16, 8);
            text.setTextSize(16);
            row.addView(text);
        }
        row.setBackground(getDrawable(R.drawable.table_cell_shape));
        scheduleTable.addView(row);
    }

    private void createTable(ArrayList<ArrayList<String>> table, int rowlength, int firstShiftStartingTime, int ShiftLength) {
        int count = 0;
        for (ArrayList<String> row : table) {
            // Shift starting time is calculated for each shift
            createRow(row, rowlength, (firstShiftStartingTime +(ShiftLength * count++))%24, ShiftLength);
        }
    }

    TableLayout getTable() {
        return scheduleTable;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_option_view);

        fetchSchedule();

        ArrayList<ArrayList<String>> optionsArray = parseScheduleToTable(
                numOfShifts.intValue(), days_num.intValue(), numOfEmployeesPerShifts.intValue());

        // Prepare the title to be presented
        TextView title = findViewById(R.id.options_view_title);
        title.setText(String.format(getString(R.string.options_view_title), group_name));


        scheduleTable = (TableLayout) findViewById(R.id.options_table);

        // Prepare the table's header
        createHeader(days_num.intValue() +1);

        // Prepare the options table itself
        createTable(optionsArray, (days_num.intValue() +1), initHour, duration.intValue());
    }
}
