package com.dst.abacustrainner.User;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import com.dst.abacustrainner.Model.StudentRegistationResponse;
import com.dst.abacustrainner.Model.StudentUserMethod;
import com.dst.abacustrainner.R;
import com.dst.abacustrainner.Services.ApiClient;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class VerifyActivity extends AppCompatActivity {

    Button butVerify;
    String id,otp,email;
    EditText edtOtp,edtPassword,edtReEnterPwd;
    TextView txtOtp,txtPassword,txtConfirmPassword;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify);

        butVerify=findViewById(R.id.but_verify);
        edtOtp=findViewById(R.id.edt_otp);
        edtPassword=findViewById(R.id.password);
        edtReEnterPwd=findViewById(R.id.edt_reenter_password);

        txtOtp = findViewById(R.id.txt_otp);
        txtPassword = findViewById(R.id.txt_password);
        txtConfirmPassword = findViewById(R.id.txt_confirm_password);

        Bundle bundle=getIntent().getExtras();

        id=bundle.getString("studentId");
        otp=bundle.getString("Otp");
        email=bundle.getString("parentEmail");



        edtOtp.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                txtOtp.setVisibility(View.GONE);
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void afterTextChanged(Editable s) {}
        });
        edtPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                txtPassword.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        edtReEnterPwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                txtConfirmPassword.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        butVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                otp = edtOtp.getText().toString();
                String password = edtPassword.getText().toString();
                String reEnterPassword = edtReEnterPwd.getText().toString();

                boolean isValid = true;

                if (!isValidOTP(otp)) {
                    isValid = false;
                }

                if (!isValidPassword(password)) {
                    isValid = false;
                }

                if (!doPasswordsMatch(password, reEnterPassword)) {
                    isValid = false;
                }

                if (isValid) {
                    VerifyMethod(id, otp, password);
                }
            }
        });
    }
    private boolean doPasswordsMatch(String password, String reEnterPassword) {
        if (!password.equals(reEnterPassword)) {
            txtConfirmPassword.setVisibility(View.VISIBLE);
            return false;
        }

        txtConfirmPassword.setVisibility(View.GONE);
        return true;
    }

    private boolean isValidPassword(String password) {
        if (password.length() < 6) {
            txtPassword.setVisibility(View.VISIBLE);
            return false;
        }

        txtPassword.setVisibility(View.GONE);
        return true;
    }

    private boolean isValidOTP(String otp) {
        if (otp.isEmpty()) {
            txtOtp.setText("Enter OTP");
            txtOtp.setVisibility(View.VISIBLE);
            return false;
        }

        if (otp.length() != 6) {
            txtOtp.setText("OTP must be 6 digits");
            txtOtp.setVisibility(View.VISIBLE);
            return false;
        }

        txtOtp.setVisibility(View.GONE);
        return true;
    }

    private void VerifyMethod(String id, String otp, String password) {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://www.abacustrainer.com/") // Replace with your API URL
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
        ApiClient apiClient=retrofit.create(ApiClient.class);
        RequestBody idPart = RequestBody.create(MediaType.parse("text/plain"), id);
        RequestBody otpPart = RequestBody.create(MediaType.parse("text/plain"), otp);
        RequestBody passwordPart = RequestBody.create(MediaType.parse("text/plain"), password);


        Call<StudentUserMethod> call=apiClient.VerifyPost(idPart,otpPart,passwordPart);
        call.enqueue(new Callback<StudentUserMethod>() {
            @Override
            public void onResponse(Call<StudentUserMethod> call, Response<StudentUserMethod> response) {
                if (response.isSuccessful()){

                    StudentUserMethod registrationResponse = response.body();
                    if (registrationResponse.getErrorCode().equals("203")) {
                        // Registration was successful
                        Toast.makeText(VerifyActivity.this, "Invalid OTP", Toast.LENGTH_SHORT).show();
                    } else if (registrationResponse.getErrorCode().equals("200")){
                        String parentEmailId="";
                        String password="";
                        StudentUserMethod.Result result = registrationResponse.getResult();

                        parentEmailId=result.getParentEmail();
                        password= result.getPassword();

                        Intent intent = new Intent(VerifyActivity.this, UserCreateActivity .class);
                        intent.putExtra("parentEmail",parentEmailId);
                        intent.putExtra("password",password);
                        startActivity(intent);

                    }
                }else {
                    Toast.makeText(VerifyActivity.this,"Data Error",Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(Call<StudentUserMethod> call, Throwable t) {

            }
        });
    }
}