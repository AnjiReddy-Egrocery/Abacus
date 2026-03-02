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
import com.dst.abacustrainner.Model.BachDetailsResponse;
import com.dst.abacustrainner.R;

import java.util.ArrayList;
import java.util.List;

public class BatchesAdapter extends RecyclerView.Adapter<BatchesAdapter.ViewHolder> {
    Context mContext;
    String studentId;
    List<BachDetailsResponse.Result> batchList = new ArrayList<>();
    public BatchesAdapter(Context context, String studentId) {

        this.mContext = context;
        this.studentId = studentId;
    }

    @NonNull
    @Override
    public BatchesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.item_batch,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BatchesAdapter.ViewHolder holder, int position) {

        BachDetailsResponse.Result detailsResponse = batchList.get(position);

        String bactchName = detailsResponse.getBatchName();
        String satrtDate = "Start Date:" + detailsResponse.getStartDate();
        String startTime = "Timings:" + detailsResponse.getStartTime() + "-" + detailsResponse.getEndTime();
        String batchId = detailsResponse.getBatchId();

        holder.txtBatchName.setText(bactchName);
        holder.txtDate.setText(satrtDate);
        holder.txtTime.setText(startTime);

        holder.butViewSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, BatchDatesDetailsActivity.class);
                intent.putExtra("StudentId",studentId);
                intent.putExtra("BatchId",batchId);
                intent.putExtra("BatchName",bactchName);
                mContext.startActivity(intent);
            }
        });






    }

    @Override
    public int getItemCount() {
        return batchList.size();
    }

    public void setData(List<BachDetailsResponse.Result> results) {
        this.batchList = results;
        notifyDataSetChanged();

    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtBatchName,txtDate,txtTime;
        Button butViewSchedule;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtBatchName = itemView.findViewById(R.id.txtbachname);
            txtDate = itemView.findViewById(R.id.txtdate);
            txtTime = itemView.findViewById(R.id.txttime);
            butViewSchedule = itemView.findViewById(R.id.btnViewSchedule);
        }
    }
}
