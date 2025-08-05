package com.dst.abacustrainner.Fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.dst.abacustrainner.Activity.CoursesActivity;
import com.dst.abacustrainner.Activity.DashboardActivity;
import com.dst.abacustrainner.Activity.WorksheetSubscriptionActivity;
import com.dst.abacustrainner.R;
import com.dst.abacustrainner.User.HomeActivity;

public class WorkSheetFragment extends Fragment {

    Button btnYes, btnNo;
    String studentId,batchId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.worksheet_fragment,container,false);

        if (isFirstTime()) {
            showSubscriptionDialog();  // Show dialog only first time
            setFirstTimeFalse();       // Set flag false after showing
        } else {
            // Directly open CoursesActivity
            Intent intent = new Intent(getContext(), CoursesActivity.class);
            startActivity(intent);
        }

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

    private void showSubscriptionDialog() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View dialogView = inflater.inflate(R.layout.work_sheet, null); // same layout

        Button btnYes = dialogView.findViewById(R.id.btn_yes);
        Button btnNo = dialogView.findViewById(R.id.btn_no);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(dialogView);
        builder.setCancelable(false); // Prevent closing by touching outside

        AlertDialog dialog = builder.create();

        btnYes.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), CoursesActivity.class);
            startActivity(intent);
            dialog.dismiss();
        });

        btnNo.setOnClickListener(v -> {
            navigateToHomeFragment();

            dialog.dismiss();
        });

        dialog.show();
    }

}
