package com.dst.abacustrainner.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dst.abacustrainner.Activity.LevelExamPracticeActivity;
import com.dst.abacustrainner.Activity.LevelExamvisualizationPracticeActivity;
import com.dst.abacustrainner.Activity.LevelResultActivity;
import com.dst.abacustrainner.Activity.LevelVisualiztionResultActivity;
import com.dst.abacustrainner.R;

import java.util.List;

public class VideoTopicsAdapter extends RecyclerView.Adapter<VideoTopicsAdapter.TopicViewHolder> {

    private List<String> topics;
    private String levelName;
    private Context context;
    private int expandedPosition = -1;

    public VideoTopicsAdapter(List<String> topics, String levelName, Context context) {
        this.topics = topics;
        this.levelName = levelName;
        this.context = context;
    }

    @NonNull
    @Override
    public VideoTopicsAdapter.TopicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_video_topic, parent, false);
        return new VideoTopicsAdapter.TopicViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoTopicsAdapter.TopicViewHolder holder, int position) {
        String topic = topics.get(position);
        holder.tvTopicName.setText(topic);


    }

    @Override
    public int getItemCount() {
        return topics.size();
    }

    static class TopicViewHolder extends RecyclerView.ViewHolder {
        TextView tvTopicName;

        LinearLayout layoutVideo;

        TopicViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTopicName = itemView.findViewById(R.id.tvTopicName);
            layoutVideo = itemView.findViewById(R.id.layout_video);
        }
    }
}

