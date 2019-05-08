package com.technion.shiftly.optionsView;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.technion.shiftly.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class OptionsViewActivity extends AppCompatActivity {

    TableLayout optionsTable;

    HashMap<String, String> group_options;

    ArrayList<ArrayList<String>> reorganizeOptionsForTable (HashMap<String, String> options,
                                                            int shifts_per_day, int days_num,
                                                            int workers_in_shift) {
        // Sets up the options list
        ArrayList<ArrayList<String>> optionsList = new ArrayList<>();
        for (int i=0 ; i<shifts_per_day ; i++) {
            ArrayList<String> rowList = new ArrayList<>();
            for (int j=0; j<days_num ; j++) {
                rowList.add("");
            }
            optionsList.add(rowList);
        }
        // Iterate over the options map and convert it to a list arranged by rows
        for (LinkedHashMap.Entry<String, String> entry : options.entrySet()) {
            int day;
            int shiftInDay;
            for (int i = 0 ; i < days_num*shifts_per_day ; i++) {
                // i'th shift =>
                // shift of the day is i%shifts_per_day
                shiftInDay = i%shifts_per_day;
                // day is i/shifts_per_day
                day = i/shifts_per_day;
                if (entry.getValue().charAt(i*workers_in_shift) == '1') {
                    // Append the worker's name to the list of name
                    optionsList.get(shiftInDay).set(day, optionsList.get(shiftInDay).get(day) + entry.getKey() + "\n");
                }
            }
        }
        return optionsList;
    }

    private void createHeader(int rowLength) {
        TableRow header = new TableRow(OptionsViewActivity.this);
        String[] days = getResources().getStringArray(R.array.week_days);
        for (int i=0 ; i<rowLength ; i++) {
            TextView text = new TextView(OptionsViewActivity.this);
            if (i == 0) {
                text.setText("");
            } else {
                text.setText(days[i-1]);
            }
            text.setPadding(16, 8, 16, 8);
            text.setTextSize(20);
            header.addView(text);
        }
        header.setBackground(getDrawable(R.drawable.table_cell_shape));
        optionsTable.addView(header);
    }

    private void createRow(ArrayList<String> arrayRow, int rowLength, int shiftStartingTime, int shiftLength) {
        TableRow row = new TableRow(OptionsViewActivity.this);
        for (int i=0 ; i<rowLength ; i++) {
            TextView text = new TextView(OptionsViewActivity.this);
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
        optionsTable.addView(row);
    }

    private void createTable(ArrayList<ArrayList<String>> table, int rowlength, int firstShiftStartingTime, int ShiftLength) {
        int count = 0;
        for (ArrayList<String> row : table) {
            // Shift starting time is calculated for each shift
            createRow(row, rowlength, (firstShiftStartingTime +(ShiftLength * count++))%24, ShiftLength);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_option_view);

        Toolbar optionsViewToolbar = findViewById(R.id.options_view_toolbar);
        optionsViewToolbar.setTitle(getResources().getString(R.string.options_view_toolbar_text));
        setSupportActionBar(optionsViewToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        optionsViewToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });

        // gets the parameters for table creation
        String group_name = getIntent().getExtras().getString("GROUP_NAME");
        int starting_time = getIntent().getExtras().getInt("STARTING_TIME");
        int shift_length = getIntent().getExtras().getInt("SHIFT_LENGTH");
        int shifts_per_day = getIntent().getExtras().getInt("SHIFTS_PER_DAY");
        int days_num = getIntent().getExtras().getInt("DAYS_NUM");
        int workers_in_shift = getIntent().getExtras().getInt("WORKERS_IN_SHIFT");
        group_options = (HashMap<String, String>)getIntent().getSerializableExtra("OPTIONS");
        String workers_without_options = getIntent().getExtras().getString("WORKERS_WITHOUT_OPTIONS");

        ArrayList<ArrayList<String>> optionsArray = reorganizeOptionsForTable (group_options,
                shifts_per_day, days_num, workers_in_shift);

        // Prepare the title to be presented
        TextView title = findViewById(R.id.options_view_title);
        title.setText(String.format(getString(R.string.options_view_title), group_name));


        optionsTable = (TableLayout) findViewById(R.id.options_table);

        // Prepare the table's header
        createHeader(days_num +1);

        // Prepare the options table itself
        createTable(optionsArray, (days_num +1), starting_time, shift_length);

        // List of people who are yet to send options
        if (!workers_without_options.isEmpty()) {
            TextView subtitle = findViewById(R.id.options_view_subtitle);
            subtitle.setText(getString(R.string.options_view_subtitle));
            TextView workers_no_options = findViewById(R.id.options_workers_without_options);
            workers_no_options.setText(workers_without_options);
        }
    }
}
