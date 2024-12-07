package com.dst.abacustrainner.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;


import com.dst.abacustrainner.Activity.BatchDatesDetailsActivity;
import com.dst.abacustrainner.Model.BachDetailsResponse;
import com.dst.abacustrainner.R;

import java.util.List;

public class BatchDetailsAdapter extends RecyclerView.Adapter<BatchDetailsAdapter.MyViewHolder> {
    Context mContext;
    List<BachDetailsResponse.Result> results;
    public BatchDetailsAdapter(FragmentActivity activity, List<BachDetailsResponse.Result> results) {
        this.mContext=activity;
        this.results=results;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.batch_details_adpter, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
      BachDetailsResponse.Result batchResult=results.get(position);
      String name=batchResult.getBatchName();
      String batchId=batchResult.getBatchId();
      holder.txtBatchName.setText(name);
      holder.butResult.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {

              Intent intent=new Intent(mContext, BatchDatesDetailsActivity.class);
              intent.putExtra("batchId",batchId);
              intent.putExtra("batchName",name);
              mContext.startActivity(intent);

          }
      });
    }

    @Override
    public int getItemCount() {
        return results.size();
    }

    public class MyViewHolder  extends RecyclerView.ViewHolder{
        TextView txtBatchName;
        Button butResult;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            txtBatchName=itemView.findViewById(R.id.txt_bach_name);
            butResult=itemView.findViewById(R.id.but_viewResult);
        }
    }
}
