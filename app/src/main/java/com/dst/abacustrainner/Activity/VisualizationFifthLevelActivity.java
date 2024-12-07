package com.dst.abacustrainner.Activity;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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

public class VisualizationFifthLevelActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {
    private Button btnNextQuestion, btnPreviousQuestion ,butSubmit;

    ImageView imageView;
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

    ArrayList<String> originalAnswers = new ArrayList<>();
    private TextToSpeech textToSpeech;
    private long lastQuestionReadTime = 0;
    private boolean isFirstQuestion = true;

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
        setContentView(R.layout.activity_visualization_fifth_level);

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
        questions = getResources().getStringArray(R.array.visualization_fifth_array);
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

        //startTimer();
        // Display the first question
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
        /*txtDisplayQuestion.setText(questions[currentQuestionIndex].replace(" ", "\n"));
        readQuestionAloud(questions[currentQuestionIndex]);*/
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
                            String currentNumber = numbers[finalI];

                            // Read each number separately
                            readQuestionAloud(currentNumber);

                            // Display each number
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

                                            // Modify the last number as needed (replace with " Answer is ")
                                            String modifiedNumber = currentNumber.replace(lastNumber, " Answer \t\t is ");

                                            // Set the modified current number to txtDisplayQuestion
                                            txtDisplayQuestion.setText(modifiedNumber.replace(" ", ""));
                                            txtTimer.setVisibility(View.VISIBLE);
                                            edtAnswer.requestFocus();

                                            // Show the keyboard
                                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                            if (imm != null) {
                                                imm.showSoftInput(edtAnswer, InputMethodManager.SHOW_IMPLICIT);
                                            }
                                            startTimer();

                                            // Read the modified question aloud
                                            readQuestionAloud(modifiedNumber);
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
            } else {
                // Handle the case where answers list is empty or index is out of bounds
                edtAnswer.getText().clear();
            }
            disableComponents();
            restoreTimerState();
            startTimer(); // Start the timer for the previous question
            txtTimer.setVisibility(View.VISIBLE);
        } else {
            Toast.makeText(VisualizationFifthLevelActivity.this, "No previous questions available", Toast.LENGTH_SHORT).show();
        }

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
                startTimer(); // Start the timer for the next question
            }
        }else {
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
        txtTimer.setVisibility(View.INVISIBLE);

        disableComponents();
        String ans=edtAnswer.getText().toString();
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

            boolean attempted = !enteredAnswer.isEmpty();
            isQuestionAttempted.add(attempted);

            // Check if the answer is correct
            if (!originalAnswers.isEmpty() && currentQuestionIndex < originalAnswers.size()) {
                boolean correctAnswer = enteredAnswer.equals(originalAnswers.get(currentQuestionIndex));
                isQuestionCorrect.add(correctAnswer);
            } else {
                // Handle the case where the originalAnswers list is empty or the index is out of bounds
                // You might want to display an error message, log a warning, or handle it according to your logic
                // Toast.makeText(FirstLevelActivity.this, "Error", Toast.LENGTH_SHORT).show();
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
            currentQuestionIndex++;
            showCurrentQuestion();
            edtAnswer.getText().clear();

            // Update UI with the timer
            restoreTimerState(); // Restore timer state for the next question
            if (currentTime == 0) {
                // If the timer was zero, start it for the next question
                currentTime = 0;
                txtTimer.setText("Countdown: 0 sec");
            }
        }
        else {
            Toast.makeText(VisualizationFifthLevelActivity.this, "Level is Completed", Toast.LENGTH_SHORT).show();
            // saveDataAndShowCompletionPopup();
            showCompletionPopup();
        }
    }
    private void showCompletionPopup() {
        stopTimer();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Level-5 Completed");
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
        Intent intent = new Intent(VisualizationFifthLevelActivity.this, VisualizationFifthLevelResultActivity.class);
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
                "335","175","95","216","150","396","315","1920","357","5369",
                "858","740","660","2303","1340","4500","4168","6232","4548","2608"
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
            Log.e("QuizActivity", "Before updating button background color: " + isQuestionAnswered.toString());
            button.setLayoutParams(params);
            Log.e("QuizActivity", "After updating button background color: " + isQuestionAnswered.toString());
            // Set background color based on whether the question is answered
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

    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int result = textToSpeech.setLanguage(Locale.getDefault());

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TextToSpeech", "Language not supported");
            } else {
                //showCurrentQuestion();
            }
        } else {
            Log.e("TextToSpeech", "Initialization failed");
        }
    }

    private void readQuestionAloud(String textToRead) {
        String[] elements = textToRead.split("\\s+");

        StringBuilder finalText = new StringBuilder();
        for (String element : elements) {
            if (element.startsWith("*")) {
                // Positive number, add "plus" before the number
                finalText.append("into ").append(element.substring(1)).append(" ");
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
        AlertDialog.Builder dialog = new AlertDialog.Builder(VisualizationFifthLevelActivity.this);
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