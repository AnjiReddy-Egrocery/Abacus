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


import com.dst.abacustrainner.Activity.ViewAssignmentListActivity;
import com.dst.abacustrainner.Activity.ViewResultAssignmentDetailsActivity;
import com.dst.abacustrainner.Model.ViewAssignmentListResponse;
import com.dst.abacustrainner.R;

import java.util.List;

public class ViewAssignmentAdapter extends RecyclerView.Adapter<ViewAssignmentAdapter.MyViewHolder> {

    Context mContext;
    List<ViewAssignmentListResponse.Result.Practices> practicesList;

    public ViewAssignmentAdapter(ViewAssignmentListActivity viewAssignmentListActivity, List<ViewAssignmentListResponse.Result.Practices> practicesList) {

        this.mContext=viewAssignmentListActivity;
        this.practicesList=practicesList;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_assignment_adapter, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
     ViewAssignmentListResponse.Result.Practices practices=practicesList.get(position);

        String practiceDate="Practiced On : " + practices.getStartedOn() + "";
        String examNo=practices.getExamRnm();
        String topicName=practices.getTopicName();
        holder.txtAssignmentTopic.setText(practiceDate);
        holder.butAssignmentViewDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(mContext, ViewResultAssignmentDetailsActivity.class);
                intent.putExtra("examRnm",examNo);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return practicesList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txtAssignmentTopic;
        Button butAssignmentViewDetails;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            txtAssignmentTopic=itemView.findViewById(R.id.txt_topic_name);
            butAssignmentViewDetails=itemView.findViewById(R.id.but_view_Result);
        }
    }
}
