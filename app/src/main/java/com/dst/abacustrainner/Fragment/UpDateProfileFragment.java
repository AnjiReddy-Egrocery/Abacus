package com.dst.abacustrainner.Fragment;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.dst.abacustrainner.Model.StudentTotalDetails;
import com.dst.abacustrainner.Model.StudentUpdateProfile;
import com.dst.abacustrainner.R;
import com.dst.abacustrainner.Services.ApiClient;
import com.dst.abacustrainner.User.HomeActivity;
import com.dst.abacustrainner.User.RegisterActivity;
import com.dst.abacustrainner.database.SharedPrefManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

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


public class UpDateProfileFragment extends Fragment {

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
    private Calendar calendar;
    public UpDateProfileFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.activity_update_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        edtfirstName = view.findViewById(R.id.edt_first_name);
        edtmiddlename =view. findViewById(R.id.edt_middle_name);
        edtLastname = view.findViewById(R.id.edt_last_name);
        edtDateofbirth =view. findViewById(R.id.edt_date_birth);
        edtgender =view. findViewById(R.id.edt_gender);
        edtmotherTongue =view. findViewById(R.id.edt_mother_tongue);
        edtFathername = view.findViewById(R.id.edt_father_name);
        edtMotherName = view.findViewById(R.id.edt_Mother_name);
        butUpdateProfile =view. findViewById(R.id.but_update_profile);

        imageView =view. findViewById(R.id.image_profile);

        btnBack =view. findViewById(R.id.btn_back_to_home);

        StudentTotalDetails.Result studentdetails = SharedPrefManager.getInstance(getContext()).getUser();

        studentid = studentdetails.getStudentId();

          StudentDetailsMethod(studentid);

        calendar = Calendar.getInstance();

        edtDateofbirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog();
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

                if (imageFile == null || !imageFile.exists()) {
                    Toast.makeText(getContext(), "Please select a profile picture", Toast.LENGTH_SHORT).show();
                    return;
                }
                ProfileUpdateMethod(studentid, firstName, middlename, lastName, dateofBirth, gender, motherTongue, fatherName, mothername, imageFile);

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

    private void showDatePickerDialog() {
        DatePickerDialog.OnDateSetListener dateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, day);
                java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("MM-dd-yyyy", Locale.US);
                edtDateofbirth.setText(dateFormat.format(calendar.getTime()));
            }
        };
        new DatePickerDialog(
                getContext(),
                dateListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        ).show();
    }

    private void showImagePickerDialog() {
        String[] options = {"Camera", "Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Select Image From");
        builder.setItems(options, (dialog, which) -> {
            if (which == 0) {
                checkCameraPermissionAndOpenCamera();
            } else {
                openGallery();
            }
        });
        builder.show();
    }

    private void checkCameraPermissionAndOpenCamera() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // Request permission
            requestPermissions(new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST);
        } else {
            openCamera(); // Permission already granted
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(getContext(), "Camera permission is required", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getContext().getPackageManager()) != null) {
            try {
                File photoFile = createImageFile();
                imageUri = FileProvider.getUriForFile(getContext(), getContext().getPackageName() + ".provider", photoFile);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private File createImageFile() throws IOException {
        String fileName = "IMG_" + System.currentTimeMillis();
        File storageDir = getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(fileName, ".jpg", storageDir);
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, GALLERY_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            try {
                Bitmap originalBitmap = null;

                if (requestCode == CAMERA_REQUEST) {
                    if (imageUri != null) {
                        originalBitmap = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), imageUri);
                        Log.d("ImagePicker", "Image captured from camera");
                    }
                } else if (requestCode == GALLERY_REQUEST && data != null) {
                    imageUri = data.getData();
                    if (imageUri != null) {
                        originalBitmap = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), imageUri);
                        Log.d("ImagePicker", "Image selected from gallery");
                    }
                }

                if (originalBitmap != null) {
                    Bitmap resizedBitmap = Bitmap.createScaledBitmap(originalBitmap, originalBitmap.getWidth(), originalBitmap.getHeight(), true);

                    Glide.with(this) // or requireContext()
                            .load(resizedBitmap)
                            .placeholder(R.drawable.headerprofile)
                            .error(R.drawable.headerprofile)
                            .circleCrop()
                            .into(imageView);  // make sure imageView is initialized

                    imageFile = new File(requireContext().getCacheDir(), "resized_image.jpg");

                    try (FileOutputStream fos = new FileOutputStream(imageFile)) {
                        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                        fos.flush();
                        Log.d("ImagePicker", "Image resized and saved: " + imageFile.getAbsolutePath());
                    }

                } else {
                    Log.e("ImagePicker", "Bitmap is null");
                    Toast.makeText(requireContext(), "Image load failed!", Toast.LENGTH_SHORT).show();
                }

            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(requireContext(), "Image processing failed", Toast.LENGTH_SHORT).show();
            }
        }
    }



    private void ProfileUpdateMethod(String studentId, String firstName, String middlename, String lastName, String dateofBirth, String gender, String motherTongue, String fatherName, String mothername, File imageFile) {

        Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getPath());
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth(), bitmap.getHeight(), true);

        // ✅ 2. Save scaled image to a new file
        File resizedFile = new File(getContext().getCacheDir(), "resized_image.jpg");
        try (FileOutputStream out = new FileOutputStream(resizedFile)) {
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        // ✅ 3. Replace original file with resized one
        imageFile = resizedFile;


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
                idPart, firstNamePart, middlenamePart, lastnamePart, dateofbirthPart, genderPart,
                mothertonguePart, fatherNamePart, matherNamePart, imagePart
        );

        call.enqueue(new Callback<StudentUpdateProfile>() {
            @Override
            public void onResponse(Call<StudentUpdateProfile> call, Response<StudentUpdateProfile> response) {
                Log.d("ImagePicker", "onResponse() called");

                if (response.isSuccessful()) {
                    Log.d("ImagePicker", "API call successful");

                    Fragment fragment=new ProfileFragment();

                    FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                    transaction.replace(R.id.flFragment, fragment); // Make sure R.id.fragment_container is the correct container in your activity layout
                    transaction.addToBackStack(null);  // So you can navigate back
                    transaction.commit();


                    if (getActivity() instanceof HomeActivity) {
                        ((HomeActivity) getActivity()).StudentDetailsMethod(studentid);
                    }


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

                    edtfirstName.setText(studentTotalDetails.getResult().getFirstName());
                    edtmiddlename.setText(studentTotalDetails.getResult().getMiddleName());
                    edtLastname.setText(studentTotalDetails.getResult().getLastName());
                    edtgender.setText(studentTotalDetails.getResult().getGender());
                    edtmotherTongue.setText(studentTotalDetails.getResult().getMotherTongue());
                    edtFathername.setText(studentTotalDetails.getResult().getFatherName());
                    edtMotherName.setText(studentTotalDetails.getResult().getMotherName());

                    String imageUrl = studentTotalDetails.getImageUrl() + studentTotalDetails.getResult().getProfilePic();

                   Glide.with(getContext())
                            .load(imageUrl)
                            .placeholder(R.drawable.headerprofile)
                            .error(R.drawable.headerprofile)
                            .skipMemoryCache(true) // ✅ Prevents loading from memory
                            .diskCacheStrategy(DiskCacheStrategy.NONE) // ✅ Avoid disk cache
                            .circleCrop()
                            .into(imageView);
                    dateofBirth = studentTotalDetails.getResult().getDateOfBirth();

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
                    Toast.makeText(getContext(), "Server Error: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<StudentTotalDetails> call, Throwable t) {
                Log.e("DEBUG", "API call failed", t);
                Toast.makeText(getContext(), "API Failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}
