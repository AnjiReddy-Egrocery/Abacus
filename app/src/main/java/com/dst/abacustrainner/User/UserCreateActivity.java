package com.dst.abacustrainner.User;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.dst.abacustrainner.Model.StudentRegistationResponse;
import com.dst.abacustrainner.R;
import com.dst.abacustrainner.Services.ApiClient;
import com.dst.abacustrainner.database.SharedPrefManager;

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

public class UserCreateActivity extends AppCompatActivity {

    LinearLayout layoutheaderSignIn,layoutheaderSignUp;
    TextView txtSignIn,txtSignUp;
    LinearLayout layoutSignInForm,layoutSignUpForm;
    LinearLayout layoutTextSignIn,layoutTextSignUp;
    Button butLogin,butRegister;
    View lineSignIn,lineSignUp;
    String parentEmail,password;
    EditText edtEmail,edtPassword;
    EditText edtFirstName,edtLastName,edtDate,edtMotherTongue,edtRegisterEmail,edtNumber;
    TextView txtForgotPassword;
    private RadioGroup genderRadioGroup;
    private Calendar calendar;
    String firstName,lastName,mobileNumber,registeremail,date,tongue,selectedGender;
    private SharedPreferences sharedPreferences;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_create);


        layoutheaderSignIn = findViewById(R.id.layout_header_signin);
        layoutheaderSignUp = findViewById(R.id.layout_header_signup);

        txtSignIn = findViewById(R.id.txt_signin);
        txtSignUp = findViewById(R.id.txt_signup);

        layoutSignInForm = findViewById(R.id.layout_form_signin);
        layoutSignUpForm = findViewById(R.id.layout_form_signup);

        layoutTextSignIn = findViewById(R.id.layout_text_signIn);
        layoutTextSignUp = findViewById(R.id.layout_already_sign);

        lineSignIn = findViewById(R.id.line_signin);
        lineSignUp = findViewById(R.id.line_signup);

        butLogin = findViewById(R.id.but_login);

        edtEmail=findViewById(R.id.edt_parent_email);
        edtPassword=findViewById(R.id.edt_password);

        edtFirstName=findViewById(R.id.edt_first_name);
        edtLastName=findViewById(R.id.edt_last_name);
        edtMotherTongue=findViewById(R.id.edt_tongue);
        edtRegisterEmail=findViewById(R.id.edt_email);
        edtNumber=findViewById(R.id.edt_number);
        butRegister=findViewById(R.id.but_register);
        edtDate=findViewById(R.id.edt_date);
        genderRadioGroup=findViewById(R.id.radio);

        calendar = Calendar.getInstance();

        txtForgotPassword = findViewById(R.id.txt_forgot_password);


        edtDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog();
            }
        });

        layoutTextSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SignUpVisiableMethod();
                changeTextColors(false);
                //FormRegisterMethod();
            }
        });

        layoutTextSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SignInVisiableMethod();
                changeTextColors(true);
            }
        });

        txtForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserCreateActivity.this, ForgotPasswordActivity.class);
                startActivity(intent);
            }
        });

        SignInVisiableMethod();
        changeTextColors(true);
        FormLoginMethod();


        txtSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SignInVisiableMethod();
                changeTextColors(true);
                FormLoginMethod();
            }
        });

        txtSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SignUpVisiableMethod();
                changeTextColors(false);
                FormRegisterMethod();
            }
        });
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
                UserCreateActivity.this,
                dateListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        ).show();
    }


    public void ShowHidePass(View view) {

        if(view.getId()==R.id.show_pass_btn){
            if(edtPassword.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())){
                ((ImageView)(view)).setImageResource(R.drawable.visiablityoff);
                //Show Password
                edtPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            }
            else{
                ((ImageView)(view)).setImageResource(R.drawable.visiablity);
                //Hide Password
                edtPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        }
    }

    private void FormLoginMethod() {
        boolean b_user = SharedPrefManager.getInstance(getApplicationContext()).isLoggedIn();
        if (b_user) {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        }

        butLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                parentEmail =edtEmail.getText().toString();
                password=edtPassword.getText().toString();

                if (!isValidEmail(parentEmail)) {
                    edtEmail.setError("Invalid email address");
                    return; // Stop further processing
                }else if (!isValidPassword(password)) {
                    edtPassword.setError("Invalid password");
                    return; // Stop further processing
                }else {
                    LoginMethod(parentEmail,password);
                }
            }
        });
    }

    private void changeTextColors(boolean b) {
        if (b) {
            txtSignIn.setTextColor(getResources().getColor(R.color.purple));  // Purple for Sign In
            txtSignUp.setTextColor(getResources().getColor(R.color.grey));
            lineSignIn.setVisibility(View.VISIBLE);
            lineSignUp.setVisibility(View.INVISIBLE);// Grey for Sign Up
        } else {
            txtSignIn.setTextColor(getResources().getColor(R.color.grey));    // Grey for Sign In
            txtSignUp.setTextColor(getResources().getColor(R.color.purple));  // Purple for Sign Up
            lineSignUp.setVisibility(View.VISIBLE);
            lineSignIn.setVisibility(View.INVISIBLE);
        }
    }

    private void SignInVisiableMethod() {
        layoutheaderSignIn.setVisibility(View.VISIBLE);
        layoutheaderSignUp.setVisibility(View.GONE);

        layoutSignInForm.setVisibility(View.VISIBLE);
        layoutSignUpForm.setVisibility(View.GONE);

//        layoutTextSignIn.setVisibility(View.VISIBLE);

    }

    private void FormRegisterMethod() {

        butRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firstName = edtFirstName.getText().toString().trim();
                lastName = edtLastName.getText().toString().trim();
                mobileNumber = edtNumber.getText().toString().trim();
                registeremail = edtRegisterEmail.getText().toString().trim();
                date = edtDate.getText().toString().trim();
                tongue = edtMotherTongue.getText().toString().trim();

                boolean error = false;

                if (isValidFirstName(firstName)
                        && isValidLastName(lastName)
                        && isValidMotherTongue(tongue)
                        && isValidEmail(registeremail)
                        && isValidMobileNumber(mobileNumber)) {

                    registerMenthod(firstName, lastName, mobileNumber, registeremail, date, tongue);
                } else {
                    Toast.makeText(UserCreateActivity.this, "Validation failed. Please check your input.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void registerMenthod(String firstName, String lastName, String mobileNumber, String registeremail, String date, String tongue) {
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
        RequestBody emailPart = RequestBody.create(MediaType.parse("text/plain"), registeremail);
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
                        Toast.makeText(UserCreateActivity.this, "Email and Mobile Number alerady Exists", Toast.LENGTH_SHORT).show();

                    }else if (registrationResponse.getErrorCode() .equals("200")){

                        String studentId="";
                        String otp="";
                        String parentEmailId="";
                        StudentRegistationResponse.Result list =registrationResponse.getResult();
                        studentId=list.getStudentId();
                        otp=list.getOtp();
                        parentEmailId=list.getParentEmail();
                        Intent intent = new Intent(UserCreateActivity.this, VerifyActivity.class);
                        intent.putExtra("studentId", studentId);
                        intent.putExtra("Otp", otp);
                        intent.putExtra("parentEmail",parentEmailId);
                        startActivity(intent);
                    }

                }else {
                    Toast.makeText(UserCreateActivity.this,"Data Error",Toast.LENGTH_LONG).show();
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


    private boolean isValidMobileNumber(String mobileNumber) {
        return Patterns.PHONE.matcher(mobileNumber).matches();
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


    private void LoginMethod(String parentEmail, String password) {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://www.abacustrainer.com/") // Replace with your API URL
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
        ApiClient apiClient=retrofit.create(ApiClient.class);

        RequestBody parentEmailPart = RequestBody.create(MediaType.parse("text/plain"), parentEmail);
        RequestBody passwordPart = RequestBody.create(MediaType.parse("text/plain"), password);


        Call<StudentRegistationResponse> call=apiClient.LoginPost(parentEmailPart,passwordPart);
        call.enqueue(new Callback<StudentRegistationResponse>() {
            @Override
            public void onResponse(Call<StudentRegistationResponse> call, Response<StudentRegistationResponse> response) {
                if (response.isSuccessful()){
                    StudentRegistationResponse registrationResponse = response.body();
                    if (registrationResponse.getErrorCode().equals("202")){
                        Toast.makeText(UserCreateActivity.this, "Incorrect , please Check Email and Password.", Toast.LENGTH_SHORT).show();
                    }else if (registrationResponse.getErrorCode().equals("200")) {
                        SharedPrefManager.getInstance(getApplicationContext()).insertData(response.body());
                        Toast.makeText(UserCreateActivity.this, "Student Login Completed Successfully.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(UserCreateActivity.this, HomeActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }else {
                    Toast.makeText(UserCreateActivity.this,"Data Error",Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(Call<StudentRegistationResponse> call, Throwable t) {
            }
        });
    }

    private boolean isValidPassword(String password) {
        return password.length() >= 6;
    }

    private boolean isValidEmail(String parentEmail) {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        return parentEmail.matches(emailPattern);
    }



    private void SignUpVisiableMethod() {

        layoutheaderSignIn.setVisibility(View.GONE);
        layoutheaderSignUp.setVisibility(View.VISIBLE);

        layoutSignInForm.setVisibility(View.GONE);
        layoutSignUpForm.setVisibility(View.VISIBLE);

//        layoutTextSignIn.setVisibility(View.GONE);



    }
}