package com.dst.abacustrainner.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import com.dst.abacustrainner.R;
import com.dst.abacustrainner.User.HomeActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class VisualiztionActivity extends AppCompatActivity {
    private Spinner spinnerOperation, spinnerOperands, spinnerTotalQuestions,spinnerTimeInterval ,spinnerSelectedLevel;
    private LinearLayout dropdownContainer,btnBack;
    private Button butStartGame,butStartNumberGame;

    private List<String> questions;
    private String selectedTotalQuestions;


    String currentDate;

    private TextView dynamicTextView;

    private  TextView textViewTotalNumbers;
    List<String> correctAnswers;
    String selectedTimeInterval;

    String studentid="",studentName="";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualiztion);

        spinnerOperation = findViewById(R.id.spinnerOperation);
        spinnerOperands = findViewById(R.id.spinnerOperands);
        spinnerTotalQuestions = findViewById(R.id.spinnerTotalQuestions);
        dropdownContainer = findViewById(R.id.dropdownContainer);
        butStartGame = findViewById(R.id.btnStartPlay);
        spinnerOperands.setVisibility(View.VISIBLE);
        spinnerTimeInterval = findViewById(R.id.spinnerTimeInterval);
        textViewTotalNumbers = findViewById(R.id.txt_total_numbers);
        spinnerSelectedLevel = findViewById(R.id.spinnerSelectedLevel);
        butStartNumberGame = findViewById(R.id.but_start_game);
        btnBack=findViewById(R.id.btn_back_to_home_vis);

        Bundle bundle=getIntent().getExtras();
        studentid=bundle.getString("studentId");
        studentName = bundle.getString("firstName");

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(VisualiztionActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });

        butStartGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                onPlayButtonClick(view);
            }
        });
        spinnerSelectedLevel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String  selectedLevel = spinnerSelectedLevel.getSelectedItem().toString();
                Log.e("Reddy","Level"+selectedLevel);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        butStartNumberGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StartNumberGame(view);
            }
        });
        spinnerTimeInterval.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
               // selectedTimeInterval  = spinnerTimeInterval.getSelectedItem().toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        spinnerOperation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedOperation = spinnerOperation.getSelectedItem().toString();
                Log.e("Spinner", "Selected Operation: " + selectedOperation);
                onOperationChange();

                if ("Multiplication".equals(selectedOperation)) {
                    // If multiplication is selected, hide spinnerOperands
                    spinnerOperands.setVisibility(View.GONE);
                    textViewTotalNumbers.setVisibility(View.GONE);
                    createDynamicOperandSpinners(2);
                } else {
                    // For other operations, show spinnerOperands
                    spinnerOperands.setVisibility(View.VISIBLE);
                    textViewTotalNumbers.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing here
            }
        });
        spinnerOperands.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedOperand = spinnerOperands.getSelectedItem().toString();

                // Check if the selected item is a valid number
                try {
                    int selectedOperandCount = Integer.parseInt(selectedOperand);
                    createDynamicOperandSpinners(Integer.parseInt(selectedOperand));
                } catch (NumberFormatException e) {
                    // Handle the case where the selected item is not a valid number
                    Log.e("NumberFormatException", "Invalid number format: " + selectedOperand);
                }

            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing here
            }
        });
        spinnerTotalQuestions.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing here
            }
        });
    }

    private void StartNumberGame(View view) {
        String selectedLevel = spinnerSelectedLevel.getSelectedItem().toString();
        if (!"Select the Level".equals(selectedLevel)) {
            // Create an intent to start the appropriate LevelActivity based on the selected level

            Intent intents =new Intent(VisualiztionActivity.this, VisualizationFirstLevelActivity.class);
            int level = 0; // Default level value

            switch (selectedLevel) {
                case "Level-1":
                    level = 1;
                    break;
                case "Level-2":
                    level= 2;
                    break;
                case "Level-3":
                    level = 3;
                    break;
                // Add more cases for additional levels as needed
                case "Level-4":
                    level = 4;
                    break;
                case "Level-5":
                    level = 5;
                    break;
                default:
                    Toast.makeText(VisualiztionActivity.this, "Unexpected level selected", Toast.LENGTH_SHORT).show();
                    return;

            }

            intents.putExtra("level", level);
            startActivity(intents);

        } else {
            // Show a toast message indicating that the user needs to select a level
            Toast.makeText(VisualiztionActivity.this, "Please select a level", Toast.LENGTH_SHORT).show();
        }
    }

    private void createDynamicOperandSpinners(int numberOfSpinners) {
        dropdownContainer.removeAllViews(); // Clear previous spinners

        for (int i = 1; i <= numberOfSpinners; i++) {

            TextView textView = new TextView(this);
            textView.setText("Select " + i +"  Number Range(upto)");

            // Customize TextView properties
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18); // Set text size in scaled pixels
            textView.setPadding(10, 10, 10, 10); // Set padding in pixels
            textView.setTextColor(getResources().getColor(R.color.black));
            textView.setTypeface(null, Typeface.BOLD); // Set text style to bold
            textView.setGravity(Gravity.CENTER_VERTICAL); // Center text vertically
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            layoutParams.setMargins(
                    (int) getResources().getDimension(R.dimen.textview_left_margin),
                    0,
                    0,
                    0
            );
            textView.setLayoutParams(layoutParams);

            dropdownContainer.addView(textView);
            // Create Spinner with the same style and layout attributes as in XML
            Spinner spinner = new Spinner(this, null, android.R.attr.spinnerStyle);

            // Set layout parameters with fixed height and margins
            // (you can customize these parameters)
            LinearLayout.LayoutParams spinnerLayoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    (int) getResources().getDimension(R.dimen.spinner_height)
            );
            spinnerLayoutParams.setMargins(
                    (int) getResources().getDimension(R.dimen.spinner_left),
                    0,
                    (int) getResources().getDimension(R.dimen.spinner_right),
                    (int) getResources().getDimension(R.dimen.spinner_right)
            );
            spinner.setLayoutParams(spinnerLayoutParams);


            spinner.setPadding(10, 10, 10, 10);
            spinner.setBackground(getResources().getDrawable(R.drawable.spinner_bg));

            ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.operand_values));
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);


            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    // Display selected item in the corresponding TextView
                    String selectedValue = parentView.getItemAtPosition(position).toString();
                    Log.e("Spinner", "Selected Value: " + selectedValue);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                    // Do nothing here
                }
            });


            dropdownContainer.addView(spinner);
        }
    }

    private void onPlayButtonClick(View view) {
        String selectedOperation = spinnerOperation.getSelectedItem().toString();
        String selectedOperands = spinnerOperands.getSelectedItem().toString();
        selectedTotalQuestions = spinnerTotalQuestions.getSelectedItem().toString();
        String selectedTimeInterval = spinnerTimeInterval.getSelectedItem().toString();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        currentDate = dateFormat.format(new Date());

        Log.e("Spinner", "Selected Operation: " + selectedOperation);
        Log.e("Spinner", "Selected Operands: " + selectedOperands);
        Log.e("Spinner", "Selected Total Questions: " + selectedTotalQuestions);
        Log.e("Spinner","Selected Time Interval"+selectedTimeInterval);

        if ("Select the Operation".equals(selectedOperation) ||
                "Select the Operands".equals(selectedOperands) ||
                "Select the Total Questions".equals(selectedTotalQuestions) ||
                "Selected Time Interval".equals(selectedTimeInterval)) {
            // Show a toast message indicating that the user needs to select all options
            Toast.makeText(this, "Please select all Spinner options", Toast.LENGTH_SHORT).show();
            return; // Stop execution if any of the values is not selected
        }

        if ("Select Time Interval".equals(selectedTimeInterval)) {
            // Show a toast message indicating that the user needs to select a time interval
            Toast.makeText(this, "Please select a time interval", Toast.LENGTH_SHORT).show();
            return; // Stop execution if time interval is not selected
        }

        if (!selectedTotalQuestions.equals("Select the Total Questions")) {
            if ("Addition".equals(selectedOperation)) {
                // Check if a valid number is selected in spinnerOperands
                if (!validateSpinnerOperands()) {
                    // Show a toast message indicating that the user needs to select a valid number
                    Toast.makeText(this, "Please select a valid number in Operands", Toast.LENGTH_SHORT).show();
                    return; // Stop execution if spinnerOperands value is not valid
                }

                // Check if dynamic spinners have valid selections for Addition operation
                if (!validateDynamicSpinnersForAddition()) {
                    // Show a toast message indicating that the user needs to select valid options in dynamic spinners
                    Toast.makeText(this, "Please select valid options in dynamic spinners for Addition", Toast.LENGTH_SHORT).show();
                    return; // Stop execution if dynamic spinner values are not valid
                }

                generateAdditionQuestions();
            } else if ("Multiplication".equals(selectedOperation)) {
                // Check if dynamic spinners have valid selections for Multiplication operation
                if (!validateDynamicSpinnersForMultiplication()) {
                    // Show a toast message indicating that the user needs to select valid options in dynamic spinners
                    Toast.makeText(this, "Please select valid options in dynamic spinners for Multiplication", Toast.LENGTH_SHORT).show();
                    return; // Stop execution if dynamic spinner values are not valid
                }

                generateMultiplicationBaseValues();
            }
        } else {
            // Handle the case where "Select the Total Questions" is selected
            Log.e("NumberFormatException", "Invalid number format: " + selectedTotalQuestions);
        }
    }

    private boolean validateDynamicSpinnersForMultiplication() {
        for (int i = 0; i < dropdownContainer.getChildCount(); i++) {
            View childView = dropdownContainer.getChildAt(i);

            if (childView instanceof Spinner) {
                Spinner dynamicSpinner = (Spinner) childView;
                String selectedValue = dynamicSpinner.getSelectedItem().toString();

                // Check if the selected value is not "Select the Range"
                if ("Select the Range".equals(selectedValue)) {
                    return false; // Return false if any dynamic spinner value is not selected
                }
            }
        }
        return true;
    }

    private boolean validateDynamicSpinnersForAddition() {
        String selectedOperandsValue = spinnerOperands.getSelectedItem().toString();

        // Check if the selected value is a valid number
        try {
            int selectedOperandCount = Integer.parseInt(selectedOperandsValue);
            for (int i = 0; i < dropdownContainer.getChildCount(); i++) {
                View childView = dropdownContainer.getChildAt(i);

                if (childView instanceof Spinner) {
                    Spinner dynamicSpinner = (Spinner) childView;
                    String selectedValue = dynamicSpinner.getSelectedItem().toString();

                    // Check if the selected value is not "Select the Range"
                    if ("Select the Range".equals(selectedValue)) {
                        return false; // Return false if any dynamic spinner value is not selected
                    }

                    // Check if the selected value is not "Select the Total Numbers"
                    if ("Select the Total Numbers".equals(selectedValue)) {
                        return false; // Return false if any dynamic spinner value is not selected
                    }

                    // Additional check: Validate that the selected range is within the allowed range
                    int selectedRange;
                    try {
                        selectedRange = Integer.parseInt(selectedValue);
                        if (selectedRange < 1 || selectedRange > 10 || selectedRange > selectedOperandCount) {
                            return false; // Return false if the selected range is invalid
                        }
                    } catch (NumberFormatException e) {
                        // Handle the case where the selected value is not a valid number
                        return false;
                    }
                }
            }
        } catch (NumberFormatException e) {
            // Handle the case where the selected operands value is not a valid number
            return false;
        }
        return true;
    }

    private boolean validateSpinnerOperands() {
        String selectedValue = spinnerOperands.getSelectedItem().toString();
        return !("Select the Operands".equals(selectedValue));
    }

    private void generateMultiplicationBaseValues() {
        List<List<String>> multiplicationBaseValues = getSelectedRanges(); // Get selected base values from dynamic spinners
        correctAnswers = new ArrayList<>();
        // Generate multiplication questions based on the selected base values
        questions = new ArrayList<>();

        for (int i = 0; i < Integer.parseInt(selectedTotalQuestions); i++) {
            String question = generateMultiplicationQuestion(multiplicationBaseValues);
            Log.e("Spinner", "Multiplication Question " + (i + 1) + ": " + question);
            questions.add(question);
            // Generate and store the correct answer for each question
            String correctAnswer = calculateMultiplicationAnswer(question);
            correctAnswers.add(correctAnswer);
            Log.e("Spinner", "Correct Answer for Question " + (i + 1) + ": " + correctAnswer);
        }
        // Start the QuizActivity with the generated questions
        startQuizActivity(questions, correctAnswers,selectedTimeInterval);
    }

    @Override
    public void onBackPressed() {
        // Reset spinners to default values or clear selections
        // For example:
        spinnerOperation.setSelection(0);
        spinnerOperands.setSelection(0);
        spinnerTotalQuestions.setSelection(0);
        dropdownContainer.removeAllViews();

        super.onBackPressed();
    }

    private void clearDynamicSpinners() {
        dropdownContainer.removeAllViews();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Reset spinners to default values or clear selections
        spinnerOperation.setSelection(0);
        spinnerOperands.setSelection(0);
        spinnerTotalQuestions.setSelection(0);
        dropdownContainer.removeAllViews();
    }



    // Call this method when the operation type changes
    private void onOperationChange() {
        // Clear dynamic spinners
        clearDynamicSpinners();
        // Additional logic for handling the change
    }

    private String calculateMultiplicationAnswer(String question) {
        String[] operands = question.split("\\*");

        // Initialize the result to 1 (since we're multiplying)
        int result = 1;

        // Multiply each operand to get the result
        for (String operand : operands) {
            try {
                // Parse the operand and multiply
                int value = Integer.parseInt(operand.trim());
                result *= value;
            } catch (NumberFormatException e) {
                // Handle the case where the operand is not a valid number
                e.printStackTrace(); // Log the error or handle it as needed
            }
        }

        // Return the result as a String
        return String.valueOf(result);
    }

    private String generateMultiplicationQuestion(List<List<String>> multiplicationBaseValues) {
        Random random = new Random();
        StringBuilder questionBuilder = new StringBuilder();

        // Generate operands based on the selected base values
        for (List<String> baseValue : multiplicationBaseValues) {
            int operand = random.nextInt(baseValue.size());
            questionBuilder.append(operand).append("\n").append(" * ");
        }

        // Remove the trailing space and the last "*"
        questionBuilder.setLength(questionBuilder.length() - 2);

        return questionBuilder.toString();
    }

    private void startQuizActivity(List<String> questions, List<String> correctAnswers, String selectedTimeInterval) {
        String selectedOperation = spinnerOperation.getSelectedItem().toString();
        String selectedOperands = spinnerOperands.getSelectedItem().toString();
        selectedTotalQuestions = spinnerTotalQuestions.getSelectedItem().toString();
        selectedTimeInterval = spinnerTimeInterval.getSelectedItem().toString();

        Log.e("Spinner", "Selected Operation: " + selectedOperation);
        Log.e("Spinner", "Selected Operands: " + selectedOperands);
        Log.e("Spinner", "Selected Total Questions: " + selectedTotalQuestions);
        Log.e("Spinner","Selected Time Interval"+selectedTimeInterval);

        Intent intent = new Intent(VisualiztionActivity.this, VisualQuizActivity.class);
        intent.putStringArrayListExtra("questions", (ArrayList<String>) questions);
        intent.putStringArrayListExtra("correctAnswers", (ArrayList<String>) correctAnswers);
        intent.putExtra("studentId",studentid);
        intent.putExtra("firstName", studentName);
        intent.putExtra("selectedOperands", selectedOperands);
        intent.putExtra("selectedOperation", selectedOperation);
        intent.putExtra("selectedTotalQuestions", selectedTotalQuestions);
        intent.putExtra("currentDate", currentDate);
        intent.putExtra("selectedTimeInterval", selectedTimeInterval);
        startActivity(intent);
        finish();
    }


    private void generateAdditionQuestions() {
        List<List<String>> selectedRanges = getSelectedRanges();
        correctAnswers = new ArrayList<>();

        questions = new ArrayList<>();

        for (int i = 0; i < Integer.parseInt(selectedTotalQuestions); i++) {
            String question = generateQuestion("Addition", selectedRanges);
            Log.e("Spinner", "Addition Question " + (i + 1) + ": " + question);
            questions.add(question);

            String correctAnswer = calculateAdditionAnswer(question);
            correctAnswers.add(correctAnswer);

            // Log the correct answer
            Log.e("Spinner", "Correct Answer for Addition Question " + (i + 1) + ": " + correctAnswer);
        }

        // Start the QuizActivity with the generated questions
        startQuizActivity(questions, correctAnswers, selectedTimeInterval);
    }

    private String calculateAdditionAnswer(String question) {
        String[] operands = question.split("\\+");

        // Initialize the result to 0 (since we're adding)
        int result = 0;

        // Add each operand to get the result
        for (String operand : operands) {
            try {
                // Parse the operand and add
                int value = Integer.parseInt(operand.trim());
                result += value;
            } catch (NumberFormatException e) {
                // Handle the case where the operand is not a valid number
                e.printStackTrace(); // Log the error or handle it as needed
            }
        }

        // Return the result as a String
        return String.valueOf(result);
    }

    private List<List<String>> getSelectedRanges() {
        List<List<String>> selectedRanges = new ArrayList<>();

        for (int i = 0; i < dropdownContainer.getChildCount(); i++) {
            View childView = dropdownContainer.getChildAt(i);

            if (childView instanceof Spinner) {
                Spinner dynamicSpinner = (Spinner) childView;
                String selectedValue = dynamicSpinner.getSelectedItem().toString();

                // Check if the selected value is not "Select the Range"
                if (!selectedValue.equals("Select the Range")) {
                    List<String> range = generateRange(Integer.parseInt(selectedValue));
                    selectedRanges.add(range);
                }
            }
        }

        return selectedRanges;
    }
    private List<String> generateRange(int parseInt) {
        int maxValue = (int) Math.pow(10, parseInt) - 1;
        List<String> range = new ArrayList<>();

        for (int i = 1; i <= maxValue; i++) {
            range.add(String.valueOf(i));
        }

        return range;
    }

    private String generateQuestion(String selectedOperation, List<List<String>> operandCount) {
        Random random = new Random();
        StringBuilder questionBuilder = new StringBuilder();

        // Ensure that there is at least one operandCount
        if (!operandCount.isEmpty()) {
            // Generate operands based on the selected ranges
            for (List<String> range : operandCount) {
                int index = random.nextInt(range.size());
                String operand = range.get(index);
                questionBuilder.append(operand).append("\n").append(" + ");
            }

            // Check if the length is greater than or equal to 2 before removing the last characters
            if (questionBuilder.length() >= 2) {
                // Remove the trailing space and the last "+"
                questionBuilder.setLength(questionBuilder.length() - 2);
            }
        }

        return questionBuilder.toString();
    }

}