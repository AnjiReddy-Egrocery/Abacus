package com.dst.abacustrainner.database;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.dst.abacustrainner.Model.StudentRegistationResponse;
import com.dst.abacustrainner.Model.StudentTotalDetails;
import com.dst.abacustrainner.User.UserCreateActivity;


public class SharedPrefManager {

    static SharedPrefManager sharedPrefManager;
    private SharedPreferences sharedPreferences;
    Context mContext;
    private static final String SHARED_PREF_NAME = "userProfile";
    private static final String STUDENT_ID="studentId";
    private static final String FIRST_NAME="firstName";
    private static final String LAST_NAME="lastName";
    private static final String EMAIL_ID="emailId";
    private static final String FATHER_MOBILE="fatherMobile";
    private static final String PARENT_EMAIL="parentEmail";

    private  static final String Profile_PIC="profilePic";
    private static final String SHARED_PREFS_NAME = "abacus_pref";
    private SharedPrefManager(Context context) {
        mContext=context;
        sharedPreferences = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
    }


    public static synchronized SharedPrefManager getInstance(Context context){
        if (sharedPrefManager==null){
            sharedPrefManager=new SharedPrefManager(context);
        }
        return sharedPrefManager;
    }
    //insert user data
    public void insertData(StudentRegistationResponse userInfo){

        SharedPreferences sharedPreferences =
                mContext.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();

        if (userInfo.getResult() != null && userInfo.getResult().size() > 0) {

            // first student
            StudentRegistationResponse.Result result = userInfo.getResult().get(0);

            editor.putString(STUDENT_ID, result.getStudentId());
            editor.putString(FIRST_NAME, result.getFirstName());
            editor.putString(LAST_NAME, result.getLastName());
            editor.putString(EMAIL_ID, result.getEmailId());
            editor.putString(FATHER_MOBILE, result.getFatherMobile());
            editor.putString(PARENT_EMAIL, result.getParentEmail());
            editor.putString(Profile_PIC, result.getProfilePic());

            // save student count
            editor.putInt("student_count", userInfo.getResult().size());
        }

        editor.apply();
    }
    public StudentRegistationResponse.Result getUserData(){
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        StudentRegistationResponse.Result userInfo=new StudentRegistationResponse.Result(
                sharedPreferences.getString(STUDENT_ID,null),
                sharedPreferences.getString(FIRST_NAME,null),
                sharedPreferences.getString(LAST_NAME,null),
                sharedPreferences.getString(EMAIL_ID,null),
                sharedPreferences.getString(FATHER_MOBILE,null),
                sharedPreferences.getString(PARENT_EMAIL,null),
                sharedPreferences.getString(Profile_PIC,null));
        return userInfo;
    }

    public StudentTotalDetails.Result getUser(){
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        StudentTotalDetails.Result userinfo=new StudentTotalDetails.Result(
                sharedPreferences.getString(STUDENT_ID,null),
                sharedPreferences.getString(FIRST_NAME,null),
                sharedPreferences.getString(LAST_NAME,null),
                sharedPreferences.getString(Profile_PIC,null));
        return userinfo;
    }

    public boolean isLoggedIn(){
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        if (sharedPreferences.getString(STUDENT_ID, null) != null){
            return true;
        }
        return false;
    }
    public void isLoggedOut(){
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.clear();
        editor.apply();
        Intent intent = new Intent(mContext, UserCreateActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        mContext.startActivity(intent);
    }

    public void saveBatchId(String batchId) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("batch_id", batchId);
        editor.apply();
    }

    // Get batchId
    public String getBatchId() {
        return sharedPreferences.getString("batch_id", null);
    }

    public void saveUserData(StudentTotalDetails.Result result) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences("user_data", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("firstName", result.getFirstName());
        editor.putString("lastName", result.getLastName());
        editor.putString("profilePic", result.getProfilePic());
        //editor.putString("email", result.getEmailId());
        editor.apply();
    }
}
