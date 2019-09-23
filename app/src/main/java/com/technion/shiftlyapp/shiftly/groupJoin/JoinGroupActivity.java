package com.technion.shiftlyapp.shiftly.groupJoin;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.technion.shiftlyapp.shiftly.R;
import com.technion.shiftlyapp.shiftly.dataAccessLayer.DataAccess;
import com.technion.shiftlyapp.shiftly.dataTypes.Group;
import com.technion.shiftlyapp.shiftly.dataTypes.User;
import com.technion.shiftlyapp.shiftly.entry.LoginActivity;
import com.technion.shiftlyapp.shiftly.groupsList.GroupListsActivity;
import com.technion.shiftlyapp.shiftly.utility.Constants;
import com.technion.shiftlyapp.shiftly.utility.CustomSnackbar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.content.ClipDescription.MIMETYPE_TEXT_PLAIN;
import static com.basgeekball.awesomevalidation.ValidationStyle.BASIC;
import static com.technion.shiftlyapp.shiftly.utility.Constants.GROUP_ID_LENGTH;

// The join group activity

public class JoinGroupActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference databaseRef;
    private List<String> groupsIds;
    private HashMap<String, String> currentGroupMembers;
    private Long currGroupMembersCount;
    private CustomSnackbar mSnackbar;
    private ConstraintLayout mLayout;
    private EditText join_group_edittext;
    private Boolean group_exists_in_db;

    @Override
    public void onStart() {
        super.onStart();
        currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
        loadClipboardData();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void loadClipboardData() {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        String pasteData = "";

        // Empty Clipboard
        if (!(clipboard.hasPrimaryClip())) {
            return;
            // Clipboard contains not a plain text
        } else if (!(clipboard.getPrimaryClipDescription().hasMimeType(MIMETYPE_TEXT_PLAIN))) {
            return;
        } else {
            // Clipboard contains plain text.
            ClipData.Item item = clipboard.getPrimaryClip().getItemAt(0);
            pasteData = item.getText().toString();
            if (pasteData.length() == GROUP_ID_LENGTH  && pasteData.startsWith("-")) { // Valid Group ID
                join_group_edittext.setText(pasteData);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_group);
        mAuth = FirebaseAuth.getInstance();
        mLayout = findViewById(R.id.join_group);
        mSnackbar = new CustomSnackbar(CustomSnackbar.SNACKBAR_DEFAULT_TEXT_SIZE);
        group_exists_in_db = false;
        currentGroupMembers = new HashMap<>();

        Toolbar mainToolbar = findViewById(R.id.join_group_toolbar);
        setSupportActionBar(mainToolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.group_join_label));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        join_group_edittext = (EditText) findViewById(R.id.join_group_edittext);
        final AwesomeValidation mAwesomeValidation = new AwesomeValidation(BASIC);
        mAwesomeValidation.addValidation(JoinGroupActivity.this, R.id.join_group_edittext, "^([\\-\\_a-zA-Z0-9]{20})*$", R.string.err_groupid);
        databaseRef = FirebaseDatabase.getInstance().getReference();
        groupsIds = new ArrayList<>();
        Button joinButton = findViewById(R.id.join_button);
        joinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (join_group_edittext.getText().length() != 0) {
                    if (mAwesomeValidation.validate()) {
                        // Add the user to the group and close this intent
                        final String group_code = join_group_edittext.getText().toString();

                        DataAccess dataAccess = new DataAccess();
                        dataAccess.getGroup(group_code, group -> {
                            // Group does not exist
                            if (group == null) {
                                mSnackbar.show(JoinGroupActivity.this, mLayout, getResources().getString(R.string.err_group_doesnt_exist), CustomSnackbar.SNACKBAR_ERROR, Snackbar.LENGTH_SHORT);
                            } else {
                                // User is the manager of the group
                                if (group.getAdmin().equals(mAuth.getUid())) {
                                    mSnackbar.show(JoinGroupActivity.this, mLayout, getResources().getString(R.string.err_user_is_admin), CustomSnackbar.SNACKBAR_ERROR, Snackbar.LENGTH_SHORT);
                                } else {
                                    Group updatedGroup = new Group(group);
                                    currentGroupMembers = new HashMap<>(group.getMembers());
                                    currGroupMembersCount = group.getMembers_count();

                                    // Update user
                                    final String user_id = currentUser.getUid();
                                    dataAccess.getUser(user_id, user -> {
                                        if (user.getGroups().contains(group_code)) {
                                            mSnackbar.show(JoinGroupActivity.this, mLayout, getResources().getString(R.string.err_group_already_exist), CustomSnackbar.SNACKBAR_ERROR, Snackbar.LENGTH_SHORT);
                                        } else {
                                            User updatedUser = new User(user);

                                            // Add this group to the user's groups list in the DB
                                            ArrayList<String> updatedGroups = user.getGroups();
                                            updatedGroups.add(group_code);
                                            updatedUser.setGroups(updatedGroups);

                                            // Increment the user's groups number by one
                                            updatedUser.setGroups_count(user.getGroups_count() + 1L);

                                            // Update members of a group
                                            String fullName = getIntent().getExtras().getString("FULL_NAME");
                                            currentGroupMembers.put(currentUser.getUid(), fullName);
                                            updatedGroup.setMembers(currentGroupMembers);

                                            // Update members count of a group
                                            updatedGroup.setMembers_count(group.getMembers_count() + 1L);

                                            // Update user and group
                                            dataAccess.updateGroup(group_code, updatedGroup);
                                            dataAccess.updateUser(currentUser.getUid(), updatedUser);

                                            mSnackbar.show(JoinGroupActivity.this, mLayout, getResources().getString(R.string.group_join_success), CustomSnackbar.SNACKBAR_SUCCESS, Snackbar.LENGTH_SHORT);
                                            Handler handler = new Handler();
                                            handler.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    // Switch back to the GROUPS_I_BELONG fragment after a short delay
                                                    Intent intent = new Intent(getApplicationContext(), GroupListsActivity.class);
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                    intent.putExtra("FRAGMENT_TO_LOAD", Constants.GROUPS_I_BELONG_FRAGMENT);
                                                    startActivity(intent);
                                                }
                                            }, Constants.REDIRECTION_DELAY);
                                        }

                                    });
                                }
                            }
                        });

                    }
                }
            }
        });
    }
}