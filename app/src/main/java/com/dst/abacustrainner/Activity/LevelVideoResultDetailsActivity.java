package com.dst.abacustrainner.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.dst.abacustrainner.R;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;

public class LevelVideoResultDetailsActivity extends AppCompatActivity {

    TableLayout tabLayout;
    private PieChart pieChart;
    ScrollView scrollView;
    LinearLayout layoutFirst,layoutSecond;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level_video_result_details);

        scrollView= findViewById(R.id.scroll_view);
        layoutFirst = findViewById(R.id.layout_first);
        layoutSecond = findViewById(R.id.layout_second);
        tabLayout = findViewById(R.id.tablelayout);

        scrollView.getViewTreeObserver().addOnScrollChangedListener(() -> {
            int scrollY = scrollView.getScrollY();

            if (scrollY > 100 && layoutFirst.getVisibility() == View.VISIBLE) {
                fadeOut(layoutFirst);
                fadeIn(layoutSecond);
            } else if (scrollY <= 100 && layoutSecond.getVisibility() == View.VISIBLE) {
                fadeOut(layoutSecond);
                fadeIn(layoutFirst);
            }
        });

        updatePieChart();
        loadStaticResults();

    }

    private void loadStaticResults() {
        String[] questions = {
                "5 + 3", " 7 - 2", " 6 × 2 ",
                "12 ÷ 4", "9 + 1", "10 - 3",
                "4 × 2", "18 ÷ 6", "11 + 2", "15 - 5"
        };
        String[] answers = {"8", "5", "12", "3", "10", "7", "8", "3", "13", "10"};
        String[] given = {"8", "4", "12", "3", "9", "7", "6", "3", "13", "10"};
        String[] times = {"10", "8", "12", "9", "7", "11", "13", "10", "6", "5"};

        for (int i = 0; i < questions.length; i++) {
            TableRow row = new TableRow(this);
            row.setBackgroundColor(Color.parseColor("#FFFFFF"));
            row.setLayoutParams(new TableLayout.LayoutParams(
                    TableLayout.LayoutParams.MATCH_PARENT,
                    TableLayout.LayoutParams.WRAP_CONTENT));

            row.addView(createCell(questions[i]));
            row.addView(createDivider());
            row.addView(createCell(answers[i]));
            row.addView(createDivider());
            row.addView(createCell(given[i]));
            row.addView(createDivider());
            row.addView(createCell(times[i]));

            tabLayout.addView(row);

            // Optional: separator view
            View separator = new View(this);
            separator.setLayoutParams(new TableLayout.LayoutParams(
                    TableLayout.LayoutParams.MATCH_PARENT, 1));
            separator.setBackgroundColor(Color.GRAY);
            tabLayout.addView(separator);
        }
    }

    private View createDivider() {
        View divider = new View(this);
        divider.setLayoutParams(new TableRow.LayoutParams(0,
                TableRow.LayoutParams.MATCH_PARENT, 0.01f));
        divider.setBackgroundColor(Color.BLACK);
        return divider;
    }

    private View createCell(String question) {
        TextView textView = new TextView(this);
        textView.setText(question);
        textView.setPadding(14, 14, 14, 14);
        textView.setGravity(Gravity.CENTER);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        textView.setLayoutParams(new TableRow.LayoutParams(0,
                TableRow.LayoutParams.WRAP_CONTENT, 1));
        textView.setTextColor(Color.BLACK);
        return textView;
    }

    private void updatePieChart() {
        ArrayList<PieEntry> pieEntries = new ArrayList<>();
        ArrayList<Integer> colors = new ArrayList<>();



        if (pieEntries.isEmpty()) {
//            pieChart.clear();
            return;
        }


        // Pie DataSet Configuration
        PieDataSet dataSet = new PieDataSet(pieEntries, "");
        dataSet.setColors(colors); // Set dynamic colors based on available data
        dataSet.setValueTextSize(14f);
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setDrawIcons(false);

        PieData pieData = new PieData(dataSet);
        // Customize the chart
        pieChart.setData(pieData);
        pieChart.setUsePercentValues(true);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleRadius(40f);
        pieChart.setTransparentCircleRadius(50f);
        pieChart.setCenterText("Pie Chart");
        pieChart.setCenterTextSize(16f);

        // Set labels and values outside the slices
        dataSet.setValueLinePart1Length(0.5f);
        dataSet.setValueLinePart2Length(0.8f);
        dataSet.setValueLineColor(Color.BLACK);
        dataSet.setUsingSliceColorAsValueLineColor(true);
        dataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        dataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);

        // Set offset for better visibility
        dataSet.setValueLineVariableLength(true);

        // Disable description text
        Description description = new Description();
        description.setText("");
        pieChart.setDescription(description);
        // Refresh chart
        pieChart.setData(pieData);
        pieChart.invalidate();
    }

    private void fadeIn(View view) {
        view.setAlpha(0f);
        view.setVisibility(View.VISIBLE);
        view.animate().alpha(1f).setDuration(0).start();
    }

    private void fadeOut(View view) {
        view.animate().alpha(0f).setDuration(0).withEndAction(() -> view.setVisibility(View.GONE)).start();
    }
}