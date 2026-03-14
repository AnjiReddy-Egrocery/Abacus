package com.dst.abacustrainner.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dst.abacustrainner.Model.StudentRegistationResponse;
import com.dst.abacustrainner.R;
import com.dst.abacustrainner.User.HomeActivity;
import com.dst.abacustrainner.User.StudentDetailsActivity;

import java.util.List;

public class StudentAdapter  extends RecyclerView.Adapter<StudentAdapter.ViewHolder>{
    Context context;
    List<StudentRegistationResponse.Result> list;
    String imageBaseUrl;
    public StudentAdapter(StudentDetailsActivity studentDetailsActivity, List<StudentRegistationResponse.Result> studentList, String imageBaseUrl) {
        this.context = studentDetailsActivity;
        this.list = studentList;
        this.imageBaseUrl = imageBaseUrl;

    }

    @NonNull
    @Override
    public StudentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_student, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentAdapter.ViewHolder holder, int position) {
        StudentRegistationResponse.Result student = list.get(position);

        holder.name.setText(student.getFirstName() + " " + student.getLastName());

        String StudentId = student.getStudentId();


        String imageUrl = imageBaseUrl + student.getProfilePic();

        Glide.with(context)
                .load(imageUrl)
                .placeholder(R.drawable.abacus_logo)
                .into(holder.image);

        holder.butViewMoreDetails.setOnClickListener(v -> {

            Intent intent = new Intent(context, HomeActivity.class);
            intent.putExtra("StudentId",StudentId);
            context.startActivity(intent);

            Log.e("Shanker","StudentId"+StudentId);

        });
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView name;
        Button butViewMoreDetails;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.studentImage);
            name = itemView.findViewById(R.id.studentName);
            butViewMoreDetails = itemView.findViewById(R.id.but_viewmoredetails);

        }
    }
}
