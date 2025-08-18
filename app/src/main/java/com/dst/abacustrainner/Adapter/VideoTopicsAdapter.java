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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dst.abacustrainner.Activity.LevelExamPracticeActivity;
import com.dst.abacustrainner.Activity.LevelExamvisualizationPracticeActivity;
import com.dst.abacustrainner.Activity.LevelResultActivity;
import com.dst.abacustrainner.Activity.LevelVideotutorialTopicActivity;
import com.dst.abacustrainner.Activity.LevelVisualiztionResultActivity;
import com.dst.abacustrainner.Activity.VideoPlayerActivity;
import com.dst.abacustrainner.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VideoTopicsAdapter extends RecyclerView.Adapter<VideoTopicsAdapter.TopicViewHolder> {

    private List<String> topics;
    private Map<String, String> topicUrls;
    private String levelName;
    private Context context;
    private int expandedPosition = -1;



    public VideoTopicsAdapter(List<String> topicsList, Map<String, String> topicUrls, String levelName, LevelVideotutorialTopicActivity context) {
        this.topics = topicsList;
        this.levelName = levelName;
        this.context = context;

        this.topicUrls = topicUrls;
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

        holder.layoutVideo.setOnClickListener(v -> {
            String videoUrl = topicUrls.get(topic);

            if (videoUrl != null && !videoUrl.isEmpty()) {
                Intent intent = new Intent(context, VideoPlayerActivity.class);
                intent.putExtra("VIDEO_URL", videoUrl);
                context.startActivity(intent);
            } else {
                Toast.makeText(context, "Video not available for " + topic, Toast.LENGTH_SHORT).show();
            }
        });

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

