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
import com.dst.abacustrainner.Model.AllocatedViewSubTopicResultResponse;
import com.dst.abacustrainner.R;
import com.dst.abacustrainner.Services.ApiClient;
import com.dst.abacustrainner.User.HomeActivity;
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

public class AllocatedViewSubAssignmentResultDetailsActivity extends AppCompatActivity {

    String examRnm="",topicName="",firstName="",startDate="",AttentQuestions="",Attamted="",Correct="",inCorrect="";
    TableLayout tabLayout;
    TextView txtName,txtStartDate,txtTopicName;
    TextView dateTime,txtTotalQuestion,txtAttemtedQuestons,txtCorrectAnswers,txtworngAnswers,txtNotAttemted;
    private PieChart pieChart;
    ScrollView scrollView;
    LinearLayout layoutFirst,layoutSecond;
    int totalQuestions ;
    int attempted ;
    int correct ;
    int incorrect,notAttempted;
    LinearLayout layoutSubmit;



    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_allocated_view_sub_assignment_result_details);


        tabLayout = findViewById(R.id.tablelayout);
//        txtName=findViewById(R.id.txt_stu_name);
//        txtStartDate=findViewById(R.id.txt_date_start);
        txtTopicName=findViewById(R.id.txt_topic_name);
        txtTotalQuestion=findViewById(R.id.txt_question);
        txtAttemtedQuestons=findViewById(R.id.txt_attemted_questions);
        txtCorrectAnswers=findViewById(R.id.txt_correct_answers);
        txtworngAnswers=findViewById(R.id.txt_wrong_answers);
        txtNotAttemted = findViewById(R.id.txt_notattemted_questions);
        dateTime = findViewById(R.id.txtDate);

        scrollView= findViewById(R.id.scroll_view);
        layoutFirst = findViewById(R.id.layout_first);
        layoutSecond = findViewById(R.id.layout_second);
        layoutSubmit = findViewById(R.id.but_submit_result_first);

        layoutSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AllocatedViewSubAssignmentResultDetailsActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });


        Bundle bundle=getIntent().getExtras();
        examRnm=bundle.getString("examRnm");
        topicName=bundle.getString("topicName");

        txtTopicName.setText(topicName);


        txtTotalQuestion.setText(AttentQuestions);
        txtAttemtedQuestons.setText(Attamted);
        txtCorrectAnswers.setText(Correct);
        txtworngAnswers.setText(inCorrect);
        pieChart = findViewById(R.id.pieChart);

     /*   int notAttempted = totalQuestions - attempted;
        updatePieChart(attempted, notAttempted, correct, incorrect);*/


        Log.e("Reddy","Num"+examRnm);

        ViewMethod(examRnm);

        // Get current date and time
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

        Call<AllocatedViewSubTopicResultResponse> call=apiClient.getAllocatedAssignmentviewResult(idPart);
        call.enqueue(new Callback<AllocatedViewSubTopicResultResponse>() {

            @Override
            public void onResponse(Call<AllocatedViewSubTopicResultResponse> call, Response<AllocatedViewSubTopicResultResponse> response) {
                if (response.isSuccessful() && response.body() != null) {

                    AllocatedViewSubTopicResultResponse.Result result = response.body().getResult();

                    firstName = result.getFirstName();
                    startDate = result.getStartedOn();

                    List<AllocatedViewSubTopicResultResponse.Question> questionsList = result.getQuestionsList();

                    totalQuestions = questionsList.size();
                    attempted = 0;
                    correct = 0;
                    incorrect = 0;
                    notAttempted = 0;

                    LayoutInflater inflater = LayoutInflater.from(AllocatedViewSubAssignmentResultDetailsActivity.this);

                    for (int i = 0; i < questionsList.size(); i++) {

                        AllocatedViewSubTopicResultResponse.Question questionObj = questionsList.get(i);

                        String questionHtml = questionObj.getQuestion();
                        String answer = questionObj.getAnswer();
                        String given = questionObj.getGiven();
                        int isCorrect = questionObj.getIs_currect();
                        int status = questionObj.getStatus();
                        String timeTaken = String.valueOf(questionObj.getTime_taken());

                        // counts
                        if (status == 1) {
                            attempted++;
                            if (isCorrect == 1) {
                                correct++;
                            } else {
                                incorrect++;
                            }
                        }



                        notAttempted = totalQuestions - attempted;

                        // update chart
                        updatePieChart(attempted, notAttempted, correct, incorrect);

                        TableRow row = new TableRow(AllocatedViewSubAssignmentResultDetailsActivity.this);

                        LinearLayout questionLayout = new LinearLayout(AllocatedViewSubAssignmentResultDetailsActivity.this);
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

                            ImageView imageView = new ImageView(AllocatedViewSubAssignmentResultDetailsActivity.this);
                            imageView.setAdjustViewBounds(true);
                            imageView.setMaxHeight(300);

                            Glide.with(AllocatedViewSubAssignmentResultDetailsActivity.this)
                                    .load(imageUrl)
                                    .into(imageView);

                            questionLayout.addView(imageView);

                        } else {

                            // ---------- TEXT QUESTION ----------
                            TextView questionTextView = new TextView(AllocatedViewSubAssignmentResultDetailsActivity.this);

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
                            questionTextView.setTextColor(Color.BLACK);
                            questionLayout.addView(questionTextView);
                        }

                        TextView answerView = new TextView(AllocatedViewSubAssignmentResultDetailsActivity.this);
                        answerView.setText(answer);
                        answerView.setGravity(Gravity.CENTER);
                        answerView.setPadding(14,14,14,14);
                        answerView.setLayoutParams(new TableRow.LayoutParams(
                                0, TableRow.LayoutParams.WRAP_CONTENT, 1));

                        TextView givenView = new TextView(AllocatedViewSubAssignmentResultDetailsActivity.this);
                        givenView.setText(given);
                        givenView.setGravity(Gravity.CENTER);
                        givenView.setPadding(14,14,14,14);
                        givenView.setLayoutParams(new TableRow.LayoutParams(
                                0, TableRow.LayoutParams.WRAP_CONTENT, 1));

                        if (given.isEmpty()) {
                            givenView.setBackgroundColor(Color.WHITE); // Set background color to white
                        } else if (given.equals(answer)) {
                            givenView.setBackgroundColor(Color.parseColor("#008000"));
                            //correctCount++;// Set background color to green
                        } else {
                            givenView.setBackgroundColor(Color.RED);
                            // wrongCount++;// Set background color to red

                        }

                        TextView timeView = new TextView(AllocatedViewSubAssignmentResultDetailsActivity.this);
                        timeView.setText(timeTaken);
                        timeView.setGravity(Gravity.CENTER);
                        timeView.setPadding(14,14,14,14);
                        timeView.setLayoutParams(new TableRow.LayoutParams(
                                0, TableRow.LayoutParams.WRAP_CONTENT, 1));

                        row.addView(questionLayout);
                        row.addView(answerView);
                        row.addView(givenView);
                        row.addView(timeView);

                        tabLayout.addView(row);

                        if (i < questionsList.size() - 1) {
                            View separator = inflater.inflate(R.layout.separator_row, tabLayout, false);
                            tabLayout.addView(separator);
                        }
                    }


                    txtTotalQuestion.setText(String.valueOf(totalQuestions));
                    txtAttemtedQuestons.setText(String.valueOf(attempted));
                    txtCorrectAnswers.setText(String.valueOf(correct));
                    txtworngAnswers.setText(String.valueOf(incorrect));
                    txtNotAttemted.setText(String.valueOf(notAttempted));

                } else {
                    Log.e("API", "Response not successful");
                }
            }



            @Override
            public void onFailure(Call<AllocatedViewSubTopicResultResponse> call, Throwable t) {
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