package com.dst.abacustrainner.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dst.abacustrainner.Model.TableRowModel;
import com.dst.abacustrainner.R;

import java.util.List;



public class TableAdapter extends RecyclerView.Adapter<TableAdapter.ViewHolder> {
    private Context context;
    private List<TableRowModel> tableData;


    public TableAdapter(Context context, List<TableRowModel> tableData) {
        this.context = context;
        this.tableData = tableData;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.table_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TableRowModel row = tableData.get(position);
        holder.date.setText(row.getDate());
        holder.time.setText(row.getTime());
       // holder.status.setText(row.getStatus());

        /*if (row.getStatus().equalsIgnoreCase("Join Now")) {
            holder.status.setBackgroundResource(R.drawable.button_join_now);
        } else {
            holder.status.setBackgroundResource(R.drawable.button_upcoming);
        }*/
    }

    @Override
    public int getItemCount() {
        return tableData.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView date, time;
        LinearLayout status;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.txtDate);
            time = itemView.findViewById(R.id.txtTime);
            status = itemView.findViewById(R.id.txtStatus);
        }
    }
}

