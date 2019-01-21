package com.technion.shiftly.groupsList;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.technion.shiftly.R;
import com.technion.shiftly.entry.LoginActivity;
import com.technion.shiftly.miscellaneous.AboutActivity;
import com.technion.shiftly.utility.Constants;

public class GroupListsActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    public FirebaseStorage getStorage() {
        return mStorage;
    }

    public StorageReference getStorageRef() {
        return mStorageRef;
    }

    private FirebaseStorage mStorage;
    private StorageReference mStorageRef;
    private GoogleSignInAccount mGoogleSignInAccount;
    private TabLayout mTabLayout;
    private Toolbar mToolbar;
    private ViewPagerAdapter mPagerAdapter;
    private ViewPager mViewPager;

    public ImageView getDel_group() {
        return del_group;
    }

    private GoogleSignInClient mGoogleSignInClient;
    private ImageView del_group;

    public Toolbar getmToolbar() {
        return mToolbar;
    }

    public FirebaseUser getCurrentUser() {
        return currentUser;
    }

    public TabLayout getmTabLayout() {
        return mTabLayout;
    }

    private void logOut() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (currentUser == null) {
            logOut();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_lists);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        mStorage = FirebaseStorage.getInstance();
        mStorageRef = mStorage.getReference();
        String userDisplayName = currentUser.getDisplayName();
        mToolbar = (Toolbar) findViewById(R.id.group_list_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);

        if (userDisplayName==null || userDisplayName.isEmpty()) {
            DatabaseReference db = FirebaseDatabase.getInstance().getReference("Users").child(currentUser.getUid());
            db.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String firstname = dataSnapshot.child("firstname").getValue(String.class);
                    String lastname = dataSnapshot.child("lastname").getValue(String.class);
                    mToolbar.setSubtitle(firstname + " " + lastname);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } else {
            mToolbar.setSubtitle(currentUser.getDisplayName());
        }

        del_group = (ImageView) findViewById(R.id.del_group);
        final DrawerLayout mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawer.openDrawer(Gravity.LEFT);
            }
        });

        // Fetch groups from DB
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Set tab layout
        mViewPager = (ViewPager) findViewById(R.id.groups);
        setupViewPager(mViewPager);

        // Tab layout
        mTabLayout = findViewById(R.id.tabs);
        mTabLayout.setupWithViewPager(mViewPager);
        setupTabIcons(mPagerAdapter);

        // Drawer layout
        final NavigationView navigationView = findViewById(R.id.nav_view);
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
                                AlertDialog.Builder signoutDialogBuilder = new AlertDialog.Builder(navigationView.getContext(), R.style.CustomAlertDialog);
                                signoutDialogBuilder.setMessage(Constants.SIGNOUT_MESSAGE);
                                signoutDialogBuilder.setCancelable(true);
                                signoutDialogBuilder.setPositiveButton(
                                        Constants.YES,
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.cancel();
                                                mAuth.signOut(); // Firebase Sign-out
                                                mGoogleSignInClient.signOut()
                                                        .addOnCompleteListener(GroupListsActivity.this, new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                gotoLogin();
                                                            }
                                                        });
                                                gotoLogin();
                                            }
                                        });
                                signoutDialogBuilder.setNegativeButton(
                                        Constants.NO,
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.cancel();
                                            }
                                        });
                                AlertDialog signoutDialog = signoutDialogBuilder.create();
                                signoutDialog.show();
                                break;
                            }
                        }
                        return true;
                    }
                });
        int intent_fragment = getIntent().getExtras().getInt("FRAGMENT_TO_LOAD");
        if (intent_fragment == Constants.GROUPS_I_MANAGE_FRAGMENT) {
            mViewPager.setCurrentItem(Constants.GROUPS_I_MANAGE_FRAGMENT);
        } else if (intent_fragment == Constants.GROUPS_I_BELONG_FRAGMENT) {
            mViewPager.setCurrentItem(Constants.GROUPS_I_BELONG_FRAGMENT);
        }
    }

    public void gotoAbout() {
        Intent about_intent = new Intent(this, AboutActivity.class);
        startActivity(about_intent);
    }

    public void gotoLogin() {
        Intent login_intent = new Intent(this, LoginActivity.class);
        startActivity(login_intent);
        finish();
    }

    private void setupTabIcons(ViewPagerAdapter pagerAdapter) {
        for (int i = 0; i < mTabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = mTabLayout.getTabAt(i);
            if (tab != null) {
                tab.setCustomView(pagerAdapter.getTabView(i));
            }
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        mPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), this);
        mPagerAdapter.addFragment(new GroupsIBelongFragment(), getString(R.string.groups_i_belong));
        mPagerAdapter.addFragment(new GroupsIManageFragment(), getString(R.string.groups_i_manage));
        viewPager.setAdapter(mPagerAdapter);
    }
}