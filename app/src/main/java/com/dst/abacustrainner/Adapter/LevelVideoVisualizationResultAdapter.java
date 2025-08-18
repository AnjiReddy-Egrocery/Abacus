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

import com.dst.abacustrainner.Activity.LevelResultVisualizationDetailsActivity;
import com.dst.abacustrainner.Activity.LevelVideoResultVisualizationDetailsActivity;
import com.dst.abacustrainner.Model.LevelResultModel;
import com.dst.abacustrainner.Model.LevelVideoResultModel;
import com.dst.abacustrainner.R;

import java.util.ArrayList;

public class LevelVideoVisualizationResultAdapter extends RecyclerView.Adapter<LevelVideoVisualizationResultAdapter.ViewHolder> {

    Context context;
    ArrayList<LevelVideoResultModel> resultList;

    public LevelVideoVisualizationResultAdapter(Context context, ArrayList<LevelVideoResultModel> resultList) {
        this.context = context;
        this.resultList = resultList;
    }

    @NonNull
    @Override
    public LevelVideoVisualizationResultAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_videolevel_visualization_result, parent, false);
        return new LevelVideoVisualizationResultAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LevelVideoVisualizationResultAdapter.ViewHolder holder, int position) {
        LevelVideoResultModel model = resultList.get(position);
        holder.tvDate.setText("Date: " + model.getDate());
        holder.tvTime.setText("Time: " + model.getTime());

        holder.btnViewResult.setOnClickListener(v -> {
            Intent intent = new Intent(context, LevelVideoResultVisualizationDetailsActivity.class);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return resultList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate, tvTime;
        Button btnViewResult;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvTime = itemView.findViewById(R.id.tvTime);
            btnViewResult = itemView.findViewById(R.id.btnViewResult);
        }
    }
}
