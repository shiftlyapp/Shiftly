package com.technion.shiftly;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

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