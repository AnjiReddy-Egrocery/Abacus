package com.dst.abacustrainner.Activity;

import androidx.annotation.LongDef;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.dst.abacustrainner.Model.StudentRegistationResponse;
import com.dst.abacustrainner.Model.StudentUpdateProfile;
import com.dst.abacustrainner.R;
import com.dst.abacustrainner.Services.ApiClient;
import com.dst.abacustrainner.User.HomeActivity;
import com.dst.abacustrainner.User.RegisterActivity;
import com.dst.abacustrainner.User.VerifyActivity;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
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
    String profilePic;
    ImageView imageView;
    private static final int REQUEST_CAMERA_PERMISSION = 100;
    private static final int GALLERY_REQUEST_CODE = 101;
    private Uri imageUri; // To store the selected image URI

    @SuppressLint("MissingInflatedId")
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

        imageView = findViewById(R.id.image_profile);

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
        profilePic = bundle.getString("profilePic");
        if (profilePic != null && !profilePic.isEmpty()) {
            Glide.with(this)
                    .load(profilePic)
                    .placeholder(R.drawable.headerprofile)
                    .error(R.drawable.headerprofile)
                    .circleCrop()
                    .into(imageView);  // Assuming you have an ImageView named imageProfile
        }


        Log.d("Reddy","Student"+studentId);
        Log.d("Reddy","Student"+firstName);
        Log.d("Reddy","Student"+middlename);
        Log.d("Reddy","Student"+lastName);
        Log.d("Reddy","Student"+dateofBirth);
        Log.d("Reddy","Student"+gender);
        Log.d("Reddy","Student"+motherTongue);
        Log.d("Reddy","Student"+fatherName);
        Log.d("Reddy","Student"+mothername);
        Log.d("Reddy","Student"+profilePic);



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

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Show a dialog or a menu to choose between camera or gallery
                AlertDialog.Builder builder = new AlertDialog.Builder(UpdateProfileActivity.this);
                builder.setTitle("Select Image")
                        .setItems(new String[]{"Camera", "Gallery"}, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == 0) {
                                    // Camera
                                    requestCameraPermissionAndOpenCamera(); // 👈 use this instead of openCamera()
                                } else {
                                    // Gallery
                                    openGallery();
                                }
                            }
                        })
                        .show();
            }
        });
    }

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE);
    }

    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                // Create an image file
                photoFile = createImageFile();
                imageUri = FileProvider.getUriForFile(this, "com.dst.abacustrainner.fileprovider", photoFile);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(cameraIntent, REQUEST_CAMERA_PERMISSION);
            } catch (IOException ex) {
                Log.e("UpdateProfileActivity", "Error creating file for camera image", ex);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CAMERA_PERMISSION) {
                // Handle camera image
                imageView.setImageURI(imageUri); // Set the captured image to ImageView
            } else if (requestCode == GALLERY_REQUEST_CODE) {
                // Handle gallery image
                if (data != null) {
                    Uri selectedImageUri = data.getData();
                    imageView.setImageURI(selectedImageUri); // Set the selected image to ImageView
                }
            }
        }
    }

    private void requestCameraPermissionAndOpenCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    REQUEST_CAMERA_PERMISSION);
        } else {
            openCamera(); // Already granted
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera(); // Permission granted
            } else {
                Toast.makeText(this, "Camera permission is required to take photos", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private File createImageFile() throws IOException {
        String imageFileName = "profile_image_" + System.currentTimeMillis();
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        return image;
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

        MultipartBody.Part imagePart = null;

        if (imageUri != null) {
            File file = new File(FileUtils.getPath(this, imageUri)); // 👈 Get real path
            RequestBody filePart = RequestBody.create(MediaType.parse("image/*"), file);
            imagePart = MultipartBody.Part.createFormData("profile_pic", file.getName(), filePart);
        }

        Call<StudentUpdateProfile> call=apiClient.studentUpdatePost(idPart,firstNamePart,middlenamePart,lastnamePart,dateofbirthPart,genderPart,mothertonguePart,fatherNamePart,matherNamePart,imagePart);
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
                        returnIntent.putExtra("profile_updated", true);
                        if (imageUri != null) {
                            returnIntent.putExtra("image_uri", imageUri.toString());
                            Log.d("ProfileUpdate", "Returning image URI: " + imageUri);
                        }
                        setResult(Activity.RESULT_OK, returnIntent);
                        Log.d("ProfileUpdate", "Result set with profile_updated: true");
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