package com.gallery.photos.editpic.ImageEDITModule.edit.Editor;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.view.MotionEventCompat;
import androidx.core.view.ViewCompat;

import com.gallery.photos.editpic.ImageEDITModule.edit.sticker.Sticker;
import com.gallery.photos.editpic.ImageEDITModule.edit.utils.SystemUtil;
import com.gallery.photos.editpic.R;
import java.util.Iterator;
import java.util.Random;
import java.util.Stack;

/* loaded from: classes.dex */
public class SplashSquareView extends AppCompatImageView {
    private Bitmap bitmap;
    private int brushSize;
    public int cSplashMode;
    private int currentMode;
    private float currentX;
    private float currentY;
    private Stack<BrushDrawingView.LinePath> linePathStack;
    private Stack<BrushDrawingView.LinePath> linePathStack1;
    private Stack<BrushDrawingView.LinePath> linePathStack2;
    private Path mPath;
    private float mTouchX;
    private float mTouchY;
    private final Matrix matrix;
    private PointF midPoint;
    private final Matrix moveMatrix;
    private float oldDistance;
    private float oldRotation;
    private Paint paint;
    private Paint paintCircle;
    private final float[] point;
    private final PointF pointF;
    private boolean showTouchIcon;
    private Sticker sticker;
    private final float[] tmp;

    public void setcSplashMode(int i) {
        this.cSplashMode = i;
    }

    public SplashSquareView(Context context) {
        super(context);
        this.brushSize = 100;
        this.pointF = new PointF();
        this.currentMode = 0;
        this.cSplashMode = 0;
        this.matrix = new Matrix();
        this.linePathStack = new Stack<>();
        this.linePathStack1 = new Stack<>();
        this.linePathStack2 = new Stack<>();
        this.midPoint = new PointF();
        this.moveMatrix = new Matrix();
        this.oldDistance = 0.0f;
        this.oldRotation = 0.0f;
        this.point = new float[2];
        this.showTouchIcon = false;
        this.tmp = new float[2];
        init();
    }

    public SplashSquareView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.brushSize = 100;
        this.pointF = new PointF();
        this.currentMode = 0;
        this.cSplashMode = 0;
        this.matrix = new Matrix();
        this.linePathStack = new Stack<>();
        this.linePathStack1 = new Stack<>();
        this.linePathStack2 = new Stack<>();
        this.midPoint = new PointF();
        this.moveMatrix = new Matrix();
        this.oldDistance = 0.0f;
        this.oldRotation = 0.0f;
        this.point = new float[2];
        this.showTouchIcon = false;
        this.tmp = new float[2];
        init();
    }

    public SplashSquareView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.brushSize = 100;
        this.pointF = new PointF();
        this.currentMode = 0;
        this.cSplashMode = 0;
        this.matrix = new Matrix();
        this.linePathStack = new Stack<>();
        this.linePathStack1 = new Stack<>();
        this.linePathStack2 = new Stack<>();
        this.midPoint = new PointF();
        this.moveMatrix = new Matrix();
        this.oldDistance = 0.0f;
        this.oldRotation = 0.0f;
        this.point = new float[2];
        this.showTouchIcon = false;
        this.tmp = new float[2];
        init();
    }

    @Override // androidx.appcompat.widget.AppCompatImageView, android.widget.ImageView
    public void setImageBitmap(Bitmap bitmap) {
        super.setImageBitmap(bitmap);
        setBitmap(bitmap);
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    private void init() {
        Paint paint = new Paint();
        this.paint = paint;
        paint.setAntiAlias(true);
        this.paint.setDither(true);
        this.paint.setStyle(Paint.Style.FILL);
        this.paint.setStrokeJoin(Paint.Join.ROUND);
        this.paint.setStrokeCap(Paint.Cap.ROUND);
        this.paint.setStrokeWidth(this.brushSize);
        this.paint.setMaskFilter(new BlurMaskFilter(20.0f, BlurMaskFilter.Blur.NORMAL));
        this.paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
        this.paint.setStyle(Paint.Style.STROKE);
        Paint paint2 = new Paint();
        this.paintCircle = paint2;
        paint2.setAntiAlias(true);
        this.paintCircle.setDither(true);
        this.paintCircle.setColor(getContext().getResources().getColor(R.color.colorAccent));
        this.paintCircle.setStrokeWidth(SystemUtil.dpToPx(getContext(), 2));
        this.paintCircle.setStyle(Paint.Style.STROKE);
        this.mPath = new Path();
    }

    public void addSticker(Sticker sticker) {
        addSticker(sticker, 1);
    }

    public void addSticker(final Sticker sticker, final int i) {
        if (ViewCompat.isLaidOut(this)) {
            addStickerImmediately(sticker, i);
        } else {
            post(new Runnable() { // from class: com.gallery.photos.editphotovideo.Editor.SplashSquareView.1
                @Override // java.lang.Runnable
                public void run() {
                    SplashSquareView.this.addStickerImmediately(sticker, i);
                }
            });
        }
    }

    public void addStickerImmediately(Sticker sticker, int i) {
        this.sticker = sticker;
        setStickerPosition(sticker, i);
        invalidate();
    }

    public void setStickerPosition(Sticker sticker, int i) {
        float f;
        int width;
        float width2 = getWidth();
        float height = getHeight();
        if (width2 > height) {
            f = (height * 4.0f) / 5.0f;
            width = sticker.getHeight();
        } else {
            f = (width2 * 4.0f) / 5.0f;
            width = sticker.getWidth();
        }
        float f2 = f / width;
        this.midPoint.set(0.0f, 0.0f);
        this.matrix.reset();
        this.moveMatrix.set(this.matrix);
        this.moveMatrix.postScale(f2, f2);
        this.moveMatrix.postRotate(new Random().nextInt(20) - 10, this.midPoint.x, this.midPoint.y);
        float width3 = width2 - ((int) (sticker.getWidth() * f2));
        float height2 = height - ((int) (sticker.getHeight() * f2));
        this.moveMatrix.postTranslate((i & 4) > 0 ? width3 / 4.0f : (i & 8) > 0 ? width3 * 0.75f : width3 / 2.0f, (i & 2) > 0 ? height2 / 4.0f : (i & 16) > 0 ? height2 * 0.75f : height2 / 2.0f);
        sticker.setMatrix(this.moveMatrix);
    }

    @Override // android.widget.ImageView, android.view.View
    public void onDraw(Canvas canvas) {
        Bitmap bitmap = this.bitmap;
        if (bitmap == null || bitmap.isRecycled()) {
            return;
        }
        super.onDraw(canvas);
        if (this.cSplashMode == 0) {
            drawStickers(canvas);
            return;
        }
        Iterator<BrushDrawingView.LinePath> it = this.linePathStack1.iterator();
        while (it.hasNext()) {
            BrushDrawingView.LinePath next = it.next();
            canvas.drawPath(next.getDrawPath(), next.getDrawPaint());
        }
        canvas.drawPath(this.mPath, this.paint);
        if (this.showTouchIcon) {
            canvas.drawCircle(this.currentX, this.currentY, this.brushSize / 2, this.paintCircle);
        }
    }

    public void drawStickers(Canvas canvas) {
        Sticker sticker = this.sticker;
        if (sticker != null && sticker.isShow()) {
            this.sticker.draw(canvas);
        }
        invalidate();
    }

    public float calculateDistance(float f, float f2, float f3, float f4) {
        double d = f - f3;
        double d2 = f2 - f4;
        return (float) Math.sqrt((d * d) + (d2 * d2));
    }

    public float calculateDistance(MotionEvent motionEvent) {
        if (motionEvent == null || motionEvent.getPointerCount() < 2) {
            return 0.0f;
        }
        return calculateDistance(motionEvent.getX(0), motionEvent.getY(0), motionEvent.getX(1), motionEvent.getY(1));
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

    public PointF calculateMidPoint(MotionEvent motionEvent) {
        if (motionEvent == null || motionEvent.getPointerCount() < 2) {
            this.midPoint.set(0.0f, 0.0f);
            return this.midPoint;
        }
        this.midPoint.set((motionEvent.getX(0) + motionEvent.getX(1)) / 2.0f, (motionEvent.getY(0) + motionEvent.getY(1)) / 2.0f);
        return this.midPoint;
    }

    public PointF calculateMidPoint() {
        Sticker sticker = this.sticker;
        if (sticker != null) {
            sticker.getMappedCenterPoint(this.midPoint, this.point, this.tmp);
        }
        return this.midPoint;
    }

    public boolean isInStickerArea(Sticker sticker, float f, float f2) {
        if (sticker == null) {
            return false;
        }
        float[] fArr = this.tmp;
        fArr[0] = f;
        fArr[1] = f2;
        return sticker.contains(fArr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Get the action type (e.g., ACTION_DOWN, ACTION_MOVE, ACTION_UP, etc.)
        int action = MotionEventCompat.getActionMasked(event);

        // Get the current touch coordinates
        float x = event.getX();
        float y = event.getY();

        // Store the current touch position
        this.currentX = x;
        this.currentY = y;

        // Constants to represent touch modes
        int MODE_NONE = 0;
        int MODE_ZOOM = 2;

        // Handle different touch actions
        switch (action) {
            case MotionEvent.ACTION_DOWN:  // First finger touches screen
                if (!onTouchDown(x, y)) {
                    invalidate(); // Redraw the view if needed
                    return false; // Return false to indicate the touch is not handled
                }
                break;

            case MotionEvent.ACTION_UP:  // Finger lifted
                onTouchUp(event);
                break;

            case MotionEvent.ACTION_MOVE:  // Finger is moving
                handleCurrentMode(x, y, event);
                invalidate(); // Redraw the view after moving
                break;

            case MotionEvent.ACTION_POINTER_DOWN:  // Second finger touches screen
                this.oldDistance = calculateDistance(event);
                this.oldRotation = calculateRotation(event);
                this.midPoint = calculateMidPoint(event);

                // Check if the second touch is inside the sticker area
                if (this.sticker != null) {
                    float secondTouchX = event.getX(1);
                    float secondTouchY = event.getY(1);

                    if (isInStickerArea(this.sticker, secondTouchX, secondTouchY)) {
                        this.currentMode = MODE_ZOOM;  // Enable zoom mode
                    }
                }
                break;

            case MotionEvent.ACTION_POINTER_UP:  // A finger is lifted in multi-touch
                this.currentMode = MODE_NONE;  // Reset to no touch mode
                break;
        }

        return true;  // Return true to indicate the touch event is handled
    }

    public void constrainSticker(Sticker sticker) {
        int width = getWidth();
        int height = getHeight();
        sticker.getMappedCenterPoint(this.pointF, this.point, this.tmp);
        float f = this.pointF.x < 0.0f ? -this.pointF.x : 0.0f;
        float f2 = width;
        if (this.pointF.x > f2) {
            f = f2 - this.pointF.x;
        }
        float f3 = this.pointF.y < 0.0f ? -this.pointF.y : 0.0f;
        float f4 = height;
        if (this.pointF.y > f4) {
            f3 = f4 - this.pointF.y;
        }
        sticker.getMatrix().postTranslate(f, f3);
    }

    public synchronized void handleCurrentMode(float f, float f2, MotionEvent motionEvent) {
        if (this.cSplashMode == 0) {
            int i = this.currentMode;
            if (i != 4) {
                if (i == 1) {
                    if (this.sticker != null) {
                        this.moveMatrix.set(this.matrix);
                        this.moveMatrix.postTranslate(motionEvent.getX() - this.mTouchX, motionEvent.getY() - this.mTouchY);
                        this.sticker.setMatrix(this.moveMatrix);
                    }
                } else if (i == 2 && this.sticker != null) {
                    float calculateDistance = calculateDistance(motionEvent);
                    float calculateRotation = calculateRotation(motionEvent);
                    this.moveMatrix.set(this.matrix);
                    Matrix matrix = this.moveMatrix;
                    float f3 = this.oldDistance;
                    matrix.postScale(calculateDistance / f3, calculateDistance / f3, this.midPoint.x, this.midPoint.y);
                    this.moveMatrix.postRotate(calculateRotation - this.oldRotation, this.midPoint.x, this.midPoint.y);
                    this.sticker.setMatrix(this.moveMatrix);
                }
            }
        } else {
            Path path = this.mPath;
            float f4 = this.mTouchX;
            float f5 = this.mTouchY;
            path.quadTo(f4, f5, (f4 + f) / 2.0f, (f5 + f2) / 2.0f);
            this.mTouchX = f;
            this.mTouchY = f2;
        }
    }

    public void setBrushSize(int i) {
        this.brushSize = i;
        this.paint.setStrokeWidth(i);
        this.showTouchIcon = true;
        this.currentX = getWidth() / 2;
        this.currentY = getHeight() / 2;
        invalidate();
    }

    public Sticker findHandlingSticker() {
        if (isInStickerArea(this.sticker, this.mTouchX, this.mTouchY)) {
            return this.sticker;
        }
        return null;
    }

    public boolean onTouchDown(float f, float f2) {
        this.currentMode = 1;
        this.mTouchX = f;
        this.mTouchY = f2;
        this.currentX = f;
        this.currentY = f2;
        if (this.cSplashMode == 0) {
            PointF calculateMidPoint = calculateMidPoint();
            this.midPoint = calculateMidPoint;
            this.oldDistance = calculateDistance(calculateMidPoint.x, this.midPoint.y, this.mTouchX, this.mTouchY);
            this.oldRotation = calculateRotation(this.midPoint.x, this.midPoint.y, this.mTouchX, this.mTouchY);
            Sticker findHandlingSticker = findHandlingSticker();
            if (findHandlingSticker != null) {
                this.matrix.set(this.sticker.getMatrix());
            }
            if (findHandlingSticker == null) {
                return false;
            }
        } else {
            this.showTouchIcon = true;
            this.linePathStack2.clear();
            this.mPath.reset();
            this.mPath.moveTo(f, f2);
        }
        invalidate();
        return true;
    }

    public void onTouchUp(MotionEvent motionEvent) {
        this.showTouchIcon = false;
        if (this.cSplashMode == 0) {
            this.currentMode = 0;
        } else {
            BrushDrawingView.LinePath linePath = new BrushDrawingView.LinePath(this.mPath, this.paint);
            this.linePathStack1.push(linePath);
            this.linePathStack.push(linePath);
            this.mPath = new Path();
        }
        invalidate();
    }

    public boolean undo() {
        if (!this.linePathStack.empty()) {
            BrushDrawingView.LinePath pop = this.linePathStack.pop();
            this.linePathStack2.push(pop);
            this.linePathStack1.remove(pop);
            invalidate();
        }
        return !this.linePathStack.empty();
    }

    public boolean redo() {
        if (!this.linePathStack2.empty()) {
            BrushDrawingView.LinePath pop = this.linePathStack2.pop();
            this.linePathStack1.push(pop);
            this.linePathStack.push(pop);
            invalidate();
        }
        return !this.linePathStack2.empty();
    }

    public Sticker getSticker() {
        return this.sticker;
    }

    public Bitmap getBitmap(Bitmap bitmap) {
        int width = getWidth();
        int height = getHeight();
        Bitmap createBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(createBitmap);
        Bitmap bitmap2 = this.bitmap;
        RectF rectF = new RectF(0.0f, 0.0f, width, height);
        canvas.drawBitmap(bitmap2, (Rect) null, rectF, (Paint) null);
        if (this.cSplashMode == 0) {
            drawStickers(canvas);
        } else {
            Iterator<BrushDrawingView.LinePath> it = this.linePathStack1.iterator();
            while (it.hasNext()) {
                BrushDrawingView.LinePath next = it.next();
                canvas.drawPath(next.getDrawPath(), next.getDrawPaint());
            }
        }
        Bitmap createBitmap2 = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas2 = new Canvas(createBitmap2);
        canvas2.drawBitmap(bitmap, (Rect) null, new RectF(0.0f, 0.0f, bitmap.getWidth(), bitmap.getHeight()), (Paint) null);
        canvas2.drawBitmap(createBitmap, (Rect) null, new RectF(0.0f, 0.0f, bitmap.getWidth(), bitmap.getHeight()), (Paint) null);
        return createBitmap2;
    }
}
