package com.dst.abacustrainner.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dst.abacustrainner.Activity.CartActivity;
import com.dst.abacustrainner.Model.CartDetailsResponse;
import com.dst.abacustrainner.Model.CourseListResponse;
import com.dst.abacustrainner.Model.OnCartDeleteListener;
import com.dst.abacustrainner.Model.OnDeleteCart;
import com.dst.abacustrainner.R;

import java.util.ArrayList;
import java.util.List;

public class CartDetailAdapter extends RecyclerView.Adapter<CartDetailAdapter.ViewHolder>{
    private Context context;
    private List<CartDetailsResponse.CourseLevels> levels = new ArrayList<>();
    private OnDeleteCart deleteListener;

    public CartDetailAdapter(CartActivity cartActivity, OnDeleteCart listener) {
        this.context = cartActivity;
        this.deleteListener = listener;
    }

    @NonNull
    @Override
    public CartDetailAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_cart_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartDetailAdapter.ViewHolder holder, int position) {
        CartDetailsResponse.CourseLevels courseLevels = levels.get(position);

        String levelName = courseLevels.getCourseLevel();
        String levelPrice = courseLevels.getCourseLevelPrice();
        String cartId = courseLevels.getCartId();

        holder.txtName.setText(levelName);
        holder.txtPrice.setText(levelPrice);
        holder.imgDelete.setOnClickListener(v -> {
            if (deleteListener != null) {
                deleteListener.onDeleteClick(courseLevels.getCartId()); // ✅ fixed
            }
        });
    }

    @Override
    public int getItemCount() {
        return levels.size();
    }

    public void setLevels(List<CartDetailsResponse.CourseLevels> levels) {
        this.levels.clear();
        this.levels.addAll(levels);
        notifyDataSetChanged();
    }
    public List<CartDetailsResponse.CourseLevels> getLevels() {
        return levels;
    }

    public void removeItemByCartId(String cartId) {
        for (int i = 0; i < levels.size(); i++) {
            if (levels.get(i).getCartId().equals(cartId)) {
                levels.remove(i);
                notifyItemRemoved(i);
                notifyItemRangeChanged(i, levels.size());
                break;
            }
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtName,txtPrice;
        ImageView imgDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtName= itemView.findViewById(R.id.tvLevelText);
            txtPrice= itemView.findViewById(R.id.tvLevelprice);
            imgDelete = itemView.findViewById(R.id.img_delete);
        }
    }

    public interface OnDeleteCart {
        void onDeleteClick(String cartId);
    }
}
