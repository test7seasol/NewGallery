package com.gallery.photos.editpic.ImageEDITModule.edit.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import androidx.core.view.InputDeviceCompat;
import androidx.core.view.ViewCompat;

import com.gallery.photos.editpic.R;

import java.util.ArrayList;
import java.util.List;

public class ColorSeekBar extends View {
    private int c0;
    private int c1;
    private int mAlpha;
    private int mAlphaBarPosition;
    private int mAlphaMaxPosition;
    private int mAlphaMinPosition;
    private Rect mAlphaRect;
    private int mBackgroundColor;
    private int mBarHeight;
    private int mBarMargin;
    private int mBarWidth;
    private int mBlue;
    private int mColorBarPosition;
    private LinearGradient mColorGradient;
    private Rect mColorRect;
    private Paint mColorRectPaint;
    private int[] mColorSeeds;
    private List<Integer> mColors;
    private int mColorsToInvoke;
    private Context mContext;
    private boolean mFirstDraw;
    private int mGreen;
    private boolean mInit;
    private boolean mIsShowAlphaBar;
    private boolean mIsVertical;
    private int mMaxPosition;
    private boolean mMovingAlphaBar;
    private boolean mMovingColorBar;
    private OnColorChangeListener mOnColorChangeLister;
    private OnInitDoneListener mOnInitDoneListener;
    private int mPaddingSize;
    private int mRed;
    private int mThumbHeight;
    private float mThumbRadius;
    private Bitmap mTransparentBitmap;
    private int mViewHeight;
    private int mViewWidth;
    private int realBottom;
    private int realLeft;
    private int realRight;
    private int realTop;
    private int thumbColor;
    private float x;
    private float y;

    public interface OnColorChangeListener {
        void onColorChangeListener(int i, int i2, int i3);
    }

    public interface OnInitDoneListener {
        void done();
    }

    public ColorSeekBar(Context context) {
        super(context);
        this.mAlphaMaxPosition = 255;
        this.mAlphaMinPosition = 0;
        this.mBackgroundColor = -1;
        this.mBarHeight = 2;
        this.mBarMargin = 5;
        this.mColorSeeds = new int[]{ViewCompat.MEASURED_STATE_MASK, -6749953, -16776961, -16711936, -16711681, -65536, -65281, -39424, InputDeviceCompat.SOURCE_ANY, -1, ViewCompat.MEASURED_STATE_MASK};
        this.mColors = new ArrayList();
        this.mColorsToInvoke = -1;
        this.mFirstDraw = true;
        this.mInit = false;
        this.mIsShowAlphaBar = false;
        this.mThumbHeight = 20;
        init(context, null, 0, 0);
    }

    public ColorSeekBar(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mAlphaMaxPosition = 255;
        this.mAlphaMinPosition = 0;
        this.mBackgroundColor = -1;
        this.mBarHeight = 2;
        this.mBarMargin = 5;
        this.mColorSeeds = new int[]{ViewCompat.MEASURED_STATE_MASK, -6749953, -16776961, -16711936, -16711681, -65536, -65281, -39424, InputDeviceCompat.SOURCE_ANY, -1, ViewCompat.MEASURED_STATE_MASK};
        this.mColors = new ArrayList();
        this.mColorsToInvoke = -1;
        this.mFirstDraw = true;
        this.mInit = false;
        this.mIsShowAlphaBar = false;
        this.mThumbHeight = 20;
        init(context, attributeSet, 0, 0);
    }

    public ColorSeekBar(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mAlphaMaxPosition = 255;
        this.mAlphaMinPosition = 0;
        this.mBackgroundColor = -1;
        this.mBarHeight = 2;
        this.mBarMargin = 5;
        this.mColorSeeds = new int[]{ViewCompat.MEASURED_STATE_MASK, -6749953, -16776961, -16711936, -16711681, -65536, -65281, -39424, InputDeviceCompat.SOURCE_ANY, -1, ViewCompat.MEASURED_STATE_MASK};
        this.mColors = new ArrayList();
        this.mColorsToInvoke = -1;
        this.mFirstDraw = true;
        this.mInit = false;
        this.mIsShowAlphaBar = false;
        this.mThumbHeight = 20;
        init(context, attributeSet, i, 0);
    }

    public void init(Context context, AttributeSet attributeSet, int i, int i2) {
        applyStyle(context, attributeSet, i, i2);
    }

    @Override // android.view.View
    public void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        this.mViewWidth = i;
        this.mViewHeight = i2;
        int mode = MeasureSpec.getMode(i);
        MeasureSpec.getMode(i2);
        boolean z = this.mIsShowAlphaBar;
        int i3 = this.mBarHeight;
        if (z) {
            i3 *= 2;
        }
        int i4 = z ? this.mThumbHeight * 2 : this.mThumbHeight;
        if (isVertical()) {
            if (mode == Integer.MIN_VALUE || mode == 0) {
                int i5 = i4 + i3 + this.mBarMargin;
                this.mViewWidth = i5;
                setMeasuredDimension(i5, this.mViewHeight);
                return;
            }
            return;
        }
        if (mode == Integer.MIN_VALUE || mode == 0) {
            int i6 = i4 + i3 + this.mBarMargin;
            this.mViewHeight = i6;
            setMeasuredDimension(this.mViewWidth, i6);
        }
    }

    public void applyStyle(Context context, AttributeSet attributeSet, int i, int i2) {
        this.mContext = context;
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.ColorSeekBar, i, i2);
        int resourceId = obtainStyledAttributes.getResourceId(5, 0);
        this.mMaxPosition = obtainStyledAttributes.getInteger(R.styleable.ColorSeekBar_maxPosition, 100);
        this.mColorBarPosition = obtainStyledAttributes.getInteger(R.styleable.ColorSeekBar_colorBarPosition, 0);
        this.mAlphaBarPosition = obtainStyledAttributes.getInteger(0, this.mAlphaMinPosition);
        this.mIsVertical = obtainStyledAttributes.getBoolean(R.styleable.ColorSeekBar_isVertical, false);
        this.mIsShowAlphaBar = obtainStyledAttributes.getBoolean(R.styleable.ColorSeekBar_showAlphaBar, false);
        this.mBackgroundColor = obtainStyledAttributes.getColor(R.styleable.ColorSeekBar_bgColor, 0);
        this.mBarHeight = (int) obtainStyledAttributes.getDimension(R.styleable.ColorSeekBar_barHeight, dp2px(2.0f));
        this.mThumbHeight = (int) obtainStyledAttributes.getDimension(R.styleable.ColorSeekBar_thumbHeight, dp2px(30.0f));
        this.mBarMargin = (int) obtainStyledAttributes.getDimension(R.styleable.ColorSeekBar_barMargin, dp2px(5.0f));
        obtainStyledAttributes.recycle();
        if (resourceId != 0) {
            this.mColorSeeds = getColorsById(resourceId);
        }
        setBackgroundColor(this.mBackgroundColor);
    }

    private int[] getColorsById(int i) {
        int i2 = 0;
        if (isInEditMode()) {
            String[] stringArray = this.mContext.getResources().getStringArray(i);
            int[] iArr = new int[stringArray.length];
            while (i2 < stringArray.length) {
                iArr[i2] = Color.parseColor(stringArray[i2]);
                i2++;
            }
            return iArr;
        }
        TypedArray obtainTypedArray = this.mContext.getResources().obtainTypedArray(i);
        int[] iArr2 = new int[obtainTypedArray.length()];
        while (i2 < obtainTypedArray.length()) {
            iArr2[i2] = obtainTypedArray.getColor(i2, ViewCompat.MEASURED_STATE_MASK);
            i2++;
        }
        obtainTypedArray.recycle();
        return iArr2;
    }

    private void init() {
        float f = this.mThumbHeight / 2;
        this.mThumbRadius = f;
        this.mPaddingSize = (int) f;
        int height = (getHeight() - getPaddingBottom()) - this.mPaddingSize;
        int width = (getWidth() - getPaddingRight()) - this.mPaddingSize;
        this.realLeft = getPaddingLeft() + this.mPaddingSize;
        this.realRight = this.mIsVertical ? height : width;
        int paddingTop = getPaddingTop() + this.mPaddingSize;
        this.realTop = paddingTop;
        if (this.mIsVertical) {
            height = width;
        }
        this.realBottom = height;
        int i = this.realRight;
        int i2 = this.realLeft;
        this.mBarWidth = i - i2;
        this.mColorRect = new Rect(i2, paddingTop, this.realRight, this.mBarHeight + paddingTop);
        this.mColorGradient = new LinearGradient(0.0f, 0.0f, this.mColorRect.width(), 0.0f, this.mColorSeeds, (float[]) null, Shader.TileMode.MIRROR);
        Paint paint = new Paint();
        this.mColorRectPaint = paint;
        paint.setShader(this.mColorGradient);
        this.mColorRectPaint.setAntiAlias(true);
        cacheColors();
        setAlphaValue();
    }

    @Override // android.view.View
    public void onSizeChanged(int i, int i2, int i3, int i4) {
        super.onSizeChanged(i, i2, i3, i4);
        if (this.mIsVertical) {
            this.mTransparentBitmap = Bitmap.createBitmap(i2, i, Bitmap.Config.ARGB_4444);
        } else {
            this.mTransparentBitmap = Bitmap.createBitmap(i, i2, Bitmap.Config.ARGB_4444);
        }
        this.mTransparentBitmap.eraseColor(0);
        init();
        this.mInit = true;
        int i5 = this.mColorsToInvoke;
        if (i5 != -1) {
            setColor(i5);
        }
    }

    private void cacheColors() {
        if (this.mBarWidth >= 1) {
            this.mColors.clear();
            for (int i = 0; i <= this.mMaxPosition; i++) {
                this.mColors.add(Integer.valueOf(pickColor(i)));
            }
        }
    }

    @Override // android.view.View
    public void onDraw(Canvas canvas) {
        if (this.mIsVertical) {
            canvas.rotate(-90.0f);
            canvas.translate(-getHeight(), 0.0f);
            canvas.scale(-1.0f, 1.0f, getHeight() / 2, getWidth() / 2);
        }
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        int color = getColor(false);
        int argb = Color.argb(this.mAlphaMaxPosition, Color.red(color), Color.green(color), Color.blue(color));
        int argb2 = Color.argb(this.mAlphaMinPosition, Color.red(color), Color.green(color), Color.blue(color));
        paint.setColor(color);
        int[] iArr = {argb, argb2};
        canvas.drawBitmap(this.mTransparentBitmap, 0.0f, 0.0f, (Paint) null);
        canvas.drawRect(this.mColorRect, this.mColorRectPaint);
        float f = ((this.mColorBarPosition / this.mMaxPosition) * this.mBarWidth) + this.realLeft;
        float height = this.mColorRect.top + (this.mColorRect.height() / 2);
        canvas.drawCircle(f, height, (this.mBarHeight / 2) + 5, paint);
        new RadialGradient(f, height, this.mThumbRadius, iArr, (float[]) null, Shader.TileMode.MIRROR);
        Paint paint2 = new Paint();
        paint2.setAntiAlias(true);
        paint2.setColor(this.thumbColor);
        canvas.drawCircle(f, height, this.mThumbHeight / 2, paint2);
        if (this.mIsShowAlphaBar) {
            int i = (int) (this.mThumbHeight + this.mThumbRadius + this.mBarHeight + this.mBarMargin);
            this.mAlphaRect = new Rect(this.realLeft, i, this.realRight, this.mBarHeight + i);
            Paint paint3 = new Paint();
            paint3.setAntiAlias(true);
            paint3.setShader(new LinearGradient(0.0f, 0.0f, this.mAlphaRect.width(), 0.0f, iArr, (float[]) null, Shader.TileMode.MIRROR));
            canvas.drawRect(this.mAlphaRect, paint3);
            int i2 = this.mAlphaBarPosition;
            int i3 = this.mAlphaMinPosition;
            float f2 = (((i2 - i3) / (this.mAlphaMaxPosition - i3)) * this.mBarWidth) + this.realLeft;
            float height2 = this.mAlphaRect.top + (this.mAlphaRect.height() / 2);
            canvas.drawCircle(f2, height2, (this.mBarHeight / 2) + 5, paint);
            new RadialGradient(f2, height2, this.mThumbRadius, iArr, (float[]) null, Shader.TileMode.MIRROR);
            canvas.drawCircle(f2, height2, this.mThumbHeight / 2, new Paint());
        }
        if (this.mFirstDraw) {
            OnColorChangeListener onColorChangeListener = this.mOnColorChangeLister;
            if (onColorChangeListener != null) {
                onColorChangeListener.onColorChangeListener(this.mColorBarPosition, this.mAlphaBarPosition, getColor());
            }
            this.mFirstDraw = false;
            OnInitDoneListener onInitDoneListener = this.mOnInitDoneListener;
            if (onInitDoneListener != null) {
                onInitDoneListener.done();
            }
        }
        super.onDraw(canvas);
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent motionEvent) {
        this.x = this.mIsVertical ? motionEvent.getY() : motionEvent.getX();
        this.y = this.mIsVertical ? motionEvent.getX() : motionEvent.getY();
        int action = motionEvent.getAction();
        if (action != 0) {
            if (action == 1) {
                this.mMovingColorBar = false;
                this.mMovingAlphaBar = false;
            } else if (action == 2) {
                getParent().requestDisallowInterceptTouchEvent(true);
                if (this.mMovingColorBar) {
                    float f = (this.x - this.realLeft) / this.mBarWidth;
                    int i = this.mMaxPosition;
                    int i2 = (int) (f * i);
                    this.mColorBarPosition = i2;
                    if (i2 < 0) {
                        this.mColorBarPosition = 0;
                    }
                    if (this.mColorBarPosition > i) {
                        this.mColorBarPosition = i;
                    }
                } else if (this.mIsShowAlphaBar && this.mMovingAlphaBar) {
                    float f2 = (this.x - this.realLeft) / this.mBarWidth;
                    int i3 = this.mAlphaMaxPosition;
                    int i4 = this.mAlphaMinPosition;
                    int i5 = (int) ((f2 * (i3 - i4)) + i4);
                    this.mAlphaBarPosition = i5;
                    if (i5 < i4) {
                        this.mAlphaBarPosition = i4;
                    } else if (i5 > i3) {
                        this.mAlphaBarPosition = i3;
                    }
                    setAlphaValue();
                }
                OnColorChangeListener onColorChangeListener = this.mOnColorChangeLister;
                if (onColorChangeListener != null && (this.mMovingAlphaBar || this.mMovingColorBar)) {
                    onColorChangeListener.onColorChangeListener(this.mColorBarPosition, this.mAlphaBarPosition, getColor());
                }
                invalidate();
            }
        } else if (isOnBar(this.mColorRect, this.x, this.y)) {
            this.mMovingColorBar = true;
        } else if (this.mIsShowAlphaBar && isOnBar(this.mAlphaRect, this.x, this.y)) {
            this.mMovingAlphaBar = true;
        }
        return true;
    }

    private boolean isOnBar(Rect rect, float f, float f2) {
        return ((float) rect.left) - this.mThumbRadius < f && f < ((float) rect.right) + this.mThumbRadius && ((float) rect.top) - this.mThumbRadius < f2 && f2 < ((float) rect.bottom) + this.mThumbRadius;
    }

    private int pickColor(int i) {
        return pickColor((i / this.mMaxPosition) * this.mBarWidth);
    }

   /* private int pickColor(float f) {
        float f2 = f / this.mBarWidth;
        if (f2 <= 0.0d) {
            return this.mColorSeeds[0];
        }
        if (f2 >= 1.0f) {
            return this.mColorSeeds[r5.length - 1];
        }
        int[] iArr = this.mColorSeeds;
        float length = f2 * (iArr.length - 1);
        int i = (int) length;
        float f3 = length - i;
        int i2 = iArr[i];
        this.c0 = i2;
        this.c1 = iArr[i + 1];
        this.mRed = mix(Color.red(i2), Color.red(this.c1), f3);
        this.mGreen = mix(Color.green(this.c0), Color.green(this.c1), f3);
        int mix = mix(Color.blue(this.c0), Color.blue(this.c1), f3);
        this.mBlue = mix;
        return Color.rgb(this.mRed, this.mGreen, mix);
    }*/

    private int mix(int i, int i2, float f) {
        return i + Math.round(f * (i2 - i));
    }

    public int getColor() {
        return getColor(this.mIsShowAlphaBar);
    }

    public int getColor(boolean z) {
        if (this.mColorBarPosition >= this.mColors.size()) {
            int pickColor = pickColor(this.mColorBarPosition);
            return z ? pickColor : Color.argb(getAlphaValue(), Color.red(pickColor), Color.green(pickColor), Color.blue(pickColor));
        }
        int intValue = this.mColors.get(this.mColorBarPosition).intValue();
        return z ? Color.argb(getAlphaValue(), Color.red(intValue), Color.green(intValue), Color.blue(intValue)) : intValue;
    }

    public int getAlphaValue() {
        return this.mAlpha;
    }

    public void setOnColorChangeListener(OnColorChangeListener onColorChangeListener) {
        this.mOnColorChangeLister = onColorChangeListener;
    }

    public int dp2px(float f) {
        return (int) ((f * this.mContext.getResources().getDisplayMetrics().density) + 0.5f);
    }

    public List<Integer> getColors() {
        return this.mColors;
    }

    public boolean isVertical() {
        return this.mIsVertical;
    }

    private void setAlphaValue() {
        this.mAlpha = 255 - this.mAlphaBarPosition;
    }

    public void setColorBarPosition(int i) {
        this.mColorBarPosition = i;
        int i2 = this.mMaxPosition;
        if (i > i2) {
            i = i2;
        }
        this.mColorBarPosition = i;
        if (i < 0) {
            i = 0;
        }
        this.mColorBarPosition = i;
        invalidate();
        OnColorChangeListener onColorChangeListener = this.mOnColorChangeLister;
        if (onColorChangeListener != null) {
            onColorChangeListener.onColorChangeListener(this.mColorBarPosition, this.mAlphaBarPosition, getColor());
        }
    }

    public void setColor(int i) {
        int rgb = Color.rgb(Color.red(i), Color.green(i), Color.blue(i));
        if (this.mInit) {
            setColorBarPosition(this.mColors.indexOf(Integer.valueOf(rgb)));
        } else {
            this.mColorsToInvoke = i;
        }
    }
}
