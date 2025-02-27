package com.gallery.photos.editpic.ImageEDITModule.github.siyamed.shapeimageview.shader;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;

import com.gallery.photos.editpic.R;


/* loaded from: classes.dex */
public class RoundedShader extends ShaderHelper {
    private int bitmapRadius;
    private final RectF borderRect = new RectF();
    private final RectF imageRect = new RectF();
    private int radius = 0;

    @Override // com.github.siyamed.shapeimageview.shader.ShaderHelper
    public void init(Context context, AttributeSet attributeSet, int i) {
        super.init(context, attributeSet, i);
        this.borderPaint.setStrokeWidth(this.borderWidth * 2);
        if (attributeSet != null) {
            TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.ShaderImageView, i, 0);
            this.radius = obtainStyledAttributes.getDimensionPixelSize(R.styleable.ShaderImageView_siRadius, this.radius);
            obtainStyledAttributes.recycle();
        }
    }

    @Override // com.github.siyamed.shapeimageview.shader.ShaderHelper
    public void draw(Canvas canvas, Paint paint, Paint paint2) {
        RectF rectF = this.borderRect;
        int i = this.radius;
        canvas.drawRoundRect(rectF, i, i, paint2);
        canvas.save();
        canvas.concat(this.matrix);
        RectF rectF2 = this.imageRect;
        int i2 = this.bitmapRadius;
        canvas.drawRoundRect(rectF2, i2, i2, paint);
        canvas.restore();
    }

    @Override // com.github.siyamed.shapeimageview.shader.ShaderHelper
    public void onSizeChanged(int i, int i2) {
        super.onSizeChanged(i, i2);
        this.borderRect.set(this.borderWidth, this.borderWidth, this.viewWidth - this.borderWidth, this.viewHeight - this.borderWidth);
    }

    @Override // com.github.siyamed.shapeimageview.shader.ShaderHelper
    public void calculate(int i, int i2, float f, float f2, float f3, float f4, float f5) {
        this.imageRect.set(-f4, -f5, i + f4, i2 + f5);
        this.bitmapRadius = Math.round(this.radius / f3);
    }

    @Override // com.github.siyamed.shapeimageview.shader.ShaderHelper
    public void reset() {
        this.imageRect.set(0.0f, 0.0f, 0.0f, 0.0f);
        this.bitmapRadius = 0;
    }

    public final int getRadius() {
        return this.radius;
    }

    public final void setRadius(int i) {
        this.radius = i;
    }
}
