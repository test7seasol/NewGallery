package com.gallery.photos.editpic.ImageEDITModule.edit.Editor.effects.mesh;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.exifinterface.media.ExifInterface;

import com.gallery.photos.editpic.ImageEDITModule.edit.activities.BodyActivity;
import com.gallery.photos.editpic.ImageEDITModule.edit.utils.CapturePhotoUtils;
import com.gallery.photos.editpic.ImageEDITModule.edit.utils.ScaleImage;
import com.gallery.photos.editpic.ImageEDITModule.edit.utils.StartPointSeekBar;
import com.gallery.photos.editpic.R;

import java.io.FileOutputStream;

/* loaded from: classes.dex */
public class Enhance implements BodyActivity.BackPressed, View.OnClickListener, CapturePhotoUtils.PhotoLoadResponse, View.OnTouchListener, ScaleImage.ScaleAndMoveInterface {
    private float currentCenterX;
    private float currentCenterY;
    private float firstX;
    private float firstY;
    private boolean isEditing;
    private int lastCurrentSize;
    private float lastY;
    private ConstraintLayout.LayoutParams layoutParams;
    private BodyActivity mActivity;
    private ImageView mCancelButton;
    private Canvas mCanvas;
    private ImageView mCircleImage;
    private ConstraintLayout mCircleLayout;
    private Bitmap mCurrentBitmap;
    private int mCurrentSize;
    private ImageView mDoneButton;
    private float[] mFinalPoint;
    private int mIdCurrent;
    private int mIdLast;
    private int mIdRequisite;
    private float[] mIntermediatePoint;
    private int mMaxSize;
    private LinearLayout mMenuEnhance;
    private int mMinSize;
    private int mNumberOfColumns;
    private int mNumberOfVerts;
    private Bitmap mOriginalBitmap;
    private Bitmap mOriginalSquare;
    private ConstraintLayout mParent;
    private ImageView mResizeImage;
    private ScaleImage mScaleImage;
    private StartPointSeekBar mSeekbar;
    private float step;
    private int xLeft;
    private float xMax;
    private float yMax;
    private int yTop;
    private final int ONE_DP = Math.round(Resources.getSystem().getDisplayMetrics().density);
    private float[] matrixValues = new float[9];
    private StartPointSeekBar.OnSeekBarChangeListener seekBarChangeListener = new StartPointSeekBar.OnSeekBarChangeListener() { // from class: com.gallery.photos.editphotovideo.Editor.effects.mesh.Enhance.1
        @Override // com.gallery.photos.editphotovideo.utils.StartPointSeekBar.OnSeekBarChangeListener
        public void onOnSeekBarValueChange(StartPointSeekBar startPointSeekBar, long j) {
            if (Enhance.this.isEditing) {
                Log.e("valueasdf", "122");
                for (int i = 0; i < Enhance.this.mNumberOfVerts; i += 2) {
                    int i2 = i / 2;
                    float f = (i2 % (Enhance.this.mNumberOfColumns + 1)) * Enhance.this.step;
                    float f2 = (i2 / (Enhance.this.mNumberOfColumns + 1)) * Enhance.this.step;
                    float f3 = j / 75.0f;
                    Enhance.this.mFinalPoint[i] = f + (Enhance.this.mIntermediatePoint[i] * f3);
                    int i3 = i + 1;
                    Enhance.this.mFinalPoint[i3] = f2 + (Enhance.this.mIntermediatePoint[i3] * f3);
                }
                Bitmap createBitmap = Bitmap.createBitmap(Enhance.this.mOriginalSquare.getWidth(), Enhance.this.mOriginalSquare.getHeight(), Bitmap.Config.ARGB_8888);
                if (!createBitmap.isMutable()) {
                    createBitmap.recycle();
                    createBitmap = Enhance.this.mOriginalSquare.copy(Bitmap.Config.ARGB_8888, true);
                }
                new Canvas(createBitmap).drawBitmapMesh(Enhance.this.mOriginalSquare, Enhance.this.mNumberOfColumns, Enhance.this.mNumberOfColumns, Enhance.this.mFinalPoint, 0, null, 0, null);
                Enhance.this.mCanvas.drawBitmap(createBitmap, Enhance.this.xLeft, Enhance.this.yTop, (Paint) null);
                createBitmap.recycle();
                Enhance.this.mScaleImage.invalidate();
            }
        }

        @Override // com.gallery.photos.editphotovideo.utils.StartPointSeekBar.OnSeekBarChangeListener
        public void onStartTrackingTouch(StartPointSeekBar startPointSeekBar) {
            Log.e("valueasdf", "123");
            Enhance.this.mResizeImage.setOnTouchListener(null);
            Enhance.this.mCircleLayout.setOnTouchListener(null);
            if (Enhance.this.isEditing) {
                return;
            }
            Enhance.this.mScaleImage.getImageMatrix().getValues(Enhance.this.matrixValues);
            Enhance enhance = Enhance.this;
            enhance.xLeft = (int) (((enhance.mCircleLayout.getTranslationX() - Enhance.this.matrixValues[2]) - Enhance.this.mScaleImage.getPaddingLeft()) / Enhance.this.matrixValues[0]);
            enhance.yTop = (int) (((enhance.mCircleLayout.getTranslationY() - Enhance.this.matrixValues[5]) - Enhance.this.mScaleImage.getPaddingTop()) / Enhance.this.matrixValues[4]);
            int translationX = (int) ((((Enhance.this.mCircleLayout.getTranslationX() + Enhance.this.mCurrentSize) - Enhance.this.matrixValues[2]) - Enhance.this.mScaleImage.getPaddingLeft()) / Enhance.this.matrixValues[0]);
            int translationY = (int) ((((Enhance.this.mCircleLayout.getTranslationY() + Enhance.this.mCurrentSize) - Enhance.this.matrixValues[5]) - Enhance.this.mScaleImage.getPaddingTop()) / Enhance.this.matrixValues[4]);
            if (translationX < 1 || translationY < 1 || Enhance.this.xLeft >= Enhance.this.mOriginalBitmap.getWidth() || Enhance.this.yTop >= Enhance.this.mOriginalBitmap.getHeight()) {
                return;
            }
            Enhance.this.isEditing = true;
            enhance.mOriginalSquare = Bitmap.createBitmap(translationX - Enhance.this.xLeft, translationY - Enhance.this.yTop, Bitmap.Config.ARGB_8888);
            new Canvas(Enhance.this.mOriginalSquare).drawBitmap(Enhance.this.mCurrentBitmap, -Enhance.this.xLeft, -Enhance.this.yTop, (Paint) null);
            enhance.mNumberOfColumns = Math.min((int) ((translationX - Enhance.this.xLeft) / 5.0f), 10);
            enhance.mNumberOfVerts = (Enhance.this.mNumberOfColumns + 1) * (Enhance.this.mNumberOfColumns + 1) * 2;
            enhance.mIntermediatePoint = new float[Enhance.this.mNumberOfVerts];
            enhance.mFinalPoint = new float[Enhance.this.mNumberOfVerts];
            enhance.step = Enhance.this.mOriginalSquare.getWidth() / Enhance.this.mNumberOfColumns;
            float width = Enhance.this.mOriginalSquare.getWidth() / 2.0f;
            float width2 = Enhance.this.mOriginalSquare.getWidth() / 2.0f;
            for (int i = 0; i < Enhance.this.mNumberOfVerts; i += 2) {
                int i2 = i / 2;
                float f = ((i2 % (Enhance.this.mNumberOfColumns + 1)) * Enhance.this.step) - width2;
                float f2 = ((i2 / (Enhance.this.mNumberOfColumns + 1)) * Enhance.this.step) - width2;
                float sqrt = (float) Math.sqrt(Math.pow(f, 2.0d) + Math.pow(f2, 2.0d));
                if (sqrt < width) {
                    float f3 = (width - sqrt) / width;
                    Enhance.this.mIntermediatePoint[i] = f * f3;
                    Enhance.this.mIntermediatePoint[i + 1] = f3 * f2;
                } else {
                    Enhance.this.mIntermediatePoint[i] = 0.0f;
                    Enhance.this.mIntermediatePoint[i + 1] = 0.0f;
                }
            }
        }

        @Override // com.gallery.photos.editphotovideo.utils.StartPointSeekBar.OnSeekBarChangeListener
        public void onStopTrackingTouch(StartPointSeekBar startPointSeekBar) {
            Log.e("valueasdf", "124");
            Enhance.this.mResizeImage.setOnTouchListener(Enhance.this);
            Enhance.this.mCircleLayout.setOnTouchListener(Enhance.this);
            if (Enhance.this.isEditing) {
                return;
            }
            Enhance.this.mSeekbar.setProgress(0.0d);
        }
    };

    public Enhance(Bitmap bitmap, BodyActivity bodyActivity, ScaleImage scaleImage) {
        this.mOriginalBitmap = bitmap;
        this.mActivity = bodyActivity;
        this.mScaleImage = scaleImage;
        onCreate();
    }

    private void onCreate() {
        this.mCancelButton = (ImageView) this.mActivity.findViewById(R.id.image_view_close);
        this.mDoneButton = (ImageView) this.mActivity.findViewById(R.id.image_view_save);
        this.mParent = (ConstraintLayout) this.mActivity.findViewById(R.id.page);
        this.mMenuEnhance = (LinearLayout) this.mActivity.findViewById(R.id.seekbarWithTwoIcon);
        this.mSeekbar = (StartPointSeekBar) this.mActivity.findViewById(R.id.SWTI_seekbar);
        ((ImageView) this.mActivity.findViewById(R.id.imageViewIcon1)).setImageResource(R.drawable.enhance_small);
        ((ImageView) this.mActivity.findViewById(R.id.imageViewIcon2)).setImageResource(R.drawable.enhance_big);
        this.mMaxSize = (int) (Math.min(this.mOriginalBitmap.getHeight(), this.mOriginalBitmap.getWidth()) * this.mScaleImage.getCalculatedMinScale());
        this.xMax = this.mScaleImage.getMeasuredWidth();
        this.yMax = this.mScaleImage.getMeasuredHeight();
        this.mActivity.isBlocked = false;
        createCircle();
        this.mCircleLayout.setOnTouchListener(this);
        this.mResizeImage.setOnTouchListener(this);
        this.mCurrentBitmap = this.mOriginalBitmap.copy(Bitmap.Config.ARGB_8888, true);
        this.mCanvas = new Canvas(this.mCurrentBitmap);
        this.mActivity.ivUndo.setOnClickListener(this);
        this.mActivity.ivRedo.setOnClickListener(this);
        this.mCancelButton.setOnClickListener(this);
        this.mDoneButton.setOnClickListener(this);
        this.mActivity.ivCompare.setOnTouchListener(new View.OnTouchListener() { // from class: com.gallery.photos.editphotovideo.Editor.effects.mesh.Enhance.2
            @Override // android.view.View.OnTouchListener
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int action = motionEvent.getAction();
                if (action == 0) {
                    Enhance.this.mScaleImage.setImageBitmap(Enhance.this.mOriginalBitmap);
                } else if (action == 1 || action == 3) {
                    Enhance.this.mScaleImage.setImageBitmap(Enhance.this.mCurrentBitmap);
                }
                return true;
            }
        });
        ((TextView) this.mActivity.findViewById(R.id.nameOfTool)).setText(this.mActivity.getResources().getString(R.string.enhance));
        this.mSeekbar.setAbsoluteMinMaxValue(-50.0d, 50.0d);
        this.mSeekbar.setProgress(0.0d);
        this.mSeekbar.setOnSeekBarChangeListener(this.seekBarChangeListener);
        this.mMenuEnhance.setVisibility(View.VISIBLE);
        this.mScaleImage.setImageBitmap(this.mCurrentBitmap);
        this.mScaleImage.setOnScaleAndMoveInterface(this);
        this.mActivity.ivSave.setOnClickListener(null);
        this.mActivity.ivBack.setOnClickListener(null);
        this.mActivity.clConfig.setVisibility(View.GONE);
        this.mActivity.findViewById(R.id.menuHome).setVisibility(View.GONE);
        this.mActivity.findViewById(R.id.constraintLayout).setVisibility(View.VISIBLE);
        this.mActivity.sendEvent("Enhance - open");
    }

    private void createCircle() {
        ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(-2, -2);
        ConstraintLayout constraintLayout = new ConstraintLayout(this.mActivity);
        this.mCircleLayout = constraintLayout;
        constraintLayout.setLayoutParams(layoutParams);
        this.mCircleLayout.setBackgroundColor(0);
        Drawable drawable = this.mActivity.getResources().getDrawable(R.drawable.enhance_arrows_button);
        int min = (int) Math.min(drawable.getIntrinsicWidth() * 2.5f, this.mMaxSize);
        this.mMinSize = min;
        int i2 = this.mMaxSize;
        if (min != i2) {
            min = (int) (((float) (min + i2)) * 0.25f);
        }
        this.mCurrentSize = min;
        ImageView imageView = new ImageView(this.mActivity);
        this.mCircleImage = imageView;
        imageView.setImageResource(R.drawable.circle);
        int i = this.mCurrentSize;
        ConstraintLayout.LayoutParams layoutParams2 = new ConstraintLayout.LayoutParams(i, i);
        this.layoutParams = layoutParams2;
        this.mCircleImage.setLayoutParams(layoutParams2);
        this.mCircleImage.setId(R.id.mCircleImage);
        ImageView imageView2 = new ImageView(this.mActivity);
        this.mResizeImage = imageView2;
        imageView2.setImageDrawable(drawable);
        ConstraintLayout.LayoutParams layoutParams3 = new ConstraintLayout.LayoutParams(-2, -2);
        this.layoutParams = layoutParams3;
        layoutParams3.circleAngle = 135.0f;
        this.layoutParams.circleConstraint = this.mCircleImage.getId();
        this.layoutParams.circleRadius = this.mCurrentSize / 2;
        this.mResizeImage.setLayoutParams(this.layoutParams);
        this.mResizeImage.setId(R.id.mResizeImage);
        this.mCircleLayout.addView(this.mCircleImage);
        this.mCircleLayout.addView(this.mResizeImage);
        this.mCircleLayout.setTranslationX((this.mScaleImage.getMeasuredWidth() - this.mCurrentSize) / 2);
        this.mCircleLayout.setTranslationY((this.mScaleImage.getMeasuredHeight() - this.mCurrentSize) / 2);
        this.mParent.addView(this.mCircleLayout, 1);
    }

    private void close(boolean z) {
        for (int i = 0; i <= this.mIdLast; i++) {
            this.mActivity.deleteFile("tool_" + i + ".png");
        }
        this.mIdCurrent = -1;
        if (z) {
            this.mActivity.sendEvent("Enhance - V");
        } else {
            this.mActivity.sendEvent("Tool - X");
            this.mActivity.sendEvent("Enhance - X");
        }
        this.mCurrentBitmap.recycle();
        this.mCircleLayout.removeAllViews();
        this.mParent.removeView(this.mCircleLayout);
        this.layoutParams.reset();
        Bitmap bitmap = this.mOriginalSquare;
        if (bitmap != null && !bitmap.isRecycled()) {
            this.mOriginalSquare.recycle();
        }
        this.mMenuEnhance.setVisibility(View.GONE);
        this.mActivity.ivUndo.setOnClickListener(this.mActivity);
        this.mActivity.ivRedo.setOnClickListener(this.mActivity);
        this.mCircleLayout.setOnTouchListener(null);
        this.mResizeImage.setOnTouchListener(null);
        this.mSeekbar.setOnSeekBarChangeListener(null);
        this.mScaleImage.setOnScaleAndMoveInterface(null);
        this.mCancelButton.setOnClickListener(null);
        this.mDoneButton.setOnClickListener(null);
        this.mActivity.ivSave.setOnClickListener(this.mActivity);
        this.mActivity.ivBack.setOnClickListener(this.mActivity);
        this.mActivity.ivCompare.setOnTouchListener(this.mActivity);
        this.mScaleImage.setImageBitmap(this.mActivity.mCurrentBitmap);
        this.mActivity.clConfig.setVisibility(View.VISIBLE);
        this.mActivity.findViewById(R.id.constraintLayout).setVisibility(View.GONE);
        this.mActivity.findViewById(R.id.menuHome).setVisibility(View.VISIBLE);
    }

    @Override // com.gallery.photos.editphotovideo.activities.BodyActivity.BackPressed
    public void onBackPressed(boolean z) {
        close(z);
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imageViewRedo /* 2131362301 */:
                if (this.mIdRequisite < this.mIdLast) {
                    saveState();
                    int i = this.mIdRequisite;
                    int i2 = i + 1;
                    this.mIdRequisite = i2;
                    CapturePhotoUtils.getBitmapFromDisk(i, i2, "tool_" + this.mIdRequisite + ".png", this, this.mActivity);
                    this.mActivity.sendEvent("Tool - Forward");
                    this.mActivity.sendEvent("Enhance - Forward");
                    break;
                }
                break;
            case R.id.imageViewUndo /* 2131362320 */:
                saveState();
                if (this.mIdRequisite >= 1) {
                    this.mActivity.sendEvent("Tool - Back");
                    this.mActivity.sendEvent("Enhance - Back");
                    int i3 = this.mIdRequisite;
                    if (i3 > 1) {
                        int i4 = i3 - 1;
                        this.mIdRequisite = i4;
                        CapturePhotoUtils.getBitmapFromDisk(i3, i4, "tool_" + this.mIdRequisite + ".png", this, this.mActivity);
                        break;
                    } else {
                        this.mIdRequisite = 0;
                        this.mIdCurrent = 0;
                        this.mCanvas.drawBitmap(this.mOriginalBitmap, 0.0f, 0.0f, (Paint) null);
                        break;
                    }
                }
                break;
            case R.id.image_view_close /* 2131362342 */:
                close(false);
                break;
            case R.id.image_view_save /* 2131362357 */:
                if (this.isEditing) {
                    this.mOriginalSquare.recycle();
                }
                this.mActivity.saveEffect(this.mCurrentBitmap);
                break;
        }
    }

    @Override // com.gallery.photos.editphotovideo.utils.CapturePhotoUtils.PhotoLoadResponse
    public void loadResponse(Bitmap bitmap, int i, int i2) {
        if (bitmap != null) {
            if ((i2 > i && this.mIdCurrent < i2) || (i2 < i && i2 < this.mIdCurrent)) {
                this.mCanvas.drawBitmap(bitmap, 0.0f, 0.0f, (Paint) null);
                this.mIdCurrent = i2;
                this.mIdRequisite = i2;
                this.mScaleImage.invalidate();
            }
            bitmap.recycle();
            return;
        }
        this.mIdRequisite = i;
    }

    private void saveState() {
        if (this.isEditing) {
            this.isEditing = false;
            this.mOriginalSquare.recycle();
            if (this.mSeekbar.getProgress() != 0) {
                this.mSeekbar.setProgress(0.0d);
                int i = this.mIdCurrent + 1;
                this.mIdCurrent = i;
                while (i <= this.mIdLast) {
                    this.mActivity.deleteFile("tool_" + i + ".png");
                    i++;
                }
                int i2 = this.mIdCurrent;
                this.mIdLast = i2;
                this.mIdRequisite = i2;
                final Bitmap copy = this.mCurrentBitmap.copy(Bitmap.Config.ARGB_8888, true);
                final String str = "tool_" + this.mIdCurrent + ".png";
                final Handler handler = new Handler();
                new Thread(new Runnable() { // from class: com.gallery.photos.editphotovideo.Editor.effects.mesh.Enhance.3
                    @Override // java.lang.Runnable
                    public void run() {
                        try {
                            FileOutputStream openFileOutput = Enhance.this.mActivity.openFileOutput(str, 0);
                            copy.compress(Bitmap.CompressFormat.PNG, 100, openFileOutput);
                            openFileOutput.close();
                            if (Enhance.this.mIdCurrent == -1) {
                                Enhance.this.mActivity.deleteFile(str);
                            }
                        } catch (Exception e) {
                            Log.d("My", "Error (save Bitmap): " + e.getMessage());
                        }
                        handler.post(new Runnable() { // from class: com.gallery.photos.editphotovideo.Editor.effects.mesh.Enhance.3.1
                            @Override // java.lang.Runnable
                            public void run() {
                                copy.recycle();
                            }
                        });
                    }
                }).start();
            }
        }
    }

    private void startWorkWithCircle() {
        this.mSeekbar.setEnabled(false);
        saveState();
    }
/*
    @Override // android.view.View.OnTouchListener
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (view.getId() == R.id.mResizeImage) {
            Log.e("statusPositionClick", "1");
            int action = motionEvent.getAction();
            if (action != 0) {
                if (action != 1) {
                    if (action == 2) {
                        float rawX = motionEvent.getRawX();
                        float rawY = motionEvent.getRawY();
                        float degrees = (float) Math.toDegrees(Math.atan2(this.currentCenterY - rawY, rawX - this.currentCenterX));
                        float sqrt = ((float) (rawY - (((float) Math.sqrt(Math.pow(rawX - this.currentCenterX, 2.0d) + Math.pow(rawY - this.currentCenterY, 2.0d))) * Math.sin(Math.toRadians(degrees - 135.0f))))) - this.lastY;
                        int i = this.mCurrentSize;
                        if (sqrt < 0.0f) {
                            int max = Math.max((int) (this.lastCurrentSize + (sqrt * 2.0f)), this.mMinSize);
                            this.mCurrentSize = max;
                            int i2 = (i - max) / 2;
                            ConstraintLayout constraintLayout = this.mCircleLayout;
                            float f = i2;
                            constraintLayout.setTranslationX(constraintLayout.getTranslationX() + f);
                            ConstraintLayout constraintLayout2 = this.mCircleLayout;
                            constraintLayout2.setTranslationY(constraintLayout2.getTranslationY() + f);
                        } else {
                            int min = Math.min((int) (this.lastCurrentSize + (sqrt * 2.0f)), this.mMaxSize);
                            this.mCurrentSize = min;
                            float f2 = (min - i) / 2;
                            float max2 = Math.max(this.mCircleLayout.getTranslationX() - f2, 0.0f);
                            float f3 = this.mCurrentSize;
                            float f4 = this.xMax;
                            if (f3 + max2 > f4) {
                                max2 = f4 - f3;
                            }
                            this.mCircleLayout.setTranslationX(max2);
                            float max3 = Math.max(this.mCircleLayout.getTranslationY() - f2, 0.0f);
                            float f5 = this.mCurrentSize;
                            float f6 = this.yMax;
                            if (f5 + max3 > f6) {
                                max3 = f6 - f5;
                            }
                            this.mCircleLayout.setTranslationY(max3);
                        }
                        this.mCircleImage.getLayoutParams().width = this.mCurrentSize;
                        this.mCircleImage.getLayoutParams().height = this.mCurrentSize;
                        this.layoutParams.circleRadius = (this.mCurrentSize / 2) - this.ONE_DP;
                        this.mCircleImage.requestLayout();
                        return true;
                    }
                    if (action != 3) {
                        return true;
                    }
                }
                this.mSeekbar.setEnabled(true);
            } else {
                startWorkWithCircle();
                float rawX2 = motionEvent.getRawX();
                this.lastY = motionEvent.getRawY();
                this.currentCenterX = this.mCircleLayout.getTranslationX() + (this.mCurrentSize * 0.8535534f);
                this.currentCenterY = this.mCircleLayout.getTranslationY() + (this.mCurrentSize * 0.8535534f);
                float degrees2 = (float) Math.toDegrees(Math.atan2(r14 - this.lastY, rawX2 - this.currentCenterX));
                this.lastY = (float) (this.lastY - (((float) Math.sqrt(Math.pow(rawX2 - this.currentCenterX, 2.0d) + Math.pow(this.lastY - this.currentCenterY, 2.0d))) * Math.sin(Math.toRadians(degrees2 - 135.0f))));
                this.lastCurrentSize = this.mCurrentSize;
            }
        } else {
            Log.e("statusPositionClick", ExifInterface.GPS_MEASUREMENT_2D);
            int action2 = motionEvent.getAction();
            if (action2 != 0) {
                if (action2 != 1) {
                    if (action2 == 2) {
                        float rawX3 = motionEvent.getRawX() - this.firstX;
                        if (rawX3 >= 0.0f && this.mCurrentSize + rawX3 <= this.xMax) {
                            this.mCircleLayout.setTranslationX(rawX3);
                        }
                        float rawY2 = motionEvent.getRawY() - this.firstY;
                        if (rawY2 >= 0.0f && this.mCurrentSize + rawY2 <= this.yMax) {
                            this.mCircleLayout.setTranslationY(rawY2);
                        }
                        return true;
                    }
                    if (action2 != 3) {
                        return true;
                    }
                }
                this.mSeekbar.setEnabled(true);
            } else {
                startWorkWithCircle();
                this.firstX = motionEvent.getRawX() - this.mCircleLayout.getTranslationX();
                this.firstY = motionEvent.getRawY() - this.mCircleLayout.getTranslationY();
            }
        }
        return true;
    }*/


    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (view.getId() == R.id.mResizeImage) {
            Log.e("statusPositionClick", "1");
            int action = motionEvent.getAction();
            if (action != 0) {
                if (action != 1) {
                    if (action == 2) {
                        float rawX = motionEvent.getRawX();
                        float rawY = motionEvent.getRawY();
                        float sqrt = ((float) (((double) rawY) - (((double) ((float) Math.sqrt(Math.pow((double) (rawX - this.currentCenterX), 2.0d) + Math.pow((double) (rawY - this.currentCenterY), 2.0d)))) * Math.sin(Math.toRadians((double) (((float) Math.toDegrees(Math.atan2((double) (this.currentCenterY - rawY), (double) (rawX - this.currentCenterX)))) - 135.0f)))))) - this.lastY;
                        int i = this.mCurrentSize;
                        if (sqrt < 0.0f) {
                            int max = Math.max((int) (((float) this.lastCurrentSize) + (sqrt * 2.0f)), this.mMinSize);
                            this.mCurrentSize = max;
                            ConstraintLayout constraintLayout = this.mCircleLayout;
                            float f = (float) ((i - max) / 2);
                            constraintLayout.setTranslationX(constraintLayout.getTranslationX() + f);
                            ConstraintLayout constraintLayout2 = this.mCircleLayout;
                            constraintLayout2.setTranslationY(constraintLayout2.getTranslationY() + f);
                        } else {
                            int min = Math.min((int) (((float) this.lastCurrentSize) + (sqrt * 2.0f)), this.mMaxSize);
                            this.mCurrentSize = min;
                            float f2 = (float) ((min - i) / 2);
                            float max2 = Math.max(this.mCircleLayout.getTranslationX() - f2, 0.0f);
                            float f3 = (float) this.mCurrentSize;
                            float f4 = this.xMax;
                            if (f3 + max2 > f4) {
                                max2 = f4 - f3;
                            }
                            this.mCircleLayout.setTranslationX(max2);
                            float max3 = Math.max(this.mCircleLayout.getTranslationY() - f2, 0.0f);
                            float f5 = (float) this.mCurrentSize;
                            float f6 = this.yMax;
                            if (f5 + max3 > f6) {
                                max3 = f6 - f5;
                            }
                            this.mCircleLayout.setTranslationY(max3);
                        }
                        this.mCircleImage.getLayoutParams().width = this.mCurrentSize;
                        this.mCircleImage.getLayoutParams().height = this.mCurrentSize;
                        this.layoutParams.circleRadius = (this.mCurrentSize / 2) - this.ONE_DP;
                        this.mCircleImage.requestLayout();
                        return true;
                    } else if (action != 3) {
                        return true;
                    }
                }
                this.mSeekbar.setEnabled(true);
            } else {
                startWorkWithCircle();
                float rawX2 = motionEvent.getRawX();
                this.lastY = motionEvent.getRawY();
                this.currentCenterX = this.mCircleLayout.getTranslationX() + (((float) this.mCurrentSize) * 0.8535534f);
                float translationY = this.mCircleLayout.getTranslationY() + (((float) this.mCurrentSize) * 0.8535534f);
                this.currentCenterY = translationY;
                float degrees = (float) Math.toDegrees(Math.atan2((double) (translationY - this.lastY), (double) (rawX2 - this.currentCenterX)));
                this.lastY = (float) (((double) this.lastY) - (((double) ((float) Math.sqrt(Math.pow((double) (rawX2 - this.currentCenterX), 2.0d) + Math.pow((double) (this.lastY - this.currentCenterY), 2.0d)))) * Math.sin(Math.toRadians((double) (degrees - 135.0f)))));
                this.lastCurrentSize = this.mCurrentSize;
            }
        } else {
            Log.e("statusPositionClick", ExifInterface.GPS_MEASUREMENT_2D);
            int action2 = motionEvent.getAction();
            if (action2 != 0) {
                if (action2 != 1) {
                    if (action2 == 2) {
                        float rawX3 = motionEvent.getRawX() - this.firstX;
                        if (rawX3 >= 0.0f && ((float) this.mCurrentSize) + rawX3 <= this.xMax) {
                            this.mCircleLayout.setTranslationX(rawX3);
                        }
                        float rawY2 = motionEvent.getRawY() - this.firstY;
                        if (rawY2 >= 0.0f && ((float) this.mCurrentSize) + rawY2 <= this.yMax) {
                            this.mCircleLayout.setTranslationY(rawY2);
                        }
                        return true;
                    } else if (action2 != 3) {
                        return true;
                    }
                }
                this.mSeekbar.setEnabled(true);
            } else {
                startWorkWithCircle();
                this.firstX = motionEvent.getRawX() - this.mCircleLayout.getTranslationX();
                this.firstY = motionEvent.getRawY() - this.mCircleLayout.getTranslationY();
            }
        }
        return true;
    }

    @Override // com.gallery.photos.editphotovideo.utils.ScaleImage.ScaleAndMoveInterface
    public void move(float f, float f2, float f3, float f4) {
        saveState();
    }
}
