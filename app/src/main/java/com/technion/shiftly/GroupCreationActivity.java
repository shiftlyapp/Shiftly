package com.technion.shiftly;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.UUID;

public class GroupCreationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_creation);
        Toolbar mainToolbar = findViewById(R.id.group_creation_toolbar);
        setSupportActionBar(mainToolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.group_create));

        TextView uuid = findViewById(R.id.group_code);
        String code_without_hyphens = remove_hyphens_from_string(UUID.randomUUID().toString()); 
        uuid.setText(code_without_hyphens);

        Button create_button = findViewById(R.id.create_button);
        create_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), TimeslotsConfigActivity.class);
                startActivity(intent);

            }
        });
    }

    private String remove_hyphens_from_string(String s) {
        return s.replace("-", "");
    }

    public void share_code(View view) {
        EditText share_text = findViewById(R.id.group_code);
        String code = share_text.getText().toString();
        String sharing_message = "You have been invited to use Shiftly! Enter this code in the app: " + code + " and join the group!";

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, sharing_message);
        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent, "Send to: "));
    }
}