package com.dst.abacustrainner.Fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.video_fragment,container,false);

        /*btnYes = view.findViewById(R.id.btn_yes);
        btnNo = view.findViewById(R.id.btn_no);

        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to courses page
                Intent intent = new Intent(getContext(), CoursesVideoActivity.class);
                startActivity(intent);
                //finish(); // optional
            }
        });

        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Go back to dashboard
                Intent intent = new Intent(getContext(), HomeActivity.class);
                startActivity(intent);
                //finish(); // optional
            }
        });*/

        showSubscriptionDialog();

        return view;
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
            startActivity(intent);
            dialog.dismiss();
        });

        btnNo.setOnClickListener(v -> {
            // Example selected levels list
            HomeFragment homeFragment = new HomeFragment();
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.flFragment, homeFragment); // Replace with correct container ID
            fragmentTransaction.addToBackStack(null); // Optional: adds to back stack
            fragmentTransaction.commit();
            dialog.dismiss();
        });
        ;

        dialog.show();
    }
}
