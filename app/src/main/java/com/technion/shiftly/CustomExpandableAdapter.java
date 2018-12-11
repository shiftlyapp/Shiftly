package com.technion.shiftly;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

public class CustomExpandableAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<String> groups;
    private HashMap<String, List<String>> children;

    public CustomExpandableAdapter(Context context, List<String> groups, HashMap<String, List<String>> children) {
        this.context = context;
        this.groups = groups;
        this.children = children;
    }

    @Override
    public int getGroupCount() {
        return this.groups.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this.children.get(this.groups.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.groups.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return this.children.get(this.groups.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String parent_group = (String)getGroup(groupPosition);
        LayoutInflater parent_inflater = (LayoutInflater)this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = parent_inflater.inflate(R.layout.group_items_layout, null);
        TextView parent_text = convertView.findViewById(R.id.expandable_group_items);
        parent_text.setText(parent_group);
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        String child_group = (String)getChild(groupPosition, childPosition);
        LayoutInflater child_inflater = (LayoutInflater)this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = child_inflater.inflate(R.layout.child_items_layout, null);
        TextView child_text = convertView.findViewById(R.id.expandable_child_items);
        child_text.setText(child_group);
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }


}
