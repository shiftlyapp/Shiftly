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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

import static com.basgeekball.awesomevalidation.ValidationStyle.BASIC;

// The first activity of the group creation process.
// In this activity the future admin sets the group name.

public class TimeslotsListActivity extends AppCompatActivity {

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
        setContentView(R.layout.activity_config_timeslots);
        timeslots_recyclerview = findViewById(R.id.ts_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        timeslots_recyclerview.setHasFixedSize(true);

        // use a linear layout manager
        timeslots_layoutmanager = new LinearLayoutManager(this);
        timeslots_recyclerview.setLayoutManager(timeslots_layoutmanager);

        // specify an adapter (see also next example)
        List<Pair<String, String>> list = new ArrayList<>();
        Pair<String, String> p = new Pair<>("AAA", "BBB");
        list.add(p);
        timeslots_adapter = new TimeslotsListAdapter(getApplicationContext(), list);
        timeslots_recyclerview.setAdapter(timeslots_adapter);
    }

//    void onClickTimeslotsMenu

}