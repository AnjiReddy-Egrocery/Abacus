package com.dst.abacustrainner.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.dst.abacustrainner.Activity.CoursesActivity;
import com.dst.abacustrainner.Activity.CoursesVideoActivity;
import com.dst.abacustrainner.R;
import com.dst.abacustrainner.User.HomeActivity;

public class VideoFragment extends Fragment {


    Button btnYes, btnNo;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.video_fragment,container,false);

        btnYes = view.findViewById(R.id.btn_yes);
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
        });

        return view;
    }
}
