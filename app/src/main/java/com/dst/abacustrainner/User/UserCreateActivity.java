package com.dst.abacustrainner.User;

import android.Manifest;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.dst.abacustrainner.Model.StudentRegistationResponse;
import com.dst.abacustrainner.R;
import com.dst.abacustrainner.Services.ApiClient;
import com.dst.abacustrainner.database.SharedPrefManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UserCreateActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

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

    Button butSchools;
    private static final int REQUEST_PHONE_NUMBER_PERMISSION = 101;
    AlertDialog loadingDialog;
    private int simRetryCount = 0;
    private static final int MAX_SIM_RETRY = 5;

    private static final int REQUEST_CODE_EMAIL_PICKER = 2001;
    private static final String GOOGLE_ACCOUNT_TYPE = "com.google";
    private GoogleApiClient mGoogleApiClient;
    private static final int RC_SIGN_IN = 9001;
    private static final int RC_SIGN_UP_WITH_GOOGLE = 9002;
    private static final int CREDENTIAL_PICKER_REQUEST = 1001;
    private String apiDob = "";

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

        //butSchools = findViewById(R.id.but_schools);

        calendar = Calendar.getInstance();

        txtForgotPassword = findViewById(R.id.txt_forgot_password);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        edtDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog();
            }
        });

     /*   edtNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkAndRequestPermissions();
            }
        });*/
        /*edtRegisterEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermissionAndShowAccounts();
            }
        });*/

       /* butSchools.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SchoolsMethod();
            }
        });*/

        layoutTextSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SignUpVisiableMethod();
                changeTextColors(false);
                FormRegisterMethod();
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

/*    private void checkPermissionAndShowAccounts() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.GET_ACCOUNTS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.GET_ACCOUNTS}, 123);
        } else {
            showAccountPicker();
        }
    }

    private void showAccountPicker() {
        Intent intent = AccountManager.newChooseAccountIntent(
                null, null,
                new String[]{GOOGLE_ACCOUNT_TYPE},
                false, null, null, null, null
        );
        startActivityForResult(intent, REQUEST_CODE_EMAIL_PICKER);
    }*/


 /*   private void checkAndRequestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Check both permissions
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this,
                        new String[]{
                                Manifest.permission.READ_PHONE_NUMBERS,
                                Manifest.permission.READ_PHONE_STATE
                        }, REQUEST_PHONE_NUMBER_PERMISSION);
            } else {
                getSimNumbers();
            }
        }
    }*/

/*    private void getSimNumbers() {
        showLoadingDialog(); // 👈 show loader

        ArrayList<String> simNumbers = new ArrayList<>();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_NUMBERS)
                != PackageManager.PERMISSION_GRANTED) {
            hideLoadingDialog();
            return;
        }

        SubscriptionManager subscriptionManager = (SubscriptionManager) getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);
        List<SubscriptionInfo> subscriptionInfoList = subscriptionManager.getActiveSubscriptionInfoList();

        if (subscriptionInfoList == null || subscriptionInfoList.isEmpty()) {
            if (simRetryCount < MAX_SIM_RETRY) {
                simRetryCount++;
                new Handler(Looper.getMainLooper()).postDelayed(this::getSimNumbers, 2000);
            } else {
                hideLoadingDialog(); // 👈 hide if failed
                Toast.makeText(this, "Unable to fetch SIM details.", Toast.LENGTH_SHORT).show();
            }
            return;
        }

        simRetryCount = 0;
        for (SubscriptionInfo info : subscriptionInfoList) {
            String phoneNumber = info.getNumber();
            int simSlot = info.getSimSlotIndex();
            String carrierName = info.getCarrierName().toString();

            if (phoneNumber == null || phoneNumber.isEmpty()) {
                phoneNumber = "Number not available";
            }

            simNumbers.add("SIM " + (simSlot + 1) + " (" + carrierName + "): " + phoneNumber);
        }

        hideLoadingDialog(); // 👈 hide after success

        if (!simNumbers.isEmpty()) {
            showSimSelectionDialog(simNumbers);
        }
    }*/

    private void showLoadingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(R.layout.dialog_loading); // we'll create this layout next
        builder.setCancelable(false);
        loadingDialog = builder.create();
        loadingDialog.show();
    }
    private void hideLoadingDialog() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
    }

    private void showSimSelectionDialog(ArrayList<String> simNumbers) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select SIM Number");
        String[] simArray = simNumbers.toArray(new String[0]);

        builder.setItems(simArray, (dialog, which) -> {
            String[] parts = simNumbers.get(which).split(": ");
            String selectedSimNumber = (parts.length > 1) ? parts[1].trim() : simNumbers.get(which);
            edtNumber.setText(selectedSimNumber);
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> {
            // Enable manual input if user cancels
            edtNumber.setFocusable(true);
            edtNumber.setFocusableInTouchMode(true);
            edtNumber.requestFocus();

            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.showSoftInput(edtNumber, InputMethodManager.SHOW_IMPLICIT);
            }
        });
        builder.show();
    }

    /*@Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_PHONE_NUMBER_PERMISSION) {
            boolean allGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }

            if (allGranted) {
                getSimNumbers();
            } else {
                Toast.makeText(this, "Permission denied.", Toast.LENGTH_SHORT).show();
            }
        }

        if (requestCode == 123 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            showAccountPicker();
        }
    }*/





    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CREDENTIAL_PICKER_REQUEST && resultCode == RESULT_OK) {
            if (data != null) {
                Credential credential = data.getParcelableExtra(Credential.EXTRA_KEY);
                if (credential != null) {
                    edtNumber.setText(credential.getId());  // Display the retrieved number
                }
            }
        }

        if (requestCode == REQUEST_CODE_EMAIL_PICKER && resultCode == RESULT_OK) {
            if (data != null) {
                String selectedEmail = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                if (selectedEmail != null) {
                    edtRegisterEmail.setText(selectedEmail);
                }
            }
        }
    }

    @SuppressLint("MissingInflatedId")
    private void SchoolsMethod() {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_custom, null);

        // Reference to EditText
        EditText editText = dialogView.findViewById(R.id.edt_school_code);

        // Create AlertDialog
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle("Enter School Code")
                .setIcon(R.drawable.gts_logo) // Replace with your logo
                .setView(dialogView)
                .setPositiveButton("Submit", null)  // Null for custom click handling
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .create();
        alertDialog.show();

        // Custom Click Handling for Submit
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String schoolCode = editText.getText().toString().trim();
            if (schoolCode.isEmpty()) {
                editText.setError("Please enter school code");
                editText.requestFocus();
            } else {
                Intent intent = new Intent(UserCreateActivity.this, StudentLoginActivity.class);
                intent.putExtra("school_code", schoolCode);
                startActivity(intent);
                alertDialog.dismiss();
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
                // UI format (user friendly)
                SimpleDateFormat uiFormat =
                        new SimpleDateFormat("dd-MM-yyyy", Locale.US);
                edtDate.setText(uiFormat.format(calendar.getTime()));

                // API format (backend required)
                SimpleDateFormat apiFormat =
                        new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                apiDob = apiFormat.format(calendar.getTime());
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

                    registerMenthod(firstName, lastName, mobileNumber, registeremail, apiDob, tongue);
                } else {
                    Toast.makeText(UserCreateActivity.this, "Validation failed. Please check your input.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void registerMenthod(String firstName, String lastName, String mobileNumber, String registeremail, String date, String tongue) {
        String selectedGender = getSelectedGender();
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .addInterceptor(loggingInterceptor)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://www.abacustrainer.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
        ApiClient apiClient=retrofit.create(ApiClient.class);
        RequestBody firstNamePart = RequestBody.create(MediaType.parse("text/plain"), firstName);
        RequestBody lastnamePart = RequestBody.create(MediaType.parse("text/plain"), lastName);
        RequestBody genderPart = RequestBody.create(MediaType.parse("text/plain"), selectedGender);
        RequestBody dateofbirthPart = RequestBody.create(MediaType.parse("text/plain"), date);
        RequestBody mothertonguePart = RequestBody.create(MediaType.parse("text/plain"), tongue);
        RequestBody emailPart = RequestBody.create(MediaType.parse("text/plain"), registeremail);
        RequestBody mobilenumberPart = RequestBody.create(MediaType.parse("text/plain"), mobileNumber);

        Call<StudentRegistationResponse> call=apiClient.studentRegisterPost(firstNamePart,lastnamePart,genderPart,dateofbirthPart,mothertonguePart, emailPart,mobilenumberPart);
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

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // Connection failed, show error message
        Toast.makeText(UserCreateActivity.this, "Connection to Google Play services failed. Please try again.", Toast.LENGTH_LONG).show();
    }

}