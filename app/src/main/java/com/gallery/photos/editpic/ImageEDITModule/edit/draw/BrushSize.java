package com.gallery.photos.editpic.ImageEDITModule.edit.draw;

import android.graphics.Paint;
import android.graphics.Path;
import androidx.core.view.ViewCompat;

/* loaded from: classes.dex */
public class BrushSize {
    private Paint paintInner;
    private Paint paintOuter;
    private Path path;

    public BrushSize() {
        Paint paint = new Paint();
        this.paintOuter = paint;
        paint.setColor(ViewCompat.MEASURED_STATE_MASK);
        this.paintOuter.setStrokeWidth(2.0f);
        this.paintOuter.setStyle(Paint.Style.STROKE);
        Paint paint2 = new Paint();
        this.paintInner = paint2;
        paint2.setColor(-7829368);
        this.paintInner.setStrokeWidth(2.0f);
        this.paintInner.setStyle(Paint.Style.FILL);
        this.path = new Path();
    }

    public void setCircle(float f, float f2, float f3, Path.Direction direction) {
        this.path.reset();
        this.path.addCircle(f, f2, f3, direction);
    }

    public Path getPath() {
        return this.path;
    }

    public Paint getPaint() {
        return this.paintOuter;
    }

    public Paint getInnerPaint() {
        return this.paintInner;
    }

    public void setPaintOpacity(int i) {
        this.paintInner.setAlpha(i);
    }
}
