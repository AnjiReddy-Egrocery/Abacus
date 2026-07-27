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

import com.dst.abacustrainner.Activity.InstructorViewPracticeActivity;
import com.dst.abacustrainner.Activity.ViewResultDetailsActivity;
import com.dst.abacustrainner.Activity.ViewResultInstructorDetailsActivity;
import com.dst.abacustrainner.Model.ViewInstructorListResponse;
import com.dst.abacustrainner.Model.ViewTopicListResponse;
import com.dst.abacustrainner.R;

import java.util.List;

public class ViewListInstructorAdapter extends RecyclerView.Adapter<ViewListInstructorAdapter.MyViewHolder>{
    Context mContext;
    List<ViewInstructorListResponse.Result.PracticeModel> list ;
    private String paperName;

    public ViewListInstructorAdapter(InstructorViewPracticeActivity instructorViewPracticeActivity, List<ViewInstructorListResponse.Result.PracticeModel> topicsList, String Papername) {
        this.mContext = instructorViewPracticeActivity;
        this.list = topicsList;
        this.paperName = Papername;
    }

    @NonNull
    @Override
    public ViewListInstructorAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_instructor_adapter, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewListInstructorAdapter.MyViewHolder holder, int position) {
        ViewInstructorListResponse.Result.PracticeModel  practices=list.get(position);
        String practiceDate="Practiced On : " + practices.getStartedOn() + "";
        String examNo=practices.getExamRnm();
        String topicName=paperName;
        String totalattamtedquestion = "0";
        String attamted= "0";
        String correect = "0";
        String inCorrect = "0";
        holder.txtPractice.setText(practiceDate);

        holder.butResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(mContext, ViewResultInstructorDetailsActivity.class);
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
        return list.size();
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
