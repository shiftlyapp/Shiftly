package com.technion.shiftly.groupsList;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.technion.shiftly.R;
import com.technion.shiftly.groupCreation.GroupCreation1Activity;
import com.technion.shiftly.scheduleView.ScheduleViewActivity;
import com.technion.shiftly.utility.Constants;
import com.technion.shiftly.utility.CustomSnackbar;
import com.technion.shiftly.utility.DividerItemDecorator;
import com.venmo.view.TooltipView;

import java.util.ArrayList;
import java.util.List;

public class GroupsIManageFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private List<String> groupsNames;
    private List<Long> groupsMembersCount;
    private List<String> groupsIds;
    private List<String> groupsIconsUrls;
    private LottieAnimationView loading_icon;
    private LinearLayout no_groups_container;
    private CustomSnackbar mSnackbar;
    private Context context;
    private GroupListsActivity activity;
    private View view;
    private TooltipView manage_tooltip;
    private FloatingActionButton create_group_fab;

    private void handleLoadingState(int state) {
        switch (state) {
            case Constants.HIDE_LOADING_ANIMATION:
                manage_tooltip.setVisibility(View.GONE);
                loading_icon.setVisibility(View.GONE);
                create_group_fab.show();
                mRecyclerView.setVisibility(View.VISIBLE);
                break;
            case Constants.SHOW_LOADING_ANIMATION:
                manage_tooltip.setVisibility(View.GONE);
                loading_icon.setVisibility(View.VISIBLE);
                create_group_fab.hide();
                mRecyclerView.setVisibility(View.INVISIBLE);
                break;
            case Constants.EMPTY_GROUPS_COUNT:
                manage_tooltip.setVisibility(View.VISIBLE);
                no_groups_container.setVisibility(View.VISIBLE);
                loading_icon.setVisibility(View.GONE);
                create_group_fab.show();
                mRecyclerView.setVisibility(View.GONE);
                break;
        }
    }

    private void showError(@NonNull DatabaseError error) {
        mSnackbar.show(context, view, error.getMessage(), CustomSnackbar.SNACKBAR_ERROR, Snackbar.LENGTH_SHORT);
    }

    private void loadRecyclerViewData() {
        // ---------------------------- Check current User groups_count ----------------------------
        DatabaseReference mGroupDatabase = FirebaseDatabase.getInstance().getReference("Groups");
        Query query = mGroupDatabase.orderByChild("admin").equalTo(activity.getCurrentUser().getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot current_group : dataSnapshot.getChildren()) {
                        String group_name = current_group.child("group_name").getValue(String.class);
                        Long members_count = current_group.child("members_count").getValue(Long.class);
                        String group_icon_url = current_group.child("group_icon_url").getValue(String.class);
                        groupsIds.add(current_group.getKey());
                        groupsIconsUrls.add(group_icon_url);
                        groupsNames.add(group_name);
                        groupsMembersCount.add(members_count);
                    }
                    mAdapter.notifyDataSetChanged();
                    handleLoadingState(Constants.HIDE_LOADING_ANIMATION);
                } else {
                    handleLoadingState(Constants.EMPTY_GROUPS_COUNT);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                showError(databaseError);
            }
        });
    }

    public void onResume() {
        super.onResume();
        if (mAdapter!=null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_groups_i_manage, container, false);
        activity = (GroupListsActivity) getActivity();
        context = getContext();

        // Getting views loaded with findViewById
        mRecyclerView = (RecyclerView) view.findViewById(R.id.groups_i_manage);
        loading_icon = view.findViewById(R.id.loading_icon_manage);
        LottieAnimationView eye_anim = view.findViewById(R.id.eye_anim_manage);
        no_groups_container = view.findViewById(R.id.no_groups_container_manage);

        create_group_fab = view.findViewById(R.id.create_fab);
        create_group_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(context,GroupCreation1Activity.class));
            }
        });

        manage_tooltip = view.findViewById(R.id.manage_tooltip);
        manage_tooltip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.setVisibility(View.GONE);
            }
        });

        mSnackbar = new CustomSnackbar(CustomSnackbar.SNACKBAR_DEFAULT_TEXT_SIZE);

        // Configuring RecyclerView with A LinearLayout and adding dividers
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        RecyclerView.ItemDecoration dividerItemDecoration = new DividerItemDecorator(ContextCompat.getDrawable(context, R.drawable.recycler_divider));
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(dividerItemDecoration);

        // Setting the animations scale
        eye_anim.setScale(Constants.EYE_ANIM_SCALE);
        loading_icon.setScale(Constants.LOADING_ANIM_SCALE);

        // Show the loading animation upon loading the fragment
        handleLoadingState(Constants.SHOW_LOADING_ANIMATION);

        groupsIds = new ArrayList<>();
        groupsNames = new ArrayList<>();
        groupsIconsUrls = new ArrayList<>();
        groupsMembersCount = new ArrayList<>();
        mAdapter = new GroupsListAdapter(context, groupsNames, groupsMembersCount, groupsIconsUrls);
        mRecyclerView.setAdapter(mAdapter);
        loadRecyclerViewData();
        ((GroupsListAdapter) mAdapter).setClickListener(new GroupsListAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent schedule_view = new Intent(context, ScheduleViewActivity.class);
                schedule_view.putExtra("GROUP_ID", groupsIds.get(position));
                startActivity(schedule_view);
            }
        });
        return view;
    }
}