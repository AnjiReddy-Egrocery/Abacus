package com.dst.abacustrainner.Activity;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.dst.abacustrainner.Adapter.TableAdapter;
import com.dst.abacustrainner.Fragment.SchedulesFragment;
import com.dst.abacustrainner.Model.TableRowModel;
import com.dst.abacustrainner.R;

import java.util.ArrayList;
import java.util.List;

public class AllSchedulesActivity extends AppCompatActivity {


    RecyclerView recyclerView;
    TableAdapter adapter;
    List<TableRowModel> tableData;
    private LinearLayout btnBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_schedules);


        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        btnBack=findViewById(R.id.btn_back_to_home);

        tableData = new ArrayList<>();
        tableData.add(new TableRowModel("21-02-2025", "3 PM - 5 PM", "Join Now"));
        tableData.add(new TableRowModel("22-02-2025", "3 PM - 5 PM", "Up Coming"));
        tableData.add(new TableRowModel("30-02-2025", "3 PM - 5 PM", "Up Coming"));
        tableData.add(new TableRowModel("31-02-2025", "3 PM - 5 PM", "Up Coming"));

        adapter = new TableAdapter(this, tableData);
        recyclerView.setAdapter(adapter);


        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create an instance of SchedulesFragment
                SchedulesFragment schedulesFragment = new SchedulesFragment();

                // Get the FragmentManager and begin a transaction
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                // Replace the current fragment with SchedulesFragment
                fragmentTransaction.replace(R.id.btn_back_to_home, schedulesFragment);
                fragmentTransaction.addToBackStack(null); // Allows going back to the previous fragment

                // Commit the transaction
                fragmentTransaction.commit();
            }
        });

    }
}