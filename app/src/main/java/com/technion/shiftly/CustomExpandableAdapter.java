package com.technion.shiftly;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

public class CustomExpandableAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<String> groups;
    private List<Map<String, CheckBox>> children;
    private List<Map<String, Boolean>> checked_items;

    public CustomExpandableAdapter(Context context, List<String> groups, List<Map<String, CheckBox>> children, List<Map<String, Boolean>> checked_items) {
        this.context = context;
        this.groups = groups;
        this.children = children;
        this.checked_items = checked_items;
    }

    @Override
    public int getGroupCount() {
        return groups.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this.children.get(groupPosition).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.groups.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return this.children.get(groupPosition).get(getShiftDayAndLetter(groupPosition,childPosition));
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
        String parent_group = (String) getGroup(groupPosition);
        LayoutInflater parent_inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = parent_inflater.inflate(R.layout.group_items_layout, null);
        convertView.setClickable(false);
        TextView parent_text = convertView.findViewById(R.id.expandable_group_items);
        parent_text.setText(parent_group);
        parent_text.setFocusable(false);
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        // Get the checkbox text
        CheckBox child = ((CheckBox) getChild(groupPosition, childPosition));
        child.setFocusable(false);
        String child_text = (String) child.getText();

        // Set the layout of the convertView
        LayoutInflater child_inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = child_inflater.inflate(R.layout.child_items_layout, null);
        convertView.setClickable(false);

        // Get the layout of the child
        CheckBox child_view = convertView.findViewById(R.id.expandable_child_items);
        child_view.setFocusable(false);
        child_view.setText(child_text);
        if (!checked_items.get(groupPosition).get(getShiftDayAndLetter(groupPosition,childPosition))) {
            child_view.setChecked(false);
        } else {
            child_view.setChecked(true);
        }

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    private String getShiftDayAndLetter(int group_position, int child_position) {
        String day = null,letter = null;
        switch (group_position) {
            case 0: day = "Sunday_"; break;
            case 1: day = "Monday_"; break;
            case 2: day = "Tuesday_"; break;
            case 3: day = "Wednesday_"; break;
            case 4: day = "Thursday_"; break;
            case 5: day = "Friday_"; break;
            case 6: day = "Saturday_"; break;
        }
        switch (child_position) {
            case 0: letter = "a"; break;
            case 1: letter = "b"; break;
            case 2: letter = "c"; break;
        }
        return day + letter;
    }

}
