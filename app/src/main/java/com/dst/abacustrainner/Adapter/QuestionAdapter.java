package com.dst.abacustrainner.Adapter;

import android.content.Context;
import android.os.Build;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dst.abacustrainner.Activity.InstructorPracticeViewQuestionsActivity;
import com.dst.abacustrainner.Model.ViewInstructorListResponse;
import com.dst.abacustrainner.R;

import java.util.List;

public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.QuestionViewHolder> {

    Context mContext;
    private List<ViewInstructorListResponse.Result.QuestionModel> questionList;

    public QuestionAdapter(InstructorPracticeViewQuestionsActivity instructorPracticeViewQuestionsActivity, List<ViewInstructorListResponse.Result.QuestionModel> questionList) {
        this.mContext = instructorPracticeViewQuestionsActivity;
        this.questionList = questionList;
    }

    @NonNull
    @Override
    public QuestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_question_card, parent, false);
        return new QuestionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuestionViewHolder holder, int position) {
        ViewInstructorListResponse.Result.QuestionModel model = questionList.get(position);

        String rawQuestion = model.getQuestion();

        if (rawQuestion != null && !rawQuestion.isEmpty()) {

            rawQuestion = rawQuestion.replace("\u00A0", " "); // remove nbsp

            Spanned formattedQuestion;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                formattedQuestion = Html.fromHtml(rawQuestion, Html.FROM_HTML_MODE_COMPACT);
            } else {
                formattedQuestion = Html.fromHtml(rawQuestion);
            }

            holder.tvQuestionContent.setText(formattedQuestion);
        } // Indexing from 1


        // Answer

    }

    @Override
    public int getItemCount() {
        return questionList.size();
    }

    public List<ViewInstructorListResponse.Result.QuestionModel> getQuestionList() {
        return questionList;

    }


    public static class QuestionViewHolder extends RecyclerView.ViewHolder {
        TextView tvQuestionNumber, tvQuestionContent;

        public QuestionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvQuestionNumber = itemView.findViewById(R.id.tvQuestionNumber);
            tvQuestionContent = itemView.findViewById(R.id.tvQuestionContent);
        }
    }
}
