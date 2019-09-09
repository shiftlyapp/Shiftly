package com.technion.shiftlyapp.shiftly.optionsView;

import android.os.Bundle;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.technion.shiftlyapp.shiftly.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class OptionsViewActivity extends AppCompatActivity {

    TableLayout optionsTable;
    HashMap<String, String> group_options;
    HashMap<String, Float> chart_data;
    ArrayList<String> labels;

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
        for (LinkedHashMap.Entry<String, String> employee : options.entrySet()) { //for every employee
            int day;
            int shiftInDay;
            for (int i = 0 ; i < days_num*shifts_per_day ; i++) {
                // i'th shift =>
                // shift of the day is i%shifts_per_day
                shiftInDay = i%shifts_per_day;
                // day is i/shifts_per_day
                day = i/shifts_per_day;
                if (employee.getValue().charAt(i*workers_in_shift) == '1') {
                    // Append the worker's name to the list of name
                    optionsList.get(shiftInDay).set(day, optionsList.get(shiftInDay).get(day) + employee.getKey() + "\n");
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

        labels = new ArrayList<>();

        BarChart flexibility_chart = findViewById(R.id.flexibility_graph);
        chart_data = new HashMap<>();
        getChartData();
        List<BarEntry> entries = new ArrayList<>();
        int counter = 0;
        for (Map.Entry<String, Float> employee : chart_data.entrySet()) {
            String emp_name = employee.getKey();
            labels.add(emp_name);
            float flex_rate = employee.getValue();
            entries.add(new BarEntry(counter, flex_rate*100));
            counter++;
        }
        BarDataSet set = new BarDataSet(entries, "Flexibility Rate");
        set.setDrawValues(true);

        BarData data = new BarData(set);
        data.setBarWidth(0.5f);
        flexibility_chart.setData(data);

        flexibility_chart.setDrawValueAboveBar(false);
        flexibility_chart.setFitBars(true);
        flexibility_chart.getDescription().setEnabled(false);

        flexibility_chart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
        flexibility_chart.getAxisRight().setAxisMaximum(110.0f);
        flexibility_chart.getAxisLeft().setAxisMaximum(110.0f);
        flexibility_chart.getAxisRight().setAxisMinimum(0.0f);
        flexibility_chart.getAxisLeft().setAxisMinimum(0.0f);

        flexibility_chart.getXAxis().setLabelCount(labels.size());
        flexibility_chart.getXAxis().setTextColor(R.color.text_color_primary);
        flexibility_chart.invalidate();
    }

    private void getChartData() {
        for (Map.Entry<String, String> entry : group_options.entrySet()) {
            String employee_name = entry.getKey();
            String employee_options = entry.getValue();
            Float flexibility_rate = flex_rate(employee_options);
            chart_data.put(employee_name, flexibility_rate);
        }


    }

    private float flex_rate(String employee_options) {
        float available_shifts_total = employee_options.length();
        int available_shifts = 0;
        for (int i = 0; i < available_shifts_total; i++) {
            if (employee_options.charAt(i) == '1') available_shifts++;
        }
        return available_shifts / available_shifts_total;
    }
}
