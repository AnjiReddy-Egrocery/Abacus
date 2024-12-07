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
    private Button btnNextQuestion, btnPreviousQuestion,butSubmit;
    private TextView textViewQuestion, txtDisplayQuestion, txtTimer;

    private EditText edtAnswer;

    private int currentQuestionIndex = 0;
    private List<String> questions;
    private List<String> coreectedAnswers;
    private List<String> answers ;
    private List<Long> questionTimes ;

    private LinearLayout layoutData;

    private CountDownTimer countDownTimer;
    private long currentTime = 0;
    private long interval = 1000;

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
        Bundle extras = getIntent().getExtras();
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
                    txtTimer.setText("Countdown: " + seconds + " sec");
                }
            }

            @Override
            public void onFinish() {
                // Timer will never finish, as it's set to Long.MAX_VALUE
            }
        };
    }

    private void onPreviousButtonClick(View view) {
        stopTimer();

        // Reset the timer to the saved time
        saveTimerState();

        currentQuestionIndex--;
        if (currentQuestionIndex >= 0 && currentQuestionIndex < questions.size()) {
            displayQuestion();
            String storedAnswer = answers.get(currentQuestionIndex);
            edtAnswer.setText(storedAnswer);
        } else {
            Toast.makeText(QuizActivity.this, "No previous questions available", Toast.LENGTH_SHORT).show();
        }
        restoreTimerState(); // Restore timer state for the previous question
        startTimer();


    }

    private void onSaveAndNextButtonClick(View view) {
        stopTimer();
        saveTimerState();
        String enteredAns = edtAnswer.getText().toString();
        boolean isEmptyAnswer = enteredAns.isEmpty();
        if (!isEmptyAnswer) {
            answers.set(currentQuestionIndex, enteredAns);
            questionTimes.set(currentQuestionIndex,currentTime);
            int currentQuestionCorrectness = enteredAns.equals(coreectedAnswers.get(currentQuestionIndex)) ? 1 : 0;
            isCorrected = String.valueOf(currentQuestionCorrectness);
            status = String.valueOf(currentQuestionCorrectness);
            quizData.add(new QuizData(txtDisplayQuestion.getText().toString(), enteredAns, coreectedAnswers.get(currentQuestionIndex), isCorrected, status, currentTime / 1000));

            // Log the value of isCorrected
            Log.e("Reddy", "isCorrected for Question " + (currentQuestionIndex + 1) + ": " + isCorrected);
        }

        // Check if there are more questions
        if (questions != null && !questions.isEmpty()) {
            // Display the next question
            String enteredAnswer = edtAnswer.getText().toString();
            answers.add(enteredAnswer);

            boolean attempted = !enteredAnswer.isEmpty();
            isQuestionAttempted.add(attempted);

            // Check if the answer is correct
            boolean correctAnswer = enteredAnswer.equals(coreectedAnswers.get(currentQuestionIndex));
            isQuestionCorrect.add(correctAnswer);

            if (!enteredAnswer.isEmpty()) {
                // Set the background color of the clicked button to indicate it's answered
                int clickedButtonIndex = currentQuestionIndex;
                if (clickedButtonIndex >= 0 && clickedButtonIndex < gridLayout.getChildCount()) {
                    Button clickedButton = (Button) gridLayout.getChildAt(clickedButtonIndex);
                    clickedButton.setBackgroundColor(getResources().getColor(R.color.answeredButtonColor));
                    isQuestionAnswered.set(clickedButtonIndex, true);
                }
            }
        }

        if (currentQuestionIndex < questions.size()-1) {
            if (currentQuestionIndex < questions.size()) {
                currentQuestionIndex++;
                displayQuestion();
                edtAnswer.getText().clear();
                currentTime = questionTimes.get(currentQuestionIndex);

                // Update UI with the timer
                restoreTimerState(); // Restore timer state for the next question
                startTimer();
            }
        }
         else {
            Toast.makeText(QuizActivity.this, "Quiz Completed", Toast.LENGTH_SHORT).show();
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
            textViewQuestion.setText("Question " + (currentQuestionIndex + 1) + ": " );
            txtDisplayQuestion.setGravity(Gravity.RIGHT);
            txtDisplayQuestion.setText( questions.get(currentQuestionIndex));
            generateButtons();

            Log.e("DebugTag", "isQuestionAnswered: " + isQuestionAnswered.toString());
        } else {
            Log.e("DisplayQuestion", "No questions available or index out of bounds");
        }
    }
    private void generateButtons() {
        gridLayout.removeAllViews();

        int marginLeftInDp = getResources().getDimensionPixelSize(R.dimen.button_margin_left);
        int marginRightInDp = getResources().getDimensionPixelSize(R.dimen.button_margin_right);
        int marginTopInDp = getResources().getDimensionPixelSize(R.dimen.button_margin_top);
        int marginBottomInDp = getResources().getDimensionPixelSize(R.dimen.button_margin_bottom);

        // Create a button for each question
        for (int i = 0; i < questions.size(); i++) {
            Button button = new Button(this);
            button.setText(String.valueOf(i + 1));
            button.setTag(i);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onButtonClicked((int) view.getTag());
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
    private void onButtonClicked(int questionIndex) {
        // Handle button click, update currentQuestionIndex, and display the question
        saveTimerState();

        currentQuestionIndex = questionIndex;
        Log.e("Reddy", "Button Clicked - Index: " + questionIndex);
        displayQuestion();
        String storedAnswer = answers.get(currentQuestionIndex);
        edtAnswer.setText(storedAnswer);
        restoreTimerState();
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
        txtTimer.setText("Countdown: " + currentTime / 1000 + " sec");
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