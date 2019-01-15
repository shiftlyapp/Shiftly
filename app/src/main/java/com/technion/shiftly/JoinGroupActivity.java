package com.technion.shiftly;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.google.common.primitives.UnsignedLong;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.basgeekball.awesomevalidation.ValidationStyle.BASIC;
import static java.lang.Math.toIntExact;

// The first activity of the group creation process.
// In this activity the future admin sets the group name.

public class JoinGroupActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference databaseRef;
    private List<String> groupsIds;

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
                    String user_id = currentUser.getUid();
                    final DatabaseReference userGroupsRef = databaseRef.child("Users").child(user_id);
                    final String group_code = ((EditText)findViewById(R.id.join_group_edittext)).getText().toString();

                    userGroupsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        // TODO: add: do this only if group not already in the groups

                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            // Increment groups number by one

                            Map<String, Object> groups_count_map = new HashMap<>();
                            Long groups_count = dataSnapshot.child("groups_count").getValue(Long.class);
                            Long newValue = (groups_count + 1L);
                            groups_count_map.put("groups_count", newValue);
                            userGroupsRef.updateChildren(groups_count_map);


                            // Add the group to the groups of the user
                            for (DataSnapshot postSnapshot : dataSnapshot.child("groups").getChildren()) {
                                groupsIds.add(postSnapshot.getValue(String.class));
                            }

                            Map<String, Object> groups_map = new HashMap<>();
                            groupsIds.add(group_code);
                            groups_map.put("groups", groupsIds);
                            userGroupsRef.updateChildren(groups_map);

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


                    Intent intent = new Intent(getApplicationContext(), GroupListsActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("FRAGMENT_TO_LOAD",Constants.GROUPS_I_BELONG_FRAGMENT);
                    startActivity(intent);


                }
            }
        });
    }
}