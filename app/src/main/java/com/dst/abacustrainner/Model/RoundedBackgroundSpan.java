package com.dst.abacustrainner.Model;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.style.ReplacementSpan;

public class RoundedBackgroundSpan extends ReplacementSpan {

    private final int backgroundColor;
    private final int textColor;

    public RoundedBackgroundSpan(int backgroundColor, int textColor) {
        this.backgroundColor = backgroundColor;
        this.textColor = textColor;
    }

    @Override
    public int getSize(Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fm) {
        return Math.round(paint.measureText(text, start, end)) + 20; // Padding for rounded shape
    }

    @Override
    public void draw(Canvas canvas, CharSequence text, int start, int end,
                     float x, int top, int y, int bottom, Paint paint) {

        // Background Paint
        Paint backgroundPaint = new Paint(paint);
        backgroundPaint.setColor(backgroundColor);
        backgroundPaint.setAntiAlias(true);

        // Rectangle with rounded corners
        RectF rect = new RectF(x, top + 8, x + paint.measureText(text, start, end) + 20, bottom - 8);
        canvas.drawRoundRect(rect, 20, 20, backgroundPaint);  // 20dp corner radius

        // Text Paint
        paint.setColor(textColor);
        canvas.drawText(text, start, end, x + 10, y, paint); // Text with padding
    }
}
