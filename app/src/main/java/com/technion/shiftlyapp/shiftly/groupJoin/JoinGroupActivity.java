package com.technion.shiftlyapp.shiftly.groupJoin;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.technion.shiftlyapp.shiftly.R;
import com.technion.shiftlyapp.shiftly.entry.LoginActivity;
import com.technion.shiftlyapp.shiftly.groupsList.GroupListsActivity;
import com.technion.shiftlyapp.shiftly.utility.Constants;
import com.technion.shiftlyapp.shiftly.utility.CustomSnackbar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.ClipDescription.MIMETYPE_TEXT_PLAIN;
import static com.basgeekball.awesomevalidation.ValidationStyle.BASIC;
import static com.technion.shiftlyapp.shiftly.utility.Constants.GROUP_ID_LENGTH;

// The join group activity

public class JoinGroupActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference databaseRef;
    private List<String> groupsIds;
    private Map<String, String> currentGroupMembers;
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

                        // Check if group exists in the db
                        databaseRef.child("Groups").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                    String group = postSnapshot.getKey();
                                    if (group.equals(group_code)) {
                                        if (postSnapshot.child("admin").getValue().toString().equals(mAuth.getUid())) {
                                            mSnackbar.show(JoinGroupActivity.this, mLayout, getResources().getString(R.string.err_user_is_admin), CustomSnackbar.SNACKBAR_ERROR, Snackbar.LENGTH_SHORT);
                                            return;
                                        } else {
                                            for (DataSnapshot postSnap : dataSnapshot.child(group_code).child("members").getChildren()) {
                                                currentGroupMembers.put(postSnap.getKey(), postSnap.getValue().toString());
                                            }
                                            currGroupMembersCount = dataSnapshot.child(group_code).child("members_count").getValue(Long.class);
                                            group_exists_in_db = true;
                                            break;
                                        }
                                    }
                                }
                                // Group does not exist in DB
                                if (!group_exists_in_db) {
                                    mSnackbar.show(JoinGroupActivity.this, mLayout, getResources().getString(R.string.err_group_doesnt_exist), CustomSnackbar.SNACKBAR_ERROR, Snackbar.LENGTH_SHORT);

                                } else {
                                    final String user_id = currentUser.getUid();
                                    final DatabaseReference userGroupsRef = databaseRef.child("Users").child(user_id);
                                    userGroupsRef.addListenerForSingleValueEvent(new ValueEventListener() {

                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                            // Get all of the groups that the user is a member of
                                            for (DataSnapshot postSnapshot : dataSnapshot.child("groups").getChildren()) {
                                                groupsIds.add(postSnapshot.getValue(String.class));
                                            }

                                            // Check if the user is already a member of this group
                                            if (groupsIds.contains(group_code)) {
                                                mSnackbar.show(JoinGroupActivity.this, mLayout, getResources().getString(R.string.err_group_already_exist), CustomSnackbar.SNACKBAR_ERROR, Snackbar.LENGTH_SHORT);
                                            } else {

                                                // Add this group to the user's groups list in the DB
                                                Map<String, Object> groups_map = new HashMap<>();
                                                groupsIds.add(group_code);
                                                groups_map.put("groups", groupsIds);
                                                userGroupsRef.updateChildren(groups_map);

                                                // Increment the user's groups number by one
                                                Map<String, Object> groups_count_map = new HashMap<>();
                                                Long groups_count = dataSnapshot.child("groups_count").getValue(Long.class);
                                                Long newGroupsCountValue = (groups_count + 1L);
                                                groups_count_map.put("groups_count", newGroupsCountValue);
                                                userGroupsRef.updateChildren(groups_count_map);

                                                // Update members of a group
                                                final DatabaseReference groupMembersRef = databaseRef.child("Groups").child(group_code);

                                                Map<String, Object> groupMembers = new HashMap<>();
                                                String fullName = getIntent().getExtras().getString("FULL_NAME");
                                                currentGroupMembers.put(currentUser.getUid(), fullName);
                                                groupMembers.put("members", currentGroupMembers);
                                                groupMembersRef.updateChildren(groupMembers);

                                                // Update members count of a group
                                                Map<String, Object> groupMembersCount = new HashMap<>();
                                                Long newMembersCountValue = (currGroupMembersCount + 1L);
                                                groupMembersCount.put("members_count", newMembersCountValue);
                                                groupMembersRef.updateChildren(groupMembersCount);


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
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                            mSnackbar.show(JoinGroupActivity.this, mLayout, databaseError.getMessage(), CustomSnackbar.SNACKBAR_ERROR, Snackbar.LENGTH_SHORT);
                                        }
                                    });
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                mSnackbar.show(JoinGroupActivity.this, mLayout, databaseError.getMessage(), CustomSnackbar.SNACKBAR_ERROR, Snackbar.LENGTH_SHORT);
                            }
                        });

                    }
                }
            }
        });
    }
}