package com.technion.shiftlyapp.shiftly.groupsList;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseError;
import com.technion.shiftlyapp.shiftly.R;
import com.technion.shiftlyapp.shiftly.dataAccessLayer.DataAccess;
import com.technion.shiftlyapp.shiftly.groupJoin.JoinGroupActivity;
import com.technion.shiftlyapp.shiftly.scheduleView.ScheduleViewActivity;
import com.technion.shiftlyapp.shiftly.utility.Constants;
import com.technion.shiftlyapp.shiftly.utility.CustomSnackbar;
import com.technion.shiftlyapp.shiftly.utility.DividerItemDecorator;
import com.venmo.view.TooltipView;

import java.util.ArrayList;
import java.util.List;

public class GroupsIBelongFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private List<String> groupsName;
    private List<Long> groupsMembersCount;
    private List<String> groupsIds;
    private List<String> groupsIconsUrls;
    private LottieAnimationView loading_icon;
    private LinearLayout no_groups_container;
    private CustomSnackbar mSnackbar;
    private Context context;
    private GroupListsActivity activity;
    private View view;
    private TooltipView belong_tooltip;
    private FloatingActionButton join_group_fab;

    private void initializeRecyclerAnimation() {
        final LayoutAnimationController controller =
                AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_fall_down);
        mRecyclerView.setLayoutAnimation(controller);
    }

    private void handleLoadingState(int state) {
        switch (state) {
            case Constants.HIDE_LOADING_ANIMATION:
                belong_tooltip.setVisibility(View.GONE);
                loading_icon.setVisibility(View.GONE);
                join_group_fab.show();
                mRecyclerView.setVisibility(View.VISIBLE);
                break;
            case Constants.SHOW_LOADING_ANIMATION:
                belong_tooltip.setVisibility(View.GONE);
                loading_icon.setVisibility(View.VISIBLE);
                join_group_fab.hide();
                mRecyclerView.setVisibility(View.INVISIBLE);
                break;
            case Constants.EMPTY_GROUPS_COUNT:
                belong_tooltip.setVisibility(View.VISIBLE);
                no_groups_container.setVisibility(View.VISIBLE);
                loading_icon.setVisibility(View.GONE);
                join_group_fab.show();
                mRecyclerView.setVisibility(View.GONE);
                break;
        }
    }

    private void showError(@NonNull DatabaseError error) {
        mSnackbar.show(context, view, error.getMessage(), CustomSnackbar.SNACKBAR_ERROR, Snackbar.LENGTH_SHORT);
    }

    private void loadRecyclerViewData() {
        DataAccess dataAccess = new DataAccess();
        dataAccess.getUser(activity.getCurrentUser().getUid(), user -> {
            if (user.getGroups_count() != null && user.getGroups_count().equals(0L)) {
                handleLoadingState(Constants.EMPTY_GROUPS_COUNT);
            } else {
                handleLoadingState(Constants.HIDE_LOADING_ANIMATION);
            }
            mAdapter.notifyDataSetChanged();

            for (String groupId : user.getGroups()) {
                groupsIds.add(groupId);
                dataAccess.getGroup(groupId, group -> {
                    groupsIconsUrls.add(group.getGroup_icon_url());
                    groupsName.add(group.getGroup_name());
                    groupsMembersCount.add(group.getMembers_count());
                    mAdapter.notifyDataSetChanged();
                    mRecyclerView.scheduleLayoutAnimation();
                });
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_groups_i_belong, container, false);
        activity = (GroupListsActivity) getActivity();
        context = inflater.getContext();

        // Getting views loaded with findViewById
        mRecyclerView = (RecyclerView) view.findViewById(R.id.groups_i_belong);
        loading_icon = view.findViewById(R.id.loading_icon_belong);
        LottieAnimationView eye_anim = view.findViewById(R.id.eye_anim_belong);
        no_groups_container = view.findViewById(R.id.no_groups_container_belong);

        join_group_fab = view.findViewById(R.id.join_fab);
        join_group_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent join_group_intent = new Intent(context, JoinGroupActivity.class);
                join_group_intent.putExtra("FULL_NAME", activity.getFullName());
                startActivity(join_group_intent);
            }
        });

        belong_tooltip = view.findViewById(R.id.belong_tooltip);
        belong_tooltip.setOnClickListener(new View.OnClickListener() {
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
        initializeRecyclerAnimation();
        // Setting the animations scale
        eye_anim.setScale(Constants.EYE_ANIM_SCALE);
        loading_icon.setScale(Constants.LOADING_ANIM_SCALE);

        // Show the loading animation upon loading the fragment
        handleLoadingState(Constants.SHOW_LOADING_ANIMATION);

        groupsIds = new ArrayList<>();
        groupsName = new ArrayList<>();
        groupsMembersCount = new ArrayList<>();
        groupsIconsUrls = new ArrayList<>();
        mAdapter = new GroupsListAdapter(context, groupsName, groupsMembersCount, groupsIconsUrls);
        mRecyclerView.setAdapter(mAdapter);
        loadRecyclerViewData();
        ((GroupsListAdapter) mAdapter).setClickListener(new GroupsListAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent schedule_view = new Intent(context, ScheduleViewActivity.class);
                schedule_view.putExtra("GROUP_ID", groupsIds.get(position));
                schedule_view.putExtra("EMPLOYEE_ID", ((GroupListsActivity) getActivity()).getCurrentUser().getUid());
                startActivity(schedule_view);
            }
        });

        return view;
    }

}