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
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.gallery.photos.editpic.ImageEDITModule.edit.activities.SplashActivity;
import com.gallery.photos.editpic.ImageEDITModule.edit.utils.FilePath;
import com.gallery.photos.editpic.ImageEDITModule.edit.utils.SystemUtil;
import com.gallery.photos.editpic.R;

import java.util.ArrayList;

/* loaded from: classes.dex */
public class SplashView extends ImageView {
    public static float resRatio;
    BitmapShader bitmapShader;
    Path brushPath;
    public Canvas canvas;
    Canvas canvasPreview;
    Paint circlePaint;
    Path circlePath;
    public int coloring;
    Context context;
    PointF curr;
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
    ArrayList<PointF> pathPoints;
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
            if (SplashView.this.prViewDefaultPosition) {
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(SplashActivity.prView.getWidth(), SplashActivity.prView.getHeight());
                layoutParams.setMargins(0, 0, 0, 0);
                SplashActivity.prView.setLayoutParams(layoutParams);
            } else {
                RelativeLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams(SplashActivity.prView.getWidth(), SplashActivity.prView.getHeight());
                layoutParams2.setMargins(0, SplashView.this.viewHeight - SplashActivity.prView.getWidth(), 0, 0);
                SplashActivity.prView.setLayoutParams(layoutParams2);
            }
        }
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        private ScaleListener() {
        }

        @Override // android.view.ScaleGestureDetector.SimpleOnScaleGestureListener, android.view.ScaleGestureDetector.OnScaleGestureListener
        public boolean onScaleBegin(ScaleGestureDetector scaleGestureDetector) {
            SplashActivity.prView.setVisibility(View.INVISIBLE);
            if (SplashView.this.mode == 1 || SplashView.this.mode == 3) {
                SplashView.this.mode = 3;
            } else {
                SplashView.this.mode = 2;
            }
            SplashView.this.draw = false;
            return true;
        }

        @Override // android.view.ScaleGestureDetector.SimpleOnScaleGestureListener, android.view.ScaleGestureDetector.OnScaleGestureListener
        public boolean onScale(ScaleGestureDetector scaleGestureDetector) {
            float scaleFactor = scaleGestureDetector.getScaleFactor();
            float f = SplashView.this.saveScale;
            SplashView.this.saveScale *= scaleFactor;
            if (SplashView.this.saveScale > SplashView.this.maxScale) {
                SplashView splashView = SplashView.this;
                splashView.saveScale = splashView.maxScale;
                scaleFactor = SplashView.this.maxScale / f;
            } else {
                float f2 = SplashView.this.saveScale;
                float f3 = SplashView.this.minScale;
            }
            if (SplashView.this.origWidth * SplashView.this.saveScale <= SplashView.this.viewWidth || SplashView.this.origHeight * SplashView.this.saveScale <= SplashView.this.viewHeight) {
                SplashView.this.matrix.postScale(scaleFactor, scaleFactor, SplashView.this.viewWidth / 2, SplashView.this.viewHeight / 2);
            } else {
                SplashView.this.matrix.postScale(scaleFactor, scaleFactor, scaleGestureDetector.getFocusX(), scaleGestureDetector.getFocusY());
            }
            SplashView.this.matrix.getValues(SplashView.this.m);
            return true;
        }

        @Override // android.view.ScaleGestureDetector.SimpleOnScaleGestureListener, android.view.ScaleGestureDetector.OnScaleGestureListener
        public void onScaleEnd(ScaleGestureDetector scaleGestureDetector) {
            SplashView.this.radius = (SplashActivity.seekBarSize.getProgress() + 10) / SplashView.this.saveScale;
            SplashView.this.updatePreviewPaint();
        }
    }

    public SplashView(Context context) {
        super(context);
        this.coloring = -1;
        this.curr = new PointF();
        this.draw = false;
        this.last = new PointF();
        this.maxScale = 5.0f;
        this.minScale = 1.0f;
        this.mode = 0;
        this.oldX = 0.0f;
        this.oldY = 0.0f;
        this.onMeasureCalled = false;
        this.opacity = 240;
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

    public SplashView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.coloring = -1;
        this.curr = new PointF();
        this.draw = false;
        this.last = new PointF();
        this.maxScale = 5.0f;
        this.minScale = 1.0f;
        this.mode = 0;
        this.oldX = 0.0f;
        this.oldY = 0.0f;
        this.onMeasureCalled = false;
        this.opacity = 240;
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
        this.splashBitmap = SplashActivity.colorBitmap;
        Bitmap createBitmap = Bitmap.createBitmap(SplashActivity.grayBitmap);
        this.drawingBitmap = createBitmap;
        setImageBitmap(createBitmap);
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
        paint5.setShader(new BitmapShader(SplashActivity.grayBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));
        BitmapShader bitmapShader = new BitmapShader(this.splashBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        this.bitmapShader = bitmapShader;
        this.drawPaint.setShader(bitmapShader);
        this.logPaintColor = new Paint(this.drawPaint);
    }

    public void updatePaintBrush() {
        try {
            this.drawPaint.setStrokeWidth(this.radius * resRatio);
            this.drawPaint.setAlpha(this.opacity);
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
        try {
        if (SplashActivity.colorBitmap.getWidth() > SplashActivity.colorBitmap.getHeight()) {
            resRatio = (SplashActivity.displayWidth / SplashActivity.colorBitmap.getWidth()) * this.saveScale;
        } else {
            resRatio = (this.origHeight / SplashActivity.colorBitmap.getHeight()) * this.saveScale;
        }
        this.drawPaint.setStrokeWidth(this.radius * resRatio);
        this.drawPaint.setMaskFilter(new BlurMaskFilter(resRatio * 15.0f, BlurMaskFilter.Blur.NORMAL));
        this.drawPaint.getShader().setLocalMatrix(this.matrix);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
                SplashView.this.mScaleDetector.onTouchEvent(motionEvent);
                SplashView.this.pCount2 = motionEvent.getPointerCount();
                SplashView.this.curr = new PointF(motionEvent.getX(), motionEvent.getY() - (((float) SplashActivity.seekBarOffset.getProgress()) * 3.0f));
                SplashView splashView = SplashView.this;
                splashView.x = (splashView.curr.x - SplashView.this.m[2]) / SplashView.this.m[0];
                SplashView splashView2 = SplashView.this;
                splashView2.y = (splashView2.curr.y - SplashView.this.m[5]) / SplashView.this.m[4];
                int action = motionEvent.getAction();
                if (action != 6) {
                    if (action == 0) {
                        SplashView.this.drawPaint.setStrokeWidth(SplashView.this.radius * SplashView.resRatio);
                        SplashView.this.drawPaint.setMaskFilter(new BlurMaskFilter(SplashView.resRatio * 15.0f, BlurMaskFilter.Blur.NORMAL));
                        SplashView.this.drawPaint.getShader().setLocalMatrix(SplashView.this.matrix);
                        SplashView splashView3 = SplashView.this;
                        splashView3.oldX = 0.0f;
                        splashView3.oldY = 0.0f;
                        splashView3.last.set(SplashView.this.curr);
                        SplashView.this.start.set(SplashView.this.last);
                        if (!(SplashView.this.mode == 1 || SplashView.this.mode == 3)) {
                            SplashView.this.draw = true;
                            SplashActivity.prView.setVisibility(0);
                        }
                        SplashView.this.circlePath.reset();
                        SplashView.this.circlePath.moveTo(SplashView.this.curr.x, SplashView.this.curr.y);
                        SplashView.this.circlePath.addCircle(SplashView.this.curr.x, SplashView.this.curr.y, (SplashView.this.radius * SplashView.resRatio) / 2.0f, Path.Direction.CW);
                        SplashView.this.pathPoints = new ArrayList<>();
                        SplashView.this.pathPoints.add(new PointF(SplashView.this.x, SplashView.this.y));
                        SplashView.this.drawPath.moveTo(SplashView.this.x, SplashView.this.y);
                        SplashView.this.brushPath.moveTo(SplashView.this.curr.x, SplashView.this.curr.y);
                    } else if (action == 1) {
                        if (SplashView.this.mode == 1) {
                            SplashView.this.matrix.getValues(SplashView.this.m);
                        }
                        int abs = (int) Math.abs(SplashView.this.curr.y - SplashView.this.start.y);
                        if (((int) Math.abs(SplashView.this.curr.x - SplashView.this.start.x)) < 3 && abs < 3) {
                            SplashView.this.performClick();
                        }
                        if (SplashView.this.draw) {
                            SplashView.this.drawPaint.setStrokeWidth(SplashView.this.radius);
                            SplashView.this.drawPaint.setMaskFilter(new BlurMaskFilter(15.0f, BlurMaskFilter.Blur.NORMAL));
                            SplashView.this.drawPaint.getShader().setLocalMatrix(new Matrix());
                            SplashView.this.canvas.drawPath(SplashView.this.drawPath, SplashView.this.drawPaint);
                        }
                        SplashActivity.prView.setVisibility(4);
                        SplashActivity.vector.add(new FilePath(SplashView.this.pathPoints, SplashView.this.coloring, SplashView.this.radius));
                        SplashView.this.circlePath.reset();
                        SplashView.this.drawPath.reset();
                        SplashView.this.brushPath.reset();
                        SplashView.this.draw = false;
                    } else if (action == 2) {
                        if (SplashView.this.mode == 1 || SplashView.this.mode == 3 || !SplashView.this.draw) {
                            if (SplashView.this.pCount1 == 1 && SplashView.this.pCount2 == 1) {
                                SplashView.this.matrix.postTranslate(SplashView.this.curr.x - SplashView.this.last.x, SplashView.this.curr.y - SplashView.this.last.y);
                            }
                            SplashView.this.last.set(SplashView.this.curr.x, SplashView.this.curr.y);
                        } else {
                            SplashView.this.circlePath.reset();
                            SplashView.this.circlePath.moveTo(SplashView.this.curr.x, SplashView.this.curr.y);
                            SplashView.this.circlePath.addCircle(SplashView.this.curr.x, SplashView.this.curr.y, (SplashView.this.radius * SplashView.resRatio) / 2.0f, Path.Direction.CW);
                            SplashView.this.pathPoints.add(new PointF(SplashView.this.x, SplashView.this.y));
                            SplashView.this.drawPath.lineTo(SplashView.this.x, SplashView.this.y);
                            SplashView.this.brushPath.lineTo(SplashView.this.curr.x, SplashView.this.curr.y);
                            SplashView.this.showBoxPreview();
                            double width = (double) SplashActivity.prView.getWidth();
                            Double.isNaN(width);
                            Double.isNaN(width);
                            int i = (int) (width * 1.3d);
                            float f = (float) i;
                            if ((SplashView.this.curr.x > f || SplashView.this.curr.y > f || !SplashView.this.prViewDefaultPosition) && SplashView.this.curr.x <= f && SplashView.this.curr.y >= ((float) (SplashView.this.viewHeight - i)) && !SplashView.this.prViewDefaultPosition) {
                                SplashView splashView4 = SplashView.this;
                                splashView4.prViewDefaultPosition = true;
                                TranslateAnimation translateAnimation = new TranslateAnimation(0.0f, 0.0f, 0.0f, (float) (-(splashView4.viewHeight - SplashActivity.prView.getWidth())));
                                translateAnimation.setDuration(500);
                                translateAnimation.setFillAfter(false);
                                translateAnimation.setAnimationListener(new MyAnimationListener());
                                SplashActivity.prView.startAnimation(translateAnimation);
                            } else {
                                SplashView splashView5 = SplashView.this;
                                splashView5.prViewDefaultPosition = true;
                                TranslateAnimation translateAnimation2 = new TranslateAnimation(0.0f, 0.0f, 0.0f, (float) (splashView5.viewHeight - SplashActivity.prView.getWidth()));
                                translateAnimation2.setDuration(500);
                                translateAnimation2.setFillAfter(false);
                                translateAnimation2.setAnimationListener(new MyAnimationListener());
                                SplashActivity.prView.startAnimation(translateAnimation2);
                            }
                        }
                    }
                } else if (SplashView.this.mode == 2) {
                    SplashView.this.mode = 0;
                }
                SplashView splashView6 = SplashView.this;
                splashView6.pCount1 = splashView6.pCount2;
                SplashView splashView7 = SplashView.this;
                splashView7.setImageMatrix(splashView7.matrix);
                SplashView.this.invalidate();
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
            SplashActivity.prView.setImageBitmap(this.tempPreviewBitmap);
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
        float f = i2;
        float f2 = this.origHeight;
        float f3 = this.saveScale;
        float f4 = (f2 * f3) + f;
        if (i2 < 0) {
            float f5 = i;
            float f6 = (this.origWidth * f3) + f5;
            float f7 = this.viewHeight;
            if (f4 > f7) {
                f4 = f7;
            }
            canvas.clipRect(f5, 0.0f, f6, f4);
        } else {
            float f8 = i;
            float f9 = (this.origWidth * f3) + f8;
            float f10 = this.viewHeight;
            if (f4 > f10) {
                f4 = f10;
            }
            canvas.clipRect(f8, f, f9, f4);
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

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        try {
            int width = MeasureSpec.getSize(widthMeasureSpec);
            int height = MeasureSpec.getSize(heightMeasureSpec);

            // Always set default dimensions first
            setMeasuredDimension(width, height);

            // Skip if dimensions are invalid
            if (width <= 0 || height <= 0) {
                return;
            }

            if (this.onMeasureCalled) {
                return;
            }

            Log.wtf("OnMeasured Call :", "OnMeasured Call");
            this.viewWidth = width;
            this.viewHeight = height;
            int i3 = this.oldMeasuredHeight;
            int i4 = this.viewWidth;

            if ((i3 == i4 && i3 == height) || i4 == 0 || height == 0) {
                return;
            }

            this.oldMeasuredHeight = height;
            this.oldMeasuredWidth = i4;
            if (this.saveScale == 1.0f) {
                fitScreen();
            }
            this.onMeasureCalled = true;

        } catch (Exception e) {
            e.printStackTrace();
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    public void fitScreen() {
        try {
            Drawable drawable = getDrawable();
            if (drawable == null || drawable.getIntrinsicWidth() == 0 || drawable.getIntrinsicHeight() == 0) {
                return;
            }

            // Ensure valid dimensions
            if (this.viewWidth <= 0 || this.viewHeight <= 0) {
                return;
            }


        if (drawable == null || drawable.getIntrinsicWidth() == 0 || drawable.getIntrinsicHeight() == 0) {
            return;
        }
        int intrinsicWidth = drawable.getIntrinsicWidth();
        int intrinsicHeight = drawable.getIntrinsicHeight();
        Log.d("bmSize", "bmWidth: " + intrinsicWidth + " bmHeight : " + intrinsicHeight);
        float f = (float) intrinsicWidth;
        float f2 = (float) intrinsicHeight;
        float min = Math.min(((float) this.viewWidth) / f, ((float) this.viewHeight) / f2);
        this.matrix.setScale(min, min);
        float f3 = (this.viewHeight - (f2 * min)) / 2.0f;
        float f4 = (this.viewWidth - (min * f)) / 2.0f;
        this.matrix.postTranslate(f4, f3);
        this.origWidth = this.viewWidth - (f4 * 2.0f);
        this.origHeight = this.viewHeight - (f3 * 2.0f);
        setImageMatrix(this.matrix);
        this.matrix.getValues(this.m);
        fixTrans();
            // Rest of your existing fitScreen logic...

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
