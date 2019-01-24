package com.technion.shiftly.groupCreation;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import com.technion.shiftly.R;

// The third activity of the group creation process.
// In this activity the future admin sets the group settings.

public class GroupCreation3Activity extends AppCompatActivity {

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
        setContentView(R.layout.activity_group_creation_3);
        Toolbar mainToolbar = findViewById(R.id.group_creation_toolbar_3);
        setSupportActionBar(mainToolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.group_create_label));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Setting the spinners with their adapters
        final Spinner days_spinner = findViewById(R.id.days_num_spinner);
        ArrayAdapter<CharSequence> days_spinner_adapter = ArrayAdapter.createFromResource(this,
                R.array.days_num, R.layout.custom_spinner_item);
        days_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        days_spinner.setAdapter(days_spinner_adapter);

        final Spinner shifts_per_day_spinner = findViewById(R.id.shifts_per_day_num_spinner);
        ArrayAdapter<CharSequence> shifts_spinner_adapter = ArrayAdapter.createFromResource(this,
                R.array.shifts_per_day, R.layout.custom_spinner_item);
        shifts_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        shifts_per_day_spinner.setAdapter(shifts_spinner_adapter);

        final Spinner employees_per_shift_spinner = findViewById(R.id.employees_per_shift_num_spinner);
        ArrayAdapter<CharSequence> employees_spinner_adapter = ArrayAdapter.createFromResource(this,
                R.array.employees_per_shift, R.layout.custom_spinner_item);
        employees_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        employees_per_shift_spinner.setAdapter(employees_spinner_adapter);

        final Spinner starting_hour_spinner = findViewById(R.id.starting_hour_spinner);
        ArrayAdapter<CharSequence> starting_hour_spinner_adapter = ArrayAdapter.createFromResource(this,
                R.array.starting_hour, R.layout.custom_spinner_item);
        starting_hour_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        starting_hour_spinner.setAdapter(starting_hour_spinner_adapter);

        final Spinner shift_len_spinner = findViewById(R.id.shift_len_spinner);
        ArrayAdapter<CharSequence> shift_len_spinner_adapter = ArrayAdapter.createFromResource(this,
                R.array.shift_len, R.layout.custom_spinner_item);
        shift_len_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        shift_len_spinner.setAdapter(shift_len_spinner_adapter);

        Button apply_button = findViewById(R.id.continue_button);
        apply_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.setEnabled(false);
                Intent timeslots_config_intent = new Intent(getApplicationContext(), GroupCreation4Activity.class);
                String group_name = getIntent().getExtras().getString("GROUP_NAME");
                byte[] group_pic_array = getIntent().getExtras().getByteArray("GROUP_PICTURE");

                Long days_num = Long.parseLong(days_spinner.getSelectedItem().toString());

                Long shifts_per_day = Long.parseLong(shifts_per_day_spinner.getSelectedItem().toString());

                Long employees_per_shift = Long.parseLong(employees_per_shift_spinner.getSelectedItem().toString());

                String starting_hour = starting_hour_spinner.getSelectedItem().toString();

                Long shift_len = Long.parseLong(shift_len_spinner.getSelectedItem().toString());

                timeslots_config_intent.putExtra("GROUP_NAME", group_name);
                timeslots_config_intent.putExtra("DAYS_NUM", days_num);
                timeslots_config_intent.putExtra("SHIFTS_PER_DAY", shifts_per_day);
                timeslots_config_intent.putExtra("EMPLOYEES_PER_SHIFT", employees_per_shift);
                timeslots_config_intent.putExtra("STARTING_HOUR", starting_hour);
                timeslots_config_intent.putExtra("SHIFT_LEN", shift_len);
                if (group_pic_array!=null) {
                    timeslots_config_intent.putExtra("GROUP_PICTURE", group_pic_array);
                }
                startActivity(timeslots_config_intent);
                finish();
            }
        });

    }
}