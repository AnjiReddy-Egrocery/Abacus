package com.dst.abacustrainner.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dst.abacustrainner.Activity.LevelTopicActivity;
import com.dst.abacustrainner.Model.CourseListResponse;
import com.dst.abacustrainner.R;

import java.util.ArrayList;
import java.util.List;

public class CourseLevelsAdapter extends RecyclerView.Adapter<CourseLevelsAdapter.ViewHolder> {

    private Context context;
    private List<CourseListResponse.CourseLevels> levels = new ArrayList<>();
    private String studentId;

    public CourseLevelsAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_courselevel, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        CourseListResponse.CourseLevels level = levels.get(position);

        String lavelName = level.getCourseLevel();

        holder.txtLevelName.setText(lavelName);

        holder.layoutItem.setOnClickListener(v -> {
            String levelId = level.getCourseLevelId();

            Log.d("LEVEL_DEBUG", "Clicked LevelId: " + levelId);

            Intent intent = new Intent(context, LevelTopicActivity.class);
            intent.putExtra("StudentId", studentId);   // 👈 important
            intent.putExtra("LevelId", levelId);
            intent.putExtra("LevelName",lavelName);

            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return levels.size();
    }

    public void setLevels(List<CourseListResponse.CourseLevels> list, String studentId) {
        this.levels.clear();
        if (list != null) {
            this.levels.addAll(list);
        }
        this.studentId = studentId;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtLevelName;
        LinearLayout layoutItem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtLevelName = itemView.findViewById(R.id.tvLevelText);
            layoutItem = itemView.findViewById(R.id.layout_item);
        }
    }
}
