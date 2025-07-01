package com.dst.abacustrainner.Activity;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.dst.abacustrainner.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CourseExpandableAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<String> courseList;
    private HashMap<String, List<String>> levelMap;
    private HashMap<String, String> courseDescriptions;

    public CourseExpandableAdapter(Context context, List<String> courseList, HashMap<String, List<String>> levelMap, HashMap<String, String> courseDescriptions) {
        this.context = context;
        this.courseList = courseList != null ? courseList : new ArrayList<>();
        this.levelMap = levelMap != null ? levelMap : new HashMap<>();
        this.courseDescriptions = courseDescriptions != null ? courseDescriptions : new HashMap<>();
    }

    @Override
    public int getGroupCount() {
        return courseList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return levelMap.get(courseList.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return courseList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return levelMap.get(courseList.get(groupPosition)).get(childPosition);
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
        return false;
    }

    // Group view (Course row with info icon)
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String courseTitle = (String) getGroup(groupPosition);

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.course_group_row, parent, false);
        }

        TextView txtCourse = convertView.findViewById(R.id.txtCourseName);
        ImageView btnInfo = convertView.findViewById(R.id.btnInfo);

        txtCourse.setText(courseTitle);

        btnInfo.setOnClickListener(v -> {
            String description = courseDescriptions.get(courseTitle);
            new AlertDialog.Builder(context)
                    .setTitle(courseTitle)
                    .setMessage(description != null ? description : "No details available.")
                    .setPositiveButton("OK", null)
                    .show();
        });

        return convertView;
    }

    // Child view (Level rows)
    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        String level = (String) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
        }

        TextView txtLevel = convertView.findViewById(android.R.id.text1);
        txtLevel.setText(level);

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}