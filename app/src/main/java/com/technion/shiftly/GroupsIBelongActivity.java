package com.technion.shiftly;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class GroupsIBelongActivity extends Fragment {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
//    private TextView count;
    private String[] mockupDataSet = {"freddie", "john", "brian", "roger", "yakir", "oshri" , "gal", "ron", "shaul" , "dan" ,"irad"};
//    private String[] mockupNumInGroup = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11"};
//    private Drawable photo = getResources().getDrawable(R.drawable.baseline_face_white_18);
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_groups_i_belong, container, false);

        // Replace 'android.R.id.list' with the 'id' of your RecyclerView
        mRecyclerView = (RecyclerView) view.findViewById(R.id.groups_i_belong);
        mLayoutManager = new LinearLayoutManager(this.getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new MyAdapter(mockupDataSet);
        mRecyclerView.setAdapter(mAdapter);
        return view;
    }
}