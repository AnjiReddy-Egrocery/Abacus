package com.dst.abacustrainner.Fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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


public class ProfileFragment extends Fragment {

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

        StudentTotalDetails.Result studentdetails = SharedPrefManager.getInstance(getContext()).getUser();

        String studentid = studentdetails.getStudentId();

        StudentDetailsMethod(studentid);
        butEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("DEBUG", "Button clicked");
                Log.d("DEBUG", "StudentId: " + studentId);
                /*Intent intent = new Intent(getContext(), UpdateProfileActivity.class);
                startActivity(intent);*/
                Fragment fragment=new UpDateProfileFragment();
                getParentFragmentManager().beginTransaction().replace(R.id.flFragment,fragment).commit();

            }
        });


        return view;
    }



    private void StudentDetailsMethod(String studentId) {

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(3, TimeUnit.SECONDS)
                .readTimeout(3, TimeUnit.SECONDS)
                .writeTimeout(3, TimeUnit.SECONDS)
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

                    String firstName = capitalizeFirstLetter(studentTotalDetails.getResult().getFirstName());
                    String lastName = capitalizeFirstLetter(studentTotalDetails.getResult().getLastName());
                    String fullName = firstName + " " + lastName;
                    txtName.setText(fullName);

                    String imageUrl = studentTotalDetails.getImageUrl() + studentTotalDetails.getResult().getProfilePic();

                    Glide.with(requireActivity())
                            .load(imageUrl)
                            .placeholder(R.drawable.headerprofile)
                            .error(R.drawable.headerprofile)
                            .circleCrop()
                            .into(imageProfile);

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
}



