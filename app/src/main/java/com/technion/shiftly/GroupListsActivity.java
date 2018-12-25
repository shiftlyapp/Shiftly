package com.technion.shiftly;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

public class GroupListsActivity extends AppCompatActivity {
    private SectionsPageAdapter mSectionsPageAdapter;
    private ViewPager mViewPager;
    private DrawerLayout mDrawerLayout;
    private Button aboutButton;
    private Button signOutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_lists);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.group_list_toolbar);
        final DrawerLayout mDrawer = (DrawerLayout)findViewById(R.id.drawer_layout);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);
        myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawer.openDrawer(Gravity.LEFT);
            }
        });

        mSectionsPageAdapter = new SectionsPageAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.groups);
        setupViewPager(mViewPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

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
                                FirebaseAuth.getInstance().signOut();
                                finish();
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
        startActivity(intent);
    }
    private void setupViewPager(ViewPager viewPager) {
        SectionsPageAdapter adapter = new SectionsPageAdapter(getSupportFragmentManager());
        adapter.addFragment(new GroupsIBelongActivity(), getString(R.string.groups_i_belong));
        adapter.addFragment(new GroupsIManageActivity(), getString(R.string.groups_i_manage));

        viewPager.setAdapter(adapter);
    }

    private void setIcons(TabLayout tabLayout) {
        tabLayout.getTabAt(0).setIcon(R.drawable.groups_belong);
        tabLayout.getTabAt(1).setIcon(R.drawable.groups_managed);

    }
}