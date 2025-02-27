package com.gallery.photos.editpic.ImageEDITModule.edit.Editor.effects.resize;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
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
public class Height implements BodyActivity.BackPressed, View.OnClickListener, CapturePhotoUtils.PhotoLoadResponse, ScaleImage.TouchInterface {
    private boolean isEditing;
    private boolean isToucing;
    private int lastProgress;
    private float lastY;
    private BodyActivity mActivity;
    private ImageView mBlueMask;
    private Bitmap mBottomBitmap;
    private ImageView mBottomImage;
    private int mBottomLine;
    private int mBottomLineCoppy;
    private ImageView mCancelButton;
    private Canvas mCanvas;
    private ConstraintLayout mControlLayout;
    private Bitmap mCurrentBitmap;
    private Bitmap mCurrentMiddle;
    private ImageView mDoneButton;
    private int mHOHA;
    private int mHalfOfHeightArrow;
    private int mHalfOfWidthArrow;
    private int mIdCurrent;
    private int mIdLast;
    private int mIdRequisite;
    private LinearLayout mImageLayout;
    private int mMaxResize;
    private LinearLayout mMenuHeight;
    private Bitmap mMiddleBitmap;
    private ImageView mMiddleImage;
    private Bitmap mOriginalBitmap;
    private ConstraintLayout mParent;
    private Bitmap mPreviewBitmap;
    private ScaleImage mScaleImage;
    private StartPointSeekBar mSeekbar;
    private int mTop;
    private Bitmap mTopBitmap;
    private int mTopCoppy;
    private ImageView mTopImage;
    private int mTopLine;
    private int mTopLineCoppy;
    private float topBorderImage;
    private int touchType;
    private final int ONE_DP = Math.round(Resources.getSystem().getDisplayMetrics().density);
    private List<HeightHistory> mHistory = new ArrayList();
    private StartPointSeekBar.OnSeekBarChangeListener seekBarChangeListener = new StartPointSeekBar.OnSeekBarChangeListener() { // from class: com.gallery.photos.editphotovideo.Editor.effects.resize.Height.1
        @Override // com.gallery.photos.editphotovideo.utils.StartPointSeekBar.OnSeekBarChangeListener
        public void onOnSeekBarValueChange(StartPointSeekBar startPointSeekBar, long j) {
            if (Height.this.isEditing) {
                int i = (int) j;
                int i2 = (Height.this.mMaxResize * i) / 50;
                if ((i2 > 0 || Height.this.mBottomLine + (i2 * 2) < Height.this.mHOHA * 2) && (Height.this.mTop < i2 || i2 <= 0)) {
                    return;
                }
                Height height = Height.this;
                height.mTopCoppy = height.mTop - i2;
                Height height2 = Height.this;
                height2.mTopLineCoppy = height2.mTopLine - i2;
                Height height3 = Height.this;
                height3.mBottomLineCoppy = height3.mBottomLine + (i2 * 2);
                Height.this.mCurrentMiddle.recycle();
                Height.this.lastProgress = i;
                Height.this.mCurrentMiddle = null;
                if (Height.this.mBottomLineCoppy > 0) {
                    Height height4 = Height.this;
                    height4.mCurrentMiddle = Bitmap.createScaledBitmap(height4.mMiddleBitmap, Height.this.mOriginalBitmap.getWidth(), Height.this.mBottomLineCoppy, true);
                }
                Height.this.mMiddleImage.setImageBitmap(Height.this.mCurrentMiddle);
            }
        }

       /* @Override // com.gallery.photos.editphotovideo.utils.StartPointSeekBar.OnSeekBarChangeListener
        public void onStartTrackingTouch(StartPointSeekBar startPointSeekBar) {
            Height.this.mScaleImage.setOnTouchInterface(null);
            if (!Height.this.isEditing) {
                Height.this.isEditing = true;
                Height.this.mMaxResize = Math.round(r9.mBottomLine * 0.1f);
                if (Height.this.mTopLine <= Height.this.mTop) {
                    Height.this.mTopBitmap = null;
                    Height.this.mTopImage.setVisibility(View.GONE);
                } else {
                    Height height = Height.this;
                    height.mTopBitmap = Bitmap.createBitmap(height.mCurrentBitmap, 0, Height.this.mTop, Height.this.mOriginalBitmap.getWidth(), Height.this.mTopLine - Height.this.mTop);
                    Height.this.mTopImage.setVisibility(View.VISIBLE);
                }
                Height height2 = Height.this;
                height2.mMiddleBitmap = Bitmap.createBitmap(height2.mCurrentBitmap, 0, Height.this.mTopLine, Height.this.mOriginalBitmap.getWidth(), Height.this.mBottomLine);
                if (((Height.this.mCurrentBitmap.getHeight() - Height.this.mTopLine) - Height.this.mBottomLine) - Height.this.mTop <= 0) {
                    Height.this.mBottomBitmap = null;
                    Height.this.mBottomImage.setVisibility(View.GONE);
                } else {
                    Height height3 = Height.this;
                    height3.mBottomBitmap = Bitmap.createBitmap(height3.mCurrentBitmap, 0, Height.this.mTopLine + Height.this.mBottomLine, Height.this.mOriginalBitmap.getWidth(), ((Height.this.mCurrentBitmap.getHeight() - Height.this.mTopLine) - Height.this.mBottomLine) - Height.this.mTop);
                    Height.this.mBottomImage.setVisibility(View.VISIBLE);
                }
                Height height4 = Height.this;
                height4.mCurrentMiddle = Bitmap.createBitmap(height4.mCurrentBitmap, 0, Height.this.mTopLine, Height.this.mOriginalBitmap.getWidth(), Height.this.mBottomLine);
                Height.this.mTopImage.setImageBitmap(Height.this.mTopBitmap);
                Height.this.mMiddleImage.setImageBitmap(Height.this.mMiddleBitmap);
                Height.this.mBottomImage.setImageBitmap(Height.this.mBottomBitmap);
                Height height5 = Height.this;
                height5.mTopCoppy = height5.mTop;
                Height height6 = Height.this;
                height6.mBottomLineCoppy = height6.mBottomLine;
                Height height7 = Height.this;
                height7.mTopLineCoppy = height7.mTopLine;
            } else {
                Height.this.changeOriginalsAndCopies();
            }
            Height.this.mCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
            Height.this.mControlLayout.setVisibility(View.INVISIBLE);
            Height.this.mImageLayout.setVisibility(View.VISIBLE);
            Height.this.mImageLayout.requestLayout();
        }*/

        public void onStartTrackingTouch(StartPointSeekBar startPointSeekBar) {
            Height.this.mScaleImage.setOnTouchInterface((ScaleImage.TouchInterface) null);
            if (!Height.this.isEditing) {
                boolean unused = Height.this.isEditing = true;
                Height height = Height.this;
                int unused2 = height.mMaxResize = Math.round(((float) height.mBottomLine) * 0.1f);
                if (Height.this.mTopLine > Height.this.mTop) {
                    Height height2 = Height.this;
                    Bitmap unused3 = height2.mTopBitmap = Bitmap.createBitmap(height2.mCurrentBitmap, 0, Height.this.mTop, Height.this.mOriginalBitmap.getWidth(), Height.this.mTopLine - Height.this.mTop);
                    Height.this.mTopImage.setVisibility(0);
                } else {
                    Bitmap unused4 = Height.this.mTopBitmap = null;
                    Height.this.mTopImage.setVisibility(8);
                }
                Height height3 = Height.this;
                Bitmap unused5 = height3.mMiddleBitmap = Bitmap.createBitmap(height3.mCurrentBitmap, 0, Height.this.mTopLine, Height.this.mOriginalBitmap.getWidth(), Height.this.mBottomLine);
                if (((Height.this.mCurrentBitmap.getHeight() - Height.this.mTopLine) - Height.this.mBottomLine) - Height.this.mTop > 0) {
                    Height height4 = Height.this;
                    Bitmap unused6 = height4.mBottomBitmap = Bitmap.createBitmap(height4.mCurrentBitmap, 0, Height.this.mTopLine + Height.this.mBottomLine, Height.this.mOriginalBitmap.getWidth(), ((Height.this.mCurrentBitmap.getHeight() - Height.this.mTopLine) - Height.this.mBottomLine) - Height.this.mTop);
                    Height.this.mBottomImage.setVisibility(View.VISIBLE);
                } else {
                    Bitmap unused7 = Height.this.mBottomBitmap = null;
                    Height.this.mBottomImage.setVisibility(8);
                }
                Height height5 = Height.this;
                Bitmap unused8 = height5.mCurrentMiddle = Bitmap.createBitmap(height5.mCurrentBitmap, 0, Height.this.mTopLine, Height.this.mOriginalBitmap.getWidth(), Height.this.mBottomLine);
                Height.this.mTopImage.setImageBitmap(Height.this.mTopBitmap);
                Height.this.mMiddleImage.setImageBitmap(Height.this.mMiddleBitmap);
                Height.this.mBottomImage.setImageBitmap(Height.this.mBottomBitmap);
                Height height6 = Height.this;
                int unused9 = height6.mTopCoppy = height6.mTop;
                Height height7 = Height.this;
                int unused10 = height7.mBottomLineCoppy = height7.mBottomLine;
                Height height8 = Height.this;
                int unused11 = height8.mTopLineCoppy = height8.mTopLine;
            } else {
                Height.this.changeOriginalsAndCopies();
            }
            Height.this.mCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
            Height.this.mControlLayout.setVisibility(4);
            Height.this.mImageLayout.setVisibility(0);
            Height.this.mImageLayout.requestLayout();
        }

        @Override // com.gallery.photos.editphotovideo.utils.StartPointSeekBar.OnSeekBarChangeListener
        public void onStopTrackingTouch(StartPointSeekBar startPointSeekBar) {
            Height.this.mScaleImage.setOnTouchInterface(Height.this);
            Height.this.mCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
            if (Height.this.mTopBitmap != null) {
                Height.this.mCanvas.drawBitmap(Height.this.mTopBitmap, 0.0f, Height.this.mTopCoppy, (Paint) null);
            }
            Height.this.mCanvas.drawBitmap(Height.this.mCurrentMiddle, 0.0f, Height.this.mTopLineCoppy, (Paint) null);
            if (Height.this.mBottomBitmap != null) {
                Height.this.mCanvas.drawBitmap(Height.this.mBottomBitmap, 0.0f, Height.this.mTopLineCoppy + Height.this.mBottomLineCoppy, (Paint) null);
            }
            if (Height.this.mTopLineCoppy != Height.this.mTopLine || Height.this.mBottomLineCoppy != Height.this.mBottomLine) {
                Height.this.changeOriginalsAndCopies();
                Height.this.mControlLayout.setTranslationY((Height.this.topBorderImage + (Height.this.mTopLine * Height.this.mScaleImage.getCalculatedMinScale())) - Height.this.mHalfOfHeightArrow);
                Height.this.mBlueMask.getLayoutParams().height = (int) (Height.this.mBottomLine * Height.this.mScaleImage.getCalculatedMinScale());
                Height.this.mBlueMask.requestLayout();
            } else {
                Height.this.isEditing = false;
                Height.this.lastProgress = 0;
                if (Height.this.mTopBitmap != null) {
                    Height.this.mTopBitmap.recycle();
                }
                Height.this.mMiddleBitmap.recycle();
                Height.this.mCurrentMiddle.recycle();
                if (Height.this.mBottomBitmap != null) {
                    Height.this.mBottomBitmap.recycle();
                }
            }
            Height.this.mSeekbar.setProgress(Height.this.lastProgress);
            Height.this.mScaleImage.invalidate();
            Height.this.mControlLayout.setVisibility(View.VISIBLE);
            Height.this.mImageLayout.setVisibility(View.INVISIBLE);
        }
    };

    /* JADX INFO: Access modifiers changed from: private */
    public void changeOriginalsAndCopies() {
        int i = this.mTop;
        this.mTop = this.mTopCoppy;
        this.mTopCoppy = i;
        int i2 = this.mTopLine;
        this.mTopLine = this.mTopLineCoppy;
        this.mTopLineCoppy = i2;
        int i3 = this.mBottomLine;
        this.mBottomLine = this.mBottomLineCoppy;
        this.mBottomLineCoppy = i3;
    }

    public Height(Bitmap bitmap, BodyActivity bodyActivity, ScaleImage scaleImage) {
        this.mOriginalBitmap = bitmap;
        this.mActivity = bodyActivity;
        this.mScaleImage = scaleImage;
        onCreate();
    }

    private void onCreate() {
        this.mCancelButton = (ImageView) this.mActivity.findViewById(R.id.image_view_close);
        this.mDoneButton = (ImageView) this.mActivity.findViewById(R.id.image_view_save);
        this.mParent = (ConstraintLayout) this.mActivity.findViewById(R.id.page);
        this.mMenuHeight = (LinearLayout) this.mActivity.findViewById(R.id.seekbarWithTwoIcon);
        this.mSeekbar = (StartPointSeekBar) this.mActivity.findViewById(R.id.SWTI_seekbar);
        ((ImageView) this.mActivity.findViewById(R.id.imageViewIcon1)).setImageResource(R.drawable.height_left_icon);
        ((ImageView) this.mActivity.findViewById(R.id.imageViewIcon2)).setImageResource(R.drawable.height_right_icon);
        int round = Math.round(this.mOriginalBitmap.getHeight() * 1.1f);
        this.mTop = (round - this.mOriginalBitmap.getHeight()) / 2;
        createControlLayout();
        this.mActivity.isBlocked = false;
        Bitmap createBitmap = Bitmap.createBitmap(this.mOriginalBitmap.getWidth(), round, Bitmap.Config.ARGB_8888);
        this.mCurrentBitmap = createBitmap;
        if (!createBitmap.isMutable()) {
            Bitmap copy = this.mCurrentBitmap.copy(Bitmap.Config.ARGB_8888, true);
            this.mCurrentBitmap.recycle();
            this.mCurrentBitmap = copy;
        }
        Canvas canvas = new Canvas(this.mCurrentBitmap);
        this.mCanvas = canvas;
        canvas.drawBitmap(this.mOriginalBitmap, 0.0f, this.mTop, (Paint) null);
        this.mPreviewBitmap = this.mCurrentBitmap.copy(Bitmap.Config.ARGB_8888, true);
        this.mActivity.ivUndo.setOnClickListener(this);
        this.mActivity.ivRedo.setOnClickListener(this);
        this.mCancelButton.setOnClickListener(this);
        this.mDoneButton.setOnClickListener(this);
        this.mActivity.ivCompare.setOnTouchListener(new View.OnTouchListener() { // from class: com.gallery.photos.editphotovideo.Editor.effects.resize.Height.2
            @Override // android.view.View.OnTouchListener
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int action = motionEvent.getAction();
                if (action == 0) {
                    Height.this.mScaleImage.setImageBitmap(Height.this.mPreviewBitmap);
                    Height.this.mScaleImage.setOnTouchInterface(null);
                    Height.this.mSeekbar.setEnabled(false);
                    Height.this.mControlLayout.setVisibility(View.INVISIBLE);
                } else if (action == 1 || action == 3) {
                    Height.this.mScaleImage.setImageBitmap(Height.this.mCurrentBitmap);
                    Height.this.mScaleImage.setOnTouchInterface(Height.this);
                    Height.this.mSeekbar.setEnabled(true);
                    Height.this.mControlLayout.setVisibility(View.VISIBLE);
                }
                return true;
            }
        });
        ((TextView) this.mActivity.findViewById(R.id.nameOfTool)).setText(this.mActivity.getResources().getString(R.string.height));
        this.mSeekbar.setProgress(0.0d);
        this.lastProgress = 0;
        this.mSeekbar.setOnSeekBarChangeListener(this.seekBarChangeListener);
        this.mMenuHeight.setVisibility(View.VISIBLE);
        this.mScaleImage.setImageBitmap(this.mCurrentBitmap);
        this.mScaleImage.setOnTouchInterface(this);
        this.mActivity.ivSave.setOnClickListener(null);
        this.mActivity.ivBack.setOnClickListener(null);
        this.mActivity.clConfig.setVisibility(View.GONE);
        this.mActivity.findViewById(R.id.menuHome).setVisibility(View.GONE);
        this.mActivity.findViewById(R.id.constraintLayout).setVisibility(View.VISIBLE);
        ScaleImage scaleImage = this.mScaleImage;
        int i = this.mHalfOfWidthArrow;
        scaleImage.setPadding(i, 0, i, 0);
        this.mScaleImage.resetToFitCenterManual();
        float pointXOnScreen = this.mScaleImage.getPointXOnScreen(0.0f);
        this.mBlueMask.getLayoutParams().width = (int) (this.mScaleImage.getPointXOnScreen(this.mOriginalBitmap.getWidth()) - pointXOnScreen);
        int min = Math.min(300, (int) (this.mOriginalBitmap.getHeight() * this.mScaleImage.getCalculatedMinScale()));
        this.mBlueMask.getLayoutParams().height = min;
        this.mHOHA = (int) (this.mHalfOfHeightArrow / this.mScaleImage.getCalculatedMinScale());
        float f = min;
        this.mTopLine = (int) ((this.mCurrentBitmap.getHeight() - (f / this.mScaleImage.getCalculatedMinScale())) / 2.0f);
        this.mBottomLine = (int) (f / this.mScaleImage.getCalculatedMinScale());
        this.topBorderImage = (this.mScaleImage.getHeight() - (this.mCurrentBitmap.getHeight() * this.mScaleImage.getCalculatedMinScale())) / 2.0f;
        this.mControlLayout.setTranslationX(pointXOnScreen);
        this.mControlLayout.setTranslationY((this.topBorderImage + (this.mTopLine * this.mScaleImage.getCalculatedMinScale())) - this.mHalfOfHeightArrow);
        this.mImageLayout.setTranslationX(pointXOnScreen);
        this.mImageLayout.getLayoutParams().width = (int) (this.mScaleImage.getPointXOnScreen(this.mOriginalBitmap.getWidth()) - pointXOnScreen);
        this.mScaleImage.setScaleMode(false, true);
        this.mActivity.sendEvent("Height - open");
    }

    private void createControlLayout() {
        ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(-2, -2);
        ConstraintLayout constraintLayout = new ConstraintLayout(this.mActivity);
        this.mControlLayout = constraintLayout;
        constraintLayout.setLayoutParams(layoutParams);
        this.mControlLayout.setBackgroundColor(0);
        this.mControlLayout.setId(R.id.mControlLayout);
        FrameLayout frameLayout = new FrameLayout(this.mActivity);
        frameLayout.setId(R.id.controlLineTop);
        frameLayout.setBackgroundColor(-1);
        FrameLayout frameLayout2 = new FrameLayout(this.mActivity);
        frameLayout2.setBackgroundColor(-1);
        ImageView imageView = new ImageView(this.mActivity);
        this.mBlueMask = imageView;
        imageView.setImageResource(R.drawable.height_red_mask);
        this.mBlueMask.setId(R.id.redMask);
        this.mBlueMask.setVisibility(View.INVISIBLE);
        ImageView imageView2 = new ImageView(this.mActivity);
        imageView2.setImageResource(R.drawable.height_arrows_button);
        imageView2.setId(R.id.controlIconTop);
        ImageView imageView3 = new ImageView(this.mActivity);
        imageView3.setImageResource(R.drawable.height_arrows_button);
        this.mControlLayout.addView(this.mBlueMask);
        this.mControlLayout.addView(frameLayout);
        this.mControlLayout.addView(frameLayout2);
        this.mControlLayout.addView(imageView2);
        this.mControlLayout.addView(imageView3);
        ConstraintLayout.LayoutParams layoutParams2 = new ConstraintLayout.LayoutParams(0, this.ONE_DP * 2);
        layoutParams2.leftToLeft = 0;
        layoutParams2.topToTop = imageView2.getId();
        layoutParams2.rightToRight = this.mBlueMask.getId();
        layoutParams2.bottomToBottom = imageView2.getId();
        frameLayout.setLayoutParams(layoutParams2);
        ConstraintLayout.LayoutParams layoutParams3 = new ConstraintLayout.LayoutParams(-2, -2);
        layoutParams3.leftToLeft = 0;
        layoutParams3.topToTop = frameLayout.getId();
        layoutParams3.topMargin = this.ONE_DP;
        this.mBlueMask.setLayoutParams(layoutParams3);
        ConstraintLayout.LayoutParams layoutParams4 = new ConstraintLayout.LayoutParams(0, this.ONE_DP * 2);
        layoutParams4.leftToLeft = 0;
        layoutParams4.topToBottom = this.mBlueMask.getId();
        layoutParams4.bottomToBottom = this.mBlueMask.getId();
        layoutParams4.rightToRight = this.mBlueMask.getId();
        frameLayout2.setLayoutParams(layoutParams4);
        ConstraintLayout.LayoutParams layoutParams5 = new ConstraintLayout.LayoutParams(-2, -2);
        layoutParams5.topToTop = 0;
        layoutParams5.rightToRight = this.mBlueMask.getId();
        layoutParams5.leftToRight = this.mBlueMask.getId();
        imageView2.setLayoutParams(layoutParams5);
        ConstraintLayout.LayoutParams layoutParams6 = new ConstraintLayout.LayoutParams(-2, -2);
        layoutParams6.topToBottom = this.mBlueMask.getId();
        layoutParams6.bottomToBottom = this.mBlueMask.getId();
        layoutParams6.rightToRight = this.mBlueMask.getId();
        layoutParams6.leftToRight = this.mBlueMask.getId();
        imageView3.setLayoutParams(layoutParams6);
        this.mHalfOfHeightArrow = imageView2.getDrawable().getIntrinsicHeight() / 2;
        this.mHalfOfWidthArrow = imageView2.getDrawable().getIntrinsicWidth() / 2;
        this.mParent.addView(this.mControlLayout, 1);
        LinearLayout linearLayout = new LinearLayout(this.mActivity);
        this.mImageLayout = linearLayout;
        linearLayout.setLayoutParams(new ConstraintLayout.LayoutParams(0, this.mScaleImage.getHeight()));
        this.mImageLayout.setGravity(16);
        this.mImageLayout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams layoutParams7 = new LinearLayout.LayoutParams(-1, -2);
        ImageView imageView4 = new ImageView(this.mActivity);
        this.mTopImage = imageView4;
        imageView4.setLayoutParams(layoutParams7);
        this.mTopImage.setAdjustViewBounds(true);
        ImageView imageView5 = new ImageView(this.mActivity);
        this.mMiddleImage = imageView5;
        imageView5.setLayoutParams(layoutParams7);
        this.mMiddleImage.setAdjustViewBounds(true);
        ImageView imageView6 = new ImageView(this.mActivity);
        this.mBottomImage = imageView6;
        imageView6.setLayoutParams(layoutParams7);
        this.mBottomImage.setAdjustViewBounds(true);
        this.mImageLayout.addView(this.mTopImage);
        this.mImageLayout.addView(this.mMiddleImage);
        this.mImageLayout.addView(this.mBottomImage);
        this.mParent.addView(this.mImageLayout, 1);
        this.mImageLayout.setVisibility(View.INVISIBLE);
    }

    private void close(boolean z) {
        for (int i = 0; i <= this.mIdLast; i++) {
            this.mActivity.deleteFile("tool_" + i + ".png");
        }
        this.mIdCurrent = -1;
        if (z) {
            this.mActivity.sendEvent("Height - V");
        } else {
            this.mActivity.sendEvent("Tool - X");
            this.mActivity.sendEvent("Height - X");
        }
        this.mCurrentBitmap.recycle();
        this.mPreviewBitmap.recycle();
        if (this.isEditing) {
            Bitmap bitmap = this.mTopBitmap;
            if (bitmap != null) {
                bitmap.recycle();
            }
            this.mMiddleBitmap.recycle();
            this.mCurrentMiddle.recycle();
            Bitmap bitmap2 = this.mBottomBitmap;
            if (bitmap2 != null) {
                bitmap2.recycle();
            }
        }
        this.mHistory.clear();
        this.mControlLayout.removeAllViews();
        this.mParent.removeView(this.mControlLayout);
        this.mImageLayout.removeAllViews();
        this.mParent.removeView(this.mImageLayout);
        this.mPreviewBitmap.recycle();
        this.mScaleImage.setPadding(0, 0, 0, 0);
        this.mScaleImage.resetToFitCenter();
        this.mMenuHeight.setVisibility(View.GONE);
        this.mActivity.ivUndo.setOnClickListener(this.mActivity);
        this.mActivity.ivRedo.setOnClickListener(this.mActivity);
        this.mScaleImage.setScaleMode(true, false);
        this.mSeekbar.setOnSeekBarChangeListener(null);
        this.mCancelButton.setOnClickListener(null);
        this.mScaleImage.setOnTouchInterface(null);
        this.mDoneButton.setOnClickListener(null);
        this.mActivity.ivSave.setOnClickListener(this.mActivity);
        this.mActivity.ivBack.setOnClickListener(this.mActivity);
        this.mActivity.ivCompare.setOnTouchListener(this.mActivity);
        this.mScaleImage.setImageBitmap(this.mOriginalBitmap);
        this.mActivity.clConfig.setVisibility(View.VISIBLE);
        this.mActivity.findViewById(R.id.constraintLayout).setVisibility(View.GONE);
        this.mActivity.findViewById(R.id.menuHome).setVisibility(View.VISIBLE);
    }

    private void save() {
        Bitmap createBitmap = Bitmap.createBitmap(this.mCurrentBitmap, 0, this.mTop, this.mOriginalBitmap.getWidth(), this.mCurrentBitmap.getHeight() - (this.mTop * 2));
        this.mActivity.mCurrentBitmap.recycle();
        if (!createBitmap.isMutable()) {
            this.mActivity.mCurrentBitmap = createBitmap.copy(Bitmap.Config.ARGB_8888, true);
            this.mOriginalBitmap = this.mActivity.mCurrentBitmap;
        } else {
            this.mActivity.mCurrentBitmap = createBitmap;
            this.mOriginalBitmap = createBitmap;
        }
        this.mActivity.addMainState();
    }

    private void redo() {
        HeightHistory heightHistory = this.mHistory.get(this.mIdCurrent);
        Bitmap createBitmap = heightHistory.topMiddle > heightHistory.top ? Bitmap.createBitmap(this.mCurrentBitmap, 0, heightHistory.top, this.mOriginalBitmap.getWidth(), heightHistory.topMiddle - heightHistory.top) : null;
        Bitmap createBitmap2 = Bitmap.createBitmap(this.mCurrentBitmap, 0, heightHistory.topMiddle, this.mOriginalBitmap.getWidth(), heightHistory.height);
        Bitmap createBitmap3 = ((this.mCurrentBitmap.getHeight() - heightHistory.topMiddle) - heightHistory.height) - heightHistory.top > 0 ? Bitmap.createBitmap(this.mCurrentBitmap, 0, heightHistory.topMiddle + heightHistory.height, this.mOriginalBitmap.getWidth(), ((this.mCurrentBitmap.getHeight() - heightHistory.topMiddle) - heightHistory.height) - heightHistory.top) : null;
        Bitmap createScaledBitmap = Bitmap.createScaledBitmap(createBitmap2, this.mOriginalBitmap.getWidth(), heightHistory.height - ((heightHistory.finalTop - heightHistory.top) * 2), true);
        createBitmap2.recycle();
        this.mTop = heightHistory.finalTop;
        this.mTopLine = heightHistory.topMiddle + (heightHistory.finalTop - heightHistory.top);
        this.mBottomLine = heightHistory.height - ((heightHistory.finalTop - heightHistory.top) * 2);
        this.mCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
        if (createBitmap != null) {
            this.mCanvas.drawBitmap(createBitmap, 0.0f, heightHistory.finalTop, (Paint) null);
            createBitmap.recycle();
        }
        this.mCanvas.drawBitmap(createScaledBitmap, 0.0f, this.mTopLine, (Paint) null);
        createScaledBitmap.recycle();
        if (createBitmap3 != null) {
            this.mCanvas.drawBitmap(createBitmap3, 0.0f, this.mTopLine + this.mBottomLine, (Paint) null);
            createBitmap3.recycle();
        }
        this.mScaleImage.invalidate();
        this.mIdCurrent++;
        this.mIdRequisite++;
        this.mControlLayout.setTranslationY((this.topBorderImage + (this.mTopLine * this.mScaleImage.getCalculatedMinScale())) - this.mHalfOfHeightArrow);
        this.mBlueMask.getLayoutParams().height = (int) (this.mBottomLine * this.mScaleImage.getCalculatedMinScale());
        this.mBlueMask.requestLayout();
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imageViewRedo /* 2131362301 */:
                int i = this.mIdRequisite;
                if (i == this.mIdCurrent && i < this.mIdLast) {
                    this.mActivity.sendEvent("Tool - Forward");
                    this.mActivity.sendEvent("Height - Forward");
                    if (!this.isEditing) {
                        redo();
                        break;
                    } else {
                        saveState();
                        break;
                    }
                }
                break;
            case R.id.imageViewUndo /* 2131362320 */:
                saveState();
                int i2 = this.mIdRequisite;
                if (i2 == this.mIdCurrent && i2 > 0) {
                    int i3 = i2 - 1;
                    this.mIdRequisite = i3;
                    CapturePhotoUtils.getBitmapFromDisk(i2, i3, "tool_" + (this.mIdRequisite + 1) + ".png", this, this.mActivity);
                    this.mActivity.sendEvent("Tool - Back");
                    this.mActivity.sendEvent("Height - Back");
                    break;
                }
                break;
            case R.id.image_view_close /* 2131362342 */:
                close(false);
                break;
            case R.id.image_view_save /* 2131362357 */:
                save();
                break;
        }
    }

    @Override // com.gallery.photos.editphotovideo.activities.BodyActivity.BackPressed
    public void onBackPressed(boolean z) {
        close(z);
    }

    private void saveState() {
        if (this.isEditing) {
            this.isEditing = false;
            this.mSeekbar.setProgress(0.0d);
            this.lastProgress = 0;
            int i = this.mIdCurrent + 1;
            this.mIdCurrent = i;
            while (i <= this.mIdLast) {
                this.mActivity.deleteFile("tool_" + i + ".png");
                List<HeightHistory> list = this.mHistory;
                list.remove(list.size() - 1);
                i++;
            }
            int i2 = this.mIdCurrent;
            this.mIdLast = i2;
            this.mIdRequisite = i2;
            this.mHistory.add(new HeightHistory(this.mTopCoppy, this.mBottomLineCoppy, this.mTopLineCoppy, this.mTop));
            final String str = "tool_" + this.mIdCurrent + ".png";
            final Bitmap copy = this.mMiddleBitmap.copy(Bitmap.Config.ARGB_8888, true);
            new Thread(new Runnable() { // from class: com.gallery.photos.editphotovideo.Editor.effects.resize.Height.3
                @Override // java.lang.Runnable
                public void run() {
                    try {
                        FileOutputStream openFileOutput = Height.this.mActivity.openFileOutput(str, 0);
                        copy.compress(Bitmap.CompressFormat.PNG, 100, openFileOutput);
                        copy.recycle();
                        openFileOutput.close();
                        if (Height.this.mIdCurrent == -1) {
                            Height.this.mActivity.deleteFile(str);
                        }
                    } catch (Exception e) {
                        Log.d("My", "Error (save Bitmap): " + e.getMessage());
                    }
                }
            }).start();
            Bitmap bitmap = this.mTopBitmap;
            if (bitmap != null) {
                bitmap.recycle();
            }
            this.mMiddleBitmap.recycle();
            Bitmap bitmap2 = this.mBottomBitmap;
            if (bitmap2 != null) {
                bitmap2.recycle();
            }
            this.mCurrentMiddle.recycle();
        }
    }

    @Override // com.gallery.photos.editphotovideo.utils.CapturePhotoUtils.PhotoLoadResponse
    public void loadResponse(Bitmap bitmap, int i, int i2) {
        if (bitmap != null) {
            HeightHistory heightHistory = this.mHistory.get(this.mIdRequisite);
            Bitmap createBitmap = heightHistory.topMiddle > heightHistory.top ? Bitmap.createBitmap(this.mCurrentBitmap, 0, heightHistory.finalTop, this.mOriginalBitmap.getWidth(), heightHistory.topMiddle - heightHistory.top) : null;
            Bitmap createBitmap2 = ((this.mCurrentBitmap.getHeight() - heightHistory.topMiddle) - heightHistory.height) - heightHistory.top > 0 ? Bitmap.createBitmap(this.mCurrentBitmap, 0, (heightHistory.topMiddle + heightHistory.height) - (heightHistory.finalTop - heightHistory.top), this.mOriginalBitmap.getWidth(), ((this.mCurrentBitmap.getHeight() - heightHistory.topMiddle) - heightHistory.height) - heightHistory.top) : null;
            this.mTop = heightHistory.top;
            this.mTopLine = heightHistory.topMiddle;
            this.mBottomLine = heightHistory.height;
            this.mCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
            if (createBitmap != null) {
                this.mCanvas.drawBitmap(createBitmap, 0.0f, heightHistory.top, (Paint) null);
                createBitmap.recycle();
            }
            this.mCanvas.drawBitmap(bitmap, 0.0f, heightHistory.topMiddle, (Paint) null);
            bitmap.recycle();
            if (createBitmap2 != null) {
                this.mCanvas.drawBitmap(createBitmap2, 0.0f, heightHistory.topMiddle + heightHistory.height, (Paint) null);
                createBitmap2.recycle();
            }
            this.mScaleImage.invalidate();
            this.mIdCurrent = i2;
            this.mIdRequisite = i2;
            this.mControlLayout.setTranslationY((this.topBorderImage + (this.mTopLine * this.mScaleImage.getCalculatedMinScale())) - this.mHalfOfHeightArrow);
            this.mBlueMask.getLayoutParams().height = (int) (this.mBottomLine * this.mScaleImage.getCalculatedMinScale());
            this.mBlueMask.requestLayout();
            return;
        }
        this.mIdRequisite = i;
    }

    @Override // com.gallery.photos.editphotovideo.utils.ScaleImage.TouchInterface
    public void touch(int i, float f, float f2, float f3) {
        if (i == 0) {
            this.isToucing = true;
            this.mSeekbar.setEnabled(false);
            int i2 = this.mTopLine;
            int i3 = this.mHOHA;
            if (f2 >= i2 - i3 && f2 <= i2 + this.mBottomLine + i3) {
                this.mBlueMask.setVisibility(View.VISIBLE);
                saveState();
            }
            int i4 = this.mTopLine;
            int i5 = this.mHOHA;
            if (f2 >= i4 - i5 && f2 <= i4 + i5) {
                this.touchType = 0;
            } else if (f2 <= i4 + i5 || f2 >= (this.mBottomLine + i4) - i5) {
                int i6 = i4 + this.mBottomLine;
                if (f2 < i6 - i5 || f2 > i6 + i5) {
                    this.touchType = -1;
                } else {
                    this.touchType = 2;
                }
            } else {
                this.touchType = 1;
            }
            this.lastY = f2;
            return;
        }
        if (i != 1) {
            if (i == 2) {
                this.isToucing = false;
                this.mSeekbar.setEnabled(true);
                this.mBlueMask.setVisibility(View.INVISIBLE);
                return;
            }
            return;
        }
        if (this.isToucing) {
            int i7 = this.touchType;
            if (i7 == 0) {
                float f4 = this.lastY;
                float f5 = f4 - f2;
                if (f5 < 0.0f) {
                    int i8 = this.mBottomLine;
                    float f6 = i8;
                    if ((f4 + f6) - f2 >= this.mHOHA * 2) {
                        int i9 = (int) (f6 + f5);
                        this.mBottomLine = i9;
                        this.mTopLine += i8 - i9;
                        this.mBlueMask.getLayoutParams().height = (int) (this.mBottomLine * this.mScaleImage.getCalculatedMinScale());
                        this.mControlLayout.setTranslationY((this.topBorderImage + (this.mTopLine * this.mScaleImage.getCalculatedMinScale())) - this.mHalfOfHeightArrow);
                    } else {
                        this.touchType = 2;
                    }
                } else {
                    int i10 = this.mBottomLine;
                    int min = Math.min((this.mTopLine + i10) - this.mTop, (int) ((i10 + f4) - f2));
                    this.mBottomLine = min;
                    this.mTopLine -= min - i10;
                    this.mBlueMask.getLayoutParams().height = (int) (this.mBottomLine * this.mScaleImage.getCalculatedMinScale());
                    this.mControlLayout.setTranslationY((this.topBorderImage + (this.mTopLine * this.mScaleImage.getCalculatedMinScale())) - this.mHalfOfHeightArrow);
                }
                this.mBlueMask.requestLayout();
            } else if (i7 == 1) {
                float f7 = this.lastY;
                if (f7 - f2 > 0.0f) {
                    this.mTopLine = (int) Math.max(this.mTop, (this.mTopLine + f2) - f7);
                } else {
                    this.mTopLine = (int) Math.min((this.mCurrentBitmap.getHeight() - this.mTop) - this.mBottomLine, (this.mTopLine + f2) - this.lastY);
                }
                this.mControlLayout.setTranslationY((this.topBorderImage + (this.mTopLine * this.mScaleImage.getCalculatedMinScale())) - this.mHalfOfHeightArrow);
            } else if (i7 == 2) {
                float f8 = this.lastY;
                float f9 = f8 - f2;
                if (f9 > 0.0f) {
                    float f10 = this.mBottomLine;
                    if ((f10 - f8) + f2 >= this.mHOHA * 2) {
                        this.mBottomLine = (int) (f10 - f9);
                        this.mBlueMask.getLayoutParams().height = (int) (this.mBottomLine * this.mScaleImage.getCalculatedMinScale());
                    } else {
                        this.touchType = 0;
                    }
                } else {
                    this.mBottomLine = Math.min((this.mCurrentBitmap.getHeight() - this.mTop) - this.mTopLine, (int) ((this.mBottomLine + f2) - this.lastY));
                    this.mBlueMask.getLayoutParams().height = (int) (this.mBottomLine * this.mScaleImage.getCalculatedMinScale());
                }
                this.mBlueMask.requestLayout();
            }
            this.lastY = f2;
        }
    }

    public class HeightHistory {
        int finalTop;
        int height;
        int top;
        int topMiddle;

        HeightHistory(int i, int i2, int i3, int i4) {
            this.top = i;
            this.finalTop = i4;
            this.height = i2;
            this.topMiddle = i3;
        }
    }
}
