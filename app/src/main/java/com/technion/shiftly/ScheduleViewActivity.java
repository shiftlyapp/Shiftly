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
import com.google.firebase.database.FirebaseDatabase;

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
        Toolbar myToolbar = (Toolbar) findViewById(R.id.schedule_view_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mLayout = (ConstraintLayout) findViewById(R.id.container);

        FloatingActionButton timeslots_fab = findViewById(R.id.timeslots_fab);
        timeslots_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), TimeslotsConfigActivity.class);
                intent.putExtra("GROUP_NAME","");
                startActivity(intent);
            }
        });

      //  mDatabase = FirebaseDatabase.getInstance().getReference("Groups");
       // currentUser = mAuth.getCurrentUser();


        if (true) { //TODO: change
            FloatingActionButton schedule_fab = findViewById(R.id.schedule_fab);
            schedule_fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // 1. Get all the info for the algorithm from the DB
                    Group group = new Group();
                  //  group.setAdmin(currentUser.getUid());
//                    group.setGroup_name();


                    // 2. call the solver and get the results back
                    // Create a new parameter group
//                    ShiftSolver solver = new ShiftSolver();
                    // 3. Display the results in the calendar view

                    showScheduleSnackBar();
                }
            });
        }

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
}