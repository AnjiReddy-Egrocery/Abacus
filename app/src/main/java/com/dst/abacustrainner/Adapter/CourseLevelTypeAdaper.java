package com.dst.abacustrainner.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dst.abacustrainner.Activity.PurchasedActivity;
import com.dst.abacustrainner.Model.CourseListResponse;
import com.dst.abacustrainner.R;
import com.google.gson.Gson;

import java.util.List;

public class CourseLevelTypeAdaper extends RecyclerView.Adapter<CourseLevelTypeAdaper.ViewHolder> {

    private Context context;
    private List<CourseListResponse.Result> list;
    private String studentId;

    public CourseLevelTypeAdaper(PurchasedActivity purchasedActivity, List<CourseListResponse.Result> resultList, String studentId) {
        this.context = purchasedActivity;
        this.list = resultList;
        this.studentId = studentId;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_levelcourse_type, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        CourseListResponse.Result item = list.get(position);
        holder.txtCourseType.setText(item.getCourseType());

        holder.itemView.setOnClickListener(v -> {

            Intent intent = new Intent(context, PurchasedActivity.class);
            intent.putExtra("StudentId", studentId);

            // 👉 pass only selected levels
            intent.putExtra("levels", new Gson().toJson(item.getCourseLevels()));

            Log.d("LEVELS_JSON", new Gson().toJson(item.getCourseLevels())); // 🔍 debug

            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() { return list.size(); }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtCourseType;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtCourseType = itemView.findViewById(R.id.tvCourseType);
        }
    }
}
