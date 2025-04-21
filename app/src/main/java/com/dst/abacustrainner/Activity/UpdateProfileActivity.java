package com.dst.abacustrainner.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.dst.abacustrainner.Model.StudentRegistationResponse;
import com.dst.abacustrainner.Model.StudentUpdateProfile;
import com.dst.abacustrainner.R;
import com.dst.abacustrainner.Services.ApiClient;
import com.dst.abacustrainner.User.HomeActivity;
import com.dst.abacustrainner.User.RegisterActivity;
import com.dst.abacustrainner.User.VerifyActivity;

import java.util.Date;
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

public class UpdateProfileActivity extends AppCompatActivity {

    String studentId,firstName,middlename,lastName,dateofBirth,gender,motherTongue,fatherName,mothername;

    EditText edtfirstName,edtmiddlename,edtLastname,edtDateofbirth,edtgender,edtmotherTongue,edtFathername,edtMotherName;
    private LinearLayout btnBack;

    private Button butUpdateProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);


        edtfirstName = findViewById(R.id.edt_first_name);
        edtmiddlename = findViewById(R.id.edt_middle_name);
        edtLastname = findViewById(R.id.edt_last_name);
        edtDateofbirth = findViewById(R.id.edt_date_birth);
        edtgender = findViewById(R.id.edt_gender);
        edtmotherTongue = findViewById(R.id.edt_mother_tongue);
        edtFathername = findViewById(R.id.edt_father_name);
        edtMotherName = findViewById(R.id.edt_Mother_name);
        butUpdateProfile = findViewById(R.id.but_update_profile);

        btnBack=findViewById(R.id.btn_back_to_home);

        Bundle bundle=getIntent().getExtras();

        studentId= bundle.getString("studentId");
        firstName = bundle.getString("firstName");
        middlename = bundle.getString("middleName");
        lastName = bundle.getString("lastName");
        dateofBirth = bundle.getString("dateOfBirth");
        gender = bundle.getString("gender");
        motherTongue = bundle.getString("motherTongue");
        fatherName = bundle.getString("fatherName");
        mothername = bundle.getString("motherName");

        Log.d("Reddy","Student"+studentId);
        Log.d("Reddy","Student"+firstName);
        Log.d("Reddy","Student"+middlename);
        Log.d("Reddy","Student"+lastName);
        Log.d("Reddy","Student"+dateofBirth);
        Log.d("Reddy","Student"+gender);
        Log.d("Reddy","Student"+motherTongue);
        Log.d("Reddy","Student"+fatherName);
        Log.d("Reddy","Student"+mothername);


        edtfirstName.setText(firstName);
        edtmiddlename.setText(middlename);
        edtLastname.setText(lastName);
        edtgender.setText(gender);
        edtmotherTongue.setText(motherTongue);
        edtFathername.setText(fatherName);
        edtMotherName.setText(mothername);


        if (dateofBirth != null && dateofBirth.matches("\\d+")) {
            // It's a timestamp, convert to readable date
            long timestamp = Long.parseLong(dateofBirth) * 1000L; // convert to milliseconds
            SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy", Locale.getDefault());
            String formattedDate = sdf.format(new Date(timestamp));
            edtDateofbirth.setText(formattedDate);
        } else {
            // Already in readable format like "06-27-1989"
            edtDateofbirth.setText(dateofBirth);
        }

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        butUpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firstName = edtfirstName.getText().toString().trim();
                middlename = edtmiddlename.getText().toString().trim();
                lastName = edtLastname.getText().toString().trim();
                dateofBirth = edtDateofbirth.getText().toString().trim();
                gender = edtgender.getText().toString().trim();
                motherTongue = edtmotherTongue.getText().toString().trim();
                fatherName = edtFathername.getText().toString().trim();
                mothername = edtMotherName.getText().toString().trim();

                ProfileUpdateMethod(studentId,firstName,middlename,lastName,dateofBirth,gender,motherTongue,fatherName,mothername);
            }
        });
    }

    private void ProfileUpdateMethod(String studentId, String firstName, String middlename, String lastName, String dateofBirth, String gender, String motherTongue, String fatherName, String mothername) {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://www.abacustrainer.com/") // Replace with your API URL
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
        ApiClient apiClient=retrofit.create(ApiClient.class);
        RequestBody idPart = RequestBody.create(MediaType.parse("text/plain"), studentId);
        RequestBody firstNamePart = RequestBody.create(MediaType.parse("text/plain"), firstName);
        RequestBody middlenamePart = RequestBody.create(MediaType.parse("text/plain"), middlename);
        RequestBody lastnamePart = RequestBody.create(MediaType.parse("text/plain"), lastName);
        RequestBody dateofbirthPart = RequestBody.create(MediaType.parse("text/plain"), dateofBirth);
        RequestBody genderPart = RequestBody.create(MediaType.parse("text/plain"), gender);
        RequestBody mothertonguePart = RequestBody.create(MediaType.parse("text/plain"), motherTongue);
        RequestBody fatherNamePart = RequestBody.create(MediaType.parse("text/plain"), fatherName);
        RequestBody matherNamePart = RequestBody.create(MediaType.parse("text/plain"), mothername);

        Call<StudentUpdateProfile> call=apiClient.studentUpdatePost(idPart,firstNamePart,middlenamePart,lastnamePart,dateofbirthPart,genderPart,mothertonguePart,fatherNamePart,matherNamePart);
        call.enqueue(new Callback<StudentUpdateProfile>() {
            @Override
            public void onResponse(Call<StudentUpdateProfile> call, Response<StudentUpdateProfile> response) {
                if (response.isSuccessful()){
                    StudentUpdateProfile registrationResponse = response.body();
                    Log.e("USERDADA","list: "+registrationResponse);
                    if(registrationResponse.getErrorCode() .equals("203")){
                        Toast.makeText(UpdateProfileActivity.this, "Email and Mobile Number alerady Exists", Toast.LENGTH_SHORT).show();

                    }else if (registrationResponse.getErrorCode() .equals("200")){
                        Toast.makeText(UpdateProfileActivity.this,"Detais Updated Success",Toast.LENGTH_LONG).show();
                        // Send result to ProfileFragment
                        Intent returnIntent = new Intent();
                        returnIntent.putExtra("profile_updated", true); // Set the flag
                        setResult(Activity.RESULT_OK, returnIntent);
                        finish();
                    }

                }else {
                    Toast.makeText(UpdateProfileActivity.this,"Data Error",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<StudentUpdateProfile> call, Throwable t) {

            }
        });

    }
}