package com.technion.shiftlyapp.shiftly.groupCreation;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.technion.shiftlyapp.shiftly.R;
import com.technion.shiftlyapp.shiftly.dataTypes.Group;

import java.util.ArrayList;
import java.util.HashMap;

import static com.basgeekball.awesomevalidation.ValidationStyle.BASIC;

// The first activity of the group creation process.
// In this activity the future admin sets the group name.
public class GroupCreation1Activity extends AppCompatActivity {
    private Group group;

    @Override
    public void onBackPressed() {
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
        setContentView(R.layout.activity_group_creation_1);
        Toolbar mainToolbar = findViewById(R.id.group_creation_toolbar_1);
        setSupportActionBar(mainToolbar);

        final String group_action = getIntent().getExtras().getString("GROUP_ACTION");

        setGroupType(group_action);

        TextView group_hint = findViewById(R.id.group_name_hint);
        setGroupHintText(group_action, group_hint);

        final String action_bar_title = setActionBarTitle(group_action);

        final EditText group_name_edittext = findViewById(R.id.group_name_edittext);
        setGroupName(group_action, group_name_edittext);

        getSupportActionBar().setTitle(action_bar_title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        final AwesomeValidation mAwesomeValidation = new AwesomeValidation(BASIC);
        mAwesomeValidation.addValidation(GroupCreation1Activity.this, R.id.group_name_edittext, "^[a-zA-Z0-9\u0590-\u05fe][a-zA-Z0-9\u0590-\u05fe\\s]*$", R.string.err_groupname);

        Button createButton = findViewById(R.id.create_button);

        final String group_id = setGroupId(group_action);
        createButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                if (mAwesomeValidation.validate()) {
                    Intent group_creation_2_intent = new Intent(getApplicationContext(), GroupCreation2Activity.class);
                    String group_name = group_name_edittext.getText().toString();

                    group.setGroup_name(group_name);
                    group.setGroup_icon_url("none");

                    group_creation_2_intent.putExtra("GROUP", group);
                    group_creation_2_intent.putExtra("GROUP_ACTION", group_action);
                    group_creation_2_intent.putExtra("GROUP_ID", group_id);
                    group_creation_2_intent.putExtra("TITLE", action_bar_title);

                    startActivity(group_creation_2_intent);
                }
            }
        });
    }

    private void setGroupType(String group_action) {
        if (group_action.equals("CREATE")) {
            group = new Group();
        }
        else {
            group = getIntent().getExtras().getParcelable("GROUP");

            // Remove current schedule and options
            group.setOptions(new HashMap<String, String>());
            group.setSchedule(new ArrayList<String>());
        }
    }

    private void setGroupName(String group_action, EditText group_name_edittext) {
        if (!group_action.equals("CREATE")) {
            // Set current group name
            group_name_edittext.setText(group.getGroup_name());
        }
    }

    private String setGroupId(String group_action) {
        String group_id;
        if (group_action.equals("CREATE")) {
            group_id = "";
        } else {
            group_id = getIntent().getExtras().getString("GROUP_ID");
        }
        return group_id;
    }

    private void setGroupHintText(String group_action, TextView group_hint) {
        if (group_action.equals("CREATE")) {
            group_hint.setText(R.string.group_name_create_hint);
        } else {
            group_hint.setText(R.string.group_name_edit_hint);
        }
    }

    private String setActionBarTitle(String group_action) {
        String action_bar_title;
        if (group_action.equals("CREATE")) {
            action_bar_title = getResources().getString(R.string.group_create_label);
        } else {
            action_bar_title = getResources().getString(R.string.group_edit_label);
        }
        return action_bar_title;
    }
}