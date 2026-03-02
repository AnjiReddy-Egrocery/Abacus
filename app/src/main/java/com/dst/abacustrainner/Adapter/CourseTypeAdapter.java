package com.dst.abacustrainner.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dst.abacustrainner.Activity.AllocatedCoursesActivity;
import com.dst.abacustrainner.Activity.CourseLevelActivity;
import com.dst.abacustrainner.Model.CourseType;
import com.dst.abacustrainner.R;

import java.util.ArrayList;
import java.util.List;

public class CourseTypeAdapter extends RecyclerView.Adapter<CourseTypeAdapter.ViewHolder> {

    private Context context;
    private List<CourseType> courseTypeList = new ArrayList<>();
    private String studentId;

    public CourseTypeAdapter(AllocatedCoursesActivity allocatedCoursesActivity) {
        this.context = allocatedCoursesActivity;

    }

    @NonNull
    @Override
    public CourseTypeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_course_type, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseTypeAdapter.ViewHolder holder, int position) {

        CourseType courseType = courseTypeList.get(position);

        String courseTpeText = courseType.getCourseType();
        holder.tvCourseTpe.setText(courseTpeText);


        holder.recyclerLevels.setLayoutManager(
                new LinearLayoutManager(context));

        CourseTypeLevelAdapter levelAdapter =
                new CourseTypeLevelAdapter(context);

        holder.recyclerLevels.setAdapter(levelAdapter);

        // ✅ ONLY THIS TYPE LEVELS
        levelAdapter.setLevels(courseType.getCourseLevels(), studentId);




    }

    @Override
    public int getItemCount() {
        return courseTypeList.size();
    }

    public void setCourseTypes(List<CourseType> courseTypes, String studentId) {
        this.courseTypeList = courseTypes;
        this.studentId = studentId;
        notifyDataSetChanged();

    }

    public class ViewHolder  extends  RecyclerView.ViewHolder {

        TextView tvCourseTpe;
        ImageView imageCourse;

        RecyclerView recyclerLevels;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvCourseTpe = itemView.findViewById(R.id.tvcoursetypename);
           // imageCourse = itemView.findViewById(R.id.imgcourse);
           // layoutCourse = itemView.findViewById(R.id.layout_course_type);
            recyclerLevels = itemView.findViewById(R.id.recycler_levels);
        }
    }
}
