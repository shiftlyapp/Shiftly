package com.technion.shiftly;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;


public class GroupsIManageActivity extends Activity {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private String[] dataset;  //mockup data set
    private TextView count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups_i_manage);
        mRecyclerView = (RecyclerView) findViewById(R.id.groups_i_manage);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        String num = Integer.toString(mAdapter.getItemCount());
        count.setText("you have " + num + " groups!");
        mLayoutManager.addView(count);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        mAdapter = new GroupListItemAdapter(dataset);
        mRecyclerView.setAdapter(mAdapter);


    }
    // ...
}