package com.dst.abacustrainner.Fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.dst.abacustrainner.Activity.UpdateProfileActivity;
import com.dst.abacustrainner.Model.BachDetailsResponse;
import com.dst.abacustrainner.Model.StudentTotalDetails;
import com.dst.abacustrainner.R;
import com.dst.abacustrainner.Services.ApiClient;
import com.dst.abacustrainner.User.HomeActivity;
import com.dst.abacustrainner.database.SharedPrefManager;

import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class ProfileFragment extends Fragment {

    /*LinearLayout layoutPassword, layoutSettings,layoutTermsCondition,layoutLogOut;*/
    private String studentId, fullName, imageUrl;
    TextView txtName;
    ImageView imageProfile;

    Button butEditProfile;

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        txtName = view.findViewById(R.id.txt_name);
        imageProfile = view.findViewById(R.id.image_profile);

        butEditProfile = view.findViewById(R.id.but_edit_profile);

        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("ProfileData", Context.MODE_PRIVATE);
        String imageUriString = sharedPreferences.getString("imageUri", null);
        String name = sharedPreferences.getString("name", "No Name");

        Bundle bundle = getArguments();
        if (bundle != null) {
            studentId = bundle.getString("studentId");
            fullName = bundle.getString("name");
            imageUrl = bundle.getString("imageUrl");

            // Use studentId and fullName as needed
            Log.d("Reddy", "Student" + studentId);
            Log.d("Reddy", "Name" + fullName);
            Log.d("Reddy", "Name" + imageUrl);
        }

       /* if (imageUriString != null) {
            Uri imageUri = Uri.parse(imageUriString);
            Glide.with(requireContext())
                    .load(imageUri)
                    .placeholder(R.drawable.headerprofile)
                    .error(R.drawable.headerprofile)
                    .circleCrop()
                    .into(imageProfile);
        }
*/
        txtName.setText(fullName);

        Glide.with(requireContext())
                .load(imageUrl)
                .placeholder(R.drawable.headerprofile)
                .error(R.drawable.headerprofile)
                .circleCrop()
                .into(imageProfile);

        butEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("DEBUG", "Button clicked");
                Log.d("DEBUG", "StudentId: " + studentId);
                StudentDetailsMethod(studentId);
            }
        });


        return view;
    }

    private void StudentDetailsMethod(String studentId) {

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://www.abacustrainer.com/") // Replace with your API URL
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
        ApiClient apiClient = retrofit.create(ApiClient.class);
        RequestBody idPart = RequestBody.create(MediaType.parse("text/plain"), studentId);
        Call<StudentTotalDetails> call = apiClient.studentData(idPart);
        call.enqueue(new Callback<StudentTotalDetails>() {
            @Override
            public void onResponse(Call<StudentTotalDetails> call, Response<StudentTotalDetails> response) {
                Log.d("DEBUG", "API response received");
                if (response.isSuccessful()) {
                    StudentTotalDetails studentTotalDetails = response.body();
                    Log.d("DEBUG", "Error Code: " + studentTotalDetails.getErrorCode());

                    if (studentTotalDetails.getErrorCode().equals("202")) {
                        Toast.makeText(getContext(), "Invalid Request, no data found for your request", Toast.LENGTH_SHORT).show();
                    } else if (studentTotalDetails.getErrorCode().equals("200")) {
                        String firstName = "";
                        String middleName = "";
                        String lastName = "";
                        String daeofBirth = "";
                        String gender = "";
                        String motherTongue = "";
                        String fatherName = "";
                        String MotherName = "";
                        String profilePic = ""; // Add this line to hold the profilePic URL
                        StudentTotalDetails.Result results = studentTotalDetails.getResult();
                        firstName = results.getFirstName();
                        middleName = results.getMiddleName();
                        lastName = results.getLastName();
                        daeofBirth = results.getDateOfBirth();
                        gender = results.getGender();
                        fatherName = results.getFatherName();
                        MotherName = results.getMotherName();
                        motherTongue = results.getMotherTongue();
                        profilePic = results.getProfilePic(); // Assuming this is the field holding the image URL
                        String fullProfilePicUrl = "https://www.abacustrainer.com/assets/student_images/" + profilePic;

                        Log.d("Reddy","ImageUrl"+profilePic);


                        String formattedFirstName = capitalizeFirstLetter(firstName);
                        String formattedLastName = capitalizeFirstLetter(lastName);
                        String name = formattedFirstName + formattedLastName;
                        txtName.setText(name);


                        Intent intent = new Intent(getContext(), UpdateProfileActivity.class);
                        intent.putExtra("studentId", studentId);
                        intent.putExtra("firstName", firstName);
                        intent.putExtra("middleName", middleName);
                        intent.putExtra("lastName", lastName);
                        intent.putExtra("dateOfBirth", daeofBirth);
                        intent.putExtra("gender", gender);
                        intent.putExtra("motherTongue", motherTongue);
                        intent.putExtra("fatherName", fatherName);
                        intent.putExtra("motherName", MotherName);
                        intent.putExtra("profilePic", fullProfilePicUrl); // Add the profilePic data

                        startActivity(intent);


                    } else {
                        Toast.makeText(getContext(), "Data Error", Toast.LENGTH_LONG).show();
                    }


                } else {
                    Log.d("DEBUG", "Response not successful: " + response.code());
                    Toast.makeText(requireContext(), "Server Error: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<StudentTotalDetails> call, Throwable t) {
                Log.e("DEBUG", "API call failed", t);
                Toast.makeText(requireContext(), "API Failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private String capitalizeFirstLetter(String firstName) {
        if (firstName == null || firstName.isEmpty()) {
            return ""; // Return empty string if name is null or empty
        }
        return firstName.substring(0, 1).toUpperCase() + firstName.substring(1).toLowerCase();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1001 && resultCode == Activity.RESULT_OK && data != null) {

                StudentDetailsMethod(studentId); // optional: refresh full data from server
            }
        }
    }



