package com.technion.shiftlyapp.shiftly.options;

import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.technion.shiftlyapp.shiftly.R;
import com.technion.shiftlyapp.shiftly.dataAccessLayer.DataAccess;
import com.technion.shiftlyapp.shiftly.dataTypes.Group;
import com.technion.shiftlyapp.shiftly.entry.LoginActivity;
import com.technion.shiftlyapp.shiftly.utility.DividerItemDecorator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class OptionsListActivity extends AppCompatActivity {

    private RecyclerView options_recyclerview;
    private RecyclerView.Adapter options_adapter;
    private RecyclerView.LayoutManager options_layoutmanager;

    private List<Pair<String, String>> list_of_texts;
    private String options;
    private String group_id;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private GoogleSignInAccount mGoogleSignInAccount;
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

        final DataAccess dataAccess = new DataAccess();
        dataAccess.getGroup(group_id, new DataAccess.DataAccessCallback<Group>() {
            @Override
            public void onCallBack(Group g) {
                group = g;

                int total_num_of_shifts = group.getDays_num().intValue() * group.getShifts_per_day().intValue();
                char[] chars = new char[total_num_of_shifts];
                Arrays.fill(chars, '0');
                options = new String(chars);
                addShiftsToList();
                options_adapter.notifyDataSetChanged();
            }
        });

        options_adapter = new OptionsListAdapter(getApplicationContext(), list_of_texts);
        OptionsListAdapter.ItemClickListener listener = new OptionsListAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                char c = options.charAt(position);
                char newChar = c == '0' ? '1' : '0';
                options = options.substring(0, position) + newChar + options.substring(position + 1);
            }
        };

        ((OptionsListAdapter) options_adapter).setClickListener(listener);
        options_recyclerview.setAdapter(options_adapter);

        FloatingActionButton doneFab = findViewById(R.id.done_fab);
        doneFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Update to the new options=
                HashMap<String, String> options_map = group.getOptions();

                String stretched = stretchStringByNameOfEmployees();
                options_map.put(currentUser.getUid(), stretched);

                group.setOptions(options_map);
                dataAccess.updateGroup(group_id, group);
            }
        });
    }

    private String stretchStringByNameOfEmployees() {
        String streached = "";
        for (int i = 0; i < options.length(); i++) {
            char charToAdd = options.charAt(i);
            StringBuilder str = new StringBuilder();
            for (int j = 0; j < group.getEmployees_per_shift().intValue(); j++) {
                str.append(charToAdd);
            }
            streached = streached.substring(0, i * group.getEmployees_per_shift().intValue()) + str.toString();
        }
        return streached;
    }

    private void addShiftsToList() {
        List<String> days_array = new ArrayList<>();
        days_array.add(getString(R.string.sunday));
        days_array.add(getString(R.string.monday));
        days_array.add(getString(R.string.tuesday));
        days_array.add(getString(R.string.wednesday));
        days_array.add(getString(R.string.thursday));
        days_array.add(getString(R.string.friday));
        days_array.add(getString(R.string.saturday));
        for (int i = 1; i < group.getDays_num().intValue() + 1; i++) {
            for (int j = 1; j < group.getShifts_per_day().intValue() + 1; j++) {
                String shift_as_string = Integer.toString(j);
                Pair<String, String> p = new Pair<>(days_array.get(i - 1), getString(R.string.shift_number_title) + shift_as_string);
                list_of_texts.add(p);
            }
        }
    }
}