package com.gallery.photos.editpic.ImageEDITModule.edit.sticker;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;

/* loaded from: classes.dex */
public abstract class Sticker {
    private boolean isFlippedHorizontally;
    private boolean isFlippedVertically;
    private final float[] boundPoints = new float[8];
    private boolean isShow = true;
    private final float[] mappedBounds = new float[8];
    private final Matrix matrix = new Matrix();
    private final float[] matrixValues = new float[9];
    private final RectF trappedRect = new RectF();
    private final float[] unrotatedPoint = new float[2];
    private final float[] unrotatedWrapperCorner = new float[8];

    public abstract void draw(Canvas canvas);

    public abstract int getAlpha();

    public abstract Drawable getDrawable();

    public abstract int getHeight();

    public abstract int getWidth();

    public void release() {
    }

    public abstract Sticker setAlpha(int i);

    public abstract Sticker setDrawable(Drawable drawable);

    public boolean contains(float f, float f2) {
        return contains(new float[]{f, f2});
    }

    public boolean contains(float[] fArr) {
        Matrix matrix = new Matrix();
        matrix.setRotate(-getCurrentAngle());
        getBoundPoints(this.boundPoints);
        getMappedPoints(this.mappedBounds, this.boundPoints);
        matrix.mapPoints(this.unrotatedWrapperCorner, this.mappedBounds);
        matrix.mapPoints(this.unrotatedPoint, fArr);
        StickerUtils.trapToRect(this.trappedRect, this.unrotatedWrapperCorner);
        RectF rectF = this.trappedRect;
        float[] fArr2 = this.unrotatedPoint;
        return rectF.contains(fArr2[0], fArr2[1]);
    }

    public RectF getBound() {
        RectF rectF = new RectF();
        getBound(rectF);
        return rectF;
    }

    public void getBound(RectF rectF) {
        rectF.set(0.0f, 0.0f, getWidth(), getHeight());
    }

    public void getBoundPoints(float[] fArr) {
        if (!this.isFlippedHorizontally) {
            if (!this.isFlippedVertically) {
                fArr[0] = 0.0f;
                fArr[1] = 0.0f;
                fArr[2] = getWidth();
                fArr[3] = 0.0f;
                fArr[4] = 0.0f;
                fArr[5] = getHeight();
                fArr[6] = getWidth();
                fArr[7] = getHeight();
                return;
            }
            fArr[0] = 0.0f;
            fArr[1] = getHeight();
            fArr[2] = getWidth();
            fArr[3] = getHeight();
            fArr[4] = 0.0f;
            fArr[5] = 0.0f;
            fArr[6] = getWidth();
            fArr[7] = 0.0f;
            return;
        }
        if (!this.isFlippedVertically) {
            fArr[0] = getWidth();
            fArr[1] = 0.0f;
            fArr[2] = 0.0f;
            fArr[3] = 0.0f;
            fArr[4] = getWidth();
            fArr[5] = getHeight();
            fArr[6] = 0.0f;
            fArr[7] = getHeight();
            return;
        }
        fArr[0] = getWidth();
        fArr[1] = getHeight();
        fArr[2] = 0.0f;
        fArr[3] = getHeight();
        fArr[4] = getWidth();
        fArr[5] = 0.0f;
        fArr[6] = 0.0f;
        fArr[7] = 0.0f;
    }

    public float[] getBoundPoints() {
        float[] fArr = new float[8];
        getBoundPoints(fArr);
        return fArr;
    }

    public PointF getCenterPoint() {
        PointF pointF = new PointF();
        getCenterPoint(pointF);
        return pointF;
    }

    public void getCenterPoint(PointF pointF) {
        pointF.set((getWidth() * 1.0f) / 2.0f, (getHeight() * 1.0f) / 2.0f);
    }

    public float getCurrentAngle() {
        return getMatrixAngle(this.matrix);
    }

    public RectF getMappedBound() {
        RectF rectF = new RectF();
        getMappedBound(rectF, getBound());
        return rectF;
    }

    public void getMappedBound(RectF rectF, RectF rectF2) {
        this.matrix.mapRect(rectF, rectF2);
    }

    public PointF getMappedCenterPoint() {
        PointF centerPoint = getCenterPoint();
        getMappedCenterPoint(centerPoint, new float[2], new float[2]);
        return centerPoint;
    }

    public void getMappedCenterPoint(PointF pointF, float[] fArr, float[] fArr2) {
        getCenterPoint(pointF);
        fArr2[0] = pointF.x;
        fArr2[1] = pointF.y;
        getMappedPoints(fArr, fArr2);
        pointF.set(fArr[0], fArr[1]);
    }

    public void getMappedPoints(float[] fArr, float[] fArr2) {
        this.matrix.mapPoints(fArr, fArr2);
    }

    public Matrix getMatrix() {
        return this.matrix;
    }

    public float getMatrixAngle(Matrix matrix) {
        return (float) Math.toDegrees(-Math.atan2(getMatrixValue(matrix, 1), getMatrixValue(matrix, 0)));
    }

    public float getMatrixValue(Matrix matrix, int i) {
        matrix.getValues(this.matrixValues);
        return this.matrixValues[i];
    }

    public boolean isFlippedHorizontally() {
        return this.isFlippedHorizontally;
    }

    public boolean isFlippedVertically() {
        return this.isFlippedVertically;
    }

    public boolean isShow() {
        return this.isShow;
    }

    public Sticker setFlippedHorizontally(boolean z) {
        this.isFlippedHorizontally = z;
        return this;
    }

    public Sticker setFlippedVertically(boolean z) {
        this.isFlippedVertically = z;
        return this;
    }

    public Sticker setMatrix(Matrix matrix) {
        this.matrix.set(matrix);
        return this;
    }

    public void setShow(boolean z) {
        this.isShow = z;
    }
}
