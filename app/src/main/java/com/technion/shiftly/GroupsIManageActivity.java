package com.technion.shiftly;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class GroupsIManageActivity extends Fragment {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    //    private TextView count;
    private String[] mockupDataSet = {"mfreddie", "mjohn", "mbrian", "mroger", "myakir", "moshri" , "mgal", "mron", "mshaul" , "mdan" ,"mirad"};
    private String[] mockupNumInGroup = {"11", "22", "33", "44", "55", "66", "77", "88", "99", "101", "111"};
//    private Drawable photo = getResources().getDrawable(R.drawable.baseline_face_white_18);

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_groups_i_manage, container, false);

        // Replace 'android.R.id.list' with the 'id' of your RecyclerView
        mRecyclerView = (RecyclerView) view.findViewById(R.id.groups_i_manage);
        mLayoutManager = new LinearLayoutManager(this.getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new MyAdapter(getContext(), mockupDataSet, mockupNumInGroup);
        MyAdapter.ItemClickListener listener = new MyAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                ViewGroup viewGroup = (ViewGroup) view;
                for (int i = 0; i < viewGroup .getChildCount(); i++) {
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