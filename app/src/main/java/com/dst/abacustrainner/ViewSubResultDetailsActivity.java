package com.dst.abacustrainner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.text.HtmlCompat;

import android.annotation.SuppressLint;
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
import com.dst.abacustrainner.Activity.AllocatedViewSubResultDetailsActivity;
import com.dst.abacustrainner.Activity.ViewResultDetailsActivity;
import com.dst.abacustrainner.Model.AllocatedViewSubTopicResultResponse;
import com.dst.abacustrainner.Model.ViewSubTopicResultResponse;
import com.dst.abacustrainner.Model.ViewTopicResultResponse;
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

public class ViewSubResultDetailsActivity extends AppCompatActivity {

    String examRnm="",topicName="",firstName="",startDate="",AttentQuestions="",Attamted="",Correct="",inCorrect="";
    TableLayout tabLayout;
    TextView txtName,txtStartDate,txtTopicName;
    TextView txtTotalQuestions,txtAttemtedQueston,txtCorrectAnswer,txtworngAnswer,dateTime,txtTotalQuestion,txtAttemtedQuestons,txtCorrectAnswers,txtworngAnswers;
    private PieChart pieChart;
    ScrollView scrollView;
    LinearLayout layoutFirst,layoutSecond;
    int totalQuestions ;
    int attempted ;
    int correct ;
    int incorrect;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_sub_result_details);

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
        RequestBody idPart = RequestBody.create(MediaType.parse("application/json"), examRnm);

        Call<ViewSubTopicResultResponse> call=apiClient.getviewResult(idPart);
        call.enqueue(new Callback<ViewSubTopicResultResponse>() {

            @Override
            public void onResponse(Call<ViewSubTopicResultResponse> call, Response<ViewSubTopicResultResponse> response) {
                if (response.isSuccessful() && response.body() != null) {

                    ViewSubTopicResultResponse.Result viewTopicResult = response.body().getResult();
                        firstName = viewTopicResult.getFirstName();
                        startDate = viewTopicResult.getStartedOn();

                        // Set Name and Start Date if required
//            txtName.setText(firstName);
//            txtStartDate.setText(startDate);

                        List<ViewSubTopicResultResponse.Question> questionsList = viewTopicResult.getQuestionsList();


                                totalQuestions = questionsList.size();
                                attempted = 0;
                                correct = 0;
                                incorrect = 0;

                                LayoutInflater inflater = LayoutInflater.from(ViewSubResultDetailsActivity.this);

                                for (int i = 0; i < questionsList.size(); i++) {
                                    ViewSubTopicResultResponse.Question question=questionsList.get(i);

                                    // Extract fields
                                    String questionHtml = question.getQuestion();
                                    String answer = question.getAnswer();
                                    String given = question.getGiven();
                                    int isCorrect = question.getIs_currect();
                                    int status = question.getStatus();
                                    String timeTaken = String.valueOf(question.getTime_taken());

                                    // Update counts
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




                                    // Create table row
                                    TableRow row = new TableRow(getApplicationContext());

                                    // Create and configure TextViews
                                    LinearLayout questionLayout = new LinearLayout(ViewSubResultDetailsActivity.this);
                                    questionLayout.setOrientation(LinearLayout.VERTICAL);
                                    questionLayout.setLayoutParams(new TableRow.LayoutParams(
                                            0, TableRow.LayoutParams.WRAP_CONTENT, 1));

                                    questionLayout.setGravity(Gravity.CENTER);

                                    Pattern pattern = Pattern.compile("<img[^>]+src=\"([^\"]+)\"");
                                    Matcher matcher = pattern.matcher(questionHtml);

                                    if (matcher.find()) {

                                        String imageUrl = matcher.group(1);

                                        ImageView imageView = new ImageView(ViewSubResultDetailsActivity.this);
                                        imageView.setAdjustViewBounds(true);
                                        imageView.setMaxHeight(300);

                                        Glide.with(ViewSubResultDetailsActivity.this)
                                                .load(imageUrl)
                                                .into(imageView);

                                        questionLayout.addView(imageView);

                                    } else {

                                        TextView questionTextView = new TextView(ViewSubResultDetailsActivity.this);

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
                                        questionTextView.setPadding(12, 12, 12, 12);

                                        questionLayout.addView(questionTextView);
                                    }




                                    TextView answersView = new TextView(getApplicationContext());
                                    answersView.setText(answer);
                                    answersView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
                                    answersView.setPadding(14, 14, 14, 14);
                                    answersView.setTextColor(Color.BLACK);
                                    answersView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1));
                                    answersView.setGravity(Gravity.CENTER);


                                    TextView givenView = new TextView(getApplicationContext());
                                    givenView.setText(given);
                                    givenView.setPadding(14, 14, 14, 14);
                                    givenView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
                                    givenView.setTextColor(Color.BLACK);
                                    givenView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1));
                                    givenView.setGravity(Gravity.CENTER);

                                    TextView timeView = new TextView(getApplicationContext());
                                    timeView.setText(timeTaken);
                                    timeView.setPadding(14, 14, 14, 14);
                                    timeView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
                                    timeView.setTextColor(Color.BLACK);
                                    timeView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1));
                                    timeView.setGravity(Gravity.CENTER);

                                    // Add views to row
                                    row.addView(questionLayout);
                                    row.addView(answersView);
                                    row.addView(givenView);
                                    row.addView(timeView);

                                    // Add row to table
                                    tabLayout.addView(row);

                                    // Add separator between rows
                                    if (i < questionsList.size() - 1) {
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


                } else {
                    Log.e("Response", "Request failed");
                }
            }

            @Override
            public void onFailure(Call<ViewSubTopicResultResponse> call, Throwable t) {
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