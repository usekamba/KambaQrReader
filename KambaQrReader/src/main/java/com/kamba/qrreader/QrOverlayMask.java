package com.kamba.qrreader;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

public class QrOverlayMask extends View {

    public QrOverlayMask(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public QrOverlayMask(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public QrOverlayMask(Context context) {
        super(context);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        Paint outerPaint = new Paint();
        outerPaint.setColor(Color.parseColor("#C6111111")); // mention any background color
        outerPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.ADD));
        outerPaint.setAntiAlias(true);

        Paint innerPaint = new Paint();
        innerPaint.setColor(Color.TRANSPARENT);
        innerPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        innerPaint.setAntiAlias(true);

        canvas.drawRect(getLeft(), getTop(), getRight(), getBottom(), outerPaint);

        int canvasW = getMeasuredWidth();
        int canvasH = getMeasuredHeight();
        Point centerOfCanvas = new Point(canvasW / 2, canvasH / 2);
        Resources r = getResources();
        int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 200, r.getDisplayMetrics());
        int left = centerOfCanvas.x - (px /2);
        int top = centerOfCanvas.y - (px /2);
        int right = centerOfCanvas.x + (px /2);
        int bottom = centerOfCanvas.y + (px /2);
        Rect rect = new Rect(left, top, right, bottom);
        canvas.drawRect(rect, innerPaint);
    }
}
