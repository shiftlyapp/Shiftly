package com.technion.shiftly;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class GroupsIBelongFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private List<String> groupNames;
    private List<Long> groupMembersNum;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_groups_i_belong, container, false);
//        Resources res = getResources();
//        String text = String.format(res.getString(R.string.total_groups_txt), mockupDataSet.length);

        // Replace 'android.R.id.list' with the 'id' of your RecyclerView
        mRecyclerView = (RecyclerView) view.findViewById(R.id.groups_i_belong);
        mLayoutManager = new LinearLayoutManager(this.getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        groupNames = new ArrayList<>();
        groupMembersNum = new ArrayList<>();

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Groups");
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    groupNames.add(snapshot.child("group_name").getValue(String.class));
                    groupMembersNum.add(snapshot.child("members_count").getValue(Long.class));
                }
                mAdapter = new MyAdapter(getContext(), groupNames, groupMembersNum);
                MyAdapter.ItemClickListener listener = new MyAdapter.ItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Intent intent = new Intent(view.getContext(), ScheduleViewActivity.class);
                        startActivity(intent);            }
                };
                ((MyAdapter) mAdapter).setClickListener(listener);
                mRecyclerView.setAdapter(mAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        return view;
    }
}