package com.technion.shiftly;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GroupsIManageFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private List<String> groupsName;
    private List<Long> groupsMembersCount;
    private Set<String> groupsIds;
    private LottieAnimationView loading_icon;
    private LinearLayout no_groups_container;
    private CustomSnackbar mSnackbar;
    private Context context;
    private GroupListsActivity activity;
    private View view;
    private Resources resources;

    private void handleLoadingState(int state) {
        switch (state) {
            case Constants.HIDE_LOADING_ANIMATION:
                loading_icon.setVisibility(View.GONE);
                mRecyclerView.setVisibility(View.VISIBLE);
                break;
            case Constants.SHOW_LOADING_ANIMATION:
                loading_icon.setVisibility(View.VISIBLE);
                mRecyclerView.setVisibility(View.INVISIBLE);
                break;
            case Constants.EMPTY_GROUPS_COUNT:
                no_groups_container.setVisibility(View.VISIBLE);
                loading_icon.setVisibility(View.GONE);
                mRecyclerView.setVisibility(View.GONE);
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
                        groupsName.add(group_name);
                        groupsMembersCount.add(members_count);
                    }
                    handleLoadingState(Constants.HIDE_LOADING_ANIMATION);
                    mAdapter.notifyDataSetChanged();
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

    private void handleLongPress(View view) {
        activity.getDel_group().setVisibility(View.VISIBLE);
        activity.getmToolbar().setTitle("");
        activity.getmToolbar().setBackgroundColor(resources.getColor(R.color.colorPrimaryLight));
        activity.getmTabLayout().setBackgroundColor(resources.getColor(R.color.colorPrimaryLight));
        view.setBackgroundColor(resources.getColor(R.color.list_item_bg_pressed));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(resources.getColor(R.color.colorPrimaryLight_Bar));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_groups_i_manage, container, false);
        activity = (GroupListsActivity) getActivity();
        context = getContext();
        resources = getResources();

        // Getting views loaded with findViewById
        mRecyclerView = (RecyclerView) view.findViewById(R.id.groups_i_manage);
        loading_icon = view.findViewById(R.id.loading_icon_manage);
        LottieAnimationView eye_anim = view.findViewById(R.id.rubberhose_anim_manage);
        no_groups_container = view.findViewById(R.id.no_groups_container_manage);
        view.findViewById(R.id.create_fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(context,GroupCreation1Activity.class));
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

        groupsIds = new HashSet<>();
        groupsName = new ArrayList<>();
        groupsMembersCount = new ArrayList<>();
        mAdapter = new GroupsListAdapter(context, groupsName, groupsMembersCount);
        mRecyclerView.setAdapter(mAdapter);
        loadRecyclerViewData();
        return view;
    }
}