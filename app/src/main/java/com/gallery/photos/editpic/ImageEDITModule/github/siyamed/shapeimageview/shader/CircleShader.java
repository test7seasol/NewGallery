package com.gallery.photos.editpic.ImageEDITModule.github.siyamed.shapeimageview.shader;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;

/* loaded from: classes.dex */
public class CircleShader extends ShaderHelper {
    private float bitmapCenterX;
    private float bitmapCenterY;
    private int bitmapRadius;
    private float borderRadius;
    private float center;

    @Override // com.github.siyamed.shapeimageview.shader.ShaderHelper
    public void init(Context context, AttributeSet attributeSet, int i) {
        super.init(context, attributeSet, i);
        this.square = true;
    }

    @Override // com.github.siyamed.shapeimageview.shader.ShaderHelper
    public void draw(Canvas canvas, Paint paint, Paint paint2) {
        float f = this.center;
        canvas.drawCircle(f, f, this.borderRadius, paint2);
        canvas.save();
        canvas.concat(this.matrix);
        canvas.drawCircle(this.bitmapCenterX, this.bitmapCenterY, this.bitmapRadius, paint);
        canvas.restore();
    }

    @Override // com.github.siyamed.shapeimageview.shader.ShaderHelper
    public void onSizeChanged(int i, int i2) {
        super.onSizeChanged(i, i2);
        this.center = Math.round(this.viewWidth / 2.0f);
        this.borderRadius = Math.round((this.viewWidth - this.borderWidth) / 2.0f);
    }

    @Override // com.github.siyamed.shapeimageview.shader.ShaderHelper
    public void calculate(int i, int i2, float f, float f2, float f3, float f4, float f5) {
        this.bitmapCenterX = Math.round(i / 2.0f);
        this.bitmapCenterY = Math.round(i2 / 2.0f);
        this.bitmapRadius = Math.round(((f / f3) / 2.0f) + 0.5f);
    }

    @Override // com.github.siyamed.shapeimageview.shader.ShaderHelper
    public void reset() {
        this.bitmapRadius = 0;
        this.bitmapCenterX = 0.0f;
        this.bitmapCenterY = 0.0f;
    }

    public final float getBorderRadius() {
        return this.borderRadius;
    }

    public final void setBorderRadius(float f) {
        this.borderRadius = f;
    }
}
