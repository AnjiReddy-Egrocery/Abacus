package com.dst.abacustrainner.Activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.text.HtmlCompat;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.dst.abacustrainner.Model.CourseTopicExamResponse;
import com.dst.abacustrainner.Model.SendData;
import com.dst.abacustrainner.Model.WorkSheetSubmitDataResponse;
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

public class CourseTopicVisualizationActivity extends AppCompatActivity {

    LinearLayout butPreviousQuestion, butSave, butSubmit,btnBack;
    TextView txtTimer, questionTextView, txtTopicName,txtdisplayquestion,txtTotalTimer;
    private EditText answerEditText;
    private int currentQuestionIndex = 0;

    private CountDownTimer countDownTimer;
    private long currentTime = 0;
    private int currentStep=0;
    private long interval = 1000;

    private boolean timerRunning = false;

    GridLayout gridLayout;

    ArrayList<Boolean> isQuestionAnswered;
    ArrayList<String> enteredAnswers;
    List<Button> questionButtons = new ArrayList<>();


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

    ImageView imageView;

    String studentId,topicId,topicName;
    private static final int REQ_CODE_SPEECH_INPUT = 100;


    private String currentNumber;

    private TextToSpeech textToSpeech;
    private boolean isTtsReady = false;

    private boolean isQuestionActive = false;
    LinearLayout linearRepeat;
    private boolean isAnswerDisplayed = false;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_topic_visualization);

        studentId= getIntent().getStringExtra("StudentId");
        topicId = getIntent().getStringExtra("TopicId");

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
        linearRepeat = findViewById(R.id.layout_repeat);

        butSubmit.setEnabled(false);
        butSubmit.setClickable(false);

        Bundle bundle = getIntent().getExtras();

        studentId = bundle.getString("StudentId");
        topicId = bundle.getString("TopicId");
        topicName = bundle.getString("TopicName");
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

        linearRepeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                repeatCurrentQuestion();
            }
        });

        Log.e("Anji","Data"+listData);
        Log.e("Anji","isQuestionAnswered"+isQuestionAnswered);
        Log.e("Anji","Answer"+enteredAnswers);
        butSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopTimer();
                saveTimerState();

                String answer = answerEditText.getText().toString().trim();
                originalAnswer = answerArray[currentQuestionIndex];

                if (!answer.isEmpty()) {

                    enteredAnswers.set(currentQuestionIndex, answer);
                    questionTimes.set(currentQuestionIndex, currentTime);

                    if (answer.equals(originalAnswer)) {
                        isCorrected = "1";
                        status = "1";
                    } else {
                        isCorrected = "0";
                        status = "0";
                    }

                    listData.add(new SendData(
                            questionTextView.getText().toString(),
                            originalAnswer,
                            answer,
                            isCorrected,
                            currentTime / 1000,
                            status
                    ));

                    isQuestionAnswered.set(currentQuestionIndex, true);
                }

                showCompletionDialog();

            }
        });

        textToSpeech = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                int result = textToSpeech.setLanguage(Locale.US); // or Locale.ENGLISH

                if (result == TextToSpeech.LANG_MISSING_DATA ||
                        result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e("TTS", "Language not supported");
                } else {
                    isTtsReady = true;
                }
            } else {
                Log.e("TTS", "Initialization failed");
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
                if (isQuestionActive) {
                    return;
                }

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



        VerifyMethod(studentId, topicId);

    }

    private void repeatCurrentQuestion() {
        if (isQuestionActive) return; // avoid multiple clicks

        isQuestionActive = true;

        // Hide repeat while replaying
        linearRepeat.setVisibility(View.GONE);

        // Stop previous speech
        if (textToSpeech != null) {
            textToSpeech.stop();
        }

        // Reset UI
        answerEditText.setVisibility(View.GONE);
        butSave.setEnabled(false);
        butSubmit.setEnabled(false);

        // Re-run same question
        String questionHtml = questionsArray[currentQuestionIndex];
        String cleanedHtml = questionHtml.replaceAll("<img[^>]+>", "");

        Spanned spannedText =
                HtmlCompat.fromHtml(cleanedHtml, HtmlCompat.FROM_HTML_MODE_LEGACY);

        String questionTextOnly = spannedText.toString()
                .replace("\u00A0", "")
                .trim();

        List<String> elements =
                Arrays.asList(questionTextOnly.split("\\s+"));

        speakAndDisplayOneByOne(elements);
    }

    private void saveAnswerAndMoveToNextQuestion() {
        stopTimer();
        saveTimerState();

        linearRepeat.setVisibility(View.GONE);



        String answer = answerEditText.getText().toString();

        originalAnswer = answerArray[currentQuestionIndex];
        if (!answer.isEmpty()) {
            questionTimes.set(currentQuestionIndex,currentTime);
            listData.add(new SendData(questionTextView.getText().toString(),originalAnswer, answer,  isCorrected,currentTime / 1000, status));
        }

        Log.e("Anji","Data"+listData);


        if (currentQuestionIndex >= 0 && currentQuestionIndex < questionsArray.length) {
            String enteredAnswer = answerEditText.getText().toString();
            //enteredAnswers.add(enteredAnswer);
            enteredAnswers.set(currentQuestionIndex, enteredAnswer);

            Log.e("DebugTag", "Index: " + currentQuestionIndex);
            Log.e("DebugTag", "Entered Answer: " + enteredAnswer);

            boolean attempted = !enteredAnswer.isEmpty();
            isQuestionAttempted.add(attempted);


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
            }else {
                showCompletionDialog();
            }
        } else {
        }
    }
    private void navigateToPreviousQuestion() {
        linearRepeat.setVisibility(View.GONE);
        if (currentQuestionIndex > 0) {

            currentQuestionIndex--;
            // Display the previous question here
            displayQuestion(currentQuestionIndex);
            String storedAnswer = enteredAnswers.get(currentQuestionIndex);
            answerEditText.setText(storedAnswer);

        }
    }
    private void displayQuestion(int currentQuestionIndex) {

        isQuestionActive = true;

        answerEditText.setVisibility(View.GONE);   // 🔥 Always hide first
        answerEditText.setText("");

        butSave.setEnabled(false);
        butSubmit.setEnabled(false);


        // 🔥 If question already answered
        if (isQuestionAnswered != null &&
                isQuestionAnswered.size() > currentQuestionIndex &&
                isQuestionAnswered.get(currentQuestionIndex)) {

            txtdisplayquestion.setText("Question " + (currentQuestionIndex + 1) + ":");

            questionImageView.setVisibility(View.GONE);
            questionTextView.setVisibility(View.VISIBLE);
            questionTextView.setText("Answer is ?");

            answerEditText.setVisibility(View.VISIBLE);
            answerEditText.setText(enteredAnswers.get(currentQuestionIndex));

            butSave.setEnabled(true);
            butSave.setClickable(true);

            butPreviousQuestion.setEnabled(true);
            butPreviousQuestion.setClickable(true);

            isQuestionActive = false;

            return;   // 🔥 VERY IMPORTANT
        }
        butPreviousQuestion.setEnabled(false);
        butPreviousQuestion.setClickable(false);

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

            // Set question number
            txtdisplayquestion.setText("Question " + (currentQuestionIndex + 1) + ":");

            if (imageUrl != null && !imageUrl.isEmpty()) {
                // ✅ Show only image
                questionImageView.setVisibility(View.VISIBLE);
                questionTextView.setVisibility(View.VISIBLE);

                questionTextView.setMaxLines(5);
                questionTextView.setEllipsize(TextUtils.TruncateAt.END);
                questionTextView.setText("Beads question not available for visualization practice.");

               /* Glide.with(this)
                        .load(imageUrl)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .dontAnimate()
                        .into(questionImageView);*/

               // speakQuestion("Please observe the question image and answer");

// ✅ Start timer after instruction
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();  // Go to previous activity
                    }
                }, 2000);  // 3 seconds delay
            } else {
                // ✅ Show only text
                questionImageView.setVisibility(View.GONE);
                questionTextView.setVisibility(View.VISIBLE);

                if (textToSpeech != null) {
                    textToSpeech.stop();
                }

                // 🔴 Stop previous TTS
                if (textToSpeech != null) {
                    textToSpeech.stop();
                }

                // 🔴 CLEAN question text (VERY IMPORTANT)
                String cleanedHtml = questionHtml.replaceAll("<img[^>]+>", "");

                Spanned spannedText =
                        HtmlCompat.fromHtml(
                                cleanedHtml,
                                HtmlCompat.FROM_HTML_MODE_LEGACY
                        );

                String questionTextOnly = spannedText.toString()
                        .replace("\u00A0", "")
                        .trim();

                // 🔵 Split correctly (space / newline / tabs)
                List<String> elements =
                        Arrays.asList(questionTextOnly.split("\\s+"));

                Log.d("TTS_DEBUG", "Elements: " + elements);

                // 🔊 Speak + display one by one
                speakAndDisplayOneByOne(elements);

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

        Call<CourseTopicExamResponse> call=apiClient.topicExamList(idPart,topicIdPart);
        call.enqueue(new Callback<CourseTopicExamResponse>() {
            @Override
            public void onResponse(Call<CourseTopicExamResponse> call, Response<CourseTopicExamResponse> response) {
                if (response.isSuccessful()) {
                    CourseTopicExamResponse examResponse = response.body();
                    if (examResponse != null) {
                        CourseTopicExamResponse.Result examResponseResult = examResponse.getResult();

                        examNum = examResponseResult.getExamRnm();
                        startedDate = examResponseResult.getStartedOn();
                        List<CourseTopicExamResponse.Question> questionsListJsonString =
                                examResponseResult.getQuestionsList();

                        int questionCount = questionsListJsonString.size();

                        questionsArray = new String[questionCount];
                        answerArray = new String[questionCount];

                        enteredAnswers = new ArrayList<>(questionCount);
                        isQuestionAnswered = new ArrayList<>(questionCount);
                        questionTimes = new ArrayList<>(questionCount);

                        for (int i = 0; i < questionCount; i++) {

                            enteredAnswers.add("");
                            isQuestionAnswered.add(false);
                            questionTimes.add(0L);

                            CourseTopicExamResponse.Question question = questionsListJsonString.get(i);

                            questionsArray[i] = question.getQuestion();

                            answerArray[i] = question.getAnswer();

                        }

                        // ⭐ Display first question after loading
                        displayQuestion(currentQuestionIndex);

                    }
                }
            }


            @Override
            public void onFailure(Call<CourseTopicExamResponse> call, Throwable t) {

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

        AlertDialog.Builder dialog=new AlertDialog.Builder(CourseTopicVisualizationActivity.this);
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
                restoreTimerState();
                startTimer();
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog=dialog.create();
        alertDialog.show();
    }
    private void showReportACtivity() {
       /* JSONArray jsonArray = new JSONArray();
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
        }*/

        JSONArray jsonArray = new JSONArray();
        try {
            for (int i=0;i<questionsArray.length;i++) {
                JSONObject jsonObject = new JSONObject();
                String givenAnswer = enteredAnswers.get(i);
                if(givenAnswer == null){
                    givenAnswer = "";
                }

                givenAnswer = givenAnswer.trim().replace("\n","").replace("\r","");
                String correctAnswer = answerArray[i];

                int isCorrect = givenAnswer.equals(correctAnswer) ? 1 : 0;
                jsonObject.put("question", questionsArray[i]);
                jsonObject.put("given", givenAnswer == null ? "" : givenAnswer);
                jsonObject.put("answer", correctAnswer);
                jsonObject.put("is_currect", isCorrect);
                jsonObject.put("time_taken", questionTimes.get(i) / 1000);
                jsonObject.put("status", givenAnswer.isEmpty() ? 0 : 1);

                jsonArray.put(jsonObject);

            }
            logLargeString("Reddy", jsonArray.toString());
            ResultMethod(examNum,jsonArray);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
    private void logLargeString(String tag, String message) {
        int maxLogSize = 1000; // or 4000
        for (int i = 0; i <= message.length() / maxLogSize; i++) {
            int start = i * maxLogSize;
            int end = Math.min((i+1) * maxLogSize, message.length());
            Log.e(tag, message.substring(start, end));
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
        RequestBody questionListPart=RequestBody.create(MediaType.parse("application/json"), jsonArray.toString());
        Call<WorkSheetSubmitDataResponse> call=apiClient.worksheetDataResponse(examNumPart,questionListPart);
        call.enqueue(new Callback<WorkSheetSubmitDataResponse>() {
            @Override
            public void onResponse(Call<WorkSheetSubmitDataResponse> call, Response<WorkSheetSubmitDataResponse> response) {
                Log.e("Reddy","Response"+response);
                if (response.isSuccessful()) {
                    if(response.body()==null){
                        Log.e("Reddy","Response Body NULL");
                        return;
                    }
                    WorkSheetSubmitDataResponse submitDataResponse = response.body();


                    Log.e("Reddy","Status : "+submitDataResponse.getStatus());
                    Log.e("Reddy","Message : "+submitDataResponse.getMessage());

                    if("Success".equalsIgnoreCase(submitDataResponse.getStatus())){

                    /*    Toast.makeText(LevelTopicExamActivity.this,
                                "All Questions Submitted",
                                Toast.LENGTH_LONG).show();*/

                        ArrayList<String> stringIsQuestionAttempted =
                                convertBooleanListToStringList(isQuestionAttempted);

                        Intent intent =
                                new Intent(CourseTopicVisualizationActivity.this,
                                        PracticeWorkSheetResultActivity.class);

                        intent.putExtra("topicName", topicName);
                        intent.putExtra("firstName", studentName);
                        intent.putExtra("startedOn", startedDate);

                        intent.putStringArrayListExtra(
                                "answers",
                                new ArrayList<>(Arrays.asList(answerArray)));

                        intent.putStringArrayListExtra(
                                "questions",
                                new ArrayList<>(Arrays.asList(questionsArray)));

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
            public void onFailure(Call<WorkSheetSubmitDataResponse> call, Throwable t) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        showExitConfirmationDialog();
    }

    private void showExitConfirmationDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(CourseTopicVisualizationActivity.this);
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
        stopTimer(); // ✅ prevent duplicate timers

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

    private void speakQuestion(String text) {
        if (isTtsReady && text != null && !text.isEmpty()) {
            textToSpeech.stop(); // previous speech stop
            textToSpeech.speak(
                    text,
                    TextToSpeech.QUEUE_FLUSH,
                    null,
                    "QUESTION_TTS"
            );
        }
    }

    @Override
    protected void onDestroy() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
    }

    private void speakAndDisplayOneByOne(List<String> elements) {
        Log.d("TTS_DEBUG", "Elements: " + elements.toString());


        questionTextView.setText("");

        Handler handler = new Handler();
        long delay = 0;

        for (int i = 0; i < elements.size(); i++) {

            String clean = elements.get(i).trim();
            if (clean.isEmpty()) continue;

            int index = i;

            handler.postDelayed(() -> {

                String speakText;
                String displayText;

                if (clean.startsWith("+")) {
                    String num = clean.substring(1);
                    speakText = "plus " + num;
                    displayText = "+ " + num;
                }
                else if (clean.startsWith("-")) {
                    String num = clean.substring(1);
                    speakText = "minus " + num;
                    displayText = "- " + num;
                }
                else {
                    speakText = "plus " + clean;
                    displayText = "+ " + clean;
                }

                questionTextView.setText(displayText);

                if (isTtsReady) {
                    textToSpeech.speak(
                            speakText,
                            TextToSpeech.QUEUE_ADD,
                            null,
                            null
                    );
                }

                // ✅ LAST ELEMENT ayyaka
                if (index == elements.size() - 1) {

                    new Handler().postDelayed(() -> {

                        questionTextView.setText("Answer is ?");

                        if (isTtsReady) {
                            textToSpeech.speak("Answer is", TextToSpeech.QUEUE_ADD, null, null);
                        }

                        currentTime = questionTimes.get(currentQuestionIndex); // restore if needed
                        startTimer();
                        linearRepeat.setVisibility(View.VISIBLE);
                        answerEditText.setVisibility(View.VISIBLE);
                        answerEditText.setFocusable(true);
                        answerEditText.setFocusableInTouchMode(true);
                        answerEditText.setClickable(true);



                        answerEditText.post(() -> {
                            answerEditText.requestFocus();

                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            if (imm != null) {
                                imm.showSoftInput(answerEditText, InputMethodManager.SHOW_IMPLICIT);
                            }
                        });



                        // 🔥 IMPORTANT — Always show here
                        answerEditText.setVisibility(View.VISIBLE);
                        answerEditText.requestFocus();

                        butSave.setEnabled(true);
                        butSubmit.setEnabled(true);
                        isQuestionActive = false;
                        butPreviousQuestion.setEnabled(true);
                        butPreviousQuestion.setClickable(true);
                        butPreviousQuestion.setVisibility(View.VISIBLE); // 🔥 Add this

                       //

                    }, 1200);
                }

            }, delay);

            delay += 1200;
        }
    }

}