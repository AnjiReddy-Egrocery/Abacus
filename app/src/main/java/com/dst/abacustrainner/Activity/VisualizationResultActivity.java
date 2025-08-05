package com.dst.abacustrainner.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.dst.abacustrainner.R;
import com.dst.abacustrainner.User.HomeActivity;
import com.dst.abacustrainner.database.ParcelableLong;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class VisualizationResultActivity extends AppCompatActivity {
    TextView txtTotalQuestions,txtAttemtedQueston,txtNotAttemtedQuestion,txtCorrectAnswer,txtworngAnswer,showLevelTop,showLevelCompleted,dateTime,txtTotalQuestion,txtAttemtedQuestons,txtCorrectAnswers,txtworngAnswers,txtTotalTime;
    TableLayout tableLayout;
    LinearLayout btnSubmit,RetakeTest,NextLevel;
    private int currentQuestionIndex = 0;
    private PieChart pieChart;
    double timeInSeconds;
    String orginalAnswer;
    String question;
    String enteredAnswer;
    long time;
    String totalTime;
    ScrollView scrollView;
    LinearLayout layoutFirst,layoutSecond;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualization_result);

        txtTotalQuestions=findViewById(R.id.txt_questions);
        txtAttemtedQueston=findViewById(R.id.txt_attemted_question);
        txtNotAttemtedQuestion=findViewById(R.id.txt_not_questions);
        txtCorrectAnswer=findViewById(R.id.txt_correct_answer);
        txtworngAnswer=findViewById(R.id.txt_wrong_answer);
        tableLayout=findViewById(R.id.tablelayout);
        RetakeTest =findViewById(R.id.retake);
        NextLevel =findViewById(R.id.next_level);
        showLevelTop=findViewById(R.id.display_level);
        showLevelCompleted=findViewById(R.id.combined_text_view);
        //dateTime = findViewById(R.id.txtDate);
        btnSubmit =findViewById(R.id.but_submit_result_first);
        txtTotalQuestion=findViewById(R.id.txt_question);
        txtAttemtedQuestons=findViewById(R.id.txt_attemted_questions);
        txtCorrectAnswers=findViewById(R.id.txt_correct_answers);
        txtworngAnswers=findViewById(R.id.txt_wrong_answers);
        txtTotalTime = findViewById(R.id.txt_total_time);

        scrollView= findViewById(R.id.scroll_view);
        layoutFirst = findViewById(R.id.layout_first);
        layoutSecond = findViewById(R.id.layout_second);

        scrollView.getViewTreeObserver().addOnScrollChangedListener(() -> {
            int scrollY = scrollView.getScrollY();

            if (scrollY > 100 && layoutFirst.getVisibility() == View.VISIBLE) {
                fadeOut(layoutFirst);
                fadeIn(layoutSecond);
            } else if (scrollY <= 100 && layoutSecond.getVisibility() == View.VISIBLE) {
                fadeOut(layoutSecond);
                fadeIn(layoutFirst);
            }
        });

        Intent intent = getIntent();
        ArrayList<String> questions = intent.getStringArrayListExtra("questions");
        ArrayList<String> enteredAnswers = intent.getStringArrayListExtra("enteredAnswers");
        ArrayList<String> originalAnswers = intent.getStringArrayListExtra("originalAnswers");
        ArrayList<String> stringIsQuestionAttempted = getIntent().getStringArrayListExtra("isQuestionAttempted");
        ArrayList<String> stringIsQuestionCorrect = getIntent().getStringArrayListExtra("isQuestionCorrect");
        List<Long> questionTimes = new ArrayList<>();

        Intent intents = getIntent();
        int levelValue = intents.getIntExtra("level", 0);
        totalTime =intent.getStringExtra("TOTAL_TIME");

        txtTotalTime.setText(totalTime);

        String combinedText1 =String.format("Level Visualization game level %s.", levelValue);
        showLevelTop.setText(String.valueOf(combinedText1));

        String combinedText =String.format("Great job on completing level %s. Keep practicing!", levelValue);
        showLevelCompleted.setText(combinedText);

        // Retrieve ParcelableLong list and convert it back to Long list
        ArrayList<ParcelableLong> parcelableTimes = intent.getParcelableArrayListExtra("questionTimes");
        if (parcelableTimes != null) {
            for (ParcelableLong parcelableLong : parcelableTimes) {
                questionTimes.add(parcelableLong.getValue());
            }
        }

        Log.e("Reddy","Times"+parcelableTimes);

        ArrayList<Boolean> isQuestionAttempted = convertStringListToBooleanList(stringIsQuestionAttempted);
        ArrayList<Boolean> isQuestionCorrect = convertStringListToBooleanList(stringIsQuestionCorrect);

        Log.e("Reddy","OrginalAns"+originalAnswers);


        int totalQuestions = questions.size();
        int attemptedCount = getAttemptedQuestionsCount(isQuestionAttempted);
        int correctCount = getCorrectAnswersCount(isQuestionAttempted, isQuestionCorrect);
        Log.e("ResultActivity", "Correct Count: " + correctCount);
        txtCorrectAnswer.setText(String.valueOf(correctCount));
        txtCorrectAnswers.setText(String.valueOf(correctCount));


        txtAttemtedQueston.setText(String.valueOf(attemptedCount));
        txtAttemtedQuestons.setText(String.valueOf(attemptedCount));
        // txtCorrectAnswer.setText(String.valueOf(correctCount));

        txtTotalQuestions.setText(String.valueOf(totalQuestions));
        txtTotalQuestion.setText(String.valueOf(totalQuestions));



        int attemptedQuestions = getAttemptedQuestionsCount(isQuestionAttempted);
        int notAttemptedQuestions = totalQuestions - attemptedQuestions;
        //int correctAnswerCount = getCorrectAnswersCount(isQuestionCorrect);




        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(VisualizationResultActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });

        // Get current date and time
        String currentDateTime = getCurrentDateTime();

        // Display the date and time in the TextView
//        dateTime.setText(currentDateTime);



        int wrongAnswerCount = attemptedQuestions - correctCount;
        // Set the statistics in the TextViews

        txtNotAttemtedQuestion.setText(String.valueOf(notAttemptedQuestions));
        // txtCorrectAnswer.setText(String.valueOf(correctAnswerCount));
        txtworngAnswer.setText(String.valueOf(wrongAnswerCount));
        txtworngAnswers.setText(String.valueOf(wrongAnswerCount));

        Log.e("Reddy","Questions"+questions);
        Log.e("Reddy","Given"+enteredAnswers);
        Log.e("Reddy","ANswer"+originalAnswers);
        Log.e("Reddy","Times"+questionTimes);

        // ************************** Graph part ***************************** //


        pieChart = findViewById(R.id.pieChart);

        ArrayList<PieEntry> pieEntries = new ArrayList<>();
        pieEntries.add(new PieEntry(attemptedCount, "Attempted"));
        pieEntries.add(new PieEntry(notAttemptedQuestions, "Not Attempted"));
        pieEntries.add(new PieEntry(correctCount, "Correct"));
        pieEntries.add(new PieEntry(wrongAnswerCount, "Incorrect"));

        // Sample data for the Pie Chart
        PieDataSet dataSet = new PieDataSet(pieEntries, "Sample Data");
        dataSet.setColors(new int[]{
                ContextCompat.getColor(this, R.color.purple),
                ContextCompat.getColor(this, R.color.orange),
                ContextCompat.getColor(this, R.color.dark_green),
                ContextCompat.getColor(this, R.color.dark_red),});
        dataSet.setValueTextSize(14f);
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setDrawIcons(false);


        PieData pieData = new PieData(dataSet);

        // Customize the chart
        pieChart.setData(pieData);
        pieChart.setUsePercentValues(true);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleRadius(40f);
        pieChart.setTransparentCircleRadius(50f);
        pieChart.setCenterText(totalTime);
        pieChart.setCenterTextSize(12f);
        // Set labels and values outside the slices
        dataSet.setValueLinePart1Length(0.5f);
        dataSet.setValueLinePart2Length(0.8f);
        dataSet.setValueLineColor(Color.BLACK);
        dataSet.setUsingSliceColorAsValueLineColor(true);
        dataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        dataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);

        // Set offset for better visibility
        dataSet.setValueLineVariableLength(true);

        // Disable description text
        Description description = new Description();
        description.setText("");
        pieChart.setDescription(description);
        // Refresh chart
        pieChart.setData(pieData);
        pieChart.invalidate();


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

    private void fadeIn(View view) {
        view.setAlpha(0f);
        view.setVisibility(View.VISIBLE);
        view.animate().alpha(1f).setDuration(0).start();
    }

    private void fadeOut(View view) {
        view.animate().alpha(0f).setDuration(0).withEndAction(() -> view.setVisibility(View.GONE)).start();
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

    private String getCurrentDateTime() {
        // Format for date and time
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy | hh:mm a", Locale.getDefault());
        // Get current date and time
        return sdf.format(new Date());
    }
}