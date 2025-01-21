package com.dst.abacustrainner.Fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.dst.abacustrainner.R;
import com.dst.abacustrainner.database.SharedPrefManager;


public class ProfileFragment extends Fragment {

    /*LinearLayout layoutPassword, layoutSettings,layoutTermsCondition,layoutLogOut;*/

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_profile,container,false);



      /*  layoutPassword=view.findViewById(R.id.layout_password);
        layoutSettings=view.findViewById(R.id.layout_settings);
        layoutTermsCondition=view.findViewById(R.id.layout_conditions);
        layoutLogOut=view.findViewById(R.id.layout_logout);

        layoutPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "Item Present Working", Toast.LENGTH_SHORT).show();
            }
        });
        layoutSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "Item Present Working", Toast.LENGTH_SHORT).show();
            }
        });
        layoutTermsCondition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "Item Present Working", Toast.LENGTH_SHORT).show();
            }
        });
        layoutLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPrefManager.getInstance(getActivity().getApplicationContext()).isLoggedOut();
            }
        });*/
        return view;
    }
}
