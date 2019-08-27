package com.technion.shiftlyapp.shiftly.miscellaneous;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.TextView;

import com.technion.shiftlyapp.shiftly.R;

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

        TextView privacy_policy = findViewById(R.id.privacy_policy);
        privacy_policy.setMovementMethod(LinkMovementMethod.getInstance());

        mainToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });
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