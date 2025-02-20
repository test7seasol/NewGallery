package com.gallery.photos.editpic.callendservice.custom;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.Typeface;
import android.os.Handler;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.Scroller;

import com.gallery.photos.editpic.R;

import java.util.Arrays;
import java.util.List;

public class WheelPicker extends View implements Runnable {
    private static final String TAG = "WheelPicker";
    private String fontPath;
    private boolean hasAtmospheric;
    private boolean hasCurtain;
    private boolean hasIndicator;
    private boolean hasSameWidth;
    private boolean isClick;
    private boolean isCurved;
    private boolean isCyclic;
    private boolean isDebug;
    private boolean isForceFinishScroll;
    private boolean isTouchTriggered;
    private Camera mCamera;
    private int mCurrentItemPosition;
    private int mCurtainColor;
    private List mData;
    private int mDownPointY;
    private int mDrawnCenterX;
    private int mDrawnCenterY;
    private int mDrawnItemCount;
    private int mHalfDrawnItemCount;
    private int mHalfItemHeight;
    private int mHalfWheelHeight;
    private final Handler mHandler;
    private int mIndicatorColor;
    private int mIndicatorSize;
    private int mItemAlign;
    private int mItemHeight;
    private int mItemSpace;
    private int mItemTextColor;
    private int mItemTextSize;
    private int mLastPointY;
    private Matrix mMatrixDepth;
    private Matrix mMatrixRotate;
    private int mMaxFlingY;
    private String mMaxWidthText;
    private int mMaximumVelocity;
    private int mMinFlingY;
    private int mMinimumVelocity;
    private OnWheelChangeListener mOnWheelChangeListener;
    private Paint mPaint;
    private Rect mRectCurrentItem;
    private Rect mRectDrawn;
    private Rect mRectIndicatorFoot;
    private Rect mRectIndicatorHead;
    private int mScrollOffsetY;
    private Scroller mScroller;
    private int mSelectedItemPosition;
    private int mSelectedItemTextColor;
    private int mTextMaxHeight;
    private int mTextMaxWidth;
    private int mTextMaxWidthPosition;
    private int mTouchSlop;
    private VelocityTracker mTracker;
    private int mVisibleItemCount;
    private int mWheelCenterX;
    private int mWheelCenterY;


    public interface OnItemSelectedListener {
    }


    public interface OnWheelChangeListener {
        void onWheelScrollStateChanged(int i);

        void onWheelScrolled(int i);

        void onWheelSelected(int i);
    }

    public void setOnItemSelectedListener(OnItemSelectedListener onItemSelectedListener) {
    }

    public WheelPicker(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mHandler = new Handler();
        this.mMinimumVelocity = 50;
        this.mMaximumVelocity = 8000;
        this.mTouchSlop = 8;
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.WheelPicker);
        int resourceId = obtainStyledAttributes.getResourceId(R.styleable.WheelPicker_wheel_data, 0);
        this.mData = Arrays.asList(getResources().getStringArray(resourceId == 0 ? R.array.WheelArrayDefault : resourceId));
        this.mItemTextSize = obtainStyledAttributes.getDimensionPixelSize(R.styleable.WheelPicker_wheel_item_text_size, getResources().getDimensionPixelSize(R.dimen.WheelItemTextSize));
        this.mVisibleItemCount = obtainStyledAttributes.getInt(R.styleable.WheelPicker_wheel_visible_item_count, 7);
        this.mSelectedItemPosition = obtainStyledAttributes.getInt(R.styleable.WheelPicker_wheel_selected_item_position, 0);
        this.hasSameWidth = obtainStyledAttributes.getBoolean(R.styleable.WheelPicker_wheel_same_width, false);
        this.mTextMaxWidthPosition = obtainStyledAttributes.getInt(R.styleable.WheelPicker_wheel_maximum_width_text_position, -1);
        this.mMaxWidthText = obtainStyledAttributes.getString(R.styleable.WheelPicker_wheel_maximum_width_text);
        this.mSelectedItemTextColor = obtainStyledAttributes.getColor(R.styleable.WheelPicker_wheel_selected_item_text_color, -1166541);
        this.mItemTextColor = obtainStyledAttributes.getColor(R.styleable.WheelPicker_wheel_item_text_color, -7829368);
        this.mItemSpace = obtainStyledAttributes.getDimensionPixelSize(R.styleable.WheelPicker_wheel_item_space, getResources().getDimensionPixelSize(R.dimen.WheelItemSpace));
        this.isCyclic = obtainStyledAttributes.getBoolean(R.styleable.WheelPicker_wheel_cyclic, false);
        this.hasIndicator = obtainStyledAttributes.getBoolean(R.styleable.WheelPicker_wheel_indicator, false);
        this.mIndicatorColor = obtainStyledAttributes.getColor(R.styleable.WheelPicker_wheel_indicator_color, -1166541);
        this.mIndicatorSize = obtainStyledAttributes.getDimensionPixelSize(R.styleable.WheelPicker_wheel_indicator_size, getResources().getDimensionPixelSize(R.dimen.WheelIndicatorSize));
        this.hasCurtain = obtainStyledAttributes.getBoolean(R.styleable.WheelPicker_wheel_curtain, false);
        this.mCurtainColor = obtainStyledAttributes.getColor(R.styleable.WheelPicker_wheel_curtain_color, -1996488705);
        this.hasAtmospheric = obtainStyledAttributes.getBoolean(R.styleable.WheelPicker_wheel_atmospheric, false);
        this.isCurved = obtainStyledAttributes.getBoolean(R.styleable.WheelPicker_wheel_curved, false);
        this.mItemAlign = obtainStyledAttributes.getInt(R.styleable.WheelPicker_wheel_item_align, 0);
        this.fontPath = obtainStyledAttributes.getString(R.styleable.WheelPicker_wheel_font_path);
        obtainStyledAttributes.recycle();
        updateVisibleItemCount();
        Paint paint = new Paint(69);
        this.mPaint = paint;
        paint.setTextSize(this.mItemTextSize);
        if (this.fontPath != null) {
            setTypeface(Typeface.createFromAsset(context.getAssets(), this.fontPath));
        }
        updateItemTextAlign();
        computeTextSize();
        this.mScroller = new Scroller(getContext());
        ViewConfiguration viewConfiguration = ViewConfiguration.get(getContext());
        this.mMinimumVelocity = viewConfiguration.getScaledMinimumFlingVelocity();
        this.mMaximumVelocity = viewConfiguration.getScaledMaximumFlingVelocity();
        this.mTouchSlop = viewConfiguration.getScaledTouchSlop();
        this.mRectDrawn = new Rect();
        this.mRectIndicatorHead = new Rect();
        this.mRectIndicatorFoot = new Rect();
        this.mRectCurrentItem = new Rect();
        this.mCamera = new Camera();
        this.mMatrixRotate = new Matrix();
        this.mMatrixDepth = new Matrix();
    }

    private void updateVisibleItemCount() {
        int i = this.mVisibleItemCount;
        if (i < 2) {
            throw new ArithmeticException("Wheel's visible item count can not be less than 2!");
        }
        if (i % 2 == 0) {
            this.mVisibleItemCount = i + 1;
        }
        int i2 = this.mVisibleItemCount + 2;
        this.mDrawnItemCount = i2;
        this.mHalfDrawnItemCount = i2 / 2;
    }

    private void computeTextSize() {
        this.mTextMaxHeight = 0;
        this.mTextMaxWidth = 0;
        if (this.hasSameWidth) {
            this.mTextMaxWidth = (int) this.mPaint.measureText(String.valueOf(this.mData.get(0)));
        } else if (isPosInRang(this.mTextMaxWidthPosition)) {
            this.mTextMaxWidth = (int) this.mPaint.measureText(String.valueOf(this.mData.get(this.mTextMaxWidthPosition)));
        } else if (!TextUtils.isEmpty(this.mMaxWidthText)) {
            this.mTextMaxWidth = (int) this.mPaint.measureText(this.mMaxWidthText);
        } else {
            for (Object obj : this.mData) {
                String valueOf = String.valueOf(obj);
                this.mTextMaxWidth = Math.max(this.mTextMaxWidth, (int) this.mPaint.measureText(valueOf));
            }
        }
        Paint.FontMetrics fontMetrics = this.mPaint.getFontMetrics();
        this.mTextMaxHeight = (int) (fontMetrics.bottom - fontMetrics.top);
    }

    private void updateItemTextAlign() {
        int i = this.mItemAlign;
        if (i == 1) {
            this.mPaint.setTextAlign(Paint.Align.LEFT);
        } else if (i == 2) {
            this.mPaint.setTextAlign(Paint.Align.RIGHT);
        } else {
            this.mPaint.setTextAlign(Paint.Align.CENTER);
        }
    }

    @Override // android.binding.View
    protected void onMeasure(int i, int i2) {
        int mode = MeasureSpec.getMode(i);
        int mode2 = MeasureSpec.getMode(i2);
        int size = MeasureSpec.getSize(i);
        int size2 = MeasureSpec.getSize(i2);
        int i3 = this.mTextMaxWidth;
        int i4 = this.mTextMaxHeight;
        int i5 = this.mVisibleItemCount;
        int i6 = (i4 * i5) + (this.mItemSpace * (i5 - 1));
        if (this.isCurved) {
            i6 = (int) ((i6 * 2) / 3.141592653589793d);
        }
        if (this.isDebug) {
            String str = TAG;
            Log.i(str, "Wheel's content size is (" + i3 + ":" + i6 + ")");
        }
        int paddingLeft = i3 + getPaddingLeft() + getPaddingRight();
        int paddingTop = i6 + getPaddingTop() + getPaddingBottom();
        if (this.isDebug) {
            String str2 = TAG;
            Log.i(str2, "Wheel's size is (" + paddingLeft + ":" + paddingTop + ")");
        }
        setMeasuredDimension(measureSize(mode, size, paddingLeft), measureSize(mode2, size2, paddingTop));
    }

    private int measureSize(int i, int i2, int i3) {
        return i == 1073741824 ? i2 : i == Integer.MIN_VALUE ? Math.min(i3, i2) : i3;
    }

    @Override // android.binding.View
    protected void onSizeChanged(int i, int i2, int i3, int i4) {
        this.mRectDrawn.set(getPaddingLeft(), getPaddingTop(), getWidth() - getPaddingRight(), getHeight() - getPaddingBottom());
        if (this.isDebug) {
            String str = TAG;
            Log.i(str, "Wheel's drawn rect size is (" + this.mRectDrawn.width() + ":" + this.mRectDrawn.height() + ") and location is (" + this.mRectDrawn.left + ":" + this.mRectDrawn.top + ")");
        }
        this.mWheelCenterX = this.mRectDrawn.centerX();
        this.mWheelCenterY = this.mRectDrawn.centerY();
        computeDrawnCenter();
        this.mHalfWheelHeight = this.mRectDrawn.height() / 2;
        int height = this.mRectDrawn.height() / this.mVisibleItemCount;
        this.mItemHeight = height;
        this.mHalfItemHeight = height / 2;
        computeFlingLimitY();
        computeIndicatorRect();
        computeCurrentItemRect();
    }

    private void computeDrawnCenter() {
        int i = this.mItemAlign;
        if (i == 1) {
            this.mDrawnCenterX = this.mRectDrawn.left;
        } else if (i == 2) {
            this.mDrawnCenterX = this.mRectDrawn.right;
        } else {
            this.mDrawnCenterX = this.mWheelCenterX;
        }
        this.mDrawnCenterY = (int) (this.mWheelCenterY - ((this.mPaint.ascent() + this.mPaint.descent()) / 2.0f));
    }

    private void computeFlingLimitY() {
        int i = this.mSelectedItemPosition;
        int i2 = this.mItemHeight;
        int i3 = i * i2;
        this.mMinFlingY = this.isCyclic ? Integer.MIN_VALUE : ((-i2) * (this.mData.size() - 1)) + i3;
        if (this.isCyclic) {
            i3 = Integer.MAX_VALUE;
        }
        this.mMaxFlingY = i3;
    }

    private void computeIndicatorRect() {
        if (this.hasIndicator) {
            int i = this.mIndicatorSize / 2;
            int i2 = this.mWheelCenterY;
            int i3 = this.mHalfItemHeight;
            int i4 = i2 + i3;
            int i5 = i2 - i3;
            Rect rect = this.mRectIndicatorHead;
            Rect rect2 = this.mRectDrawn;
            rect.set(rect2.left, i4 - i, rect2.right, i4 + i);
            Rect rect3 = this.mRectIndicatorFoot;
            Rect rect4 = this.mRectDrawn;
            rect3.set(rect4.left, i5 - i, rect4.right, i5 + i);
        }
    }

    private void computeCurrentItemRect() {
        if (this.hasCurtain || this.mSelectedItemTextColor != -1) {
            Rect rect = this.mRectCurrentItem;
            Rect rect2 = this.mRectDrawn;
            int i = rect2.left;
            int i2 = this.mWheelCenterY;
            int i3 = this.mHalfItemHeight;
            rect.set(i, i2 - i3, rect2.right, i2 + i3);
        }
    }

    public static final int ALIGN_CENTER = 0, ALIGN_LEFT = 1, ALIGN_RIGHT = 2;


    @Override
    protected void onDraw(Canvas canvas) {
        if (null != mOnWheelChangeListener) mOnWheelChangeListener.onWheelScrolled(mScrollOffsetY);
        if (mData.size() == 0) return;
        int drawnDataStartPos = -mScrollOffsetY / mItemHeight - mHalfDrawnItemCount;
        for (int drawnDataPos = drawnDataStartPos + mSelectedItemPosition, drawnOffsetPos = -mHalfDrawnItemCount; drawnDataPos < drawnDataStartPos + mSelectedItemPosition + mDrawnItemCount; drawnDataPos++, drawnOffsetPos++) {
            String data = "";
            if (isCyclic) {
                int actualPos = drawnDataPos % mData.size();
                actualPos = actualPos < 0 ? (actualPos + mData.size()) : actualPos;
                data = String.valueOf(mData.get(actualPos));
            } else {
                if (isPosInRang(drawnDataPos)) data = String.valueOf(mData.get(drawnDataPos));
            }
            mPaint.setColor(mItemTextColor);
            mPaint.setStyle(Paint.Style.FILL);
            int mDrawnItemCenterY = mDrawnCenterY + (drawnOffsetPos * mItemHeight) + mScrollOffsetY % mItemHeight;

            int distanceToCenter = 0;
            if (isCurved) {
                // 计算数据项绘制中心距离滚轮中心的距离比率
                // Correct ratio of item's drawn center to wheel center
                float ratio = (mDrawnCenterY - Math.abs(mDrawnCenterY - mDrawnItemCenterY) - mRectDrawn.top) * 1.0F / (mDrawnCenterY - mRectDrawn.top);

                // 计算单位
                // Correct unit
                int unit = 0;
                if (mDrawnItemCenterY > mDrawnCenterY) unit = 1;
                else if (mDrawnItemCenterY < mDrawnCenterY) unit = -1;

                float degree = (-(1 - ratio) * 90 * unit);
                if (degree < -90) degree = -90;
                if (degree > 90) degree = 90;
                distanceToCenter = computeSpace((int) degree);

                int transX = mWheelCenterX;
                switch (mItemAlign) {
                    case ALIGN_LEFT:
                        transX = mRectDrawn.left;
                        break;
                    case ALIGN_RIGHT:
                        transX = mRectDrawn.right;
                        break;
                }
                int transY = mWheelCenterY - distanceToCenter;

                mCamera.save();
                mCamera.rotateX(degree);
                mCamera.getMatrix(mMatrixRotate);
                mCamera.restore();
                mMatrixRotate.preTranslate(-transX, -transY);
                mMatrixRotate.postTranslate(transX, transY);

                mCamera.save();
                mCamera.translate(0, 0, computeDepth((int) degree));
                mCamera.getMatrix(mMatrixDepth);
                mCamera.restore();
                mMatrixDepth.preTranslate(-transX, -transY);
                mMatrixDepth.postTranslate(transX, transY);

                mMatrixRotate.postConcat(mMatrixDepth);
            }
            if (hasAtmospheric) {
                int alpha = (int) ((mDrawnCenterY - Math.abs(mDrawnCenterY - mDrawnItemCenterY)) * 1.0F / mDrawnCenterY * 255);
                alpha = alpha < 0 ? 0 : alpha;
                mPaint.setAlpha(alpha);
            }
            // 根据卷曲与否计算数据项绘制Y方向中心坐标
            // Correct item's drawn centerY base on curved state
            int drawnCenterY = isCurved ? mDrawnCenterY - distanceToCenter : mDrawnItemCenterY;

            // 判断是否需要为当前数据项绘制不同颜色
            // Judges need to draw different color for current item or not
            if (mSelectedItemTextColor != -1) {
                canvas.save();
                if (isCurved) canvas.concat(mMatrixRotate);
                canvas.clipRect(mRectCurrentItem, Region.Op.DIFFERENCE);
                canvas.drawText(data, mDrawnCenterX, drawnCenterY, mPaint);
                canvas.restore();

                mPaint.setColor(mSelectedItemTextColor);
                canvas.save();
                if (isCurved) canvas.concat(mMatrixRotate);
                canvas.clipRect(mRectCurrentItem);
                canvas.drawText(data, mDrawnCenterX, drawnCenterY, mPaint);
                canvas.restore();
            } else {
                canvas.save();
                canvas.clipRect(mRectDrawn);
                if (isCurved) canvas.concat(mMatrixRotate);
                canvas.drawText(data, mDrawnCenterX, drawnCenterY, mPaint);
                canvas.restore();
            }
            if (isDebug) {
                canvas.save();
                canvas.clipRect(mRectDrawn);
                mPaint.setColor(0xFFEE3333);
                int lineCenterY = mWheelCenterY + (drawnOffsetPos * mItemHeight);
                canvas.drawLine(mRectDrawn.left, lineCenterY, mRectDrawn.right, lineCenterY, mPaint);
                mPaint.setColor(0xFF3333EE);
                mPaint.setStyle(Paint.Style.STROKE);
                int top = lineCenterY - mHalfItemHeight;
                canvas.drawRect(mRectDrawn.left, top, mRectDrawn.right, top + mItemHeight, mPaint);
                canvas.restore();
            }
        }
        // 是否需要绘制幕布
        // Need to draw curtain or not
        if (hasCurtain) {
            mPaint.setColor(mCurtainColor);
            mPaint.setStyle(Paint.Style.FILL);
            canvas.drawRect(mRectCurrentItem, mPaint);
        }
        // 是否需要绘制指示器
        // Need to draw indicator or not
        if (hasIndicator) {
            mPaint.setColor(mIndicatorColor);
            mPaint.setStyle(Paint.Style.FILL);
            canvas.drawRect(mRectIndicatorHead, mPaint);
            canvas.drawRect(mRectIndicatorFoot, mPaint);
        }
        if (isDebug) {
            mPaint.setColor(0x4433EE33);
            mPaint.setStyle(Paint.Style.FILL);
            canvas.drawRect(0, 0, getPaddingLeft(), getHeight(), mPaint);
            canvas.drawRect(0, 0, getWidth(), getPaddingTop(), mPaint);
            canvas.drawRect(getWidth() - getPaddingRight(), 0, getWidth(), getHeight(), mPaint);
            canvas.drawRect(0, getHeight() - getPaddingBottom(), getWidth(), getHeight(), mPaint);
        }
    }

    private boolean isPosInRang(int i) {
        return i >= 0 && i < this.mData.size();
    }

    private int computeSpace(int i) {
        return (int) (Math.sin(Math.toRadians(i)) * this.mHalfWheelHeight);
    }

    private int computeDepth(int i) {
        return (int) (this.mHalfWheelHeight - (Math.cos(Math.toRadians(i)) * this.mHalfWheelHeight));
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override // android.binding.View
    public boolean onTouchEvent(MotionEvent motionEvent) {
        int action = motionEvent.getAction();
        if (action == 0) {
            this.isTouchTriggered = true;
            if (getParent() != null) {
                getParent().requestDisallowInterceptTouchEvent(true);
            }
            VelocityTracker velocityTracker = this.mTracker;
            if (velocityTracker == null) {
                this.mTracker = VelocityTracker.obtain();
            } else {
                velocityTracker.clear();
            }
            this.mTracker.addMovement(motionEvent);
            if (!this.mScroller.isFinished()) {
                this.mScroller.abortAnimation();
                this.isForceFinishScroll = true;
            }
            int y = (int) motionEvent.getY();
            this.mLastPointY = y;
            this.mDownPointY = y;
        } else if (action == 1) {
            if (getParent() != null) {
                getParent().requestDisallowInterceptTouchEvent(false);
            }
            if (!this.isClick || this.isForceFinishScroll) {
                this.mTracker.addMovement(motionEvent);
                this.mTracker.computeCurrentVelocity(1000, this.mMaximumVelocity);
                this.isForceFinishScroll = false;
                int yVelocity = (int) this.mTracker.getYVelocity();
                if (Math.abs(yVelocity) > this.mMinimumVelocity) {
                    this.mScroller.fling(0, this.mScrollOffsetY, 0, yVelocity, 0, 0, this.mMinFlingY, this.mMaxFlingY);
                    Scroller scroller = this.mScroller;
                    scroller.setFinalY(scroller.getFinalY() + computeDistanceToEndPoint(this.mScroller.getFinalY() % this.mItemHeight));
                } else {
                    Scroller scroller2 = this.mScroller;
                    int i = this.mScrollOffsetY;
                    scroller2.startScroll(0, i, 0, computeDistanceToEndPoint(i % this.mItemHeight));
                }
                if (!this.isCyclic) {
                    int finalY = this.mScroller.getFinalY();
                    int i2 = this.mMaxFlingY;
                    if (finalY > i2) {
                        this.mScroller.setFinalY(i2);
                    } else {
                        int finalY2 = this.mScroller.getFinalY();
                        int i3 = this.mMinFlingY;
                        if (finalY2 < i3) {
                            this.mScroller.setFinalY(i3);
                        }
                    }
                }
                this.mHandler.post(this);
                VelocityTracker velocityTracker2 = this.mTracker;
                if (velocityTracker2 != null) {
                    velocityTracker2.recycle();
                    this.mTracker = null;
                }
            }
        } else if (action != 2) {
            if (action == 3) {
                if (getParent() != null) {
                    getParent().requestDisallowInterceptTouchEvent(false);
                }
                VelocityTracker velocityTracker3 = this.mTracker;
                if (velocityTracker3 != null) {
                    velocityTracker3.recycle();
                    this.mTracker = null;
                }
            }
        } else if (Math.abs(this.mDownPointY - motionEvent.getY()) < this.mTouchSlop) {
            this.isClick = true;
        } else {
            this.isClick = false;
            this.mTracker.addMovement(motionEvent);
            OnWheelChangeListener onWheelChangeListener = this.mOnWheelChangeListener;
            if (onWheelChangeListener != null) {
                onWheelChangeListener.onWheelScrollStateChanged(1);
            }
            float y2 = motionEvent.getY() - this.mLastPointY;
            if (Math.abs(y2) >= 1.0f) {
                this.mScrollOffsetY = (int) (this.mScrollOffsetY + y2);
                this.mLastPointY = (int) motionEvent.getY();
                invalidate();
            }
        }
        return true;
    }

    private int computeDistanceToEndPoint(int i) {
        int i2;
        if (Math.abs(i) > this.mHalfItemHeight) {
            if (this.mScrollOffsetY < 0) {
                i2 = -this.mItemHeight;
            } else {
                i2 = this.mItemHeight;
            }
            return i2 - i;
        }
        return -i;
    }

    @Override // java.lang.Runnable
    public void run() {
        List list = this.mData;
        if (list == null || list.size() == 0) {
            return;
        }
        if (this.mScroller.isFinished() && !this.isForceFinishScroll) {
            int i = this.mItemHeight;
            if (i == 0) {
                return;
            }
            int size = (((-this.mScrollOffsetY) / i) + this.mSelectedItemPosition) % this.mData.size();
            if (size < 0) {
                size += this.mData.size();
            }
            if (this.isDebug) {
                String str = TAG;
                Log.i(str, size + ":" + this.mData.get(size) + ":" + this.mScrollOffsetY);
            }
            this.mCurrentItemPosition = size;
            OnWheelChangeListener onWheelChangeListener = this.mOnWheelChangeListener;
            if (onWheelChangeListener != null && this.isTouchTriggered) {
                onWheelChangeListener.onWheelSelected(size);
                this.mOnWheelChangeListener.onWheelScrollStateChanged(0);
            }
        }
        if (this.mScroller.computeScrollOffset()) {
            OnWheelChangeListener onWheelChangeListener2 = this.mOnWheelChangeListener;
            if (onWheelChangeListener2 != null) {
                onWheelChangeListener2.onWheelScrollStateChanged(2);
            }
            this.mScrollOffsetY = this.mScroller.getCurrY();
            postInvalidate();
            this.mHandler.postDelayed(this, 16L);
        }
    }

    public void setDebug(boolean z) {
        this.isDebug = z;
    }

    public int getVisibleItemCount() {
        return this.mVisibleItemCount;
    }

    public void setVisibleItemCount(int i) {
        this.mVisibleItemCount = i;
        updateVisibleItemCount();
        requestLayout();
    }

    public void setCyclic(boolean z) {
        this.isCyclic = z;
        computeFlingLimitY();
        invalidate();
    }

    public int getSelectedItemPosition() {
        return this.mSelectedItemPosition;
    }

    public void setSelectedItemPosition(int i) {
        setSelectedItemPosition(i, true);
    }

    public void setSelectedItemPosition(int i, boolean z) {
        this.isTouchTriggered = false;
        if (z && this.mScroller.isFinished()) {
            int size = getData().size();
            int i2 = i - this.mCurrentItemPosition;
            if (i2 == 0) {
                return;
            }
            if (this.isCyclic && Math.abs(i2) > size / 2) {
                if (i2 > 0) {
                    size = -size;
                }
                i2 += size;
            }
            Scroller scroller = this.mScroller;
            scroller.startScroll(0, scroller.getCurrY(), 0, (-i2) * this.mItemHeight);
            this.mHandler.post(this);
            return;
        }
        if (!this.mScroller.isFinished()) {
            this.mScroller.abortAnimation();
        }
        int max = Math.max(Math.min(i, this.mData.size() - 1), 0);
        this.mSelectedItemPosition = max;
        this.mCurrentItemPosition = max;
        this.mScrollOffsetY = 0;
        computeFlingLimitY();
        requestLayout();
        invalidate();
    }

    public int getCurrentItemPosition() {
        return this.mCurrentItemPosition;
    }

    public List getData() {
        return this.mData;
    }

    public void setData(List list) {
        if (list == null) {
            throw new NullPointerException("WheelPicker's data can not be null!");
        }
        this.mData = list;
        if (this.mSelectedItemPosition > list.size() - 1 || this.mCurrentItemPosition > list.size() - 1) {
            int size = list.size() - 1;
            this.mCurrentItemPosition = size;
            this.mSelectedItemPosition = size;
        } else {
            this.mSelectedItemPosition = this.mCurrentItemPosition;
        }
        this.mScrollOffsetY = 0;
        computeTextSize();
        computeFlingLimitY();
        requestLayout();
        invalidate();
    }

    public void setSameWidth(boolean z) {
        this.hasSameWidth = z;
        computeTextSize();
        requestLayout();
        invalidate();
    }

    public void setOnWheelChangeListener(OnWheelChangeListener onWheelChangeListener) {
        this.mOnWheelChangeListener = onWheelChangeListener;
    }

    public String getMaximumWidthText() {
        return this.mMaxWidthText;
    }

    public void setMaximumWidthText(String str) {
        if (str == null) {
            throw new NullPointerException("Maximum width text can not be null!");
        }
        this.mMaxWidthText = str;
        computeTextSize();
        requestLayout();
        invalidate();
    }

    public int getMaximumWidthTextPosition() {
        return this.mTextMaxWidthPosition;
    }

    public void setMaximumWidthTextPosition(int i) {
        if (!isPosInRang(i)) {
            throw new ArrayIndexOutOfBoundsException("Maximum width text Position must in [0, " + this.mData.size() + "), but current is " + i);
        }
        this.mTextMaxWidthPosition = i;
        computeTextSize();
        requestLayout();
        invalidate();
    }

    public int getSelectedItemTextColor() {
        return this.mSelectedItemTextColor;
    }

    public void setSelectedItemTextColor(int i) {
        computeCurrentItemRect();
        invalidate();
    }

    public int getItemTextColor() {
        return this.mItemTextColor;
    }

    public void setItemTextColor(int i) {
        this.mItemTextColor = i;
        invalidate();
    }

    public int getItemTextSize() {
        return this.mItemTextSize;
    }

    public void setItemTextSize(int i) {
        this.mItemTextSize = i;
        this.mPaint.setTextSize(i);
        computeTextSize();
        requestLayout();
        invalidate();
    }

    public int getItemSpace() {
        return this.mItemSpace;
    }

    public void setItemSpace(int i) {
        this.mItemSpace = i;
        requestLayout();
        invalidate();
    }

    public void setIndicator(boolean z) {
        this.hasIndicator = z;
        computeIndicatorRect();
        invalidate();
    }

    public int getIndicatorSize() {
        return this.mIndicatorSize;
    }

    public void setIndicatorSize(int i) {
        this.mIndicatorSize = i;
        computeIndicatorRect();
        invalidate();
    }

    public int getIndicatorColor() {
        return this.mIndicatorColor;
    }

    public void setIndicatorColor(int i) {
        this.mIndicatorColor = i;
        invalidate();
    }

    public void setCurtain(boolean z) {
        this.hasCurtain = z;
        computeCurrentItemRect();
        invalidate();
    }

    public int getCurtainColor() {
        return this.mCurtainColor;
    }

    public void setCurtainColor(int i) {
        this.mCurtainColor = i;
        invalidate();
    }

    public void setAtmospheric(boolean z) {
        this.hasAtmospheric = z;
        invalidate();
    }

    public void setCurved(boolean z) {
        this.isCurved = z;
        requestLayout();
        invalidate();
    }

    public int getItemAlign() {
        return this.mItemAlign;
    }

    public void setItemAlign(int i) {
        this.mItemAlign = i;
        updateItemTextAlign();
        computeDrawnCenter();
        invalidate();
    }

    public Typeface getTypeface() {
        Paint paint = this.mPaint;
        if (paint != null) {
            return paint.getTypeface();
        }
        return null;
    }

    public void setTypeface(Typeface typeface) {
        Paint paint = this.mPaint;
        if (paint != null) {
            paint.setTypeface(typeface);
        }
        computeTextSize();
        requestLayout();
        invalidate();
    }
}
