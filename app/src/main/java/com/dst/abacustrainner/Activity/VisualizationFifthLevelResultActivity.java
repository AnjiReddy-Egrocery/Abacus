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

public class VisualizationFifthLevelResultActivity extends AppCompatActivity {
    TextView txtTotalQuestions,txtAttemtedQueston,txtNotAttemtedQuestion,txtCorrectAnswer,txtworngAnswer;
    TableLayout tableLayout;
    private int currentQuestionIndex = 0;

    double timeInSeconds;
    String orginalAnswer;
    String question;
    String enteredAnswer;
    long time;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualization_fifth_level_result);

        txtTotalQuestions=findViewById(R.id.txt_questions);
        txtAttemtedQueston=findViewById(R.id.txt_attemted_question);
        txtNotAttemtedQuestion=findViewById(R.id.txt_not_questions);
        txtCorrectAnswer=findViewById(R.id.txt_correct_answer);
        txtworngAnswer=findViewById(R.id.txt_wrong_answer);
        tableLayout=findViewById(R.id.tablelayout);

        Intent intent = getIntent();
        ArrayList<String> questions = intent.getStringArrayListExtra("questions");
        ArrayList<String> enteredAnswers = intent.getStringArrayListExtra("enteredAnswers");
        ArrayList<String> originalAnswers = intent.getStringArrayListExtra("originalAnswers");
        ArrayList<String> stringIsQuestionAttempted = getIntent().getStringArrayListExtra("isQuestionAttempted");
        ArrayList<String> stringIsQuestionCorrect = getIntent().getStringArrayListExtra("isQuestionCorrect");
        List<Long> questionTimes = new ArrayList<>();

        // Retrieve ParcelableLong list and convert it back to Long list
        ArrayList<ParcelableLong> parcelableTimes = intent.getParcelableArrayListExtra("questionTimes");
        if (parcelableTimes != null) {
            for (ParcelableLong parcelableLong : parcelableTimes) {
                questionTimes.add(parcelableLong.getValue());
            }
        }

        ArrayList<Boolean> isQuestionAttempted = convertStringListToBooleanList(stringIsQuestionAttempted);
        ArrayList<Boolean> isQuestionCorrect = convertStringListToBooleanList(stringIsQuestionCorrect);

        Log.e("Reddy","OrginalAns"+originalAnswers);

        int totalQuestions = questions.size();
        int attemptedCount = getAttemptedQuestionsCount(isQuestionAttempted);
        int correctCount = getCorrectAnswersCount(isQuestionAttempted, isQuestionCorrect);
        Log.e("ResultActivity", "Correct Count: " + correctCount);
        txtCorrectAnswer.setText(String.valueOf(correctCount));


        txtAttemtedQueston.setText(String.valueOf(attemptedCount));
        // txtCorrectAnswer.setText(String.valueOf(correctCount));

        txtTotalQuestions.setText(String.valueOf(totalQuestions));

        int attemptedQuestions = getAttemptedQuestionsCount(isQuestionAttempted);
        int notAttemptedQuestions = totalQuestions - attemptedQuestions;
        //int correctAnswerCount = getCorrectAnswersCount(isQuestionCorrect);

        int wrongAnswerCount = attemptedQuestions - correctCount;
        // Set the statistics in the TextViews

        txtNotAttemtedQuestion.setText(String.valueOf(notAttemptedQuestions));
        // txtCorrectAnswer.setText(String.valueOf(correctAnswerCount));
        txtworngAnswer.setText(String.valueOf(wrongAnswerCount));

        Log.e("Reddy","Questions"+questions);
        Log.e("Reddy","Given"+enteredAnswers);
        Log.e("Reddy","ANswer"+originalAnswers);
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

            if (i < originalAnswers.size()) {
                orginalAnswer = originalAnswers.get(i);
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
            int marginInDp = 18;
            int marginInPixels = (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    marginInDp,
                    getResources().getDisplayMetrics()
            );

            TableRow row = new TableRow(getApplicationContext());
            TextView question1 = new TextView(getApplicationContext());
            question1.setText(question.replace(" ", "\n"));
            question1.setPadding(14, 14, 14, 14);
            question1.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
            question1.setTextColor(Color.BLACK);
            TableRow.LayoutParams layoutParams1 = new TableRow.LayoutParams(
                    0,
                    TableRow.LayoutParams.WRAP_CONTENT,
                    1
            );
            layoutParams1.setMargins(0, 0, marginInPixels, 0);
            question1.setLayoutParams(layoutParams1);
            question1.setGravity(Gravity.CENTER);

            TextView orginal = new TextView(getApplicationContext());
            orginal.setText(orginalAnswer.replace(" ", "\n"));
            orginal.setPadding(14, 14, 14, 14);
            orginal.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
            orginal.setTextColor(Color.BLACK);
            TableRow.LayoutParams layoutParams2 = new TableRow.LayoutParams(
                    0,
                    TableRow.LayoutParams.WRAP_CONTENT,
                    1
            );
            layoutParams2.setMargins(0, 0, marginInPixels, 0);
            orginal.setLayoutParams(layoutParams2);
            orginal.setGravity(Gravity.CENTER);

            TextView enterAnswer = new TextView(getApplicationContext());
            enterAnswer.setText(enteredAnswer.replace(" ", "\n"));
            enterAnswer.setPadding(14, 14, 14, 14);
            enterAnswer.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
            enterAnswer.setTextColor(Color.BLACK);
            TableRow.LayoutParams layoutParams3 = new TableRow.LayoutParams(
                    0,
                    TableRow.LayoutParams.WRAP_CONTENT,
                    1
            );
            layoutParams3.setMargins(0, 0, marginInPixels, 0);
            enterAnswer.setLayoutParams(layoutParams3);
            enterAnswer.setGravity(Gravity.CENTER);

            if (enteredAnswer.isEmpty()) {
                enterAnswer.setBackgroundColor(Color.WHITE); // Set background color to white
            } else if (enteredAnswer.equals(orginalAnswer)) {
                enterAnswer.setBackgroundColor(Color.parseColor("#008000")); // Set background color to green
            } else {
                enterAnswer.setBackgroundColor(Color.RED); // Set background color to red

            }

            TextView times = new TextView(getApplicationContext());
            times.setText(formatTime(timeInSeconds));
            times.setPadding(14, 14, 14, 14);
            times.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
            times.setTextColor(Color.BLACK);
            TableRow.LayoutParams layoutParams4 = new TableRow.LayoutParams(
                    0,
                    TableRow.LayoutParams.WRAP_CONTENT,
                    1
            );
            layoutParams4.setMargins(0, 0, marginInPixels, 0);
            times.setLayoutParams(layoutParams4);
            times.setGravity(Gravity.CENTER);

            row.addView(question1);
            row.addView(orginal);
            row.addView(enterAnswer);
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