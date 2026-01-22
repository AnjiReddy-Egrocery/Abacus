package com.dst.abacustrainner.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dst.abacustrainner.Activity.LevelTopicActivity;
import com.dst.abacustrainner.Activity.PurchasedActivity;
import com.dst.abacustrainner.Model.CourseListResponse;
import com.dst.abacustrainner.R;

import java.util.ArrayList;
import java.util.List;

public class CourseLevelAdapter extends RecyclerView.Adapter<CourseLevelAdapter.ViewHolder> {
    private Context context;
    private List<CourseListResponse.CourseLevels> levels = new ArrayList<>();
    private String studentId;

    public CourseLevelAdapter(PurchasedActivity purchasedActivity) {
        this.context = purchasedActivity;
    }

    @NonNull
    @Override
    public CourseLevelAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_course_level, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseLevelAdapter.ViewHolder holder, int position) {
        CourseListResponse.CourseLevels level = levels.get(position);
        String levelId = level.getCourseLevelId();
        holder.txtLevelName.setText(level.getCourseLevel());

        // 👉 click kavali ante
        holder.layoutItem.setOnClickListener(v -> {

            Intent intent = new Intent(context, LevelTopicActivity.class);
            intent.putExtra("StudentId",studentId);
            intent.putExtra("LevelId",levelId);
            context.startActivity(intent);

        });
    }

    @Override
    public int getItemCount() {
        return levels == null ? 0 : levels.size();
    }

    public void setLevels(List<CourseListResponse.CourseLevels> levels, String studentId) {
        this.levels.clear();
        this.levels.addAll(levels);
        this.studentId = studentId;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtLevelName;
        LinearLayout layoutItem;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtLevelName = itemView.findViewById(R.id.tvLevelText);
            layoutItem = itemView.findViewById(R.id.layout_item);
        }
    }
}
