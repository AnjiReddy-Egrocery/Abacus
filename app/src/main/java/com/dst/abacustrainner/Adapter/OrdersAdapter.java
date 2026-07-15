package com.dst.abacustrainner.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dst.abacustrainner.Activity.OrderDetailsActivity;
import com.dst.abacustrainner.Model.WorksheetOrder;
import com.dst.abacustrainner.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.ViewHolder> {

    private Context context;
    private List<WorksheetOrder> orderList;
    private String studentId;

    public OrdersAdapter(Context context,
                         List<WorksheetOrder> orderList,
                         String studentId) {

        this.context = context;
        this.orderList = orderList;
        this.studentId = studentId;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_order, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        WorksheetOrder order = orderList.get(position);

        holder.txtNo.setText(String.valueOf(position + 1));

        holder.txtDate.setText(formatDate(order.getOrderedOn()));

        holder.txtAmount.setText("₹ " + order.getAmount());

        holder.txtStatus.setText(order.getState());

        if (order.getState() != null) {

            if (order.getState().equalsIgnoreCase("COMPLETED")
                    || order.getState().equalsIgnoreCase("SUCCESS")
                    || order.getState().equalsIgnoreCase("PAID")) {

                holder.txtStatus.setTextColor(Color.parseColor("#2E7D32"));

            } else {

                holder.txtStatus.setTextColor(Color.parseColor("#D32F2F"));
            }
        }

        holder.imgView.setOnClickListener(v -> {

            Intent intent = new Intent(context, OrderDetailsActivity.class);

            intent.putExtra("studentId", studentId);
            intent.putExtra("orderId", order.getOrderId());

            context.startActivity(intent);

        });
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtNo, txtDate, txtAmount, txtStatus;
        ImageView imgView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtNo = itemView.findViewById(R.id.txtNo);
            txtDate = itemView.findViewById(R.id.txtDate);
            txtAmount = itemView.findViewById(R.id.txtAmount);
            txtStatus = itemView.findViewById(R.id.txtStatus);
            imgView = itemView.findViewById(R.id.imgView);
        }
    }

    private String formatDate(String timestamp) {

        try {

            long time = Long.parseLong(timestamp) * 1000;

            Date date = new Date(time);

            SimpleDateFormat sdf = new SimpleDateFormat(
                    "dd MMM yyyy\nhh:mm a",
                    Locale.getDefault());

            return sdf.format(date);

        } catch (Exception e) {

            return "-";
        }
    }
}
