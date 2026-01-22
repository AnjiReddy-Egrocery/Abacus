package com.dst.abacustrainner.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dst.abacustrainner.Activity.CourseDetailActivity;
import com.dst.abacustrainner.Activity.CoursesActivity;
import com.dst.abacustrainner.Model.CourseResult;
import com.dst.abacustrainner.R;

import java.util.ArrayList;
import java.util.List;

public class CoursesAdapter extends RecyclerView.Adapter<CoursesAdapter.ViewHolder> {
    private Context context;
    private List<CourseResult> list = new ArrayList<>();
    public CoursesAdapter(CoursesActivity coursesActivity) {
        this.context = coursesActivity;
    }

    @NonNull
    @Override
    public CoursesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_course_type1, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CoursesAdapter.ViewHolder holder, int position) {
        CourseResult item = list.get(position);

        String name = item.getCourseType();
        String levels = getLevelCount(name);

        String coursesId = item.getCourseTypeId();

        holder.tvName.setText(name + " " + levels);

        //holder.tvName.setText(item.getCourseType());
        holder.tvAge.setText("Age group: " + getAgeRange(item.getCourseType()));

        if (item.getCourseType().toLowerCase().contains("abacus junior")) {
            holder.ivIcon.setImageResource(R.drawable.abacusjunior);
        } else if (item.getCourseType().toLowerCase().contains("abacus senior")) {
            holder.ivIcon.setImageResource(R.drawable.abacussenior);
        } else if (item.getCourseType().toLowerCase().contains("vedic maths")){
            holder.ivIcon.setImageResource(R.drawable.vedicmaths);
        }

        holder.buttonSubscribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(context, CourseDetailActivity.class);
                intent.putExtra("CoursesTypeId",coursesId);
                context.startActivity(intent);
            }
        });
    }

    private String getLevelCount(String name) {
        if (name == null) return "(0 Levels)";

        switch (name.trim()) {
            case "Abacus Junior":
                return "(6 Levels)";
            case "Abacus Senior":
                return "(10 Levels)";
            case "Vedic Maths":
                return "(4 Levels)";
            default:
                return "(0 Levels)"; // default
        }
    }



    private String getAgeRange(String courseType) {
        if (courseType == null) return "(5 - 16 Years)"; // default static

        switch (courseType.toLowerCase().trim()) {
            case "abacus junior":
                return "(5 - 8 Years)";
            case "abacus senior":
                return "(8 - 11 Years)";
            case "vedic maths":
                return "(11 - 14 Years)";
            default:
                return "(5 - 16 Years)"; // default static
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setData(List<CourseResult> result) {
        this.list = result;
        notifyDataSetChanged();

    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvName, tvAge;
        ImageView ivIcon;
        Button buttonSubscribe;
        public ViewHolder(@NonNull View itemView) {

            super(itemView);

            ivIcon = itemView.findViewById(R.id.imgCourse);
            tvName  = itemView.findViewById(R.id.tvCourseTitle);
            tvAge  = itemView.findViewById(R.id.tvAgeGroup);

            buttonSubscribe = itemView.findViewById(R.id.btnPurchase1);
        }
    }
}
