package com.dst.abacustrainner.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dst.abacustrainner.Model.StudentUserMethod;
import com.dst.abacustrainner.R;
import com.dst.abacustrainner.Services.ApiClient;
import com.dst.abacustrainner.User.UserCreateActivity;
import com.dst.abacustrainner.User.VerifyActivity;
import com.dst.abacustrainner.database.SharedPrefManager;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ChangePasswordActivity extends AppCompatActivity {
    Button butUpdate;
    String id;
    EditText edtOldPassword,edtNewPassword,edtReEnterPwd;
    TextView txtOldPassword,txtPassword,txtConfirmPassword;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        butUpdate=findViewById(R.id.but_update);
        edtOldPassword=findViewById(R.id.edt_old_password);
        edtNewPassword=findViewById(R.id.newpassword);
        edtReEnterPwd=findViewById(R.id.edt_reenter_password);

        txtOldPassword = findViewById(R.id.txt_oldpassword);
        txtPassword = findViewById(R.id.txt_newpassword);
        txtConfirmPassword = findViewById(R.id.txt_confirm_password);

        Bundle bundle=getIntent().getExtras();

        id=bundle.getString("studentId");



        edtOldPassword.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                txtOldPassword.setVisibility(View.GONE);
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void afterTextChanged(Editable s) {}
        });
        edtNewPassword.addTextChangedListener(new TextWatcher() {
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

        butUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String oldPassword = edtOldPassword.getText().toString();
                String password = edtNewPassword.getText().toString();
                String reEnterPassword = edtReEnterPwd.getText().toString();

                boolean isValid = true;

                if (!isValidOldpassword(oldPassword)) {
                    isValid = false;
                }

                if (!isValidPassword(password)) {
                    isValid = false;
                }

                if (!doPasswordsMatch(password, reEnterPassword)) {
                    isValid = false;
                }

                if (isValid) {
                    updateMethod(id, oldPassword, password);
                }
            }
        });
    }

    private boolean isValidPassword(String password) {
        if (password.length() < 6) {
            txtPassword.setVisibility(View.VISIBLE);
            return false;
        }

        txtPassword.setVisibility(View.GONE);
        return true;
    }

    private boolean doPasswordsMatch(String password, String reEnterPassword) {
        if (!password.equals(reEnterPassword)) {
            txtConfirmPassword.setVisibility(View.VISIBLE);
            return false;
        }

        txtConfirmPassword.setVisibility(View.GONE);
        return true;
    }

    private boolean isValidOldpassword(String oldPassword) {
        if (oldPassword.length() < 6) {
            txtOldPassword.setVisibility(View.VISIBLE);
            return false;
        }

        txtOldPassword.setVisibility(View.GONE);
        return true;
    }

    private void updateMethod(String id, String oldPassword, String password) {
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
        RequestBody oldpasswordPart = RequestBody.create(MediaType.parse("text/plain"), oldPassword);
        RequestBody passwordPart = RequestBody.create(MediaType.parse("text/plain"), password);

        Call<StudentUserMethod> call=apiClient.changepassword(idPart,oldpasswordPart,passwordPart);
        call.enqueue(new Callback<StudentUserMethod>() {
            @Override
            public void onResponse(Call<StudentUserMethod> call, Response<StudentUserMethod> response) {

                    if (response.isSuccessful()){

                        StudentUserMethod registrationResponse = response.body();
                        if (registrationResponse.getErrorCode().equals("203")) {
                            // Registration was successful
                            Toast.makeText(ChangePasswordActivity.this, "Invalid Old Password", Toast.LENGTH_SHORT).show();
                        } else if (registrationResponse.getErrorCode().equals("200")){

                            Log.d("DEBUG","Entered 200 block");

                            SharedPrefManager
                                    .getInstance(getApplicationContext())
                                    .isLoggedOut();

                            finish();

                        }
                    }else {
                        Toast.makeText(ChangePasswordActivity.this,
                                "Response Empty",
                                Toast.LENGTH_LONG).show();
                    }
                }
                @Override
                public void onFailure(Call<StudentUserMethod> call, Throwable t) {
                    Log.e("API_ERROR", t.getMessage());
                }
            });

    }
}