package com.dst.abacustrainner.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dst.abacustrainner.Activity.CourseLevelActivity;
import com.dst.abacustrainner.Model.CourseTypeLevel;
import com.dst.abacustrainner.R;

import java.util.ArrayList;
import java.util.List;

public class CourseTypeLevelAdapter extends RecyclerView.Adapter<CourseTypeLevelAdapter.ViewHolder> {

    private Context context;
    private List<CourseTypeLevel> levelList = new ArrayList<>();
    private String studentId;



    public CourseTypeLevelAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public CourseTypeLevelAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_course_type_level, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseTypeLevelAdapter.ViewHolder holder, int position) {

        CourseTypeLevel level = levelList.get(position);

        String CourseLevel = level.getCourseLevel();
        String CourseLevelId = level.getCourseLevelId();

        holder.txtLevel.setText(CourseLevel);

        holder.layoutLevel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CourseLevelActivity.class);
                intent.putExtra("StudentId",studentId);
                intent.putExtra("CourseLevelId",CourseLevelId);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return levelList.size();
    }

    public void setLevels(List<CourseTypeLevel> allLevels, String studentId) {
        this.levelList = allLevels;
        this.studentId = studentId;
        notifyDataSetChanged();

    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtLevel;
        LinearLayout layoutLevel;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtLevel = itemView.findViewById(R.id.tvcoursetypelevelname);
            layoutLevel = itemView.findViewById(R.id.layout_level);
        }
    }



}
