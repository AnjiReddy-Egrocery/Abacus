package com.dst.abacustrainner.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.dst.abacustrainner.Activity.NoDataActivity;
import com.dst.abacustrainner.Activity.TopicListActivity;
import com.dst.abacustrainner.Activity.TopicPracticeActivity;
import com.dst.abacustrainner.Activity.ViewPracticeListActivity;
import com.dst.abacustrainner.Model.StudentRegistationResponse;
import com.dst.abacustrainner.Model.TopicListResponse;
import com.dst.abacustrainner.R;
import com.dst.abacustrainner.database.SharedPrefManager;

import java.util.List;

public class TopicListAdapter extends RecyclerView.Adapter<TopicListAdapter.TopicViewHolder> {
    Context mContext;
    private List<TopicListResponse.Result.Topics> topicsList;
    public TopicListAdapter(Context context, List<TopicListResponse.Result.Topics> topicsList) {
        this.mContext=context;
        this.topicsList=topicsList;
    }
    @NonNull
    @Override
    public TopicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.topic_adapter, parent, false);
        return new TopicViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull TopicViewHolder holder, int position) {
        TopicListResponse.Result.Topics topic = topicsList.get(position);
        String count= "View Practices : [" + topic.getPracticesCount() + "]";
        String topicId=topic.getTopicId();
        String topicName=topic.getTopicName();
        StudentRegistationResponse.Result result= SharedPrefManager.getInstance(mContext.getApplicationContext()).getUserData();
        String studentName =result.getFirstName();
        String studentId=result.getStudentId();
        holder.txtTopicName.setText(topic.getTopicName());
        holder.txtPracticeCount.setText(count);

        holder.layoutPractice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (topic.getPracticesCount() > 0) {
                    Intent intent = new Intent(mContext, ViewPracticeListActivity.class);
                    intent.putExtra("topicId", topicId);
                    intent.putExtra("studentId", studentId);
                    intent.putExtra("firstName", studentName);
                    intent.putExtra("topicName", topicName);
                    mContext.startActivity(intent);
                } else {
                    // Show a "No Data" message or handle it as needed
                    Intent noDataIntent = new Intent(mContext, NoDataActivity.class);
                    noDataIntent.putExtra("topicId", topicId);
                    noDataIntent.putExtra("studentId", studentId);
                    noDataIntent.putExtra("firstName", studentName);
                    noDataIntent.putExtra("topicName", topicName);
                    mContext.startActivity(noDataIntent);
                }

            }
        });
        holder.butPracticeExam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(mContext, TopicPracticeActivity.class);
                intent.putExtra("topicId", topicId);
                intent.putExtra("studentId", studentId);
                intent.putExtra("topicName", topicName);
                intent.putExtra("firstName",studentName);
                mContext.startActivity(intent);
            }
        });
    }
    @Override
    public int getItemCount() {
        return topicsList.size();
    }

    public class TopicViewHolder extends RecyclerView.ViewHolder {

        TextView txtTopicName,txtPracticeCount;

        LinearLayout layoutPractice;
        LinearLayout butPracticeExam;
        public TopicViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTopicName=itemView.findViewById(R.id.txt_topic_name);
            txtPracticeCount=itemView.findViewById(R.id.txt_count);
            layoutPractice=itemView.findViewById(R.id.layout_practice);
            butPracticeExam=itemView.findViewById(R.id.but_practice);
        }
    }
}
