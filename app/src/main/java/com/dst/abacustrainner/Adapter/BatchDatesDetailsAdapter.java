package com.dst.abacustrainner.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.dst.abacustrainner.Activity.BatchDatesDetailsActivity;
import com.dst.abacustrainner.Activity.ViewDetailsActivity;
import com.dst.abacustrainner.Model.DatedetailsResponse;
import com.dst.abacustrainner.Model.StudentRegistationResponse;
import com.dst.abacustrainner.R;
import com.dst.abacustrainner.database.SharedPrefManager;

import java.util.List;

public class BatchDatesDetailsAdapter extends RecyclerView.Adapter<BatchDatesDetailsAdapter.MyViewHolder> {
    Context mContext;
    List<DatedetailsResponse.Result.Date> dateLists;
    String name;

    String startTime;
    String endTime;
    public BatchDatesDetailsAdapter(BatchDatesDetailsActivity batchDatesDetailsActivity, List<DatedetailsResponse.Result.Date> dateList, String name, String startTime, String endTime) {

        this.mContext=batchDatesDetailsActivity;
        this.dateLists=dateList;
        this.name=name;

        this.startTime=startTime;
        this.endTime=endTime;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dates_detail_adapter, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
     DatedetailsResponse.Result.Date listdetail= dateLists.get(position);

     StudentRegistationResponse.Result result= SharedPrefManager.getInstance(mContext.getApplicationContext()).getUserData();
     String studentId=result.getStudentId();

     String dateId= listdetail.getDateId();

     String dateName=listdetail.getScheduleDate();
     holder.txtDate.setText(dateName);

     holder.butResult.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
             Intent intent=new Intent(mContext, ViewDetailsActivity.class);
             intent.putExtra("studentId",studentId);
             intent.putExtra("dateId",dateId);
             intent.putExtra("batchName",name);
             intent.putExtra("scheduleDate",dateName);
             intent.putExtra("startTime",startTime);
             intent.putExtra("endTime",endTime);
             mContext.startActivity(intent);
         }
     });

    }

    @Override
    public int getItemCount() {
        return dateLists.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txtDate;
        Button butResult;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            txtDate=itemView.findViewById(R.id.txt_date);
            butResult=itemView.findViewById(R.id.but_viewResult);

        }
    }
}
