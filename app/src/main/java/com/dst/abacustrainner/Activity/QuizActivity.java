package com.dst.abacustrainner.Activity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;


import com.dst.abacustrainner.Model.GameResponse;
import com.dst.abacustrainner.Model.QuizData;
import com.dst.abacustrainner.R;
import com.dst.abacustrainner.Services.ApiClient;
import com.dst.abacustrainner.database.ParcelableLong;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class QuizActivity extends AppCompatActivity {
    private LinearLayout btnNextQuestion, btnPreviousQuestion,butSubmit,btnBack;
    private TextView textViewQuestion, txtDisplayQuestion, txtTimer,txtTotalTimer;

    private EditText edtAnswer;
    private int currentQuestionIndex = 0;
    private List<String> questions;
    private List<String> coreectedAnswers;
    private List<String> answers ;
    private List<Long> questionTimes ;
    LinearLayout leftIcon,rightIcon;
    private LinearLayout layoutData;

    private CountDownTimer countDownTimer;
    private long currentTime = 0;
    private long interval = 1000;
    final Handler handler = new Handler();
    private boolean timerRunning = false;

    GridLayout gridLayout;

    private List<Boolean> isQuestionAnswered = new ArrayList<>();

    private boolean quizCompleted = false;

    private List<Boolean> isQuestionAttempted = new ArrayList<>();
    private List<Boolean> isQuestionCorrect = new ArrayList<>();

    private long currentTimeOnSaveAndNext = 0;

    int isVisualization = 0;

    List<QuizData> quizData;

    String currentDate;
    String selectedOperation;
    String selectedOperands;
    String selectedTotalQuestions;
    String studentId,studentName,startedDate;
    String isCorrected;
    String status;
    private int currentStep = 0;

    private List<CountDownTimer> questionTimers ;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        textViewQuestion = findViewById(R.id.textViewQuestion);
        txtDisplayQuestion = findViewById(R.id.textQuestion);
        txtTimer = findViewById(R.id.timerTextView);
        btnNextQuestion = findViewById(R.id.btnNext);
        btnPreviousQuestion = findViewById(R.id.prv_qus);
        layoutData = findViewById(R.id.layout_data);
        edtAnswer = findViewById(R.id.answerEditText);
        gridLayout = findViewById(R.id.gridLayoutButtons);
        butSubmit=findViewById(R.id.but_submit);
        txtTotalTimer= findViewById(R.id.total_timer_display_id);
        leftIcon =findViewById(R.id.left_icon_click);
        rightIcon =findViewById(R.id.right_icon_click);
        btnBack=findViewById(R.id.btn_back_to_game_creation);
        Bundle extras = getIntent().getExtras();

        // Create and start the countdown timer
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

// Start the count-up timer
        handler.post(runnable);
        if (extras != null) {
            questions = extras.getStringArrayList("questions");
            coreectedAnswers = extras.getStringArrayList("correctAnswers");

        }

        Log.e("Test","Questions"+questions);

        Intent intent = getIntent();

        studentId = intent.getStringExtra("studentId");
        selectedOperation = intent.getStringExtra("selectedOperation");
        currentDate = intent.getStringExtra("currentDate");
        studentName = intent.getStringExtra("firstName");

        quizData = new ArrayList<>();
        answers = new ArrayList<>(Collections.nCopies(questions.size(), ""));
        questionTimes = new ArrayList<>(Collections.nCopies(questions.size(), 0L));
        questionTimers = new ArrayList<>();
        for (int i = 0; i < questions.size(); i++) {
            questionTimers.add(createCountDownTimer(i));
        }
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent =new Intent(QuizActivity.this,VisualiztionActivity.class);
                showExitConfirmationDialog();

            }
        });
        btnNextQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSaveAndNextButtonClick(view);
            }
        });
        btnPreviousQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onPreviousButtonClick(view);
            }
        });

        butSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveTimerState();
                displayQuestion();
                edtAnswer.getText().clear();
                restoreTimerState();
                showCompletionPopup();
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



        for (int i = 0; i < questions.size(); i++) {
            isQuestionAnswered.add(false);
        }

        displayQuestion();
        startTimer();

    }

    private CountDownTimer createCountDownTimer(final int questionIndex) {
        final long smallerInterval = 500;
        return new CountDownTimer(Long.MAX_VALUE, interval) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (timerRunning) {
                    currentTime += 1000; // Increase the time by 1 second
                    long seconds = currentTime / 1000;
                    txtTimer.setText("Time Spent: " + seconds + " sec");
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
//
//        currentQuestionIndex--;
//        if (currentQuestionIndex >= 0 && currentQuestionIndex < questions.size()) {
//            displayQuestion();
//            String storedAnswer = answers.get(currentQuestionIndex);
//            edtAnswer.setText(storedAnswer);
//        } else {
//            Toast.makeText(QuizActivity.this, "No previous questions available", Toast.LENGTH_SHORT).show();
//        }
//        restoreTimerState(); // Restore timer state for the previous question
//        startTimer();
//
//
//    }
private void onPreviousButtonClick(View view) {
    stopTimer();
    saveTimerState();

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

        if (coreectedAnswers != null && currentQuestionIndex < coreectedAnswers.size()) {
            boolean correctAnswer = enteredAnswer.equals(coreectedAnswers.get(currentQuestionIndex));
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
        displayQuestion();

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
        Toast.makeText(QuizActivity.this, "No previous questions available", Toast.LENGTH_SHORT).show();
    }
}

//    private void onSaveAndNextButtonClick(View view) {
//        stopTimer();
//        saveTimerState();
//        String enteredAns = edtAnswer.getText().toString();
//        boolean isEmptyAnswer = enteredAns.isEmpty();
//        if (!isEmptyAnswer) {
//            answers.set(currentQuestionIndex, enteredAns);
//            questionTimes.set(currentQuestionIndex,currentTime);
//            int currentQuestionCorrectness = enteredAns.equals(coreectedAnswers.get(currentQuestionIndex)) ? 1 : 0;
//            isCorrected = String.valueOf(currentQuestionCorrectness);
//            status = String.valueOf(currentQuestionCorrectness);
//            quizData.add(new QuizData(txtDisplayQuestion.getText().toString(), enteredAns, coreectedAnswers.get(currentQuestionIndex), isCorrected, status, currentTime / 1000));
//
//            // Log the value of isCorrected
//            Log.e("Reddy", "isCorrected for Question " + (currentQuestionIndex + 1) + ": " + isCorrected);
//        }
//
//        // Check if there are more questions
//        if (questions != null && !questions.isEmpty()) {
//            // Display the next question
//            String enteredAnswer = edtAnswer.getText().toString();
//            answers.add(enteredAnswer);
//
//            boolean attempted = !enteredAnswer.isEmpty();
//            isQuestionAttempted.add(attempted);
//
//            // Check if the answer is correct
//            boolean correctAnswer = enteredAnswer.equals(coreectedAnswers.get(currentQuestionIndex));
//            isQuestionCorrect.add(correctAnswer);
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
//
//        if (currentQuestionIndex < questions.size()-1) {
//            if (currentQuestionIndex < questions.size()) {
//                currentQuestionIndex++;
//                displayQuestion();
//                edtAnswer.getText().clear();
//                currentTime = questionTimes.get(currentQuestionIndex);
//
//                // Update UI with the timer
//                restoreTimerState(); // Restore timer state for the next question
//                startTimer();
//            }
//        }
//         else {
//            Toast.makeText(QuizActivity.this, "Quiz Completed", Toast.LENGTH_SHORT).show();
//            showCompletionPopup();
//        }
//    }

    private void onSaveAndNextButtonClick(View view) {

        stopTimer();
        saveTimerState();
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

            if (coreectedAnswers != null && currentQuestionIndex < coreectedAnswers.size()) {
                boolean correctAnswer = enteredAnswer.equals(coreectedAnswers.get(currentQuestionIndex));
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
            displayQuestion();
            edtAnswer.getText().clear();
            currentTime = questionTimes.get(currentQuestionIndex);

            restoreTimerState();
            startTimer();
        } else {
            Toast.makeText(QuizActivity.this, "Level is Completed", Toast.LENGTH_SHORT).show();
            showCompletionPopup();
        }
    }

    private void showCompletionPopup() {
        stopTimer();
        AlertDialog.Builder dialog=new AlertDialog.Builder(QuizActivity.this);
        dialog.setMessage("Are you sure you want to submit exam. You are not able to modify any thing after submiting.?");
        dialog.setTitle("www.abacustrainer.com says");
        dialog.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int which) {

                         showReportActivity();

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

    private void displayQuestion() {
        // Display the question on the TextView
        if (questions != null && !questions.isEmpty() && currentQuestionIndex < questions.size()) {
            // Display the question on the TextView
            textViewQuestion.setText("Question " + (currentQuestionIndex + 1) + "/"+(questions.size())+": " );
            txtDisplayQuestion.setGravity(Gravity.RIGHT);
            txtDisplayQuestion.setText( questions.get(currentQuestionIndex));
            generateButtons();

            Log.e("DebugTag", "isQuestionAnswered: " + isQuestionAnswered.toString());
        } else {
            Log.e("DisplayQuestion", "No questions available or index out of bounds");
        }
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
            final int[] stepIndex = {i / 2}; // Determine the step index
            if (isQuestionAnswered.size() > stepIndex[0] && isQuestionAnswered.get(stepIndex[0])){
                stepButton.setTextColor(Color.WHITE);
            }else{
                stepButton.setTextColor(Color.BLACK);
            }

            stepButton.setTextSize(14);
            stepButton.setTypeface(null, Typeface.BOLD);

            // Set background color based on status

            if (isQuestionAnswered.size() > stepIndex[0] && isQuestionAnswered.get(stepIndex[0])) {
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
            stepButton.setLayoutParams(stepParams);

            // Add click listener for the step button
            stepButton.setTag(stepIndex[0]);

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
//    private void onButtonClicked(int questionIndex) {
//        // Handle button click, update currentQuestionIndex, and display the question
//        saveTimerState();
//
//        currentQuestionIndex = questionIndex;
//        Log.e("Reddy", "Button Clicked - Index: " + questionIndex);
//        displayQuestion();
//        String storedAnswer = answers.get(currentQuestionIndex);
//        edtAnswer.setText(storedAnswer);
//        restoreTimerState();
//    }
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

            // Save the answer and time for the current question (before navigating)
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

            if (coreectedAnswers != null && currentQuestionIndex < coreectedAnswers.size()) {
                boolean correctAnswer = enteredAnswer.equals(coreectedAnswers.get(currentQuestionIndex));
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

            // Display the selected question and restore its answer
            displayQuestion();
            String storedAnswer = answers.get(currentQuestionIndex);
            edtAnswer.setText(storedAnswer);

            // Save the time state for the selected question
            currentTime = questionTimes.get(currentQuestionIndex);

            // Restore the timer state and start the timer for the selected question
            restoreTimerState();
            startTimer();

            // Invalidate GridLayout to ensure changes are visible
            gridLayout.invalidate();
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


    private void saveTimerState() {
        if (currentQuestionIndex >= 0 && currentQuestionIndex < questionTimes.size()) {
        questionTimes.set(currentQuestionIndex, currentTime);
        }
    }
    private void restoreTimerState() {
        if (currentQuestionIndex >= 0 && currentQuestionIndex < questionTimes.size()) {
        currentTime = questionTimes.get(currentQuestionIndex);
        // Update UI with the restored timer
        txtTimer.setText("TimeSpent: " + currentTime / 1000 + " sec");
        }
    }
    private void startTimerForQuestion(int questionIndex) {
        questionTimers.get(questionIndex).start();
    }

    private void stopTimerForQuestion(int questionIndex) {
        questionTimers.get(questionIndex).cancel();
    }

    private void showReportActivity() {
        JSONArray jsonArray = new JSONArray();
        try {
            for (int i = 0; i < quizData.size(); i++) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("question", quizData.get(i).getQuestion());
                jsonObject.put("answer", quizData.get(i).getCorrectAnswer());
                jsonObject.put("given", quizData.get(i).getEnterAnswer());
                jsonObject.put("is_currect", quizData.get(i).getIsCorrect());
                jsonObject.put("time_taken", quizData.get(i).getTimeTaken());
                jsonObject.put("status", quizData.get(i).getStatus());
                // Add the questionObject to the questionsArray
                jsonArray.put(jsonObject);
            }
            Log.e("Reddy","studentId"+studentId);
            Log.e("Reddy","createdOn"+currentDate);
            Log.e("Reddy", "Formatted JSON Array Contents: " + jsonArray.toString());
            Log.e("Reddy","operation"+selectedOperation );
            Log.e("Reddy","isVisualization"+isVisualization);
            ReportMethod(studentId, currentDate, jsonArray , selectedOperation, isVisualization);


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void ReportMethod(String studentId, String currentDate, JSONArray jsonData, String selectedOperation, int isVisualization) {
        Log.e("Reddy","studentId"+studentId);
        Log.e("Reddy","createdOn"+currentDate);
        Log.e("Reddy","questionsList"+jsonData.toString());
        Log.e("Reddy","operation"+selectedOperation );
        Log.e("Reddy","isVisualization"+isVisualization);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://www.abacustrainer.com/") // Replace with your API URL
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
        ApiClient apiClient=retrofit.create(ApiClient.class);

        RequestBody idPart = RequestBody.create(MediaType.parse("text/plain"), studentId);
        RequestBody datePart=RequestBody.create(MediaType.parse("text/plain"), currentDate);
        RequestBody arrayPart=RequestBody.create(MediaType.parse("text/plain"),jsonData.toString());
        RequestBody operationPart = RequestBody.create(MediaType.parse("text/plain"),selectedOperation);
        RequestBody visualizationPart = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(isVisualization));



        Call<GameResponse> call=apiClient.gameData(idPart,datePart,arrayPart,operationPart,visualizationPart);

        call.enqueue(new Callback<GameResponse>() {
            @Override
            public void onResponse(Call<GameResponse> call, Response<GameResponse> response) {
                if (response.isSuccessful()){
                    GameResponse gameResponse=response.body();
                    if (gameResponse!=null){

                        GameResponse.Result gameResult=gameResponse.getResult();

                        startedDate =gameResult.getSubmitedOn();

                        Toast.makeText(QuizActivity.this,"Student Number Game Successfully Submited.",Toast.LENGTH_LONG).show();
                        ArrayList<String> stringIsQuestionAttempted = convertBooleanListToStringList(isQuestionAttempted);
                        ArrayList<String> stringIsQuestionCorrect = convertBooleanListToStringList(isQuestionCorrect);
                        Intent intent = new Intent(QuizActivity.this, ResultActivity.class);
                        intent.putExtra("firstName",studentName);
                        intent.putExtra("submitedOn",startedDate);
                        intent.putStringArrayListExtra("questions", new ArrayList<>(questions));
                        intent.putStringArrayListExtra("correctAnswers", new ArrayList<>(coreectedAnswers));
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
                }
            }

            @Override
            public void onFailure(Call<GameResponse> call, Throwable t) {

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

    @Override
    public void onBackPressed() {
        showExitConfirmationDialog();
    }

    private void showExitConfirmationDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(QuizActivity.this);
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