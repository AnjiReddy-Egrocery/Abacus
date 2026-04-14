package com.dst.abacustrainner.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dst.abacustrainner.Activity.CourseDetailActivity;
import com.dst.abacustrainner.Model.CourseLevel;
import com.dst.abacustrainner.R;


import java.util.ArrayList;
import java.util.List;

public class LevelsAdapter extends RecyclerView.Adapter<LevelsAdapter.ViewHolder> {
     Context mContext;
    List<CourseLevel> levelList = new ArrayList<>();
    OnLevelSelectListener listener;


    public LevelsAdapter(CourseDetailActivity courseDetailActivity, OnLevelSelectListener listener) {
        this.mContext = courseDetailActivity;
        this.listener = listener;


    }

    @NonNull
    @Override
    public LevelsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.levels_adapter,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LevelsAdapter.ViewHolder holder, int position) {
        CourseLevel level = levelList.get(position);
        holder.textViewLevelName.setText(level.getCourseLevel());

        if (level.getPrice() != null) {
            holder.levelPrice.setText("₹" + level.getPrice());
        } else {
            holder.levelPrice.setText("—");
        }

        if (level.isSubscribed()) {

            holder.checkBox.setVisibility(View.GONE);
            holder.tvSubscribed.setVisibility(View.VISIBLE);

        } else {

            holder.checkBox.setVisibility(View.VISIBLE);
            holder.tvSubscribed.setVisibility(View.GONE);
        }



        holder.checkBox.setOnCheckedChangeListener(null);
        holder.checkBox.setChecked(level.isSelected());

        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {

            Log.e("Reddy",
                    "Clicked CourseLevelId = " + level.getCourseLevelId() +
                            " | isChecked = " + isChecked);

            // 🚫 If selecting but duration not selected
            if (isChecked && !listener.isDurationSelected()) {
                holder.checkBox.setChecked(false);

                Toast.makeText(mContext,
                        "Please Select Preferred Duration to Purchase..! ",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            // ✅ ALWAYS update selection (important)
            level.setSelected(isChecked);

            // 🔥 ALWAYS notify activity (for both check & uncheck)
            listener.onLevelSelected(level);
        });

    }

    @Override
    public int getItemCount() {
        return levelList.size();
    }

    public void setLevels(List<CourseLevel> levels) {

        this.levelList.clear();
        this.levelList.addAll(levels);
        notifyDataSetChanged();
    }

    public CourseLevel getSelectedLevel() {
        if (levelList == null) return null;

        for (CourseLevel level : levelList) {
            if (level.isSelected()) {
                return level;
            }
        }
        return null;
    }

    public interface OnLevelSelectListener {
        boolean isDurationSelected();
        void onLevelSelected(CourseLevel level);
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewLevelName,levelPrice,tvSubscribed;
        CheckBox checkBox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewLevelName = itemView.findViewById(R.id.level_name);
            levelPrice = itemView.findViewById(R.id.level_price);
            tvSubscribed = itemView.findViewById(R.id.tvSubscribed);
            checkBox = itemView.findViewById(R.id.check_levels);
        }
    }
}
