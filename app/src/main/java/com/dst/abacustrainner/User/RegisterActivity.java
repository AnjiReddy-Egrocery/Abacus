package com.dst.abacustrainner.User;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import com.dst.abacustrainner.Model.StudentRegistationResponse;
import com.dst.abacustrainner.R;
import com.dst.abacustrainner.Services.ApiClient;

import java.text.SimpleDateFormat;
import java.util.Calendar;
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

public class RegisterActivity extends AppCompatActivity {
    TextView txtTermsAndCondition,txtSignIn;
    EditText edtFirstName,edtLastName,edtDate,edtMotherTongue,edtEmail,edtNumber;
    private RadioGroup genderRadioGroup;
    Button butSubmit;
    private Calendar calendar;
    String firstName,lastName,mobileNumber,email,date,tongue,selectedGender;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        txtTermsAndCondition=findViewById(R.id.txt_tearms_condition);
        txtSignIn=findViewById(R.id.txt_signIn);
        edtFirstName=findViewById(R.id.edt_first_name);
        edtLastName=findViewById(R.id.edt_last_name);
        edtMotherTongue=findViewById(R.id.edt_tongue);
        edtEmail=findViewById(R.id.edt_email);
        edtNumber=findViewById(R.id.edt_number);
        butSubmit=findViewById(R.id.but_submit);
        edtDate=findViewById(R.id.edt_date);
        genderRadioGroup=findViewById(R.id.radio);

        String txt="<font color=#000>By signing up, you agree to our</font> <font color=#3EA2DA>Terms,Privacy Policy</font><font color=#000>  and  </font><font color=#3DA2DA> Cookies Policy .</font>";
        txtTermsAndCondition.setText(Html.fromHtml(txt));

        String text = "<font color=#434242>Have an account?</font> <font color=#2295D5>Log in</font>";
        txtSignIn.setText(Html.fromHtml(text));

        calendar = Calendar.getInstance();

        edtDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog();
            }
        });
        txtSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(RegisterActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        });
        butSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firstName = edtFirstName.getText().toString().trim();
                lastName = edtLastName.getText().toString().trim();
                mobileNumber = edtNumber.getText().toString().trim();
                email = edtEmail.getText().toString().trim();
                date = edtDate.getText().toString().trim();
                tongue = edtMotherTongue.getText().toString().trim();

                boolean error = false;

                if (isValidFirstName(firstName)
                        && isValidLastName(lastName)
                        && isValidMotherTongue(tongue)
                        && isValidEmail(email)
                        && isValidMobileNumber(mobileNumber)) {

                    registerMenthod(firstName, lastName, mobileNumber, email, date, tongue);
                } else {
                   Toast.makeText(RegisterActivity.this, "Validation failed. Please check your input.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private boolean isValidMobileNumber(String mobileNumber) {
        return Patterns.PHONE.matcher(mobileNumber).matches();
    }
    private boolean isValidEmail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
    private boolean isValidMotherTongue(String tongue) {
        return !TextUtils.isEmpty(tongue);
    }
    private boolean isValidLastName(String lastName) {
        return !TextUtils.isEmpty(lastName);
    }
    private boolean isValidFirstName(String firstName) {
        return !TextUtils.isEmpty(firstName);
    }
    private void showDatePickerDialog() {
        DatePickerDialog.OnDateSetListener dateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, day);
                SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy", Locale.US);
                edtDate.setText(dateFormat.format(calendar.getTime()));
            }
        };
        new DatePickerDialog(
                RegisterActivity.this,
                dateListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        ).show();
    }
    private void registerMenthod(String firstName, String lastName, String mobileNumber, String email, String date, String tongue) {
        String selectedGender = getSelectedGender();

        /*HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
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
        RequestBody firstNamePart = RequestBody.create(MediaType.parse("text/plain"), firstName);
        RequestBody lastnamePart = RequestBody.create(MediaType.parse("text/plain"), lastName);
        RequestBody mobilenumberPart = RequestBody.create(MediaType.parse("text/plain"), mobileNumber);
        RequestBody emailPart = RequestBody.create(MediaType.parse("text/plain"), email);
        RequestBody genderPart = RequestBody.create(MediaType.parse("text/plain"), selectedGender);
        RequestBody mothertonguePart = RequestBody.create(MediaType.parse("text/plain"), tongue);
        RequestBody dateofbirthPart = RequestBody.create(MediaType.parse("text/plain"), date);

          Call<StudentRegistationResponse> call=apiClient.studentRegisterPost(firstNamePart,lastnamePart,emailPart,mobilenumberPart,genderPart,mothertonguePart,dateofbirthPart);
          call.enqueue(new Callback<StudentRegistationResponse>() {
              @Override
              public void onResponse(Call<StudentRegistationResponse> call, Response<StudentRegistationResponse> response) {
                  if (response.isSuccessful()){
                      StudentRegistationResponse registrationResponse = response.body();
                      Log.e("USERDADA","list: "+registrationResponse);
                      if(registrationResponse.getErrorCode() .equals("203")){
                          Toast.makeText(RegisterActivity.this, "Email and Mobile Number alerady Exists", Toast.LENGTH_SHORT).show();

                      }else if (registrationResponse.getErrorCode() .equals("200")){

                          String studentId="2251";
                          String otp="";
                          String parentEmailId="";
                          StudentRegistationResponse.Result list =registrationResponse.getResult();
                         // studentId=list.getStudentId();
                          otp=list.getOtp();
                          parentEmailId=list.getParentEmail();
                          Intent intent = new Intent(RegisterActivity.this, VerifyActivity.class);
                          intent.putExtra("studentId", studentId);
                          intent.putExtra("Otp", otp);
                          intent.putExtra("parentEmail",parentEmailId);
                          startActivity(intent);
                      }

                  }else {
                      Toast.makeText(RegisterActivity.this,"Data Error",Toast.LENGTH_LONG).show();
                  }
              }

              @Override
              public void onFailure(Call<StudentRegistationResponse> call, Throwable t) {

              }
          });
    }

    private String getSelectedGender() {

        int selectedGenderId = genderRadioGroup.getCheckedRadioButtonId();
        RadioButton selectedRadioButton = findViewById(selectedGenderId);
        if (selectedRadioButton != null) {
            return selectedRadioButton.getText().toString();
        }
        return "";
    }
}