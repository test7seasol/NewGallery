package com.gallery.photos.editpic.ImageEDITModule.edit.sticker;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.widget.RelativeLayout;
import androidx.core.content.ContextCompat;
import androidx.core.view.MotionEventCompat;
import androidx.core.view.ViewCompat;

import com.gallery.photos.editpic.ImageEDITModule.edit.Editor.PTextView;
import com.gallery.photos.editpic.ImageEDITModule.edit.event.DeleteIconEvent;
import com.gallery.photos.editpic.ImageEDITModule.edit.event.FlipHorizontallyEvent;
import com.gallery.photos.editpic.ImageEDITModule.edit.event.ZoomIconEvent;
import com.gallery.photos.editpic.ImageEDITModule.edit.utils.SystemUtil;
import com.gallery.photos.editpic.R;
import java.io.File;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/* loaded from: classes.dex */
public class StickerView extends RelativeLayout {
    private static final int DEFAULT_MIN_CLICK_DELAY_TIME = 200;
    public static final int FLIP_HORIZONTALLY = 1;
    public static final int FLIP_VERTICALLY = 2;
    private static final String TAG = "StickerView";
    private final float[] bitmapPoints;
    private final Paint borderPaint;
    private final Paint borderPaintRed;
    private final float[] bounds;
    private boolean bringToFrontCurrentSticker;
    private int circleRadius;
    private boolean constrained;
    private final PointF currentCenterPoint;
    private BitmapStickerIcon currentIcon;
    private int currentMode;
    private float currentMoveingX;
    private float currentMoveingY;
    private final Matrix downMatrix;
    private float downX;
    private float downY;
    private boolean drawCirclePoint;
    private Sticker handlingSticker;
    private final List<BitmapStickerIcon> icons;
    private long lastClickTime;
    private Sticker lastHandlingSticker;
    private final Paint linePaint;
    private boolean locked;
    private PointF midPoint;
    private int minClickDelayTime;
    private final Matrix moveMatrix;
    private float oldDistance;
    private float oldRotation;
    private boolean onMoving;
    private OnStickerOperationListener onStickerOperationListener;
    private Paint paintCircle;
    private final float[] point;
    private boolean showBorder;
    private boolean showIcons;
    private final Matrix sizeMatrix;
    private final RectF stickerRect;
    private final List<Sticker> stickers;
    private final float[] tmp;
    private int touchSlop;

    @Retention(RetentionPolicy.SOURCE)
    public @interface ActionMode {
        public static final int CLICK = 4;
        public static final int DRAG = 1;
        public static final int ICON = 3;
        public static final int NONE = 0;
        public static final int ZOOM_WITH_TWO_FINGER = 2;
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface Flip {
    }

    public interface OnStickerOperationListener {
        void onStickerAdded(Sticker sticker);

        void onStickerClicked(Sticker sticker);

        void onStickerDeleted(Sticker sticker);

        void onStickerDoubleTapped(Sticker sticker);

        void onStickerDragFinished(Sticker sticker);

        void onStickerFlipped(Sticker sticker);

        void onStickerTouchOutside();

        void onStickerTouchedDown(Sticker sticker);

        void onStickerZoomFinished(Sticker sticker);

        void onTouchDownForBeauty(float f, float f2);

        void onTouchDragForBeauty(float f, float f2);

        void onTouchUpForBeauty(float f, float f2);
    }

    /* JADX WARN: 'this' call moved to the top of the method (can break code semantics) */
    public StickerView(Context context) {
        this(context, null);
    }

    public StickerView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public StickerView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.stickers = new ArrayList();
        this.icons = new ArrayList(4);
        Paint paint = new Paint();
        this.borderPaint = paint;
        Paint paint2 = new Paint();
        this.borderPaintRed = paint2;
        this.linePaint = new Paint();
        this.stickerRect = new RectF();
        this.sizeMatrix = new Matrix();
        this.downMatrix = new Matrix();
        this.moveMatrix = new Matrix();
        this.bitmapPoints = new float[8];
        this.bounds = new float[8];
        this.point = new float[2];
        this.currentCenterPoint = new PointF();
        this.tmp = new float[2];
        this.midPoint = new PointF();
        this.drawCirclePoint = false;
        this.onMoving = false;
        this.oldDistance = 0.0f;
        this.oldRotation = 0.0f;
        this.currentMode = 0;
        this.lastClickTime = 0L;
        this.minClickDelayTime = 200;
        Paint paint3 = new Paint();
        this.paintCircle = paint3;
        paint3.setAntiAlias(true);
        this.paintCircle.setDither(true);
        this.paintCircle.setColor(getContext().getResources().getColor(R.color.mainColor));
        this.paintCircle.setStrokeWidth(SystemUtil.dpToPx(getContext(), 2));
        this.paintCircle.setStyle(Paint.Style.STROKE);
        this.touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        try {
            TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, new int[]{R.attr.borderAlpha, R.attr.borderColor, R.attr.bringToFrontCurrentSticker, R.attr.showBorder, R.attr.showIcons});
            try {
                this.showIcons = obtainStyledAttributes.getBoolean(4, false);
                this.showBorder = obtainStyledAttributes.getBoolean(3, false);
                this.bringToFrontCurrentSticker = obtainStyledAttributes.getBoolean(2, false);
                paint.setAntiAlias(true);
                paint.setColor(obtainStyledAttributes.getColor(1, Color.parseColor("#FAAF76")));
                paint.setAlpha(obtainStyledAttributes.getInteger(0, 255));
                paint2.setAntiAlias(true);
                paint2.setColor(obtainStyledAttributes.getColor(1, Color.parseColor("#FAAF76")));
                paint2.setAlpha(obtainStyledAttributes.getInteger(0, 255));
                configDefaultIcons();
            } finally {
                if (obtainStyledAttributes != null) {
                    obtainStyledAttributes.recycle();
                }
            }
        } catch (Throwable unused) {
        }
    }

    public StickerView(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        this.stickers = new ArrayList();
        this.icons = new ArrayList(4);
        this.borderPaint = new Paint();
        this.borderPaintRed = new Paint();
        this.linePaint = new Paint();
        this.stickerRect = new RectF();
        this.sizeMatrix = new Matrix();
        this.downMatrix = new Matrix();
        this.moveMatrix = new Matrix();
        this.bitmapPoints = new float[8];
        this.bounds = new float[8];
        this.point = new float[2];
        this.currentCenterPoint = new PointF();
        this.tmp = new float[2];
        this.midPoint = new PointF();
        this.drawCirclePoint = false;
        this.onMoving = false;
        this.oldDistance = 0.0f;
        this.oldRotation = 0.0f;
        this.currentMode = 0;
        this.lastClickTime = 0L;
        this.minClickDelayTime = 200;
    }

    public Matrix getSizeMatrix() {
        return this.sizeMatrix;
    }

    public Matrix getDownMatrix() {
        return this.downMatrix;
    }

    public Matrix getMoveMatrix() {
        return this.moveMatrix;
    }

    public List<Sticker> getStickers() {
        return this.stickers;
    }

    public void configDefaultIcons() {
        BitmapStickerIcon bitmapStickerIcon = new BitmapStickerIcon(ContextCompat.getDrawable(getContext(), R.drawable.ic_outline_close), 0, BitmapStickerIcon.REMOVE);
        bitmapStickerIcon.setIconEvent(new DeleteIconEvent());
        BitmapStickerIcon bitmapStickerIcon2 = new BitmapStickerIcon(ContextCompat.getDrawable(getContext(), R.drawable.ic_outline_scale), 3, BitmapStickerIcon.ZOOM);
        bitmapStickerIcon2.setIconEvent(new ZoomIconEvent());
        BitmapStickerIcon bitmapStickerIcon3 = new BitmapStickerIcon(ContextCompat.getDrawable(getContext(), R.drawable.ic_outline_flip), 1, BitmapStickerIcon.FLIP);
        bitmapStickerIcon3.setIconEvent(new FlipHorizontallyEvent());
        BitmapStickerIcon bitmapStickerIcon4 = new BitmapStickerIcon(ContextCompat.getDrawable(getContext(), R.drawable.ic_outline_edit), 2, BitmapStickerIcon.EDIT);
        bitmapStickerIcon4.setIconEvent(new FlipHorizontallyEvent());
        this.icons.clear();
        this.icons.add(bitmapStickerIcon);
        this.icons.add(bitmapStickerIcon2);
        this.icons.add(bitmapStickerIcon3);
        this.icons.add(bitmapStickerIcon4);
    }

    public void swapLayers(int i, int i2) {
        if (this.stickers.size() < i || this.stickers.size() < i2) {
            return;
        }
        Collections.swap(this.stickers, i, i2);
        invalidate();
    }

    public void setHandlingSticker(Sticker sticker) {
        this.lastHandlingSticker = this.handlingSticker;
        this.handlingSticker = sticker;
        invalidate();
    }

    public void showLastHandlingSticker() {
        Sticker sticker = this.lastHandlingSticker;
        if (sticker == null || sticker.isShow()) {
            return;
        }
        this.lastHandlingSticker.setShow(true);
        invalidate();
    }

    @Override // android.widget.RelativeLayout, android.view.ViewGroup, android.view.View
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        if (z) {
            this.stickerRect.left = i;
            this.stickerRect.top = i2;
            this.stickerRect.right = i3;
            this.stickerRect.bottom = i4;
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    public void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (this.drawCirclePoint && this.onMoving) {
            canvas.drawCircle(this.downX, this.downY, this.circleRadius, this.paintCircle);
            canvas.drawLine(this.downX, this.downY, this.currentMoveingX, this.currentMoveingY, this.paintCircle);
        }
        drawStickers(canvas);
    }

    public void drawStickers(Canvas canvas) {
        float f;
        float f2;
        float f3;
        float f4;
        Canvas canvas2 = canvas;
        for (int i = 0; i < this.stickers.size(); i++) {
            Sticker sticker = this.stickers.get(i);
            if (sticker != null && sticker.isShow()) {
                sticker.draw(canvas2);
            }
        }
        Sticker sticker2 = this.handlingSticker;
        if (sticker2 != null && !this.locked && (this.showBorder || this.showIcons)) {
            getStickerPoints(sticker2, this.bitmapPoints);
            float[] fArr = this.bitmapPoints;
            float f5 = fArr[0];
            int i2 = 1;
            float f6 = fArr[1];
            int i3 = 2;
            float f7 = fArr[2];
            float f8 = fArr[3];
            float f9 = fArr[4];
            float f10 = fArr[5];
            float f11 = fArr[6];
            float f12 = fArr[7];
            if (this.showBorder) {
                Canvas canvas3 = canvas;
                f4 = f12;
                float f13 = f5;
                f3 = f11;
                float f14 = f6;
                f2 = f10;
                f = f9;
                canvas3.drawLine(f13, f14, f7, f8, this.borderPaint);
                canvas3.drawLine(f13, f14, f, f2, this.borderPaint);
                canvas3.drawLine(f7, f8, f3, f4, this.borderPaint);
                canvas3.drawLine(f3, f4, f, f2, this.borderPaint);
            } else {
                f4 = f12;
                f3 = f11;
                f2 = f10;
                f = f9;
            }
            if (this.showIcons) {
                float f15 = f4;
                float f16 = f3;
                float f17 = f2;
                float f18 = f;
                float calculateRotation = calculateRotation(f16, f15, f18, f17);
                int i4 = 0;
                while (i4 < this.icons.size()) {
                    BitmapStickerIcon bitmapStickerIcon = this.icons.get(i4);
                    int position = bitmapStickerIcon.getPosition();
                    if (position != 0) {
                        if (position != i2) {
                            if (position != i3) {
                                if (position == 3) {
                                    if ((!(this.handlingSticker instanceof PTextView) || !bitmapStickerIcon.getTag().equals(BitmapStickerIcon.ROTATE)) && (!(this.handlingSticker instanceof DrawableSticker) || !bitmapStickerIcon.getTag().equals(BitmapStickerIcon.ZOOM))) {
                                        Sticker sticker3 = this.handlingSticker;
                                        if (sticker3 instanceof BeautySticker) {
                                            BeautySticker beautySticker = (BeautySticker) sticker3;
                                            if (beautySticker.getType() != i2) {
                                                if (beautySticker.getType() != 2 && beautySticker.getType() != 8) {
                                                    if (beautySticker.getType() != 4) {
                                                    }
                                                }
                                                configIconMatrix(bitmapStickerIcon, f16, f15, calculateRotation);
                                                bitmapStickerIcon.draw(canvas2, this.borderPaint);
                                            } else {
                                                configIconMatrix(bitmapStickerIcon, f16, f15, calculateRotation);
                                                bitmapStickerIcon.draw(canvas2, this.borderPaint);
                                            }
                                        }
                                    } else {
                                        configIconMatrix(bitmapStickerIcon, f16, f15, calculateRotation);
                                        bitmapStickerIcon.draw(canvas2, this.borderPaint);
                                    }
                                }
                            }
                        } else if (((this.handlingSticker instanceof PTextView) && bitmapStickerIcon.getTag().equals(BitmapStickerIcon.EDIT)) || ((this.handlingSticker instanceof DrawableSticker) && bitmapStickerIcon.getTag().equals(BitmapStickerIcon.FLIP))) {
                            configIconMatrix(bitmapStickerIcon, f7, f8, calculateRotation);
                            bitmapStickerIcon.draw(canvas2, this.borderPaint);
                        }
                        Sticker sticker4 = this.handlingSticker;
                        if (!(sticker4 instanceof BeautySticker)) {
                            configIconMatrix(bitmapStickerIcon, f18, f17, calculateRotation);
                            bitmapStickerIcon.draw(canvas2, this.borderPaint);
                        } else if (((BeautySticker) sticker4).getType() == 0) {
                            configIconMatrix(bitmapStickerIcon, f18, f17, calculateRotation);
                            bitmapStickerIcon.draw(canvas2, this.borderPaint);
                        }
                    } else {
                        configIconMatrix(bitmapStickerIcon, f5, f6, calculateRotation);
                        bitmapStickerIcon.draw(canvas2, this.borderPaintRed);
                    }
                    i4++;
                    i2 = 1;
                    i3 = 2;
                }
            }
        }
        invalidate();
    }

    public void configIconMatrix(BitmapStickerIcon bitmapStickerIcon, float f, float f2, float f3) {
        bitmapStickerIcon.setX(f);
        bitmapStickerIcon.setY(f2);
        bitmapStickerIcon.getMatrix().reset();
        bitmapStickerIcon.getMatrix().postRotate(f3, bitmapStickerIcon.getWidth() / 2, bitmapStickerIcon.getHeight() / 2);
        bitmapStickerIcon.getMatrix().postTranslate(f - (bitmapStickerIcon.getWidth() / 2), f2 - (bitmapStickerIcon.getHeight() / 2));
    }

    @Override // android.view.ViewGroup
    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        if (this.locked) {
            return super.onInterceptTouchEvent(motionEvent);
        }
        if (motionEvent.getAction() != 0) {
            return super.onInterceptTouchEvent(motionEvent);
        }
        this.downX = motionEvent.getX();
        this.downY = motionEvent.getY();
        return (findCurrentIconTouched() == null && findHandlingSticker() == null) ? false : true;
    }

    public void setDrawCirclePoint(boolean z) {
        this.drawCirclePoint = z;
        this.onMoving = false;
    }

    @Override public boolean onTouchEvent(MotionEvent event) {
        if (locked) {
            return super.onTouchEvent(event);
        }

        int action = MotionEventCompat.getActionMasked(event);

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (!onTouchDown(event)) {
                    return false;
                }
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                oldDistance = calculateDistance(event);
                oldRotation = calculateRotation(event);

                midPoint = calculateMidPoint(event);

                if (handlingSticker != null && isInStickerArea(handlingSticker, event.getX(1),
                        event.getY(1)) && findCurrentIconTouched() == null) {
                    currentMode = ActionMode.ZOOM_WITH_TWO_FINGER;
                }
                break;

            case MotionEvent.ACTION_MOVE:
                handleCurrentMode(event);
                invalidate();
                break;

            case MotionEvent.ACTION_UP:
                onTouchUp(event);
                break;

            case MotionEvent.ACTION_POINTER_UP:
                if (currentMode == ActionMode.ZOOM_WITH_TWO_FINGER && handlingSticker != null) {
                    if (onStickerOperationListener != null) {
                        onStickerOperationListener.onStickerZoomFinished(handlingSticker);
                    }
                }
                currentMode = ActionMode.NONE;
                break;
        }

        return true;
    }

    public boolean onTouchDown(MotionEvent motionEvent) {
        this.currentMode = 1;
        this.downX = motionEvent.getX();
        this.downY = motionEvent.getY();
        this.onMoving = true;
        this.currentMoveingX = motionEvent.getX();
        this.currentMoveingY = motionEvent.getY();
        PointF calculateMidPoint = calculateMidPoint();
        this.midPoint = calculateMidPoint;
        this.oldDistance = calculateDistance(calculateMidPoint.x, this.midPoint.y, this.downX, this.downY);
        this.oldRotation = calculateRotation(this.midPoint.x, this.midPoint.y, this.downX, this.downY);
        BitmapStickerIcon findCurrentIconTouched = findCurrentIconTouched();
        this.currentIcon = findCurrentIconTouched;
        if (findCurrentIconTouched != null) {
            this.currentMode = 3;
            findCurrentIconTouched.onActionDown(this, motionEvent);
        } else {
            this.handlingSticker = findHandlingSticker();
        }
        Sticker sticker = this.handlingSticker;
        if (sticker != null) {
            this.downMatrix.set(sticker.getMatrix());
            if (this.bringToFrontCurrentSticker) {
                this.stickers.remove(this.handlingSticker);
                this.stickers.add(this.handlingSticker);
            }
            OnStickerOperationListener onStickerOperationListener = this.onStickerOperationListener;
            if (onStickerOperationListener != null) {
                onStickerOperationListener.onStickerTouchedDown(this.handlingSticker);
            }
        }
        if (this.drawCirclePoint) {
            this.onStickerOperationListener.onTouchDownForBeauty(this.currentMoveingX, this.currentMoveingY);
            invalidate();
            return true;
        }
        if (this.currentIcon == null && this.handlingSticker == null) {
            return false;
        }
        invalidate();
        return true;
    }

    public void onTouchUp(MotionEvent motionEvent) {
        Sticker sticker;
        OnStickerOperationListener onStickerOperationListener;
        Sticker sticker2;
        OnStickerOperationListener onStickerOperationListener2;
        BitmapStickerIcon bitmapStickerIcon;
        long uptimeMillis = SystemClock.uptimeMillis();
        this.onMoving = false;
        if (this.drawCirclePoint) {
            this.onStickerOperationListener.onTouchUpForBeauty(motionEvent.getX(), motionEvent.getY());
        }
        if (this.currentMode == 3 && (bitmapStickerIcon = this.currentIcon) != null && this.handlingSticker != null) {
            bitmapStickerIcon.onActionUp(this, motionEvent);
        }
        if (this.currentMode == 1 && Math.abs(motionEvent.getX() - this.downX) < this.touchSlop && Math.abs(motionEvent.getY() - this.downY) < this.touchSlop && (sticker2 = this.handlingSticker) != null) {
            this.currentMode = 4;
            OnStickerOperationListener onStickerOperationListener3 = this.onStickerOperationListener;
            if (onStickerOperationListener3 != null) {
                onStickerOperationListener3.onStickerClicked(sticker2);
            }
            if (uptimeMillis - this.lastClickTime < this.minClickDelayTime && (onStickerOperationListener2 = this.onStickerOperationListener) != null) {
                onStickerOperationListener2.onStickerDoubleTapped(this.handlingSticker);
            }
        }
        if (this.currentMode == 1 && (sticker = this.handlingSticker) != null && (onStickerOperationListener = this.onStickerOperationListener) != null) {
            onStickerOperationListener.onStickerDragFinished(sticker);
        }
        this.currentMode = 0;
        this.lastClickTime = uptimeMillis;
    }

    public void handleCurrentMode(MotionEvent motionEvent) {
        BitmapStickerIcon bitmapStickerIcon;
        int i = this.currentMode;
        if (i != 1) {
            if (i == 2) {
                if (this.handlingSticker != null) {
                    float calculateDistance = calculateDistance(motionEvent);
                    float calculateRotation = calculateRotation(motionEvent);
                    this.moveMatrix.set(this.downMatrix);
                    Matrix matrix = this.moveMatrix;
                    float f = this.oldDistance;
                    matrix.postScale(calculateDistance / f, calculateDistance / f, this.midPoint.x, this.midPoint.y);
                    this.moveMatrix.postRotate(calculateRotation - this.oldRotation, this.midPoint.x, this.midPoint.y);
                    this.handlingSticker.setMatrix(this.moveMatrix);
                    return;
                }
                return;
            }
            if (i != 3 || this.handlingSticker == null || (bitmapStickerIcon = this.currentIcon) == null) {
                return;
            }
            bitmapStickerIcon.onActionMove(this, motionEvent);
            return;
        }
        this.currentMoveingX = motionEvent.getX();
        float y = motionEvent.getY();
        this.currentMoveingY = y;
        if (this.drawCirclePoint) {
            this.onStickerOperationListener.onTouchDragForBeauty(this.currentMoveingX, y);
        }
        if (this.handlingSticker != null) {
            this.moveMatrix.set(this.downMatrix);
            Sticker sticker = this.handlingSticker;
            if (sticker instanceof BeautySticker) {
                BeautySticker beautySticker = (BeautySticker) sticker;
                if (beautySticker.getType() == 10 || beautySticker.getType() == 11) {
                    this.moveMatrix.postTranslate(0.0f, motionEvent.getY() - this.downY);
                } else {
                    this.moveMatrix.postTranslate(motionEvent.getX() - this.downX, motionEvent.getY() - this.downY);
                }
            } else {
                this.moveMatrix.postTranslate(motionEvent.getX() - this.downX, motionEvent.getY() - this.downY);
            }
            this.handlingSticker.setMatrix(this.moveMatrix);
            if (this.constrained) {
                constrainSticker(this.handlingSticker);
            }
        }
    }

    public void zoomAndRotateCurrentSticker(MotionEvent motionEvent) {
        zoomAndRotateSticker(this.handlingSticker, motionEvent);
    }

    public void alignHorizontally() {
        this.moveMatrix.set(this.downMatrix);
        this.moveMatrix.postRotate(-getCurrentSticker().getCurrentAngle(), this.midPoint.x, this.midPoint.y);
        this.handlingSticker.setMatrix(this.moveMatrix);
    }

    public void zoomAndRotateSticker(Sticker sticker, MotionEvent motionEvent) {
        float calculateDistance;
        if (sticker != null) {
            boolean z = sticker instanceof BeautySticker;
            if (z) {
                BeautySticker beautySticker = (BeautySticker) sticker;
                if (beautySticker.getType() == 10 || beautySticker.getType() == 11) {
                    return;
                }
            }
            if (sticker instanceof PTextView) {
                calculateDistance = this.oldDistance;
            } else {
                calculateDistance = calculateDistance(this.midPoint.x, this.midPoint.y, motionEvent.getX(), motionEvent.getY());
            }
            float calculateRotation = calculateRotation(this.midPoint.x, this.midPoint.y, motionEvent.getX(), motionEvent.getY());
            this.moveMatrix.set(this.downMatrix);
            Matrix matrix = this.moveMatrix;
            float f = this.oldDistance;
            matrix.postScale(calculateDistance / f, calculateDistance / f, this.midPoint.x, this.midPoint.y);
            if (!z) {
                this.moveMatrix.postRotate(calculateRotation - this.oldRotation, this.midPoint.x, this.midPoint.y);
            }
            this.handlingSticker.setMatrix(this.moveMatrix);
        }
    }

    public void constrainSticker(Sticker sticker) {
        int width = getWidth();
        int height = getHeight();
        sticker.getMappedCenterPoint(this.currentCenterPoint, this.point, this.tmp);
        float f = this.currentCenterPoint.x < 0.0f ? -this.currentCenterPoint.x : 0.0f;
        float f2 = width;
        if (this.currentCenterPoint.x > f2) {
            f = f2 - this.currentCenterPoint.x;
        }
        float f3 = this.currentCenterPoint.y < 0.0f ? -this.currentCenterPoint.y : 0.0f;
        float f4 = height;
        if (this.currentCenterPoint.y > f4) {
            f3 = f4 - this.currentCenterPoint.y;
        }
        sticker.getMatrix().postTranslate(f, f3);
    }

    public BitmapStickerIcon findCurrentIconTouched() {
        for (BitmapStickerIcon bitmapStickerIcon : this.icons) {
            float x = bitmapStickerIcon.getX() - this.downX;
            float y = bitmapStickerIcon.getY() - this.downY;
            if ((x * x) + (y * y) <= Math.pow(bitmapStickerIcon.getIconRadius() + bitmapStickerIcon.getIconRadius(), 2.0d)) {
                return bitmapStickerIcon;
            }
        }
        return null;
    }

    public Sticker findHandlingSticker() {
        for (int size = this.stickers.size() - 1; size >= 0; size--) {
            if (isInStickerArea(this.stickers.get(size), this.downX, this.downY)) {
                return this.stickers.get(size);
            }
        }
        return null;
    }

    public boolean isInStickerArea(Sticker sticker, float f, float f2) {
        float[] fArr = this.tmp;
        fArr[0] = f;
        fArr[1] = f2;
        return sticker.contains(fArr);
    }

    public PointF calculateMidPoint(MotionEvent motionEvent) {
        if (motionEvent == null || motionEvent.getPointerCount() < 2) {
            this.midPoint.set(0.0f, 0.0f);
            return this.midPoint;
        }
        this.midPoint.set((motionEvent.getX(0) + motionEvent.getX(1)) / 2.0f, (motionEvent.getY(0) + motionEvent.getY(1)) / 2.0f);
        return this.midPoint;
    }

    public PointF calculateMidPoint() {
        Sticker sticker = this.handlingSticker;
        if (sticker == null) {
            this.midPoint.set(0.0f, 0.0f);
            return this.midPoint;
        }
        sticker.getMappedCenterPoint(this.midPoint, this.point, this.tmp);
        return this.midPoint;
    }

    public float calculateRotation(MotionEvent motionEvent) {
        if (motionEvent == null || motionEvent.getPointerCount() < 2) {
            return 0.0f;
        }
        return calculateRotation(motionEvent.getX(0), motionEvent.getY(0), motionEvent.getX(1), motionEvent.getY(1));
    }

    public float calculateRotation(float f, float f2, float f3, float f4) {
        return (float) Math.toDegrees(Math.atan2(f2 - f4, f - f3));
    }

    public float calculateDistance(MotionEvent motionEvent) {
        if (motionEvent == null || motionEvent.getPointerCount() < 2) {
            return 0.0f;
        }
        return calculateDistance(motionEvent.getX(0), motionEvent.getY(0), motionEvent.getX(1), motionEvent.getY(1));
    }

    public float calculateDistance(float f, float f2, float f3, float f4) {
        double d = f - f3;
        double d2 = f2 - f4;
        return (float) Math.sqrt((d * d) + (d2 * d2));
    }

    public void transformSticker(Sticker sticker) {
        if (sticker == null) {
            Log.e(TAG, "transformSticker: the bitmapSticker is null or the bitmapSticker bitmap is null");
            return;
        }
        this.sizeMatrix.reset();
        float width = getWidth();
        float height = getHeight();
        float width2 = sticker.getWidth();
        float height2 = sticker.getHeight();
        this.sizeMatrix.postTranslate((width - width2) / 2.0f, (height - height2) / 2.0f);
        float f = (width < height ? width / width2 : height / height2) / 2.0f;
        this.sizeMatrix.postScale(f, f, width / 2.0f, height / 2.0f);
        sticker.getMatrix().reset();
        sticker.setMatrix(this.sizeMatrix);
        invalidate();
    }

    @Override // android.view.View
    public void onSizeChanged(int i, int i2, int i3, int i4) {
        super.onSizeChanged(i, i2, i3, i4);
        for (int i5 = 0; i5 < this.stickers.size(); i5++) {
            Sticker sticker = this.stickers.get(i5);
            if (sticker != null) {
                transformSticker(sticker);
            }
        }
    }

    public void flipCurrentSticker(int i) {
        flip(this.handlingSticker, i);
    }

    public void flip(Sticker sticker, int i) {
        if (sticker != null) {
            sticker.getCenterPoint(this.midPoint);
            if ((i & 1) > 0) {
                sticker.getMatrix().preScale(-1.0f, 1.0f, this.midPoint.x, this.midPoint.y);
                sticker.setFlippedHorizontally(!sticker.isFlippedHorizontally());
            }
            if ((i & 2) > 0) {
                sticker.getMatrix().preScale(1.0f, -1.0f, this.midPoint.x, this.midPoint.y);
                sticker.setFlippedVertically(!sticker.isFlippedVertically());
            }
            OnStickerOperationListener onStickerOperationListener = this.onStickerOperationListener;
            if (onStickerOperationListener != null) {
                onStickerOperationListener.onStickerFlipped(sticker);
            }
            invalidate();
        }
    }

    public boolean replace(Sticker sticker) {
        return replace(sticker, true);
    }

    public Sticker getLastHandlingSticker() {
        return this.lastHandlingSticker;
    }

    public boolean replace(Sticker sticker, boolean z) {
        int intrinsicHeight;
        float f;
        int intrinsicWidth;
        if (this.handlingSticker == null) {
            this.handlingSticker = this.lastHandlingSticker;
        }
        if (this.handlingSticker == null || sticker == null) {
            return false;
        }
        float width = getWidth();
        float height = getHeight();
        if (z) {
            sticker.setMatrix(this.handlingSticker.getMatrix());
            sticker.setFlippedVertically(this.handlingSticker.isFlippedVertically());
            sticker.setFlippedHorizontally(this.handlingSticker.isFlippedHorizontally());
        } else {
            this.handlingSticker.getMatrix().reset();
            sticker.getMatrix().postTranslate((width - this.handlingSticker.getWidth()) / 2.0f, (height - this.handlingSticker.getHeight()) / 2.0f);
            if (width < height) {
                Sticker sticker2 = this.handlingSticker;
                if (sticker2 instanceof PTextView) {
                    intrinsicWidth = sticker2.getWidth();
                } else {
                    intrinsicWidth = sticker2.getDrawable().getIntrinsicWidth();
                }
                f = width / intrinsicWidth;
            } else {
                Sticker sticker3 = this.handlingSticker;
                if (sticker3 instanceof PTextView) {
                    intrinsicHeight = sticker3.getHeight();
                } else {
                    intrinsicHeight = sticker3.getDrawable().getIntrinsicHeight();
                }
                f = height / intrinsicHeight;
            }
            float f2 = f / 2.0f;
            sticker.getMatrix().postScale(f2, f2, width / 2.0f, height / 2.0f);
        }
        List<Sticker> list = this.stickers;
        list.set(list.indexOf(this.handlingSticker), sticker);
        this.handlingSticker = sticker;
        invalidate();
        return true;
    }

    public boolean remove(Sticker sticker) {
        if (this.stickers.contains(sticker)) {
            this.stickers.remove(sticker);
            OnStickerOperationListener onStickerOperationListener = this.onStickerOperationListener;
            if (onStickerOperationListener != null) {
                onStickerOperationListener.onStickerDeleted(sticker);
            }
            if (this.handlingSticker == sticker) {
                this.handlingSticker = null;
            }
            invalidate();
            return true;
        }
        Log.d(TAG, "remove: the stickers is not in this StickerView");
        return false;
    }

    public boolean removeCurrentSticker() {
        return remove(this.handlingSticker);
    }

    public void removeAllStickers() {
        this.stickers.clear();
        Sticker sticker = this.handlingSticker;
        if (sticker != null) {
            sticker.release();
            this.handlingSticker = null;
        }
        invalidate();
    }

    public StickerView addSticker(Sticker sticker) {
        return addSticker(sticker, 1);
    }

    public StickerView addSticker(final Sticker sticker, final int i) {
        if (ViewCompat.isLaidOut(this)) {
            addStickerImmediately(sticker, i);
        } else {
            post(new Runnable() { // from class: com.gallery.photos.editphotovideo.sticker.StickerView.1
                @Override // java.lang.Runnable
                public void run() {
                    StickerView.this.addStickerImmediately(sticker, i);
                }
            });
        }
        return this;
    }

    public void addStickerImmediately(Sticker sticker, int i) {
        setStickerPosition(sticker, i);
        sticker.getMatrix().postScale(1.0f, 1.0f, getWidth(), getHeight());
        this.handlingSticker = sticker;
        this.stickers.add(sticker);
        OnStickerOperationListener onStickerOperationListener = this.onStickerOperationListener;
        if (onStickerOperationListener != null) {
            onStickerOperationListener.onStickerAdded(sticker);
        }
        invalidate();
    }

    public void setStickerPosition(Sticker sticker, int i) {
        float f;
        float width = getWidth() - sticker.getWidth();
        float height = getHeight() - sticker.getHeight();
        if (sticker instanceof BeautySticker) {
            BeautySticker beautySticker = (BeautySticker) sticker;
            f = height / 2.0f;
            if (beautySticker.getType() != 0) {
                if (beautySticker.getType() == 1) {
                    width *= 2.0f;
                } else {
                    if (beautySticker.getType() != 2 && beautySticker.getType() != 4) {
                        if (beautySticker.getType() == 10) {
                            width /= 2.0f;
                            f = (f * 2.0f) / 3.0f;
                        } else if (beautySticker.getType() == 11) {
                            width /= 2.0f;
                            f = (f * 3.0f) / 2.0f;
                        }
                    }
                    width /= 2.0f;
                }
            }
            width /= 3.0f;
        } else {
            f = (i & 2) > 0 ? height / 4.0f : (i & 16) > 0 ? height * 0.75f : height / 2.0f;
            if ((i & 4) > 0) {
                width /= 4.0f;
            } else {
                if ((i & 8) > 0) {
                    width *= 0.75f;
                }
                width /= 2.0f;
            }
        }
        sticker.getMatrix().postTranslate(width, f);
    }

    public void editTextSticker() {
        this.onStickerOperationListener.onStickerDoubleTapped(this.handlingSticker);
    }

    public float[] getStickerPoints(Sticker sticker) {
        float[] fArr = new float[8];
        getStickerPoints(sticker, fArr);
        return fArr;
    }

    public void getStickerPoints(Sticker sticker, float[] fArr) {
        if (sticker == null) {
            Arrays.fill(fArr, 0.0f);
        } else {
            sticker.getBoundPoints(this.bounds);
            sticker.getMappedPoints(fArr, this.bounds);
        }
    }

    public void save(File file) {
        try {
            StickerUtils.saveImageToGallery(file, createBitmap());
            StickerUtils.notifySystemGallery(getContext(), file);
        } catch (IllegalArgumentException | IllegalStateException unused) {
        }
    }

    public Bitmap createBitmap() throws OutOfMemoryError {
        this.handlingSticker = null;
        Bitmap createBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        draw(new Canvas(createBitmap));
        return createBitmap;
    }

    public int getStickerCount() {
        return this.stickers.size();
    }

    public boolean isNoneSticker() {
        return getStickerCount() == 0;
    }

    public StickerView setLocked(boolean z) {
        this.locked = z;
        invalidate();
        return this;
    }

    public StickerView setMinClickDelayTime(int i) {
        this.minClickDelayTime = i;
        return this;
    }

    public int getMinClickDelayTime() {
        return this.minClickDelayTime;
    }

    public boolean isConstrained() {
        return this.constrained;
    }

    public StickerView setConstrained(boolean z) {
        this.constrained = z;
        postInvalidate();
        return this;
    }

    public StickerView setOnStickerOperationListener(OnStickerOperationListener onStickerOperationListener) {
        this.onStickerOperationListener = onStickerOperationListener;
        return this;
    }

    public OnStickerOperationListener getOnStickerOperationListener() {
        return this.onStickerOperationListener;
    }

    public Sticker getCurrentSticker() {
        return this.handlingSticker;
    }

    public List<BitmapStickerIcon> getIcons() {
        return this.icons;
    }

    public void setIcons(List<BitmapStickerIcon> list) {
        this.icons.clear();
        this.icons.addAll(list);
        invalidate();
    }
}
