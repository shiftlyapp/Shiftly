package com.technion.shiftly;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class GroupsIBelongActivity extends Fragment {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    //    private TextView count;
    private String[] mockupDataSet = {"Doctors", "Cleaners", "Pilots", "Teachers", "Policemen"};
    private String[] mockupNumInGroup = {"7", "12","4","8","6"};
//    private Drawable photo = getResources().getDrawable(R.drawable.baseline_face_white_18);

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_groups_i_belong, container, false);
//        Resources res = getResources();
//        String text = String.format(res.getString(R.string.total_groups_txt), mockupDataSet.length);

        // Replace 'android.R.id.list' with the 'id' of your RecyclerView
        mRecyclerView = (RecyclerView) view.findViewById(R.id.groups_i_belong);
        mLayoutManager = new LinearLayoutManager(this.getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new MyAdapter(getContext(), mockupDataSet, mockupNumInGroup);
        MyAdapter.ItemClickListener listener = new MyAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(view.getContext(), ScheduleViewActivity.class);
                startActivity(intent);            }
        };
        ((MyAdapter) mAdapter).setClickListener(listener);
        mRecyclerView.setAdapter(mAdapter);
        return view;
    }
}