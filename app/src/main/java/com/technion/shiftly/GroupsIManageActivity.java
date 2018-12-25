package com.technion.shiftly;

import android.content.Intent;
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

public class GroupsIManageActivity extends Fragment {

    private String[] mockupDataSet = {"Doctors", "Gardeners", "Cleaners"};
    private String[] mockupNumInGroup = {"7", "4", "12"};

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,@Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_groups_i_manage, container, false);

        RecyclerView mRecyclerView = (RecyclerView) view.findViewById(R.id.groups_i_manage);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this.getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        FloatingActionButton fab = view.findViewById(R.id.add_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), GroupCreationActivity.class);
                startActivity(intent);
            }
        });
        RecyclerView.Adapter mAdapter = new MyAdapter(getContext(), mockupDataSet, mockupNumInGroup);
        MyAdapter.ItemClickListener listener = new MyAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                ViewGroup viewGroup = (ViewGroup) view;
                for (int i = 0; i < viewGroup.getChildCount(); i++) {
                    View child = viewGroup.getChildAt(i);
                    child.setPressed(true);
                }
                return;
            }
        };
        ((MyAdapter) mAdapter).setClickListener(listener);
        mRecyclerView.setAdapter(mAdapter);
        return view;
    }
}