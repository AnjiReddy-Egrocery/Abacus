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
import com.dst.abacustrainner.Activity.LevelVisualiztionResultActivity;
import com.dst.abacustrainner.R;

import java.util.List;

public class TopicsAdapter extends RecyclerView.Adapter<TopicsAdapter.TopicViewHolder> {

    private List<String> topics;
    private String levelName;
    private Context context;
    private int expandedPosition = -1;

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

        boolean isExpanded = position == expandedPosition;
        holder.layoutPractice.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        holder.imgArrow.setImageResource(isExpanded ?
                R.drawable.baseline_keyboard_arrow_up_24 :
                R.drawable.baseline_keyboard_arrow_down_24);

        holder.imgArrow.setOnClickListener(v -> {
            if (isExpanded) {
                // Collapse the currently expanded item
                expandedPosition = -1;
            } else {
                // Expand clicked item and collapse previous
                expandedPosition = position;
            }
            notifyDataSetChanged(); // Refresh whole list (or use payloads for optimization)
        });
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

        holder.butVisualizationResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, LevelVisualiztionResultActivity.class);
                //intent.putExtra("topicName", topicList.get(position).getName());
                context.startActivity(intent);
            }
        });
        holder.butPracticeVisualization.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, LevelExamvisualizationPracticeActivity.class);
                //intent.putExtra("topicName", topicList.get(position).getName());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return topics.size();
    }

    static class TopicViewHolder extends RecyclerView.ViewHolder {
        TextView tvTopicName;
        ImageView imgArrow;
        Button btnViewResult, btnPracticeNow,butPracticeVisualization,butVisualizationResult;
        LinearLayout layoutPractice;

        TopicViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTopicName = itemView.findViewById(R.id.tvTopicName);
            imgArrow = itemView.findViewById(R.id.ivArrow);
            layoutPractice = itemView.findViewById(R.id.layout_practice);
            btnViewResult = itemView.findViewById(R.id.btnViewResul);
            btnPracticeNow = itemView.findViewById(R.id.btnPracticeNo);
            butPracticeVisualization = itemView.findViewById(R.id.btnPractice);
            butVisualizationResult = itemView.findViewById(R.id.btnViewResulvisualization);
        }
    }
}
