package com.gallery.photos.editpic.ImageEDITModule.github.siyamed.shapeimageview.mask;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;

import androidx.core.view.ViewCompat;

import com.gallery.photos.editpic.R;


/* loaded from: classes.dex */
public abstract class PorterImageView extends androidx.appcompat.widget.AppCompatImageView {
    private static final PorterDuffXfermode PORTER_DUFF_XFERMODE = new PorterDuffXfermode(PorterDuff.Mode.DST_IN);
    private static final String TAG = "PorterImageView";
    private Bitmap drawableBitmap;
    private Canvas drawableCanvas;
    private Paint drawablePaint;
    private boolean invalidated;
    private Bitmap maskBitmap;
    private Canvas maskCanvas;
    private Paint maskPaint;
    private boolean square;

    protected abstract void paintMaskCanvas(Canvas canvas, Paint paint, int i, int i2);

    public PorterImageView(Context context) {
        super(context);
        this.invalidated = true;
        this.square = false;
        setup(context, null, 0);
    }

    public PorterImageView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.invalidated = true;
        this.square = false;
        setup(context, attributeSet, 0);
    }

    public PorterImageView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.invalidated = true;
        this.square = false;
        setup(context, attributeSet, i);
    }

    public void setSquare(boolean z) {
        this.square = z;
    }

    private void setup(Context context, AttributeSet attributeSet, int i) {
        if (attributeSet != null) {
            TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.ShaderImageView, i, 0);
            this.square = obtainStyledAttributes.getBoolean(R.styleable.ShaderImageView_siSquare, false);
            obtainStyledAttributes.recycle();
        }
        if (getScaleType() == ScaleType.FIT_CENTER) {
            setScaleType(ScaleType.CENTER_CROP);
        }
        Paint paint = new Paint(1);
        this.maskPaint = paint;
        paint.setColor(ViewCompat.MEASURED_STATE_MASK);
    }

    @Override // android.view.View
    public void invalidate() {
        this.invalidated = true;
        super.invalidate();
    }

    @Override // android.view.View
    protected void onSizeChanged(int i, int i2, int i3, int i4) {
        super.onSizeChanged(i, i2, i3, i4);
        createMaskCanvas(i, i2, i3, i4);
    }

    private void createMaskCanvas(int i, int i2, int i3, int i4) {
        boolean z = false;
        boolean z2 = (i == i3 && i2 == i4) ? false : true;
        if (i > 0 && i2 > 0) {
            z = true;
        }
        if (z) {
            if (this.maskCanvas == null || z2) {
                this.maskCanvas = new Canvas();
                Bitmap createBitmap = Bitmap.createBitmap(i, i2, Bitmap.Config.ARGB_8888);
                this.maskBitmap = createBitmap;
                this.maskCanvas.setBitmap(createBitmap);
                this.maskPaint.reset();
                paintMaskCanvas(this.maskCanvas, this.maskPaint, i, i2);
                this.drawableCanvas = new Canvas();
                Bitmap createBitmap2 = Bitmap.createBitmap(i, i2, Bitmap.Config.ARGB_8888);
                this.drawableBitmap = createBitmap2;
                this.drawableCanvas.setBitmap(createBitmap2);
                this.drawablePaint = new Paint(1);
                this.invalidated = true;
            }
        }
    }

    @Override // android.widget.ImageView, android.view.View
    protected void onDraw(Canvas canvas) {
        Drawable drawable;
        if (!isInEditMode()) {
            int saveLayer = canvas.saveLayer(0.0f, 0.0f, getWidth(), getHeight(), null, 31);
            try {
                try {
                    if (this.invalidated && (drawable = getDrawable()) != null) {
                        this.invalidated = false;
                        Matrix imageMatrix = getImageMatrix();
                        if (imageMatrix == null) {
                            drawable.draw(this.drawableCanvas);
                        } else {
                            int saveCount = this.drawableCanvas.getSaveCount();
                            this.drawableCanvas.save();
                            this.drawableCanvas.concat(imageMatrix);
                            drawable.draw(this.drawableCanvas);
                            this.drawableCanvas.restoreToCount(saveCount);
                        }
                        this.drawablePaint.reset();
                        this.drawablePaint.setFilterBitmap(false);
                        this.drawablePaint.setXfermode(PORTER_DUFF_XFERMODE);
                        this.drawableCanvas.drawBitmap(this.maskBitmap, 0.0f, 0.0f, this.drawablePaint);
                    }
                    if (!this.invalidated) {
                        this.drawablePaint.setXfermode(null);
                        canvas.drawBitmap(this.drawableBitmap, 0.0f, 0.0f, this.drawablePaint);
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Exception occured while drawing " + getId(), e);
                }
                return;
            } finally {
                canvas.restoreToCount(saveLayer);
            }
        }
        super.onDraw(canvas);
    }

    @Override // android.widget.ImageView, android.view.View
    protected void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        if (this.square) {
            int min = Math.min(getMeasuredWidth(), getMeasuredHeight());
            setMeasuredDimension(min, min);
        }
    }
}
