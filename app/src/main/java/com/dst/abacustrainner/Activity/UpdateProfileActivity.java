package com.dst.abacustrainner.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.dst.abacustrainner.Fragment.ProfileFragment;
import com.dst.abacustrainner.Model.StudentTotalDetails;
import com.dst.abacustrainner.Model.StudentUpdateProfile;
import com.dst.abacustrainner.R;
import com.dst.abacustrainner.Services.ApiClient;
import com.dst.abacustrainner.User.HomeActivity;
import com.dst.abacustrainner.database.SharedPrefManager;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UpdateProfileActivity extends FragmentActivity {

    String studentId, firstName, middlename, lastName, dateofBirth, gender, motherTongue, fatherName, mothername;

    EditText edtfirstName, edtmiddlename, edtLastname, edtDateofbirth, edtgender, edtmotherTongue, edtFathername, edtMotherName;
    private LinearLayout btnBack;

    private Button butUpdateProfile;
    String profilePic;
    ImageView imageView;
    private static final int CAMERA_REQUEST = 100;
    private static final int GALLERY_REQUEST = 101;

    private Uri imageUri;
    private File imageFile;
    String studentid;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);


        edtfirstName = findViewById(R.id.edt_first_name);

        edtDateofbirth = findViewById(R.id.edt_date_birth);
        edtgender = findViewById(R.id.edt_gender);
        edtmotherTongue = findViewById(R.id.edt_mother_tongue);
        edtFathername = findViewById(R.id.edt_father_name);
        edtMotherName = findViewById(R.id.edt_Mother_name);
        butUpdateProfile = findViewById(R.id.but_update_profile);

        imageView = findViewById(R.id.image_profile);

        btnBack = findViewById(R.id.btn_back_to_home);

      /*  Bundle bundle = getIntent().getExtras();

        studentId = bundle.getString("studentId");
        firstName = bundle.getString("firstName");
        middlename = bundle.getString("middleName");
        lastName = bundle.getString("lastName");
        dateofBirth = bundle.getString("dateOfBirth");
        gender = bundle.getString("gender");
        motherTongue = bundle.getString("motherTongue");
        fatherName = bundle.getString("fatherName");
        mothername = bundle.getString("motherName");
        profilePic = bundle.getString("profilePic");
        Glide.get(UpdateProfileActivity.this).clearMemory();
        if (profilePic != null && !profilePic.isEmpty()) {
            Glide.with(this)
                    .load(profilePic)
                    .placeholder(R.drawable.headerprofile)
                    .error(R.drawable.headerprofile)
                    .circleCrop()
                    .into(imageView);  // Assuming you have an ImageView named imageProfile
        }*/


        StudentTotalDetails.Result studentdetails = SharedPrefManager.getInstance(getApplicationContext()).getUser();

        studentid = studentdetails.getStudentId();

      //  StudentDetailsMethod(studentid);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        butUpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               /* firstName = edtfirstName.getText().toString().trim();
                middlename = edtmiddlename.getText().toString().trim();
                lastName = edtLastname.getText().toString().trim();
                dateofBirth = edtDateofbirth.getText().toString().trim();
                gender = edtgender.getText().toString().trim();
                motherTongue = edtmotherTongue.getText().toString().trim();
                fatherName = edtFathername.getText().toString().trim();
                mothername = edtMotherName.getText().toString().trim();*/

             /*   if (imageFile == null || !imageFile.exists()) {
                    Toast.makeText(UpdateProfileActivity.this, "Please select a profile picture", Toast.LENGTH_SHORT).show();
                    return;
                }*/
               // ProfileUpdateMethod(studentid, firstName, middlename, lastName, dateofBirth, gender, motherTongue, fatherName, mothername, imageFile);
                Fragment fragment=new ProfileFragment();

                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.flFragment, fragment); // Make sure R.id.fragment_container is the correct container in your activity layout
                transaction.addToBackStack(null);  // So you can navigate back
                transaction.commit();
                /*Intent intent = new Intent(UpdateProfileActivity.this, HomeActivity.class);
                startActivity(intent);*/
            }

        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Show a dialog or a menu to choose between camera or gallery
                showImagePickerDialog();
            }
        });
    }



    private void showImagePickerDialog() {
        String[] options = {"Camera", "Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Image From");
        builder.setItems(options, (dialog, which) -> {
            if (which == 0) {
                openCamera();
            } else {
                openGallery();
            }
        });
        builder.show();
    }

    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            try {
                File photoFile = createImageFile();
                imageUri = FileProvider.getUriForFile(this, getPackageName() + ".provider", photoFile);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private File createImageFile() throws IOException {
        String fileName = "IMG_" + System.currentTimeMillis();
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(fileName, ".jpg", storageDir);
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, GALLERY_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            try {
                Bitmap originalBitmap = null;

                if (requestCode == CAMERA_REQUEST) {
                    // Capture from camera
                    if (imageUri != null) {
                        originalBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                        Log.d("ImagePicker", "Image captured from camera");
                    }
                } else if (requestCode == GALLERY_REQUEST && data != null) {
                    // Picked from gallery
                    imageUri = data.getData();
                    if (imageUri != null) {
                        originalBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                        Log.d("ImagePicker", "Image selected from gallery");
                    }
                }

                if (originalBitmap != null) {
                    // Resize to 200x200
                    Bitmap resizedBitmap = Bitmap.createScaledBitmap(originalBitmap, 200, 200, true);

                    // ✅ Set image to ImageView
                   // imageView.setImageBitmap(resizedBitmap);
                    Glide.with(this)
                            .load(resizedBitmap)
                            .placeholder(R.drawable.headerprofile)
                            .error(R.drawable.headerprofile)
                            .circleCrop()
                            .into(imageView);  // Assuming you have an ImageView named imageProfile


                    // Save resized bitmap to a file
                    imageFile = new File(getCacheDir(), "resized_image.jpg");

                    // ztodo ramana
                    try (FileOutputStream fos = new FileOutputStream(imageFile)) {
                        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                        fos.flush();
                        Log.d("ImagePicker", "Image resized and saved: " + imageFile.getAbsolutePath());
                    }

                } else {
                    Log.e("ImagePicker", "Bitmap is null");
                    Toast.makeText(this, "Image load failed!", Toast.LENGTH_SHORT).show();
                }

            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Image processing failed", Toast.LENGTH_SHORT).show();
            }
        }
    }




    private void ProfileUpdateMethod(String studentId, String firstName, String middlename, String lastName, String dateofBirth, String gender, String motherTongue, String fatherName, String mothername, File imageFile) {

        Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getPath());
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, 200, 200, true);

        // ✅ 2. Save scaled image to a new file
        File resizedFile = new File(getCacheDir(), "resized_image.jpg");
        try (FileOutputStream out = new FileOutputStream(resizedFile)) {
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        // ✅ 3. Replace original file with resized one
        imageFile = resizedFile;

        // ✅ 4. Log image details to confirm
        Log.d("ImagePicker", "Resized Width: " + scaledBitmap.getWidth() + " Height: " + scaledBitmap.getHeight());
        Log.d("ImagePicker", "Resized File Path: " + imageFile.getAbsolutePath());
        Log.d("ImagePicker", "Resized File Size: " + imageFile.length() / 1024 + " KB");

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://www.abacustrainer.com/") // Replace with your API URL
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        ApiClient apiClient = retrofit.create(ApiClient.class);

        // Create RequestBody for the fields
        RequestBody idPart = RequestBody.create(MediaType.parse("text/plain"), studentId);
        RequestBody firstNamePart = RequestBody.create(MediaType.parse("text/plain"), firstName);
        RequestBody middlenamePart = RequestBody.create(MediaType.parse("text/plain"), middlename);
        RequestBody lastnamePart = RequestBody.create(MediaType.parse("text/plain"), lastName);
        RequestBody dateofbirthPart = RequestBody.create(MediaType.parse("text/plain"), dateofBirth);
        RequestBody genderPart = RequestBody.create(MediaType.parse("text/plain"), gender);
        RequestBody mothertonguePart = RequestBody.create(MediaType.parse("text/plain"), motherTongue);
        RequestBody fatherNamePart = RequestBody.create(MediaType.parse("text/plain"), fatherName);
        RequestBody matherNamePart = RequestBody.create(MediaType.parse("text/plain"), mothername);

        // Send Base64 image string
        MultipartBody.Part imagePart = null;
        if (imageFile != null && imageFile.exists()) {
            RequestBody imageRequestBody = RequestBody.create(MediaType.parse("image/jpeg"), imageFile);
            imagePart = MultipartBody.Part.createFormData("profilePic", imageFile.getName(), imageRequestBody);
        }

        // Call API with the data
        Call<StudentUpdateProfile> call = apiClient.studentUpdatePost(
                idPart, firstNamePart, dateofbirthPart, genderPart,
                mothertonguePart, fatherNamePart, matherNamePart, imagePart
        );

        call.enqueue(new Callback<StudentUpdateProfile>() {
            @Override
            public void onResponse(Call<StudentUpdateProfile> call, Response<StudentUpdateProfile> response) {
                Log.d("ImagePicker", "onResponse() called");

                if (response.isSuccessful()) {
                    Log.d("ImagePicker", "API call successful");

                    /*Fragment fragment=new ProfileFragment();
                    *//*Bundle args = new Bundle();
                    // args.putString("studentId", id);
                    args.putString("ProfilePic",cleanBase64String);// pass studentId if needed
                    fragment.setArguments(args);*//*
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.flFragment, fragment); // Make sure R.id.fragment_container is the correct container in your activity layout
                    transaction.addToBackStack(null);  // So you can navigate back
                    transaction.commit();*/

                    /*StudentUpdateProfile updatedProfile = response.body();
                    Log.d("ImagePicker", "Full Response Body: " + new Gson().toJson(updatedProfile)); // ✅ Log full response

                    if (updatedProfile != null && updatedProfile.getResult() != null) {
                        Log.d("ImagePicker", "Profile result not null");

                        String profilePic = updatedProfile.getResult().getProfilePic();
                        Log.d("ImagePicker", "ProfilePic: " + profilePic); // ✅ Log actual value

                        if (profilePic != null && !profilePic.isEmpty()) {
                            String cleanBase64String = profilePic.replace("\\/", "/");

                            Fragment fragment=new ProfileFragment();
                            Bundle args = new Bundle();
                           // args.putString("studentId", id);
                            args.putString("ProfilePic",cleanBase64String);// pass studentId if needed
                            fragment.setArguments(args);
                            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                            transaction.replace(R.id.flFragment, fragment); // Make sure R.id.fragment_container is the correct container in your activity layout
                            transaction.addToBackStack(null);  // So you can navigate back
                            transaction.commit();

                            *//*Intent returnIntent = new Intent();
                            returnIntent.putExtra("profile_updated", true);
                            returnIntent.putExtra("profile_pic_base64", cleanBase64String);  // optional
                            setResult(Activity.RESULT_OK, returnIntent);
                            finish();*//*

                            if (imageUri != null) {
                                //returnIntent.putExtra("image_uri", imageUri.toString());
                                Log.d("ProfileUpdate", "Returning image URI: " + imageUri);
                            }

                           // setResult(Activity.RESULT_OK, returnIntent);
                            Log.d("ProfileUpdate", "Result set with profile_updated: true");
                            finish();
                        } else {
                            Log.d("ImagePicker", "ProfilePic is null or empty");
                        }
                    } else {
                        Log.d("ImagePicker", "updatedProfile or result is null");
                    }
                } else {
                    Log.d("ImagePicker", "API error: " + response.code() + " - " + response.message());
                }*/
                }
            }

            @Override
            public void onFailure(Call<StudentUpdateProfile> call, Throwable t) {
                Log.e("ImagePicker", "API call failed: ", t);
            }
        });
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

                    edtfirstName.setText(studentTotalDetails.getResult().getFullName());

                    edtgender.setText(studentTotalDetails.getResult().getGender());
                    edtmotherTongue.setText(studentTotalDetails.getResult().getMotherTongue());
                    edtFathername.setText(studentTotalDetails.getResult().getFatherName());
                    edtMotherName.setText(studentTotalDetails.getResult().getMotherName());

                    String imageUrl = studentTotalDetails.getImageUrl() + studentTotalDetails.getResult().getProfilePic();

                    Glide.with(getApplicationContext())
                            .load(imageUrl)
                            .placeholder(R.drawable.headerprofile)
                            .error(R.drawable.headerprofile)
                            .circleCrop()
                            .into(imageView);



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




                } else {
                    Log.d("DEBUG", "Response not successful: " + response.code());
                    Toast.makeText(getApplicationContext(), "Server Error: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<StudentTotalDetails> call, Throwable t) {
                Log.e("DEBUG", "API call failed", t);
                Toast.makeText(getApplicationContext(), "API Failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}




