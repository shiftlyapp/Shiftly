package com.technion.shiftly.groupJoin;

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
import com.technion.shiftly.R;
import com.technion.shiftly.entry.LoginActivity;
import com.technion.shiftly.groupsList.GroupListsActivity;
import com.technion.shiftly.utility.Constants;
import com.technion.shiftly.utility.CustomSnackbar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.basgeekball.awesomevalidation.ValidationStyle.BASIC;

// The join group activity

public class JoinGroupActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference databaseRef;
    private List<String> groupsIds;
    private CustomSnackbar mSnackbar;
    private ConstraintLayout mLayout;
    private Boolean group_exists_in_db;

    @Override
    public void onStart() {
        super.onStart();
        currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }

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
        setContentView(R.layout.activity_join_group);
        mAuth = FirebaseAuth.getInstance();
        mLayout = findViewById(R.id.join_group);
        mSnackbar = new CustomSnackbar(CustomSnackbar.SNACKBAR_DEFAULT_TEXT_SIZE);
        group_exists_in_db = false;

        Toolbar mainToolbar = findViewById(R.id.join_group_toolbar);
        setSupportActionBar(mainToolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.group_join_label));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        final AwesomeValidation mAwesomeValidation = new AwesomeValidation(BASIC);
        mAwesomeValidation.addValidation(JoinGroupActivity.this, R.id.join_group_edittext, "^[\\-\\_a-zA-Z0-9]*$", R.string.err_groupid);
        databaseRef = FirebaseDatabase.getInstance().getReference();
        groupsIds = new ArrayList<>();

        Button joinButton = findViewById(R.id.join_button);
        joinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mAwesomeValidation.validate()) {
                    // Add the user to the group and close this intent
                    final String group_code = ((EditText)findViewById(R.id.join_group_edittext)).getText().toString();

                    // Check if group exists in the db
                    databaseRef.child("Groups").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                String group = postSnapshot.getKey();
                                if (group.equals(group_code)) {
                                    group_exists_in_db = true;
                                    break;
                                }

                            }
                            if (!group_exists_in_db) {
                                mSnackbar.show(JoinGroupActivity.this, mLayout, getResources().getString(R.string.err_group_doesnt_exist), CustomSnackbar.SNACKBAR_ERROR,Snackbar.LENGTH_SHORT);

                            } else {
                                String user_id = currentUser.getUid();
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
                                            mSnackbar.show(JoinGroupActivity.this, mLayout, getResources().getString(R.string.err_group_already_exist), CustomSnackbar.SNACKBAR_ERROR,Snackbar.LENGTH_SHORT);
                                        } else {

                                            // Add this group to the user's groups list in the DB
                                            Map<String, Object> groups_map = new HashMap<>();
                                            groupsIds.add(group_code);
                                            groups_map.put("groups", groupsIds);
                                            userGroupsRef.updateChildren(groups_map);

                                            // Increment the user's groups number by one
                                            Map<String, Object> groups_count_map = new HashMap<>();
                                            Long groups_count = dataSnapshot.child("groups_count").getValue(Long.class);
                                            Long newValue = (groups_count + 1L);
                                            groups_count_map.put("groups_count", newValue);
                                            userGroupsRef.updateChildren(groups_count_map);

                                            mSnackbar.show(JoinGroupActivity.this, mLayout, getResources().getString(R.string.group_join_success), CustomSnackbar.SNACKBAR_SUCCESS,Snackbar.LENGTH_SHORT);
                                            Handler handler = new Handler();
                                            handler.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    // Switch back to the GROUPS_I_BELONG fragment after a short delay
                                                    Intent intent = new Intent(getApplicationContext(), GroupListsActivity.class);
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                    intent.putExtra("FRAGMENT_TO_LOAD",Constants.GROUPS_I_BELONG_FRAGMENT);
                                                    startActivity(intent);
                                                }
                                            }, Constants.REDIRECTION_DELAY);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }
            }
        });
    }
}