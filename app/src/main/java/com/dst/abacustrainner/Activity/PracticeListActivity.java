package com.dst.abacustrainner.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.dst.abacustrainner.R;
import com.dst.abacustrainner.database.ParcelableLong;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class PracticeListActivity extends AppCompatActivity {

    TextView txtTopicName,txtName,txtStartedOn,txtTotalQuestions,txtCorrectAnswer,txtWrongAnswer,txtAttemtedQuestion,txtNotAttemtedQuestion;

    String topicName="";
    String studentName="";

    String startedOn="";
    TableLayout tableLayout;

    double timeInSeconds;

    String question ;
    String enteredAnswer ;
    String correctAnswer ;
    long time;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practice_list);

        txtTopicName = findViewById(R.id.txt_subject);
        txtName =findViewById(R.id.txt_name);
        txtStartedOn = findViewById(R.id.txt_date);
        txtTotalQuestions = findViewById(R.id.txt_questions);
        txtCorrectAnswer = findViewById(R.id.txt_correct_answer);
        txtWrongAnswer =findViewById(R.id.txt_wrong_answer);
        txtAttemtedQuestion = findViewById(R.id.txt_attemted_question);
        txtNotAttemtedQuestion = findViewById(R.id.txt_not_questions);

        Intent intent = getIntent();
        topicName = intent.getStringExtra("topicName");
        studentName = intent.getStringExtra("firstName");
        startedOn = intent.getStringExtra("startedOn");

        //String orginalAnswer =intent.getStringExtra("answer");
        ArrayList<String> questions = intent.getStringArrayListExtra("questions");
        ArrayList<String> enteredAnswers = intent.getStringArrayListExtra("enteredAnswers");
        ArrayList<String> orginalAnswers = intent.getStringArrayListExtra("answers");
        ArrayList<String> stringIsQuestionAttempted = intent.getStringArrayListExtra("isQuestionAttempted");


        ArrayList<Boolean> isQuestionAttempted = convertStringListToBooleanList(stringIsQuestionAttempted);

        int attemptedCount = getAttemptedQuestionsCount(isQuestionAttempted);
        List<Long> questionTimes = new ArrayList<>();

        // Retrieve ParcelableLong list and convert it back to Long list
        ArrayList<ParcelableLong> parcelableTimes = intent.getParcelableArrayListExtra("questionTimes");
        if (parcelableTimes != null) {
            for (ParcelableLong parcelableLong : parcelableTimes) {
                questionTimes.add(parcelableLong.getValue());
            }
        }

        txtTopicName.setText(topicName);
        txtName.setText(studentName);
        txtStartedOn.setText(startedOn);

        int correctCount = 0;
        int wrongCount = 0;


        int totalQuestions = questions.size();
        int attemptedQuestions = getAttemptedQuestionsCount(isQuestionAttempted);
        int notAttemptedQuestions = totalQuestions - attemptedQuestions;

        txtTotalQuestions.setText(String.valueOf(totalQuestions));
        txtAttemtedQuestion.setText(String.valueOf(attemptedCount));
        txtNotAttemtedQuestion.setText(String.valueOf(notAttemptedQuestions));

        Log.e("Reddy","TName"+topicName);
        Log.e("Reddy","firstName"+studentName);
        Log.e("Reddy","StartDate"+startedOn);
        Log.e("Reddy","Questions"+questions);
        Log.e("Reddy","Given"+enteredAnswers);
        Log.e("Reddy","ANswer"+orginalAnswers);
        Log.e("Reddy","Times"+questionTimes);

        tableLayout = findViewById(R.id.tablelayout);

        // Loop through questions and answers to dynamically create TableRow elements
        for (int i = 0; i < questions.size(); i++) {
            if (i<questions.size()){
                question  = questions.get(i);
            } else {
                Log.e("Error", "Index out of bounds for originalAnswers: " + i);
            }

            if (i<enteredAnswers.size()){
                enteredAnswer  = enteredAnswers.get(i);
            } else {
                Log.e("Error", "Index out of bounds for originalAnswers: " + i);
            }

            if (i < orginalAnswers.size()) {
                correctAnswer = orginalAnswers.get(i);
            } else {
                // Handle the case where the index is out of bounds
                Log.e("Error", "Index out of bounds for originalAnswers: " + i);
            }
            // Check if the index is within the bounds of the questionTimes list
            if (i < questionTimes.size()) {
                time  = questionTimes.get(i);
                timeInSeconds = time / 1000;
            } else {
                // Handle the case where the index is out of bounds
                Log.e("Error", "Index out of bounds for questionTimes: " + i);
            }



            TableRow row = new TableRow(this);

            // Create TextView for question
            TextView question1 = new TextView(getApplicationContext());
            question1.setText(question);
            question1.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
            question1.setPadding(14, 14, 14, 14);
            question1.setTextColor(Color.BLACK);
            question1.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1));
            question1.setGravity(Gravity.CENTER);

            TextView answerOrginal = new TextView(getApplicationContext());
            answerOrginal.setText(correctAnswer);
            answerOrginal.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
            answerOrginal.setPadding(14, 14, 14, 14);
            answerOrginal.setTextColor(Color.BLACK);
            answerOrginal.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1));
            answerOrginal.setGravity(Gravity.CENTER);

            // Create TextView for entered answer
            TextView answers = new TextView(getApplicationContext());
            answers.setText(enteredAnswer);
            answers.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
            answers.setPadding(14, 14, 14, 14);
            answers.setTextColor(Color.BLACK);
            answers.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1));
            answers.setGravity(Gravity.CENTER);

            if (enteredAnswer.isEmpty()) {
                answers.setBackgroundColor(Color.WHITE); // Set background color to white
            } else if (enteredAnswer.equals(correctAnswer)) {
                answers.setBackgroundColor(Color.parseColor("#008000"));
                correctCount++;// Set background color to green
            } else {
                answers.setBackgroundColor(Color.RED);
                wrongCount++;// Set background color to red

            }

            TextView times = new TextView(getApplicationContext());
            times.setText(formatTime(timeInSeconds));
            times.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
            times.setPadding(14, 14, 14, 14);
            times.setTextColor(Color.BLACK);
            times.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1));
            times.setGravity(Gravity.CENTER);

            // Add TableRow to TableLayout
            row.addView(question1);
            row.addView(answerOrginal);
            row.addView(answers);
            row.addView(times);


            View horizontalLine = new View(getApplicationContext());
            horizontalLine.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, 1));
            horizontalLine.setBackgroundColor(Color.BLACK);
            tableLayout.addView(row);
            if (i < questions.size() - 1) {
                tableLayout.addView(horizontalLine);
            }
        }

        txtCorrectAnswer.setText(String.valueOf(correctCount));
        txtWrongAnswer.setText(String.valueOf(wrongCount));
    }


    private String formatTime(double timeInSeconds) {
        DecimalFormat decimalFormat = new DecimalFormat("#0");
        return decimalFormat.format(timeInSeconds);
    }

    private int getAttemptedQuestionsCount(ArrayList<Boolean> isQuestionAttempted) {
        int count = 0;
        for (boolean attempted : isQuestionAttempted) {
            if (attempted) {
                count++;
            }
        }
        return count;
    }
    private ArrayList<Boolean> convertStringListToBooleanList(ArrayList<String> stringIsQuestionAttempted) {
        ArrayList<Boolean> booleanList = new ArrayList<>();
        for (String value : stringIsQuestionAttempted) {
            booleanList.add(value.equals("1"));
        }
        return booleanList;
    }
}