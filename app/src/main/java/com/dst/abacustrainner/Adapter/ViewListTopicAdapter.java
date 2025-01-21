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


import com.dst.abacustrainner.Activity.ViewPracticeListActivity;
import com.dst.abacustrainner.Activity.ViewResultDetailsActivity;
import com.dst.abacustrainner.Model.ViewTopicListResponse;
import com.dst.abacustrainner.R;

import java.util.List;

public class ViewListTopicAdapter extends RecyclerView.Adapter<ViewListTopicAdapter.MyViewHolder> {

    Context mContext;
    List<ViewTopicListResponse.Result.Practices> practicesList;

    public ViewListTopicAdapter(ViewPracticeListActivity viewPracticeListActivity, List<ViewTopicListResponse.Result.Practices> result) {

        this.mContext=viewPracticeListActivity;
        this.practicesList=result;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_topic_adapter, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
     ViewTopicListResponse.Result.Practices  practices=practicesList.get(position);
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
             Intent intent=new Intent(mContext, ViewResultDetailsActivity.class);
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

            txtPractice=itemView.findViewById(R.id.txt_practice);
            butResult=itemView.findViewById(R.id.but_view_Result);
        }
    }
}
