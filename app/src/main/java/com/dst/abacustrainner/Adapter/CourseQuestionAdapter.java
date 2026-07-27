package com.dst.abacustrainner.Adapter;

import android.content.Context;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dst.abacustrainner.Activity.CourseTopicQuestionsActivity;
import com.dst.abacustrainner.Activity.InstructorPracticeViewQuestionsActivity;
import com.dst.abacustrainner.Model.CourseQuestionsResponse;
import com.dst.abacustrainner.Model.ViewInstructorListResponse;
import com.dst.abacustrainner.R;

import java.util.List;

public class CourseQuestionAdapter extends RecyclerView.Adapter<CourseQuestionAdapter.CourseQuestionViewHolder> {

    Context mContext;
    private List<CourseQuestionsResponse.Result.Questions> questionList;

    public CourseQuestionAdapter(CourseTopicQuestionsActivity courseTopicQuestionsActivity, List<CourseQuestionsResponse.Result.Questions> questionList) {
        this.mContext = courseTopicQuestionsActivity;
        this.questionList = questionList;
    }

    @NonNull
    @Override
    public CourseQuestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_coursequestion_card, parent, false);
        return new CourseQuestionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseQuestionViewHolder holder, int position) {
        CourseQuestionsResponse.Result.Questions model = questionList.get(position);
        Log.d("API_RESPONSE", model.getQuestion());
        // Indexing from 1
        holder.tvQuestionNumber.setText("Q" + (position + 1));

        // Question (ఇక్కడ API నుంచి వచ్చే question లో లైన్స్ లాగా వస్తే ఇక్కడ సెట్ అవుతుంది)


        String rawQuestion = model.getQuestion();

        if (rawQuestion != null) {
            // ఒకవేళ <p> ట్యాగ్స్ ఉంటే వాటిని తొలగించి శుభ్రం చేయడం లేదా Html.fromHtml వాడటం
            Spanned formattedQuestion;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                formattedQuestion = Html.fromHtml(rawQuestion, Html.FROM_HTML_MODE_LEGACY);
            } else {
                formattedQuestion = Html.fromHtml(rawQuestion);
            }

            holder.tvQuestionContent.setText(formattedQuestion);
        }

        // Answer

    }

    @Override
    public int getItemCount() {
        return questionList.size();
    }

    public List<CourseQuestionsResponse.Result.Questions> getQuestionList() {
        return questionList;

    }


    public static class CourseQuestionViewHolder extends RecyclerView.ViewHolder {
        TextView tvQuestionNumber, tvQuestionContent;

        public CourseQuestionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvQuestionNumber = itemView.findViewById(R.id.tvQuestionNumber);
            tvQuestionContent = itemView.findViewById(R.id.tvQuestionContent);
        }
    }
}

