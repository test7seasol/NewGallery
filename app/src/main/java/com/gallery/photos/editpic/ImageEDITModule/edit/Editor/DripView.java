package com.gallery.photos.editpic.ImageEDITModule.edit.Editor;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import androidx.appcompat.widget.AppCompatImageView;

import com.gallery.photos.editpic.ImageEDITModule.edit.drip.TouchListener;

/* loaded from: classes.dex */
public class DripView extends AppCompatImageView {
    TouchListener multiTouchListener;

    /* JADX WARN: 'this' call moved to the top of the method (can break code semantics) */
    public DripView(Context context) {
        this(context, null);
    }

    public DripView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
        setPadding(0, 0, 0, 0);
    }

    public DripView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.multiTouchListener = null;
        initBorderPaint();
    }

    private void initBorderPaint() {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(-1);
        paint.setStrokeWidth(0.0f);
    }

    @Override // android.widget.ImageView, android.view.View
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    public void setOnTouchListenerCustom(TouchListener touchListener) {
        this.multiTouchListener = touchListener;
        setOnTouchListener(touchListener);
    }
}
