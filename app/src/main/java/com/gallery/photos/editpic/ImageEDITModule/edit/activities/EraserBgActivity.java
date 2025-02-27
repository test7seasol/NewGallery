package com.gallery.photos.editpic.ImageEDITModule.edit.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.gallery.photos.editpic.ImageEDITModule.edit.eraser.ConstantsApp;
import com.gallery.photos.editpic.ImageEDITModule.edit.eraser.eraser.EraseView;
import com.gallery.photos.editpic.ImageEDITModule.edit.eraser.eraser.MultiTouchListener;
import com.gallery.photos.editpic.ImageEDITModule.edit.support.Constants;
import com.gallery.photos.editpic.ImageEDITModule.edit.utils.ImageUtils;
import com.gallery.photos.editpic.R;

/* loaded from: classes.dex */
public class EraserBgActivity extends AppCompatActivity implements View.OnClickListener {
    private static int IMAGE_GALLERY_REQUEST = 20;
    public static Bitmap b = null;
    public static Bitmap bgCircleBit = null;
    public static Bitmap bitmap = null;
    public static int curBgType = 1;
    public static int orgBitHeight;
    public static int orgBitWidth;
    public static BitmapShader patternBMPshader;

    public Animation animSlideDown;
    public Animation animSlideUp;
    public ImageView back_btn;
    public ImageView dv1;
    public EraseView eraseView;
    public int height;
    private ImageView imageViewAuto;
    public ImageView imageViewBackgroundCover;
    ImageView imageViewCutInSide;
    ImageView imageViewCutOutSide;
    private ImageView imageViewEraser;
    private ImageView imageViewLasso;
    private ImageView imageViewRestore;
    private ImageView imageViewZoom;
    private LinearLayout lay_lasso_cut;
    private LinearLayout linearLayoutAuto;
    private LinearLayout linearLayoutEraser;
    public RelativeLayout main_rel;
    private String openFrom;
    public Bitmap orgBitmap;
    private SeekBar radius_seekbar;
    ImageView redo_btn;
    public RelativeLayout relativeLayoutSeekBar;
    RelativeLayout relative_layout_loading;
    public ImageView save_btn;
    public Animation scale_anim;
    private SeekBar seekBarBrushOffset;
    private SeekBar seekBarExtractOffset;
    private SeekBar seekBarOffset;
    private SeekBar seekBarThreshold;
    private TextView textViewAuto;
    private TextView textViewEraser;
    private TextView textViewLasso;
    private TextView textViewRestore;
    private TextView textViewZoom;
    ImageView undo_btn;
    public int width;
    public boolean isTutOpen = true;
    public boolean showDialog = false;


    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        requestWindowFeature(1);
        getWindow().setFlags(1024, 1024);
        setContentView(R.layout.activity_eraser_bg);
        this.openFrom = getIntent().getStringExtra(Constants.KEY_OPEN_FROM);

        this.animSlideUp = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_slide_up);
        this.animSlideDown = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_slide_down);
        this.scale_anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_scale_anim);
        initUI();
        this.isTutOpen = false;
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int i = displayMetrics.heightPixels;
        this.width = displayMetrics.widthPixels;
        this.height = i - ImageUtils.dpToPx((Context) this, 120.0f);
        curBgType = 1;
        this.main_rel.postDelayed(new Runnable() { // from class: com.gallery.photos.editphotovideo.activities.EraserBgActivity.1
            @Override // java.lang.Runnable
            public void run() {
                if (EraserBgActivity.this.isTutOpen) {
                    ImageView imageView = EraserBgActivity.this.imageViewBackgroundCover;
                    EraserBgActivity eraserBgActivity = EraserBgActivity.this;
                    imageView.setImageBitmap(ImageUtils.getTiledBitmap(eraserBgActivity, R.drawable.tbg3, eraserBgActivity.width, EraserBgActivity.this.height));
                    EraserBgActivity.bgCircleBit = ImageUtils.getBgCircleBit(EraserBgActivity.this, R.drawable.tbg3);
                } else {
                    ImageView imageView2 = EraserBgActivity.this.imageViewBackgroundCover;
                    EraserBgActivity eraserBgActivity2 = EraserBgActivity.this;
                    imageView2.setImageBitmap(ImageUtils.getTiledBitmap(eraserBgActivity2, R.drawable.tbg2, eraserBgActivity2.width, EraserBgActivity.this.height));
                    EraserBgActivity.bgCircleBit = ImageUtils.getBgCircleBit(EraserBgActivity.this, R.drawable.tbg2);
                }
                EraserBgActivity.this.importImageFromUri();
            }
        }, 1000L);
    }

    private void initUI() {
        this.relativeLayoutSeekBar = (RelativeLayout) findViewById(R.id.relativeLayoutSeekBar);
        this.textViewEraser = (TextView) findViewById(R.id.textViewEraser);
        this.textViewAuto = (TextView) findViewById(R.id.textViewAuto);
        this.textViewLasso = (TextView) findViewById(R.id.textViewLasso);
        this.textViewRestore = (TextView) findViewById(R.id.textViewRestore);
        this.textViewZoom = (TextView) findViewById(R.id.textViewZoom);
        this.imageViewEraser = (ImageView) findViewById(R.id.imageViewEraser);
        this.imageViewAuto = (ImageView) findViewById(R.id.imageViewAuto);
        this.imageViewLasso = (ImageView) findViewById(R.id.imageViewLasso);
        this.imageViewRestore = (ImageView) findViewById(R.id.imageViewRestore);
        this.imageViewZoom = (ImageView) findViewById(R.id.imageViewZoom);
        this.relative_layout_loading = (RelativeLayout) findViewById(R.id.relative_layout_loading);
        this.main_rel = (RelativeLayout) findViewById(R.id.main_rel);
        this.linearLayoutAuto = (LinearLayout) findViewById(R.id.linearLayoutAuto);
        this.linearLayoutEraser = (LinearLayout) findViewById(R.id.linearLayoutEraser);
        this.lay_lasso_cut = (LinearLayout) findViewById(R.id.lay_lasso_cut);
        this.imageViewCutInSide = (ImageView) findViewById(R.id.imageViewCutInSide);
        this.imageViewCutOutSide = (ImageView) findViewById(R.id.imageViewCutOutSide);
        this.undo_btn = (ImageView) findViewById(R.id.imageViewUndo);
        this.redo_btn = (ImageView) findViewById(R.id.imageViewRedo);
        this.back_btn = (ImageView) findViewById(R.id.btn_back);
        this.save_btn = (ImageView) findViewById(R.id.save_image_btn);
        this.imageViewBackgroundCover = (ImageView) findViewById(R.id.imageViewBackgroundCover);
        this.back_btn.setOnClickListener(this);
        this.undo_btn.setOnClickListener(this);
        this.redo_btn.setOnClickListener(this);
        this.save_btn.setOnClickListener(this);
        this.imageViewCutInSide.setOnClickListener(this);
        this.imageViewCutOutSide.setOnClickListener(this);
        this.seekBarBrushOffset = (SeekBar) findViewById(R.id.seekBarBrushOffset);
        this.seekBarOffset = (SeekBar) findViewById(R.id.seekBarOffset);
        this.seekBarExtractOffset = (SeekBar) findViewById(R.id.seekBarExtractOffset);
        this.seekBarBrushOffset.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() { // from class: com.gallery.photos.editphotovideo.activities.EraserBgActivity.2
            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
                if (EraserBgActivity.this.eraseView != null) {
                    EraserBgActivity.this.eraseView.setOffset(i - 150);
                    EraserBgActivity.this.eraseView.invalidate();
                }
            }
        });
        this.seekBarOffset.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() { // from class: com.gallery.photos.editphotovideo.activities.EraserBgActivity.3
            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
                if (EraserBgActivity.this.eraseView != null) {
                    EraserBgActivity.this.eraseView.setOffset(i - 150);
                    EraserBgActivity.this.eraseView.invalidate();
                }
            }
        });
        this.seekBarExtractOffset.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() { // from class: com.gallery.photos.editphotovideo.activities.EraserBgActivity.4
            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
                if (EraserBgActivity.this.eraseView != null) {
                    EraserBgActivity.this.eraseView.setOffset(i - 150);
                    EraserBgActivity.this.eraseView.invalidate();
                }
            }
        });
        SeekBar seekBar = (SeekBar) findViewById(R.id.seekBarSize);
        this.radius_seekbar = seekBar;
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() { // from class: com.gallery.photos.editphotovideo.activities.EraserBgActivity.5
            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStartTrackingTouch(SeekBar seekBar2) {
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStopTrackingTouch(SeekBar seekBar2) {
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onProgressChanged(SeekBar seekBar2, int i, boolean z) {
                if (EraserBgActivity.this.eraseView != null) {
                    EraserBgActivity.this.eraseView.setRadius(i + 2);
                    EraserBgActivity.this.eraseView.invalidate();
                }
            }
        });
        SeekBar seekBar2 = (SeekBar) findViewById(R.id.seekBarThreshold);
        this.seekBarThreshold = seekBar2;
        seekBar2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() { // from class: com.gallery.photos.editphotovideo.activities.EraserBgActivity.6
            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onProgressChanged(SeekBar seekBar3, int i, boolean z) {
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStartTrackingTouch(SeekBar seekBar3) {
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStopTrackingTouch(SeekBar seekBar3) {
                if (EraserBgActivity.this.eraseView != null) {
                    EraserBgActivity.this.eraseView.setThreshold(seekBar3.getProgress() + 10);
                    EraserBgActivity.this.eraseView.updateThreshHold();
                }
            }
        });
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        if (this.eraseView != null || view.getId() == R.id.btn_back) {
            switch (view.getId()) {
                case R.id.btn_back /* 2131361926 */:
                    onBackPressed();
                    break;
                case R.id.imageViewCutInSide /* 2131362277 */:
                    this.eraseView.enableInsideCut(true);
                    this.imageViewCutInSide.clearAnimation();
                    this.imageViewCutOutSide.clearAnimation();
                    break;
                case R.id.imageViewCutOutSide /* 2131362278 */:
                    this.eraseView.enableInsideCut(false);
                    this.imageViewCutInSide.clearAnimation();
                    this.imageViewCutOutSide.clearAnimation();
                    break;
                case R.id.imageViewRedo /* 2131362301 */:
                    this.relative_layout_loading.setVisibility(View.VISIBLE);
                    new Thread(new Runnable() { // from class: com.gallery.photos.editphotovideo.activities.EraserBgActivity.7
                        @Override // java.lang.Runnable
                        public void run() {
                            try {
                                EraserBgActivity.this.runOnUiThread(new Runnable() { // from class: com.gallery.photos.editphotovideo.activities.EraserBgActivity.7.1
                                    @Override // java.lang.Runnable
                                    public void run() {
                                        EraserBgActivity.this.eraseView.redoChange();
                                    }
                                });
                                Thread.sleep(500L);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            EraserBgActivity.this.relative_layout_loading.setVisibility(View.GONE);
                        }
                    }).start();
                    break;
                case R.id.imageViewUndo /* 2131362320 */:
                    this.relative_layout_loading.setVisibility(View.VISIBLE);
                    new Thread(new Runnable() { // from class: com.gallery.photos.editphotovideo.activities.EraserBgActivity.8
                        @Override // java.lang.Runnable
                        public void run() {
                            try {
                                EraserBgActivity.this.runOnUiThread(new Runnable() { // from class: com.gallery.photos.editphotovideo.activities.EraserBgActivity.8.1
                                    @Override // java.lang.Runnable
                                    public void run() {
                                        EraserBgActivity.this.eraseView.undoChange();
                                    }
                                });
                                Thread.sleep(500L);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            EraserBgActivity.this.relative_layout_loading.setVisibility(View.GONE);
                        }
                    }).start();
                    break;
                case R.id.relativeLayoutAuto /* 2131362711 */:
                    this.eraseView.enableTouchClear(true);
                    this.main_rel.setOnTouchListener(null);
                    this.eraseView.setMODE(2);
                    this.eraseView.invalidate();
                    this.imageViewAuto.setColorFilter(getResources().getColor(R.color.mainColor));
                    this.imageViewEraser.setColorFilter(getResources().getColor(R.color.iconColor));
                    this.imageViewLasso.setColorFilter(getResources().getColor(R.color.iconColor));
                    this.imageViewRestore.setColorFilter(getResources().getColor(R.color.iconColor));
                    this.imageViewZoom.setColorFilter(getResources().getColor(R.color.iconColor));
                    this.textViewAuto.setTextColor(getResources().getColor(R.color.mainColor));
                    this.textViewEraser.setTextColor(getResources().getColor(R.color.iconColor));
                    this.textViewLasso.setTextColor(getResources().getColor(R.color.iconColor));
                    this.textViewRestore.setTextColor(getResources().getColor(R.color.iconColor));
                    this.textViewZoom.setTextColor(getResources().getColor(R.color.iconColor));
                    this.seekBarOffset.setProgress(this.eraseView.getOffset() + 150);
                    this.linearLayoutEraser.setVisibility(View.GONE);
                    this.linearLayoutAuto.setVisibility(View.VISIBLE);
                    this.lay_lasso_cut.setVisibility(View.GONE);
                    break;
                case R.id.relativeLayoutBackground /* 2131362712 */:
                    changeBG();
                    break;
                case R.id.relativeLayoutEraser /* 2131362738 */:
                    this.eraseView.enableTouchClear(true);
                    this.main_rel.setOnTouchListener(null);
                    this.eraseView.setMODE(1);
                    this.eraseView.invalidate();
                    this.imageViewAuto.setColorFilter(getResources().getColor(R.color.iconColor));
                    this.imageViewEraser.setColorFilter(getResources().getColor(R.color.mainColor));
                    this.imageViewLasso.setColorFilter(getResources().getColor(R.color.iconColor));
                    this.imageViewRestore.setColorFilter(getResources().getColor(R.color.iconColor));
                    this.imageViewZoom.setColorFilter(getResources().getColor(R.color.iconColor));
                    this.textViewAuto.setTextColor(getResources().getColor(R.color.iconColor));
                    this.textViewEraser.setTextColor(getResources().getColor(R.color.mainColor));
                    this.textViewLasso.setTextColor(getResources().getColor(R.color.iconColor));
                    this.textViewRestore.setTextColor(getResources().getColor(R.color.iconColor));
                    this.textViewZoom.setTextColor(getResources().getColor(R.color.iconColor));
                    this.seekBarBrushOffset.setProgress(this.eraseView.getOffset() + 150);
                    this.linearLayoutEraser.setVisibility(View.VISIBLE);
                    this.linearLayoutAuto.setVisibility(View.GONE);
                    this.lay_lasso_cut.setVisibility(View.GONE);
                    break;
                case R.id.relativeLayoutLasso /* 2131362745 */:
                    this.eraseView.enableTouchClear(true);
                    this.main_rel.setOnTouchListener(null);
                    this.eraseView.setMODE(3);
                    this.eraseView.invalidate();
                    this.imageViewAuto.setColorFilter(getResources().getColor(R.color.iconColor));
                    this.imageViewEraser.setColorFilter(getResources().getColor(R.color.iconColor));
                    this.imageViewLasso.setColorFilter(getResources().getColor(R.color.mainColor));
                    this.imageViewRestore.setColorFilter(getResources().getColor(R.color.iconColor));
                    this.imageViewZoom.setColorFilter(getResources().getColor(R.color.iconColor));
                    this.textViewAuto.setTextColor(getResources().getColor(R.color.iconColor));
                    this.textViewEraser.setTextColor(getResources().getColor(R.color.iconColor));
                    this.textViewLasso.setTextColor(getResources().getColor(R.color.mainColor));
                    this.textViewRestore.setTextColor(getResources().getColor(R.color.iconColor));
                    this.textViewZoom.setTextColor(getResources().getColor(R.color.iconColor));
                    this.seekBarExtractOffset.setProgress(this.eraseView.getOffset() + 150);
                    this.linearLayoutEraser.setVisibility(View.GONE);
                    this.linearLayoutAuto.setVisibility(View.GONE);
                    this.lay_lasso_cut.setVisibility(View.VISIBLE);
                    break;
                case R.id.relativeLayoutRestore /* 2131362754 */:
                    this.eraseView.enableTouchClear(true);
                    this.main_rel.setOnTouchListener(null);
                    this.eraseView.setMODE(4);
                    this.eraseView.invalidate();
                    this.imageViewAuto.setColorFilter(getResources().getColor(R.color.iconColor));
                    this.imageViewEraser.setColorFilter(getResources().getColor(R.color.iconColor));
                    this.imageViewLasso.setColorFilter(getResources().getColor(R.color.iconColor));
                    this.imageViewRestore.setColorFilter(getResources().getColor(R.color.mainColor));
                    this.imageViewZoom.setColorFilter(getResources().getColor(R.color.iconColor));
                    this.textViewAuto.setTextColor(getResources().getColor(R.color.iconColor));
                    this.textViewEraser.setTextColor(getResources().getColor(R.color.iconColor));
                    this.textViewLasso.setTextColor(getResources().getColor(R.color.iconColor));
                    this.textViewRestore.setTextColor(getResources().getColor(R.color.mainColor));
                    this.textViewZoom.setTextColor(getResources().getColor(R.color.iconColor));
                    this.seekBarBrushOffset.setProgress(this.eraseView.getOffset() + 150);
                    this.linearLayoutEraser.setVisibility(View.VISIBLE);
                    this.linearLayoutAuto.setVisibility(View.GONE);
                    this.lay_lasso_cut.setVisibility(View.GONE);
                    break;
                case R.id.relativeLayoutZoom /* 2131362765 */:
                    this.eraseView.enableTouchClear(false);
                    this.main_rel.setOnTouchListener(new MultiTouchListener());
                    this.eraseView.setMODE(0);
                    this.eraseView.invalidate();
                    this.imageViewAuto.setColorFilter(getResources().getColor(R.color.iconColor));
                    this.imageViewEraser.setColorFilter(getResources().getColor(R.color.iconColor));
                    this.imageViewLasso.setColorFilter(getResources().getColor(R.color.iconColor));
                    this.imageViewRestore.setColorFilter(getResources().getColor(R.color.iconColor));
                    this.imageViewZoom.setColorFilter(getResources().getColor(R.color.mainColor));
                    this.textViewAuto.setTextColor(getResources().getColor(R.color.iconColor));
                    this.textViewEraser.setTextColor(getResources().getColor(R.color.iconColor));
                    this.textViewLasso.setTextColor(getResources().getColor(R.color.iconColor));
                    this.textViewRestore.setTextColor(getResources().getColor(R.color.iconColor));
                    this.textViewZoom.setTextColor(getResources().getColor(R.color.mainColor));
                    this.linearLayoutEraser.setVisibility(View.GONE);
                    this.linearLayoutAuto.setVisibility(View.GONE);
                    this.lay_lasso_cut.setVisibility(View.GONE);
                    break;
                case R.id.save_image_btn /* 2131362793 */:
                    SaveView();
                    break;
            }
            return;
        }
        Toast.makeText(this, getResources().getString(R.string.import_img_warning), Toast.LENGTH_SHORT).show();
    }

    private void SaveView() {
        Bitmap finalBitmap = this.eraseView.getFinalBitmap();
        bitmap = finalBitmap;
        if (finalBitmap != null) {
            try {
                int dpToPx = ImageUtils.dpToPx((Context) this, 42.0f);
                Bitmap resizeBitmap = ImageUtils.resizeBitmap(bitmap, orgBitWidth + dpToPx + dpToPx, orgBitHeight + dpToPx + dpToPx);
                bitmap = resizeBitmap;
                int i = dpToPx + dpToPx;
                Bitmap createBitmap = Bitmap.createBitmap(resizeBitmap, dpToPx, dpToPx, resizeBitmap.getWidth() - i, bitmap.getHeight() - i);
                bitmap = createBitmap;
                Bitmap createScaledBitmap = Bitmap.createScaledBitmap(createBitmap, orgBitWidth, orgBitHeight, true);
                bitmap = createScaledBitmap;
                bitmap = ImageUtils.bitmapmasking(this.orgBitmap, createScaledBitmap);
                if (this.openFrom.equalsIgnoreCase(Constants.VALUE_OPEN_FROM_REMOVE_BG)) {
                    RemoveBgActivity.eraserResultBmp = bitmap;
                }
               /* else if (this.openFrom.equalsIgnoreCase(Constants.VALUE_OPEN_FROM_DRIP)) {
                    DripActivity.resultBmp = bitmap;
                } */
                else if (this.openFrom.equalsIgnoreCase(Constants.VALUE_OPEN_FROM_NEON)) {
                    NeonActivity.resultBmp = bitmap;
                } else if (this.openFrom.equalsIgnoreCase(Constants.VALUE_OPEN_FROM_ART)) {
                    PortraitActivity.resultBmp = bitmap;
                }
                setResult(-1);
                finish();
                return;
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
                return;
            }
        }
        finish();
    }

    private void changeBG() {
        int i = curBgType;
        if (i == 1) {
            curBgType = 2;
            this.imageViewBackgroundCover.setImageBitmap(null);
            this.imageViewBackgroundCover.setImageBitmap(ImageUtils.getTiledBitmap(this, R.drawable.tbg1, this.width, this.height));
            bgCircleBit = ImageUtils.getBgCircleBit(this, R.drawable.tbg1);
            return;
        }
        if (i == 2) {
            curBgType = 3;
            this.imageViewBackgroundCover.setImageBitmap(null);
            this.imageViewBackgroundCover.setImageBitmap(ImageUtils.getTiledBitmap(this, R.drawable.tbg, this.width, this.height));
            bgCircleBit = ImageUtils.getBgCircleBit(this, R.drawable.tbg);
            return;
        }
        if (i == 3) {
            curBgType = 4;
            this.imageViewBackgroundCover.setImageBitmap(null);
            this.imageViewBackgroundCover.setImageBitmap(ImageUtils.getTiledBitmap(this, R.drawable.tbg3, this.width, this.height));
            bgCircleBit = ImageUtils.getBgCircleBit(this, R.drawable.tbg3);
            return;
        }
        if (i == 4) {
            curBgType = 5;
            this.imageViewBackgroundCover.setImageBitmap(null);
            this.imageViewBackgroundCover.setImageBitmap(ImageUtils.getTiledBitmap(this, R.drawable.tbg4, this.width, this.height));
            bgCircleBit = ImageUtils.getBgCircleBit(this, R.drawable.tbg4);
            return;
        }
        if (i == 5) {
            curBgType = 6;
            this.imageViewBackgroundCover.setImageBitmap(null);
            this.imageViewBackgroundCover.setImageBitmap(ImageUtils.getTiledBitmap(this, R.drawable.tbg5, this.width, this.height));
            bgCircleBit = ImageUtils.getBgCircleBit(this, R.drawable.tbg5);
            return;
        }
        if (i == 6) {
            curBgType = 1;
            this.imageViewBackgroundCover.setImageBitmap(null);
            this.imageViewBackgroundCover.setImageBitmap(ImageUtils.getTiledBitmap(this, R.drawable.tbg2, this.width, this.height));
            bgCircleBit = ImageUtils.getBgCircleBit(this, R.drawable.tbg2);
        }
    }

    public void importImageFromUri() {
        this.showDialog = false;
        final ProgressDialog show = ProgressDialog.show(this, "", getResources().getString(R.string.importing_image), true);
        show.setCancelable(false);
        new Thread(new Runnable() { // from class: com.gallery.photos.editphotovideo.activities.EraserBgActivity.9
            @Override // java.lang.Runnable
            public void run() {
                try {
                    if (EraserBgActivity.b == null) {
                        EraserBgActivity.this.showDialog = true;
                    } else {
                        EraserBgActivity.this.orgBitmap = EraserBgActivity.b.copy(EraserBgActivity.b.getConfig(), true);
                        int dpToPx = ImageUtils.dpToPx((Context) EraserBgActivity.this, 42.0f);
                        EraserBgActivity.orgBitWidth = EraserBgActivity.b.getWidth();
                        EraserBgActivity.orgBitHeight = EraserBgActivity.b.getHeight();
                        Bitmap createBitmap = Bitmap.createBitmap(EraserBgActivity.b.getWidth() + dpToPx + dpToPx, EraserBgActivity.b.getHeight() + dpToPx + dpToPx, EraserBgActivity.b.getConfig());
                        Canvas canvas = new Canvas(createBitmap);
                        canvas.drawColor(0);
                        float f = dpToPx;
                        canvas.drawBitmap(EraserBgActivity.b, f, f, (Paint) null);
                        EraserBgActivity.b = createBitmap;
                        if (EraserBgActivity.b.getWidth() > EraserBgActivity.this.width || EraserBgActivity.b.getHeight() > EraserBgActivity.this.height || (EraserBgActivity.b.getWidth() < EraserBgActivity.this.width && EraserBgActivity.b.getHeight() < EraserBgActivity.this.height)) {
                            EraserBgActivity.b = ImageUtils.resizeBitmap(EraserBgActivity.b, EraserBgActivity.this.width, EraserBgActivity.this.height);
                        }
                    }
                    Thread.sleep(1000L);
                } catch (Exception e) {
                    e.printStackTrace();
                    EraserBgActivity.this.showDialog = true;
                    show.dismiss();
                } catch (OutOfMemoryError e2) {
                    e2.printStackTrace();
                    EraserBgActivity.this.showDialog = true;
                    show.dismiss();
                }
                show.dismiss();
            }
        }).start();
        show.setOnDismissListener(new DialogInterface.OnDismissListener() { // from class: com.gallery.photos.editphotovideo.activities.EraserBgActivity.10
            @Override // android.content.DialogInterface.OnDismissListener
            public void onDismiss(DialogInterface dialogInterface) {
                if (EraserBgActivity.this.showDialog) {
                    EraserBgActivity eraserBgActivity = EraserBgActivity.this;
                    Toast.makeText(eraserBgActivity, eraserBgActivity.getResources().getString(R.string.import_error), Toast.LENGTH_SHORT).show();
                    EraserBgActivity.this.finish();
                } else {
                    ConstantsApp.rewid = "";
                    ConstantsApp.uri = "";
                    ConstantsApp.bitmapSticker = null;
                    EraserBgActivity.this.setImageBitmap();
                }
            }
        });
    }

    public void setImageBitmap() {
        this.eraseView = new EraseView(this);
        this.dv1 = new ImageView(this);
        this.eraseView.setImageBitmap(b);
        this.dv1.setImageBitmap(getGreenLayerBitmap(b));
        this.eraseView.invalidate();
        this.eraseView.enableTouchClear(true);
        this.main_rel.setOnTouchListener(null);
        this.eraseView.setMODE(1);
        this.eraseView.invalidate();
        this.seekBarBrushOffset.setProgress(this.eraseView.getOffset() + 150);
        this.radius_seekbar.setProgress(18);
        this.seekBarThreshold.setProgress(20);
        this.main_rel.removeAllViews();
        this.main_rel.setScaleX(1.0f);
        this.main_rel.setScaleY(1.0f);
        this.main_rel.addView(this.dv1);
        this.main_rel.addView(this.eraseView);
        this.eraseView.invalidate();
        this.dv1.setVisibility(View.GONE);
        this.eraseView.setUndoRedoListener(new EraseView.UndoRedoListener() { // from class: com.gallery.photos.editphotovideo.activities.EraserBgActivity.11
            @Override // com.gallery.photos.editphotovideo.eraser.eraser.EraseView.UndoRedoListener
            public void enableUndo(boolean z, int i) {
                if (z) {
                    EraserBgActivity eraserBgActivity = EraserBgActivity.this;
                    eraserBgActivity.setBGDrawable(i, eraserBgActivity.undo_btn, R.drawable.ic_undo_active, z);
                } else {
                    EraserBgActivity eraserBgActivity2 = EraserBgActivity.this;
                    eraserBgActivity2.setBGDrawable(i, eraserBgActivity2.undo_btn, R.drawable.ic_undo, z);
                }
            }

            @Override // com.gallery.photos.editphotovideo.eraser.eraser.EraseView.UndoRedoListener
            public void enableRedo(boolean z, int i) {
                if (z) {
                    EraserBgActivity eraserBgActivity = EraserBgActivity.this;
                    eraserBgActivity.setBGDrawable(i, eraserBgActivity.redo_btn, R.drawable.ic_redo_active, z);
                } else {
                    EraserBgActivity eraserBgActivity2 = EraserBgActivity.this;
                    eraserBgActivity2.setBGDrawable(i, eraserBgActivity2.redo_btn, R.drawable.ic_redo, z);
                }
            }
        });
        b.recycle();
        this.eraseView.setActionListener(new EraseView.ActionListener() { // from class: com.gallery.photos.editphotovideo.activities.EraserBgActivity.12
            @Override // com.gallery.photos.editphotovideo.eraser.eraser.EraseView.ActionListener
            public void onActionCompleted(final int i) {
                EraserBgActivity.this.runOnUiThread(new Runnable() { // from class: com.gallery.photos.editphotovideo.activities.EraserBgActivity.12.1
                    @Override // java.lang.Runnable
                    public void run() {
                    }
                });
            }

            @Override // com.gallery.photos.editphotovideo.eraser.eraser.EraseView.ActionListener
            public void onAction(int i) {
                EraserBgActivity.this.runOnUiThread(new Runnable() { // from class: com.gallery.photos.editphotovideo.activities.EraserBgActivity.12.2
                    @Override // java.lang.Runnable
                    public void run() {
                    }
                });
            }
        });
    }

    public void setBGDrawable(int i, final ImageView imageView, final int i2, final boolean z) {
        runOnUiThread(new Runnable() { // from class: com.gallery.photos.editphotovideo.activities.EraserBgActivity.13
            @Override // java.lang.Runnable
            public void run() {
                imageView.setImageResource(i2);
                imageView.setEnabled(z);
            }
        });
    }

    public Bitmap getGreenLayerBitmap(Bitmap bitmap2) {
        Paint paint = new Paint();
        paint.setColor(-16711936);
        paint.setAlpha(80);
        int dpToPx = ImageUtils.dpToPx((Context) this, 42.0f);
        Bitmap createBitmap = Bitmap.createBitmap(orgBitWidth + dpToPx + dpToPx, orgBitHeight + dpToPx + dpToPx, bitmap2.getConfig());
        Canvas canvas = new Canvas(createBitmap);
        canvas.drawColor(0);
        float f = dpToPx;
        canvas.drawBitmap(this.orgBitmap, f, f, (Paint) null);
        canvas.drawRect(f, f, orgBitWidth + dpToPx, orgBitHeight + dpToPx, paint);
        Bitmap createBitmap2 = Bitmap.createBitmap(orgBitWidth + dpToPx + dpToPx, orgBitHeight + dpToPx + dpToPx, bitmap2.getConfig());
        Canvas canvas2 = new Canvas(createBitmap2);
        canvas2.drawColor(0);
        canvas2.drawBitmap(this.orgBitmap, f, f, (Paint) null);
        patternBMPshader = new BitmapShader(ImageUtils.resizeBitmap(createBitmap2, this.width, this.height), Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
        return ImageUtils.resizeBitmap(createBitmap, this.width, this.height);
    }

    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onDestroy() {
        Bitmap bitmap2 = b;
        if (bitmap2 != null) {
            bitmap2.recycle();
            b = null;
        }
        try {
            if (!isFinishing() && this.eraseView.pd != null && this.eraseView.pd.isShowing()) {
                this.eraseView.pd.dismiss();
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e2) {
            e2.printStackTrace();
        }
        super.onDestroy();
    }
}
