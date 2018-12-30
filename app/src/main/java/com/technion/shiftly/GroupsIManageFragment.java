package com.technion.shiftly;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroupsIManageFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;

    private List<String> groupsName;
    private List<Long> groupsMembersCount;
    private Map<String, String> groupsIdMap;
    private LottieAnimationView loading_icon;
    private Resources res;

    private void loadRecyclerViewData() {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("Groups");
        ChildEventListener cl = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String group_name = dataSnapshot.child("group_name").getValue(String.class);
                Long members_count = dataSnapshot.child("members_count").getValue(Long.class);
                groupsName.add(group_name);
                groupsMembersCount.add(members_count);
                groupsIdMap.put(dataSnapshot.getKey(), group_name);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String changed_group_name = dataSnapshot.child("group_name").getValue(String.class);
                String key = dataSnapshot.getKey(); // UID OF GROUP
                String old_group_name = groupsIdMap.get(key);
                if (changed_group_name != null) {
                    int idx = groupsName.indexOf(old_group_name);
                    groupsName.set(idx, changed_group_name);
                    groupsIdMap.put(key, changed_group_name);
                }
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                String removed_group_name = dataSnapshot.child("group_name").getValue(String.class);
                String key = dataSnapshot.getKey(); // UID OF GROUP
                if (removed_group_name != null) {
                    groupsName.remove(removed_group_name);
                    groupsIdMap.remove(key);
                }
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        mDatabase.addChildEventListener(cl);

        ValueEventListener val_li = new ValueEventListener() {
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() > 0) {
                    loading_icon.setVisibility(View.GONE);
                }
                if (getActivity() != null) {
                    mRecyclerView.setVisibility(View.VISIBLE);
                    mRecyclerView.setBackground(res.getDrawable(R.drawable.recycler_bg));
                    mAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };
        mDatabase.addListenerForSingleValueEvent(val_li);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_groups_i_manage, container, false);
        res = view.getResources();
        mRecyclerView = (RecyclerView) view.findViewById(R.id.groups_i_manage);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this.getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setVisibility(View.GONE); // Hide the recycler view until recycler is loaded
        loading_icon = view.findViewById(R.id.loading_icon_manage);
        loading_icon.setScale(0.2f);
        FloatingActionButton add_fab = view.findViewById(R.id.add_fab);
        add_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(view.getContext(), GroupCreation1Activity.class));
            }
        });
        groupsIdMap = new HashMap<>();
        groupsName = new ArrayList<>();
        groupsMembersCount = new ArrayList<>();
        loadRecyclerViewData();
        mAdapter = new MyAdapter(getContext(), groupsName, groupsMembersCount);
        MyAdapter.ItemClickListener listener = new MyAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(view.getContext(), ScheduleViewActivity.class);
                startActivity(intent);
            }
        };
        ((MyAdapter) mAdapter).setClickListener(listener);
        mRecyclerView.setAdapter(mAdapter);
        return view;
    }
}

