package com.technion.shiftly;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.airbnb.lottie.LottieAnimationView;
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

    private List<String> groupNames;
    private List<Long> groupMembersNum;
    private View view;
    private LottieAnimationView loading_icon;
    private Resources res;

    private void loadRecyclerViewData() {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("Groups");
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() > 0) {
                    loading_icon.setVisibility(View.GONE);
                    view.setBackground(res.getDrawable(R.drawable.gradient_bg));
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        groupNames.add(snapshot.child("group_name").getValue(String.class));
                        groupMembersNum.add(snapshot.child("members_count").getValue(Long.class));
                    }
                } else  {
                    loading_icon.setVisibility(View.VISIBLE);
                }
                if (getActivity() != null) {
                    mAdapter = new MyAdapter(getContext(), groupNames, groupMembersNum);
                    MyAdapter.ItemClickListener listener = new MyAdapter.ItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {
                            Intent intent = new Intent(view.getContext(), ScheduleViewActivity.class);
                            startActivity(intent);
                        }
                    };
                    ((MyAdapter) mAdapter).setClickListener(listener);
                    mRecyclerView.setAdapter(mAdapter);
                    mRecyclerView.setVisibility(View.VISIBLE);
                    mRecyclerView.setBackground(res.getDrawable(R.drawable.recycler_bg));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_groups_i_belong, container, false);
        res = view.getResources();
        mRecyclerView = (RecyclerView) view.findViewById(R.id.groups_i_belong);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this.getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setVisibility(View.GONE); // Hide the recycler view until recycler is loaded
        loading_icon = view.findViewById(R.id.loading_icon_belong);
        loading_icon.setScale(0.2f);
        groupNames = new ArrayList<>();
        groupMembersNum = new ArrayList<>();
        loadRecyclerViewData();
        return view;
    }
}