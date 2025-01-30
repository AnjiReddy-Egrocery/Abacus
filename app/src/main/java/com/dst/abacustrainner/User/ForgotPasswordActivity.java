package com.dst.abacustrainner.User;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.dst.abacustrainner.Model.ForgotPassword;
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


public class ForgotPasswordActivity extends AppCompatActivity {

    EditText edtEmail;

    Button butSend;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        edtEmail = findViewById(R.id.edt_email);
        butSend=findViewById(R.id.but_send);

        butSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 String email = edtEmail.getText().toString().trim();

                 if (isValidEmail(email)){

                     resetPasswordMethod(email);
                 }
            }
        });
    }

    private void resetPasswordMethod(String email) {
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
        RequestBody emailPart = RequestBody.create(MediaType.parse("text/plain"), email);

        Call<ForgotPassword> call=apiClient.forgot(emailPart);

        call.enqueue(new Callback<ForgotPassword>() {
            @Override
            public void onResponse(Call<ForgotPassword> call, Response<ForgotPassword> response) {
                if (response.isSuccessful()){
                    ForgotPassword forgotPassword = response.body();
                    Log.e("USERDADA","list: "+forgotPassword);
                  if (forgotPassword.getErrorCode() .equals("200")){

                      String studentId="";
                      String otp="";
                      String parentEmailId="";
                      ForgotPassword.Result list =forgotPassword.getResult();
                      studentId=list.getStudentId();
                        otp=list.getOtp();
                        parentEmailId=list.getParentEmail();
                        Intent intent = new Intent(ForgotPasswordActivity.this, VerifyActivity.class);
                      intent.putExtra("studentId", studentId);
                      intent.putExtra("Otp", otp);
                      intent.putExtra("parentEmail",parentEmailId);
                      startActivity(intent);
                    }

                }else {
                    Toast.makeText(ForgotPasswordActivity.this,"Data Error",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ForgotPassword> call, Throwable t) {

            }
        });

    }

    private boolean isValidEmail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}