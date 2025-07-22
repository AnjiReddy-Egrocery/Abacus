package com.dst.abacustrainner.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dst.abacustrainner.Activity.LevelExamPracticeActivity;
import com.dst.abacustrainner.Activity.LevelResultActivity;
import com.dst.abacustrainner.R;

import java.util.List;

public class TopicsAdapter extends RecyclerView.Adapter<TopicsAdapter.TopicViewHolder> {

    private List<String> topics;
    private String levelName;
    private Context context;

    public TopicsAdapter(List<String> topics, String levelName, Context context) {
        this.topics = topics;
        this.levelName = levelName;
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
        String topic = topics.get(position);
        holder.tvTopicName.setText(topic);

        holder.btnViewResult.setOnClickListener(v -> {
            Intent intent = new Intent(context, LevelResultActivity.class);
            //intent.putExtra("topicName", topicList.get(position).getName());
            context.startActivity(intent);
        });

        holder.btnPracticeNow.setOnClickListener(v -> {
            Intent intent = new Intent(context, LevelExamPracticeActivity.class);
            //intent.putExtra("topicName", topicList.get(position).getName());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return topics.size();
    }

    static class TopicViewHolder extends RecyclerView.ViewHolder {
        TextView tvTopicName;
        Button btnViewResult, btnPracticeNow;

        TopicViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTopicName = itemView.findViewById(R.id.tvTopicName);
            btnViewResult = itemView.findViewById(R.id.btnViewResul);
            btnPracticeNow = itemView.findViewById(R.id.btnPracticeNo);
        }
    }
}
