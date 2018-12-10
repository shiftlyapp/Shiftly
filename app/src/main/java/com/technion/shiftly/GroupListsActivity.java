package com.technion.shiftly;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;

public class GroupListsActivity extends FragmentActivity {
    private SectionsPageAdapter mSectionsPageAdapter;

    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_lists);

        mSectionsPageAdapter = new SectionsPageAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.groups);
        setupViewPager(mViewPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        setIcons(tabLayout);
    }

    private void setupViewPager(ViewPager viewPager) {
        SectionsPageAdapter adapter = new SectionsPageAdapter(getSupportFragmentManager());
        adapter.addFragment(new GroupsIBelongActivity(), getString(R.string.groups_i_belong));
        adapter.addFragment(new GroupsIManageActivity(), getString(R.string.groups_i_manage));

        viewPager.setAdapter(adapter);
    }

    private void setIcons(TabLayout tabLayout) {
        tabLayout.getTabAt(0).setIcon(R.drawable.sharp_sentiment_very_satisfied_white_18);
        tabLayout.getTabAt(1).setIcon(R.drawable.sharp_sentiment_very_satisfied_white_18);

    }
}