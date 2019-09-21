package com.technion.shiftlyapp.shiftly.groupsList;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.technion.shiftlyapp.shiftly.R;
import com.technion.shiftlyapp.shiftly.dataAccessLayer.DataAccess;
import com.technion.shiftlyapp.shiftly.entry.LoginActivity;
import com.technion.shiftlyapp.shiftly.miscellaneous.AboutActivity;
import com.technion.shiftlyapp.shiftly.userUpdate.UserUpdateActivity;
import com.technion.shiftlyapp.shiftly.utility.Constants;
import com.technion.shiftlyapp.shiftly.utility.CustomSnackbar;

import java.util.ArrayList;

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
    private String fullName;
    private DatabaseReference databaseRef;
    private Boolean groupsIManageExist;
    private CustomSnackbar mSnackbar;
    private ConstraintLayout mLayout;

    public String getFullName() {
        return fullName;
    }

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
        groupsIManageExist = false;
        mSnackbar = new CustomSnackbar(CustomSnackbar.SNACKBAR_DEFAULT_TEXT_SIZE);
        mLayout = (ConstraintLayout) findViewById(R.id.group_lists);

        if (userDisplayName==null || userDisplayName.isEmpty()) {
            DatabaseReference db = FirebaseDatabase.getInstance().getReference("Users").child(currentUser.getUid());
            db.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String firstname = dataSnapshot.child("firstname").getValue(String.class);
                    String lastname = dataSnapshot.child("lastname").getValue(String.class);
                    fullName = firstname + " " + lastname;
                    mToolbar.setSubtitle(fullName);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } else {
            fullName = currentUser.getDisplayName();
            mToolbar.setSubtitle(fullName);
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
                            case R.id.drawer_edit_user_button: {
                                gotoUserUpdate();
                                break;
                            }
                            case R.id.drawer_delete_user_button: {
                                presentDeleteUserDialog();
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
                                                LoginManager.getInstance().logOut();
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
        if (getIntent().getExtras() == null ||
                getIntent().getExtras().getInt("FRAGMENT_TO_LOAD") == Constants.GROUPS_I_BELONG_FRAGMENT) {
            mViewPager.setCurrentItem(Constants.GROUPS_I_BELONG_FRAGMENT);
        } else {
            mViewPager.setCurrentItem(Constants.GROUPS_I_MANAGE_FRAGMENT);
        }
    }

    public void presentDeleteUserDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(GroupListsActivity.this, R.style.CustomAlertDialog);
        builder.setMessage(R.string.delete_user_dialog);
        builder.setCancelable(true);
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Delete the user only if the user tapped on YES in the dialog
                deleteUser();
            }
        });
        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog delete_dialog = builder.create();
        delete_dialog.show();
    }

    public void deleteUser() {
        groupsIManageExist = false;

        final DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("Users");
        final ArrayList<String> groupsUserIsMemberOf = new ArrayList<>();

        // Check if the user still manages one or more groups.
        final DatabaseReference groupsRef = FirebaseDatabase.getInstance().getReference("Groups");
        groupsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot groupToCheckAdmin : dataSnapshot.getChildren()) {
                    String admin = String.valueOf(groupToCheckAdmin.child("admin").getValue());
                    if (admin.equals(currentUser.getUid())) {
                        groupsIManageExist = true;
                        break;
                    }
                }
                // If yes - present a message saying "you have to delete all of the groups you manage first"
                if (groupsIManageExist) {
                    mSnackbar.show(GroupListsActivity.this, mLayout, getResources().getString(R.string.user_deletion_error), CustomSnackbar.SNACKBAR_ERROR, Snackbar.LENGTH_SHORT);
                // Else
                } else {
                    usersRef.child(currentUser.getUid()).child("groups").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot groupMemberOf : dataSnapshot.getChildren()) {
                                String group = String.valueOf(groupMemberOf.getValue());
                                // Save the groups that the user is a member of
                                groupsUserIsMemberOf.add(group);
                            }

                            // Delete the user from her/his groups
                            groupsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                // For each group - delete the member from the members section
                                    // And reduce members count by one
                                    for (DataSnapshot group : dataSnapshot.getChildren()) {
                                        if (groupsUserIsMemberOf.contains(group.getKey())) {
                                            final Long old_members_count_num = (Long)dataSnapshot.child(group.getKey()).child("members_count").getValue();
                                            final Long new_members_count_num = (Long)Long.valueOf(old_members_count_num) - 1;

                                            // Delete member
                                            DataAccess dataAccess = new DataAccess();
                                            dataAccess.removeUser(currentUser.getUid());
                                            // Reduce members_count of group by 1
                                            groupsRef.child(group.getKey()).child("members_count").setValue(new_members_count_num);

                                            // Remove the user from options & schedule in the group
                                            if(group.child("options").exists()) {
                                                group.child("options").child(currentUser.getUid()).getRef().removeValue();
                                            }
                                            if(group.child("schedule").exists()) {
                                                int i=0;
                                                for(DataSnapshot current_shift : group.child("schedule").getChildren()) {
                                                    String currentEmployeeId = current_shift.getValue(String.class);
                                                    if(currentEmployeeId.equals(currentUser.getUid())) {
                                                        group.child("schedule").getRef().child(String.valueOf(i)).setValue(Constants.NA);
                                                    }
                                                    i++;
                                                }
                                            }

                                        }
                                    }
                                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                                    user.delete()
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {

                                                }
                                            });
                                    // Go back to the login activity
                                    AccessToken accessToken = AccessToken.getCurrentAccessToken();
                                    if (accessToken != null) {
                                        LoginManager.getInstance().logOut();
                                    } else {
                                        mAuth.signOut();
                                    }
                                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                                    usersRef.child(currentUser.getUid()).removeValue();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                }
                            });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public void gotoAbout() {
        Intent about_intent = new Intent(this, AboutActivity.class);
        startActivity(about_intent);
    }

    public void gotoUserUpdate() {
        Intent update_user_intent = new Intent(this, UserUpdateActivity.class);
        startActivity(update_user_intent);
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