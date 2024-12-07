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


import com.dst.abacustrainner.Activity.AssignmentListActivity;
import com.dst.abacustrainner.Activity.AssignmentPracticeActivity;
import com.dst.abacustrainner.Activity.NoDataActivity;
import com.dst.abacustrainner.Activity.ViewAssignmentListActivity;
import com.dst.abacustrainner.Model.AssignmentListResponse;
import com.dst.abacustrainner.Model.StudentRegistationResponse;
import com.dst.abacustrainner.R;
import com.dst.abacustrainner.database.SharedPrefManager;

import java.util.List;

public class AssignmentListAdapter extends RecyclerView.Adapter<AssignmentListAdapter.TopicViewHolder>  {
    Context mContext;
    private List<AssignmentListResponse.Result.AssignmentTopics> assignmentTopicsList;

    public AssignmentListAdapter(AssignmentListActivity assignmentListActivity, List<AssignmentListResponse.Result.AssignmentTopics> assignmentTopicsList) {
        this.mContext=assignmentListActivity;
        this.assignmentTopicsList=assignmentTopicsList;
    }

    @NonNull
    @Override
    public TopicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.assignment_adapter, parent, false);
        return new TopicViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TopicViewHolder holder, int position) {

        AssignmentListResponse.Result.AssignmentTopics assignmentTopics = assignmentTopicsList.get(position);
        String count= "View Practices : [" + assignmentTopics.getPracticesCount() + "]";
        String topicId=assignmentTopics.getTopicId();
        String topicName=assignmentTopics.getTopicName();
        StudentRegistationResponse.Result result= SharedPrefManager.getInstance(mContext.getApplicationContext()).getUserData();
        String studentName =result.getFirstName();
        String studentId=result.getStudentId();
        holder.txtAssignmentName.setText(assignmentTopics.getTopicName());
        holder.txtAssignPracticeCount.setText(count);

        holder.layoutAssignmentPractice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (assignmentTopics.getPracticesCount()>0){
                    Intent intent = new Intent(mContext, ViewAssignmentListActivity.class);
                    intent.putExtra("topicId", topicId);
                    intent.putExtra("studentId", studentId);
                    intent.putExtra("firstName", studentName);
                    intent.putExtra("topicName", topicName);
                    mContext.startActivity(intent);
                }else {
                    Intent noDataIntent = new Intent(mContext, NoDataActivity.class);
                    noDataIntent.putExtra("topicId", topicId);
                    noDataIntent.putExtra("studentId", studentId);
                    noDataIntent.putExtra("firstName", studentName);
                    noDataIntent.putExtra("topicName", topicName);
                    mContext.startActivity(noDataIntent);
                }
            }
        });

        holder.butAssignmentExam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(mContext, AssignmentPracticeActivity.class);
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
        return assignmentTopicsList.size();
    }

    public class TopicViewHolder extends RecyclerView.ViewHolder {
        TextView txtAssignmentName,txtAssignPracticeCount;
        LinearLayout layoutAssignmentPractice;
        Button butAssignmentExam;
        public TopicViewHolder(@NonNull View itemView) {
            super(itemView);

            txtAssignmentName=itemView.findViewById(R.id.txt_topic_name);
            txtAssignPracticeCount=itemView.findViewById(R.id.txt_assign_count);

            layoutAssignmentPractice=itemView.findViewById(R.id.layout_practice);
            butAssignmentExam=itemView.findViewById(R.id.but_practice);
        }
    }
}
