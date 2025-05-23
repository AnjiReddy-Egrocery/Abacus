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
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;


import com.dst.abacustrainner.R;
import com.dst.abacustrainner.database.ParcelableLong;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class VisualizationSecondLevelActivity extends AppCompatActivity  {
    LinearLayout btnPreviousQuestion,btnNextQuestion,butSubmit;
    ImageView imageView;
    private TextView textViewQuestion, txtDisplayQuestion, txtTimer;

    private EditText edtAnswer;
    private int currentStep = 0;

    private CountDownTimer countDownTimer;
    private String[] questions;
    private int currentQuestionIndex = 0;
    GridLayout gridLayout;
    private int savedQuestionCount = 0;

    private long interval = 1000;

    private boolean timerRunning = false;
    private long currentTime = 0;
    private long currentTimeOnSaveAndNext = 0;
    private List<Boolean> isQuestionAnswered = new ArrayList<>();
    private List<String> answers ;
    private List<Long> questionTimes ;

    private List<Boolean> isQuestionAttempted = new ArrayList<>();
    private List<Boolean> isQuestionCorrect = new ArrayList<>();

    private ArrayList<String> stringIsQuestionAttempted;

    ArrayList<String> originalAnswers = new ArrayList<>();
    private TextToSpeech textToSpeech;
    private long lastQuestionReadTime = 0;
    String currentNumber;

    private boolean isComponentsEnabled = false;

    private static final int REQ_CODE_SPEECH_INPUT = 100;
    private boolean isLastAnswerMessageDisplayed = false;

    int count = 0;
    LinearLayout linearRepeat;
    private boolean isFirstRepetition = true;

    private boolean isFirstTimeQuestionDisplay = true;
    private boolean isAnswerDisplayed = false;
    private List<CountDownTimer> questionTimers ;



    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualization_second_level);

        textViewQuestion = findViewById(R.id.textViewQuestion);
        txtDisplayQuestion = findViewById(R.id.textQuestion);
        btnNextQuestion = findViewById(R.id.btnNext);
        btnPreviousQuestion = findViewById(R.id.prv_qus);
        txtTimer = findViewById(R.id.timerTextView);
        edtAnswer = findViewById(R.id.answerEditText);
        gridLayout = findViewById(R.id.gridLayoutButtons);
        linearRepeat = findViewById(R.id.layout_repeat);
        butSubmit=findViewById(R.id.but_submit);
        imageView= findViewById(R.id.imagespeaker);
        btnNextQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSaveAndNextButtonClick(view);
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                promptSpeechInput();
            }
        });
        btnPreviousQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onPreviousButtonClick(view);
            }
        });
        linearRepeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                repeatCurrentQuestion();
            }
        });
        butSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveTimerState();

                showCompletionPopup();
            }
        });
        questions = getResources().getStringArray(R.array.visualiztion_second_array);
        Log.e("Reddy","Questions"+questions);
//        isQuestionAnswered = new ArrayList<>(questions.length);
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

        disableComponents();

        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    // TextToSpeech initialization successful, proceed to display and speak the first question
                    showCurrentQuestion();
                } else {
                    // Handle the case where TextToSpeech initialization fails
                    Log.e("TextToSpeech", "Initialization failed");
                    // You might want to show an error message or take appropriate action here
                }
            }
        });


    }

    private void repeatCurrentQuestion() {
        stopTimer();


        // Display the current question
        showCurrentQuestion();
        disableComponents();
        // Start the timer
        startTimer();

        isFirstRepetition = false;
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
                    long seconds = currentTime / 2000;
                    txtTimer.setText("Countdown: " + seconds + " sec");
                }
            }

            @Override
            public void onFinish() {
                // Timer will never finish, as it's set to Long.MAX_VALUE
            }
        };
    }
    private void showCurrentQuestion() {
        int questionNumber = currentQuestionIndex + 1;
        textViewQuestion.setText("Question " + questionNumber + ":");
        txtDisplayQuestion.setGravity(Gravity.RIGHT);
        String[] numbers = questions[currentQuestionIndex].split("\\s+");
        readNumbersAloud(numbers);
        generateButtons();
    }
    private void readNumbersAloud(String[] numbers) {
        int totalNumbers = numbers.length;

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
                        readQuestionAloud(currentNumber);

                        // Display the original question
                        txtDisplayQuestion.setText(currentNumber.replace(" ", "\n"));

                        if (finalI == totalNumbers - 1) {
                            // Delay for a short duration (adjust the milliseconds as needed)
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    // Find the last number in the currentNumber
                                    String[] numberParts = currentNumber.split("\\s+");
                                    if (numberParts.length > 0) {
                                        String lastNumber = numberParts[numberParts.length - 1];

                                        // Replace the last number with " Answer is " in the currentNumber
                                        currentNumber = currentNumber.replace(lastNumber, "Answer \t\t is");

                                        // Set the modified current number to txtDisplayQuestion
                                        txtDisplayQuestion.setText(currentNumber.replace(" ", ""));
                                        txtTimer.setVisibility(View.VISIBLE);
                                        edtAnswer.requestFocus();

                                        // Show the keyboard
                                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                        if (imm != null) {
                                            imm.showSoftInput(edtAnswer, InputMethodManager.SHOW_IMPLICIT);
                                        }
                                        startTimer();
                                        // Read the modified question aloud
                                        readQuestionAloud(currentNumber);
                                        enableComponents();


                                    }
                                }
                            }, 2000);   // Adjust the delay time as needed (2 seconds in this example)
                        }
                    }
                }

            }.start();

        }

    }



    private void enableComponents() {
        btnNextQuestion.setEnabled(true);
        btnPreviousQuestion.setEnabled(true);
        edtAnswer.setEnabled(true);
        imageView.setEnabled(true);
        edtAnswer.requestFocus();

        // Show the keyboard
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(edtAnswer, InputMethodManager.SHOW_IMPLICIT);
        }
        linearRepeat.setVisibility(View.VISIBLE);
        isComponentsEnabled = true;
        isAnswerDisplayed = true;
        butSubmit.setEnabled(true);
    }

    private void disableComponents() {
        // Disable components
        btnNextQuestion.setEnabled(false);
        btnPreviousQuestion.setEnabled(false);
        edtAnswer.setEnabled(false);
        imageView.setEnabled(false);
        linearRepeat.setVisibility(View.INVISIBLE);
        isComponentsEnabled = false;
        isAnswerDisplayed = false;
        butSubmit.setEnabled(false);
    }

    private void onPreviousButtonClick(View view) {
        stopTimer();
        saveTimerState();


        currentQuestionIndex--;

        if (currentQuestionIndex >= 0 && currentQuestionIndex < questions.length) {
            showCurrentQuestion();
            if (!answers.isEmpty() && currentQuestionIndex < answers.size()) {
                String storedAnswer = answers.get(currentQuestionIndex);
                edtAnswer.setText(storedAnswer);
            }else {
                // Handle the case where answers list is empty or index is out of bounds
                edtAnswer.getText().clear();
            }
            disableComponents();
            restoreTimerState();
            startTimer(); // Start the timer for the previous question
        } else {
            Toast.makeText(VisualizationSecondLevelActivity.this, "No previous questions available", Toast.LENGTH_SHORT).show();
        }
        txtTimer.setVisibility(View.VISIBLE);

    }

    private void restoreTimerState() {
        if (currentQuestionIndex >= 0 && currentQuestionIndex < questionTimes.size()) {
            currentTime = questionTimes.get(currentQuestionIndex);

            // Update UI with the restored timer
            if (currentTime == 0) {
                // If the timer is zero, make txtTimer invisible
                txtTimer.setVisibility(View.INVISIBLE);
                txtTimer.setText("Countdown: 0 sec");
            } else {
                // If the timer is not zero, make txtTimer visible
                txtTimer.setVisibility(View.VISIBLE);
                txtTimer.setText("Countdown: " + currentTime / 1000 + " sec");
                startTimer(); // Start the timer for the next question
            }
        } else {
            // Handle the case where currentQuestionIndex is out of bounds
            Log.e("RestoreTimerState", "Invalid currentQuestionIndex: " + currentQuestionIndex);
        }
    }

    private void saveTimerState() {
        if (currentQuestionIndex >= 0 && currentQuestionIndex < questionTimes.size()) {
            questionTimes.set(currentQuestionIndex, currentTime);
        } else {
            // Handle the case where currentQuestionIndex is out of bounds
            Log.e("SaveTimerState", "Invalid currentQuestionIndex: " + currentQuestionIndex);
        }
        // txtTimer.setVisibility(View.VISIBLE);
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


    private void onSaveAndNextButtonClick(View view) {

        stopTimer();
        saveTimerState();
        disableComponents();
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
            Toast.makeText(VisualizationSecondLevelActivity.this, "Level is Completed", Toast.LENGTH_SHORT).show();
            showCompletionPopup();
        }
    }
    private void showCompletionPopup() {
        stopTimer();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Level-2 Completed");
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
        ArrayList<String> stringIsQuestionAttempted = convertBooleanListToStringList(isQuestionAttempted);
        ArrayList<String> stringIsQuestionCorrect = convertBooleanListToStringList(isQuestionCorrect);
        Intent intent = new Intent(VisualizationSecondLevelActivity.this, VisualizationSecondLevelResultActivity.class);
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
        String[] originalAnswersArray = {
                "5", "5", "4", "0", "5", "0", "2", "17", "10", "79",
                "25", "15", "22", "10", "33", "12", "78", "126", "172", "88"
        };

        // Find the index of the current question in the array
        int questionIndex = Arrays.asList(questions).indexOf(question);

        // Check if the question index is valid
        if (questionIndex >= 0 && questionIndex < originalAnswersArray.length) {
            return originalAnswersArray[questionIndex];
        } else {
            // Return a default value or handle the case where the index is out of bounds
            return "";
        }
    }

    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }
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
    private void onButtonClicked(int tag) {
        if (isAnswerDisplayed) {
            stopTimer();

            saveTimerState();

            currentQuestionIndex = tag;
            showCurrentQuestion();
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
        }
    }
    private void readQuestionAloud(String textToRead) {
        String[] elements = textToRead.split("\\s+");

        StringBuilder finalText = new StringBuilder();
        for (String element : elements) {
            if (element.startsWith("+")) {
                // Positive number, add "plus" before the number
                finalText.append("plus ").append(element.substring(1)).append(" ");
            } else if (element.startsWith("-")) {
                // Negative number, add "minus" before the number
                finalText.append("minus ").append(element.substring(1)).append(" ");
            } else {
                // Not a signed number, keep it as it is
                finalText.append(element).append(" ");
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            textToSpeech.speak(finalText.toString().trim(), TextToSpeech.QUEUE_FLUSH, null, null);
        } else {
            textToSpeech.speak(finalText.toString().trim(), TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    // Add onDestroy method to release TextToSpeech resources
    @Override
    protected void onDestroy() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        showExitConfirmationDialog();
    }

    private void showExitConfirmationDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(VisualizationSecondLevelActivity.this);
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