package com.dst.abacustrainner.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dst.abacustrainner.Activity.CartActivity;
import com.dst.abacustrainner.Activity.CoursesLevelsActivity;
import com.dst.abacustrainner.Activity.LevelTopicActivity;
import com.dst.abacustrainner.Activity.PurchasedActivity;
import com.dst.abacustrainner.Model.CourseListResponse;
import com.dst.abacustrainner.R;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class CourseLevelAdapter extends RecyclerView.Adapter<CourseLevelAdapter.ViewHolder> {
    private Context context;
    private List<CourseListResponse.Result> courseTypes = new ArrayList<>();
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
        CourseListResponse.Result item = courseTypes.get(position);

        String name = item.getCourseType();
        String orderId = item.getOrderId();

        List<CourseListResponse.CourseLevels> levels = item.getCourseLevels();



        if (levels != null && !levels.isEmpty()) {
            StringBuilder levelNames = new StringBuilder();

            for (CourseListResponse.CourseLevels level : levels) {
                levelNames.append(level.getCourseLevel()).append(", ");
            }

            String finalLevels = levelNames.substring(0, levelNames.length() - 2);

            holder.txtLevel.setText(finalLevels);
        }



        holder.txtLevelName.setText(name);


        if (item.getCourseType().toLowerCase().contains("abacus junior")) {
            holder.ivOrderIcon.setImageResource(R.drawable.abacusjunior);
        } else if (item.getCourseType().toLowerCase().contains("abacus senior")) {
            holder.ivOrderIcon.setImageResource(R.drawable.abacussenior);
        } else if (item.getCourseType().toLowerCase().contains("vedic maths")) {
            holder.ivOrderIcon.setImageResource(R.drawable.vedicmaths);
        }

        holder.layoutItem.setOnClickListener(v -> {

            String levelsJson = new Gson().toJson(item.getCourseLevels());

            // ✅ LOG here
            Log.d("LEVEL_DEBUG", "Sending Levels JSON: " + levelsJson);
            Log.d("LEVEL_DEBUG", "StudentId: " + studentId);
            Log.d("LEVEL_DEBUG", "OrderID" + orderId);

            Intent intent = new Intent(context, CoursesLevelsActivity.class);

            intent.putExtra("StudentId", studentId);
            intent.putExtra("HeaderName",name);
            intent.putExtra("OrderId",orderId);

            // 👉 send levels list
            intent.putExtra("levels", levelsJson);



            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return courseTypes == null ? 0 : courseTypes.size();
    }



    public void setCourseTypes(List<CourseListResponse.Result> courseTypes, String studentId) {
        this.courseTypes.clear();
        this.courseTypes.addAll(courseTypes);
        this.studentId = studentId;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtLevelName,txtLevel;

        ImageView ivOrderIcon;
        LinearLayout layoutItem;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtLevelName = itemView.findViewById(R.id.tvLevelText);
            ivOrderIcon = itemView.findViewById(R.id.imgorder);
            layoutItem = itemView.findViewById(R.id.layout_item);
            txtLevel = itemView.findViewById(R.id.tvLevel);
        }
    }
}
