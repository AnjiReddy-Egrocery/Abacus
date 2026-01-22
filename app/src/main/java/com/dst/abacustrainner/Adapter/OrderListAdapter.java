package com.dst.abacustrainner.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dst.abacustrainner.Activity.PurchasedActivity;
import com.dst.abacustrainner.Model.OrderListResponse;
import com.dst.abacustrainner.R;

import java.util.ArrayList;
import java.util.List;

public class OrderListAdapter extends RecyclerView.Adapter<OrderListAdapter.MyViewHolder> {
    private Context context;
    private List<OrderListResponse.CourseType> levels = new ArrayList<>();
    String studentId;

    public OrderListAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public OrderListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view= LayoutInflater.from(context).inflate(R.layout.order_list_item,parent,false);
       return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderListAdapter.MyViewHolder holder, int position) {

        OrderListResponse.CourseType item = levels.get(position);

        holder.tvOrderName.setText(item.getCourseType());




        if (item.getCourseType().toLowerCase().contains("abacus junior")) {
            holder.ivOrderIcon.setImageResource(R.drawable.abacusjunior);
        } else if (item.getCourseType().toLowerCase().contains("abacus senior")) {
            holder.ivOrderIcon.setImageResource(R.drawable.abacussenior);
        } else if (item.getCourseType().toLowerCase().contains("vedic maths")) {
            holder.ivOrderIcon.setImageResource(R.drawable.vedicmaths);
        }


        holder.layoutOrderList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PurchasedActivity.class);
                intent.putExtra("StudentId",studentId);
                context.startActivity(intent);
            }
        });
    }



        @Override
    public int getItemCount() {
        return levels.size();
    }

    public void setLevels(List<OrderListResponse.CourseType> levels, String studentId) {
        this.levels.clear();
        this.levels.addAll(levels);
        this.studentId = studentId;
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderName;
        ImageView ivOrderIcon;
        LinearLayout layoutOrderList;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            ivOrderIcon = itemView.findViewById(R.id.imgorder);
            tvOrderName  = itemView.findViewById(R.id.tvOrderTitle);
            layoutOrderList = itemView.findViewById(R.id.layout_order_list);
        }
    }
}
