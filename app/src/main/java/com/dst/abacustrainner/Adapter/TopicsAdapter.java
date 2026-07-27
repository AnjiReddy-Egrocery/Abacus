package com.dst.abacustrainner.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dst.abacustrainner.Activity.CoursePracticesActivity;
import com.dst.abacustrainner.Activity.CourseTopicExamActivity;
import com.dst.abacustrainner.Activity.CourseTopicQuestionsActivity;
import com.dst.abacustrainner.Activity.CourseTopicVisualizationActivity;
import com.dst.abacustrainner.Model.CourseLevelTopicResponse;
import com.dst.abacustrainner.Model.Question;
import com.dst.abacustrainner.Model.TopicPractice;
import com.dst.abacustrainner.R;
import com.google.android.exoplayer2.source.mediaparser.InputReaderAdapterV30;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
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
        int practiceCount = 0;

        if (courseLevelTopics.getTopicPractices() != null) {
            practiceCount = courseLevelTopics.getTopicPractices().size();
        }

        holder.tvPracticeCount.setText("View Practices : [" + practiceCount + "]");

        int questionCount = getQuestionCount(courseLevelTopics.getQuestions());

        holder.tvQuestionsCount.setText("Questions : [" + questionCount + "]");
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

               Intent intent = new Intent(context, CoursePracticesActivity.class);
               intent.putExtra("StudentId",studentId);
               intent.putExtra("TopicId",topicId);
               context.startActivity(intent);
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

       holder.butViewQuestions.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent intent = new Intent(context, CourseTopicQuestionsActivity.class);
               intent.putExtra("StudentId",studentId);
               intent.putExtra("TopicId",topicId);
               intent.putExtra("TopicName",topicName);
               context.startActivity(intent);
           }
       });
    }

    private int getQuestionCount(Object questions) {

        if (questions == null) {
            return 0;
        }

        try {

            if (questions instanceof String) {

                String json = (String) questions;

                Type type = new TypeToken<List<Question>>() {}.getType();

                List<Question> list =
                        new Gson().fromJson(json, type);

                return list == null ? 0 : list.size();
            }

            if (questions instanceof List) {
                return ((List<?>) questions).size();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
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
        TextView tvTopicName,tvPracticeCount, tvQuestionsCount;;
        LinearLayout butPractice,butViewResult,butVisualization, butViewQuestions;


        TopicViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTopicName = itemView.findViewById(R.id.tvTopicName);
            tvPracticeCount = itemView.findViewById(R.id.txt_count);
            butPractice = itemView.findViewById(R.id.but_practice);
            butViewResult = itemView.findViewById(R.id.layout_result);
            butVisualization = itemView.findViewById(R.id.but_visualization);
            butViewQuestions = itemView.findViewById(R.id.layout_view_questions);
            tvQuestionsCount = itemView.findViewById(R.id.txt_question_count);

        }
    }
}
