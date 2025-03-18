package com.dst.abacustrainner.User;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

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
import android.view.LayoutInflater;
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

public class StudentLoginActivity extends AppCompatActivity {


    TextView txtSignIn,txtSignUp;
    LinearLayout layoutSignInForm,layoutSignUpForm;
    LinearLayout layoutTextSignIn,layoutTextSignUp;
    Button butLogin,butRegister;
    View lineSignIn,lineSignUp;
    String parentEmail,password;
    EditText edtEmail,edtPassword;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_login);

        txtSignIn = findViewById(R.id.txt_signin);
        txtSignUp = findViewById(R.id.txt_signup);

        layoutSignInForm = findViewById(R.id.layout_form_signin);
        layoutSignUpForm = findViewById(R.id.layout_form_signup);

        layoutTextSignIn = findViewById(R.id.layout_text_signIn);
        layoutTextSignUp = findViewById(R.id.layout_already_sign);

        lineSignIn = findViewById(R.id.line_signin);
        lineSignUp = findViewById(R.id.line_signup);

        butLogin = findViewById(R.id.but_student_login);

        edtEmail=findViewById(R.id.edt_student_email);
        edtPassword=findViewById(R.id.edt_student_password);


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
                //FormRegisterMethod();
            }
        });
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
                    Intent intent = new Intent(StudentLoginActivity.this,StudentHomeActivity.class);
                    intent.putExtra("Email","AnjiReddy");
                    startActivity(intent);
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
        //layoutheaderSignIn.setVisibility(View.VISIBLE);
        //layoutheaderSignUp.setVisibility(View.GONE);

        layoutSignInForm.setVisibility(View.VISIBLE);
        layoutSignUpForm.setVisibility(View.GONE);

 }
    private void SignUpVisiableMethod() {

        //layoutheaderSignIn.setVisibility(View.GONE);
        //layoutheaderSignUp.setVisibility(View.VISIBLE);

        layoutSignInForm.setVisibility(View.GONE);
        layoutSignUpForm.setVisibility(View.VISIBLE);

 }




    private boolean isValidPassword(String password) {
        return password.length() >= 6;
    }

    private boolean isValidEmail(String parentEmail) {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        return parentEmail.matches(emailPattern);
    }




}