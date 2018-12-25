package com.technion.shiftly;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class GroupsIManageActivity extends Fragment {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<String> groupNames;
    private List<Long> groupMembersNum;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,@Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_groups_i_manage, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.groups_i_manage);
        mLayoutManager = new LinearLayoutManager(this.getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        FloatingActionButton fab = view.findViewById(R.id.add_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), GroupCreationActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

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