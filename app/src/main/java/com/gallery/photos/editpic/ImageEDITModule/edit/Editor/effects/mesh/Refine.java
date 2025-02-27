package com.gallery.photos.editpic.ImageEDITModule.edit.Editor.effects.mesh;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.gallery.photos.editpic.R;
import com.gallery.photos.editpic.ImageEDITModule.edit.activities.BodyActivity;
import com.gallery.photos.editpic.ImageEDITModule.edit.utils.ScaleImage;
import com.gallery.photos.editpic.ImageEDITModule.edit.utils.StartPointSeekBar;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class Refine implements BodyActivity.BackPressed, View.OnClickListener, ScaleImage.TouchInterface {
    private boolean isTouching;
    private float lastx;
    private float lasty;
    private BodyActivity mActivity;
    private ImageView mBottomUtils;
    private ImageView mCancelButton;
    private Canvas mCanvas;
    private int mCircleRadius;
    private int mColums;
    private Bitmap mCurrentBitmap;
    private ImageView mDoneButton;
    private Paint mInterruptPaint;
    private Bitmap mLastBitmap;
    private StartPointSeekBar mMenuRefine;
    private Bitmap mOriginalBitmap;
    private Paint mPaint;
    private int mRow;
    private ScaleImage mScaleImage;
    private int maxSize;
    private LinearLayout seekbarWithTwo;
    private float stepX;
    private float stepY;
    private float[] verts;
    private int mCurrentState = -1;
    private List<RefineHistory> mRefineHistory = new ArrayList();

    public Refine(Bitmap bitmap, BodyActivity bodyActivity, ScaleImage scaleImage) {
        this.mOriginalBitmap = bitmap;
        this.mActivity = bodyActivity;
        this.mScaleImage = scaleImage;
        bodyActivity.mLoading.setVisibility(View.VISIBLE);
        final Handler handler = new Handler();
        new Thread(new Runnable() { // from class: com.gallery.photos.editphotovideo.Editor.effects.mesh.Refine.1
            @Override // java.lang.Runnable
            public void run() {
                Refine.this.createMesh();
                handler.post(new Runnable() { // from class: com.gallery.photos.editphotovideo.Editor.effects.mesh.Refine.1.1
                    @Override // java.lang.Runnable
                    public void run() {
                        Refine.this.mActivity.mLoading.setVisibility(View.GONE);
                        Refine.this.mActivity.isBlocked = false;
                        Refine.this.onCreate();
                    }
                });
            }
        }).start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    @SuppressLint("WrongConstant")
    public void onCreate() {
        this.mBottomUtils = (ImageView) this.mActivity.findViewById(R.id.imageViewCompare);
        this.mCancelButton = (ImageView) this.mActivity.findViewById(R.id.image_view_close);
        this.mDoneButton = (ImageView) this.mActivity.findViewById(R.id.image_view_save);
        this.mMenuRefine = (StartPointSeekBar) this.mActivity.findViewById(R.id.menuRefine);
        this.seekbarWithTwo = (LinearLayout) this.mActivity.findViewById(R.id.seekbarWithTwo);
        Paint paint = new Paint();
        this.mPaint = paint;
        paint.setStrokeWidth(3.0f);
        this.mPaint.setStyle(Paint.Style.STROKE);
        this.mPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        this.mPaint.setColor(-1);
        Paint paint2 = new Paint();
        this.mInterruptPaint = paint2;
        paint2.setStrokeWidth(3.0f);
        this.mInterruptPaint.setStyle(Paint.Style.STROKE);
        this.mInterruptPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        this.mInterruptPaint.setColor(-1);
        this.mInterruptPaint.setPathEffect(new DashPathEffect(new float[]{15.0f, 10.0f}, 0.0f));
        this.mCurrentBitmap = this.mOriginalBitmap.copy(Bitmap.Config.ARGB_8888, true);
        Bitmap createBitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
        this.mLastBitmap = createBitmap;
        createBitmap.recycle();
        this.mCanvas = new Canvas(this.mCurrentBitmap);
        ((TextView) this.mActivity.findViewById(R.id.nameOfTool)).setText(this.mActivity.getResources().getString(R.string.refine));
        this.mScaleImage.setOnTouchInterface(this);
        this.mActivity.ivUndo.setOnClickListener(this);
        this.mActivity.ivRedo.setOnClickListener(this);
        this.mCancelButton.setOnClickListener(this);
        this.mDoneButton.setOnClickListener(this);
        this.mActivity.ivCompare.setOnTouchListener(new View.OnTouchListener() { // from class: com.gallery.photos.editphotovideo.Editor.effects.mesh.Refine.2
            @Override // android.view.View.OnTouchListener
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int action = motionEvent.getAction();
                if (action == 0) {
                    Refine.this.mScaleImage.setImageBitmap(Refine.this.mOriginalBitmap);
                } else if (action == 1 || action == 3) {
                    Refine.this.mScaleImage.setImageBitmap(Refine.this.mCurrentBitmap);
                }
                return true;
            }
        });
        this.mMenuRefine.setProgress(50.0d);
        this.mMenuRefine.setOnSeekBarChangeListener(new StartPointSeekBar.OnSeekBarChangeListener() { // from class: com.gallery.photos.editphotovideo.Editor.effects.mesh.Refine.3
            @Override // com.gallery.photos.editphotovideo.utils.StartPointSeekBar.OnSeekBarChangeListener
            public void onOnSeekBarValueChange(StartPointSeekBar startPointSeekBar, long j) {
                Refine.this.mCanvas.drawBitmap(Refine.this.mLastBitmap, 0.0f, 0.0f, (Paint) null);
                Refine refine = Refine.this;
                refine.mCircleRadius = (int) (refine.stepX * 3.0f * ((j / 50.0f) + 1.0f));
                Refine.this.mCanvas.drawCircle(Refine.this.mOriginalBitmap.getWidth() / 2, Refine.this.mOriginalBitmap.getHeight() / 2, Refine.this.mCircleRadius, Refine.this.mPaint);
            }

            @Override // com.gallery.photos.editphotovideo.utils.StartPointSeekBar.OnSeekBarChangeListener
            public void onStartTrackingTouch(StartPointSeekBar startPointSeekBar) {
                if (Refine.this.mLastBitmap.isRecycled()) {
                    Refine refine = Refine.this;
                    refine.mLastBitmap = refine.mCurrentBitmap.copy(Bitmap.Config.ARGB_8888, true);
                }
                Refine.this.mScaleImage.setOnTouchInterface(null);
            }

            @Override // com.gallery.photos.editphotovideo.utils.StartPointSeekBar.OnSeekBarChangeListener
            public void onStopTrackingTouch(StartPointSeekBar startPointSeekBar) {
                Refine.this.mCanvas.drawBitmap(Refine.this.mLastBitmap, 0.0f, 0.0f, (Paint) null);
                if (!Refine.this.isTouching) {
                    Refine.this.mLastBitmap.recycle();
                    Refine.this.mBottomUtils.setVisibility(View.VISIBLE);
                }
                Refine.this.mScaleImage.invalidate();
                Refine.this.mScaleImage.setOnTouchInterface(Refine.this);
            }
        });
        this.seekbarWithTwo.setVisibility(View.VISIBLE);
        this.mScaleImage.setImageBitmap(this.mCurrentBitmap);
        this.mActivity.ivSave.setOnClickListener(null);
        this.mActivity.ivBack.setOnClickListener(null);
        this.mActivity.clConfig.setVisibility(View.GONE);
        this.mActivity.findViewById(R.id.menuHome).setVisibility(View.GONE);
        this.mActivity.findViewById(R.id.constraintLayout).setVisibility(View.VISIBLE);
        this.mActivity.sendEvent("Refine - open");
    }

    private void close(boolean z) {
        this.mCurrentBitmap.recycle();
        this.mRefineHistory.clear();
        if (z) {
            this.mActivity.sendEvent("Refine - V");
        } else {
            this.mActivity.sendEvent("Tool - X");
            this.mActivity.sendEvent("Refine - X");
        }
        this.mScaleImage.setOnTouchInterface(null);
        this.mMenuRefine.setOnSeekBarChangeListener(null);
        this.mActivity.ivUndo.setOnClickListener(this.mActivity);
        this.mActivity.ivRedo.setOnClickListener(this.mActivity);
        this.mCancelButton.setOnClickListener(null);
        this.mDoneButton.setOnClickListener(null);
        this.mActivity.ivSave.setOnClickListener(this.mActivity);
        this.mActivity.ivBack.setOnClickListener(this.mActivity);
        this.mActivity.ivCompare.setOnTouchListener(this.mActivity);
        this.mScaleImage.setImageBitmap(this.mActivity.mCurrentBitmap);
        this.mActivity.clConfig.setVisibility(View.VISIBLE);
        this.mActivity.findViewById(R.id.constraintLayout).setVisibility(View.GONE);
        this.mActivity.findViewById(R.id.menuHome).setVisibility(View.VISIBLE);
        this.seekbarWithTwo.setVisibility(View.GONE);
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imageViewRedo /* 2131362301 */:
                if (this.mCurrentState + 1 < this.mRefineHistory.size()) {
                    int i = this.mCurrentState + 1;
                    this.mCurrentState = i;
                    RefineHistory refineHistory = this.mRefineHistory.get(i);
                    changeMesh(refineHistory.xLeft, refineHistory.yTop, refineHistory.xRight, refineHistory.yBottom, refineHistory.values, 1);
                    this.mActivity.sendEvent("Tool - Forward");
                    this.mActivity.sendEvent("Refine - Forward");
                    break;
                }
                break;
            case R.id.imageViewUndo /* 2131362320 */:
                if (this.mCurrentState > -1) {
                    this.mActivity.sendEvent("Tool - Back");
                    this.mActivity.sendEvent("Refine - Back");
                    RefineHistory refineHistory2 = this.mRefineHistory.get(this.mCurrentState);
                    changeMesh(refineHistory2.xLeft, refineHistory2.yTop, refineHistory2.xRight, refineHistory2.yBottom, refineHistory2.values, -1);
                    this.mCurrentState--;
                    break;
                }
                break;
            case R.id.image_view_close /* 2131362342 */:
                close(false);
                break;
            case R.id.image_view_save /* 2131362357 */:
                this.mActivity.saveEffect(this.mCurrentBitmap);
                break;
        }
    }

    @Override // com.gallery.photos.editphotovideo.activities.BodyActivity.BackPressed
    public void onBackPressed(boolean z) {
        close(z);
    }

    @Override // com.gallery.photos.editphotovideo.utils.ScaleImage.TouchInterface
    public void touch(int i, float f, float f2, float f3) {
        if (i == 0) {
            this.isTouching = true;
            this.lastx = f;
            this.lasty = f2;
            this.mLastBitmap = this.mCurrentBitmap.copy(Bitmap.Config.ARGB_8888, true);
            this.mCanvas.drawCircle(f, f2, this.mCircleRadius, this.mPaint);
            this.mScaleImage.invalidate();
            this.mBottomUtils.setVisibility(View.INVISIBLE);
            return;
        }
        if (i == 1) {
            if (this.isTouching) {
                this.mCanvas.drawBitmap(this.mLastBitmap, 0.0f, 0.0f, (Paint) null);
                this.mCanvas.drawCircle(this.lastx, this.lasty, this.mCircleRadius, this.mPaint);
                this.mCanvas.drawCircle(f, f2, this.mCircleRadius, this.mPaint);
                this.mCanvas.drawLine(this.lastx, this.lasty, f, f2, this.mInterruptPaint);
                this.mScaleImage.invalidate();
                return;
            }
            return;
        }
        if (i == 2) {
            this.mBottomUtils.setVisibility(View.VISIBLE);
            if (!this.mLastBitmap.isRecycled()) {
                this.mCanvas.drawBitmap(this.mLastBitmap, 0.0f, 0.0f, (Paint) null);
                this.mLastBitmap.recycle();
            }
            if (this.isTouching && f != -1.0f) {
                float degrees = (float) Math.toDegrees(Math.atan2(this.lasty - f2, f - this.lastx));
                float sqrt = ((float) Math.sqrt(Math.pow(this.lasty - f2, 2.0d) + Math.pow(this.lastx - f, 2.0d))) / this.maxSize;
                double d = degrees;
                float cos = this.stepX * sqrt * ((float) Math.cos(Math.toRadians(d)));
                float sin = (-sqrt) * this.stepY * ((float) Math.sin(Math.toRadians(d)));
                int max = Math.max((int) ((this.lastx - this.mCircleRadius) / this.stepX), 0);
                int min = Math.min(((int) ((this.lastx + this.mCircleRadius) / this.stepX)) + 1, this.mColums);
                int max2 = Math.max((int) ((this.lasty - this.mCircleRadius) / this.stepY), 0);
                int min2 = Math.min(((int) ((this.lasty + this.mCircleRadius) / this.stepY)) + 1, this.mRow);
                if (min - max <= 0 || min2 - max2 <= 0) {
                    this.isTouching = false;
                    return;
                }
                this.mCurrentState++;
                while (this.mRefineHistory.size() > this.mCurrentState) {
                    List<RefineHistory> list = this.mRefineHistory;
                    list.remove(list.size() - 1);
                }
                changeMesh(max, max2, min, min2, cos, sin);
            }
            this.isTouching = false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void createMesh() {
        if (this.mOriginalBitmap.getWidth() > this.mOriginalBitmap.getHeight()) {
            this.mColums = 100;
            this.stepX = this.mOriginalBitmap.getWidth() / this.mColums;
            this.mRow = (int) (this.mOriginalBitmap.getHeight() / this.stepX);
            this.stepY = this.mOriginalBitmap.getHeight() / this.mRow;
        } else {
            this.mRow = 100;
            this.stepY = this.mOriginalBitmap.getHeight() / this.mRow;
            this.mColums = (int) (this.mOriginalBitmap.getWidth() / this.stepY);
            this.stepX = this.mOriginalBitmap.getWidth() / this.mColums;
        }
        this.mCircleRadius = (int) (this.stepX * 6.0f);
        this.maxSize = Math.max(this.mOriginalBitmap.getHeight(), this.mOriginalBitmap.getWidth()) / 2;
        int i = (this.mColums + 1) * (this.mRow + 1) * 2;
        this.verts = new float[i];
        for (int i2 = 0; i2 < i; i2 += 2) {
            float[] fArr = this.verts;
            int i3 = i2 / 2;
            int i4 = this.mColums + 1;
            fArr[i2] = (i3 % i4) * this.stepX;
            fArr[i2 + 1] = (i3 / i4) * this.stepY;
        }
    }

    private void changeMesh(int i, int i2, int i3, int i4, float[][][] fArr, int i5) {
        for (int i6 = i2; i6 <= i4; i6++) {
            for (int i7 = i; i7 <= i3; i7++) {
                int i8 = (((this.mColums + 1) * i6) + i7) * 2;
                float[] fArr2 = this.verts;
                float f = i5;
                float f2 = fArr2[i8];
                float[] fArr3 = fArr[i6 - i2][i7 - i];
                fArr2[i8] = f2 + (fArr3[0] * f);
                int i9 = i8 + 1;
                fArr2[i9] = fArr2[i9] + (fArr3[1] * f);
            }
        }
        this.mCanvas.drawBitmapMesh(this.mOriginalBitmap, this.mColums, this.mRow, this.verts, 0, null, 0, null);
        this.mScaleImage.invalidate();
    }

    private void changeMesh(int i, int i2, int i3, int i4, float f, float f2) {
        float[][][] fArr = (float[][][]) Array.newInstance((Class<?>) Float.TYPE, (i4 - i2) + 1, (i3 - i) + 1, 2);
        for (int i5 = i2; i5 <= i4; i5++) {
            for (int i6 = i; i6 <= i3; i6++) {
                int i7 = (((this.mColums + 1) * i5) + i6) * 2;
                float[] fArr2 = this.verts;
                float f3 = fArr2[i7];
                int i8 = i7 + 1;
                float f4 = fArr2[i8];
                float abs = Math.abs(this.lastx - f3);
                float abs2 = Math.abs(this.lasty - f4);
                float sqrt = (float) Math.sqrt((abs * abs) + (abs2 * abs2));
                float f5 = this.mCircleRadius;
                if (sqrt < f5) {
                    float f6 = (f5 - sqrt) / f5;
                    if (i6 == 0 || i6 == this.mColums) {
                        float[] fArr3 = this.verts;
                        float f7 = f6 * f2;
                        fArr3[i8] = fArr3[i8] + f7;
                        fArr[i5 - i2][i6 - i][1] = f7;
                    } else if (i5 == 0 || i5 == this.mRow) {
                        float[] fArr4 = this.verts;
                        float f8 = f6 * f;
                        fArr4[i7] = fArr4[i7] + f8;
                        fArr[i5 - i2][i6 - i][0] = f8;
                    } else {
                        float[] fArr5 = this.verts;
                        float f9 = f * f6;
                        fArr5[i7] = fArr5[i7] + f9;
                        float f10 = f2 * f6;
                        fArr5[i8] = fArr5[i8] + f10;
                        float[] fArr6 = fArr[i5 - i2][i6 - i];
                        fArr6[0] = f9;
                        fArr6[1] = f10;
                    }
                }
            }
        }
        this.mRefineHistory.add(new RefineHistory(i, i2, i3, i4, fArr));
        this.mCanvas.drawBitmapMesh(this.mOriginalBitmap, this.mColums, this.mRow, this.verts, 0, null, 0, null);
        this.mScaleImage.invalidate();
    }

    public class RefineHistory {
        float[][][] values;
        int xLeft;
        int xRight;
        int yBottom;
        int yTop;

        RefineHistory(int i, int i2, int i3, int i4, float[][][] fArr) {
            this.values = fArr;
            this.xLeft = i;
            this.xRight = i3;
            this.yBottom = i4;
            this.yTop = i2;
        }
    }
}
