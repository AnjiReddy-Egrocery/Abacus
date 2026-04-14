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
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.signature.ObjectKey;
import com.dst.abacustrainner.Activity.AllocatedCoursesActivity;
import com.dst.abacustrainner.Activity.PurchasedActivity;
import com.dst.abacustrainner.Activity.UpdateProfileActivity;
import com.dst.abacustrainner.Model.BachDetailsResponse;
import com.dst.abacustrainner.Model.StudentTotalDetails;
import com.dst.abacustrainner.R;
import com.dst.abacustrainner.Services.ApiClient;
import com.dst.abacustrainner.User.HomeActivity;
import com.dst.abacustrainner.database.SharedPrefManager;

import java.text.SimpleDateFormat;
import java.util.Date;
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


public class ProfileFragment extends Fragment {

    private String studentId, fullName, imageUrl;
    TextView txtName,txtEmail,txtnumber,txtdob;
    ImageView imageProfile;

    Button butEditProfile;
    Button butViewMoreDetails, butSubdetails, butCourses;


    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        txtName = view.findViewById(R.id.txt_name);
        imageProfile = view.findViewById(R.id.image_profile);
        txtEmail = view.findViewById(R.id.txt_email);
        txtnumber = view.findViewById(R.id.txt_mobilenumber);
        txtdob = view.findViewById(R.id.txt_dateofbirth);

        butViewMoreDetails = view.findViewById(R.id.but_viewmoredetails);
        butSubdetails = view.findViewById(R.id.but_subscription_details);
        butCourses = view.findViewById(R.id.but_view_courses);

        butEditProfile = view.findViewById(R.id.but_edit_profile);

        StudentTotalDetails.Result studentdetails = SharedPrefManager.getInstance(getContext()).getUser();

        studentId = studentdetails.getStudentId();
        Log.d("Reddy",studentId);

        StudentDetailsMethod(studentId);
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

        butViewMoreDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openScheduleFragment();
            }
        });

        butSubdetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), PurchasedActivity.class);
                intent.putExtra("studentId",studentId);
                startActivity(intent);
            }
        });

        butCourses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), AllocatedCoursesActivity.class);
                intent.putExtra("studentId",studentId);
                startActivity(intent);
            }
        });



        return view;
    }

    private void openScheduleFragment() {
        SchedulesFragment scheduleFragment = new SchedulesFragment();
        Bundle args = new Bundle();
        args.putString("studentId", studentId);
        //args.putString("batchId",batchId);// pass studentId if needed
        scheduleFragment.setArguments(args);

        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.flFragment, scheduleFragment); // Make sure R.id.fragment_container is the correct container in your activity layout
        transaction.addToBackStack(null);  // So you can navigate back
        transaction.commit();
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
                if (!isAdded()) return; // Prevent crash if fragment is detached
                if (response.isSuccessful()) {
                    StudentTotalDetails studentTotalDetails = response.body();
                    Log.d("DEBUG", "Error Code: " + studentTotalDetails.getErrorCode());

                    String imageUrl = studentTotalDetails.getImageUrl() + studentTotalDetails.getResult().getProfilePic();

                    Glide.with(requireContext())
                            .load(imageUrl)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .signature(new ObjectKey(System.currentTimeMillis())) // forces fresh load
                            .circleCrop()
                            .into(imageProfile);

                    String firstName = capitalizeFirstLetter(studentTotalDetails.getResult().getFirstName());
                    String middleName = capitalizeFirstLetter(studentTotalDetails.getResult().getMiddleName());
                    String lastName = capitalizeFirstLetter(studentTotalDetails.getResult().getLastName());

                    String email = studentTotalDetails.getResult().getParentEmail();
                    String mobileNumber = studentTotalDetails.getResult().getFatherMobile();
                    String dateOfBirth = studentTotalDetails.getResult().getDateOfBirth();

                    try {
                        // 🔥 Convert string to long
                        long timestamp = Long.parseLong(dateOfBirth);

                        // 🔥 Convert seconds → milliseconds
                        Date date = new Date(timestamp * 1000);

                        // 🔥 Format date
                        SimpleDateFormat outputFormat =
                                new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());

                        String formattedDate = outputFormat.format(date);

                        txtdob.setText(formattedDate);

                        Log.d("DOB", formattedDate);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    String fullName = firstName +  middleName + " " + lastName;
                    txtName.setText(fullName);
                    txtEmail.setText(email);
                    txtnumber.setText(mobileNumber);




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



