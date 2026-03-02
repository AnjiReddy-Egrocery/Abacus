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

import com.dst.abacustrainner.Activity.CourseLevelActivity;
import com.dst.abacustrainner.Activity.CourseTopicExamActivity;
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


        // UI Styling based on type
        switch (item.getType()) {

            case LevelDisplayItem.TYPE_TOPIC_HEADER:
                holder.txtTopic.setText("Course Level Topics");
                holder.txtTopic.setTextSize(18);
                holder.txtTopic.setTextColor(Color.WHITE);
                holder.txtTopic.setPadding(20,20,20,20);

                holder.txtTopic.setBackgroundResource(R.drawable.bg_header);

                // remove card look
                holder.cardView.setCardElevation(0);
                holder.cardView.setCardBackgroundColor(Color.TRANSPARENT);

                holder.btnView.setVisibility(View.GONE);
                holder.btnVisualization.setVisibility(View.GONE);
                holder.btnPractice.setVisibility(View.GONE);


                break;

            // ✅ ASSIGNMENT HEADER
            case LevelDisplayItem.TYPE_ASSIGNMENT_HEADER:

                holder.txtTopic.setText("Course Level Assignment Topics");
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

            // ✅ NORMAL ITEMS
            default:

                holder.txtTopic.setTextSize(14);
                holder.txtTopic.setTextColor(Color.BLACK);
                holder.txtTopic.setPadding(60,10,20,10);

                holder.txtTopic.setBackground(null);

                // restore card style (VERY IMPORTANT)
                holder.cardView.setCardElevation(6);
                holder.cardView.setCardBackgroundColor(Color.WHITE);

                holder.btnView.setVisibility(View.VISIBLE);
                holder.btnVisualization.setVisibility(View.VISIBLE);
                holder.btnPractice.setVisibility(View.VISIBLE);


                break;
        }

        holder.btnVisualization.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String topicName = item.getTitle();
                String topicId = item.getTopicId();
               /* Intent intent = new Intent(context, LevelTopicExamActivity.class);
                intent.putExtra("StudentId",studentId);
                intent.putExtra("TopicId",topicId);
                intent.putExtra("TopicName",topicName);
                context.startActivity(intent);
*/
            }
        });
        holder.btnPractice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String topicName = item.getTitle();
                Toast.makeText(context,
                        "View clicked : " + topicName,
                        Toast.LENGTH_SHORT).show();
            }
        });
        holder.btnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String topicName = item.getTitle();
                Toast.makeText(context,
                        "View clicked : " + topicName,
                        Toast.LENGTH_SHORT).show();

            }
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
