package com.dst.abacustrainner.User;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import com.dst.abacustrainner.Model.StudentRegistationResponse;
import com.dst.abacustrainner.R;
import com.dst.abacustrainner.Services.ApiClient;
import com.dst.abacustrainner.database.SharedPrefManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {
    TextView txtForgotPassword,txtSignUp;
    Button butLogin;
    String parentEmail,password;
    EditText edtEmail,edtPassword;
    private SharedPreferences sharedPreferences;
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;

    RelativeLayout layoutAuth;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        txtSignUp=findViewById(R.id.txt_sigUp);
        txtForgotPassword=findViewById(R.id.txt_forgot_password);
        edtEmail=findViewById(R.id.edt_parent_email);
        edtPassword=findViewById(R.id.edt_password);
        butLogin=findViewById(R.id.but_login);
        layoutAuth=findViewById(R.id.RelativeLayout01);

        //one time login
        boolean b_user = SharedPrefManager.getInstance(getApplicationContext()).isLoggedIn();
        if (b_user) {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        }
        String txt="<font color=#434242>Don't have an account?</font> <font color=#2295D5> Sign up</font>";
        String text="<font color=#434242>Don't have an account?</font> <font color=#2295D5> Sign up</font>";
        txtSignUp.setText(Html.fromHtml(txt));
        txtSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
        txtForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(LoginActivity.this,ForgotPasswordActivity.class);
                startActivity(intent);
            }
        });
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

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.web_client_id)).requestEmail().build();
        gsc = GoogleSignIn.getClient(LoginActivity.this, gso);


        layoutAuth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SignIn();
            }
        });

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null) {
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            startActivity(intent);
        }
    }

    private void SignIn() {
        Intent intent = gsc.getSignInIntent();
        startActivityForResult(intent, 1000);
    }

    private boolean isValidPassword(String password) {
        return password.length() >= 6;
    }

    private boolean isValidEmail(String parentEmail) {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        return parentEmail.matches(emailPattern);
    }
   private void LoginMethod(String parentEmail, String password) {

     /*  HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
       loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
*/
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
                        Toast.makeText(LoginActivity.this, "Some thing went wrong, please Check Email and Password.", Toast.LENGTH_SHORT).show();
                    }else if (registrationResponse.getErrorCode().equals("200")) {
                        SharedPrefManager.getInstance(getApplicationContext()).insertData(response.body());
                        String firstname= registrationResponse.getResult().getFirstName();
                        String lastname = registrationResponse.getResult().getLastName();
                        Log.e("Reddy","Name"+firstname+lastname);
                        Toast.makeText(LoginActivity.this, "Student Login Completed Successfully.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                        intent.putExtra("FirstName",firstname);
                        intent.putExtra("LastName",lastname);
                        startActivity(intent);
                        finish();
                    }
                }else {
                    Toast.makeText(LoginActivity.this,"Data Error",Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(Call<StudentRegistationResponse> call, Throwable t) {
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                task.getResult(ApiException.class);
                finish();
                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                startActivity(intent);
            } catch (ApiException e) {
                Toast.makeText(LoginActivity.this, "Something went Wrong", Toast.LENGTH_LONG).show();
            }
        }
    }

}