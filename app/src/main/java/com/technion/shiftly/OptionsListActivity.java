package com.technion.shiftly;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Pair;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

// The first activity of the group creation process.
// In this activity the future admin sets the group name.

public class OptionsListActivity extends AppCompatActivity {

    private RecyclerView timeslots_recyclerview;
    private RecyclerView.Adapter timeslots_adapter;
    private RecyclerView.LayoutManager timeslots_layoutmanager;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private GoogleSignInAccount mGoogleSignInAccount;
    private TabLayout mTabLayout;
    private ViewPagerAdapter mPagerAdapter;
    private ViewPager mViewPager;
    private GoogleSignInClient mGoogleSignInClient;

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
        setContentView(R.layout.activity_config_options);
        timeslots_recyclerview = findViewById(R.id.ts_recycler_view);
        Toolbar timeslotsToolbar = findViewById(R.id.timeslots_toolbar);
        timeslotsToolbar.setTitle(getResources().getString(R.string.timeslots_toolbar_text));
        setSupportActionBar(timeslotsToolbar);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        timeslots_recyclerview.setHasFixedSize(true);

        // use a linear layout manager
        timeslots_layoutmanager = new LinearLayoutManager(this);
        timeslots_recyclerview.setLayoutManager(timeslots_layoutmanager);

        // specify an adapter (see also next example)
        List<Pair<String, String>> list = new ArrayList<>();
        Pair<String, String> p = new Pair<>("AAA", "BBB");
        Pair<String, String> p2 = new Pair<>("CCC", "DDD");
        list.add(p);
        list.add(p2);
        timeslots_adapter = new OptionsListAdapter(getApplicationContext(), list);
        timeslots_recyclerview.setAdapter(timeslots_adapter);
    }

//    void onClickTimeslotsMenu

}