package com.technion.shiftly.options;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.technion.shiftly.R;

public class OptionsViewActivity extends AppCompatActivity {

    TableLayout optionsTable;
    TableRow row;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_option_view);

        optionsTable = (TableLayout) findViewById(R.id.options_table);

        row = new TableRow(OptionsViewActivity.this);

    }
}
