package com.technion.shiftly;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// The first activity of the group creation process.
// In this activity the future admin sets the group name.

public class OptionsListActivity extends AppCompatActivity {

    private RecyclerView options_recyclerview;
    private RecyclerView.Adapter options_adapter;
    private RecyclerView.LayoutManager options_layoutmanager;

    private List<Pair<String, String>> list;
    private Long days_num_param = 5L;
    private Long shifts_per_day_param = 3L;
    private String options;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private GoogleSignInAccount mGoogleSignInAccount;

    @Override
    public void onStart() {
        super.onStart();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        mGoogleSignInAccount = GoogleSignIn.getLastSignedInAccount(this);
        if (currentUser == null) {
            if (mGoogleSignInAccount == null) {
                startActivity(new Intent(this, LoginActivity.class));
                finish();
            }
        }
    }

    private void loadParamsFromDatabase() {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("Groups");
        ChildEventListener cl = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                days_num_param = dataSnapshot.child("days_num").getValue(Long.class);
                shifts_per_day_param = dataSnapshot.child("shifts_per_day").getValue(Long.class);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config_options);
        options_recyclerview = findViewById(R.id.options_recycler_view);
        Toolbar optionsToolbar = findViewById(R.id.options_toolbar);
        optionsToolbar.setTitle(getResources().getString(R.string.options_toolbar_text));
        setSupportActionBar(optionsToolbar);
        options_recyclerview.setHasFixedSize(true);

        options_layoutmanager = new LinearLayoutManager(this);
        options_recyclerview.setLayoutManager(options_layoutmanager);

        loadParamsFromDatabase();
        addShiftsToList();

        int total_num_of_shifts = (int) (days_num_param * shifts_per_day_param);
        char[] chars = new char[total_num_of_shifts];
        Arrays.fill(chars, '0');
        options = new String(chars);

        options_adapter = new OptionsListAdapter(getApplicationContext(), list);

        OptionsListAdapter.ItemClickListener listener = new OptionsListAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                Character c = options.charAt(position);
                Character newChar;

                if (c == '0') {
                    newChar = '1';
                } else {
                    newChar = '0';
                }
                options = options.substring(0, position) + newChar + options.substring(position + 1);
            }
        };

        ((OptionsListAdapter) options_adapter).setClickListener(listener);
        options_recyclerview.setAdapter(options_adapter);

    }

    private void addShiftsToList() {
        list = new ArrayList<>();
        for (int i = 1; i < days_num_param + 1; i++) {
            for (int j = 1; j < shifts_per_day_param + 1; j++) {
                String day_as_string = Integer.toString(i);
                String shift_as_string = Integer.toString(j);
                Pair<String, String> p = new Pair<>("Day number: " + day_as_string, "Shift number: " + shift_as_string);
                list.add(p);
            }
        }
    }

}