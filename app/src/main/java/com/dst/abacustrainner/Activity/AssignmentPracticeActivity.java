package com.dst.abacustrainner.Activity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.text.HtmlCompat;


import com.dst.abacustrainner.Model.AssignmentExamResponse;
import com.dst.abacustrainner.Model.AssignmentSubmitDataResponse;
import com.dst.abacustrainner.Model.SendData;
import com.dst.abacustrainner.R;
import com.dst.abacustrainner.Services.ApiClient;
import com.dst.abacustrainner.database.ParcelableLong;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
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

public class AssignmentPracticeActivity extends AppCompatActivity {
    Button butPreviousQuestion,butSave,butSubmit;
    TextView txtTimer,questionTextView,txtTopicName,txtdisplayquestion;
    private EditText answerEditText;
    private int currentQuestionIndex = 0;

    private CountDownTimer countDownTimer;
    private long currentTime = 0;
    private long interval = 1000;

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

    private ArrayList<Long> questionTimes ;

    String startedDate;
    String originalAnswer;
    private int correctAnswers = 0;
    private int wrongAnswers = 0;

    private int attemptedQuestions = 0;
    private int notAttemptedQuestions = 0;

    private List<Boolean> isQuestionAttempted = new ArrayList<>();
    private List<Boolean> isQuestionCorrect = new ArrayList<>();

    private long currentTimeOnSaveAndNext = 0;

    List<SendData> listData;
    private List<CountDownTimer> questionTimers ;

    private static final int MAX_QUESTIONS = 30;

    long seconds;



    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assignment_practice);

        butPreviousQuestion=findViewById(R.id.prv_qus);
        questionTextView = findViewById(R.id.questionTextView);
        answerEditText = findViewById(R.id.answerEditText);
        txtTimer=findViewById(R.id.timerTextView);
        butSave=findViewById(R.id.but_save);
        butSubmit=findViewById(R.id.but_submit);
        imageLeft=findViewById(R.id.leftArrow);
        imageRight=findViewById(R.id.rightArrow);
        gridLayout=findViewById(R.id.grid_layout);
        txtTopicName=findViewById(R.id.topic_name);
        txtdisplayquestion=findViewById(R.id.displaytextview);

        Bundle bundle=getIntent().getExtras();

        studentid=bundle.getString("studentId");
        topicid=bundle.getString("topicId");
        topicName=bundle.getString("topicName");
        studentName =bundle.getString("firstName");

        txtTopicName.setText(topicName);


        displayQuestion(currentQuestionIndex);
        questionTimers = new ArrayList<>();
        for (int i = 0; i < MAX_QUESTIONS; i++) {
            questionTimers.add(createCountDownTimer(i));
        }

        isQuestionAnswered = new ArrayList<>(30);
        enteredAnswers = new ArrayList<>(30);
        questionTimes = new ArrayList<>(30);
        listData = new ArrayList<>();
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

        butSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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

        butPreviousQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopTimer();

                // Reset the timer to the saved time
                saveTimerState();

                //txtTimer.setText("Countdown: 0 sec");
                navigateToPreviousQuestion();
                restoreTimerState(); // Restore timer state for the previous question
                startTimer();
            }

        });
        imageLeft.setOnClickListener(new View.OnClickListener() {
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
        });

        startTimer();
        VerifyMethod(studentid,topicid);

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

            // Display the question text with indentation and monospaced font
            txtdisplayquestion.setText("Question " + (currentQuestionIndex + 1) + ":\n\n");
            questionTextView.setText(questionHtml.replace("\n", "\n   "));

            // Set left margin for questionTextView
            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) questionTextView.getLayoutParams();
            layoutParams.leftMargin = (int) getResources().getDimension(R.dimen.question_margin_left);
            questionTextView.setLayoutParams(layoutParams);
            generateButtons();
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

        int marginLeftInDp = getResources().getDimensionPixelSize(R.dimen.button_margin_left);
        int marginRightInDp = getResources().getDimensionPixelSize(R.dimen.button_margin_right);
        int marginTopInDp = getResources().getDimensionPixelSize(R.dimen.button_margin_top);
        int marginBottomInDp = getResources().getDimensionPixelSize(R.dimen.button_margin_bottom);

        for (int i = 0; i < questionsArray.length; i++) {
            Button button = new Button(this);
            button.setText(String.valueOf(i + 1));
            button.setTag(i);

            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 0;
            params.height = GridLayout.LayoutParams.WRAP_CONTENT;
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            params.rowSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            params.leftMargin = marginLeftInDp;
            params.rightMargin = marginRightInDp;
            params.topMargin = marginTopInDp;
            params.bottomMargin = marginBottomInDp;

            button.setLayoutParams(params);

            // Check if isQuestionAnswered is not null and has a size greater than i
            if (isQuestionAnswered != null && isQuestionAnswered.size() > i && isQuestionAnswered.get(i)) {
                button.setBackgroundColor(getResources().getColor(R.color.answeredButtonColor));
            } else {
                button.setBackgroundColor(getResources().getColor(R.color.unansweredButtonColor));
            }

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int clickedButtonTag = (int) view.getTag();
                    onButtonClicked(clickedButtonTag);
                }
            });

            gridLayout.addView(button);

        }
    }


    private void onButtonClicked(int tag) {
        saveTimerState();
        currentQuestionIndex = tag;
        Log.e("Reddy","CurrentQuestion"+currentQuestionIndex);
        displayQuestion(currentQuestionIndex);
        String storedAnswer = enteredAnswers.get(currentQuestionIndex);
        answerEditText.setText(storedAnswer);
        restoreTimerState();
    }

    private void saveTimerState() {
        questionTimes.set(currentQuestionIndex, currentTime);
    }
    private void restoreTimerState() {

        currentTime = questionTimes.get(currentQuestionIndex);
        // Update UI with the restored timer
        txtTimer.setText("Countdown: " + currentTime / 1000 + " sec");
    }
    private void startTimerForQuestion(int questionIndex) {
        questionTimers.get(questionIndex).start();
    }

    private void stopTimerForQuestion(int questionIndex) {
        questionTimers.get(questionIndex).cancel();
    }

    private void VerifyMethod(String studentid, String topicid) {
       /* HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
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

        Call<AssignmentExamResponse> call=apiClient.assignmentexam(idPart,topicIdPart);
        call.enqueue(new Callback<AssignmentExamResponse>() {
            @Override
            public void onResponse(Call<AssignmentExamResponse> call, Response<AssignmentExamResponse> response) {
                if (response.isSuccessful()){
                    AssignmentExamResponse examResponse=response.body();
                    if (examResponse!=null){
                        AssignmentExamResponse.Result examResponseResult=examResponse.getResult();
                        examNum =examResponseResult.getExamRnm();
                        startedDate  = examResponseResult.getStartedOn();
                        String questionsListJsonString =examResponseResult.getQuestionsList();
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
                                    buttons = new Button[questionCount];
                                    //questionsArray = new String[jsonArray.length()];

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
            public void onFailure(Call<AssignmentExamResponse> call, Throwable t) {

            }
        });
    }

    private CountDownTimer createCountDownTimer(final int questionIndex) {
        final long smallerInterval = 500;
        return new CountDownTimer(Long.MAX_VALUE, interval) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (timerRunning) {
                    currentTime +=1000;
                    long seconds = currentTime / 1000;// Increase the time by 1 second
                    txtTimer.setText("Countdown: " + seconds  + " sec");
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
        AlertDialog.Builder dialog=new AlertDialog.Builder(AssignmentPracticeActivity.this);
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

    private void ResultMethod(String examNum, JSONArray jsonArray) {
        Log.e("Reddy","id"+examNum);
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
        Call<AssignmentSubmitDataResponse> call=apiClient.assignmentSubmitData(examNumPart,questionListPart);
       call.enqueue(new Callback<AssignmentSubmitDataResponse>() {
           @Override
           public void onResponse(Call<AssignmentSubmitDataResponse> call, Response<AssignmentSubmitDataResponse> response) {
               Log.e("Reddy","Response"+response);
               if (response.isSuccessful()){
                   AssignmentSubmitDataResponse assignmentSubmitDataResponse = response.body();
                   if (assignmentSubmitDataResponse != null){
                       Toast.makeText(AssignmentPracticeActivity.this,"All Questions are Submited",Toast.LENGTH_LONG).show();
                       ArrayList<String> stringIsQuestionAttempted = convertBooleanListToStringList(isQuestionAttempted);
                       Intent intent = new Intent(AssignmentPracticeActivity.this, AssignmentResultActivity.class);
                       intent.putExtra("topicName",topicName);
                       intent.putExtra("firstName",studentName);
                       intent.putExtra("startedOn",startedDate);
                       intent.putStringArrayListExtra("answers", new ArrayList<>(Arrays.asList(answerArray)));
                       intent.putStringArrayListExtra("questions", new ArrayList<>(Arrays.asList(questionsArray)));
                       intent.putStringArrayListExtra("enteredAnswers", enteredAnswers);
                       intent.putStringArrayListExtra("isQuestionAttempted", stringIsQuestionAttempted);

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
           public void onFailure(Call<AssignmentSubmitDataResponse> call, Throwable t) {

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
        AlertDialog.Builder dialog = new AlertDialog.Builder(AssignmentPracticeActivity.this);
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