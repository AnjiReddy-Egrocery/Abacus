package com.dst.abacustrainner.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dst.abacustrainner.Activity.CourseDetailActivity;
import com.dst.abacustrainner.Model.CourseResult;
import com.dst.abacustrainner.Model.DurationResult;
import com.dst.abacustrainner.R;
import com.dst.abacustrainner.Services.OnDurationSelectListener;

import java.util.ArrayList;
import java.util.List;

public class DurationAdapter extends RecyclerView.Adapter<DurationAdapter.ViewHolder> {
    Context mContext;
    private List<DurationResult> list = new ArrayList<>();
    private OnDurationSelectListener listener;
    private int selectedPosition = -1;

    public DurationAdapter(CourseDetailActivity courseDetailActivity, OnDurationSelectListener listener) {
        this.mContext = courseDetailActivity;
        this.listener = listener;
    }

    @NonNull
    @Override
    public DurationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.adapter_duration,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DurationAdapter.ViewHolder holder, int position) {

        DurationResult durationResult=list.get(position);

        String durationName = durationResult.getDuration();

        holder.textViewDurationName.setText(durationName);

        holder.checkBox.setOnCheckedChangeListener(null);
        holder.checkBox.setChecked(position == selectedPosition);

        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                String durationId = durationResult.getDurationId();

                Log.e("Reddy",
                        "Selected Duration ID = " + durationId);

                selectedPosition = holder.getAdapterPosition();
                notifyDataSetChanged();

                // 🔥 Activity ki durationId pass
                listener.onDurationSelected(durationResult.getDurationId());
            }
        });


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setData(List<DurationResult> result) {

        this.list = result;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewDurationName;
        CheckBox checkBox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewDurationName = itemView.findViewById(R.id.duration_name);
            checkBox = itemView.findViewById(R.id.check_duration);
        }
    }
}
