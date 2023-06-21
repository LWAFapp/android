package com.Zakovskiy.lwaf.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class RedCircle extends View {

    private Paint paint;

    public RedCircle(Context context) {
        this(context, null);
    }

    public RedCircle(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RedCircle(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.RED);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getWidth();
        int height = getHeight();
        int radius = Math.min(width, height) / 2;
        canvas.drawCircle(width - radius, radius, radius, paint);
    }
}
