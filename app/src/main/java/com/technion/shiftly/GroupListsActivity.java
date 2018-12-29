package com.technion.shiftly;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.rahimlis.badgedtablayout.BadgedTabLayout;

public class GroupListsActivity extends AppCompatActivity {
    private ViewPager mViewPager;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private GoogleSignInAccount mGoogleSignInAccount;
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
        setContentView(R.layout.activity_group_lists);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.group_list_toolbar);
        final DrawerLayout mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);
        myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawer.openDrawer(Gravity.LEFT);
            }
        });

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        SectionsPageAdapter mSectionsPageAdapter = new SectionsPageAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.groups);
        setupViewPager(mViewPager);

        BadgedTabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.setBadgeText(1, "1");
        setIcons(tabLayout);

        // Drawer menu

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.drawer_about_button: {
                                gotoAbout();
                                break;
                            }
                            case R.id.drawer_signout_button: {
                                mAuth.signOut(); // Firebase Sign-out
                                mGoogleSignInClient.signOut()
                                        .addOnCompleteListener(GroupListsActivity.this, new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                gotoLogin();
                                            }
                                        });
                                gotoLogin();
                                break;
                            }
                        }
                        // close drawer when item is tapped
                        // mDrawerLayout.closeDrawers();

                        // Add code here to update the UI based on the item selected
                        // For example, swap UI fragments here

                        return true;
                    }
                });
    }

    public void gotoAbout() {
        Intent intent = new Intent(this, AboutActiviy.class);
        startActivity(intent);
    }

    public void gotoLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        finish();
        startActivity(intent);
    }

    private void setupViewPager(ViewPager viewPager) {
        SectionsPageAdapter adapter = new SectionsPageAdapter(getSupportFragmentManager());
        adapter.addFragment(new GroupsIBelongFragment(), getString(R.string.groups_i_belong));
        adapter.addFragment(new GroupsIManageFragment(), getString(R.string.groups_i_manage));

        viewPager.setAdapter(adapter);
    }

    private void setIcons(BadgedTabLayout tabLayout) {
        tabLayout.setIcon(0, R.drawable.ic_favorite); // 0 is the position of tab where icon should be added
        tabLayout.setIcon(1, R.drawable.ic_shopping);

    }
}