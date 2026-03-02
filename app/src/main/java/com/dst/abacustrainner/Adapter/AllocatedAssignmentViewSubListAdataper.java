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

import com.dst.abacustrainner.Activity.AllocatedAssignmentViewPracticeActivity;
import com.dst.abacustrainner.Activity.AllocatedViewPracticeActivity;
import com.dst.abacustrainner.Activity.AllocatedViewSubAssignmentResultDetailsActivity;
import com.dst.abacustrainner.Activity.AllocatedViewSubResultDetailsActivity;
import com.dst.abacustrainner.Model.AlloactedViewSubTopicListResponse;
import com.dst.abacustrainner.R;

import java.util.List;

public class AllocatedAssignmentViewSubListAdataper extends RecyclerView.Adapter<AllocatedAssignmentViewSubListAdataper.MyViewHolder> {

    Context mContext;
    List<AlloactedViewSubTopicListResponse.Result.PracticesList> practicesList;

    public AllocatedAssignmentViewSubListAdataper(AllocatedAssignmentViewPracticeActivity viewPracticeListActivity, List<AlloactedViewSubTopicListResponse.Result.PracticesList> result) {

        this.mContext=viewPracticeListActivity;
        this.practicesList=result;

    }

    @NonNull
    @Override
    public AllocatedAssignmentViewSubListAdataper.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewallocated_assignsub_topic_adapter, parent, false);
        return new AllocatedAssignmentViewSubListAdataper.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AllocatedAssignmentViewSubListAdataper.MyViewHolder holder, int position) {
        AlloactedViewSubTopicListResponse.Result.PracticesList  practices=practicesList.get(position);
        String practiceDate="Practiced On : " + practices.getStartedOn() + "";
        String examNo=practices.getExamRnm();
        String topicName=practices.getTopicName();
        String totalattamtedquestion = "0";
        String attamted= "0";
        String correect = "0";
        String inCorrect = "0";
        holder.txtPractice.setText(practiceDate);
        holder.butResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(mContext, AllocatedViewSubAssignmentResultDetailsActivity.class);
                intent.putExtra("examRnm",examNo);
                intent.putExtra("topicName",topicName);
                intent.putExtra("AQuestion",totalattamtedquestion);
                intent.putExtra("Attemted",attamted);
                intent.putExtra("Correct",correect);
                intent.putExtra("InCorrect",inCorrect);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return practicesList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txtPractice;
        Button butResult;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            txtPractice=itemView.findViewById(R.id.tvTopicName);
            butResult=itemView.findViewById(R.id.but_view_Result);
        }
    }
}

