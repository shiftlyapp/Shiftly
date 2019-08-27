package com.technion.shiftlyapp.shiftly.options;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.technion.shiftlyapp.shiftly.R;
import com.technion.shiftlyapp.shiftly.entry.LoginActivity;
import com.technion.shiftlyapp.shiftly.utility.DividerItemDecorator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OptionsListActivity extends AppCompatActivity {

    private RecyclerView options_recyclerview;
    private RecyclerView.Adapter options_adapter;
    private RecyclerView.LayoutManager options_layoutmanager;

    private List<Pair<String, String>> list_of_texts;
    private Long days_num_param;
    private Long shifts_per_day_param;
    private Long num_of_employees_per_shift;
    private String options;
    private String group_id;

    private DatabaseReference databaseRef;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private GoogleSignInAccount mGoogleSignInAccount;

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
                num_of_employees_per_shift = dataSnapshot.child("employees_per_shift").getValue(Long.class);
                int total_num_of_shifts = (int) (days_num_param.intValue() * shifts_per_day_param.intValue());
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
                Log.v("TAG", "Item position: " + position);
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
                        String streched = strech_string_by_num_of_employees();
                        options_map.put(currentUser.getUid(), streched);

                        Map<String, Object> options_map_of_db = new HashMap<>();
                        options_map_of_db.put("options", options_map);

                        databaseRef.updateChildren(options_map_of_db);

                        Toast.makeText(OptionsListActivity.this, R.string.options_updated_text, Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
    }
    private String strech_string_by_num_of_employees() {
        String streached = "";
        for (int i=0 ; i<options.length() ; i++) {
            char charToAdd = options.charAt(i);
            String str = "";
            for (int j=0 ; j<num_of_employees_per_shift.intValue() ; j++) {
                str += charToAdd;
            }
            streached = streached.substring(0, i*num_of_employees_per_shift.intValue()) + str;
        }
        return streached;
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
                String shift_as_string = Integer.toString(j);
                Pair<String, String> p = new Pair<>(days_array.get(i-1), "Shift number: " + shift_as_string);
                list_of_texts.add(p);
            }
        }
    }
}