package com.dst.abacustrainner.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dst.abacustrainner.Activity.OrderDetailsActivity;
import com.dst.abacustrainner.Model.OrderInfoResponse;
import com.dst.abacustrainner.R;

import java.util.List;

public class OrderInfoAdapter  extends RecyclerView.Adapter<OrderInfoAdapter.ViewHolder> {
    Context mContext;
    List<OrderInfoResponse.Subscription> orderSubscription;
    public OrderInfoAdapter(OrderDetailsActivity orderDetailsActivity, List<OrderInfoResponse.Subscription> subscriptions) {

        this.mContext = orderDetailsActivity;
        this.orderSubscription = subscriptions;

    }

    @NonNull
    @Override
    public OrderInfoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.order_info_details, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderInfoAdapter.ViewHolder holder, int position) {

        OrderInfoResponse.Subscription subscription = orderSubscription.get(position);

        holder.tvCouseId.setText(String.valueOf(position + 1));

        holder.tvCourse.setText(
                subscription.getCourseType() + " - " + subscription.getCourseLevel()
        );

        holder.tvamount.setText(subscription.getAmount());
    }

    @Override
    public int getItemCount() {
        return orderSubscription.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvCouseId, tvCourse, tvamount;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvCouseId = itemView.findViewById(R.id.tvCourseTitle);
            tvCourse = itemView.findViewById(R.id.tvCourseInfo);
            tvamount = itemView.findViewById(R.id.tvAmount);
        }
    }
}
