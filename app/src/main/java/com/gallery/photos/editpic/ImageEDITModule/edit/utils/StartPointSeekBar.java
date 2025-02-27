package com.gallery.photos.editpic.ImageEDITModule.edit.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.gallery.photos.editpic.R;


public class StartPointSeekBar extends View {
    private static final float DEFAULT_MAX_VALUE = 100.0f;
    private double absoluteMaxValue;
    private double absoluteMinValue;
    private final int defaultBackgroundColor;
    private final int defaultRangeColor;
    private final float lineHeight;
    private OnSeekBarChangeListener listener;
    private double normalizedThumbValue;
    private final float padding;
    private long progress;
    RectF rect;
    private final float thumbHalfHeight;
    private final float thumbHalfWidth;
    private final Bitmap thumbImage;
    private static final int DEFAULT_BACKGROUND_COLOR = Color.parseColor("#66ffffff");
    private static final int DEFAULT_BORDER_COLOR = Color.parseColor("#33121212");
    private static final int DEFAULT_RANGE_COLOR = Color.parseColor("#F7252E");
    private static final Paint paint = new Paint(1);
    private static final Paint strokePaint = new Paint(1);

    public interface OnSeekBarChangeListener {
        void onOnSeekBarValueChange(StartPointSeekBar startPointSeekBar, long j);

        void onStartTrackingTouch(StartPointSeekBar startPointSeekBar);

        void onStopTrackingTouch(StartPointSeekBar startPointSeekBar);
    }

    public void setOnSeekBarChangeListener(OnSeekBarChangeListener onSeekBarChangeListener) {
        this.listener = onSeekBarChangeListener;
    }

    public StartPointSeekBar(Context context) {
        this(context, null);
    }

    public StartPointSeekBar(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public StartPointSeekBar(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.normalizedThumbValue = 0.0d;
        this.rect = new RectF();
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.ActionBar, i, 0);
        Drawable drawable = obtainStyledAttributes.getDrawable(7);
        drawable = drawable == null ? getResources().getDrawable(R.drawable.progress_thumb_alpha) : drawable;
        int dimension = (int) obtainStyledAttributes.getDimension(3, drawable.getIntrinsicHeight());
        int intrinsicHeight = (int) (dimension / (drawable.getIntrinsicHeight() / drawable.getIntrinsicWidth()));
        Bitmap createBitmap = Bitmap.createBitmap(intrinsicHeight, dimension, Bitmap.Config.ARGB_8888);
        this.thumbImage = createBitmap;
        Canvas canvas = new Canvas(createBitmap);
        drawable.setBounds(0, 0, intrinsicHeight, dimension);
        drawable.draw(canvas);
        this.absoluteMinValue = obtainStyledAttributes.getFloat(5, 0.0f);
        this.absoluteMaxValue = obtainStyledAttributes.getFloat(4, DEFAULT_MAX_VALUE);
        double valueToNormalized = valueToNormalized(obtainStyledAttributes.getFloat(6, (float) this.absoluteMinValue));
        this.normalizedThumbValue = valueToNormalized;
        this.progress = Math.round(normalizedToValue(valueToNormalized));
        this.defaultBackgroundColor = obtainStyledAttributes.getColor(0, DEFAULT_BACKGROUND_COLOR);
        this.defaultRangeColor = obtainStyledAttributes.getColor(1, DEFAULT_RANGE_COLOR);
        int color = obtainStyledAttributes.getColor(2, DEFAULT_BORDER_COLOR);
        obtainStyledAttributes.recycle();
        Paint paint2 = strokePaint;
        paint2.setStyle(Paint.Style.STROKE);
        paint2.setStrokeWidth(1.0f);
        paint2.setColor(color);
        float width = createBitmap.getWidth() * 0.5f;
        this.thumbHalfWidth = width;
        float height = createBitmap.getHeight() * 0.5f;
        this.thumbHalfHeight = height;
        this.lineHeight = height * 0.45f;
        this.padding = width;
        setFocusable(true);
        setFocusableInTouchMode(true);
    }

    public void setAbsoluteMinMaxValue(double d, double d2) {
        this.absoluteMinValue = d;
        this.absoluteMaxValue = d2;
    }

    @Override // android.view.View
    public synchronized void onMeasure(int i, int i2) {
        int size = MeasureSpec.getMode(i) != 0 ? MeasureSpec.getSize(i) : 200;
        int height = this.thumbImage.getHeight();
        if (MeasureSpec.getMode(i2) != 0) {
            height = Math.min(height, MeasureSpec.getSize(i2));
        }
        setMeasuredDimension(size, height);
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (!isEnabled()) {
            return false;
        }
        int action = motionEvent.getAction() & 255;
        if (action == 0) {
            trackTouchEvent(motionEvent);
            attemptClaimDrag();
            this.progress = Math.round(normalizedToValue(this.normalizedThumbValue));
            OnSeekBarChangeListener onSeekBarChangeListener = this.listener;
            if (onSeekBarChangeListener != null) {
                onSeekBarChangeListener.onStartTrackingTouch(this);
                this.listener.onOnSeekBarValueChange(this, this.progress);
            }
        } else if (action == 1) {
            trackTouchEvent(motionEvent);
            OnSeekBarChangeListener onSeekBarChangeListener2 = this.listener;
            if (onSeekBarChangeListener2 != null) {
                onSeekBarChangeListener2.onStopTrackingTouch(this);
            }
        } else if (action == 2) {
            trackTouchEvent(motionEvent);
            sendMoveAction();
        } else if (action == 3) {
            OnSeekBarChangeListener onSeekBarChangeListener3 = this.listener;
            if (onSeekBarChangeListener3 != null) {
                onSeekBarChangeListener3.onStopTrackingTouch(this);
            }
        } else if (action == 5) {
            trackTouchEvent(motionEvent);
            sendMoveAction();
        } else if (action == 6) {
            trackTouchEvent(motionEvent);
            sendMoveAction();
        }
        return true;
    }

    public long getProgress() {
        return this.progress;
    }

    private void sendMoveAction() {
        long round = Math.round(normalizedToValue(this.normalizedThumbValue));
        if (round != this.progress) {
            this.progress = round;
            OnSeekBarChangeListener onSeekBarChangeListener = this.listener;
            if (onSeekBarChangeListener != null) {
                onSeekBarChangeListener.onOnSeekBarValueChange(this, round);
            }
        }
    }

    private void attemptClaimDrag() {
        if (getParent() != null) {
            getParent().requestDisallowInterceptTouchEvent(true);
        }
    }

    private void trackTouchEvent(MotionEvent motionEvent) {
        setNormalizedValue(screenToNormalized(motionEvent.getX(motionEvent.getPointerCount() - 1)));
    }

   /* private double screenToNormalized(float f) {
        return getWidth() <= 2.0f * this.padding ? 0.0d : Math.min(1.0d, Math.max(0.0d, (f - r1) / (r0 - r2)));
    }*/

    private double screenToNormalized(float f) {
        float width = (float) getWidth();
        float f2 = this.padding;
        float f3 = 2.0f * f2;
        if (width <= f3) {
            return 0.0d;
        }
        return Math.min(1.0d, Math.max(0.0d, (double) ((f - f2) / (width - f3))));
    }



    private double normalizedToValue(double d) {
        double d2 = this.absoluteMinValue;
        return d2 + (d * (this.absoluteMaxValue - d2));
    }

    private double valueToNormalized(double d) {
        double d2 = this.absoluteMaxValue;
        double d3 = this.absoluteMinValue;
        double d4 = d2 - d3;
        return 0.0d == d4 ? 0.0d : (d - d3) / d4;
    }

    private void setNormalizedValue(double d) {
        this.normalizedThumbValue = Math.max(0.0d, d);
        invalidate();
    }

    public void setProgress(double d) {
        double valueToNormalized = valueToNormalized(d);
        if (valueToNormalized > this.absoluteMaxValue || valueToNormalized < this.absoluteMinValue) {
            throw new IllegalArgumentException("Value should be in the middle of max and min value");
        }
        this.normalizedThumbValue = valueToNormalized;
        invalidate();
    }

    private float normalizedToScreen(double d) {
        return (float) (this.padding + (d * (getWidth() - (this.padding * 2.0f))));
    }

    @Override // android.view.View
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.rect.top = (getHeight() - this.lineHeight) * 0.5f;
        this.rect.bottom = (getHeight() + this.lineHeight) * 0.5f;
        this.rect.left = this.padding;
        this.rect.right = getWidth() - this.padding;
        Paint paint2 = paint;
        paint2.setColor(this.defaultBackgroundColor);
        RectF rectF = this.rect;
        float f = this.lineHeight;
        canvas.drawRoundRect(rectF, f, f, paint2);
        if (normalizedToScreen(valueToNormalized(0.0d)) < normalizedToScreen(this.normalizedThumbValue)) {
            this.rect.left = normalizedToScreen(valueToNormalized(0.0d));
            this.rect.right = normalizedToScreen(this.normalizedThumbValue);
        } else {
            this.rect.right = normalizedToScreen(valueToNormalized(0.0d));
            this.rect.left = normalizedToScreen(this.normalizedThumbValue);
        }
        paint2.setColor(this.defaultRangeColor);
        if (this.absoluteMinValue < 0.0d) {
            canvas.drawRect(this.rect, paint2);
        } else {
            RectF rectF2 = this.rect;
            float f2 = this.lineHeight;
            canvas.drawRoundRect(rectF2, f2, f2, paint2);
        }
        this.rect.left = this.padding;
        this.rect.right = getWidth() - this.padding;
        RectF rectF3 = this.rect;
        float f3 = this.lineHeight;
        canvas.drawRoundRect(rectF3, f3, f3, strokePaint);
        drawThumb(normalizedToScreen(this.normalizedThumbValue), canvas);
    }

    private void drawThumb(float f, Canvas canvas) {
        canvas.drawBitmap(this.thumbImage, f - this.thumbHalfWidth, (getHeight() * 0.5f) - this.thumbHalfHeight, paint);
    }
}
