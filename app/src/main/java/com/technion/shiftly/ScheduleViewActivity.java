package com.technion.shiftly;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.HashMap;

public class ScheduleViewActivity extends AppCompatActivity {


    private ConstraintLayout mLayout;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment = null;
            switch (item.getItemId()) {
                case R.id.navigation_daily:
                    fragment = new DailyViewFragment();
                    break;
                case R.id.navigation_weekly:
                    fragment = new WeeklyViewFragment();
                    break;
            }
            return launchFragment(fragment);
        }
    };

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_view);
        Toolbar schedule_view_toolbar = (Toolbar) findViewById(R.id.schedule_view_toolbar);
        setSupportActionBar(schedule_view_toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mLayout = (ConstraintLayout) findViewById(R.id.container);

        // TODO: Make available only if the user is not the admin, otherwise don't present the button
        // TODO: or make it present a toast saying "you are not an employee"
        com.getbase.floatingactionbutton.FloatingActionButton optionsFab = findViewById(R.id.options_fab);
        optionsFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), OptionsListActivity.class);
                String group_id = getIntent().getExtras().getString("GROUP_ID");
                intent.putExtra("GROUP_ID", group_id);
                startActivity(intent);
            }
        });

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        launchFragment(new DailyViewFragment());

    }

    // TODO: later remove duplicate code with login snackbar
    private void showScheduleSnackBar() {
        final Snackbar mySnackbar = Snackbar.make(mLayout, R.string.schedule_generated, Snackbar.LENGTH_INDEFINITE);
        final View snackbarView = mySnackbar.getView();
        final TextView tv = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        } else {
            tv.setGravity(Gravity.CENTER_HORIZONTAL);
        }
        snackbarView.setBackgroundColor(getResources().getColor(R.color.text_color_primary));
        tv.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        tv.setTextSize(22);
        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(snackbarView, "alpha", 0f, 1f);
        fadeIn.setDuration(500);
        fadeIn.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                snackbarView.setVisibility(View.VISIBLE);
                snackbarView.setAlpha(0);
                mySnackbar.setAction(R.string.confirm_schedule, new ConfirmListener());
                mySnackbar.setActionTextColor(getResources().getColor(R.color.background_color));
                mySnackbar.show();
            }
        });
        fadeIn.start();
    }

    public class ConfirmListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            // TODO: Upload the newly created schedule to the DB
        }
    }

    private boolean launchFragment(Fragment fragment) {
        if (fragment == null) {
            return false;
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
        return true;
    }

    private ArrayList<ArrayList<HashMap<String, Boolean>>> makeFullSched() {
        ArrayList<ArrayList<HashMap<String, Boolean>>> options = new ArrayList<>();
        for (int k=0 ; k<7 ; k++) {
            ArrayList<HashMap<String, Boolean>> day1 = new ArrayList<>();
            for (int j = 0; j < 3; j++) {
                HashMap<String, Boolean> shift1 = new HashMap<>();
                for (int i = 0; i < 5; i++) {
                    shift1.put(Integer.toString(i), true);
                }
                day1.add(shift1);
            }
            options.add(day1);
        }
        return options;
    }
}