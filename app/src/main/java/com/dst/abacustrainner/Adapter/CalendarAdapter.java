package com.dst.abacustrainner.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.icu.util.Calendar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dst.abacustrainner.R;
import com.dst.abacustrainner.Services.OnDateClickListener;

import java.text.SimpleDateFormat;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.ViewHolder> {
    private Context context;
    private List<String> days;
    private List<String> scheduledDates; // Store scheduled dates
    private OnDateClickListener listener;
    private Map<String, String> dateIdMap;
    private Calendar currentCalendar;

    public CalendarAdapter(Context context, List<String> days, Map<String, String> dateIdMap, Calendar currentCalendar, OnDateClickListener listener) {
        this.context = context;
        this.days = days;
        this.listener = listener;
        this.dateIdMap = dateIdMap;
        this.currentCalendar = currentCalendar; // ✅ Initialize dateIdMap
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_calendar_day, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String dayText = days.get(position);
        if (!dayText.isEmpty()) { // Ensure it's a valid date
            holder.tvDate.setText(dayText);
            holder.tvDate.setTextSize(18);
            holder.tvDate.setTextColor(Color.BLACK);
            holder.tvDate.setBackgroundResource(0); // Reset background

            if (dayText.contains("\n")) { // Check if date contains a scheduled class

                holder.tvDate.setTextSize(14); // Reduce text size for better visibility
                holder.tvDate.setTextColor(Color.BLACK); // Set scheduled classes in black
                holder.tvDate.setBackgroundResource(R.color.grey); // Apply gray background

                String selectedDay = dayText.split("\n")[0].trim(); // Extract day number
                Calendar tempCalendar = (Calendar) currentCalendar.clone();
                tempCalendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(selectedDay));

                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                String formattedDate = dateFormat.format(tempCalendar.getTime());

                // ✅ Get `dateId` using full date format
                String dateId = dateIdMap.get(formattedDate);

                Log.d("AdapterDateID", "SelectedDate: " + formattedDate + " | DateID: " + dateId);

                // ✅ Click Listener for scheduled dates
                holder.itemView.setOnClickListener(v -> {
                    if (listener != null) {
                        listener.onDateClick(formattedDate, dateId); // Send selected date to Fragment
                    }
                });
            } else {
                holder.itemView.setOnClickListener(null); // Remove click listener for non-scheduled dates
            }
        } else {
            holder.tvDate.setText(""); // Empty cells for spacing
            holder.tvDate.setBackgroundResource(0); // No background for empty cells
            holder.itemView.setOnClickListener(null); // Remove click listener
        }
    }




    @Override
    public int getItemCount() {
        return days.size();
    }



    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tvDate);
        }
    }
}