package com.gallery.photos.editpic.ImageEDITModule.github.siyamed.shapeimageview.mask;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;

/* loaded from: classes.dex */
public class PorterCircularImageView extends PorterImageView {
    private final RectF rect;

    public PorterCircularImageView(Context context) {
        super(context);
        this.rect = new RectF();
        setup();
    }

    public PorterCircularImageView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.rect = new RectF();
        setup();
    }

    public PorterCircularImageView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.rect = new RectF();
        setup();
    }

    private void setup() {
        setSquare(true);
    }

    @Override // com.github.siyamed.shapeimageview.mask.PorterImageView
    protected void paintMaskCanvas(Canvas canvas, Paint paint, int i, int i2) {
        this.rect.set(0.0f, 0.0f, i, i2);
        canvas.drawCircle(this.rect.centerX(), this.rect.centerY(), Math.min(i, i2) / 2.0f, paint);
    }
}
