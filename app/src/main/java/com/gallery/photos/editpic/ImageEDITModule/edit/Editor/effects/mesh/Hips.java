package com.gallery.photos.editpic.ImageEDITModule.edit.Editor.effects.mesh;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.gallery.photos.editpic.R;
import com.gallery.photos.editpic.ImageEDITModule.edit.activities.BodyActivity;
import com.gallery.photos.editpic.ImageEDITModule.edit.utils.CapturePhotoUtils;
import com.gallery.photos.editpic.ImageEDITModule.edit.utils.ScaleImage;
import com.gallery.photos.editpic.ImageEDITModule.edit.utils.StartPointSeekBar;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class Hips implements BodyActivity.BackPressed, View.OnClickListener, View.OnTouchListener, ScaleImage.ScaleAndMoveInterface, CapturePhotoUtils.PhotoLoadResponse {
    private int column;
    private float initialHeight;
    private float initialTranslationX;
    private float initialTranslationY;
    private float initialWidth;
    private boolean isEditing;
    private float lastX;
    private float lastY;
    private BodyActivity mActivity;
    private ImageView mBottomImage;
    private ImageView mCancelButton;
    private Canvas mCanvas;
    private ImageView mCenterImage;
    private ConstraintLayout mControlLayout;
    private Bitmap mCurrentBitmap;
    private float[] mCurrentMesh;
    private ImageView mDoneButton;
    private int mHeight;
    private int mIdCurrent;
    private int mIdLast;
    private int mIdRequisite;
    private long mLastProgress;
    private ImageView mLeftImage;
    private float[] mMaxChangeMesh;
    private LinearLayout mMenuHips;
    private int mMinHeight;
    private int mMinWidth;
    private int mNumberOfVerts;
    private Bitmap mOriginalBitmap;
    private Bitmap mOriginalSquare;
    private ConstraintLayout mParent;
    private ImageView mRightImage;
    private ScaleImage mScaleImage;
    private StartPointSeekBar mSeekbar;
    private ImageView mTopImage;
    private int mWidth;
    private int row;
    private int xStart;
    private int yStart;
    private int MAX_ROW = 30;
    private int NUMBER_OF_COLUMN = 10;
    private List<HipsHistory> history = new ArrayList();
    private float[] matrixValues = new float[9];
    private StartPointSeekBar.OnSeekBarChangeListener seekBarChangeListener = new StartPointSeekBar.OnSeekBarChangeListener() { // from class: com.gallery.photos.editphotovideo.Editor.effects.mesh.Hips.1
        @Override // com.gallery.photos.editphotovideo.utils.StartPointSeekBar.OnSeekBarChangeListener
        public void onOnSeekBarValueChange(StartPointSeekBar startPointSeekBar, long j) {
            if (Hips.this.isEditing) {
                long j2 = j - Hips.this.mLastProgress;
                Hips.this.mLastProgress = j;
                for (int i = 0; i < Hips.this.mNumberOfVerts; i += 2) {
                    float[] fArr = Hips.this.mCurrentMesh;
                    fArr[i] = fArr[i] + ((Hips.this.mMaxChangeMesh[i] * j2) / 50.0f);
                }
                Bitmap createBitmap = Bitmap.createBitmap(Hips.this.mOriginalSquare.getWidth(), Hips.this.mOriginalSquare.getHeight(), Bitmap.Config.ARGB_8888);
                if (!createBitmap.isMutable()) {
                    Bitmap copy = createBitmap.copy(Bitmap.Config.ARGB_8888, true);
                    createBitmap.recycle();
                    createBitmap = copy;
                }
                new Canvas(createBitmap).drawBitmapMesh(Hips.this.mOriginalSquare, Hips.this.column, Hips.this.row, Hips.this.mCurrentMesh, 0, null, 0, null);
                Hips.this.mCanvas.drawBitmap(createBitmap, Hips.this.xStart, Hips.this.yStart, (Paint) null);
                createBitmap.recycle();
            }
        }

        public void onStartTrackingTouch(StartPointSeekBar startPointSeekBar) {
            float f;
            float f2;
            Hips.this.mControlLayout.setVisibility(View.INVISIBLE);
            if (!Hips.this.isEditing) {
                long unused = Hips.this.mLastProgress = 0;
                Hips.this.mScaleImage.getImageMatrix().getValues(Hips.this.matrixValues);
                Hips hips = Hips.this;
                int i = 0;
                int unused2 = hips.xStart = Math.round((hips.mControlLayout.getTranslationX() - Hips.this.matrixValues[2]) / Hips.this.matrixValues[0]);
                Hips hips2 = Hips.this;
                int unused3 = hips2.yStart = Math.round((hips2.mControlLayout.getTranslationY() - Hips.this.matrixValues[5]) / Hips.this.matrixValues[4]);
                int round = Math.round(((float) Hips.this.mControlLayout.getWidth()) / Hips.this.matrixValues[0]);
                int round2 = Math.round(((float) Hips.this.mControlLayout.getHeight()) / Hips.this.matrixValues[4]);
                float f3 = 2.0f;
                float f4 = ((float) round) / 2.0f;
                float f5 = ((float) round2) / 2.0f;
                if (Hips.this.xStart < 0) {
                    round += Hips.this.xStart;
                    f = ((float) Hips.this.xStart) + f4;
                    int unused4 = Hips.this.xStart = 0;
                } else {
                    f = f4;
                }
                if (Hips.this.yStart < 0) {
                    f2 = (float) (-Hips.this.yStart);
                    round2 += Hips.this.yStart;
                    int unused5 = Hips.this.yStart = 0;
                } else {
                    f2 = 0.0f;
                }
                int min = Math.min(round, Hips.this.mCurrentBitmap.getWidth() - Hips.this.xStart);
                int min2 = Math.min(round2, Hips.this.mCurrentBitmap.getHeight() - Hips.this.yStart);
                if (min >= 50 && min2 >= 50) {
                    Hips hips3 = Hips.this;
                    Bitmap unused6 = hips3.mOriginalSquare = Bitmap.createBitmap(hips3.mCurrentBitmap, Hips.this.xStart, Hips.this.yStart, min, min2);
                    Hips hips4 = Hips.this;
                    int unused7 = hips4.column = hips4.NUMBER_OF_COLUMN;
                    float access$600 = ((float) min) / ((float) Hips.this.column);
                    Hips hips5 = Hips.this;
                    int unused8 = hips5.row = Math.min(min2 / 10, hips5.MAX_ROW);
                    float access$700 = ((float) min2) / ((float) Hips.this.row);
                    Hips hips6 = Hips.this;
                    int unused9 = hips6.mNumberOfVerts = (hips6.column + 1) * 2 * (Hips.this.row + 1);
                    Hips hips7 = Hips.this;
                    float[] unused10 = hips7.mCurrentMesh = new float[hips7.mNumberOfVerts];
                    Hips hips8 = Hips.this;
                    float[] unused11 = hips8.mMaxChangeMesh = new float[hips8.mNumberOfVerts];
                    while (i < Hips.this.mNumberOfVerts) {
                        int i2 = i / 2;
                        int access$6002 = i2 % (Hips.this.column + 1);
                        float f6 = ((float) access$6002) * access$600;
                        float access$6003 = ((float) (i2 / (Hips.this.column + 1))) * access$700;
                        Hips.this.mCurrentMesh[i] = f6;
                        Hips.this.mCurrentMesh[i + 1] = access$6003;
                        if (!(access$6002 == 0 || access$6002 == Hips.this.column)) {
                            Hips.this.mMaxChangeMesh[i] = ((((float) Math.sin((((double) (access$6003 + f2)) * 3.141592653589793d) / ((double) (f5 * f3)))) * access$600) * (f6 - f)) / f4;
                        }
                        i += 2;
                        f3 = 2.0f;
                    }
                    boolean unused12 = Hips.this.isEditing = true;
                }
            }
        }

        public void onStopTrackingTouch(StartPointSeekBar startPointSeekBar) {
            if (!Hips.this.isEditing) {
                Hips.this.mSeekbar.setProgress(0.0d);
            }
            Hips.this.mControlLayout.setVisibility(View.VISIBLE);
        }
    };

        /*@Override // com.gallery.photos.editphotovideo.utils.StartPointSeekBar.OnSeekBarChangeListener
        public void onStartTrackingTouch(StartPointSeekBar startPointSeekBar) {
            float f;
            float f2;
            Hips.this.mControlLayout.setVisibility(View.INVISIBLE);
            if (Hips.this.isEditing) {
                return;
            }
            Hips.this.mLastProgress = 0L;
            Hips.this.mScaleImage.getImageMatrix().getValues(Hips.this.matrixValues);
            Hips hips = Hips.this;
            int i = 0;
            hips.xStart = Math.round((hips.mControlLayout.getTranslationX() - Hips.this.matrixValues[2]) / Hips.this.matrixValues[0]);
            Hips hips2 = Hips.this;
            hips2.yStart = Math.round((hips2.mControlLayout.getTranslationY() - Hips.this.matrixValues[5]) / Hips.this.matrixValues[4]);
            int round = Math.round(Hips.this.mControlLayout.getWidth() / Hips.this.matrixValues[0]);
            int round2 = Math.round(Hips.this.mControlLayout.getHeight() / Hips.this.matrixValues[4]);
            float f3 = 2.0f;
            float f4 = round / 2.0f;
            float f5 = round2 / 2.0f;
            if (Hips.this.xStart < 0) {
                round += Hips.this.xStart;
                f = Hips.this.xStart + f4;
                Hips.this.xStart = 0;
            } else {
                f = f4;
            }
            if (Hips.this.yStart < 0) {
                f2 = -Hips.this.yStart;
                round2 += Hips.this.yStart;
                Hips.this.yStart = 0;
            } else {
                f2 = 0.0f;
            }
            int min = Math.min(round, Hips.this.mCurrentBitmap.getWidth() - Hips.this.xStart);
            int min2 = Math.min(round2, Hips.this.mCurrentBitmap.getHeight() - Hips.this.yStart);
            if (min < 50 || min2 < 50) {
                return;
            }
            Hips hips3 = Hips.this;
            hips3.mOriginalSquare = Bitmap.createBitmap(hips3.mCurrentBitmap, Hips.this.xStart, Hips.this.yStart, min, min2);
            Hips hips4 = Hips.this;
            hips4.column = hips4.NUMBER_OF_COLUMN;
            float f6 = min / Hips.this.column;
            Hips hips5 = Hips.this;
            hips5.row = Math.min(min2 / 10, hips5.MAX_ROW);
            float f7 = min2 / Hips.this.row;
            Hips hips6 = Hips.this;
            hips6.mNumberOfVerts = (hips6.column + 1) * 2 * (Hips.this.row + 1);
            Hips hips7 = Hips.this;
            hips7.mCurrentMesh = new float[hips7.mNumberOfVerts];
            Hips hips8 = Hips.this;
            hips8.mMaxChangeMesh = new float[hips8.mNumberOfVerts];
            while (i < Hips.this.mNumberOfVerts) {
                int i2 = (i / 2) % (Hips.this.column + 1);
                float f8 = i2 * f6;
                Hips.this.mCurrentMesh[i] = f8;
                Hips.this.mCurrentMesh[i + 1] = (r5 / (Hips.this.column + 1)) * f7;
                if (i2 != 0 && i2 != Hips.this.column) {
                    Hips.this.mMaxChangeMesh[i] = ((((float) Math.sin(((r5 + f2) * 3.141592653589793d) / (f5 * f3))) * f6) * (f8 - f)) / f4;
                }
                i += 2;
                f3 = 2.0f;
            }

            Hips.this.isEditing = true;
        }

        @Override // com.gallery.photos.editphotovideo.utils.StartPointSeekBar.OnSeekBarChangeListener
        public void onStopTrackingTouch(StartPointSeekBar startPointSeekBar) {
            if (!Hips.this.isEditing) {
                Hips.this.mSeekbar.setProgress(0.0d);
            }
            Hips.this.mControlLayout.setVisibility(View.VISIBLE);
        }
    };*/

    public Hips(Bitmap bitmap, BodyActivity bodyActivity, ScaleImage scaleImage) {
        this.mOriginalBitmap = bitmap;
        this.mActivity = bodyActivity;
        this.mScaleImage = scaleImage;
        onCreate();
    }

    private void onCreate() {
        this.mCancelButton = (ImageView) this.mActivity.findViewById(R.id.image_view_close);
        this.mDoneButton = (ImageView) this.mActivity.findViewById(R.id.image_view_save);
        this.mParent = (ConstraintLayout) this.mActivity.findViewById(R.id.page);
        this.mMenuHips = (LinearLayout) this.mActivity.findViewById(R.id.seekbarWithTwoIcon);
        StartPointSeekBar startPointSeekBar = (StartPointSeekBar) this.mActivity.findViewById(R.id.SWTI_seekbar);
        this.mSeekbar = startPointSeekBar;
        startPointSeekBar.setAbsoluteMinMaxValue(-50.0d, 50.0d);
        ((ImageView) this.mActivity.findViewById(R.id.imageViewIcon1)).setImageResource(R.drawable.hips_left_icon);
        ((ImageView) this.mActivity.findViewById(R.id.imageViewIcon2)).setImageResource(R.drawable.hips_right_icon);
        this.mActivity.isBlocked = false;
        createControlLayout();
        this.mCurrentBitmap = this.mOriginalBitmap.copy(Bitmap.Config.ARGB_8888, true);
        this.mCanvas = new Canvas(this.mCurrentBitmap);
        this.mActivity.ivUndo.setOnClickListener(this);
        this.mActivity.ivRedo.setOnClickListener(this);
        this.mCancelButton.setOnClickListener(this);
        this.mDoneButton.setOnClickListener(this);
        this.mActivity.ivCompare.setOnTouchListener(new View.OnTouchListener() { // from class: com.gallery.photos.editphotovideo.Editor.effects.mesh.Hips.2
            @Override // android.view.View.OnTouchListener
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int action = motionEvent.getAction();
                if (action == 0) {
                    Hips.this.mScaleImage.setImageBitmap(Hips.this.mOriginalBitmap);
                } else if (action == 1 || action == 3) {
                    Hips.this.mScaleImage.setImageBitmap(Hips.this.mCurrentBitmap);
                }
                return true;
            }
        });
        ((TextView) this.mActivity.findViewById(R.id.nameOfTool)).setText(this.mActivity.getResources().getString(R.string.hips));
        this.mSeekbar.setProgress(0.0d);
        this.mSeekbar.setOnSeekBarChangeListener(this.seekBarChangeListener);
        this.mMenuHips.setVisibility(View.VISIBLE);
        this.mScaleImage.setImageBitmap(this.mCurrentBitmap);
        this.mScaleImage.setOnScaleAndMoveInterface(this);
        this.mActivity.ivSave.setOnClickListener(null);
        this.mActivity.ivBack.setOnClickListener(null);
        this.mActivity.clConfig.setVisibility(View.GONE);
        this.mActivity.findViewById(R.id.menuHome).setVisibility(View.GONE);
        this.mActivity.findViewById(R.id.constraintLayout).setVisibility(View.VISIBLE);
        this.mActivity.sendEvent("Hips - open");
    }

    private void createControlLayout() {
        this.mControlLayout = new ConstraintLayout(this.mActivity);
        ImageView imageView = new ImageView(this.mActivity);
        this.mTopImage = imageView;
        imageView.setId(R.id.mTopImage);
        this.mTopImage.setImageResource(R.drawable.transform_up);
        ImageView imageView2 = new ImageView(this.mActivity);
        this.mBottomImage = imageView2;
        imageView2.setId(R.id.mBottomImage);
        this.mBottomImage.setImageResource(R.drawable.transform_down);
        ImageView imageView3 = new ImageView(this.mActivity);
        this.mLeftImage = imageView3;
        imageView3.setId(R.id.mLeftImage);
        this.mLeftImage.setImageResource(R.drawable.hips_transform_left);
        ImageView imageView4 = new ImageView(this.mActivity);
        this.mRightImage = imageView4;
        imageView4.setId(R.id.mRightImage);
        this.mRightImage.setImageResource(R.drawable.hips_transform_right);
        ImageView imageView5 = new ImageView(this.mActivity);
        this.mCenterImage = imageView5;
        imageView5.setId(R.id.mCenterImage);
        FrameLayout frameLayout = new FrameLayout(this.mActivity);
        frameLayout.setId(R.id.centerLine);
        frameLayout.setBackgroundResource(R.drawable.transform_line_center);
        FrameLayout frameLayout2 = new FrameLayout(this.mActivity);
        frameLayout2.setBackgroundResource(R.drawable.hips_transform_line);
        FrameLayout frameLayout3 = new FrameLayout(this.mActivity);
        frameLayout3.setId(R.id.lineLeft);
        frameLayout3.setBackgroundResource(R.drawable.hips_transform_left_line);
        FrameLayout frameLayout4 = new FrameLayout(this.mActivity);
        frameLayout4.setId(R.id.lineRight);
        frameLayout4.setBackgroundResource(R.drawable.hips_transform_right_line);
        int intrinsicHeight = this.mTopImage.getDrawable().getIntrinsicHeight();
        this.mMinHeight = intrinsicHeight * 4;
        this.mMinWidth = this.mLeftImage.getDrawable().getIntrinsicWidth() * 4;
        ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(Math.round(this.mMinHeight / 2.5f), Math.round(this.mMinWidth / 2.5f));
        layoutParams.leftToLeft = 0;
        layoutParams.rightToRight = 0;
        layoutParams.topToTop = 0;
        layoutParams.bottomToBottom = 0;
        this.mCenterImage.setLayoutParams(layoutParams);
        ConstraintLayout.LayoutParams layoutParams2 = new ConstraintLayout.LayoutParams(-2, -2);
        layoutParams2.leftToLeft = 0;
        layoutParams2.rightToRight = 0;
        layoutParams2.topToTop = 0;
        this.mTopImage.setLayoutParams(layoutParams2);
        ConstraintLayout.LayoutParams layoutParams3 = new ConstraintLayout.LayoutParams(-2, -2);
        layoutParams3.leftToLeft = 0;
        layoutParams3.rightToRight = 0;
        layoutParams3.bottomToBottom = 0;
        this.mBottomImage.setLayoutParams(layoutParams3);
        ConstraintLayout.LayoutParams layoutParams4 = new ConstraintLayout.LayoutParams(-2, -2);
        layoutParams4.topToTop = 0;
        layoutParams4.bottomToBottom = 0;
        this.mLeftImage.setLayoutParams(layoutParams4);
        ConstraintLayout.LayoutParams layoutParams5 = new ConstraintLayout.LayoutParams(-2, -2);
        layoutParams5.rightToRight = 0;
        layoutParams5.topToTop = 0;
        layoutParams5.bottomToBottom = 0;
        this.mRightImage.setLayoutParams(layoutParams5);
        ConstraintLayout.LayoutParams layoutParams6 = new ConstraintLayout.LayoutParams(-2, 0);
        layoutParams6.leftToLeft = 0;
        layoutParams6.rightToRight = 0;
        layoutParams6.topToTop = 0;
        layoutParams6.bottomToBottom = 0;
        int i = intrinsicHeight / 2;
        layoutParams6.topMargin = i;
        layoutParams6.bottomMargin = i;
        frameLayout.setLayoutParams(layoutParams6);
        ConstraintLayout.LayoutParams layoutParams7 = new ConstraintLayout.LayoutParams(-2, 0);
        layoutParams7.topToTop = frameLayout.getId();
        layoutParams7.bottomToBottom = frameLayout.getId();
        frameLayout3.setLayoutParams(layoutParams7);
        ConstraintLayout.LayoutParams layoutParams8 = new ConstraintLayout.LayoutParams(-2, 0);
        layoutParams8.topToTop = frameLayout.getId();
        layoutParams8.bottomToBottom = frameLayout.getId();
        layoutParams8.rightToRight = 0;
        frameLayout4.setLayoutParams(layoutParams8);
        ConstraintLayout.LayoutParams layoutParams9 = new ConstraintLayout.LayoutParams(0, -2);
        layoutParams9.topToTop = 0;
        layoutParams9.bottomToBottom = 0;
        layoutParams9.leftToLeft = 0;
        layoutParams9.rightToRight = 0;
        frameLayout2.setLayoutParams(layoutParams9);
        this.mControlLayout.addView(frameLayout3);
        this.mControlLayout.addView(frameLayout4);
        this.mControlLayout.addView(frameLayout);
        this.mControlLayout.addView(frameLayout2);
        this.mControlLayout.addView(this.mTopImage);
        this.mControlLayout.addView(this.mRightImage);
        this.mControlLayout.addView(this.mBottomImage);
        this.mControlLayout.addView(this.mLeftImage);
        this.mControlLayout.addView(this.mCenterImage);
        ConstraintLayout.LayoutParams layoutParams10 = new ConstraintLayout.LayoutParams(this.mMinWidth, this.mMinHeight);
        this.mControlLayout.setLayoutParams(layoutParams10);

//        this.mControlLayout.setLayoutParams(new ConstraintLayout.LayoutParams(this.mMinWidth, this.mMinHeight));
        this.mParent.addView(this.mControlLayout, 1);
        this.mWidth = this.mScaleImage.getWidth();
        this.mHeight = this.mScaleImage.getHeight();
        this.mControlLayout.setTranslationX((this.mWidth - layoutParams10.width) / 2.0f);
        this.mControlLayout.setTranslationY((this.mHeight - layoutParams10.height) / 2.0f);
        this.mTopImage.setOnTouchListener(this);
        this.mBottomImage.setOnTouchListener(this);
        this.mLeftImage.setOnTouchListener(this);
        this.mRightImage.setOnTouchListener(this);
        this.mCenterImage.setOnTouchListener(this);
    }

    private void close(boolean z) {
        for (int i = 0; i <= this.mIdLast; i++) {
            this.mActivity.deleteFile("tool_" + i + ".png");
        }
        this.mIdCurrent = -1;
        Bitmap bitmap = this.mOriginalSquare;
        if (bitmap != null && !bitmap.isRecycled()) {
            this.mOriginalSquare.recycle();
        }
        if (z) {
            this.mActivity.sendEvent("Hips - V");
        } else {
            this.mActivity.sendEvent("Tool - X");
            this.mActivity.sendEvent("Hips - X");
        }
        this.mCurrentBitmap.recycle();
        this.mControlLayout.removeAllViews();
        this.mParent.removeView(this.mControlLayout);
        this.history.clear();
        this.mTopImage.setOnTouchListener(null);
        this.mBottomImage.setOnTouchListener(null);
        this.mLeftImage.setOnTouchListener(null);
        this.mRightImage.setOnTouchListener(null);
        this.mCenterImage.setOnTouchListener(null);
        this.mMenuHips.setVisibility(View.GONE);
        this.mActivity.ivUndo.setOnClickListener(this.mActivity);
        this.mActivity.ivRedo.setOnClickListener(this.mActivity);
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
                int i = this.mIdRequisite;
                if (i == this.mIdCurrent && i < this.mIdLast) {
                    this.mActivity.sendEvent("Tool - Forward");
                    this.mActivity.sendEvent("Hips - Forward");
                    if (!this.isEditing) {
                        int i2 = this.mIdRequisite;
                        int i3 = i2 + 1;
                        this.mIdRequisite = i3;
                        CapturePhotoUtils.getBitmapFromDisk(i2, i3, "tool_" + this.mIdRequisite + ".png", this, this.mActivity);
                        break;
                    } else {
                        saveState();
                        break;
                    }
                }
                break;
            case R.id.imageViewUndo /* 2131362320 */:
                saveState();
                int i4 = this.mIdRequisite;
                if (i4 == this.mIdCurrent && i4 > 0) {
                    this.mActivity.sendEvent("Tool - Back");
                    this.mActivity.sendEvent("Hips - Back");
                    int i5 = this.mIdRequisite;
                    int i6 = i5 - 1;
                    this.mIdRequisite = i6;
                    CapturePhotoUtils.getBitmapFromDisk(i5, i6, "tool_" + (this.mIdRequisite + 1) + ".png", this, this.mActivity);
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

    private void saveState() {
        if (this.isEditing) {
            this.isEditing = false;
            if (this.mSeekbar.getProgress() != 0) {
                int i = this.mIdCurrent + 1;
                this.mIdCurrent = i;
                while (i <= this.mIdLast) {
                    this.mActivity.deleteFile("tool_" + i + ".png");
                    List<HipsHistory> list = this.history;
                    list.remove(list.size() - 1);
                    i++;
                }
                int i2 = this.mIdCurrent;
                this.mIdLast = i2;
                this.mIdRequisite = i2;
                final Bitmap copy = this.mOriginalSquare.copy(Bitmap.Config.ARGB_8888, true);
                this.mOriginalSquare.recycle();
                this.history.add(new HipsHistory((float[]) this.mCurrentMesh.clone(), this.xStart, this.yStart, this.column, this.row));
                this.mSeekbar.setProgress(0.0d);
                final String str = "tool_" + this.mIdCurrent + ".png";
                final Handler handler = new Handler();
                new Thread(new Runnable() { // from class: com.gallery.photos.editphotovideo.Editor.effects.mesh.Hips.3
                    @Override // java.lang.Runnable
                    public void run() {
                        try {
                            FileOutputStream openFileOutput = Hips.this.mActivity.openFileOutput(str, 0);
                            copy.compress(Bitmap.CompressFormat.PNG, 100, openFileOutput);
                            openFileOutput.close();
                            if (Hips.this.mIdCurrent == -1) {
                                Hips.this.mActivity.deleteFile(str);
                            }
                        } catch (Exception e) {
                            Log.d("My", "Error (save Bitmap): " + e.getMessage());
                        }
                        handler.post(new Runnable() { // from class: com.gallery.photos.editphotovideo.Editor.effects.mesh.Hips.3.1
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

    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (motionEvent.getAction() == 0) {
            this.lastX = motionEvent.getRawX();
            this.lastY = motionEvent.getRawY();
            this.initialTranslationX = this.mControlLayout.getTranslationX();
            this.initialTranslationY = this.mControlLayout.getTranslationY();
            this.initialWidth = (float) this.mControlLayout.getWidth();
            this.initialHeight = (float) this.mControlLayout.getHeight();
            startWorkWithControl();
            return true;
        } else if (motionEvent.getAction() != 2) {
            if (motionEvent.getAction() == 1 || motionEvent.getAction() == 3) {
                this.mSeekbar.setEnabled(true);
            }
            return true;
        } else {
            switch (view.getId()) {
                case R.id.mBottomImage:
                    int rawY = (int) ((this.initialHeight + motionEvent.getRawY()) - this.lastY);
                    if (rawY >= this.mMinHeight && ((float) rawY) <= ((float) this.mHeight) - this.initialTranslationY) {
                        this.mControlLayout.getLayoutParams().height = rawY;
                        this.mControlLayout.requestLayout();
                        break;
                    }
                case R.id.mCenterImage:
                    float rawX = (this.initialTranslationX + motionEvent.getRawX()) - this.lastX;
                    float rawY2 = (this.initialTranslationY + motionEvent.getRawY()) - this.lastY;
                    if (rawX >= 0.0f && rawX <= ((float) this.mWidth) - this.initialWidth) {
                        this.mControlLayout.setTranslationX(rawX);
                    }
                    if (rawY2 >= 0.0f && rawY2 <= ((float) this.mHeight) - this.initialHeight) {
                        this.mControlLayout.setTranslationY(rawY2);
                        break;
                    }
                case R.id.mLeftImage:
                    float rawX2 = motionEvent.getRawX() - this.lastX;
                    float f = this.initialWidth;
                    int i = (int) (f - rawX2);
                    if (i >= this.mMinWidth && ((float) i) <= f + this.initialTranslationX) {
                        this.mControlLayout.getLayoutParams().width = i;
                        this.mControlLayout.setTranslationX(this.initialTranslationX + rawX2);
                        this.mControlLayout.requestLayout();
                        break;
                    }
                case R.id.mRightImage:
                    int rawX3 = (int) ((this.initialWidth + motionEvent.getRawX()) - this.lastX);
                    if (rawX3 >= this.mMinWidth && ((float) rawX3) <= ((float) this.mWidth) - this.initialTranslationX) {
                        this.mControlLayout.getLayoutParams().width = rawX3;
                        this.mControlLayout.requestLayout();
                        break;
                    }
                case R.id.mTopImage:
                    float rawY3 = motionEvent.getRawY() - this.lastY;
                    float f2 = this.initialHeight;
                    int i2 = (int) (f2 - rawY3);
                    if (i2 >= this.mMinHeight && ((float) i2) <= this.initialTranslationY + f2) {
                        this.mControlLayout.getLayoutParams().height = i2;
                        this.mControlLayout.setTranslationY(this.initialTranslationY + rawY3);
                        this.mControlLayout.requestLayout();
                        break;
                    }
            }
            return true;
        }
    }

    private void startWorkWithControl() {
        this.mSeekbar.setEnabled(false);
        saveState();
    }

    @Override // com.gallery.photos.editphotovideo.utils.ScaleImage.ScaleAndMoveInterface
    public void move(float f, float f2, float f3, float f4) {
        saveState();
    }

    @Override // com.gallery.photos.editphotovideo.utils.CapturePhotoUtils.PhotoLoadResponse
    public void loadResponse(Bitmap bitmap, int i, int i2) {
        if (bitmap != null) {
            if (i2 > i && this.mIdCurrent < i2) {
                Bitmap createBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
                if (!createBitmap.isMutable()) {
                    Bitmap copy = createBitmap.copy(Bitmap.Config.ARGB_8888, true);
                    createBitmap.recycle();
                    createBitmap = copy;
                }
                Canvas canvas = new Canvas(createBitmap);
                HipsHistory hipsHistory = this.history.get(i2 - 1);
                canvas.drawBitmapMesh(bitmap, hipsHistory.column, hipsHistory.row, hipsHistory.currentMesh, 0, null, 0, null);
                this.mCanvas.drawBitmap(createBitmap, hipsHistory.x, hipsHistory.y, (Paint) null);
                createBitmap.recycle();
                this.mIdCurrent = i2;
                this.mIdRequisite = i2;
            } else if (i2 < i && i2 < this.mIdCurrent) {
                this.mCanvas.drawBitmap(bitmap, this.history.get(i2).x, this.history.get(i2).y, (Paint) null);
                this.mIdCurrent = i2;
                this.mIdRequisite = i2;
            }
            this.mScaleImage.invalidate();
            bitmap.recycle();
            return;
        }
        this.mIdRequisite = i;
    }

    public class HipsHistory {
        int column;
        float[] currentMesh;
        int row;
        float x;
        float y;

        HipsHistory(float[] fArr, float f, float f2, int i, int i2) {
            this.currentMesh = fArr;
            this.x = f;
            this.y = f2;
            this.column = i;
            this.row = i2;
        }
    }
}
