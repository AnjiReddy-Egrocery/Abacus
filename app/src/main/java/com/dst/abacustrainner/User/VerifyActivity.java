package com.dst.abacustrainner.User;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import com.dst.abacustrainner.Model.StudentRegistationResponse;
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

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify);

        butVerify=findViewById(R.id.but_verify);
        edtOtp=findViewById(R.id.edt_otp);
        edtPassword=findViewById(R.id.password);
        edtReEnterPwd=findViewById(R.id.edt_reenter_password);

        Bundle bundle=getIntent().getExtras();

        id=bundle.getString("studentId");
        otp=bundle.getString("Otp");
        email=bundle.getString("parentEmail");

        edtOtp.setText(otp);

        butVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                otp = edtOtp.getText().toString();
                String password = edtPassword.getText().toString();
                String reEnterPassword = edtReEnterPwd.getText().toString();

                if (!isValidOTP(otp)) {
                    edtOtp.setError("Plz Enter Valid Otp");
                    return; // Stop further processing
                } else if (!isValidPassword(password)) {
                    edtPassword.setError("Invalid password");
                    return; // Stop further processing
                } else if (!doPasswordsMatch(password, reEnterPassword)) {
                    Toast.makeText(VerifyActivity.this, "PassWord Doen't Match.", Toast.LENGTH_SHORT).show();
                } else {
                    VerifyMethod(id, otp, password);
                }
            }
        });
    }
    private boolean doPasswordsMatch(String password, String reEnterPassword) {
        return password.equals(reEnterPassword);
    }

    private boolean isValidPassword(String password) {
        return password.length() >= 6;
    }

    private boolean isValidOTP(String otp) {
        return otp.length() == 6;
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


        Call<StudentRegistationResponse> call=apiClient.VerifyPost(idPart,otpPart,passwordPart);
        call.enqueue(new Callback<StudentRegistationResponse>() {
            @Override
            public void onResponse(Call<StudentRegistationResponse> call, Response<StudentRegistationResponse> response) {
                if (response.isSuccessful()){

                    StudentRegistationResponse registrationResponse = response.body();
                    if (registrationResponse.getErrorCode().equals("203")) {
                        // Registration was successful
                        Toast.makeText(VerifyActivity.this, "Invalid OTP", Toast.LENGTH_SHORT).show();
                    } else if (registrationResponse.getErrorCode().equals("200")){
                        String parentEmailId="";
                        String password="";
                        StudentRegistationResponse.Result result = registrationResponse.getResult();

                        parentEmailId=result.getParentEmail();
                        password= result.getPassword();

                        Intent intent = new Intent(VerifyActivity.this, LoginActivity.class);
                        intent.putExtra("parentEmail",parentEmailId);
                        intent.putExtra("password",password);
                        startActivity(intent);

                    }
                }else {
                    Toast.makeText(VerifyActivity.this,"Data Error",Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(Call<StudentRegistationResponse> call, Throwable t) {

            }
        });
    }
}