package com.dst.abacustrainner.Activity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.SystemClock;
import android.text.Spanned;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.text.HtmlCompat;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.dst.abacustrainner.Model.SendData;
import com.dst.abacustrainner.Model.SubmitDataResponse;
import com.dst.abacustrainner.Model.TopicExamResponse;
import com.dst.abacustrainner.R;
import com.dst.abacustrainner.Services.ApiClient;
import com.dst.abacustrainner.database.ParcelableLong;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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

public class TopicPracticeActivity extends AppCompatActivity {

    LinearLayout butPreviousQuestion, butSave, butSubmit,btnBack;
    TextView txtTimer, questionTextView, txtTopicName,txtdisplayquestion,txtTotalTimer;
    private EditText answerEditText;
    private int currentQuestionIndex = 0;

    private CountDownTimer countDownTimer;
    private long currentTime = 0;
    private int currentStep=0;
    private long interval = 1000;

    private boolean timerRunning = false;
    //ImageView imageLeft, imageRight;

    GridLayout gridLayout;

    ArrayList<Boolean> isQuestionAnswered;
    ArrayList<String> enteredAnswers;
    List<Button> questionButtons = new ArrayList<>();

    String studentid = "";
    String topicid = "";
    String topicName = "";

    String[] questionsArray = new String[]{""};
    String[] answerArray = new String[]{""};

    long seconds;
    List<SendData> listData ;

    String answer;
    String answerText = "";
    String isCorrected;

    String status;

    String examNum = "";
    String studentName="";

    String examRnm = "";
    String questionList = "";
    JSONArray jsonArray = new JSONArray();
    private List<Long> questionTimes ;

    private long currentTimeOnSaveAndNext = 0;
    private long currenttimeonprvious = 0;
    String originalAnswer;
    HorizontalScrollView scrollView;

    private List<Boolean> isQuestionAttempted = new ArrayList<>();

    private int correctAnswers = 0;
    private int wrongAnswers = 0;

    private int attemptedQuestions = 0;
    private int notAttemptedQuestions = 0;

    private String totalTime = "";

    String startedDate;

    private boolean shouldStartTimer = true;

    private long remainingTime = 0;
    private List<CountDownTimer> questionTimers ;
    private Handler handler = new Handler();
    private static final int MAX_QUESTIONS = 20;
    LinearLayout leftIcon,rightIcon;
    ImageView questionImageView;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic_practice);
        butPreviousQuestion = findViewById(R.id.prv_qus);
        questionTextView = findViewById(R.id.questionTextView);
        txtdisplayquestion =findViewById(R.id.displaytextvie);
        txtTotalTimer= findViewById(R.id.total_timer_display_id);
        answerEditText = findViewById(R.id.answerEditText);
        txtTimer = findViewById(R.id.timerTextView);
        butSave = findViewById(R.id.btnNext);
        butSubmit = findViewById(R.id.but_submit);
        //imageLeft = findViewById(R.id.leftArrow);
        // imageRight = findViewById(R.id.rightArrow);
        gridLayout = findViewById(R.id.gridLayoutButtons);
        txtTopicName = findViewById(R.id.topic_name);
        leftIcon =findViewById(R.id.left_icon_click);
        rightIcon =findViewById(R.id.right_icon_click);
        btnBack=findViewById(R.id.btn_back_level_select);
        scrollView = findViewById(R.id.horizontalScrollView);
        questionImageView = findViewById(R.id.questionImageView);


        Bundle bundle = getIntent().getExtras();

        studentid = bundle.getString("studentId");
        topicid = bundle.getString("topicId");
        topicName = bundle.getString("topicName");
        studentName =bundle.getString("firstName");
        txtTopicName.setText(topicName);

        displayQuestion(currentQuestionIndex);
        questionTimers = new ArrayList<>();
        for (int i = 0; i < MAX_QUESTIONS; i++) {
            questionTimers.add(createCountDownTimer(i));
        }
        isQuestionAnswered = new ArrayList<>(20);
        enteredAnswers = new ArrayList<>(20);
        //questionButtons = new ArrayList<>();
        questionTimes = new ArrayList<>(20);
        listData = new ArrayList<>();

        Log.e("Anji","Data"+listData);
        Log.e("Anji","isQuestionAnswered"+isQuestionAnswered);
        Log.e("Anji","Answer"+enteredAnswers);
        butSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveTimerState();
                displayQuestion(currentQuestionIndex);
                answerEditText.getText().clear();
                restoreTimerState();
                showCompletionDialog();

            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showExitConfirmationDialog();
            }
        });

        final long[] totalElapsedTime = {1000};
        final long interval = 1000; // Update interval in milliseconds

// Used for formatting digits to be in 2 digits only
        final NumberFormat f = new DecimalFormat("00");

// Create a handler to manage the count-up process

        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                long hour = (totalElapsedTime[0] / 3600000) % 24;
                long min = (totalElapsedTime[0] / 60000) % 60;
                long sec = (totalElapsedTime[0] / 1000) % 60;

                // Update the timer text
                txtTotalTimer.setText(f.format(hour) + ":" + f.format(min) + ":" + f.format(sec));

                String formattedTime = f.format(hour) + ":" + f.format(min) + ":" + f.format(sec);

                Log.d("Reddy","Time"+formattedTime);

                totalTime = formattedTime;


                // Increment the total elapsed time
                totalElapsedTime[0] += interval;

                // Schedule the next update
                handler.postDelayed(this, interval);
            }
        };

// Start the timer
        handler.post(runnable);


        butSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int currentX = scrollView.getScrollX();
                int moveX = currentX + 100;  // Move 100 pixels to the left
                if (moveX < 0) moveX = 0; // Don't scroll beyond the leftmost position
                scrollView.smoothScrollTo(moveX, 0);

                if (currentQuestionIndex >= 0 && currentQuestionIndex < answerArray.length) {
                    answer = answerEditText.getText().toString();
                    enteredAnswers.set(currentQuestionIndex, answer);

                    String originalAnswer = answerArray[currentQuestionIndex];

                    Log.e("Timer", "Saved time for question " + currentQuestionIndex + ": " + currentTime);
                    Log.e("Anji", "Entered Answer: " + answer);
                    Log.e("Anji", "Original Answer: " + originalAnswer);

                    // Your existing logic...
                    originalAnswer = answerArray[currentQuestionIndex];
                    String[] questionLines = originalAnswer.split("\n");

                    // Concatenate all lines of the question
                    for (String line : questionLines) {
                        Spanned lineText = HtmlCompat.fromHtml(line, HtmlCompat.FROM_HTML_MODE_LEGACY);
                        answerText += lineText + "\n";
                        Log.e("Anji", "OrAns" + lineText);
                    }


                    if (answer.equals(originalAnswer)) {
                        isCorrected = "1";
                       status ="1";
                    } else {
                        isCorrected = "0";
                        status = "0";
                    }
                    Log.e("Anji", "Is Corrected: " + isCorrected);

                    Log.e("Timer", "Saved time for question " + currentQuestionIndex + ": " + currentTime);
                    saveAnswerAndMoveToNextQuestion();


                } else {
                    // Handle the case where currentQuestionIndex is out of bounds
                    Log.e("Anji", "Invalid currentQuestionIndex: " + currentQuestionIndex);
                }
            }
        });

        butPreviousQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopTimer();

                int currentX = scrollView.getScrollX();
                int moveX = currentX - 100;  // Move 100 pixels to the left
                if (moveX < 0) moveX = 0; // Don't scroll beyond the leftmost position
                scrollView.smoothScrollTo(moveX, 0);

                // Reset the timer to the saved time
                saveTimerState();


                // Start the timer for the current question

                // Navigate to the previous question
                navigateToPreviousQuestion();
                restoreTimerState(); // Restore timer state for the previous question
                startTimer();
            }

        });


        leftIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Ensure horizontalScrollView is initialized
                HorizontalScrollView horizontalScrollView = findViewById(R.id.horizontalScrollView);

                if (horizontalScrollView != null) {
                    // Get the current X position
                    int currentScrollX = horizontalScrollView.getScrollX();

                    // Convert 20dp to pixels
                    int dpToPx = (int) TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP,
                            40,
                            view.getContext().getResources().getDisplayMetrics()
                    );

                    // Smoothly scroll to the new position
                    horizontalScrollView.smoothScrollTo(currentScrollX - dpToPx, horizontalScrollView.getScrollY());
                }
            }
        });

        rightIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Ensure horizontalScrollView is initialized
                HorizontalScrollView horizontalScrollView = findViewById(R.id.horizontalScrollView);

                if (horizontalScrollView != null) {
                    // Get the current X position
                    int currentScrollX = horizontalScrollView.getScrollX();

                    // Convert 20dp to pixels
                    int dpToPx = (int) TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP,
                            40,
                            view.getContext().getResources().getDisplayMetrics()
                    );

                    // Smoothly scroll to the new position
                    horizontalScrollView.smoothScrollTo(currentScrollX + dpToPx, horizontalScrollView.getScrollY());
                }
            }
        });


        /*imageLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageLeft.setVisibility(View.GONE);
                imageRight.setVisibility(View.VISIBLE);
                gridLayout.setVisibility(View.VISIBLE);

            }
        });

        imageRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageLeft.setVisibility(View.VISIBLE);
                imageRight.setVisibility(View.GONE);
                gridLayout.setVisibility(View.GONE);

            }
        });*/

        startTimer();
        VerifyMethod(studentid, topicid);

    }
    private void saveAnswerAndMoveToNextQuestion() {
        stopTimer();
        saveTimerState();

        String answer = answerEditText.getText().toString();

        originalAnswer = answerArray[currentQuestionIndex];
        if (!answer.isEmpty()) {
            questionTimes.set(currentQuestionIndex,currentTime);
            listData.add(new SendData(questionTextView.getText().toString(), answer, originalAnswer, isCorrected, status,currentTime / 1000));
        }

        Log.e("Anji","Data"+listData);

//        if (isQuestionAnswered != null && !isQuestionAnswered.isEmpty()) {
//            // Display the next question
//            String enteredAnswer = answerEditText.getText().toString();
//            enteredAnswers.add(enteredAnswer);
//
//            boolean attempted = !enteredAnswer.isEmpty();
//            isQuestionAttempted.add(attempted);
//            int previousButtonIndex = currentQuestionIndex - 1 ; // Previous button index
//            int currentButtonIndex = currentQuestionIndex * 2;       // Current button index
//
//            // Reset previous question's button color
//            if (previousButtonIndex >= 0 && previousButtonIndex < gridLayout.getChildCount()) {
//                View previousButtonView = gridLayout.getChildAt(previousButtonIndex);
//                if (previousButtonView instanceof Button) {
//                    Button previousButton = (Button) previousButtonView;
//                    previousButton.setBackgroundColor(getResources().getColor(R.color.answeredButtonColor)); // Answered color
//                }
//            }
//
//            if (!enteredAnswer.isEmpty()) {
//                int buttonIndex = currentQuestionIndex*2; // Step buttons are at even indices
//                if (buttonIndex >= 0 && buttonIndex < gridLayout.getChildCount()) {
//                    View buttonView = gridLayout.getChildAt(buttonIndex);
//                    if (buttonView instanceof Button) {
//                        Button stepButton = (Button) buttonView;
//                        stepButton.setBackgroundColor(getResources().getColor(R.color.answeredButtonColor));
//                        isQuestionAnswered.set(currentQuestionIndex, true);
//                    }
//                }
//            }
//        }


        if (currentQuestionIndex >= 0 && currentQuestionIndex < questionsArray.length) {
            String enteredAnswer = answerEditText.getText().toString();
            enteredAnswers.add(enteredAnswer);

            Log.e("DebugTag", "Index: " + currentQuestionIndex);
            Log.e("DebugTag", "Entered Answer: " + enteredAnswer);

            boolean attempted = !enteredAnswer.isEmpty();
            isQuestionAttempted.add(attempted);

//            if (originalAnswers != null && currentQuestionIndex < originalAnswers.size()) {
//                boolean correctAnswer = enteredAnswer.equals(originalAnswers.get(currentQuestionIndex));
//                isQuestionCorrect.add(correctAnswer);
//            } else {
//                isQuestionCorrect.add(false); // Default to false
//            }

            int previousButtonIndex = (currentQuestionIndex - 1) * 2; // Previous button index
            int currentButtonIndex = currentQuestionIndex * 2;       // Current button index

            // Reset previous question's button color
            if (previousButtonIndex >= 0 && previousButtonIndex < gridLayout.getChildCount()) {
                View previousButtonView = gridLayout.getChildAt(previousButtonIndex);
                if (previousButtonView instanceof Button) {
                    Button previousButton = (Button) previousButtonView;
                    previousButton.setBackgroundColor(getResources().getColor(R.color.answeredButtonColor)); // Answered color
                }
            }

            if (!enteredAnswer.isEmpty()) {
                int buttonIndex = currentQuestionIndex * 2; // Step buttons are at even indices
                if (buttonIndex >= 0 && buttonIndex < gridLayout.getChildCount()) {
                    View buttonView = gridLayout.getChildAt(buttonIndex);
                    if (buttonView instanceof Button) {
                        Button stepButton = (Button) buttonView;
                        stepButton.setBackgroundColor(getResources().getColor(R.color.answeredButtonColor));

                        isQuestionAnswered.set(currentQuestionIndex, true);
                    }
                }
            }

        }


        if (isCorrected.equals("1")) {
            correctAnswers++;
        } else {
            wrongAnswers++;
        }

        if (isQuestionAnswered.get(currentQuestionIndex)) {
            attemptedQuestions++;
        } else {
            notAttemptedQuestions++;
        }

        if (questionsArray != null && questionsArray.length > 0) {
            enteredAnswers.set(currentQuestionIndex, answer);


            if (!answer.isEmpty()) {
                isQuestionAnswered.set(currentQuestionIndex, true);

            }
            if (currentQuestionIndex < questionsArray.length-1) {
                currentQuestionIndex++; // Increment index first
                currentStep = currentQuestionIndex; // Sync the step index
                displayQuestion(currentQuestionIndex); // Display next question
                answerEditText.setText(""); // Clear the answer field for the next question
                currentTime = questionTimes.get(currentQuestionIndex); // Restore timer state for the next question
                restoreTimerState();
                startTimer();
            }else {
                showCompletionDialog();
            }
        } else {
        }
  }
  private void navigateToPreviousQuestion() {
        if (currentQuestionIndex > 0) {

            currentQuestionIndex--;
            // Display the previous question here
            displayQuestion(currentQuestionIndex);
            String storedAnswer = enteredAnswers.get(currentQuestionIndex);
            answerEditText.setText(storedAnswer);

        }
    }
    private void displayQuestion(int currentQuestionIndex) {
        if (questionsArray != null && questionsArray.length > currentQuestionIndex) {
            String questionHtml = questionsArray[currentQuestionIndex];

            // Log question length and raw content
            if (questionHtml == null || questionHtml.trim().isEmpty()) {
                Log.d("QuestionDebug", "Empty or null question at index: " + currentQuestionIndex);
            } else {
                Log.d("QuestionDebug", "Raw HTML: " + questionHtml);
                Log.d("QuestionDebug", "Question Length: " + questionHtml.length());
            }

            // Extract <img src="...">
            Pattern pattern = Pattern.compile("<img[^>]+src=\\\\*\"([^\"]+)\\\\*\"");
            Matcher matcher = pattern.matcher(questionHtml);

            String imageUrl = null;
            if (matcher.find()) {
                String relativePath = matcher.group(1).replace("\\", "");
                imageUrl = "https://www.abacustrainer.com/" + relativePath.replace("../../../", "");
                Log.d("QuestionDebug", "Image URL: " + imageUrl);
            }

            // Remove <img> tag and backslashes from HTML to get plain text
            String questionTextOnly = questionHtml.replaceAll("<img[^>]+>", "").replaceAll("\\\\", "");
            Log.d("QuestionDebug", "Cleaned Text: " + questionTextOnly);

            // Set question number
            txtdisplayquestion.setText("Question " + (currentQuestionIndex + 1) + ":");

            if (imageUrl != null && !imageUrl.isEmpty()) {
                // ✅ Show only image
                questionImageView.setVisibility(View.VISIBLE);
                questionTextView.setVisibility(View.GONE);

                Glide.with(this)
                        .load(imageUrl)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .dontAnimate()
                        .into(questionImageView);

            } else {
                // ✅ Show only text
                questionImageView.setVisibility(View.GONE);
                questionTextView.setVisibility(View.VISIBLE);

                questionTextView.setText("   " + questionTextOnly.replace("\n", "\n   "));

                // Set margin
                ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) questionTextView.getLayoutParams();
                layoutParams.leftMargin = (int) getResources().getDimension(R.dimen.question_margin_left);
                questionTextView.setLayoutParams(layoutParams);
            }

            generateButtons();
        } else {
            if (questionsArray == null) {
                Log.d("QuestionDebug", "questionsArray is null.");
                Toast.makeText(this, "Questions not loaded.", Toast.LENGTH_SHORT).show();
            } else {
                Log.d("QuestionDebug", "Index out of bounds: " + currentQuestionIndex);
                showCompletionDialog();
            }
        }
    }



    private void generateButtons() {
        gridLayout.removeAllViews();

        int totalSteps = questionsArray.length; // Total steps (buttons)
        int totalColumns = totalSteps * 2 - 1; // Steps + Connectors

        for (int i = 0; i < totalColumns; i++) {
            if (i % 2 == 0) {
                // Create a circular step button
                Button stepButton = new Button(this);
                stepButton.setText(String.valueOf((i / 2) + 1)); // Step number
                stepButton.setGravity(Gravity.CENTER);
                final int[] stepIndex = {i / 2}; // Determine the step index
                if (isQuestionAnswered != null && isQuestionAnswered.size() > i && isQuestionAnswered.get(i/2)) {
                   stepButton.setTextColor(Color.WHITE);
            } else {
                   stepButton.setTextColor(Color.BLACK);
            }


                stepButton.setTextSize(14);
                stepButton.setTypeface(null, Typeface.BOLD);

                // Set background color based on status

                if (isQuestionAnswered != null && isQuestionAnswered.size() > i && isQuestionAnswered.get(i/2)) {
                    stepButton.setBackground(getDrawable(R.drawable.circle_green)); // Answered
                } else if (stepIndex[0] == currentStep) {
                    stepButton.setBackground(getDrawable(R.drawable.circle_orange)); // Current step
                } else {
                    stepButton.setBackground(getDrawable(R.drawable.circle_gray)); // Unanswered
                }

                // Set layout parameters for the step button
                GridLayout.LayoutParams stepParams = new GridLayout.LayoutParams();
                stepParams.width = dpToPx(40); // Circular size
                stepParams.height = dpToPx(40);
                stepParams.setMargins(dpToPx(0), dpToPx(16), dpToPx(0), dpToPx(16));
                if (i == totalColumns - 1) {
                    stepParams.setMargins(dpToPx(0), dpToPx(16), dpToPx(25), dpToPx(16));
                }
                stepButton.setLayoutParams(stepParams);

                // Add click listener for the step button
                stepButton.setTag(stepIndex[0]);

                stepButton.setOnClickListener(view -> {
                    int clickedStep = (int) view.getTag();

                    scrollToCenter(stepButton);
                    onButtonClicked(clickedStep);

                });

                // Add the step button to the GridLayout
                gridLayout.addView(stepButton);
            } else {
                // Create a connector line
                View connector = new View(this);
                connector.setBackgroundColor(Color.GRAY); // Connector color
                // Set layout parameters for the connector
                GridLayout.LayoutParams connectorParams = new GridLayout.LayoutParams();
                connectorParams.width = dpToPx(8); // Connector width
                connectorParams.height = dpToPx(4); // Connector height
                connectorParams.setMargins(0, dpToPx(35), 0, dpToPx(0)); // Vertical alignment
                connector.setLayoutParams(connectorParams);

                // Add the connector to the GridLayout
                gridLayout.addView(connector);
            }
        }
    }

    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }
    int temp=0;
 private void onButtonClicked(int tag) {
        // Stop the current timer before changing the question
        if(temp!=tag) {
            temp = tag;
            stopTimer();
            saveTimerState();

            // Set the current question index to the clicked button's tag (index of the clicked button)
            currentQuestionIndex = tag;
            currentStep = currentQuestionIndex;
            Log.e("Reddy", "Button Clicked - Index: " + tag);
            // Save the answer and time for the current question (before navigating)        // Update the answer and time for the current question if needed

            // Update button colors based on answered state
            for (int i = 0; i < gridLayout.getChildCount(); i++) {
                View child = gridLayout.getChildAt(i);
                if (child instanceof Button) { // Check if the child is a button
                    int stepIndex = (int) child.getTag(); // Get the tag (index) of the button

                    // Update the background color based on the step index
                    if (stepIndex == currentQuestionIndex) {
                        child.setBackground(getDrawable(R.drawable.circle_orange)); // Current step
                    } else if (isQuestionAnswered.size() > stepIndex && isQuestionAnswered.get(stepIndex)) {
                        child.setBackground(getDrawable(R.drawable.circle_green)); // Answered
                    } else {
                        child.setBackground(getDrawable(R.drawable.circle_gray)); // Unanswered
                    }
                }
            }

            // Display the selected question and restore its answer
           // saveTimerState();
            currentQuestionIndex = tag;
            Log.e("Reddy","CurrentQuestion"+currentQuestionIndex);
            displayQuestion(currentQuestionIndex);
            String storedAnswer = enteredAnswers.get(currentQuestionIndex);
            answerEditText.setText(storedAnswer);
            currentTime = questionTimes.get(currentQuestionIndex);
            restoreTimerState();
            startTimer();

            // Invalidate GridLayout to ensure changes are visible
            gridLayout.invalidate();
        }
    }
   /* private void onButtonClicked(int tag) {
        saveTimerState();
        currentQuestionIndex = tag;
        Log.e("Reddy","CurrentQuestion"+currentQuestionIndex);
        displayQuestion(currentQuestionIndex);
        String storedAnswer = enteredAnswers.get(currentQuestionIndex);
        answerEditText.setText(storedAnswer);
        restoreTimerState();
       // startTimerForQuestion(currentQuestionIndex);
    }
*/
    private void saveTimerState() {
        questionTimes.set(currentQuestionIndex, currentTime);
    }
    private void restoreTimerState() {

        currentTime = questionTimes.get(currentQuestionIndex);
        // Update UI with the restored timer
        txtTimer.setText("TimeSpent: " + currentTime / 1000 + " sec");
    }
    private void startTimerForQuestion(int questionIndex) {
        questionTimers.get(questionIndex).start();
    }

    private void stopTimerForQuestion(int questionIndex) {
        questionTimers.get(questionIndex).cancel();
    }

    private void VerifyMethod(String studentid, String topicid) {
        /*HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
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

        RequestBody idPart = RequestBody.create(MediaType.parse("text/plain"), studentid);
        RequestBody topicIdPart=RequestBody.create(MediaType.parse("text/plain"), topicid);

        Call<TopicExamResponse> call=apiClient.examList(idPart,topicIdPart);
        call.enqueue(new Callback<TopicExamResponse>() {
            @Override
            public void onResponse(Call<TopicExamResponse> call, Response<TopicExamResponse> response) {
                if (response.isSuccessful()){
                    TopicExamResponse examResponse=response.body();
                    if (examResponse!=null){
                        TopicExamResponse.Result examResponseResult=examResponse.getResult();

                        examNum =examResponseResult.getExamRnm();
                        startedDate  = examResponseResult.getStartedOn();
                        String questionsListJsonString =examResponseResult.getQuestionsList();
                        Log.e("Anji","ExamRnm"+examNum);
                        if (questionsListJsonString!=null){
                            try {
                                JSONArray jsonArray=new JSONArray(questionsListJsonString);
                                questionsArray = new String[jsonArray.length()];
                                answerArray=new String[jsonArray.length()];

                                if (questionsArray != null){
                                    int questionCount = jsonArray.length();
                                    questionsArray = new String[questionCount];
                                    enteredAnswers = new ArrayList<>(questionCount);
                                    isQuestionAnswered = new ArrayList<>(questionCount);

                                   // questionsArray = new String[jsonArray.length()];

                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        enteredAnswers.add("");
                                        isQuestionAnswered.add(false);
                                        questionTimes.add(0L);

                                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                                        enteredAnswers.add("");
                                        isQuestionAnswered.add(false);
                                        String questionHtml = jsonObject.getString("question");
                                        String answerHtml=jsonObject.getString("answer");
                                        questionsArray[i] = HtmlCompat.fromHtml(questionHtml, HtmlCompat.FROM_HTML_MODE_LEGACY).toString();
                                        answerArray[i] = HtmlCompat.fromHtml(answerHtml, HtmlCompat.FROM_HTML_MODE_LEGACY).toString();
                                    }

                                    displayQuestion(currentQuestionIndex);
                                } else {

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }else {

                }
            }

            @Override
            public void onFailure(Call<TopicExamResponse> call, Throwable t) {

            }
        });
    }

    private CountDownTimer createCountDownTimer(final int questionIndex) {
        final long smallerInterval = 500;
        return new CountDownTimer(Long.MAX_VALUE, interval) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (timerRunning) {
                    currentTime +=1000; // Increase the time by 1 second
                    long seconds = currentTime / 1000;
                    txtTimer.setText("TimeSpent: " + seconds + " sec");
                }
            }
            @Override
            public void onFinish() {
                // Timer will never finish, as it's set to Long.MAX_VALUE
            }
        };
    }
    private void showCompletionDialog() {

        stopTimer();

        AlertDialog.Builder dialog=new AlertDialog.Builder(TopicPracticeActivity.this);
        dialog.setMessage("Are you sure you want to submit exam. You are not able to modify any thing after submiting.?");
        dialog.setTitle("www.abacustrainer.com");
        dialog.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        showReportACtivity();
                    }
                });
        dialog.setNegativeButton("cancel",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog=dialog.create();
        alertDialog.show();
    }
    private void showReportACtivity() {
        JSONArray jsonArray = new JSONArray();
        try {
            for (int i=0;i<listData.size();i++) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("question",listData.get(i).getQuestion());
                jsonObject.put("given",listData.get(i).getEnterAnswer());
                jsonObject.put("answer",listData.get(i).getCorrectAnswer());
                jsonObject.put("is_currect",listData.get(i).getIsCorrect());
                jsonObject.put("time_taken",listData.get(i).getTimeTaken());
                jsonObject.put("status",listData.get(i).getStatus());

                jsonArray.put(jsonObject);

            }
            Log.e("Reddy", "Formatted JSON Array Contents: " + jsonArray.toString());
            ResultMethod(examNum,jsonArray);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
    private void ResultMethod(String examRnm, JSONArray jsonArray) {
        Log.e("Reddy","id"+examRnm);
        Log.e("Reddy","Array"+jsonArray.toString());

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://www.abacustrainer.com/") // Replace with your API URL
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
        ApiClient apiClient=retrofit.create(ApiClient.class);
        RequestBody examNumPart = RequestBody.create(MediaType.parse("text/plain"), examNum);
        RequestBody questionListPart=RequestBody.create(MediaType.parse("text/plain"), jsonArray.toString());
        Call<SubmitDataResponse> call=apiClient.submitData(examNumPart,questionListPart);
        call.enqueue(new Callback<SubmitDataResponse>() {
            @Override
            public void onResponse(Call<SubmitDataResponse> call, Response<SubmitDataResponse> response) {
                Log.e("Reddy","Response"+response);
                if (response.isSuccessful()) {
                    SubmitDataResponse submitDataResponse = response.body();
                    if (submitDataResponse != null) {
                        Toast.makeText(TopicPracticeActivity.this, "All Questions are Submited", Toast.LENGTH_LONG).show();

                        // If you don't want to start a new activity, remove or modify the following code
                        ArrayList<String> stringIsQuestionAttempted = convertBooleanListToStringList(isQuestionAttempted);
                        Intent intent = new Intent(TopicPracticeActivity.this, PracticeListActivity.class);
                        intent.putExtra("topicName", topicName);
                        intent.putExtra("firstName", studentName);
                        intent.putExtra("startedOn", startedDate);
                        intent.putStringArrayListExtra("answers", new ArrayList<>(Arrays.asList(answerArray)));
                        intent.putStringArrayListExtra("questions", new ArrayList<>(Arrays.asList(questionsArray)));
                        intent.putStringArrayListExtra("enteredAnswers", enteredAnswers);
                        intent.putStringArrayListExtra("isQuestionAttempted", stringIsQuestionAttempted);
                        intent.putExtra("TOTAL_TIME", totalTime);

                        ArrayList<ParcelableLong> parcelableTimes = new ArrayList<>();
                        for (Long time : questionTimes) {
                            parcelableTimes.add(new ParcelableLong(time));
                        }
                        intent.putParcelableArrayListExtra("questionTimes", parcelableTimes);

                        startActivity(intent);
                        finish();


                    }
                }
            }
            @Override
            public void onFailure(Call<SubmitDataResponse> call, Throwable t) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        showExitConfirmationDialog();
    }

    private void showExitConfirmationDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(TopicPracticeActivity.this);
        dialog.setMessage("Do you want to exit the exam? Your progress will be lost.");
        dialog.setTitle("www.abacustrainer.com");
        dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Handle the exit action (e.g., finish the activity)
                finish();
            }
        });
        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = dialog.create();
        alertDialog.show();
    }

    private ArrayList<String> convertBooleanListToStringList(List<Boolean> isQuestionAttempted) {
        ArrayList<String> stringList = new ArrayList<>();
        for (Boolean value : isQuestionAttempted) {
            stringList.add(value ? "1" : "0");
        }
        return stringList;
    }

    private void updateTimerText() {
        if (currentTime <= 0) {
            txtTimer.setText("Timer: 0 seconds");
        } else {
            seconds = currentTime / 1000;
            txtTimer.setText("Timer: " + seconds + " sec");
        }
    }

    private void startTimer() {
        timerRunning = true;
        countDownTimer = createCountDownTimer(currentQuestionIndex);
        countDownTimer.start();

    }
    private void stopTimer() {
        timerRunning = false;
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
    private void scrollToCenter(View view){
        int scrollViewWidth=scrollView.getWidth();
        int buttonWidth=scrollView.getWidth();
        int scrollX =(view.getLeft()+ view.getRight())/2-scrollViewWidth/2;
        scrollView.smoothScrollTo(scrollX,0);
    }

}