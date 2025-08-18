package com.dst.abacustrainner.Fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.dst.abacustrainner.Activity.CoursesActivity;
import com.dst.abacustrainner.Activity.CoursesVideoActivity;
import com.dst.abacustrainner.Activity.VideoCoursesActivity;
import com.dst.abacustrainner.R;
import com.dst.abacustrainner.User.HomeActivity;
import com.google.gson.Gson;

import java.util.ArrayList;

public class VideoFragment extends Fragment {


    Button btnYes, btnNo;
    String studentId,batchId;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.video_fragment,container,false);

        if (getArguments() != null) {
            studentId = getArguments().getString("studentId");
            batchId   = getArguments().getString("batchId");

            Log.d("Reddy", "Received studentId=" + studentId + " batchId=" + batchId);
        } else {
            Log.d("Reddy", "Arguments bundle is null!");
        }

        if (isFirstTime()) {
            showSubscriptionDialog();  // Show dialog only first time
            setFirstTimeFalse();       // Set flag false after showing
        } else {
            // Directly open CoursesActivity
            Intent intent = new Intent(getContext(), VideoCoursesActivity.class);
            intent.putExtra("studentId", studentId);
            intent.putExtra("batchId", batchId);
            startActivity(intent);
        }

        //showSubscriptionDialog();

        return view;
    }

    private void navigateToHomeFragment() {
        HomeFragment homeFragment = new HomeFragment();
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.flFragment, homeFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }



    private boolean isFirstTime() {
        SharedPreferences prefs = requireActivity().getSharedPreferences("worksheet_prefs", getContext().MODE_PRIVATE);
        return prefs.getBoolean("isFirstTimeDialogShown", true);
    }

    private void setFirstTimeFalse() {
        SharedPreferences prefs = requireActivity().getSharedPreferences("worksheet_prefs", getContext().MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("isFirstTimeDialogShown", false);
        editor.apply();
    }
    @SuppressLint("MissingInflatedId")
    private void showSubscriptionDialog() {

        LayoutInflater inflater = LayoutInflater.from(getContext());
        View dialogView = inflater.inflate(R.layout.video_tutiorials, null); // same layout

        Button btnYes = dialogView.findViewById(R.id.btn_yes);
        Button btnNo = dialogView.findViewById(R.id.btn_no);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(dialogView);
        builder.setCancelable(false); // Prevent closing by touching outside

        AlertDialog dialog = builder.create();

        btnYes.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), VideoCoursesActivity.class);
            intent.putExtra("studentId", studentId);
            intent.putExtra("batchId", batchId);
            startActivity(intent);
            dialog.dismiss();
        });

        btnNo.setOnClickListener(v -> {
            // Example selected levels list
            navigateToHomeFragment();
            dialog.dismiss();
        });
        ;

        dialog.show();
    }
}
