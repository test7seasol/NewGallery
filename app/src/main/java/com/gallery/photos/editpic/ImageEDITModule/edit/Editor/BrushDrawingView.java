package com.gallery.photos.editpic.ImageEDITModule.edit.Editor;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.gallery.photos.editpic.ImageEDITModule.edit.resource.BrushColor;
import com.gallery.photos.editpic.ImageEDITModule.edit.utils.SystemUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Stack;

/* loaded from: classes.dex */
public class BrushDrawingView extends View {
    private static final float TOUCH_TOLERANCE = 4.0f;
    private Paint bitmapPaint;
    private int brushBitmapSize;
    private List<Point> currentBitmapPoint;
    private DrawBitmapModel currentMagicBrush;
    private int distance;
    private int drawMode;
    private Stack<List<Point>> lstPoints;
    private boolean mBrushDrawMode;
    private float mBrushEraserSize;
    private float mBrushSize;
    private BrushViewChangeListener mBrushViewChangeListener;
    private Canvas mDrawCanvas;
    private Paint mDrawPaint;
    private Paint mDrawPaintBlur;
    private float mLastTouchX;
    private float mLastTouchY;
    private int mOpacity;
    private Path mPath;
    private Stack<Point> mPoints;
    private Stack<List<Point>> mRedoPaths;
    private float mTouchX;
    private float mTouchY;
    private Rect tempRect;

    /* JADX WARN: 'this' call moved to the top of the method (can break code semantics) */
    public BrushDrawingView(Context context) {
        this(context, null);
    }

    public BrushDrawingView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mBrushSize = 25.0f;
        this.mBrushEraserSize = 50.0f;
        this.mOpacity = 255;
        this.mPoints = new Stack<>();
        this.lstPoints = new Stack<>();
        this.mRedoPaths = new Stack<>();
        this.brushBitmapSize = SystemUtil.dpToPx(getContext(), 25);
        this.distance = SystemUtil.dpToPx(getContext(), 3);
        this.currentBitmapPoint = new ArrayList();
        this.tempRect = new Rect();
        setupBrushDrawing();
    }

    public BrushDrawingView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mBrushSize = 25.0f;
        this.mBrushEraserSize = 50.0f;
        this.mOpacity = 255;
        this.mPoints = new Stack<>();
        this.lstPoints = new Stack<>();
        this.mRedoPaths = new Stack<>();
        this.brushBitmapSize = SystemUtil.dpToPx(getContext(), 25);
        this.distance = SystemUtil.dpToPx(getContext(), 3);
        this.currentBitmapPoint = new ArrayList();
        this.tempRect = new Rect();
        setupBrushDrawing();
    }

    public void setCurrentMagicBrush(DrawBitmapModel drawBitmapModel) {
        this.currentMagicBrush = drawBitmapModel;
    }

    public void setDrawMode(int i) {
        this.drawMode = i;
        if (i == 2) {
            this.mDrawPaint.setColor(-1);
            this.mDrawPaintBlur.setColor(Color.parseColor(BrushColor.listColorBrush().get(0)));
            refreshBrushDrawing();
        } else {
            this.mDrawPaint.setColor(Color.parseColor(BrushColor.listColorBrush().get(0)));
            refreshBrushDrawing();
        }
    }

    private void setupBrushDrawing() {
        setLayerType(View.LAYER_TYPE_HARDWARE, null);
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        this.mDrawPaint = new Paint();
        this.mPath = new Path();
        this.mDrawPaint.setAntiAlias(true);
        this.mDrawPaint.setDither(true);
        this.mDrawPaint.setColor(Color.parseColor(BrushColor.listColorBrush().get(0)));
        this.mDrawPaint.setStyle(Paint.Style.FILL);
        this.mDrawPaint.setStrokeJoin(Paint.Join.ROUND);
        this.mDrawPaint.setStrokeCap(Paint.Cap.ROUND);
        this.mDrawPaint.setStrokeWidth(this.mBrushSize);
        this.mDrawPaint.setAlpha(this.mOpacity);
        this.mDrawPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));
        Paint paint = new Paint();
        this.mDrawPaintBlur = paint;
        paint.setAntiAlias(true);
        this.mDrawPaintBlur.setDither(true);
        this.mDrawPaintBlur.setStyle(Paint.Style.STROKE);
        this.mDrawPaintBlur.setStrokeJoin(Paint.Join.ROUND);
        this.mDrawPaintBlur.setMaskFilter(new BlurMaskFilter(25.0f, BlurMaskFilter.Blur.OUTER));
        this.mDrawPaintBlur.setStrokeCap(Paint.Cap.ROUND);
        this.mDrawPaintBlur.setStrokeWidth(this.mBrushSize * 1.1f);
        this.mDrawPaintBlur.setColor(Color.parseColor(BrushColor.listColorBrush().get(0)));
        this.mDrawPaintBlur.setAlpha(this.mOpacity);
        this.mDrawPaintBlur.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));
        Paint paint2 = new Paint();
        this.bitmapPaint = paint2;
        paint2.setStyle(Paint.Style.FILL);
        this.bitmapPaint.setStrokeJoin(Paint.Join.ROUND);
        this.bitmapPaint.setStrokeCap(Paint.Cap.ROUND);
        this.bitmapPaint.setStrokeWidth(this.mBrushSize);
        this.bitmapPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));
        setVisibility(View.GONE);
    }

    private void refreshBrushDrawing() {
        this.mBrushDrawMode = true;
        this.mPath = new Path();
        this.mDrawPaint.setAntiAlias(true);
        this.mDrawPaint.setDither(true);
        this.mDrawPaint.setStyle(Paint.Style.STROKE);
        this.mDrawPaint.setStrokeJoin(Paint.Join.ROUND);
        this.mDrawPaint.setStrokeCap(Paint.Cap.ROUND);
        this.mDrawPaint.setStrokeWidth(this.mBrushSize);
        this.mDrawPaint.setAlpha(this.mOpacity);
        this.mDrawPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));
        this.mDrawPaintBlur.setAntiAlias(true);
        this.mDrawPaintBlur.setDither(true);
        this.mDrawPaintBlur.setStyle(Paint.Style.STROKE);
        this.mDrawPaintBlur.setStrokeJoin(Paint.Join.ROUND);
        this.mDrawPaintBlur.setMaskFilter(new BlurMaskFilter(30.0f, BlurMaskFilter.Blur.OUTER));
        this.mDrawPaintBlur.setStrokeCap(Paint.Cap.ROUND);
        this.mDrawPaintBlur.setStrokeWidth(this.mBrushSize * 1.1f);
        this.mDrawPaintBlur.setAlpha(this.mOpacity);
        this.mDrawPaintBlur.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));
        this.bitmapPaint.setStyle(Paint.Style.FILL);
        this.bitmapPaint.setStrokeJoin(Paint.Join.ROUND);
        this.bitmapPaint.setStrokeCap(Paint.Cap.ROUND);
        this.bitmapPaint.setStrokeWidth(this.mBrushSize);
        this.bitmapPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));
    }

    public void brushEraser() {
        this.mBrushDrawMode = true;
        this.drawMode = 4;
        this.mDrawPaint.setStrokeWidth(this.mBrushEraserSize);
        this.mDrawPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
    }

    public void setBrushDrawingMode(boolean z) {
        this.mBrushDrawMode = z;
        if (z) {
            setVisibility(View.VISIBLE);
            refreshBrushDrawing();
        }
    }

    public boolean getBrushDrawingMode() {
        return this.mBrushDrawMode;
    }

    public void setBrushSize(float f) {
        if (this.drawMode == 3) {
            this.brushBitmapSize = SystemUtil.dpToPx(getContext(), (int) f);
        } else {
            this.mBrushSize = f;
            setBrushDrawingMode(true);
        }
    }

    public void setBrushColor(int i) {
        int i2 = this.drawMode;
        if (i2 == 1) {
            this.mDrawPaint.setColor(i);
        } else if (i2 == 2) {
            this.mDrawPaintBlur.setColor(i);
        }
        setBrushDrawingMode(true);
    }

    public void setBrushEraserSize(float f) {
        this.mBrushEraserSize = f;
        setBrushDrawingMode(true);
    }

    public void setBrushEraserColor(int i) {
        this.mDrawPaint.setColor(i);
        setBrushDrawingMode(true);
    }

    public float getEraserSize() {
        return this.mBrushEraserSize;
    }

    public float getBrushSize() {
        return this.mBrushSize;
    }

    public int getBrushColor() {
        return this.mDrawPaint.getColor();
    }

    public void clearAll() {
        this.mRedoPaths.clear();
        this.mPoints.clear();
        this.lstPoints.clear();
        Canvas canvas = this.mDrawCanvas;
        if (canvas != null) {
            canvas.drawColor(0, PorterDuff.Mode.CLEAR);
        }
        invalidate();
    }

    public void setBrushViewChangeListener(BrushViewChangeListener brushViewChangeListener) {
        this.mBrushViewChangeListener = brushViewChangeListener;
    }

    @Override // android.view.View
    public void onSizeChanged(int i, int i2, int i3, int i4) {
        super.onSizeChanged(i, i2, i3, i4);
        if (i <= 0 || i2 <= 0) {
            return;
        }
        this.mDrawCanvas = new Canvas(Bitmap.createBitmap(i, i2, Bitmap.Config.ARGB_8888));
    }

    @Override // android.view.View
    public void onDraw(Canvas canvas) {
        Iterator<Point> it = this.mPoints.iterator();
        while (it.hasNext()) {
            Point next = it.next();
            if (next.vector2 != null) {
                this.tempRect.set(next.vector2.x, next.vector2.y, next.vector2.x1, next.vector2.y1);
                canvas.drawBitmap(next.vector2.bitmap, (Rect) null, this.tempRect, this.bitmapPaint);
            } else if (next.linePath != null) {
                canvas.drawPath(next.linePath.getDrawPath(), next.linePath.getDrawPaint());
            }
        }
        if (this.drawMode == 2) {
            canvas.drawPath(this.mPath, this.mDrawPaintBlur);
        }
        canvas.drawPath(this.mPath, this.mDrawPaint);
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (!this.mBrushDrawMode) {
            return false;
        }
        int x = (int) motionEvent.getX();
        int y = (int) motionEvent.getY();
        int action = motionEvent.getAction();
        if (action == 0) {
            touchStart(x, y);
        } else if (action == 1) {
            touchUp();
        } else if (action == 2) {
            touchMove(x, y);
        }
        invalidate();
        return true;
    }

    public static class LinePath {
        private Paint mDrawPaint;
        private Path mDrawPath;

        public LinePath(Path path, Paint paint) {
            this.mDrawPaint = new Paint(paint);
            this.mDrawPath = new Path(path);
        }

        public Paint getDrawPaint() {
            return this.mDrawPaint;
        }

        public Path getDrawPath() {
            return this.mDrawPath;
        }
    }

    public boolean undo() {
        if (!this.lstPoints.empty()) {
            List<Point> pop = this.lstPoints.pop();
            this.mRedoPaths.push(pop);
            this.mPoints.removeAll(pop);
            invalidate();
        }
        BrushViewChangeListener brushViewChangeListener = this.mBrushViewChangeListener;
        if (brushViewChangeListener != null) {
            brushViewChangeListener.onViewRemoved(this);
        }
        return !this.lstPoints.empty();
    }

    public boolean redo() {
        if (!this.mRedoPaths.empty()) {
            List<Point> pop = this.mRedoPaths.pop();
            Iterator<Point> it = pop.iterator();
            while (it.hasNext()) {
                this.mPoints.push(it.next());
            }
            this.lstPoints.push(pop);
            invalidate();
        }
        BrushViewChangeListener brushViewChangeListener = this.mBrushViewChangeListener;
        if (brushViewChangeListener != null) {
            brushViewChangeListener.onViewAdd(this);
        }
        return !this.mRedoPaths.empty();
    }

    private void touchStart(float f, float f2) {
        this.mRedoPaths.clear();
        this.mPath.reset();
        this.mPath.moveTo(f, f2);
        this.mTouchX = f;
        this.mTouchY = f2;
        BrushViewChangeListener brushViewChangeListener = this.mBrushViewChangeListener;
        if (brushViewChangeListener != null) {
            brushViewChangeListener.onStartDrawing();
        }
        if (this.drawMode == 3) {
            this.currentBitmapPoint.clear();
        }
    }

    private void touchMove(int i, int i2) {
        int nextInt;
        float f = i;
        float abs = Math.abs(f - this.mTouchX);
        float f2 = i2;
        float abs2 = Math.abs(f2 - this.mTouchY);
        if (abs >= TOUCH_TOLERANCE || abs2 >= TOUCH_TOLERANCE) {
            if (this.drawMode != 3) {
                Path path = this.mPath;
                float f3 = this.mTouchX;
                float f4 = this.mTouchY;
                path.quadTo(f3, f4, (f3 + f) / 2.0f, (f4 + f2) / 2.0f);
                this.mTouchX = f;
                this.mTouchY = f2;
                return;
            }
            if (Math.abs(f - this.mLastTouchX) > this.brushBitmapSize + this.distance || Math.abs(f2 - this.mLastTouchY) > this.brushBitmapSize + this.distance) {
                Random random = new Random();
                List<Vector2> list = this.currentMagicBrush.getmPositions();
                int i3 = list.size() > 0 ? list.get(list.size() - 1).drawableIndex : -1;
                do {
                    nextInt = random.nextInt(this.currentMagicBrush.getLstIconWhenDrawing().size());
                } while (nextInt == i3);
                int i4 = this.brushBitmapSize;
                Vector2 vector2 = new Vector2(i, i2, i + i4, i2 + i4, nextInt, this.currentMagicBrush.getBitmapByIndex(nextInt));
                list.add(vector2);
                Point point = new Point(vector2);
                this.mPoints.push(point);
                this.currentBitmapPoint.add(point);
                this.mLastTouchX = f;
                this.mLastTouchY = f2;
            }
        }
    }

    private void touchUp() {
        if (this.drawMode != 3) {
            ArrayList arrayList = new ArrayList();
            Point point = new Point(new LinePath(this.mPath, this.mDrawPaint));
            this.mPoints.push(point);
            arrayList.add(point);
            if (this.drawMode == 2) {
                Point point2 = new Point(new LinePath(this.mPath, this.mDrawPaintBlur));
                this.mPoints.push(point2);
                arrayList.add(point2);
            }
            this.lstPoints.push(arrayList);
        } else {
            this.lstPoints.push(new ArrayList(this.currentBitmapPoint));
            this.currentBitmapPoint.clear();
        }
        this.mPath = new Path();
        BrushViewChangeListener brushViewChangeListener = this.mBrushViewChangeListener;
        if (brushViewChangeListener != null) {
            brushViewChangeListener.onStopDrawing();
            this.mBrushViewChangeListener.onViewAdd(this);
        }
        this.mLastTouchX = 0.0f;
        this.mLastTouchY = 0.0f;
    }

    public static final class Vector2 {
        public Bitmap bitmap;
        int drawableIndex;
        public int x;
        int x1;
        public int y;
        int y1;

        Vector2(int i, int i2, int i3, int i4, int i5, Bitmap bitmap) {
            this.x = i;
            this.y = i2;
            this.x1 = i3;
            this.y1 = i4;
            this.bitmap = bitmap;
            this.drawableIndex = i5;
        }
    }

    class Point {
        LinePath linePath;
        Vector2 vector2;

        Point(LinePath linePath) {
            this.linePath = linePath;
        }

        Point(Vector2 vector2) {
            this.vector2 = vector2;
        }
    }

    public Bitmap getDrawBitmap(Bitmap bitmap) {
        int width = getWidth();
        int height = getHeight();
        Bitmap createBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(createBitmap);
        RectF rectF = new RectF(0.0f, 0.0f, width, height);
        canvas.drawBitmap(bitmap, null, rectF, null);
        Iterator<Point> it = this.mPoints.iterator();
        while (it.hasNext()) {
            Point next = it.next();
            if (next.vector2 != null) {
                this.tempRect.set(next.vector2.x, next.vector2.y, next.vector2.x1, next.vector2.y1);
                canvas.drawBitmap(next.vector2.bitmap, (Rect) null, this.tempRect, this.bitmapPaint);
            } else if (next.linePath != null) {
                canvas.drawPath(next.linePath.getDrawPath(), next.linePath.getDrawPaint());
            }
        }
        return createBitmap;
    }
}
