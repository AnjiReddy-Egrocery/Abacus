package com.dst.abacustrainner.Activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.text.HtmlCompat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.text.Spanned;
import android.util.Log;
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

import com.dst.abacustrainner.Model.AssignmentExamResponse;
import com.dst.abacustrainner.Model.AssignmentSubmitDataResponse;
import com.dst.abacustrainner.Model.SendData;
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

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AssignmentPracticeVisualizationActivity extends AppCompatActivity {

    LinearLayout butPreviousQuestion,butSave,butSubmit,butBack;
    TextView txtTimer,questionTextView,txtTopicName,txtdisplayquestion,txtTotalTimer;
    private EditText answerEditText;

    private int currentQuestionIndex = 0;
    private long currentTime = 0;
    private CountDownTimer countDownTimer;
    private long currentStep = 0;
    final Handler handler = new Handler();


    private boolean timerRunning = false;
    ImageView imageLeft,imageRight;

    GridLayout gridLayout;
    Button[] buttons;
    ArrayList<Boolean> isQuestionAnswered;
    ArrayList<String> enteredAnswers;

    String studentid="";
    String examNum = "";
    String topicid="";
    String topicName="";
    String studentName="";

    String answer;
    String answerText = "";
    String isCorrected;

    String status;

    String[] questionsArray= new String[]{""};
    String[] answerArray = new String[]{""};

    private String totalTime = "";

    private ArrayList<Long> questionTimes ;

    String startedDate;
    String originalAnswer;
    private int correctAnswers = 0;
    private int wrongAnswers = 0;
    final long interval = 1000; // Update interval in milliseconds

    private int attemptedQuestions = 0;
    private int notAttemptedQuestions = 0;

    private List<Boolean> isQuestionAttempted = new ArrayList<>();
    private List<Boolean> isQuestionCorrect = new ArrayList<>();

    private long currentTimeOnSaveAndNext = 0;

    List<SendData> listData;
    private List<CountDownTimer> questionTimers ;

    private static final int MAX_QUESTIONS = 30;

    long seconds;

    HorizontalScrollView scrollView;
    private TextToSpeech textToSpeech;
    private boolean isTtsReady = false;
    private boolean isQuestionActive = false;
    LinearLayout linearRepeat;
    private boolean isAnswerDisplayed = false;
    private boolean isFromPreviousClick = false;




    @SuppressLint({"MissingInflatedId", "WrongViewCast"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assignment_practice_visualization);

        butPreviousQuestion=findViewById(R.id.prv_qus);
        questionTextView = findViewById(R.id.questionTextView);
        answerEditText = findViewById(R.id.answerEditText);
        txtTimer=findViewById(R.id.timerTextView);
        butSave=findViewById(R.id .btnNext);
        butSubmit=findViewById(R.id.but_submit);
    /*  imageLeft=findViewById(R.id.leftArrow);
        imageRight=findViewById(R.id.rightArrow);*/
        gridLayout=findViewById(R.id.grid_layout);
        txtTopicName=findViewById(R.id.topic_name);
        txtTotalTimer= findViewById(R.id.total_timer_display_id);
        gridLayout = findViewById(R.id.gridLayoutButtons);
        butBack = findViewById(R.id.btn_back_to_home);

        txtdisplayquestion=findViewById(R.id.displaytextvie);
        scrollView = findViewById(R.id.horizontalScrollView);
        linearRepeat = findViewById(R.id.layout_repeat);
        linearRepeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                repeatCurrentQuestion();
            }
        });


        // Create and start the countdown timer
        final long[] totalElapsedTime = {1000};


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

        handler.post(runnable);

        Bundle bundle=getIntent().getExtras();

        studentid=bundle.getString("studentId");
        topicid=bundle.getString("topicId");
        topicName=bundle.getString("topicName");
        studentName =bundle.getString("firstName");

        txtTopicName.setText(topicName);

        Log.d("Reddy","StudentId"+ studentid);
        Log.e("Reddy","TopicId"+topicid);

        displayQuestion(currentQuestionIndex);
        questionTimers = new ArrayList<>();
        for (int i = 0; i < MAX_QUESTIONS; i++) {
            questionTimers.add(createCountDownTimer(i));
        }

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


        isQuestionAnswered = new ArrayList<>(30);
        enteredAnswers = new ArrayList<>(30);
        questionTimes = new ArrayList<>(30);
        listData = new ArrayList<>();
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

                    originalAnswer = answerArray[currentQuestionIndex];

                    Log.e("SaveAnswer", "Entered Answer: " + answer);
                    Log.e("SaveAnswer", "Original Answer: " + originalAnswer);

                    // Your existing logic...
                    originalAnswer = answerArray[currentQuestionIndex];
                    String[] questionLines = originalAnswer.split("\n");

                    // Concatenate all lines of the question
                    for (String line : questionLines) {
                        Spanned lineText = HtmlCompat.fromHtml(line, HtmlCompat.FROM_HTML_MODE_LEGACY);
                        answerText += lineText + "\n";
                        Log.e("SaveAnswer", "OrAns" + lineText);
                    }

                    if (answer.equals(originalAnswer)) {
                        isCorrected = "1";
                        status ="1";
                    } else {
                        isCorrected = "0";
                        status = "0";
                    }
                    Log.e("SaveAnswer", "Is Corrected: " + isCorrected);

                    saveAnswerAndMoveToNextQuestion();
                } else {
                    // Handle the case where currentQuestionIndex is out of bounds
                    Log.e("SaveAnswer", "Invalid currentQuestionIndex: " + currentQuestionIndex);
                }
            }

        });

        butBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showExitConfirmationDialog();
            }
        });

        butPreviousQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopTimer();

                // Reset the timer to the saved time
                saveTimerState();

                isFromPreviousClick = true; // 🔥 KEY FIX

                //txtTimer.setText("Countdown: 0 sec");
                navigateToPreviousQuestion();
                restoreTimerState(); // Restore timer state for the previous question
               // startTimer();
                int currentX = scrollView.getScrollX();
                int moveX = currentX - 100;  // Move 100 pixels to the left
                if (moveX < 0) moveX = 0; // Don't scroll beyond the leftmost position

                scrollView.smoothScrollTo(moveX, 0);
            }

        });
       /* imageLeft.setOnClickListener(new View.OnClickListener() {
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

        VerifyMethod(studentid,topicid);

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

                        // 🔥 IMPORTANT — Always show here
                        answerEditText.setVisibility(View.VISIBLE);
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



/*    private void saveAnswerAndMoveToNextQuestion() {
        stopTimer();
        saveTimerState();

        String answer = answerEditText.getText().toString();

        originalAnswer = answerArray[currentQuestionIndex];
        if (!answer.isEmpty()) {
            questionTimes.set(currentQuestionIndex,currentTime);
            listData.add(new SendData(questionTextView.getText().toString(), answer, originalAnswer, isCorrected, status,currentTime / 1000));
        }
        if (isQuestionAnswered != null && !isQuestionAnswered.isEmpty()) {
            String enteredAnswer = answerEditText.getText().toString();
            enteredAnswers.set(currentQuestionIndex, enteredAnswer);

            boolean attempted = !enteredAnswer.isEmpty();
            isQuestionAttempted.add(attempted);

            // Check if the answer is not empty before changing the button background color
            if (!enteredAnswer.isEmpty()) {
                int clickedButtonIndex = currentQuestionIndex;
                if (clickedButtonIndex >= 0 && clickedButtonIndex < gridLayout.getChildCount()) {
                    Button clickedButton = (Button) gridLayout.getChildAt(clickedButtonIndex);
                    clickedButton.setBackgroundColor(getResources().getColor(R.color.answeredButtonColor));
                    isQuestionAnswered.set(clickedButtonIndex, true);
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
                if (currentQuestionIndex < questionsArray.length) {
                    currentQuestionIndex++;
                    displayQuestion(currentQuestionIndex);
                    answerEditText.setText("");
                    currentTime = questionTimes.get(currentQuestionIndex);

                    // Update UI with the timer
                    restoreTimerState(); // Restore timer state for the next question
                    startTimer();
                }
            }else {
                showCompletionDialog();
            }
        } else {
        }
    }*/

    private void saveAnswerAndMoveToNextQuestion() {
        stopTimer();
        saveTimerState();
        linearRepeat.setVisibility(View.GONE);
        String answer = answerEditText.getText().toString();

        questionTimes.set(currentQuestionIndex,currentTime);

        if(answer.isEmpty()){
            answer = "";
            isCorrected = "0";
            status = "0";
        }

        listData.add(new SendData(
                questionsArray[currentQuestionIndex],
                originalAnswer,
                answer,
                isCorrected,
                currentTime / 1000,
                status
        ));

        Log.e("Anji","Data"+listData);




        if (currentQuestionIndex >= 0 && currentQuestionIndex < questionsArray.length) {
            String enteredAnswer = answerEditText.getText().toString();
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
                currentStep = currentQuestionIndex;
                displayQuestion(currentQuestionIndex); // Display next question
                answerEditText.setText(""); // Clear the answer field for the next question
                currentTime = questionTimes.get(currentQuestionIndex); // Restore timer state for the next question
                restoreTimerState();

            }else {
                showCompletionDialog();
            }
        } else{
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
        if (!isFromPreviousClick && isQuestionAnswered != null &&
                isQuestionAnswered.size() > currentQuestionIndex &&
                isQuestionAnswered.get(currentQuestionIndex)) {

            txtdisplayquestion.setText("Question " + (currentQuestionIndex + 1) + ":");

           // questionImageView.setVisibility(View.GONE);
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

            // Display the question text with indentation and monospaced font
            txtdisplayquestion.setText("Question " + (currentQuestionIndex + 1) + ":");
            if (textToSpeech != null) {
                textToSpeech.stop();
            }

            // 🔴 Stop previous TTS
            if (textToSpeech != null) {
                textToSpeech.stop();
            }


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
            generateButtons();
            isFromPreviousClick = false;
        } else {
            if (questionsArray == null) {
                // Handle the case where questionsArray is null (not fetched yet)
                // You may want to show an error message or take appropriate action
            } else {
                // Handle the case where currentQuestionIndex is out of bounds
                showCompletionDialog(); // or perform other necessary actions
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
                connectorParams.width = dpToPx(7); // Connector width
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

            if (currentTime == 0 && !isAnswerDisplayed) {
                // If the timer was zero, start it for the next question
                txtTimer.setVisibility(View.INVISIBLE);
                currentTime = 0;
                txtTimer.setText("Countdown: 0 sec");
            } else {
                // If the timer is not zero, update txtTimer visibility accordingly
                txtTimer.setVisibility(View.VISIBLE);
                txtTimer.setText("Countdown: " + currentTime / 1000 + " sec");
                startTimer();
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
        txtTimer.setText("TimeSpent: " + currentTime / 1000 + " sec");

    }
    private void startTimerForQuestion(int questionIndex) {
        questionTimers.get(questionIndex).start();
    }

    private void stopTimerForQuestion(int questionIndex) {
        questionTimers.get(questionIndex).cancel();
    }

    private void VerifyMethod(String studentid, String topicid) {

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
                MediaType.parse("text/plain"), studentid);

        RequestBody topicIdPart = RequestBody.create(
                MediaType.parse("text/plain"), topicid);

        Call<AssignmentExamResponse> call =
                apiClient.assignmentexam(idPart, topicIdPart);

        call.enqueue(new Callback<AssignmentExamResponse>() {

            @Override
            public void onResponse(Call<AssignmentExamResponse> call,
                                   Response<AssignmentExamResponse> response) {

                if (response.isSuccessful() && response.body() != null) {

                    try {

                        AssignmentExamResponse examResponse = response.body();
                        AssignmentExamResponse.Result result = examResponse.getResult();

                        examNum = result.getExamRnm();
                        startedDate = result.getStartedOn();

                        List<AssignmentExamResponse.QuestionItem> questionList =
                                result.getQuestionsList();

                        int questionCount = questionList.size();

                        questionsArray = new String[questionCount];
                        answerArray = new String[questionCount];

                        enteredAnswers = new ArrayList<>();
                        isQuestionAnswered = new ArrayList<>();
                        questionTimes = new ArrayList<>();
                        buttons = new Button[questionCount];

                        for (int i = 0; i < questionCount; i++) {

                            AssignmentExamResponse.QuestionItem q = questionList.get(i);

                            questionsArray[i] = q.getQuestion();
                            answerArray[i] = q.getAnswer();

                            enteredAnswers.add("");
                            isQuestionAnswered.add(false);
                            questionTimes.add(0L);
                        }

                        displayQuestion(currentQuestionIndex);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {

                    Toast.makeText(getApplicationContext(),
                            "Response Error", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<AssignmentExamResponse> call, Throwable t) {

                Toast.makeText(getApplicationContext(),
                        "API Failed : " + t.getMessage(),
                        Toast.LENGTH_LONG).show();

            }
        });
    }    private CountDownTimer createCountDownTimer(final int questionIndex) {
        final long smallerInterval = 500;
        return new CountDownTimer(Long.MAX_VALUE, interval) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (timerRunning) {
                    currentTime +=1000;
                    long seconds = currentTime / 1000;// Increase the time by 1 second
                    txtTimer.setText("TimeSpent: " + seconds  + " sec");
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
        AlertDialog.Builder dialog=new AlertDialog.Builder(AssignmentPracticeVisualizationActivity.this);
        dialog.setMessage("Are you sure you want to submit exam. You are not able to modify any thing after submiting.?");
        dialog.setTitle("www.abacustrainer.com says");
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
                startTimer();          // 🔥 IMPORTANT: timer restart

                dialog.dismiss();
            }
        });
        AlertDialog alertDialog=dialog.create();
        alertDialog.show();
    }

    private void showReportACtivity() {

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
    private void ResultMethod(String examNum, JSONArray jsonArray) {

        Log.e("Reddy","Exam Id : "+examNum);
        logLargeString("Reddy", jsonArray.toString());

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

        RequestBody examNumPart =
                RequestBody.create(MediaType.parse("text/plain"), examNum);

        RequestBody questionListPart =
                RequestBody.create(MediaType.parse("application/json"),
                        jsonArray.toString());

        Call<AssignmentSubmitDataResponse> call =
                apiClient.assignmentSubmitData(examNumPart, questionListPart);

        call.enqueue(new Callback<AssignmentSubmitDataResponse>() {

            @Override
            public void onResponse(Call<AssignmentSubmitDataResponse> call,
                                   Response<AssignmentSubmitDataResponse> response) {

                Log.e("Reddy","Response Code : "+response.code());

                if(response.body()==null){
                    Log.e("Reddy","Response Body NULL");
                    return;
                }

                AssignmentSubmitDataResponse res = response.body();

                Log.e("Reddy","Status : "+res.getStatus());
                Log.e("Reddy","Message : "+res.getMessage());

                if("Success".equalsIgnoreCase(res.getStatus())){

                    Toast.makeText(AssignmentPracticeVisualizationActivity.this,
                            "All Questions Submitted",
                            Toast.LENGTH_LONG).show();

                    ArrayList<String> stringIsQuestionAttempted =
                            convertBooleanListToStringList(isQuestionAttempted);

                    Intent intent =
                            new Intent(AssignmentPracticeVisualizationActivity.this,
                                    AssignmentVisualizationResultActivity.class);

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

                } else {

                    Toast.makeText(AssignmentPracticeVisualizationActivity.this,
                            res.getMessage(),
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<AssignmentSubmitDataResponse> call, Throwable t) {

                Log.e("Reddy","API FAILED : "+t.getMessage());

                Toast.makeText(AssignmentPracticeVisualizationActivity.this,
                        "Submission Failed",
                        Toast.LENGTH_LONG).show();
            }
        });
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

    @Override
    public void onBackPressed() {
        showExitConfirmationDialog();
    }

    private void showExitConfirmationDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(AssignmentPracticeVisualizationActivity.this);
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

    private void scrollToCenter(View view){
        int scrollViewWidth=scrollView.getWidth();
        int buttonWidth=scrollView.getWidth();
        int scrollX =(view.getLeft()+ view.getRight())/2-scrollViewWidth/2;
        scrollView.smoothScrollTo(scrollX,0);
    }

}