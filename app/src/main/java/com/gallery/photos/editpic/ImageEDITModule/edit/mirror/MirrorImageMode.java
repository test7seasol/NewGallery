package com.gallery.photos.editpic.ImageEDITModule.edit.mirror;

import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;

/* loaded from: classes.dex */
public class MirrorImageMode {
    public int count;
    private Rect drawBitmapSrc;
    public Matrix matrix1;
    public Matrix matrix2;
    public Matrix matrix3;
    public RectF rect1;
    public RectF rect2;
    public RectF rect3;
    public RectF rect4;
    public RectF rectTotalArea;
    private RectF srcRect;
    public int touchMode;

    public MirrorImageMode(int i, RectF rectF, RectF rectF2, RectF rectF3, Matrix matrix, int i2, RectF rectF4) {
        Rect rect = new Rect();
        this.drawBitmapSrc = rect;
        this.count = i;
        this.srcRect = rectF;
        rectF.round(rect);
        this.rect1 = rectF2;
        this.rect2 = rectF3;
        this.matrix1 = matrix;
        this.touchMode = i2;
        this.rectTotalArea = rectF4;
    }

    public void setSrcRect(RectF rectF) {
        this.srcRect.set(rectF);
        updateBitmapSrc();
    }

    public RectF getSrcRect() {
        return this.srcRect;
    }

    public Rect getDrawBitmapSrc() {
        return this.drawBitmapSrc;
    }

    public void updateBitmapSrc() {
        this.srcRect.round(this.drawBitmapSrc);
    }

    public MirrorImageMode(int i, RectF rectF, RectF rectF2, RectF rectF3, RectF rectF4, RectF rectF5, Matrix matrix, Matrix matrix2, Matrix matrix3, int i2, RectF rectF6) {
        Rect rect = new Rect();
        this.drawBitmapSrc = rect;
        this.count = i;
        this.srcRect = rectF;
        rectF.round(rect);
        this.rect1 = rectF2;
        this.rect2 = rectF3;
        this.rect3 = rectF4;
        this.rect4 = rectF5;
        this.matrix1 = matrix;
        this.matrix2 = matrix2;
        this.matrix3 = matrix3;
        this.touchMode = i2;
        this.rectTotalArea = rectF6;
    }
}
