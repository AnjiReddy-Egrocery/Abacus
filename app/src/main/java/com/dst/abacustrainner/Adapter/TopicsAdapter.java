package com.dst.abacustrainner.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dst.abacustrainner.Activity.CourseTopicExamActivity;
import com.dst.abacustrainner.Activity.CourseTopicVisualizationActivity;
import com.dst.abacustrainner.Model.CourseLevelTopicResponse;
import com.dst.abacustrainner.R;
import com.google.android.exoplayer2.source.mediaparser.InputReaderAdapterV30;

import java.util.ArrayList;
import java.util.List;

public class TopicsAdapter extends RecyclerView.Adapter<TopicsAdapter.TopicViewHolder> {

    private List<CourseLevelTopicResponse.courseLevelTopics> topics = new ArrayList<>();

    private Context context;
    private String studentId;

    private String courseLevelId;


    public TopicsAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public TopicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_topic, parent, false);
        return new TopicViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TopicViewHolder holder, int position) {
     CourseLevelTopicResponse.courseLevelTopics courseLevelTopics = topics.get(position);

     String topicId = courseLevelTopics.getTopicId();

     String topicName = courseLevelTopics.getTopic();

       holder.tvTopicName.setText(topicName);

       holder.butPractice.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent intent = new Intent(context, CourseTopicExamActivity.class);
               intent.putExtra("StudentId",studentId);
               intent.putExtra("TopicId",topicId);
               intent.putExtra("TopicName",topicName);
               context.startActivity(intent);
           }
       });

       holder.butViewResult.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {

           }
       });

       holder.butVisualization.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent intent = new Intent(context, CourseTopicVisualizationActivity.class);
               intent.putExtra("StudentId",studentId);
               intent.putExtra("TopicId",topicId);
               intent.putExtra("TopicName",topicName);
               context.startActivity(intent);
           }
       });
    }

    @Override
    public int getItemCount() {
        return topics.size();
    }

    public void setLevels(List<CourseLevelTopicResponse.courseLevelTopics> levels, String studentId, String courseLevelId) {


        this.topics.clear();
        this.topics.addAll(levels);
        this.studentId = studentId;
        this.courseLevelId = courseLevelId;
        notifyDataSetChanged();
    }

    static class TopicViewHolder extends RecyclerView.ViewHolder {
        TextView tvTopicName;
        Button butPractice,butViewResult,butVisualization;


        TopicViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTopicName = itemView.findViewById(R.id.tvTopicName);
            butPractice = itemView.findViewById(R.id.but_practice);
            butViewResult = itemView.findViewById(R.id.but_view_practice);
            butVisualization = itemView.findViewById(R.id.but_visualization);

        }
    }
}
