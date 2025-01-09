package com.dst.abacustrainner.Activity;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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


import com.dst.abacustrainner.Model.QuizData;
import com.dst.abacustrainner.R;
import com.dst.abacustrainner.database.ParcelableLong;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class VisualQuizActivity extends AppCompatActivity  {
    TextView questionTextView,displayquestion,txtTimer,txtTotalTimer;
    LinearLayout nextButton,prvButton, butSubmit;
    private EditText edtAnswer;
    final Handler handler = new Handler();
    List<String> questions;
    List<String> correctAnswers;
    String selectedTimeInterval;

    TextToSpeech textToSpeech;
    private int currentStep = 0;
    private int currentQuestionIndex = 0;

    GridLayout gridLayout;

    private List<String> answers = new ArrayList<>();
    private  List<Boolean> isQuestionAnswered = new ArrayList<>();

    private List<Boolean> isQuestionAttempted = new ArrayList<>();
    private List<Boolean> isQuestionCorrect = new ArrayList<>();

    private boolean isFirstTime = true;


    private boolean isFirstInitialization = true;
    private boolean isAppOpened = false;

    ImageView imageView;

    private boolean isTtsInitialized = false;
    private boolean isInitComplete = false;
    private LinearLayout leftIcon,rightIcon;
    private List<Long> questionTimes ;
    private CountDownTimer countDownTimer;
    private long currentTime = 0;
    private long interval = 1000;

    private boolean timerRunning = false;

    private long currentTimeOnSaveAndNext = 0;

    int isVisualization = 1;
    String currentDate;
    String selectedOperation;
    String selectedOperands;
    String selectedTotalQuestions;
    String studentId,studentName,startedDate;
    String isCorrected;
    String status;

    List<QuizData> quizData;
    private boolean isFirstTimeQuestionDisplay = true;
    private boolean isAnswerDisplayed = false;
    private List<CountDownTimer> questionTimers ;

    String currentNumber;

    private static final int REQ_CODE_SPEECH_INPUT = 100;

    private boolean isComponentsEnabled = false;

    LinearLayout linearRepeat;
    private boolean isFirstRepetition = true;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visual_quiz);

        questionTextView = findViewById(R.id.questionTextView);
        displayquestion = findViewById(R.id.textViewQuestion);
        nextButton = findViewById(R.id.btnNext);
        prvButton =findViewById(R.id.prv_qus);
        txtTimer = findViewById(R.id.timerTextView);
        gridLayout = findViewById(R.id.gridLayoutButtons);
        edtAnswer = findViewById(R.id.answerEditText);
        imageView = findViewById(R.id.imagespeaker);
        butSubmit=findViewById(R.id.but_submit);
        linearRepeat = findViewById(R.id.layout_repeat);
        leftIcon =findViewById(R.id.left_icon_click1);
        rightIcon =findViewById(R.id.right_icon_click1);
        txtTotalTimer= findViewById(R.id.total_timer_display_id);

        final long[] totalElapsedTime = {0};
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

                // Increment the total elapsed time
                totalElapsedTime[0] += interval;

                // Schedule the next update
                handler.postDelayed(this, interval);
            }
        };
        handler.post(runnable);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            questions = extras.getStringArrayList("questions");
            correctAnswers = extras.getStringArrayList("correctAnswers");
        }
        Intent intent = getIntent();
        selectedTimeInterval = intent.getStringExtra("selectedTimeInterval");
        studentId = intent.getStringExtra("studentId");
        selectedOperation = intent.getStringExtra("selectedOperation");
        currentDate = intent.getStringExtra("currentDate");
        studentName = intent.getStringExtra("firstName");

        Log.e("Test","Questions"+questions);

        quizData = new ArrayList<>();
        answers = new ArrayList<>(Collections.nCopies(questions.size(), ""));
        questionTimes = new ArrayList<>(Collections.nCopies(questions.size(), 0L));
        questionTimers = new ArrayList<>();
        for (int i = 0; i < questions.size(); i++) {
            questionTimers.add(createCountDownTimer(i));
        }
        for (int i = 0; i < questions.size(); i++) {
            isQuestionAnswered.add(false);
        }
            nextButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Check if there are more questions
                    onNextButtonClick();
                }
            });

            prvButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onPreviousButtonClick(view);
                }
            });

           imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                promptSpeechInput();
            }
        });

        linearRepeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                repeatCurrentQuestion();
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

        butSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveTimerState();

                restoreTimerState();
                showReportActivity();
            }
        });

        edtAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Highlight the text
                edtAnswer.setSelection(0, edtAnswer.getText().length());

                // Show the soft keyboard
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(edtAnswer, InputMethodManager.SHOW_IMPLICIT);
            }
        });

        disableComponents();

        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    // TextToSpeech initialization successful, proceed to display and speak the first question
                    displayQuestionAndSpeak();

                } else {
                    // Handle the case where TextToSpeech initialization fails
                    Log.e("Test", "Initialization failed");
                    // You might want to show an error message or take appropriate action here
                }
            }
        });
    }

    private void repeatCurrentQuestion() {
        stopTimer();

        // If it's not the first repetition, reset the timer state
        /*if (!isFirstRepetition) {
            currentTime = 0;
            count = 0;
        }*/

        // Display the current question
        displayQuestionAndSpeak();
        disableComponents();
        // Start the timer
        startTimer();

        isFirstRepetition = true;
    }

    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say something");

        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(getApplicationContext(), "Speech input not supported", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQ_CODE_SPEECH_INPUT) {
            if (resultCode == RESULT_OK && data != null) {
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                if (result != null && result.size() > 0) {
                    String spokenText = result.get(0);
                    edtAnswer.setText(spokenText);
                }
            }
        }
    }

    private CountDownTimer createCountDownTimer(final int questionIndex) {
        return new CountDownTimer(Long.MAX_VALUE, interval) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (timerRunning) {
                    currentTime += 1000; // Increase the time by 1 second
                    long seconds = currentTime / 1000;
                    txtTimer.setText("Countdown: " + seconds + " sec");
                }
            }

            @Override
            public void onFinish() {
                // Timer will never finish, as it's set to Long.MAX_VALUE
            }
        };
    }

//    private void onPreviousButtonClick(View view) {
//        stopTimer();
//
//        // Reset the timer to the saved time
//        saveTimerState();
//        currentQuestionIndex--;
//        if (currentQuestionIndex >= 0 && currentQuestionIndex < questions.size()) {
//            displayQuestionAndSpeak();
//            String storedAnswer = answers.get(currentQuestionIndex);
//            edtAnswer.setText(storedAnswer);
//        } else {
//            Toast.makeText(VisualQuizActivity.this, "No previous questions available", Toast.LENGTH_SHORT).show();
//        }
//        disableComponents();
//        restoreTimerState();
//        startTimer();
//    }

    private void onPreviousButtonClick(View view) {
        stopTimer();
        saveTimerState();
        disableComponents();
        // Save the current answer and time
        String ans = edtAnswer.getText().toString();
        boolean isEmptyAnswer = ans.isEmpty();

        if (!isEmptyAnswer) {
            if (answers.size() > currentQuestionIndex) {
                answers.set(currentQuestionIndex, ans);
            } else {
                answers.add(ans);
            }

            if (questionTimes.size() > currentQuestionIndex) {
                questionTimes.set(currentQuestionIndex, currentTime);
            } else {
                questionTimes.add(currentTime);
            }
        }

        if (currentQuestionIndex >= 0 && currentQuestionIndex < questions.size()) {
            String enteredAnswer = edtAnswer.getText().toString();
            if (answers.size() > currentQuestionIndex) {
                answers.set(currentQuestionIndex, enteredAnswer);
            } else {
                answers.add(enteredAnswer);
            }

            Log.e("DebugTag", "Index: " + currentQuestionIndex);
            Log.e("DebugTag", "Entered Answer: " + enteredAnswer);

            boolean attempted = !enteredAnswer.isEmpty();
            if (isQuestionAttempted.size() > currentQuestionIndex) {
                isQuestionAttempted.set(currentQuestionIndex, attempted);
            } else {
                isQuestionAttempted.add(attempted);
            }

            if (correctAnswers != null && currentQuestionIndex < correctAnswers.size()) {
                boolean correctAnswer = enteredAnswer.equals(correctAnswers.get(currentQuestionIndex));
                if (isQuestionCorrect.size() > currentQuestionIndex) {
                    isQuestionCorrect.set(currentQuestionIndex, correctAnswer);
                } else {
                    isQuestionCorrect.add(correctAnswer);
                }
            } else {
                if (isQuestionCorrect.size() > currentQuestionIndex) {
                    isQuestionCorrect.set(currentQuestionIndex, false); // Default to false
                } else {
                    isQuestionCorrect.add(false);
                }
            }

            int currentButtonIndex = currentQuestionIndex * 2;       // Current button index
            int previousButtonIndex = (currentQuestionIndex - 1) * 2; // Previous button index

            // Update current question's button color
            if (gridLayout.getChildCount() > currentButtonIndex) {
                View currentButtonView = gridLayout.getChildAt(currentButtonIndex);
                if (currentButtonView instanceof Button) {
                    Button currentButton = (Button) currentButtonView;
                    currentButton.setBackgroundColor(getResources().getColor(R.color.answeredButtonColor)); // Answered color
                }
            }

            // Update previous question's button color
            if (gridLayout.getChildCount() > previousButtonIndex) {
                View previousButtonView = gridLayout.getChildAt(previousButtonIndex);
                if (previousButtonView instanceof Button) {
                    Button previousButton = (Button) previousButtonView;
                    previousButton.setBackgroundColor(getResources().getColor(R.color.answeredButtonColor)); // Answered color
                }
            }

            // Update isQuestionAnswered list
            if (!enteredAnswer.isEmpty()) {
                if (isQuestionAnswered.size() > currentQuestionIndex) {
                    isQuestionAnswered.set(currentQuestionIndex, true);
                } else {
                    isQuestionAnswered.add(true);
                }
            }
        }

        if (currentQuestionIndex > 0) {
            currentQuestionIndex--;
            currentStep = currentQuestionIndex;
            displayQuestionAndSpeak();

            if (!answers.isEmpty() && currentQuestionIndex < answers.size()) {
                String storedAnswer = answers.get(currentQuestionIndex);
                edtAnswer.setText(storedAnswer);
            } else {
                edtAnswer.getText().clear();
            }

            currentTime = questionTimes.get(currentQuestionIndex);

            restoreTimerState();
            startTimer();
        } else {
            Toast.makeText(VisualQuizActivity.this, "No previous questions available", Toast.LENGTH_SHORT).show();
        }
    }
//    private void onNextButtonClick() {
//        stopTimer();
//        saveTimerState();
//        disableComponents();
//        String enteredAns = edtAnswer.getText().toString();
//        boolean isEmptyAnswer = enteredAns.isEmpty();
//        if (!isEmptyAnswer) {
//            answers.set(currentQuestionIndex, enteredAns);
//            questionTimes.set(currentQuestionIndex,currentTime);
//            int currentQuestionCorrectness = enteredAns.equals(correctAnswers.get(currentQuestionIndex)) ? 1 : 0;
//            isCorrected = String.valueOf(currentQuestionCorrectness);
//            status = String.valueOf(currentQuestionCorrectness);
//            quizData.add(new QuizData(questionTextView.getText().toString(), enteredAns, correctAnswers.get(currentQuestionIndex), isCorrected, status, currentTime / 1000));
//
//            // Log the value of isCorrected
//            Log.e("Test", "isCorrected for Question " + (currentQuestionIndex + 1) + ": " + isCorrected);
//        }
//
//        if (questions != null && !questions.isEmpty()) {
//            // Display the next question
//            String enteredAnswer = edtAnswer.getText().toString();
//            answers.add(enteredAnswer);
//
//            boolean attempted = !enteredAnswer.isEmpty();
//            isQuestionAttempted.add(attempted);
//            // Check if the answer is correct
//            boolean correctedAnswer = enteredAnswer.equals(correctAnswers.get(currentQuestionIndex));
//            isQuestionCorrect.add(correctedAnswer);
//
//            if (!enteredAnswer.isEmpty()) {
//                // Set the background color of the clicked button to indicate it's answered
//                int clickedButtonIndex = currentQuestionIndex;
//                if (clickedButtonIndex >= 0 && clickedButtonIndex < gridLayout.getChildCount()) {
//                    Button clickedButton = (Button) gridLayout.getChildAt(clickedButtonIndex);
//                    clickedButton.setBackgroundColor(getResources().getColor(R.color.answeredButtonColor));
//                    isQuestionAnswered.set(clickedButtonIndex, true);
//                }
//            }
//        }
//        if (currentQuestionIndex < questions.size()-1) {
//            currentQuestionIndex++;
//            displayQuestionAndSpeak();
//            edtAnswer.getText().clear();
//            currentTime = questionTimes.get(currentQuestionIndex);
//
//            // Update UI with the timer
//            restoreTimerState(); // Restore timer state for the next question
//            if (currentTime == 0) {
//                // If the timer was zero, start it for the next question
//                currentTime = 0;
//                txtTimer.setText("Countdown: 0 sec");
//            }
//        }
//
//       else {
//            Toast.makeText(VisualQuizActivity.this, "Quiz Completed", Toast.LENGTH_SHORT).show();
//            showReportActivity();
//        }
//    }

    private void onNextButtonClick() {

        stopTimer();
        saveTimerState();
        disableComponents();
        String ans = edtAnswer.getText().toString();
        boolean isEmptyAnswer = ans.isEmpty();

        if (!isEmptyAnswer) {
            answers.set(currentQuestionIndex, ans);
            questionTimes.set(currentQuestionIndex, currentTime);
        }

        if (currentQuestionIndex >= 0 && currentQuestionIndex < questions.size()) {
            String enteredAnswer = edtAnswer.getText().toString();
            answers.add(enteredAnswer);

            Log.e("DebugTag", "Index: " + currentQuestionIndex);
            Log.e("DebugTag", "Entered Answer: " + enteredAnswer);

            boolean attempted = !enteredAnswer.isEmpty();
            isQuestionAttempted.add(attempted);

            if (correctAnswers != null && currentQuestionIndex < correctAnswers.size()) {
                boolean correctAnswer = enteredAnswer.equals(correctAnswers.get(currentQuestionIndex));
                isQuestionCorrect.add(correctAnswer);
            } else {
                isQuestionCorrect.add(false); // Default to false
            }

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

        if (currentQuestionIndex < questions.size() - 1) {
            currentQuestionIndex++;
            currentStep = currentQuestionIndex;
            displayQuestionAndSpeak();
            edtAnswer.getText().clear();
            currentTime = questionTimes.get(currentQuestionIndex);

            restoreTimerState();
            startTimer();
        } else {
            Toast.makeText(VisualQuizActivity.this, "Level is Completed", Toast.LENGTH_SHORT).show();
            showReportActivity();
        }
    }

    private void showReportActivity() {
        stopTimer();

        AlertDialog.Builder dialog=new AlertDialog.Builder(VisualQuizActivity.this);
        dialog.setMessage("Are you sure you want to submit exam. You are not able to modify any thing after submiting.?");
        dialog.setTitle("www.abacustrainer.com says");
        dialog.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int which) {

                        ShowResultPopup();

                    }
                });
        dialog.setNegativeButton("cancel",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                startTimer();
            }
        });
        AlertDialog alertDialog=dialog.create();
        alertDialog.show();
    }

    private void ShowResultPopup() {
        ArrayList<String> stringIsQuestionAttempted = convertBooleanListToStringList(isQuestionAttempted);
        ArrayList<String> stringIsQuestionCorrect = convertBooleanListToStringList(isQuestionCorrect);
        Intent intent = new Intent(VisualQuizActivity.this, VisualResultActivity.class);
        intent.putExtra("firstName",studentName);
        intent.putExtra("submitedOn",startedDate);
        intent.putStringArrayListExtra("questions", new ArrayList<>(questions));
        intent.putStringArrayListExtra("correctAnswers", new ArrayList<>(correctAnswers));
        intent.putStringArrayListExtra("enteredAnswers", new ArrayList<>(answers));
        intent.putStringArrayListExtra("isQuestionAttempted", stringIsQuestionAttempted);
        intent.putStringArrayListExtra("isQuestionCorrect", stringIsQuestionCorrect);
        ArrayList<ParcelableLong> parcelableTimes = new ArrayList<>();
        for (Long time : questionTimes) {
            parcelableTimes.add(new ParcelableLong(time));
        }
        intent.putParcelableArrayListExtra("questionTimes", parcelableTimes);
        startActivity(intent);
        finish();
    }

    private ArrayList<String> convertBooleanListToStringList(List<Boolean> isQuestionAttempted) {
        ArrayList<String> stringList = new ArrayList<>();
        for (Boolean value : isQuestionAttempted) {
            stringList.add(value ? "1" : "0");
        }
        return stringList;
    }

//    private void generateButtons() {
//        gridLayout.removeAllViews();
//
//        int marginLeftInDp = getResources().getDimensionPixelSize(R.dimen.button_margin_left);
//        int marginRightInDp = getResources().getDimensionPixelSize(R.dimen.button_margin_right);
//        int marginTopInDp = getResources().getDimensionPixelSize(R.dimen.button_margin_top);
//        int marginBottomInDp = getResources().getDimensionPixelSize(R.dimen.button_margin_bottom);
//
//        // Create a button for each question
//        for (int i = 0; i < questions.size(); i++) {
//            Button button = new Button(this);
//            button.setText(String.valueOf(i + 1));
//            button.setTag(i);
//            button.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    onButtonClicked((int) view.getTag());
//                }
//            });
//
//            // Set layout parameters for the button in the grid
//            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
//            params.width = 0; // This will make buttons equally distribute in columns
//            params.height = GridLayout.LayoutParams.WRAP_CONTENT;
//            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f); // Equally distribute columns
//            params.rowSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f); // Equally distribute rows
//
//            // Set margins for the button
//            params.leftMargin = marginLeftInDp;
//            params.rightMargin = marginRightInDp;
//            params.topMargin = marginTopInDp;
//            params.bottomMargin = marginBottomInDp;
//
//            button.setLayoutParams(params);
//
//            // Set background color based on whether the question is answered
//            if (isQuestionAnswered.size() > i && isQuestionAnswered.get(i)) {
//                button.setBackgroundColor(getResources().getColor(R.color.answeredButtonColor));
//            } else {
//                button.setBackgroundColor(getResources().getColor(R.color.unansweredButtonColor));
//            }
//            // Add the button to the layout
//            gridLayout.addView(button);
//        }
//    }


    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }

    private void generateButtons() {
        gridLayout.removeAllViews();

        int totalSteps = questions.size(); // Total steps (buttons)
        int totalColumns = totalSteps * 2 - 1; // Steps + Connectors

        for (int i = 0; i < totalColumns; i++) {
            if (i % 2 == 0) {
                // Create a circular step button
                Button stepButton = new Button(this);
                stepButton.setText(String.valueOf((i / 2) + 1)); // Step number
                stepButton.setGravity(Gravity.CENTER);
                int stepIndex = i / 2; // Determine the step index
                if (isQuestionAnswered.size() > stepIndex && isQuestionAnswered.get(stepIndex)){
                    stepButton.setTextColor(Color.WHITE);
                }else{
                    stepButton.setTextColor(Color.BLACK);
                }

                stepButton.setTextSize(14);
                stepButton.setTypeface(null, Typeface.BOLD);

                // Set background color based on status

                if (isQuestionAnswered.size() > stepIndex && isQuestionAnswered.get(stepIndex)) {
                    stepButton.setBackground(getDrawable(R.drawable.circle_green)); // Answered
                } else if (stepIndex == currentStep) {
                    stepButton.setBackground(getDrawable(R.drawable.circle_orange)); // Current step
                } else {
                    stepButton.setBackground(getDrawable(R.drawable.circle_gray)); // Unanswered
                }

                // Set layout parameters for the step button
                GridLayout.LayoutParams stepParams = new GridLayout.LayoutParams();
                stepParams.width = dpToPx(40); // Circular size
                stepParams.height = dpToPx(40);
                stepParams.setMargins(dpToPx(0), dpToPx(16), dpToPx(0), dpToPx(16));
                stepButton.setLayoutParams(stepParams);

                // Add click listener for the step button
                stepButton.setTag(stepIndex);
                stepButton.setOnClickListener(view -> {
                    int clickedStep = (int) view.getTag();
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
                connectorParams.width = dpToPx(15); // Connector width
                connectorParams.height = dpToPx(4); // Connector height
                connectorParams.setMargins(0, dpToPx(35), 0, dpToPx(0)); // Vertical alignment
                connector.setLayoutParams(connectorParams);

                // Add the connector to the GridLayout
                gridLayout.addView(connector);
            }
        }
    }

//    private void onButtonClicked(int tag) {
//        if (isAnswerDisplayed) {
//            stopTimer();
//
//            saveTimerState();
//
//            currentQuestionIndex = tag;
//            displayQuestionAndSpeak();
//            disableComponents();
//            String storedAnswer = answers.get(currentQuestionIndex);
//            edtAnswer.setText(storedAnswer);
//            restoreTimerState();
//
//            if (currentTime == 0 && !isAnswerDisplayed) {
//                // If the timer was zero, start it for the next question
//                txtTimer.setVisibility(View.INVISIBLE);
//                currentTime = 0;
//                txtTimer.setText("Countdown: 0 sec");
//            } else {
//                // If the timer is not zero, update txtTimer visibility accordingly
//                txtTimer.setVisibility(View.VISIBLE);
//                txtTimer.setText("Countdown: " + currentTime / 1000 + " sec");
//                startTimer();
//            }
//        }
//    }

    private void onButtonClicked(int tag) {
        if (isAnswerDisplayed) {
            stopTimer();
            saveTimerState();
            currentQuestionIndex = tag;
            displayQuestionAndSpeak();
            disableComponents();
            String storedAnswer = answers.get(currentQuestionIndex);
            edtAnswer.setText(storedAnswer);
            restoreTimerState();

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
            String ans = edtAnswer.getText().toString();
            boolean isEmptyAnswer = ans.isEmpty();

            if (!isEmptyAnswer) {
                if (answers.size() > currentQuestionIndex) {
                    answers.set(currentQuestionIndex, ans);
                } else {
                    answers.add(ans);
                }

                if (questionTimes.size() > currentQuestionIndex) {
                    questionTimes.set(currentQuestionIndex, currentTime);
                } else {
                    questionTimes.add(currentTime);
                }
            }

            // Update the answer and time for the current question if needed
            String enteredAnswer = edtAnswer.getText().toString();
            if (answers.size() > currentQuestionIndex) {
                answers.set(currentQuestionIndex, enteredAnswer);
            } else {
                answers.add(enteredAnswer);
            }

            boolean attempted = !enteredAnswer.isEmpty();
            if (isQuestionAttempted.size() > currentQuestionIndex) {
                isQuestionAttempted.set(currentQuestionIndex, attempted);
            } else {
                isQuestionAttempted.add(attempted);
            }

            if (correctAnswers != null && currentQuestionIndex < correctAnswers.size()) {
                boolean correctAnswer = enteredAnswer.equals(correctAnswers.get(currentQuestionIndex));
                if (isQuestionCorrect.size() > currentQuestionIndex) {
                    isQuestionCorrect.set(currentQuestionIndex, correctAnswer);
                } else {
                    isQuestionCorrect.add(correctAnswer);
                }
            } else {
                if (isQuestionCorrect.size() > currentQuestionIndex) {
                    isQuestionCorrect.set(currentQuestionIndex, false); // Default to false if no answer
                } else {
                    isQuestionCorrect.add(false);
                }
            }

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
            // Restore the timer state and start the timer for the selected question

            startTimer();

            // Invalidate GridLayout to ensure changes are visible
            gridLayout.invalidate();


        }
    }


    private void displayQuestionAndSpeak() {
        if (questions != null && !questions.isEmpty() && currentQuestionIndex < questions.size()) {
            // Speak the current question
            displayquestion.setText("Question " + (currentQuestionIndex + 1) + ": ");
            questionTextView.setGravity(Gravity.CENTER);
            String[] questionsArray = questions.toArray(new String[0]);
            String[] numberParts = questionsArray[currentQuestionIndex].split("(?<=\\D)(?=\\d)");

            Log.e("Test", "Question: " + Arrays.toString(numberParts));

           readNumbersAloud(numberParts);
           generateButtons();

        } else {
            // Handle the case where there are no more questions
            Toast.makeText(VisualQuizActivity.this, "No more questions", Toast.LENGTH_SHORT).show();
            // You can finish the activity or navigate to another screen as needed
        }
    }

    private void readNumbersAloud (String[] numbers) {
        for (int i = 0; i < numbers.length; i++) {
            Log.e("Test", "Element at index " + i + ": " + numbers[i]);
        }

        int totalNumbers = numbers.length;

        // Rest of the code...

        // The following part assumes that the rest of the code includes the processing of each element in the numbers array.
        for (int i = 0; i < totalNumbers; i++) {
            int finalI = i;

            new CountDownTimer(i * 1500, 500) {
                public void onTick(long millisUntilFinished) {
                    // Do nothing on tick
                }

                public void onFinish() {
                    if (finalI < totalNumbers) {
                        currentNumber = numbers[finalI];

                        // Read the original question aloud
                        speak(currentNumber);

                        // Display the original question
                        questionTextView.setText(currentNumber.replace(" ", ""));

                        if (finalI == totalNumbers - 1) {
                            // Delay for a short duration
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    // Split the currentNumber into parts based on commas and spaces
                                    String[] numberParts = currentNumber.split("(?<=\\D)(?=\\d)");

                                    if (numberParts.length > 0) {
                                        String lastNumber = numberParts[numberParts.length - 1];

                                        // Replace the last number with " Answer is " in the currentNumber
                                        currentNumber = currentNumber.replace(lastNumber, "Answer \t\t is");

                                        // Set the modified current number to txtDisplayQuestion
                                        questionTextView.setText(currentNumber.replace(" ", ""));
                                        txtTimer.setVisibility(View.VISIBLE);
                                        edtAnswer.requestFocus();

                                        // Show the keyboard
                                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                        if (imm != null) {
                                            imm.showSoftInput(edtAnswer, InputMethodManager.SHOW_IMPLICIT);
                                        }
                                        startTimer();
                                        // Read the modified question aloud
                                        speak(currentNumber);
                                        enableComponents();
                                    }
                                }
                            }, 2000); // Adjust the delay time as needed (2 seconds in this example)
                        }
                    }
                }
            }.start();
        }
    }

    private void enableComponents() {
        nextButton.setEnabled(true);
        prvButton.setEnabled(true);
        edtAnswer.setEnabled(true);
        imageView.setEnabled(true);
        edtAnswer.requestFocus();
        linearRepeat.setVisibility(View.VISIBLE);
        // Show the keyboard
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(edtAnswer, InputMethodManager.SHOW_IMPLICIT);
        }
       // linearRepeat.setVisibility(View.VISIBLE);
        isComponentsEnabled = true;
        isAnswerDisplayed = true;
        butSubmit.setEnabled(true);
    }

    private void disableComponents() {
        // Disable components
        nextButton.setEnabled(false);
        prvButton.setEnabled(false);
        edtAnswer.setEnabled(false);
        imageView.setEnabled(false);
        linearRepeat.setVisibility(View.INVISIBLE);
        isComponentsEnabled = false;
        isAnswerDisplayed = false;
        butSubmit.setEnabled(false);
    }

    private void speak(String text) {
        String[] elements = text.split("\\s+");

        StringBuilder finalText = new StringBuilder();
        for (String element : elements) {
            if (element.equals("+")) {
                // Replace "+" with "plus"
                finalText.append("plus ");
            } else if (element.equals("*")) {
                // Replace "*" with "into"
                finalText.append("into ");
            } else {
                // Not "+" or "*", keep it as it is
                finalText.append(element).append(" ");
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            textToSpeech.speak(finalText.toString().trim(), TextToSpeech.QUEUE_FLUSH, null, null);
        } else {
            textToSpeech.speak(finalText.toString().trim(), TextToSpeech.QUEUE_FLUSH, null);
        }
    }
    @Override
    protected void onDestroy() {
        // Shutdown TextToSpeech only if it was initialized in this activity
        if (textToSpeech != null && !isChangingConfigurations()) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }

        super.onDestroy();
    }

    private void restoreTimerState() {
        if (currentQuestionIndex >= 0 && currentQuestionIndex < questionTimes.size()) {
            currentTime = questionTimes.get(currentQuestionIndex);
            // Update UI with the restored timer
            if (currentTime == 0) {
                // If the timer is zero, make txtTimer invisible
                txtTimer.setVisibility(View.INVISIBLE);
                currentTime = 0;
                txtTimer.setText("Countdown: 0 sec");
            } else {
                // If the timer is not zero, make txtTimer visible
                txtTimer.setVisibility(View.VISIBLE);
                txtTimer.setText("Countdown: " + currentTime / 1000 + " sec");
                //startTimer(); // Start the timer for the next question
            }
        }
    }

    private void saveTimerState() {
        if (currentQuestionIndex >= 0 && currentQuestionIndex < questionTimes.size()) {
            questionTimes.set(currentQuestionIndex, currentTime);
        }
    }

    private void stopTimer() {
        timerRunning = false;
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    private void startTimer() {
        stopTimer();
        timerRunning = true;
        countDownTimer = createCountDownTimer(currentQuestionIndex);
        countDownTimer.start();
    }

    @Override
    public void onBackPressed() {
        showExitConfirmationDialog();
    }

    private void showExitConfirmationDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(VisualQuizActivity.this);
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
}

