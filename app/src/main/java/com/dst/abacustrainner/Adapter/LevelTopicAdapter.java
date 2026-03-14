package com.dst.abacustrainner.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.dst.abacustrainner.Activity.AllocatedAssignmentViewPracticeActivity;
import com.dst.abacustrainner.Activity.AllocatedViewPracticeActivity;
import com.dst.abacustrainner.Activity.CourseLevelActivity;
import com.dst.abacustrainner.Activity.CoursePracticesActivity;
import com.dst.abacustrainner.Activity.CourseTopicExamActivity;
import com.dst.abacustrainner.Activity.LevelAssignmentExamActivity;
import com.dst.abacustrainner.Activity.LevelAssignmentVisualizationActivity;
import com.dst.abacustrainner.Activity.LevelTopicExamActivity;
import com.dst.abacustrainner.Activity.LevelTopicVisualizationActivity;
import com.dst.abacustrainner.Model.CourseLevelAssignmentTopic;
import com.dst.abacustrainner.Model.CourseLevelTopic;
import com.dst.abacustrainner.Model.CourseTypeLevel;
import com.dst.abacustrainner.Model.CourseTypeResponse;
import com.dst.abacustrainner.Model.LevelDisplayItem;
import com.dst.abacustrainner.R;

import java.util.ArrayList;
import java.util.List;

public class LevelTopicAdapter extends RecyclerView.Adapter<LevelTopicAdapter.ViewHolder> {
    private Context context;
    private List<LevelDisplayItem> displayList = new ArrayList<>();

    private String studentId;

    public LevelTopicAdapter(CourseLevelActivity courseLevelActivity) {
        this.context = courseLevelActivity;

    }

    @NonNull
    @Override
    public LevelTopicAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_level_topic, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LevelTopicAdapter.ViewHolder holder, int position) {
        LevelDisplayItem item = displayList.get(position);

        holder.txtTopic.setText(item.getTitle());

        String topicName = item.getTitle();
        String topicId = item.getTopicId();

        switch (item.getType()) {

            // ===================== TOPIC HEADER =====================
            case LevelDisplayItem.TYPE_TOPIC_HEADER:

                holder.txtTopic.setText("Topics");
                holder.txtTopic.setTextSize(18);
                holder.txtTopic.setTextColor(Color.WHITE);
                holder.txtTopic.setPadding(20,20,20,20);
                holder.txtTopic.setBackgroundResource(R.drawable.bg_header);

                holder.cardView.setCardElevation(0);
                holder.cardView.setCardBackgroundColor(Color.TRANSPARENT);

                holder.btnView.setVisibility(View.GONE);
                holder.btnVisualization.setVisibility(View.GONE);
                holder.btnPractice.setVisibility(View.GONE);

                break;


            // ===================== ASSIGNMENT HEADER =====================
            case LevelDisplayItem.TYPE_ASSIGNMENT_HEADER:

                holder.txtTopic.setText("Assignment Topics");
                holder.txtTopic.setTextSize(18);
                holder.txtTopic.setTextColor(Color.WHITE);
                holder.txtTopic.setPadding(20,20,20,20);
                holder.txtTopic.setBackgroundResource(R.drawable.bg_header);

                holder.cardView.setCardElevation(0);
                holder.cardView.setCardBackgroundColor(Color.TRANSPARENT);

                holder.btnView.setVisibility(View.GONE);
                holder.btnVisualization.setVisibility(View.GONE);
                holder.btnPractice.setVisibility(View.GONE);

                break;


            // ===================== NORMAL TOPIC =====================
            case LevelDisplayItem.TYPE_TOPIC:

                styleNormalItem(holder);

                holder.btnPractice.setOnClickListener(v -> {

                    Intent intent =
                            new Intent(context, LevelTopicExamActivity.class);
                    intent.putExtra("StudentId", studentId);
                    intent.putExtra("TopicId", topicId);
                    intent.putExtra("TopicName", topicName);
                    context.startActivity(intent);
                });

                break;


            // ===================== ASSIGNMENT TOPIC =====================
            case LevelDisplayItem.TYPE_ASSIGNMENT_TOPIC:

                styleNormalItem(holder);

                holder.btnPractice.setOnClickListener(v -> {

                    Intent intent =
                            new Intent(context, LevelAssignmentExamActivity.class);
                    intent.putExtra("StudentId", studentId);
                    intent.putExtra("TopicId", topicId);
                    intent.putExtra("TopicName", topicName);
                    context.startActivity(intent);
                });
                holder.btnVisualization.setOnClickListener(v -> {

                    Intent intent =
                            new Intent(context, LevelAssignmentVisualizationActivity.class);
                    intent.putExtra("StudentId", studentId);
                    intent.putExtra("TopicId", topicId);
                    intent.putExtra("TopicName", topicName);
                    context.startActivity(intent);
                });

                // ✅ View Practice (Assignment)
                holder.btnView.setOnClickListener(v -> {

                    Intent intent =
                            new Intent(context, AllocatedAssignmentViewPracticeActivity.class);
                    intent.putExtra("StudentId", studentId);
                    intent.putExtra("TopicId", topicId);
                    context.startActivity(intent);
                });



                break;
        }
    }

    private void styleNormalItem(ViewHolder holder) {

        holder.txtTopic.setTextSize(14);
        holder.txtTopic.setTextColor(Color.BLACK);
        holder.txtTopic.setPadding(60,10,20,10);
        holder.txtTopic.setBackground(null);

        holder.cardView.setCardElevation(6);
        holder.cardView.setCardBackgroundColor(Color.WHITE);

        holder.btnView.setVisibility(View.VISIBLE);
        holder.btnVisualization.setVisibility(View.VISIBLE);
        holder.btnPractice.setVisibility(View.VISIBLE);

        // Visualization common
        holder.btnVisualization.setOnClickListener(v -> {

            Intent intent =
                    new Intent(context, LevelTopicVisualizationActivity.class);
            intent.putExtra("StudentId", studentId);
            intent.putExtra("TopicId",
                    displayList.get(holder.getAdapterPosition()).getTopicId());
            intent.putExtra("TopicName",
                    displayList.get(holder.getAdapterPosition()).getTitle());
            context.startActivity(intent);
        });

        // View practice common
        holder.btnView.setOnClickListener(v -> {

            Intent intent =
                    new Intent(context, AllocatedViewPracticeActivity.class);
            intent.putExtra("StudentId", studentId);
            intent.putExtra("TopicId",
                    displayList.get(holder.getAdapterPosition()).getTopicId());
            context.startActivity(intent);
        });
    }
    @Override
    public int getItemCount() {
        return displayList.size();
    }

    public void setData(List<CourseTypeLevel> levels, String studentId) {
        displayList.clear();
        this.studentId = studentId;

        for (CourseTypeLevel level : levels) {

            // ✅ LEVEL HEADER
            displayList.add(new LevelDisplayItem(
                    LevelDisplayItem.TYPE_TOPIC_HEADER,
                    level.getCourseLevel()
            ));

            // ✅ NORMAL TOPICS
            List<CourseLevelTopic> topics = level.getCourseLevelTopics();

            if (topics != null && !topics.isEmpty()) {
                for (CourseLevelTopic topic : topics) {

                    displayList.add(new LevelDisplayItem(
                            LevelDisplayItem.TYPE_TOPIC,
                            topic.getTopic(),
                            topic.getTopicId()   // ✅ IMPORTANT
                    ));
                }
            }

            // ✅ ASSIGNMENT HEADER
            List<CourseLevelAssignmentTopic> assignments =
                    level.getCourseLevelAssignmentTopics();

            if (assignments != null && !assignments.isEmpty()) {

                displayList.add(new LevelDisplayItem(
                        LevelDisplayItem.TYPE_ASSIGNMENT_HEADER,
                        "Assignment Topics"
                ));

                // ✅ ASSIGNMENT TOPICS
                for (CourseLevelAssignmentTopic a : assignments) {

                    displayList.add(new LevelDisplayItem(
                            LevelDisplayItem.TYPE_ASSIGNMENT_TOPIC,
                            a.getTopic(),
                            a.getTopicId()   // ✅ IMPORTANT
                    ));
                }
            }
        }
notifyDataSetChanged();
        this.studentId = studentId;
        notifyDataSetChanged();

    }



    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtLevel, txtTopic;
        LinearLayout layoutLevel;
        CardView cardView;
        Button btnPractice, btnVisualization, btnView;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtTopic = itemView.findViewById(R.id.tvcoursename);
            layoutLevel = itemView.findViewById(R.id.layout_level);
            cardView = itemView.findViewById(R.id.cardView);

            btnView = itemView.findViewById(R.id.btnView);
            btnPractice = itemView.findViewById(R.id.btnPractice);
            btnVisualization = itemView.findViewById(R.id.btnVisualization);

        }
    }
}
