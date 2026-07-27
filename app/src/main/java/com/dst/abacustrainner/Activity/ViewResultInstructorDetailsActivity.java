package com.dst.abacustrainner.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.text.HtmlCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spanned;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dst.abacustrainner.Model.ViewInstructorResultResponse;
import com.dst.abacustrainner.Model.ViewTopicResultResponse;
import com.dst.abacustrainner.R;
import com.dst.abacustrainner.Services.ApiClient;
import com.dst.abacustrainner.User.HomeActivity;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ViewResultInstructorDetailsActivity extends AppCompatActivity {
    private String examRnm = "";
    private String topicName = "";
    private String firstName = "";
    private String startDate = "";

    private TableLayout tabLayout;

    private TextView txtTopicName;
    private TextView txtTotalQuestion;
    private TextView txtAttemtedQuestons;
    private TextView txtCorrectAnswers;
    private TextView txtworngAnswers;
    private TextView txtNotAttemted;
    private TextView dateTime;

    private PieChart pieChart;

    private ScrollView scrollView;
    private LinearLayout layoutFirst, layoutSecond;
    private LinearLayout btnSubmit;

    // Result Counts
    private int totalQuestions = 0;
    private int attempted = 0;
    private int correct = 0;
    private int incorrect = 0;
    private int notAttempted = 0;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_result_instructor_details);

        tabLayout = findViewById(R.id.tablelayout);

        txtTopicName = findViewById(R.id.txt_topic_name);

        txtTotalQuestion = findViewById(R.id.txt_question);
        txtAttemtedQuestons = findViewById(R.id.txt_attemted_questions);
        txtCorrectAnswers = findViewById(R.id.txt_correct_answers);
        txtworngAnswers = findViewById(R.id.txt_wrong_answers);
        txtNotAttemted = findViewById(R.id.txt_notattemted_questions);

        dateTime = findViewById(R.id.txtDate);

        scrollView = findViewById(R.id.scroll_view);
        layoutFirst = findViewById(R.id.layout_first);
        layoutSecond = findViewById(R.id.layout_second);

        btnSubmit = findViewById(R.id.but_submit_result_first);

        pieChart = findViewById(R.id.pieChart);

        // Intent Data
        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            examRnm = bundle.getString("examRnm", "");
            topicName = bundle.getString("topicName", "");
        }

        txtTopicName.setText(topicName);

        Log.d("Reddy", examRnm);
        Log.d("Reddy", topicName);

        // Submit Button
        btnSubmit.setOnClickListener(v -> {
            Intent intent = new Intent(
                    ViewResultInstructorDetailsActivity.this,
                    HomeActivity.class
            );
            startActivity(intent);
            finish();
        });

        // Initially show zero values
        txtTotalQuestion.setText("0");
        txtAttemtedQuestons.setText("0");
        txtCorrectAnswers.setText("0");
        txtworngAnswers.setText("0");
        txtNotAttemted.setText("0");

        // Load API Data
        ViewMethod(examRnm);
    }

    private void ViewMethod(String examRnm) {

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new HttpLoggingInterceptor()
                        .setLevel(HttpLoggingInterceptor.Level.BODY))
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://www.abacustrainer.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        ApiClient apiClient = retrofit.create(ApiClient.class);

        RequestBody idPart = RequestBody.create(
                MediaType.parse("application/json"),
                examRnm
        );

        Call<ViewInstructorResultResponse> call =
                apiClient.instructorviewResult(idPart);

        call.enqueue(new Callback<ViewInstructorResultResponse>() {

            @Override
            public void onResponse(Call<ViewInstructorResultResponse> call,
                                   Response<ViewInstructorResultResponse> response) {

                if (!response.isSuccessful()
                        || response.body() == null
                        || response.body().getResult() == null) {

                    Log.d("Reddy", "No Data Found");
                    return;
                }

                ViewInstructorResultResponse.Result result =
                        response.body().getResult();

                firstName = result.getFullName();
                startDate = result.getStartedOn();

                dateTime.setText(result.getSubmitedOn());

                List<ViewInstructorResultResponse.Question> questionsList =
                        result.getQuestionsList();

                if (questionsList == null)
                    return;

                // Reset Counts
                totalQuestions = questionsList.size();
                attempted = 0;
                correct = 0;
                incorrect = 0;

                for (ViewInstructorResultResponse.Question question : questionsList) {

                    int status = question.getStatus();
                    int isCorrect = question.getIs_currect();

                    if (status == 1) {

                        attempted++;

                        if (isCorrect == 1) {
                            correct++;
                        } else {
                            incorrect++;
                        }
                    }
                }

                notAttempted = totalQuestions - attempted;

                Log.d("Reddy",
                        "Total=" + totalQuestions +
                                " Attempted=" + attempted +
                                " Correct=" + correct +
                                " Incorrect=" + incorrect +
                                " NotAttempted=" + notAttempted);

                // Update Counts
                txtTotalQuestion.setText(String.valueOf(totalQuestions));
                txtAttemtedQuestons.setText(String.valueOf(attempted));
                txtCorrectAnswers.setText(String.valueOf(correct));
                txtworngAnswers.setText(String.valueOf(incorrect));
                txtNotAttemted.setText(String.valueOf(notAttempted));

                // Update Pie Chart ONLY ONCE
                updatePieChart(
                        attempted,
                        notAttempted,
                        correct,
                        incorrect
                );



                // Build Table
                loadQuestionsTable(questionsList);

            }

            @Override
            public void onFailure(Call<ViewInstructorResultResponse> call,
                                  Throwable t) {

                Log.e("API ERROR", t.getMessage());
            }
        });
    }

    private void updatePieChart(int attempted,
                                int notAttempted,
                                int correct,
                                int incorrect) {

        ArrayList<PieEntry> entries = new ArrayList<>();

        if (attempted > 0)
            entries.add(new PieEntry(attempted, "Attempted"));

        if (correct > 0)
            entries.add(new PieEntry(correct, "Correct"));

        if (incorrect > 0)
            entries.add(new PieEntry(incorrect, "Incorrect"));

        if (notAttempted > 0)
            entries.add(new PieEntry(notAttempted, "Not Attempted"));
        if (entries.isEmpty()) {
            pieChart.clear();
            return;
        }

        PieDataSet dataSet = new PieDataSet(entries, "");

        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(Color.parseColor("#3F51B5"));   // Attempted
        colors.add(Color.parseColor("#4CAF50"));   // Correct
        colors.add(Color.parseColor("#F44336"));   // Incorrect
        colors.add(Color.parseColor("#FF9800"));   // Not Attempted

        dataSet.setColors(colors);

        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(8f);

        dataSet.setValueTextSize(14f);
        dataSet.setValueTextColor(Color.WHITE);

        PieData data = new PieData(dataSet);

        pieChart.setData(data);

        pieChart.setUsePercentValues(false);

        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleRadius(45f);

        pieChart.setTransparentCircleRadius(50f);

        pieChart.setCenterText("Result");
        pieChart.setCenterTextSize(18f);

        pieChart.getDescription().setEnabled(false);

        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.setEntryLabelTextSize(12f);

        pieChart.setDrawEntryLabels(false);

        pieChart.getLegend().setEnabled(true);

        pieChart.animateY(1200);

        pieChart.invalidate();
    }

    private void loadQuestionsTable(List<ViewInstructorResultResponse.Question> questionsList) {

        while (tabLayout.getChildCount() > 2) {
            tabLayout.removeViewAt(2);
        }
        LayoutInflater inflater = LayoutInflater.from(this);

        Pattern pattern = Pattern.compile("<img[^>]+src=\"([^\"]+)\"");

        for (int i = 0; i < questionsList.size(); i++) {

            ViewInstructorResultResponse.Question questionObj = questionsList.get(i);

            String questionHtml = questionObj.getQuestion();
            String answer = questionObj.getAnswer();
            String given = questionObj.getGiven();
            String timeTaken = String.valueOf(questionObj.getTime_taken());

            TableRow row = new TableRow(this);

            row.setLayoutParams(new TableRow.LayoutParams(
                    TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT));

            //---------------- Question ----------------//

            LinearLayout questionLayout = new LinearLayout(this);
            questionLayout.setOrientation(LinearLayout.VERTICAL);

            questionLayout.setLayoutParams(
                    new TableRow.LayoutParams(
                            0,
                            TableRow.LayoutParams.WRAP_CONTENT,
                            1));

            Matcher matcher = pattern.matcher(questionHtml);

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

                TextView txtQuestion = new TextView(this);

                String cleanHtml =
                        questionHtml.replaceAll("<img[^>]+>", "");

                Spanned spanned = HtmlCompat.fromHtml(
                        cleanHtml,
                        HtmlCompat.FROM_HTML_MODE_LEGACY);

                txtQuestion.setText(
                        spanned.toString()
                                .replace("\u00A0", "")
                                .trim());

                txtQuestion.setTextColor(Color.BLACK);
                txtQuestion.setTextSize(18);

                txtQuestion.setGravity(Gravity.CENTER);

                txtQuestion.setPadding(10,10,10,10);

                questionLayout.addView(txtQuestion);
            }

            //---------------- Correct Answer ----------------//

            TextView txtAnswer = new TextView(this);

            txtAnswer.setText(answer);

            txtAnswer.setGravity(Gravity.CENTER);

            txtAnswer.setTextColor(Color.BLACK);

            txtAnswer.setPadding(10,10,10,10);

            txtAnswer.setLayoutParams(
                    new TableRow.LayoutParams(
                            0,
                            TableRow.LayoutParams.WRAP_CONTENT,
                            1));

            //---------------- Given Answer ----------------//

            TextView txtGiven = new TextView(this);

            txtGiven.setText(given);

            txtGiven.setGravity(Gravity.CENTER);

            txtGiven.setPadding(10,10,10,10);

            txtGiven.setLayoutParams(
                    new TableRow.LayoutParams(
                            0,
                            TableRow.LayoutParams.WRAP_CONTENT,
                            1));

            if (given == null || given.isEmpty()) {

                txtGiven.setBackgroundColor(Color.WHITE);

            } else if (given.equals(answer)) {

                txtGiven.setBackgroundColor(Color.parseColor("#4CAF50"));
                txtGiven.setTextColor(Color.WHITE);

            } else {

                txtGiven.setBackgroundColor(Color.RED);
                txtGiven.setTextColor(Color.WHITE);
            }

            //---------------- Time ----------------//

            TextView txtTime = new TextView(this);

            txtTime.setText(timeTaken);

            txtTime.setGravity(Gravity.CENTER);

            txtTime.setPadding(10,10,10,10);

            txtTime.setLayoutParams(
                    new TableRow.LayoutParams(
                            0,
                            TableRow.LayoutParams.WRAP_CONTENT,
                            1));

            //---------------- Add Views ----------------//

            row.addView(questionLayout);
            row.addView(txtAnswer);
            row.addView(txtGiven);
            row.addView(txtTime);

            tabLayout.addView(row);

            if (i != questionsList.size() - 1) {

                View separator = inflater.inflate(
                        R.layout.separator_row,
                        tabLayout,
                        false);

                tabLayout.addView(separator);
            }
        }
    }
}