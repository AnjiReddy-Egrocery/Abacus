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
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.text.HtmlCompat;


import com.dst.abacustrainner.Model.ViewTopicResultResponse;
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
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ViewResultDetailsActivity extends AppCompatActivity {

    String examRnm="",topicName="",firstName="",startDate="",AttentQuestions="",Attamted="",Correct="",inCorrect="";
    TableLayout tabLayout;
    TextView txtName,txtStartDate,txtTopicName;
    TextView txtTotalQuestions,txtAttemtedQueston,txtCorrectAnswer,txtworngAnswer,dateTime,txtTotalQuestion,txtAttemtedQuestons,txtCorrectAnswers,txtworngAnswers;
    private PieChart pieChart;
    ScrollView scrollView;
    LinearLayout layoutFirst,layoutSecond;



    @SuppressLint({"MissingInflatedId", "WrongViewCast"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_result_details);

        tabLayout = findViewById(R.id.tablelayout);
//        txtName=findViewById(R.id.txt_stu_name);
//        txtStartDate=findViewById(R.id.txt_date_start);
        txtTopicName=findViewById(R.id.txt_topic_name);
        txtTotalQuestions=findViewById(R.id.txt_questions);
        txtAttemtedQueston=findViewById(R.id.txt_attemted_question);
        //txtNotAttemtedQuestion=findViewById(R.id.txt_not_questions);
        txtCorrectAnswer=findViewById(R.id.txt_correct_answer);
        txtworngAnswer=findViewById(R.id.txt_wrong_answer);
        txtTotalQuestion=findViewById(R.id.txt_question);
        txtAttemtedQuestons=findViewById(R.id.txt_attemted_questions);
        txtCorrectAnswers=findViewById(R.id.txt_correct_answers);
        txtworngAnswers=findViewById(R.id.txt_wrong_answers);
        dateTime = findViewById(R.id.txtDate);

        scrollView= findViewById(R.id.scroll_view);
        layoutFirst = findViewById(R.id.layout_first);
        layoutSecond = findViewById(R.id.layout_second);

        scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                int scrollY = scrollView.getScrollY();
                if (scrollY > 100) { // Adjust this value based on your requirement
                    layoutFirst.setVisibility(View.GONE);
                    layoutSecond.setVisibility(View.VISIBLE);
                } else {
                    layoutFirst.setVisibility(View.VISIBLE);
                    layoutSecond.setVisibility(View.GONE);
                }
            }
        });

        Bundle bundle=getIntent().getExtras();
        examRnm=bundle.getString("examRnm");
        topicName=bundle.getString("topicName");

        txtTopicName.setText(topicName);

        txtTotalQuestions.setText(AttentQuestions);
        txtAttemtedQueston.setText(Attamted);
        txtCorrectAnswer.setText(Correct);
        txtworngAnswer.setText(inCorrect);

        txtTotalQuestion.setText(AttentQuestions);
        txtAttemtedQuestons.setText(Attamted);
        txtCorrectAnswers.setText(Correct);
        txtworngAnswers.setText(inCorrect);



        Log.e("Reddy","Num"+examRnm);


        ViewMethod(examRnm);

        // ************ Graph part ***************************** //

        pieChart = findViewById(R.id.pieChart);

        ArrayList<PieEntry> pieEntries = new ArrayList<>();
        pieEntries.add(new PieEntry(4, "Attempted"));
        pieEntries.add(new PieEntry(4, "Not Attempted"));
        pieEntries.add(new PieEntry(1, "Correct"));
        pieEntries.add(new PieEntry(0, "Incorrect"));

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


        // Get current date and time
        String currentDateTime = getCurrentDateTime();

        // Display the date and time in the TextView
        dateTime.setText(currentDateTime);

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

        Call<ViewTopicResultResponse> call=apiClient.viewResult(idPart);
        call.enqueue(new Callback<ViewTopicResultResponse>() {

            @Override
            public void onResponse(Call<ViewTopicResultResponse> call, Response<ViewTopicResultResponse> response) {
                if (response.isSuccessful()) {
                    ViewTopicResultResponse result = response.body();
                    Log.d("Response", "Anji" + result);

                    if (result != null) {
                        ViewTopicResultResponse.Result viewTopicResult = result.getResult();
                        firstName = viewTopicResult.getFirstName();
                        startDate = viewTopicResult.getStartedOn();

                        // Set Name and Start Date if required
//            txtName.setText(firstName);
//            txtStartDate.setText(startDate);

                        String questionsListJsonString = viewTopicResult.getQuestionsList();
                        if (questionsListJsonString != null) {
                            try {
                                JSONArray jsonArray = new JSONArray(questionsListJsonString);

                                // Initialize counters
                                int totalQuestions = jsonArray.length();
                                int attempted = 0;
                                int correct = 0;
                                int incorrect = 0;

                                LayoutInflater inflater = LayoutInflater.from(ViewResultDetailsActivity.this);

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject questionObject = jsonArray.getJSONObject(i);

                                    // Extract fields
                                    String questionHtml = questionObject.getString("question");
                                    String answer = questionObject.getString("answer");
                                    String given = questionObject.getString("given");
                                    int isCorrect = questionObject.getInt("is_currect");
                                    String timeTaken = questionObject.getString("time_taken");
                                    int status = questionObject.getInt("status");

                                    // Update counts
                                    if (status == 1) {
                                        attempted++;
                                    }
                                    if (isCorrect == 1) {
                                        correct++;
                                    } else {
                                        incorrect++;
                                    }

                                    // Convert HTML to Spanned text for display
                                    Spanned questionText = HtmlCompat.fromHtml(questionHtml, HtmlCompat.FROM_HTML_MODE_LEGACY);
                                    Spanned answerText = HtmlCompat.fromHtml(answer, HtmlCompat.FROM_HTML_MODE_LEGACY);
                                    Spanned givenText = HtmlCompat.fromHtml(given, HtmlCompat.FROM_HTML_MODE_LEGACY);
                                    Spanned timeText = HtmlCompat.fromHtml(timeTaken, HtmlCompat.FROM_HTML_MODE_LEGACY);

                                    // Create table row
                                    TableRow row = new TableRow(getApplicationContext());

                                    // Create and configure TextViews
                                    TextView questionView = new TextView(getApplicationContext());
                                    questionView.setText(questionText);
                                    questionView.setPadding(14, 14, 14, 14);
                                    questionView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
                                    questionView.setTextColor(Color.BLACK);
                                    questionView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1));
                                    questionView.setGravity(Gravity.CENTER);



                                    TextView answersView = new TextView(getApplicationContext());
                                    answersView.setText(answerText);
                                    answersView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
                                    answersView.setPadding(14, 14, 14, 14);
                                    answersView.setTextColor(Color.BLACK);
                                    answersView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1));
                                    answersView.setGravity(Gravity.CENTER);


                                    TextView givenView = new TextView(getApplicationContext());
                                    givenView.setText(givenText);
                                    givenView.setPadding(14, 14, 14, 14);
                                    givenView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
                                    givenView.setTextColor(Color.BLACK);
                                    givenView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1));
                                    givenView.setGravity(Gravity.CENTER);

                                    TextView timeView = new TextView(getApplicationContext());
                                    timeView.setText(timeText);
                                    timeView.setPadding(14, 14, 14, 14);
                                    timeView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
                                    timeView.setTextColor(Color.BLACK);
                                    timeView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1));
                                    timeView.setGravity(Gravity.CENTER);

                                    // Add views to row
                                    row.addView(questionView);
                                    row.addView(answersView);
                                    row.addView(givenView);
                                    row.addView(timeView);

                                    // Add row to table
                                    tabLayout.addView(row);

                                    // Add separator between rows
                                    if (i < jsonArray.length() - 1) {
                                        View separator = inflater.inflate(R.layout.separator_row, tabLayout, false);
                                        tabLayout.addView(separator);
                                    }
                                }

                                // Set total values to respective TextViews
                                txtTotalQuestions.setText(String.valueOf(totalQuestions));
                                txtAttemtedQueston.setText(String.valueOf(attempted));
                                txtCorrectAnswer.setText(String.valueOf(correct));
                                txtworngAnswer.setText(String.valueOf(incorrect));

                                txtTotalQuestion.setText(String.valueOf(totalQuestions));
                                txtAttemtedQuestons.setText(String.valueOf(attempted));
                                txtCorrectAnswers.setText(String.valueOf(correct));
                                txtworngAnswers.setText(String.valueOf(incorrect));


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } else {
                    Log.e("Response", "Request failed");
                }
            }

            @Override
            public void onFailure(Call<ViewTopicResultResponse> call, Throwable t) {
                Log.e("Response", "Request failed: " + t.getMessage());
            }




        });



    }

    private String getCurrentDateTime() {
        // Format for date and time
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy | hh:mm a", Locale.getDefault());
        // Get current date and time
        return sdf.format(new Date());
    }
}