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

;

import com.dst.abacustrainner.R;
import com.dst.abacustrainner.database.ParcelableLong;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class VisualResultActivity extends AppCompatActivity {
    TextView txtTotalQuestions,txtAttemtedQueston,txtNotAttemtedQuestion,txtCorrectAnswer,txtworngAnswer,txtstudent,txtstartedon;
    TableLayout tableLayout;
    String question ;
    String enteredAnswer ;
    String correctAnswer ;
    long time;
    double timeInSeconds;
    String studentName,startedOn;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visual_result);

        txtTotalQuestions=findViewById(R.id.txt_questions);
        txtAttemtedQueston=findViewById(R.id.txt_attemted_question);
        txtNotAttemtedQuestion=findViewById(R.id.txt_not_questions);
        txtCorrectAnswer=findViewById(R.id.txt_correct_answer);
        txtworngAnswer=findViewById(R.id.txt_wrong_answer);
        tableLayout=findViewById(R.id.tablelayout);
        txtstudent = findViewById(R.id.txt_student);
        txtstartedon =findViewById(R.id.txt_submitted_on);

        Intent intent = getIntent();
        studentName =intent.getStringExtra("firstName");
        startedOn = intent.getStringExtra("submitedOn");
        ArrayList<String> questions = intent.getStringArrayListExtra("questions");
        ArrayList<String> correctedAnswer = intent.getStringArrayListExtra("correctAnswers");
        ArrayList<String> enteredAnswers = intent.getStringArrayListExtra("enteredAnswers");
        ArrayList<String> stringIsQuestionAttempted = getIntent().getStringArrayListExtra("isQuestionAttempted");
        ArrayList<String> stringIsQuestionCorrect = getIntent().getStringArrayListExtra("isQuestionCorrect");

        ArrayList<Boolean> isQuestionAttempted = convertStringListToBooleanList(stringIsQuestionAttempted);
        ArrayList<Boolean> isQuestionCorrect = convertStringListToBooleanList(stringIsQuestionCorrect);

        txtstudent.setText(studentName);
        txtstartedon.setText(startedOn);


        List<Long> questionTimes = new ArrayList<>();

        // Retrieve ParcelableLong list and convert it back to Long list
        ArrayList<ParcelableLong> parcelableTimes = intent.getParcelableArrayListExtra("questionTimes");
        if (parcelableTimes != null) {
            for (ParcelableLong parcelableLong : parcelableTimes) {
                questionTimes.add(parcelableLong.getValue());
            }
        }

        int attemptedCount = getAttemptedQuestionsCount(isQuestionAttempted);
        int correctCount = getCorrectAnswersCount(isQuestionAttempted, isQuestionCorrect);


        txtAttemtedQueston.setText(String.valueOf(attemptedCount));
        txtCorrectAnswer.setText(String.valueOf(correctCount));

        int totalQuestions = questions.size();
        int attemptedQuestions = getAttemptedQuestionsCount(isQuestionAttempted);
        int notAttemptedQuestions = totalQuestions - attemptedQuestions;
        //int correctAnswerCount = getCorrectAnswersCount(isQuestionCorrect);

        int wrongAnswerCount = attemptedQuestions - correctCount;
        // Set the statistics in the TextViews
        txtTotalQuestions.setText(String.valueOf(totalQuestions));
        //txtAttemtedQueston.setText(String.valueOf(attemptedQuestions));
        txtNotAttemtedQuestion.setText(String.valueOf(notAttemptedQuestions));
        // txtCorrectAnswer.setText(String.valueOf(correctAnswerCount));
        txtworngAnswer.setText(String.valueOf(wrongAnswerCount));

        Log.e("Reddy","firstName"+studentName);
        Log.e("Reddy","StartDate"+startedOn);
        Log.e("Reddy","Questions"+questions);
        Log.e("Reddy","Given"+enteredAnswers);
        Log.e("Reddy","ANswer"+correctedAnswer);
        Log.e("Reddy","Times"+questionTimes);

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

            if (i < correctedAnswer.size()) {
                correctAnswer = correctedAnswer.get(i);
            } else {
                // Handle the case where the index is out of bounds
                Log.e("Error", "Index out of bounds for originalAnswers: " + i);
            }
            if (i < questionTimes.size()) {
                time  = questionTimes.get(i);
                timeInSeconds = time / 1000;
            } else {
                // Handle the case where the index is out of bounds
                Log.e("Error", "Index out of bounds for questionTimes: " + i);            }

            TableRow row = new TableRow(this);

            TextView question1 = new TextView(getApplicationContext());
            question1.setText(question);
            question1.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
            question1.setPadding(14, 14, 14, 14);
            question1.setTextColor(Color.BLACK);
            question1.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1));
            question1.setGravity(Gravity.RIGHT);

            TextView answers = new TextView(getApplicationContext());
            answers.setText(correctAnswer);
            answers.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
            answers.setPadding(14,14,14,14);
            answers.setTextColor(Color.BLACK);
            answers.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1));
            answers.setGravity(Gravity.CENTER);


            TextView givenname = new TextView(getApplicationContext());
            givenname.setText(enteredAnswer);
            givenname.setPadding(14,14,14,14);
            givenname.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
            givenname.setTextColor(Color.WHITE);
            givenname.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1));
            givenname.setGravity(Gravity.CENTER);

            if (enteredAnswer.isEmpty()) {
                givenname.setBackgroundColor(Color.WHITE); // Set background color to white
            } else if (enteredAnswer.equals(correctAnswer)) {
                givenname.setBackgroundColor(Color.parseColor("#008000")); // Set background color to green
            } else {
                givenname.setBackgroundColor(Color.RED); // Set background color to red
            }

            TextView times = new TextView(getApplicationContext());
            times.setText(formatTime(timeInSeconds));
            times.setPadding(14,14,14,14);
            times.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
            times.setTextColor(Color.BLACK);
            times.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1));
            times.setGravity(Gravity.CENTER);

            row.addView(question1);
            row.addView(answers);
            row.addView(givenname);
            row.addView(times);
            View horizontalLine = new View(getApplicationContext());
            horizontalLine.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, 1));
            horizontalLine.setBackgroundColor(Color.BLACK);
            tableLayout.addView(row);
            if (i < questions.size() - 1) {
                tableLayout.addView(horizontalLine);
            }

        }

    }

    private ArrayList<Boolean> convertStringListToBooleanList(ArrayList<String> stringIsQuestionAttempted) {
        ArrayList<Boolean> booleanList = new ArrayList<>();
        for (String value : stringIsQuestionAttempted) {
            booleanList.add(value.equals("1"));
        }
        return booleanList;
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

    private int getCorrectAnswersCount(ArrayList<Boolean> isQuestionAttempted, ArrayList<Boolean> isQuestionCorrect) {
        int count = 0;

        // Make sure both lists have the same size to avoid IndexOutOfBoundsException
        if (isQuestionAttempted.size() == isQuestionCorrect.size()) {
            for (int i = 0; i < isQuestionAttempted.size(); i++) {
                boolean attempted = isQuestionAttempted.get(i);
                boolean correct = isQuestionCorrect.get(i);

                if (attempted && correct) {
                    count++;
                }
            }
        } else {
            // Handle the case where the lists have different sizes
            Log.e("ResultActivity", "Size mismatch between isQuestionAttempted and isQuestionCorrect");
        }

        return count;
    }

    private String formatTime(double timeInSeconds) {
        DecimalFormat decimalFormat = new DecimalFormat("#0");
        return decimalFormat.format(timeInSeconds);
    }

}