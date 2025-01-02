package com.dst.abacustrainner.Activity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.Layout;
import android.text.method.Touch;
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
import androidx.core.content.ContextCompat;

import com.dst.abacustrainner.R;
import com.dst.abacustrainner.database.ParcelableLong;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class FirstLevelActivity extends AppCompatActivity {

    private TextView textViewQuestion, txtDisplayQuestion, txtTimer,  txtTotalTimer;
    LinearLayout btnPreviousQuestion,btnNextQuestion,butSubmit;
    final Handler handler = new Handler();

    private EditText edtAnswer;
    private CountDownTimer countDownTimer;

    private String[] questions;
    private int currentQuestionIndex = 0;
    GridLayout gridLayout;
    private int savedQuestionCount = 0;
    private long interval = 1000;
    private LinearLayout leftIcon,rightIcon;

    private boolean timerRunning = false;
    private boolean totalTimerRunning= false;
    private long currentTime = 0;
    private long currentTimeOnSaveAndNext = 0;
    private List<Boolean> isQuestionAnswered = new ArrayList<>();
    private List<String> answers ;
    private List<Long> questionTimes ;
    private List<Boolean> isQuestionAttempted = new ArrayList<>();
    private List<Boolean> isQuestionCorrect = new ArrayList<>();
    private ArrayList<String> stringIsQuestionAttempted;
    private int attemptedQuestions = 0;
    private int notAttemptedQuestions = 0;

    ArrayList<String> originalAnswers = new ArrayList<>();
    private List<CountDownTimer> questionTimers ;
    private int currentStep = 0;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_level);
        textViewQuestion = findViewById(R.id.textViewQuestion);
        txtDisplayQuestion = findViewById(R.id.textQuestion);
        txtTimer = findViewById(R.id.timerTextView);
        txtTotalTimer= findViewById(R.id.total_timer_display_id);
        btnNextQuestion = findViewById(R.id.btnNext);
        btnPreviousQuestion = findViewById(R.id.prv_qus);
        edtAnswer = findViewById(R.id.answerEditText);
        gridLayout = findViewById(R.id.gridLayoutButtons);
        butSubmit=findViewById(R.id.but_submit);
        leftIcon =findViewById(R.id.left_icon_click);
        rightIcon =findViewById(R.id.right_icon_click);

        // Initialize the TextView


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
        // handler.removeCallbacks(runnable);
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
                handler.removeCallbacks(runnable);
                saveTimerState();
                showCurrentQuestion();
                edtAnswer.getText().clear();
                restoreTimerState();
                showCompletionPopup();
            }
        });

        questions = getResources().getStringArray(R.array.questions_array);
        //isQuestionAnswered = new ArrayList<>(questions.length);
        answers = new ArrayList<>(Collections.nCopies(questions.length, ""));
        questionTimes = new ArrayList<>(Collections.nCopies(questions.length, 0L));
        questionTimers = new ArrayList<>();

        for (int i = 0; i < questions.length; i++) {
            questionTimers.add(createCountDownTimer(i));
        }

        for (int i = 0; i < questions.length; i++) {
            isQuestionAnswered.add(false);
        }

        for (String question : questions) {
            originalAnswers.add(generateOriginalAnswer(question));
        }

        currentStep = Math.max(0,Math.min(currentStep,questions.length - 1));

//        leftIcon.setOnClickListener(new View.OnClickListener() {
//
//
//            @Override
//            public void onClick(View view) {
//                Touch horizontalScrollView = null;
////                horizontalScrollView.scrollTo(10, y);
//            }
//        });

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



        // Display the first question
        showCurrentQuestion();
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

    private void showCurrentQuestion() {
        if (currentQuestionIndex >= 0 && currentQuestionIndex < questions.length) {
            textViewQuestion.setText("Question " + (currentQuestionIndex + 1) +"/"+"20"+ ":");

//            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) txtDisplayQuestion.getLayoutParams();
//            params.setMargins(96, 32, 96, 32);
//            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            txtDisplayQuestion.setGravity(Gravity.CENTER_VERTICAL);
            txtDisplayQuestion.setGravity(Gravity.CENTER_HORIZONTAL);
            txtDisplayQuestion.setText(questions[currentQuestionIndex].replace(" ", "\n"));
//            txtDisplayQuestion.setBackgroundColor(ContextCompat.getColor(this, R.color.unansweredButtonColor));
            generateButtons();
        } else {
            showCompletionPopup();
        }

    }

//    private void onPreviousButtonClick(View view) {
//        stopTimer();
//
//        // Reset the timer to the saved time
//        saveTimerState();
//
//            currentQuestionIndex--;
//            if (currentQuestionIndex >= 0 && currentQuestionIndex < questions.length) {
//                showCurrentQuestion();
//                if (!answers.isEmpty() && currentQuestionIndex < answers.size()) {
//                    String storedAnswer = answers.get(currentQuestionIndex);
//                    edtAnswer.setText(storedAnswer);
//                }else {
//                    // Handle the case where answers list is empty or index is out of bounds
//                    edtAnswer.getText().clear();
//                }
//
//            } else {
//                Toast.makeText(FirstLevelActivity.this, "No previous questions available", Toast.LENGTH_SHORT).show();
//            }
//
//            restoreTimerState(); // Restore timer state for the previous question
//            startTimer();
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

        if (currentQuestionIndex >= 0 && currentQuestionIndex < questions.length) {
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

            if (originalAnswers != null && currentQuestionIndex < originalAnswers.size()) {
                boolean correctAnswer = enteredAnswer.equals(originalAnswers.get(currentQuestionIndex));
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
            showCurrentQuestion();

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
            Toast.makeText(FirstLevelActivity.this, "No previous questions available", Toast.LENGTH_SHORT).show();
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

    private void startTotalTimer(){
        totalTimerRunning=true;

    }

    private void stopTotalTimer(){
        totalTimerRunning=true;

    }

    private void onSaveAndNextButtonClick(View view) {

        stopTimer();
        saveTimerState();
        String ans = edtAnswer.getText().toString();
        boolean isEmptyAnswer = ans.isEmpty();

        if (!isEmptyAnswer) {
            answers.set(currentQuestionIndex, ans);
            questionTimes.set(currentQuestionIndex, currentTime);
        }

        if (currentQuestionIndex >= 0 && currentQuestionIndex < questions.length) {
            String enteredAnswer = edtAnswer.getText().toString();
            answers.add(enteredAnswer);

            Log.e("DebugTag", "Index: " + currentQuestionIndex);
            Log.e("DebugTag", "Entered Answer: " + enteredAnswer);

            boolean attempted = !enteredAnswer.isEmpty();
            isQuestionAttempted.add(attempted);

            if (originalAnswers != null && currentQuestionIndex < originalAnswers.size()) {
                boolean correctAnswer = enteredAnswer.equals(originalAnswers.get(currentQuestionIndex));
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

        if (currentQuestionIndex < questions.length - 1) {
            currentQuestionIndex++;
            currentStep = currentQuestionIndex;
            showCurrentQuestion();
            edtAnswer.getText().clear();
            currentTime = questionTimes.get(currentQuestionIndex);

            restoreTimerState();
            startTimer();
        } else {
            Toast.makeText(FirstLevelActivity.this, "Level is Completed", Toast.LENGTH_SHORT).show();
            showCompletionPopup();
        }
    }

   private void showCompletionPopup() {
        stopTimer();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Level-1 Completed");
        builder.setMessage("Are you Sure want to Submit Play with Number Game.Your not able to modify any thing after submiting...");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                showReportActivity();
//
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        builder.setCancelable(false);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showReportActivity() {
        JSONObject jsonData = new JSONObject();
        try {
            jsonData.put("questions", new JSONArray(Arrays.asList(questions)));
            jsonData.put("enteredAnswers", new JSONArray(answers));
            jsonData.put("isQuestionAttempted", new JSONArray(convertBooleanListToStringList(isQuestionAttempted)));
            jsonData.put("isQuestionCorrect", new JSONArray(convertBooleanListToStringList(isQuestionCorrect)));

            JSONArray originalAnswersArray = new JSONArray();
            for (String question : questions) {
                originalAnswersArray.put(generateOriginalAnswer(question));
            }
            jsonData.put("originalAnswers", originalAnswersArray);

            JSONArray questionTimesArray = new JSONArray();
            for (Long time : questionTimes) {
                questionTimesArray.put(time);
            }
            jsonData.put("questionTimes", questionTimesArray);

            JSONArray jsonArray=new JSONArray();
            jsonArray.put(jsonData);

            ReportMethod();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void ReportMethod() {
                Toast.makeText(FirstLevelActivity.this,"All Questions are Submited",Toast.LENGTH_LONG).show();
                ArrayList<String> stringIsQuestionAttempted = convertBooleanListToStringList(isQuestionAttempted);
                ArrayList<String> stringIsQuestionCorrect = convertBooleanListToStringList(isQuestionCorrect);
                Intent intent = new Intent(FirstLevelActivity.this, FirstLevelResultActivity.class);
                intent.putStringArrayListExtra("questions", new ArrayList<>(Arrays.asList(questions)));
                intent.putStringArrayListExtra("enteredAnswers", new ArrayList<>(answers));
                intent.putStringArrayListExtra("isQuestionAttempted", stringIsQuestionAttempted);
                intent.putStringArrayListExtra("isQuestionCorrect", stringIsQuestionCorrect);
                for (String question : questions) {
                    originalAnswers.add(generateOriginalAnswer(question));
                }
                intent.putStringArrayListExtra("originalAnswers", originalAnswers);


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

    private String generateOriginalAnswer(String question) {
        String[] originalAnswers = {
                "5","6","5","8","0","8","9","8","4","9",
                "5","6","3","1","7","1","1","16","14","11"
        };

        // Find the index of the current question in the array
        int questionIndex = Arrays.asList(questions).indexOf(question);

        // Check if the question index is valid
        if (questionIndex >= 0 && questionIndex < originalAnswers.length) {
            return originalAnswers[questionIndex];
        } else {
            // Return a default value or handle the case where the index is out of bounds
            return "";
        }
    }


    /*private void generateButtons() {
        gridLayout.removeAllViews();

        int marginLeftInDp = getResources().getDimensionPixelSize(R.dimen.button_margin_left);
        int marginRightInDp = getResources().getDimensionPixelSize(R.dimen.button_margin_right);
        int marginTopInDp = getResources().getDimensionPixelSize(R.dimen.button_margin_top);
        int marginBottomInDp = getResources().getDimensionPixelSize(R.dimen.button_margin_bottom);

        // Create a button for each question
        for (int i = 0; i < questions.length; i++) {
            Button button = new Button(this);
            button.setText(String.valueOf(i + 1));
            button.setTag(i);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int clickedButtonTag = (int) view.getTag();
                    onButtonClicked(clickedButtonTag);
                }
            });

            // Set layout parameters for the button in the grid
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 0; // This will make buttons equally distribute in columns
            params.height = GridLayout.LayoutParams.WRAP_CONTENT;
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f); // Equally distribute columns
            params.rowSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f); // Equally distribute rows
            // Set margins for the button
            params.leftMargin = marginLeftInDp;
            params.rightMargin = marginRightInDp;
            params.topMargin = marginTopInDp;
            params.bottomMargin = marginBottomInDp;

            button.setLayoutParams(params);

            if (isQuestionAnswered.size() > i && isQuestionAnswered.get(i)) {
                button.setBackgroundColor(getResources().getColor(R.color.answeredButtonColor));
            } else {
                button.setBackgroundColor(getResources().getColor(R.color.unansweredButtonColor));
            }
            // Add the button to the layout
            gridLayout.addView(button);
        }
    }*/

    private void generateButtons() {
        gridLayout.removeAllViews();

        int totalSteps = questions.length; // Total steps (buttons)
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

    // Helper method to convert dp to px
    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }
//    private void onButtonClicked(int tag) {
//        // Handle button click, update currentQuestionIndex, and display the question
//        saveTimerState();
//        currentQuestionIndex = tag;
//        Log.e("Reddy", "Button Clicked - Index: " + tag);
//
//        // Iterate through GridLayout children and update button backgrounds
//        for (int i = 0; i < gridLayout.getChildCount(); i++) {
//            View child = gridLayout.getChildAt(i);
//
//            if (child instanceof Button) { // Check if the child is a button
//                int stepIndex = (int) child.getTag();
//
//                // Update the background color based on the step index
//                if (stepIndex == currentQuestionIndex) {
//                    child.setBackground(getDrawable(R.drawable.circle_orange)); // Current step
//                    child.invalidate(); // Force redraw
//                } else if (isQuestionAnswered.size() > stepIndex && isQuestionAnswered.get(stepIndex)) {
//                    child.setBackground(getDrawable(R.drawable.circle_green)); // Answered
//                } else {
//                    child.setBackground(getDrawable(R.drawable.circle_gray)); // Unanswered
//                }
//            }
//        }
//
//        // Display the selected question and restore its answer
//        showCurrentQuestion();
//        String storedAnswer = answers.get(currentQuestionIndex);
//        edtAnswer.setText(storedAnswer);
//        restoreTimerState();
//
//        // Invalidate GridLayout to ensure changes are visible
//        gridLayout.invalidate();
//    }
private void onButtonClicked(int tag) {
    // Stop the current timer before changing the question
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

    if (originalAnswers != null && currentQuestionIndex < originalAnswers.size()) {
        boolean correctAnswer = enteredAnswer.equals(originalAnswers.get(currentQuestionIndex));
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
    showCurrentQuestion();
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



    private void saveTimerState() {
        if (currentQuestionIndex >= 0 && currentQuestionIndex < questionTimes.size()) {
            questionTimes.set(currentQuestionIndex, currentTime);
        }
    }
    private void restoreTimerState() {

        if (currentQuestionIndex >= 0 && currentQuestionIndex < questionTimes.size()) {
            currentTime = questionTimes.get(currentQuestionIndex);
            // Update UI with the restored timer
            txtTimer.setText("Time Spent: " + currentTime / 1000 + " sec");
        }
    }
    private void startTimerForQuestion(int questionIndex) {
        questionTimers.get(questionIndex).start();
    }

    private void stopTimerForQuestion(int questionIndex) {
        questionTimers.get(questionIndex).cancel();
    }

    @Override
    public void onBackPressed() {
        showExitConfirmationDialog();
    }

    private void showExitConfirmationDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(FirstLevelActivity.this);
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