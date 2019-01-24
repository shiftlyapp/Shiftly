package com.technion.shiftly.miscellaneous;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;
import com.technion.shiftly.R;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        Toolbar mainToolbar = findViewById(R.id.about_toolbar);
        setSupportActionBar(mainToolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.about_toolbar_txt));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        // Fetching list of credits and set them in a text view
        String[] creditsList = getResources().getStringArray(R.array.credits_list);

        StringBuilder creditsString = new StringBuilder();
        for (String name: creditsList) {
            creditsString.append(name);
            creditsString.append("\n");
        }

        TextView credits = findViewById(R.id.credits);
        credits.setText(creditsString.toString());

    }
}