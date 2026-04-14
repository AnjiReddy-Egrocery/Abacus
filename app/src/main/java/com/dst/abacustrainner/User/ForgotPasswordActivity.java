package com.dst.abacustrainner.User;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
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
    LinearLayout layoutBack;
    TextView txtemail;



    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        edtEmail = findViewById(R.id.edt_forgot_email);
        butSend=findViewById(R.id.but_send);
        layoutBack = findViewById(R.id.layout_back);
        txtemail = findViewById(R.id.txt_email);

        layoutBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        edtEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                txtemail.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        butSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 String email = edtEmail.getText().toString().trim();
                Log.d("API_ERROR", "Entered Email: " + email);
                boolean isValid = true;

                if (!isValidEmail(email)) {
                    isValid = false;
                }

                if (isValid) {
                    resetPasswordMethod(email);

                } else {
                    //Toast.makeText(UserCreateActivity.this, "Validation failed. Please check your input.", Toast.LENGTH_SHORT).show();
                }


            }
        });
    }

    private void resetPasswordMethod(String email) {
        Log.d("API_ERROR", "Sending Email to API: " + email);
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
                Log.d("API_ERROR","Code: "+response.code());

                if(response.isSuccessful() && response.body()!=null){

                    ForgotPassword forgotPassword = response.body();

                    Log.d("API_ERROR","ErrorCode: "+forgotPassword.getErrorCode());
                    Log.d("API_ERROR","Message: "+forgotPassword.getMessage());

                    if ("200".equals(forgotPassword.getErrorCode()) && forgotPassword.getResult() != null) {
                        ForgotPassword.Result result = forgotPassword.getResult();
                        Intent intent = new Intent(ForgotPasswordActivity.this, VerifyActivity.class);
                        intent.putExtra("studentId", result.getStudentId());
                        intent.putExtra("Otp", result.getOtp());
                        intent.putExtra("parentEmail", result.getParentEmail());
                        startActivity(intent);

                        Toast.makeText(ForgotPasswordActivity.this,
                                "OTP generated. Please check your email.",
                                Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(ForgotPasswordActivity.this,
                                forgotPassword.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }

                }else{

                    Log.e("API_ERROR","Response Failed");

                    Toast.makeText(ForgotPasswordActivity.this,
                            "Server Error",
                            Toast.LENGTH_LONG).show();
                }
            }


            @Override
            public void onFailure(Call<ForgotPassword> call, Throwable t) {
                Log.e("API_ERROR","Error: "+t.getMessage());

                Toast.makeText(ForgotPasswordActivity.this,
                        "Network Error",
                        Toast.LENGTH_LONG).show();
            }
        });

    }

    private boolean isValidEmail(String email) {
        if (email.isEmpty()) {
            //txtemail.setText("Enter Email");
            txtemail.setVisibility(View.VISIBLE);
            return false;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            txtemail.setText("Enter Valid Email");
            txtemail.setVisibility(View.VISIBLE);
            return false;
        }

        txtemail.setVisibility(View.GONE);
        return true;
    }
}