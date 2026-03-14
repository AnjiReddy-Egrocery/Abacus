package com.dst.abacustrainner.Activity;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spanned;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
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
import com.dst.abacustrainner.Model.AllocatedViewSubTopicResultResponse;
import com.dst.abacustrainner.Model.ViewAssignmentResultResponse;
import com.dst.abacustrainner.R;
import com.dst.abacustrainner.Services.ApiClient;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

public class ViewResultAssignmentDetailsActivity extends AppCompatActivity {
    String examRnm="",topicName="",firstName="",startDate="",AttentQuestions="",Attamted="",Correct="",inCorrect="";
    TableLayout tabLayout;
    TextView txtName,txtStartDate,txtTopicName,dateTime,txtTotalQuestions,txtAttemtedQueston,txtCorrectAnswer,txtworngAnswer,txtTotalQuestion,txtAttemtedQuestons,txtCorrectAnswers,txtworngAnswers;
    private PieChart pieChart;
    ScrollView scrollView;
    LinearLayout layoutFirst,layoutSecond;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_result_assignment_details);

        tabLayout = findViewById(R.id.tablelayout);
       // txtName=findViewById(R.id.txt_stu_name);
       //txtStartDate=findViewById(R.id.txt_date_start);
        txtTopicName=findViewById(R.id.txt_topic_name);
        dateTime = findViewById(R.id.txtDate);
        txtTotalQuestions=findViewById(R.id.txt_questions);
        txtAttemtedQueston=findViewById(R.id.txt_attemted_question);
        //txtNotAttemtedQuestion=findViewById(R.id.txt_not_questions);
        txtCorrectAnswer=findViewById(R.id.txt_correct_answer);
        txtworngAnswer=findViewById(R.id.txt_wrong_answer);
        txtTotalQuestion=findViewById(R.id.txt_question);
        txtAttemtedQuestons=findViewById(R.id.txt_attemted_questions);
        txtCorrectAnswers=findViewById(R.id.txt_correct_answers);
        txtworngAnswers=findViewById(R.id.txt_wrong_answers);

        scrollView= findViewById(R.id.scroll_view);
        layoutFirst = findViewById(R.id.layout_first);
        layoutSecond = findViewById(R.id.layout_second);
        pieChart = findViewById(R.id.pieChart);

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
        Bundle bundle=getIntent().getExtras();
        examRnm=bundle.getString("examRnm");

        Log.d("Reddy","Id"+examRnm);

        txtTotalQuestions.setText(AttentQuestions);
        txtAttemtedQueston.setText(Attamted);
        txtCorrectAnswer.setText(Correct);
        txtworngAnswer.setText(inCorrect);

        txtTotalQuestion.setText(AttentQuestions);
        txtAttemtedQuestons.setText(Attamted);
        txtCorrectAnswers.setText(Correct);
        txtworngAnswers.setText(inCorrect);

        ViewMethod(examRnm);




        String currentDateTime = getCurrentDateTime();

        // Display the date and time in the TextView
        dateTime.setText(currentDateTime);


    }

    private void fadeIn(View view) {
        view.setAlpha(0f);
        view.setVisibility(View.VISIBLE);
        view.animate().alpha(1f).setDuration(0).start();
    }

    private void fadeOut(View view) {
        view.animate().alpha(0f).setDuration(0).withEndAction(() -> view.setVisibility(View.GONE)).start();
    }

    private void ViewMethod(String examRnm) {
       /* HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);*/
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://www.abacustrainer.com/") // Replace with your API URL
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
        ApiClient apiClient=retrofit.create(ApiClient.class);
        RequestBody idPart = RequestBody.create(MediaType.parse("text/plain"), examRnm);

        Call<ViewAssignmentResultResponse> call=apiClient.viewAssignmentResult(idPart);
        call.enqueue(new Callback<ViewAssignmentResultResponse>() {
            @Override
            public void onResponse(Call<ViewAssignmentResultResponse> call, Response<ViewAssignmentResultResponse> response) {

                if (response.isSuccessful() && response.body() != null) {
                    ViewAssignmentResultResponse.Result result = response.body().getResult();

                        topicName=result.getTopicName();

                        txtTopicName.setText(topicName);
                    List<ViewAssignmentResultResponse.Question> questionsList = result.getQuestionsList();

                                int totalQuestions = questionsList.size();
                                int attempted = 0;
                                int correct = 0;
                                int incorrect = 0;
                                LayoutInflater inflater = LayoutInflater.from(ViewResultAssignmentDetailsActivity.this);
                                for (int i = 0; i < questionsList.size(); i++) {
                                    ViewAssignmentResultResponse.Question questionObj = questionsList.get(i);
                                    String questionHtml = questionObj.getQuestion();
                                    String answer = questionObj.getAnswer();
                                    String given = questionObj.getGiven();
                                    int isCorrect = questionObj.getIs_currect();
                                    int status = questionObj.getStatus();
                                    String timeTaken = String.valueOf(questionObj.getTime_taken());

                                    if (status == 1) {
                                        attempted++;
                                    }
                                    if (isCorrect == 1) {
                                        correct++;
                                    } else {
                                        incorrect++;
                                    }
                                    int notAttempted = totalQuestions - attempted;

                                    // ✅ Now update Pie Chart
                                    updatePieChart(attempted, notAttempted, correct, incorrect);

                                    Log.d("Question_Debug", "Question Text: " + questionHtml);




                                    // Set question and its properties in separate TextViews
                                    TableRow row = new TableRow(getApplicationContext());

                                    LinearLayout questionLayout = new LinearLayout(ViewResultAssignmentDetailsActivity.this);
                                    questionLayout.setOrientation(LinearLayout.VERTICAL);
                                    questionLayout.setLayoutParams(
                                            new TableRow.LayoutParams(
                                                    0,
                                                    TableRow.LayoutParams.WRAP_CONTENT,
                                                    1
                                            )
                                    );
                                    questionLayout.setGravity(Gravity.CENTER);

// ✅ Extract image from HTML
                                    Pattern pattern = Pattern.compile("<img[^>]+src=\"([^\"]+)\"");
                                    Matcher matcher = pattern.matcher(questionHtml);

                                    if (matcher.find()) {

                                        String imageUrl = matcher.group(1);

                                        Log.d("QuestionDebug", "Original URL: " + imageUrl);

                                        // ✅ FIX RELATIVE PATH
                                        if (imageUrl.contains("../../../")) {
                                            imageUrl = imageUrl.replace("../../../", "");
                                            imageUrl = "https://www.abacustrainer.com/" + imageUrl;
                                        }
                                        Log.d("QuestionDebug", "Final URL: " + imageUrl);

                                        ImageView imageView = new ImageView(ViewResultAssignmentDetailsActivity.this);
                                        imageView.setAdjustViewBounds(true);
                                        imageView.setMaxHeight(300);

                                        Glide.with(ViewResultAssignmentDetailsActivity.this)
                                                .load(imageUrl)
                                                .into(imageView);

                                        questionLayout.addView(imageView);

                                    } else {

                                        // ---------- TEXT QUESTION ----------
                                        TextView questionTextView = new TextView(ViewResultAssignmentDetailsActivity.this);

                                        String cleanedHtml = questionHtml.replaceAll("<img[^>]+>", "");

                                        Spanned spannedText = HtmlCompat.fromHtml(
                                                cleanedHtml,
                                                HtmlCompat.FROM_HTML_MODE_LEGACY
                                        );

                                        String finalText = spannedText.toString()
                                                .replace("\u00A0", "")
                                                .trim();

                                        questionTextView.setText(finalText);
                                        questionTextView.setTextSize(18);
                                        questionTextView.setTextColor(Color.BLACK);
                                        questionTextView.setGravity(Gravity.CENTER);
                                        questionTextView.setPadding(12,12,12,12);

                                        questionLayout.addView(questionTextView);
                                    }


                                    TextView answers = new TextView(getApplicationContext());
                                    answers.setText(answer);
                                    answers.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
                                    answers.setPadding(14,14,14,14);
                                    answers.setTextColor(Color.BLACK);
                                    answers.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1));
                                    answers.setGravity(Gravity.CENTER);


                                    TextView givenname = new TextView(getApplicationContext());
                                    givenname.setText(given);
                                    givenname.setPadding(14,14,14,14);
                                    givenname.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
                                    givenname.setTextColor(Color.BLACK);
                                    givenname.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1));
                                    givenname.setGravity(Gravity.CENTER);

                                    TextView time = new TextView(getApplicationContext());
                                    time.setText(timeTaken);
                                    time.setPadding(14,14,14,14);
                                    time.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
                                    time.setTextColor(Color.BLACK);
                                    time.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1));
                                    time.setGravity(Gravity.CENTER);


                                    row.addView(questionLayout);
                                    row.addView(answers);
                                    row.addView(givenname);
                                    row.addView(time);

                                    tabLayout.addView(row);

                                    if (i < questionsList.size() - 1) {
                                        View separator = inflater.inflate(R.layout.separator_row, tabLayout, false);
                                        tabLayout.addView(separator);
                                    }

                                }

                                txtTotalQuestions.setText(String.valueOf(totalQuestions));
                                txtAttemtedQueston.setText(String.valueOf(attempted));
                                txtCorrectAnswer.setText(String.valueOf(correct));
                                txtworngAnswer.setText(String.valueOf(incorrect));

                                txtTotalQuestion.setText(String.valueOf(totalQuestions));
                                txtAttemtedQuestons.setText(String.valueOf(attempted));
                                txtCorrectAnswers.setText(String.valueOf(correct));
                                txtworngAnswers.setText(String.valueOf(incorrect));

                            }else {

                }

            }

            @Override
            public void onFailure(Call<ViewAssignmentResultResponse> call, Throwable t) {

            }
        });
    }

    private void updatePieChart(int attempted, int notAttempted, int correct, int incorrect) {
        ArrayList<PieEntry> pieEntries = new ArrayList<>();
        ArrayList<Integer> colors = new ArrayList<>();

        if (attempted == 0) {
            // Show only "Not Attempted" when no question is attempted
            pieEntries.add(new PieEntry(notAttempted, "Not Attempted"));
            colors.add(ContextCompat.getColor(this, R.color.orange));
        } else {
            // Show attempted section
            pieEntries.add(new PieEntry(attempted, "Attempted"));
            colors.add(ContextCompat.getColor(this, R.color.purple));

            if (correct > 0) {
                pieEntries.add(new PieEntry(correct, "Correct"));
                colors.add(ContextCompat.getColor(this, R.color.dark_green));
            }
            if (incorrect > 0) {
                pieEntries.add(new PieEntry(incorrect, "Incorrect"));
                colors.add(ContextCompat.getColor(this, R.color.dark_red));
            }
            if (notAttempted > 0) {
                pieEntries.add(new PieEntry(notAttempted, "Not Attempted"));
                colors.add(ContextCompat.getColor(this, R.color.orange));
            }
        }


        // If there is no data, clear chart
        if (pieEntries.isEmpty()) {
            pieChart.clear();
            return;
        }

        // Pie DataSet Configuration
        PieDataSet dataSet = new PieDataSet(pieEntries, "");
        dataSet.setColors(colors); // Set dynamic colors based on available data
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
        pieChart.setCenterText("Pie Chart");
        pieChart.setCenterTextSize(16f);

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

    }

    private String getCurrentDateTime() {
        // Format for date and time
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy | hh:mm a", Locale.getDefault());
        // Get current date and time
        return sdf.format(new Date());
    }



}