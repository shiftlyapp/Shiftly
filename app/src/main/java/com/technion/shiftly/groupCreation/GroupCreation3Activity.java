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
        Toolbar mainToolbar = findViewById(R.id.group_creation_toolbar_25);
        setSupportActionBar(mainToolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.group_create_label));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Setting the spinners with their adapters
        Spinner days_spinner = findViewById(R.id.days_num_spinner);
        ArrayAdapter<CharSequence> days_spinner_adapter = ArrayAdapter.createFromResource(this,
                R.array.days_num, android.R.layout.simple_spinner_item);
        days_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        days_spinner.setAdapter(days_spinner_adapter);

        Spinner shifts_per_day_spinner = findViewById(R.id.shifts_per_day_num_spinner);
        ArrayAdapter<CharSequence> shifts_spinner_adapter = ArrayAdapter.createFromResource(this,
                R.array.shifts_per_day, android.R.layout.simple_spinner_item);
        shifts_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        shifts_per_day_spinner.setAdapter(shifts_spinner_adapter);

        Spinner employees_per_shift_spinner = findViewById(R.id.employees_per_shift_num_spinner);
        ArrayAdapter<CharSequence> employees_spinner_adapter = ArrayAdapter.createFromResource(this,
                R.array.employees_per_shift, android.R.layout.simple_spinner_item);
        employees_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        employees_per_shift_spinner.setAdapter(employees_spinner_adapter);


        Button apply_button = findViewById(R.id.continue_button);
        apply_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.setEnabled(false);
                Intent timeslots_config_intent = new Intent(getApplicationContext(), GroupCreation4Activity.class);
                String group_name = getIntent().getExtras().getString("GROUP_NAME");
                byte[] group_pic_array = getIntent().getExtras().getByteArray("GROUP_PICTURE");

                final Spinner days_num_spinner = findViewById(R.id.days_num_spinner);
                Long days_num = Long.parseLong(days_num_spinner.getSelectedItem().toString());

                final Spinner shifts_num_spinner = findViewById(R.id.shifts_per_day_num_spinner);
                Long shifts_per_day = Long.parseLong(shifts_num_spinner.getSelectedItem().toString());

                final Spinner employees_per_shift_num_spinner = findViewById(R.id.employees_per_shift_num_spinner);
                Long employees_per_shift = Long.parseLong(employees_per_shift_num_spinner.getSelectedItem().toString());

                timeslots_config_intent.putExtra("GROUP_NAME", group_name);
                timeslots_config_intent.putExtra("DAYS_NUM", days_num);
                timeslots_config_intent.putExtra("SHIFTS_PER_DAY", shifts_per_day);
                timeslots_config_intent.putExtra("EMPLOYEES_PER_SHIFT", employees_per_shift);
                if (group_pic_array!=null) {
                    timeslots_config_intent.putExtra("GROUP_PICTURE", group_pic_array);
                }
                startActivity(timeslots_config_intent);
                finish();
            }
        });

    }
}