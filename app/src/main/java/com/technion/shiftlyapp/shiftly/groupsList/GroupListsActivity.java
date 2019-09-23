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
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.technion.shiftlyapp.shiftly.R;
import com.technion.shiftlyapp.shiftly.dataAccessLayer.DataAccess;
import com.technion.shiftlyapp.shiftly.dataTypes.Group;
import com.technion.shiftlyapp.shiftly.dataTypes.User;
import com.technion.shiftlyapp.shiftly.entry.LoginActivity;
import com.technion.shiftlyapp.shiftly.miscellaneous.AboutActivity;
import com.technion.shiftlyapp.shiftly.userUpdate.UserUpdateActivity;
import com.technion.shiftlyapp.shiftly.utility.Constants;
import com.technion.shiftlyapp.shiftly.utility.CustomSnackbar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
    private TabLayout mTabLayout;
    private Toolbar mToolbar;
    private ViewPagerAdapter mPagerAdapter;
    private ViewPager mViewPager;
    private String fullName;
    private CustomSnackbar mSnackbar;
    private ConstraintLayout mLayout;

    private DataAccess dataAccess = new DataAccess();

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
        mToolbar = (Toolbar) findViewById(R.id.group_list_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);
        mSnackbar = new CustomSnackbar(CustomSnackbar.SNACKBAR_DEFAULT_TEXT_SIZE);
        mLayout = (ConstraintLayout) findViewById(R.id.group_lists);

        setToolbarSubtitile();

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
        setDrawerLayout();

        if (getIntent().getExtras() == null ||
                getIntent().getExtras().getInt("FRAGMENT_TO_LOAD") == Constants.GROUPS_I_BELONG_FRAGMENT) {
            mViewPager.setCurrentItem(Constants.GROUPS_I_BELONG_FRAGMENT);
        } else {
            mViewPager.setCurrentItem(Constants.GROUPS_I_MANAGE_FRAGMENT);
        }
    }

    private void setDrawerLayout() {
        final NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener((MenuItem menuItem) -> {
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
                    presentSignoutDialog(navigationView);
                    break;
                }
            }
            return true;
        });
    }

    private void setToolbarSubtitile() {
        String userDisplayName = currentUser.getDisplayName();
        if (userDisplayName==null || userDisplayName.isEmpty()) {
            dataAccess.getUser(currentUser.getUid(), new DataAccess.DataAccessCallback<User>() {
                @Override
                public void onCallBack(User user) {
                    fullName = String.format("%s %s", user.getFirstname(), user.getLastname());
                    mToolbar.setSubtitle(fullName);
                }
            });
        } else {
            fullName = currentUser.getDisplayName();
            mToolbar.setSubtitle(fullName);
        }
    }

    private void presentSignoutDialog(NavigationView navigationView) {
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
        String currUid = currentUser.getUid();
        // Check if the user still manages one or more groups.
        dataAccess.findGroupsBy((group -> group.getAdmin().equals(currUid)),
                new DataAccess.DataAccessCallback<HashMap<String, Group>>() {
                    @Override
                    public void onCallBack(HashMap<String, Group> groupsByAdmin) {
                        // If yes - present a message saying "you have to delete all of the groups you manage first"
                        if (groupsByAdmin.size() > 0) {
                            mSnackbar.show(GroupListsActivity.this, mLayout, GroupListsActivity.this.getResources().getString(R.string.user_deletion_error), CustomSnackbar.SNACKBAR_ERROR, Snackbar.LENGTH_SHORT);
                            // Else proceed
                        } else {
                            // Iterate over the user's groups and remove him from them
                            dataAccess.findGroupsBy((Group group) -> group.getMembers().containsKey(currUid),
                                    (HashMap<String, Group> groupsUserBelongsTo) -> {
                                        HashMap<String, Group> updatedGroups = new HashMap<>();
                                        for (Map.Entry<String, Group> groupEntry : groupsUserBelongsTo.entrySet()) {
                                            Group currentGroup = groupEntry.getValue();

                                            // Reduce members count by one
                                            currentGroup.setMembers_count(currentGroup.getMembers_count() - 1);

                                            // Remove user from group members
                                            Map<String, String> updatedMembers = currentGroup.getMembers();
                                            updatedGroups.remove(currUid);
                                            currentGroup.setMembers(updatedMembers);

                                            // Remove the user from options
                                            HashMap<String, String> updatedOptions = currentGroup.getOptions();
                                            updatedOptions.remove(currUid);
                                            currentGroup.setOptions(updatedOptions);

                                            // Remove the user from schedule
                                            ArrayList<String> updatedSchedule = currentGroup.getSchedule();
                                            for (int i = 0; i < updatedSchedule.size(); i++) {
                                                if (updatedSchedule.get(i).equals(currUid)) {
                                                    updatedSchedule.set(i, Constants.NA);
                                                }
                                            }
                                            updatedGroups.put(groupEntry.getKey(), currentGroup);
                                        }
                                    });
                            // Delete member from Database
                            DataAccess dataAccess = new DataAccess();
                            dataAccess.removeUser(currUid);

                            // Delete member from auth
                            FirebaseAuth.getInstance().getCurrentUser().delete();

                            // Go back to the login activity
                            AccessToken accessToken = AccessToken.getCurrentAccessToken();
                            if (accessToken != null) {
                                LoginManager.getInstance().logOut();
                            } else {
                                mAuth.signOut();
                            }
                            GroupListsActivity.this.startActivity(new Intent(GroupListsActivity.this.getApplicationContext(), LoginActivity.class));
                        }
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