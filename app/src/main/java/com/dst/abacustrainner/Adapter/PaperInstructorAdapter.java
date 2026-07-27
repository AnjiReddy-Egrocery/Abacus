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

import com.dst.abacustrainner.Activity.InstructorPracticeExamActivity;
import com.dst.abacustrainner.Activity.InstructorPracticeViewQuestionsActivity;
import com.dst.abacustrainner.Activity.InstructorPracticeVisualizationExamActivity;
import com.dst.abacustrainner.Activity.InstructorViewPracticeActivity;
import com.dst.abacustrainner.Activity.PaperAllocatedInstructorActivity;
import com.dst.abacustrainner.Model.PaperListResponse;
import com.dst.abacustrainner.Model.PracticeModel;
import com.dst.abacustrainner.Model.QuestionModel;
import com.dst.abacustrainner.Model.StudentRegistationResponse;
import com.dst.abacustrainner.R;
import com.dst.abacustrainner.database.SharedPrefManager;

import java.util.List;

public class PaperInstructorAdapter extends RecyclerView.Adapter<PaperInstructorAdapter.PaperViewHolder>{
    Context mContext;
    List<PaperListResponse.Result> paperList;

    private List<QuestionModel> questions;
    private List<PracticeModel> practicesList;

    public PaperInstructorAdapter(PaperAllocatedInstructorActivity paperAllocatedInstructorActivity, List<PaperListResponse.Result> paperList) {
         this.mContext = paperAllocatedInstructorActivity;
         this.paperList = paperList;
    }

    @NonNull
    @Override
    public PaperInstructorAdapter.PaperViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.paper_adapter, parent, false);
        return new PaperViewHolder(view);    }

    @Override
    public void onBindViewHolder(@NonNull PaperInstructorAdapter.PaperViewHolder holder, int position) {

        PaperListResponse.Result paperListResponse = paperList.get(position);

        String paperName = paperListResponse.getPaperTitle();
        String paperId = paperListResponse.getPaperId();
        StudentRegistationResponse.Result result= SharedPrefManager.getInstance(mContext.getApplicationContext()).getUserData();
        String studentId=result.getStudentId();
        int questionCount = 0;
        if (paperListResponse.getQuestions() != null) {
             questionCount = paperListResponse.getQuestions().size();
        }

        // Practice Count
        int practiceCount = 0;
        if (paperListResponse.getPracticesList() != null) {
            practiceCount = paperListResponse.getPracticesList().size();
        }

        holder.txtQuestionCount.setText("View Questions : [" + questionCount + "]");
        holder.txtPracticeCount.setText("View Practices : [" + practiceCount + "]");

        holder.txtTopicName.setText(paperName);

        holder.layoutPractice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, InstructorPracticeExamActivity.class);
                intent.putExtra("StudentId",studentId);
                intent.putExtra("PaperId",paperId);
                intent.putExtra("PaperName",paperName);
                mContext.startActivity(intent);
            }
        });
        holder.layoutViewPractice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, InstructorViewPracticeActivity.class);
                intent.putExtra("StudentId",studentId);
                intent.putExtra("PaperId",paperId);
                intent.putExtra("PaperName",paperName);
                mContext.startActivity(intent);
            }
        });

        holder.layoutVisualization.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, InstructorPracticeVisualizationExamActivity.class);
                intent.putExtra("StudentId",studentId);
                intent.putExtra("PaperId",paperId);
                intent.putExtra("PaperName",paperName);
                mContext.startActivity(intent);
            }
        });

        holder.layoutQuestions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, InstructorPracticeViewQuestionsActivity.class);
                intent.putExtra("StudentId",studentId);
                intent.putExtra("PaperId",paperId);
                intent.putExtra("PaperName",paperName);
                mContext.startActivity(intent);
            }
        });




    }

    @Override
    public int getItemCount() {
        return paperList.size();
    }


    public class PaperViewHolder extends RecyclerView.ViewHolder {
        TextView txtTopicName,txtPracticeCount, txtQuestionCount;

        LinearLayout layoutPractice,layoutVisualization,layoutQuestions,layoutViewPractice;

        public PaperViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTopicName=itemView.findViewById(R.id.txt_topic_name);
            txtPracticeCount=itemView.findViewById(R.id.txt_count);
            txtQuestionCount = itemView.findViewById(R.id.txt_questions_count);
            layoutPractice=itemView.findViewById(R.id.layout_practice);
            layoutQuestions=itemView.findViewById(R.id.layout_view_questions);
            layoutVisualization = itemView.findViewById(R.id.layout_visualization);
            layoutViewPractice = itemView.findViewById(R.id.layout_view_result);

        }
    }
}
