package com.dst.abacustrainner.database;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.dst.abacustrainner.Model.StudentRegistationResponse;
import com.dst.abacustrainner.User.LoginActivity;


public class SharedPrefManager {

    static SharedPrefManager sharedPrefManager;
    Context mContext;
    private static final String SHARED_PREF_NAME = "userProfile";
    private static final String STUDENT_ID="studentId";
    private static final String FIRST_NAME="firstName";
    private static final String LAST_NAME="lastName";
    private static final String EMAIL_ID="emailId";
    private static final String FATHER_MOBILE="fatherMobile";
    private static final String PARENT_EMAIL="parentEmail";
    private SharedPrefManager(Context context) {
        mContext=context;
    }

    public static synchronized SharedPrefManager getInstance(Context context){
        if (sharedPrefManager==null){
            sharedPrefManager=new SharedPrefManager(context);
        }
        return sharedPrefManager;
    }
    //insert user data
    public void insertData(StudentRegistationResponse userInfo){
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString(STUDENT_ID,userInfo.getResult().getStudentId());
        editor.putString(FIRST_NAME,userInfo.getResult().getFirstName());
        editor.putString(LAST_NAME,userInfo.getResult().getLastName());
        editor.putString(EMAIL_ID,userInfo.getResult().getEmailId());
        editor.putString(FATHER_MOBILE,userInfo.getResult().getFatherMobile());
        editor.putString(PARENT_EMAIL,userInfo.getResult().getParentEmail());
        editor.commit();

    }

    public StudentRegistationResponse.Result getUserData(){
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        StudentRegistationResponse.Result userInfo=new StudentRegistationResponse.Result(
                sharedPreferences.getString(STUDENT_ID,null),
                sharedPreferences.getString(FIRST_NAME,null),
                sharedPreferences.getString(LAST_NAME,null),
                sharedPreferences.getString(EMAIL_ID,null),
                sharedPreferences.getString(FATHER_MOBILE,null),
                sharedPreferences.getString(PARENT_EMAIL,null));
        return userInfo;
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
        Intent intent = new Intent(mContext, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        mContext.startActivity(intent);
    }
}
