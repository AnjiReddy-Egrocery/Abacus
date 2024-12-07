package com.dst.abacustrainner.Activity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
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

public class FirstLevelActivity extends AppCompatActivity {
    private Button btnNextQuestion, btnPreviousQuestion,butSubmit;
    private TextView textViewQuestion, txtDisplayQuestion, txtTimer;

    private EditText edtAnswer;
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
    private int attemptedQuestions = 0;
    private int notAttemptedQuestions = 0;

    ArrayList<String> originalAnswers = new ArrayList<>();
    private List<CountDownTimer> questionTimers ;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_level);

        textViewQuestion = findViewById(R.id.textViewQuestion);
        txtDisplayQuestion = findViewById(R.id.textQuestion);
        txtTimer = findViewById(R.id.timerTextView);
        btnNextQuestion = findViewById(R.id.btnNext);
        btnPreviousQuestion = findViewById(R.id.prv_qus);
        edtAnswer = findViewById(R.id.answerEditText);
        gridLayout = findViewById(R.id.gridLayoutButtons);
        butSubmit=findViewById(R.id.but_submit);
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
        if (currentQuestionIndex >= 0 && currentQuestionIndex < questions.length) {
            textViewQuestion.setText("Question " + (currentQuestionIndex + 1) + ":");
            txtDisplayQuestion.setGravity(Gravity.RIGHT);
            txtDisplayQuestion.setText(questions[currentQuestionIndex].replace(" ", "\n"));
            generateButtons();
        } else {

            showCompletionPopup();
        }

    }

    private void onPreviousButtonClick(View view) {
        stopTimer();

        // Reset the timer to the saved time
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

            } else {
                Toast.makeText(FirstLevelActivity.this, "No previous questions available", Toast.LENGTH_SHORT).show();
            }

            restoreTimerState(); // Restore timer state for the previous question
            startTimer();
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

    private void onSaveAndNextButtonClick(View view) {
        stopTimer();
        saveTimerState();
        String ans = edtAnswer.getText().toString();
        boolean isEmptyAnswer = ans.isEmpty();
        if (!isEmptyAnswer){
            answers.set(currentQuestionIndex, ans);
            questionTimes.set(currentQuestionIndex,currentTime);
        }
        // Check if there are more questions
        if (currentQuestionIndex >= 0 && currentQuestionIndex < questions.length) {
            // Display the next question
            String enteredAnswer = edtAnswer.getText().toString();
            answers.add(enteredAnswer);

            Log.e("DebugTag", "Index: " + currentQuestionIndex);
            Log.e("DebugTag", "Entered Answer: " + enteredAnswer);

            boolean attempted = !enteredAnswer.isEmpty();
            isQuestionAttempted.add(attempted);

            if (originalAnswers != null && currentQuestionIndex < originalAnswers.size()) {
                boolean correctAnswer = enteredAnswer.equals(originalAnswers.get(currentQuestionIndex));
                Log.e("DebugTag", "Correct Answer Comparison: " + correctAnswer);
                isQuestionCorrect.add(correctAnswer);
            } else {
                // Handle the case where the originalAnswers list is null or the index is out of bounds
                // Add a default value or set isQuestionCorrect to false for this question
                Log.e("DebugTag", "Error: originalAnswers is null or index out of bounds");
                isQuestionCorrect.add(false);  // Assuming a default value of false for incorrect
                // You might want to adjust this based on your application's logic
            }
            if (!enteredAnswer.isEmpty()) {
                int clickedButtonIndex = currentQuestionIndex;
                if (clickedButtonIndex >= 0 && clickedButtonIndex < gridLayout.getChildCount()) {
                    Button clickedButton = (Button) gridLayout.getChildAt(clickedButtonIndex);
                    clickedButton.setBackgroundColor(getResources().getColor(R.color.answeredButtonColor));
                    isQuestionAnswered.set(clickedButtonIndex, true);
                }
            }
        }

        if (currentQuestionIndex < questions.length-1) {
            if (currentQuestionIndex < questions.length) {
                currentQuestionIndex++;
                showCurrentQuestion();
                edtAnswer.getText().clear();
                currentTime = questionTimes.get(currentQuestionIndex);

                // Update UI with the timer
                restoreTimerState(); // Restore timer state for the next question
                startTimer();
            }
        }
       else {
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


    private void generateButtons() {
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
    }
    private void onButtonClicked(int tag) {
        // Handle button click, update currentQuestionIndex, and display the question
        saveTimerState();

        currentQuestionIndex = tag;
        Log.e("Reddy", "Button Clicked - Index: " + tag);
        showCurrentQuestion();
        String storedAnswer = answers.get(currentQuestionIndex);
        edtAnswer.setText(storedAnswer);
        restoreTimerState();
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
            txtTimer.setText("Countdown: " + currentTime / 1000 + " sec");
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