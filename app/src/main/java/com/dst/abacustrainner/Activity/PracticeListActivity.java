package com.dst.abacustrainner.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spanned;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.text.HtmlCompat;

import com.bumptech.glide.Glide;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PracticeListActivity extends AppCompatActivity {

    TextView txtTopicName,txtName,txtStartedOn,txtNotAttemtedQuestion,showLevelTop,showLevelCompleted,dateTime,txtTotalQuestion,txtAttemtedQuestons,txtCorrectAnswers,txtworngAnswers,txtTotalTime;
    private PieChart pieChart;
    LinearLayout btnSubmit,RetakeTest,NextLevel;
    String topicName="";
    String studentName="";

    String startedOn="";
    TableLayout tableLayout;

    double timeInSeconds;

    String question ;
    String enteredAnswer ;
    String correctAnswer ;
    long time;
    String totalTime;

    ScrollView scrollView;
    LinearLayout layoutFirst,layoutSecond;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practice_list);

        //txtTopicName = findViewById(R.id.txt_subject);
//        txtName =findViewById(R.id.txt_name);
//        txtStartedOn = findViewById(R.id.txt_date);

        btnSubmit =findViewById(R.id.but_submit_result_first);
        dateTime = findViewById(R.id.txtDate);
        txtTotalQuestion=findViewById(R.id.txt_question);
        txtAttemtedQuestons=findViewById(R.id.txt_attemted_questions);
        txtCorrectAnswers=findViewById(R.id.txt_correct_answers);
        txtworngAnswers=findViewById(R.id.txt_wrong_answers);
        showLevelTop=findViewById(R.id.display_level);
        showLevelCompleted=findViewById(R.id.combined_text_view);
//        txtNotAttemtedQuestion = findViewById(R.id.txt_not_questions);
        pieChart = findViewById(R.id.pieChart);


        layoutFirst = findViewById(R.id.layout_first);
        layoutSecond = findViewById(R.id.layout_second);



        txtTotalTime = findViewById(R.id.txt_total_time);


        Intent intent = getIntent();
        totalTime =intent.getStringExtra("TOTAL_TIME");
        topicName = intent.getStringExtra("topicName");
        studentName = intent.getStringExtra("firstName");
        startedOn = intent.getStringExtra("startedOn");

        txtTotalTime.setText(totalTime);



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

//        txtTopicName.setText(topicName);
//        txtName.setText(studentName);
//        txtStartedOn.setText(startedOn);

        String combinedText1 =String.format("%s ", topicName);
        showLevelTop.setText(String.valueOf(combinedText1));

        String combinedText =String.format("Great job %s  . Keep practicing!", topicName);
        showLevelCompleted.setText(combinedText);

        int correctCount = 0;
        int wrongCount = 0;


        int totalQuestions = questions.size();
        int attemptedQuestions = getAttemptedQuestionsCount(isQuestionAttempted);

        int notAttemptedQuestions = totalQuestions - attemptedQuestions;

        int wrongAnswerCount = attemptedQuestions - correctCount;


        txtTotalQuestion.setText(String.valueOf(totalQuestions));

        txtAttemtedQuestons.setText(String.valueOf(attemptedCount));
//        txtNotAttemtedQuestion.setText(String.valueOf(notAttemptedQuestions));

        Log.e("Reddy","TName"+topicName);
        Log.e("Reddy","firstName"+studentName);
        Log.e("Reddy","StartDate"+startedOn);
        Log.e("Reddy","Questions"+questions);
        Log.e("Reddy","Given"+enteredAnswers);
        Log.e("Reddy","ANswer"+orginalAnswers);
        Log.e("Reddy","Times"+questionTimes);

        tableLayout = findViewById(R.id.tablelayout);
        // Get current date and time
        String currentDateTime = getCurrentDateTime();

        // Display the date and time in the TextView
        dateTime.setText(currentDateTime);


        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(PracticeListActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });




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




            ArrayList<PieEntry> entries = new ArrayList<>();
            entries.add(new PieEntry(attemptedCount, "Attempted"));
            entries.add(new PieEntry(notAttemptedQuestions, "Not Attempted"));
            entries.add(new PieEntry(correctCount, "Correct"));
            entries.add(new PieEntry(wrongCount, "Incorrect"));

            PieDataSet dataSet = new PieDataSet(entries, "");

            dataSet.setColors(new int[]{
                    ContextCompat.getColor(this, R.color.purple),
                    ContextCompat.getColor(this, R.color.orange),
                    ContextCompat.getColor(this, R.color.dark_green),
                    ContextCompat.getColor(this, R.color.dark_red)
            });

            dataSet.setValueTextSize(12f);
            dataSet.setValueTextColor(Color.BLACK);


            // spacing fix
            dataSet.setValueLinePart1Length(0.5f);
            dataSet.setValueLinePart2Length(0.7f);
            dataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
            dataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);

            PieData data = new PieData(dataSet);

            pieChart.setUsePercentValues(false); // important
            pieChart.setData(data);
            pieChart.setDrawHoleEnabled(true);
            pieChart.setHoleRadius(30f);
            pieChart.setTransparentCircleRadius(40f);
            pieChart.setCenterText(totalTime);
            pieChart.setCenterTextSize(10f);

            pieChart.setExtraOffsets(5f, 5f, 5f, 5f);

            Description desc = new Description();
            desc.setText("");
            pieChart.setDescription(desc);

            pieChart.invalidate();






            TableRow row = new TableRow(this);

            // Create TextView for question
            LinearLayout questionLayout = new LinearLayout(this);
            questionLayout.setOrientation(LinearLayout.VERTICAL);
            questionLayout.setLayoutParams(
                    new TableRow.LayoutParams(0,
                            TableRow.LayoutParams.WRAP_CONTENT, 1)
            );
            questionLayout.setGravity(Gravity.CENTER);

// ✅ Extract image URL
            Pattern pattern = Pattern.compile("<img[^>]+src=\"([^\"]+)\"");
            Matcher matcher = pattern.matcher(question);

            if (matcher.find()) {

                String imageUrl = matcher.group(1);

                ImageView imageView = new ImageView(this);
                imageView.setAdjustViewBounds(true);
                imageView.setMaxHeight(300);

                Glide.with(this)
                        .load(imageUrl)
                        .into(imageView);

                questionLayout.addView(imageView);

            } else {

                TextView questionText = new TextView(this);

                // ✅ Remove image tags if any
                String cleanedHtml = question.replaceAll("<img[^>]+>", "");

                // ✅ Convert HTML → Normal Text
                Spanned spannedText = HtmlCompat.fromHtml(
                        cleanedHtml,
                        HtmlCompat.FROM_HTML_MODE_LEGACY
                );

                String finalText = spannedText.toString()
                        .replace("\u00A0", "")   // remove &nbsp;
                        .trim();

                questionText.setText(finalText);
                questionText.setTextSize(18);
                questionText.setGravity(Gravity.CENTER);

                questionLayout.addView(questionText);

            }
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
            row.addView(questionLayout);
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


        txtCorrectAnswers.setText(String.valueOf(correctCount));

        txtworngAnswers.setText(String.valueOf(wrongCount));
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

    private String getCurrentDateTime() {
        // Format for date and time
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy | hh:mm a", Locale.getDefault());
        // Get current date and time
        return sdf.format(new Date());
    }
}