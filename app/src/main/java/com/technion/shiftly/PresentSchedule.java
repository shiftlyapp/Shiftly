package com.technion.shiftly;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

public class PresentSchedule extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_present_schedule);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.schedule_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        String result = getIntent().getExtras().getString("RESULT");

        TextView schedule = findViewById(R.id.schedule);
        schedule.setText(result);

    }


}