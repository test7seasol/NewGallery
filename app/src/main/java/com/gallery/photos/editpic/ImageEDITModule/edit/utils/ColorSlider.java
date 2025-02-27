package com.gallery.photos.editpic.ImageEDITModule.edit.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import androidx.core.content.ContextCompat;

import com.gallery.photos.editpic.R;

/* loaded from: classes.dex */
public class ColorSlider extends View {
    private Rect[] mColorFullRects;
    private Rect[] mColorRects;
    private int[] mColors;
    private boolean mIsLockMode;
    private OnColorSelectedListener mListener;
    private Paint mPaint;
    private int mSelectedItem;
    private Paint mSelectorPaint;

    public interface OnColorSelectedListener {
        void onColorChanged(int i, int i2);
    }

    public ColorSlider(Context context) {
        this(context, null);
    }

    public ColorSlider(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public ColorSlider(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mColors = new int[0];
        this.mColorRects = new Rect[0];
        this.mColorFullRects = new Rect[0];
        this.mIsLockMode = false;
        init(context, attributeSet);
    }

    public void setLockMode(boolean z) {
        this.mIsLockMode = z;
    }

    public boolean isLockMode() {
        return this.mIsLockMode;
    }

    public void setSelectorColor(int i) {
        Paint paint = this.mSelectorPaint;
        if (paint != null) {
            paint.setColor(i);
            invalidate();
        }
    }

    public void setSelectorStyle(Paint.Style style) {
        Paint paint = this.mSelectorPaint;
        if (paint != null) {
            paint.setStyle(style);
            invalidate();
        }
    }

    public void setSelectorColorResource(int i) {
        if (i != 0) {
            setSelectorColor(ContextCompat.getColor(getContext(), i));
        }
    }

    public void setHexColors(String[] strArr) {
        if (strArr == null || strArr.length <= 0) {
            return;
        }
        convertToColors(strArr);
        calculateRectangles();
        invalidate();
    }

    public void setColors(int[] iArr) {
        if (iArr == null || iArr.length <= 0) {
            return;
        }
        this.mColors = iArr;
        calculateRectangles();
        invalidate();
    }

    public void setGradient(int i, int i2, int i3) {
        if (i == 0 || i2 == 0 || i3 == 0) {
            return;
        }
        calculateColors(i, i2, i3);
        calculateRectangles();
        invalidate();
    }

    public void setGradient(int[] iArr, int i) {
        if (iArr == null || iArr.length < 2) {
            throw new IllegalArgumentException("Colors array must contain 2 or more color.");
        }
        if (iArr.length == 2) {
            setGradient(iArr[0], iArr[1], i);
            return;
        }
        calculateColors(iArr, i);
        calculateRectangles();
        invalidate();
    }

    public void selectColor(int i) {
        int i2 = 0;
        while (true) {
            int[] iArr = this.mColors;
            if (i2 >= iArr.length) {
                return;
            }
            if (iArr[i2] == i) {
                this.mSelectedItem = i2;
                invalidate();
                return;
            }
            i2++;
        }
    }

    public void setSelection(int i) {
        if (i >= this.mColors.length) {
            return;
        }
        this.mSelectedItem = i;
        invalidate();
    }

    public int getSelectedItem() {
        return this.mSelectedItem;
    }

    public int getSelectedColor() {
        return this.mColors[this.mSelectedItem];
    }

    public void setListener(OnColorSelectedListener onColorSelectedListener) {
        this.mListener = onColorSelectedListener;
    }
    private class ColorSliderTouchListener implements OnTouchListener {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (mIsLockMode) {
                return false;
            }
            return processTouch(motionEvent);
        }
    }

    private void init(Context context, AttributeSet attrs) {
        // Initialize Paint objects
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);

        mSelectorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mSelectorPaint.setStyle(Paint.Style.STROKE);
        mSelectorPaint.setColor(ContextCompat.getColor(context, android.R.color.darker_gray));
        mSelectorPaint.setStrokeWidth(2.0f);

        // Set touch listener
        setOnTouchListener(new ColorSliderTouchListener());

        if (attrs != null) {
            TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs,
                    R.styleable.ColorSlider, 0, 0);
            try {
                int selectorColor = typedArray.getColor(R.styleable.ColorSlider_cs_selector_color,
                        ContextCompat.getColor(context, android.R.color.darker_gray));
                int colorArrayResId = typedArray.getResourceId(R.styleable.ColorSlider_cs_colors, 0);
                int steps = typedArray.getInt(R.styleable.ColorSlider_cs_steps, 21);
                int fromColor = typedArray.getColor(R.styleable.ColorSlider_cs_from_color, 0);
                int toColor = typedArray.getColor(R.styleable.ColorSlider_cs_to_color, 0);
                int hexColorResId = typedArray.getResourceId(R.styleable.ColorSlider_cs_hex_colors, 0);

                if (colorArrayResId != 0) {
                    mColors = getResources().getIntArray(colorArrayResId);
                } else if (hexColorResId != 0) {
                    String[] hexColors = getResources().getStringArray(hexColorResId);
                    convertToColors(hexColors);
                } else if (fromColor != 0 && toColor != 0 && steps > 0) {
                    calculateColors(fromColor, toColor, steps);
                }

                mSelectorPaint.setColor(selectorColor);
            } catch (Exception e) {
                Log.e("ColorSlider", "Error initializing attributes: " + e.getMessage(), e);
            } finally {
                typedArray.recycle();
            }
        }

        // Ensure colors array is initialized
        if (mColors == null || mColors.length == 0) {
            initDefaultColors();
        }

        // Initialize rectangles
        mColorRects = new Rect[mColors.length];
        mColorFullRects = new Rect[mColors.length];
    }



    private void initDefaultColors() {
        this.mColors = new int[]{Color.parseColor("#FFFFFF"), Color.parseColor("#F44336"), Color.parseColor("#E91E63"), Color.parseColor("#9C27B0"), Color.parseColor("#673AB7"), Color.parseColor("#3F51B5"), Color.parseColor("#2196F3"), Color.parseColor("#03A9F4"), Color.parseColor("#00BCD4"), Color.parseColor("#009688"), Color.parseColor("#4CAF50"), Color.parseColor("#8BC34A"), Color.parseColor("#CDDC39"), Color.parseColor("#FFEB3B"), Color.parseColor("#FFC107"), Color.parseColor("#FF9800"), Color.parseColor("#FF5722"), Color.parseColor("#795548"), Color.parseColor("#9E9E9E"), Color.parseColor("#607D8B")};
    }

    private void convertToColors(String[] strArr) {
        this.mColors = new int[strArr.length];
        for (int i = 0; i < strArr.length; i++) {
            this.mColors[i] = Color.parseColor(strArr[i]);
        }
    }

    private void calculateColors(int[] iArr, int i) {
        ColorSlider colorSlider = this;
        int[] iArr2 = iArr;
        int length = iArr2.length;
        int i2 = length - 1;
        int i3 = i / i2;
        int i4 = i % i3;
        if (i4 == 0) {
            i4 = 0;
        }
        colorSlider.mColors = new int[i];
        int i5 = 1;
        while (i5 < length) {
            int i6 = i5 - 1;
            int i7 = iArr2[i6];
            int i8 = iArr2[i5];
            int i9 = i6 * i3;
            int i10 = i5 * i3;
            if (i5 == i2) {
                i10 += i4;
            }
            float alpha = Color.alpha(i7);
            float red = Color.red(i7);
            float green = Color.green(i7);
            float blue = Color.blue(i7);
            float alpha2 = Color.alpha(i8);
            int i11 = length;
            float f = i10 - i9;
            float f2 = (alpha2 - alpha) / f;
            float red2 = (Color.red(i8) - red) / f;
            float green2 = (Color.green(i8) - green) / f;
            float blue2 = (Color.blue(i8) - blue) / f;
            int i12 = 0;
            while (i9 < i10) {
                int i13 = i2;
                int[] iArr3 = colorSlider.mColors;
                float f3 = i12;
                iArr3[i9] = Color.argb((int) (alpha + (f2 * f3)), (int) (red + (red2 * f3)), (int) (green + (green2 * f3)), (int) ((f3 * blue2) + blue));
                i12++;
                i9++;
                colorSlider = this;
                i2 = i13;
                f2 = f2;
                red2 = red2;
                green2 = green2;
            }
            i5++;
            colorSlider = this;
            iArr2 = iArr;
            length = i11;
        }
    }

    private void calculateColors(int i, int i2, int i3) {
        float alpha = Color.alpha(i);
        float red = Color.red(i);
        float green = Color.green(i);
        float blue = Color.blue(i);
        float alpha2 = Color.alpha(i2);
        float f = i3;
        float f2 = (alpha2 - alpha) / f;
        float red2 = (Color.red(i2) - red) / f;
        float green2 = (Color.green(i2) - green) / f;
        float blue2 = (Color.blue(i2) - blue) / f;
        this.mColors = new int[i3];
        for (int i4 = 0; i4 < i3; i4++) {
            float f3 = i4;
            this.mColors[i4] = Color.argb((int) ((f2 * f3) + alpha), (int) ((red2 * f3) + red), (int) ((green2 * f3) + green), (int) ((f3 * blue2) + blue));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean processTouch(MotionEvent motionEvent) {
        if (motionEvent.getAction() == 0) {
            return true;
        }
        if (motionEvent.getAction() != 2 && motionEvent.getAction() != 1) {
            return false;
        }
        updateView(motionEvent.getX(), motionEvent.getY());
        return true;
    }

    private void updateView(float f, float f2) {
        boolean z = false;
        int i = 0;
        while (true) {
            Rect[] rectArr = this.mColorFullRects;
            if (i < rectArr.length) {
                Rect rect = rectArr[i];
                if (rect != null && isInRange(rect, (int) f, (int) f2) && i != this.mSelectedItem) {
                    this.mSelectedItem = i;
                    z = true;
                    break;
                }
                i++;
            } else {
                break;
            }
        }
        if (z) {
            invalidate();
            notifyChanged();
        }
    }

    private boolean isInRange(Rect rect, int i, int i2) {
        if (this.mIsLockMode) {
            return rect.contains(i, i2);
        }
        return rect.left <= i && rect.right >= i;
    }

    private void notifyChanged() {
        OnColorSelectedListener onColorSelectedListener = this.mListener;
        if (onColorSelectedListener != null) {
            int i = this.mSelectedItem;
            onColorSelectedListener.onColorChanged(i, this.mColors[i]);
        }
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (this.mColorRects.length > 0) {
            drawSlider(canvas);
        }
    }

    private void drawSlider(Canvas canvas) {
        if (this.mPaint != null) {
            for (int i = 0; i < this.mColorRects.length; i++) {
                this.mPaint.setColor(this.mColors[i]);
                if (i == this.mSelectedItem) {
                    canvas.drawRect(this.mColorFullRects[i], this.mPaint);
                    Paint paint = this.mSelectorPaint;
                    if (paint != null) {
                        canvas.drawRect(this.mColorFullRects[i], paint);
                    }
                } else {
                    canvas.drawRect(this.mColorRects[i], this.mPaint);
                }
            }
        }
    }

    @Override // android.view.View
    protected void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        setMeasuredDimension(MeasureSpec.getSize(i), MeasureSpec.getSize(i2));
        calculateRectangles();
    }

    private void calculateRectangles() {
        float measuredWidth = getMeasuredWidth();
        float measuredHeight = getMeasuredHeight();
        int[] iArr = this.mColors;
        float length = measuredWidth / iArr.length;
        this.mColorRects = new Rect[iArr.length];
        this.mColorFullRects = new Rect[iArr.length];
        float f = 0.1f * measuredHeight;
        int i = 0;
        while (i < this.mColors.length) {
            int i2 = (int) (i * length);
            int i3 = i + 1;
            int i4 = (int) (i3 * length);
            this.mColorRects[i] = new Rect(i2, (int) f, i4, (int) (measuredHeight - f));
            this.mColorFullRects[i] = new Rect(i2, 0, i4, (int) measuredHeight);
            i = i3;
        }
    }
}
