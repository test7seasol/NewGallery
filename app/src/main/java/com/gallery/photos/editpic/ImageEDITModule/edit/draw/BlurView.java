package com.gallery.photos.editpic.ImageEDITModule.edit.draw;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.gallery.photos.editpic.ImageEDITModule.edit.activities.BlurActivity;
import com.gallery.photos.editpic.ImageEDITModule.edit.utils.SystemUtil;
import com.gallery.photos.editpic.R;

/* loaded from: classes.dex */
public class BlurView extends ImageView {
    public static float resRatio;
    BitmapShader bitmapShader;
    Path brushPath;
    public Canvas canvas;
    Canvas canvasPreview;
    Paint circlePaint;
    Path circlePath;
    public boolean coloring;
    Context context;
    PointF curr;
    public int currentImageIndex;
    boolean draw;
    Paint drawPaint;
    Path drawPath;
    public Bitmap drawingBitmap;
    Rect dstRect;
    PointF last;
    Paint logPaintColor;
    Paint logPaintGray;
    float[] m;
    ScaleGestureDetector mScaleDetector;
    Matrix matrix;
    float maxScale;
    float minScale;
    public int mode;
    int oldMeasuredHeight;
    int oldMeasuredWidth;
    float oldX;
    float oldY;
    boolean onMeasureCalled;
    public int opacity;
    protected float origHeight;
    protected float origWidth;
    int pCount1;
    int pCount2;
    public boolean prViewDefaultPosition;
    Paint previewPaint;
    public float radius;
    public float saveScale;
    public Bitmap splashBitmap;
    PointF start;
    Paint tempPaint;
    Bitmap tempPreviewBitmap;
    int viewHeight;
    int viewWidth;
    float x;
    float y;

    public float getFixTrans(float f, float f2, float f3) {
        float f4;
        float f5;
        if (f3 <= f2) {
            f5 = f2 - f3;
            f4 = 0.0f;
        } else {
            f4 = f2 - f3;
            f5 = 0.0f;
        }
        if (f < f4) {
            return (-f) + f4;
        }
        if (f > f5) {
            return (-f) + f5;
        }
        return 0.0f;
    }

    private class MyAnimationListener implements Animation.AnimationListener {
        @Override // android.view.animation.Animation.AnimationListener
        public void onAnimationRepeat(Animation animation) {
        }

        @Override // android.view.animation.Animation.AnimationListener
        public void onAnimationStart(Animation animation) {
        }

        private MyAnimationListener() {
        }

        @Override // android.view.animation.Animation.AnimationListener
        public void onAnimationEnd(Animation animation) {
            if (BlurView.this.prViewDefaultPosition) {
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(BlurActivity.prView.getWidth(), BlurActivity.prView.getHeight());
                layoutParams.setMargins(0, 0, 0, 0);
                BlurActivity.prView.setLayoutParams(layoutParams);
            } else {
                RelativeLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams(BlurActivity.prView.getWidth(), BlurActivity.prView.getHeight());
                layoutParams2.setMargins(0, BlurView.this.viewHeight - BlurActivity.prView.getWidth(), 0, 0);
                BlurActivity.prView.setLayoutParams(layoutParams2);
            }
        }
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        private ScaleListener() {
        }

        @Override // android.view.ScaleGestureDetector.SimpleOnScaleGestureListener, android.view.ScaleGestureDetector.OnScaleGestureListener
        public boolean onScaleBegin(ScaleGestureDetector scaleGestureDetector) {
            BlurActivity.prView.setVisibility(View.INVISIBLE);
            if (BlurView.this.mode == 1 || BlurView.this.mode == 3) {
                BlurView.this.mode = 3;
            } else {
                BlurView.this.mode = 2;
            }
            BlurView.this.draw = false;
            return true;
        }

        @Override // android.view.ScaleGestureDetector.SimpleOnScaleGestureListener, android.view.ScaleGestureDetector.OnScaleGestureListener
        public boolean onScale(ScaleGestureDetector scaleGestureDetector) {
            float scaleFactor = scaleGestureDetector.getScaleFactor();
            float f = BlurView.this.saveScale;
            BlurView.this.saveScale *= scaleFactor;
            if (BlurView.this.saveScale > BlurView.this.maxScale) {
                BlurView blurView = BlurView.this;
                blurView.saveScale = blurView.maxScale;
                scaleFactor = BlurView.this.maxScale / f;
            } else {
                float f2 = BlurView.this.saveScale;
                float f3 = BlurView.this.minScale;
            }
            if (BlurView.this.origWidth * BlurView.this.saveScale <= BlurView.this.viewWidth || BlurView.this.origHeight * BlurView.this.saveScale <= BlurView.this.viewHeight) {
                BlurView.this.matrix.postScale(scaleFactor, scaleFactor, BlurView.this.viewWidth / 2, BlurView.this.viewHeight / 2);
            } else {
                BlurView.this.matrix.postScale(scaleFactor, scaleFactor, scaleGestureDetector.getFocusX(), scaleGestureDetector.getFocusY());
            }
            BlurView.this.matrix.getValues(BlurView.this.m);
            return true;
        }

        @Override // android.view.ScaleGestureDetector.SimpleOnScaleGestureListener, android.view.ScaleGestureDetector.OnScaleGestureListener
        public void onScaleEnd(ScaleGestureDetector scaleGestureDetector) {
            BlurView.this.radius = (BlurActivity.seekBarSize.getProgress() + 50) / BlurView.this.saveScale;
            BlurActivity.brushView.setShapeRadiusRatio((BlurActivity.seekBarSize.getProgress() + 50) / BlurView.this.saveScale);
            BlurView.this.updatePreviewPaint();
        }
    }

    public BlurView(Context context) {
        super(context);
        this.coloring = true;
        this.curr = new PointF();
        this.currentImageIndex = 0;
        this.draw = false;
        this.last = new PointF();
        this.maxScale = 5.0f;
        this.minScale = 1.0f;
        this.mode = 0;
        this.oldX = 0.0f;
        this.oldY = 0.0f;
        this.onMeasureCalled = false;
        this.opacity = 25;
        this.pCount1 = -1;
        this.pCount2 = -1;
        this.radius = 150.0f;
        this.saveScale = 1.0f;
        this.start = new PointF();
        this.context = context;
        sharedConstructing(context);
        this.prViewDefaultPosition = true;
        setDrawingCacheEnabled(true);
    }

    public BlurView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.coloring = true;
        this.curr = new PointF();
        this.currentImageIndex = 0;
        this.draw = false;
        this.last = new PointF();
        this.maxScale = 5.0f;
        this.minScale = 1.0f;
        this.mode = 0;
        this.oldX = 0.0f;
        this.oldY = 0.0f;
        this.onMeasureCalled = false;
        this.opacity = 25;
        this.pCount1 = -1;
        this.pCount2 = -1;
        this.radius = 150.0f;
        this.saveScale = 1.0f;
        this.start = new PointF();
        this.context = context;
        sharedConstructing(context);
        this.prViewDefaultPosition = true;
        setDrawingCacheEnabled(true);
    }

    public void initDrawing() {
        this.splashBitmap = BlurActivity.bitmapClear.copy(Bitmap.Config.ARGB_8888, true);
        Bitmap copy = Bitmap.createBitmap(BlurActivity.bitmapBlur).copy(Bitmap.Config.ARGB_8888, true);
        this.drawingBitmap = copy;
        setImageBitmap(copy);
        this.canvas = new Canvas(this.drawingBitmap);
        this.circlePath = new Path();
        this.drawPath = new Path();
        this.brushPath = new Path();
        Paint paint = new Paint();
        this.circlePaint = paint;
        paint.setAntiAlias(true);
        this.circlePaint.setDither(true);
        this.circlePaint.setColor(getContext().getResources().getColor(R.color.colorAccent));
        this.circlePaint.setStrokeWidth(SystemUtil.dpToPx(getContext(), 2));
        this.circlePaint.setStyle(Paint.Style.STROKE);
        Paint paint2 = new Paint(1);
        this.drawPaint = paint2;
        paint2.setStyle(Paint.Style.STROKE);
        this.drawPaint.setStrokeWidth(this.radius);
        this.drawPaint.setStrokeCap(Paint.Cap.ROUND);
        this.drawPaint.setStrokeJoin(Paint.Join.ROUND);
        setLayerType(1, null);
        Paint paint3 = new Paint();
        this.tempPaint = paint3;
        paint3.setStyle(Paint.Style.FILL);
        this.tempPaint.setColor(-1);
        Paint paint4 = new Paint();
        this.previewPaint = paint4;
        paint4.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        this.tempPreviewBitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
        this.canvasPreview = new Canvas(this.tempPreviewBitmap);
        this.dstRect = new Rect(0, 0, 100, 100);
        Paint paint5 = new Paint(this.drawPaint);
        this.logPaintGray = paint5;
        paint5.setShader(new BitmapShader(BlurActivity.bitmapBlur, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));
        BitmapShader bitmapShader = new BitmapShader(this.splashBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        this.bitmapShader = bitmapShader;
        this.drawPaint.setShader(bitmapShader);
        this.logPaintColor = new Paint(this.drawPaint);
    }

    public void updatePaintBrush() {
        try {
            this.drawPaint.setStrokeWidth(this.radius * resRatio);
        } catch (Exception unused) {
        }
    }

    @Override // android.view.View
    public void onSizeChanged(int i, int i2, int i3, int i4) {
        super.onSizeChanged(i, i2, i3, i4);
        updatePreviewPaint();
    }

    public void changeShaderBitmap() {
        BitmapShader bitmapShader = new BitmapShader(this.splashBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        this.bitmapShader = bitmapShader;
        this.drawPaint.setShader(bitmapShader);
        updatePreviewPaint();
    }

    public void updatePreviewPaint() {
        if (BlurActivity.bitmapClear.getWidth() > BlurActivity.bitmapClear.getHeight()) {
            resRatio = (BlurActivity.displayWidth / BlurActivity.bitmapClear.getWidth()) * this.saveScale;
        } else {
            resRatio = (this.origHeight / BlurActivity.bitmapClear.getHeight()) * this.saveScale;
        }
        this.drawPaint.setStrokeWidth(this.radius * resRatio);
        this.drawPaint.setMaskFilter(new BlurMaskFilter(resRatio * 30.0f, BlurMaskFilter.Blur.NORMAL));
    }

    private void sharedConstructing(Context context2) {
        super.setClickable(true);
        this.context = context2;
        this.mScaleDetector = new ScaleGestureDetector(context2, new ScaleListener());
        Matrix matrix2 = new Matrix();
        this.matrix = matrix2;
        this.m = new float[9];
        setImageMatrix(matrix2);
        setScaleType(ScaleType.MATRIX);
        setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View view, MotionEvent motionEvent) {
                BlurView.this.mScaleDetector.onTouchEvent(motionEvent);
                BlurView.this.pCount2 = motionEvent.getPointerCount();
                BlurView.this.curr = new PointF(motionEvent.getX(), motionEvent.getY() - (((float) BlurActivity.seekBarOffset.getProgress()) * 3.0f));
                BlurView blurView = BlurView.this;
                blurView.x = (blurView.curr.x - BlurView.this.m[2]) / BlurView.this.m[0];
                BlurView blurView2 = BlurView.this;
                blurView2.y = (blurView2.curr.y - BlurView.this.m[5]) / BlurView.this.m[4];
                int action = motionEvent.getAction();
                if (action == 0) {
                    BlurView.this.drawPaint.setStrokeWidth(BlurView.this.radius * BlurView.resRatio);
                    BlurView.this.drawPaint.setMaskFilter(new BlurMaskFilter(BlurView.resRatio * 30.0f, BlurMaskFilter.Blur.NORMAL));
                    BlurView.this.drawPaint.getShader().setLocalMatrix(BlurView.this.matrix);
                    BlurView blurView3 = BlurView.this;
                    blurView3.oldX = 0.0f;
                    blurView3.oldY = 0.0f;
                    blurView3.last.set(BlurView.this.curr);
                    BlurView.this.start.set(BlurView.this.last);
                    if (!(BlurView.this.mode == 1 || BlurView.this.mode == 3)) {
                        BlurView.this.draw = true;
                        BlurActivity.prView.setVisibility(0);
                    }
                    BlurView.this.circlePath.reset();
                    BlurView.this.circlePath.moveTo(BlurView.this.curr.x, BlurView.this.curr.y);
                    BlurView.this.circlePath.addCircle(BlurView.this.curr.x, BlurView.this.curr.y, (BlurView.this.radius * BlurView.resRatio) / 2.0f, Path.Direction.CW);
                    BlurView.this.drawPath.moveTo(BlurView.this.x, BlurView.this.y);
                    BlurView.this.brushPath.moveTo(BlurView.this.curr.x, BlurView.this.curr.y);
                    BlurView.this.showBoxPreview();
                } else if (action == 1) {
                    if (BlurView.this.mode == 1) {
                        BlurView.this.matrix.getValues(BlurView.this.m);
                    }
                    int abs = (int) Math.abs(BlurView.this.curr.y - BlurView.this.start.y);
                    if (((int) Math.abs(BlurView.this.curr.x - BlurView.this.start.x)) < 3 && abs < 3) {
                        BlurView.this.performClick();
                    }
                    if (BlurView.this.draw) {
                        BlurView.this.drawPaint.setStrokeWidth(BlurView.this.radius);
                        BlurView.this.drawPaint.setMaskFilter(new BlurMaskFilter(30.0f, BlurMaskFilter.Blur.NORMAL));
                        BlurView.this.drawPaint.getShader().setLocalMatrix(new Matrix());
                        BlurView.this.canvas.drawPath(BlurView.this.drawPath, BlurView.this.drawPaint);
                    }
                    BlurActivity.prView.setVisibility(4);
                    BlurView.this.circlePath.reset();
                    BlurView.this.drawPath.reset();
                    BlurView.this.brushPath.reset();
                    BlurView.this.draw = false;
                } else if (action != 2) {
                    if (action == 6 && BlurView.this.mode == 2) {
                        BlurView.this.mode = 0;
                    }
                } else if (BlurView.this.mode == 1 || BlurView.this.mode == 3 || !BlurView.this.draw) {
                    if (BlurView.this.pCount1 == 1 && BlurView.this.pCount2 == 1) {
                        BlurView.this.matrix.postTranslate(BlurView.this.curr.x - BlurView.this.last.x, BlurView.this.curr.y - BlurView.this.last.y);
                    }
                    BlurView.this.last.set(BlurView.this.curr.x, BlurView.this.curr.y);
                } else {
                    BlurView.this.circlePath.reset();
                    BlurView.this.circlePath.moveTo(BlurView.this.curr.x, BlurView.this.curr.y);
                    BlurView.this.circlePath.addCircle(BlurView.this.curr.x, BlurView.this.curr.y, (BlurView.this.radius * BlurView.resRatio) / 2.0f, Path.Direction.CW);
                    BlurView.this.drawPath.lineTo(BlurView.this.x, BlurView.this.y);
                    BlurView.this.brushPath.lineTo(BlurView.this.curr.x, BlurView.this.curr.y);
                    BlurView.this.showBoxPreview();
                    double width = (double) BlurActivity.prView.getWidth();
                    Double.isNaN(width);
                    int i = (int) (width * 1.3d);
                    float f = (float) i;
                    if ((BlurView.this.curr.x > f || BlurView.this.curr.y > f || !BlurView.this.prViewDefaultPosition) && BlurView.this.curr.x <= f && BlurView.this.curr.y >= ((float) (BlurView.this.viewHeight - i)) && !BlurView.this.prViewDefaultPosition) {
                        BlurView blurView4 = BlurView.this;
                        blurView4.prViewDefaultPosition = true;
                        TranslateAnimation translateAnimation = new TranslateAnimation(0.0f, 0.0f, 0.0f, (float) (-(blurView4.viewHeight - BlurActivity.prView.getWidth())));
                        translateAnimation.setDuration(500);
                        translateAnimation.setFillAfter(false);
                        translateAnimation.setAnimationListener(new MyAnimationListener());
                        BlurActivity.prView.startAnimation(translateAnimation);
                    } else {
                        BlurView blurView5 = BlurView.this;
                        blurView5.prViewDefaultPosition = true;
                        TranslateAnimation translateAnimation2 = new TranslateAnimation(0.0f, 0.0f, 0.0f, (float) (blurView5.viewHeight - BlurActivity.prView.getWidth()));
                        translateAnimation2.setDuration(500);
                        translateAnimation2.setFillAfter(false);
                        translateAnimation2.setAnimationListener(new MyAnimationListener());
                        BlurActivity.prView.startAnimation(translateAnimation2);
                    }
                }
                BlurView blurView6 = BlurView.this;
                blurView6.pCount1 = blurView6.pCount2;
                BlurView blurView7 = BlurView.this;
                blurView7.setImageMatrix(blurView7.matrix);
                BlurView.this.invalidate();
                return true;
            }
        });
    }
    public void updateRefMetrix() {
        this.matrix.getValues(this.m);
    }

    public void showBoxPreview() {
        buildDrawingCache();
        try {
            Bitmap createBitmap = Bitmap.createBitmap(getDrawingCache());
            this.canvasPreview.drawRect(this.dstRect, this.tempPaint);
            this.canvasPreview.drawBitmap(createBitmap, new Rect(((int) this.curr.x) - 100, ((int) this.curr.y) - 100, ((int) this.curr.x) + 100, ((int) this.curr.y) + 100), this.dstRect, this.previewPaint);
            BlurActivity.prView.setImageBitmap(this.tempPreviewBitmap);
            destroyDrawingCache();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    @Override // android.widget.ImageView, android.view.View
    public void onDraw(Canvas canvas) {
        float[] fArr = new float[9];
        this.matrix.getValues(fArr);
        int i = (int) fArr[2];
        int i2 = (int) fArr[5];
        super.onDraw(canvas);
        float f = this.origHeight;
        float f2 = this.saveScale;
        float f3 = i2;
        float f4 = (f * f2) + f3;
        if (i2 < 0) {
            float f5 = i;
            float f6 = (this.origWidth * f2) + f5;
            float f7 = this.viewHeight;
            if (f4 > f7) {
                f4 = f7;
            }
            canvas.clipRect(f5, 0.0f, f6, f4);
        } else {
            float f8 = i;
            float f9 = (this.origWidth * f2) + f8;
            float f10 = this.viewHeight;
            if (f4 > f10) {
                f4 = f10;
            }
            canvas.clipRect(f8, f3, f9, f4);
        }
        if (this.draw) {
            canvas.drawPath(this.brushPath, this.drawPaint);
            canvas.drawPath(this.circlePath, this.circlePaint);
        }
    }

    public void fixTrans() {
        this.matrix.getValues(this.m);
        float[] fArr = this.m;
        float f = fArr[2];
        float f2 = fArr[5];
        float fixTrans = getFixTrans(f, this.viewWidth, this.origWidth * this.saveScale);
        float fixTrans2 = getFixTrans(f2, this.viewHeight, this.origHeight * this.saveScale);
        if (fixTrans != 0.0f || fixTrans2 != 0.0f) {
            this.matrix.postTranslate(fixTrans, fixTrans2);
        }
        this.matrix.getValues(this.m);
        updatePreviewPaint();
    }

    @Override // android.widget.ImageView, android.view.View
    public void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        if (this.onMeasureCalled) {
            return;
        }
        this.viewWidth = MeasureSpec.getSize(i);
        int size = MeasureSpec.getSize(i2);
        this.viewHeight = size;
        int i3 = this.oldMeasuredHeight;
        int i4 = this.viewWidth;
        if ((i3 == i4 && i3 == size) || i4 == 0 || size == 0) {
            return;
        }
        this.oldMeasuredHeight = size;
        this.oldMeasuredWidth = i4;
        if (this.saveScale == 1.0f) {
            fitScreen();
        }
        this.onMeasureCalled = true;
    }

    public void fitScreen() {
        Drawable drawable = getDrawable();
        if (drawable == null || drawable.getIntrinsicWidth() == 0 || drawable.getIntrinsicHeight() == 0) {
            return;
        }
        float intrinsicWidth = drawable.getIntrinsicWidth();
        float intrinsicHeight = drawable.getIntrinsicHeight();
        float min = Math.min(this.viewWidth / intrinsicWidth, this.viewHeight / intrinsicHeight);
        this.matrix.setScale(min, min);
        float f = (this.viewHeight - (intrinsicHeight * min)) / 2.0f;
        float f2 = (this.viewWidth - (intrinsicWidth * min)) / 2.0f;
        this.matrix.postTranslate(f2, f);
        this.origWidth = this.viewWidth - (f2 * 2.0f);
        this.origHeight = this.viewHeight - (f * 2.0f);
        setImageMatrix(this.matrix);
        this.matrix.getValues(this.m);
        fixTrans();
    }
}
