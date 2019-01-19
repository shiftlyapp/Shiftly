package com.technion.shiftly.options;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Pair;
import android.view.View;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.technion.shiftly.R;
import com.technion.shiftly.entry.LoginActivity;
import com.technion.shiftly.utility.DividerItemDecorator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
// The first activity of the group creation process.
// In this activity the future admin sets the group name.

public class OptionsListActivity extends AppCompatActivity {

    private RecyclerView options_recyclerview;
    private RecyclerView.Adapter options_adapter;
    private RecyclerView.LayoutManager options_layoutmanager;

    private List<Pair<String, String>> list_of_texts;
    private Long days_num_param;
    private Long shifts_per_day_param;
    private String options;
    private String group_id;

    private DatabaseReference databaseRef;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private GoogleSignInAccount mGoogleSignInAccount;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options_list);
        options_recyclerview = findViewById(R.id.options_recycler_view);
        Toolbar optionsToolbar = findViewById(R.id.options_toolbar);
        optionsToolbar.setTitle(getResources().getString(R.string.options_toolbar_text));
        setSupportActionBar(optionsToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        options_recyclerview.setHasFixedSize(true);
        list_of_texts = new ArrayList<>();

        options_layoutmanager = new LinearLayoutManager(this);
        options_recyclerview.setLayoutManager(options_layoutmanager);
        RecyclerView.ItemDecoration dividerItemDecoration = new DividerItemDecorator(ContextCompat.getDrawable(getApplicationContext(), R.drawable.recycler_divider));
        options_recyclerview.addItemDecoration(dividerItemDecoration);

        group_id = getIntent().getExtras().getString("GROUP_ID");

        databaseRef = FirebaseDatabase.getInstance().getReference().child("Groups").child(group_id);
        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                days_num_param = dataSnapshot.child("days_num").getValue(Long.class);
                shifts_per_day_param = dataSnapshot.child("shifts_per_day").getValue(Long.class);
                int total_num_of_shifts = (int) (days_num_param * shifts_per_day_param);
                char[] chars = new char[total_num_of_shifts];
                Arrays.fill(chars, '0');
                options = new String(chars);
                addShiftsToList();
                options_adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        options_adapter = new OptionsListAdapter(getApplicationContext(), list_of_texts);
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

        FloatingActionButton doneFab = findViewById(R.id.done_fab);
        doneFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Push options to DB

                databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Map<String, Object> options_map = new HashMap<>();
                        for (DataSnapshot postSnapshot : dataSnapshot.child("options").getChildren()) {
                            options_map.put(postSnapshot.getKey(), postSnapshot.getValue());
                        }
                        options_map.put(currentUser.getUid(), options);

                        Map<String, Object> options_map_of_db = new HashMap<>();
                        options_map_of_db.put("options", options_map);

                        databaseRef.updateChildren(options_map_of_db);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
    }

    private void addShiftsToList() {
        List<String> days_array = new ArrayList<>();
        days_array.add("Sunday");
        days_array.add("Monday");
        days_array.add("Tuesday");
        days_array.add("Wednesday");
        days_array.add("Thursday");
        days_array.add("Friday");
        days_array.add("Saturday");
        for (int i = 1; i < days_num_param + 1; i++) {
            for (int j = 1; j < shifts_per_day_param + 1; j++) {
//                String day_as_string = Integer.toString(i);
                String shift_as_string = Integer.toString(j);
                Pair<String, String> p = new Pair<>("Day: " + days_array.get(i), "Shift number: " + shift_as_string);
                list_of_texts.add(p);
            }
        }
    }

}