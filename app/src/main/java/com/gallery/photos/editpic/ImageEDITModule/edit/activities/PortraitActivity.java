package com.gallery.photos.editpic.ImageEDITModule.edit.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.gallery.photos.editpic.ImageEDITModule.edit.Editor.DripView;
import com.gallery.photos.editpic.ImageEDITModule.edit.adapters.PortraitAdapter;
import com.gallery.photos.editpic.ImageEDITModule.edit.drip.TouchListener;
import com.gallery.photos.editpic.ImageEDITModule.edit.listener.LayoutItemListener;
import com.gallery.photos.editpic.ImageEDITModule.edit.support.Constants;
import com.gallery.photos.editpic.ImageEDITModule.edit.utils.BitmapTransfer;
import com.gallery.photos.editpic.ImageEDITModule.edit.utils.ColorSlider;
import com.gallery.photos.editpic.ImageEDITModule.edit.utils.DripFrameLayout;
import com.gallery.photos.editpic.ImageEDITModule.edit.utils.DripUtils;
import com.gallery.photos.editpic.ImageEDITModule.edit.utils.ImageUtils;
import com.gallery.photos.editpic.ImageEDITModule.github.flipzeus.FlipDirection;
import com.gallery.photos.editpic.ImageEDITModule.github.flipzeus.ImageFlipper;
import com.gallery.photos.editpic.R;

import java.util.ArrayList;

import yuku.ambilwarna.AmbilWarnaDialog;

/* loaded from: classes.dex */
public class PortraitActivity extends BaseActivity implements LayoutItemListener {
    private static Bitmap faceBitmap;
    public static Bitmap resultBmp;

    private PortraitAdapter dripItemAdapter;
    private DripView dripViewBackground;
    private DripView dripViewImage;
    private DripView dripViewStyle;
    private Bitmap foreground;
    private DripFrameLayout frameLayoutBackground;
    private ImageView imageViewBg;
    private ImageView imageViewColor;
    private ImageView imageViewStyle;
    private LinearLayout linLayoutColor;
    private RecyclerView recyclerViewStyle;
    private SeekBar seekBarSmooth;
    private Bitmap selectedBitmap;
    private TextView textViewBg;
    private TextView textViewColor;
    private TextView textViewStyle;
    private Bitmap OverLayBack = null;
    public int count = 0;
    private Bitmap OverLayFront = null;
    private boolean isFirst = true;
    private ArrayList<String> dripEffectList = new ArrayList<>();
    FEATURES selectedFeatures = FEATURES.COLOR;
    private ColorSlider.OnColorSelectedListener mListener = new ColorSlider.OnColorSelectedListener() { // from class: com.gallery.photos.editphotovideo.activities.PortraitActivity.12
        @Override // com.gallery.photos.editphotovideo.utils.ColorSlider.OnColorSelectedListener
        public void onColorChanged(int i, int i2) {
            PortraitActivity.this.updateView(i2);
        }
    };

    enum FEATURES {
        COLOR,
        BACKGROUND
    }


    @Override
    // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        requestWindowFeature(1);
        getWindow().setFlags(1024, 1024);
        setContentView(R.layout.activity_portrait);

        this.dripViewStyle = (DripView) findViewById(R.id.dripViewStyle);
        this.dripViewImage = (DripView) findViewById(R.id.dripViewImage);
        this.textViewStyle = (TextView) findViewById(R.id.textViewStyle);
        this.textViewBg = (TextView) findViewById(R.id.textViewBg);
        this.textViewColor = (TextView) findViewById(R.id.textViewColor);
        this.imageViewStyle = (ImageView) findViewById(R.id.imageViewStyle);
        this.imageViewBg = (ImageView) findViewById(R.id.imageViewBg);
        this.imageViewColor = (ImageView) findViewById(R.id.imageViewColor);
        this.dripViewBackground = (DripView) findViewById(R.id.dripViewBackground);
        this.frameLayoutBackground = (DripFrameLayout) findViewById(R.id.frameLayoutBackground);
        this.linLayoutColor = (LinearLayout) findViewById(R.id.linLayoutColor);
        this.dripViewImage.setOnTouchListenerCustom(new TouchListener());
        SeekBar seekBar = (SeekBar) findViewById(R.id.seekBarSmooth);
        this.seekBarSmooth = seekBar;
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() { // from class: com.gallery.photos.editphotovideo.activities.PortraitActivity.1
            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStartTrackingTouch(SeekBar seekBar2) {
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStopTrackingTouch(SeekBar seekBar2) {
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onProgressChanged(SeekBar seekBar2, int i, boolean z) {
                Float valueOf = Float.valueOf((i / 20.0f) + 1.0f);
                PortraitActivity.this.dripViewStyle.setScaleX(valueOf.floatValue());
                PortraitActivity.this.dripViewBackground.setScaleX(valueOf.floatValue());
                PortraitActivity.this.dripViewStyle.setScaleY(valueOf.floatValue());
                PortraitActivity.this.dripViewBackground.setScaleY(valueOf.floatValue());
            }
        });
        new Handler().postDelayed(new Runnable() { // from class: com.gallery.photos.editphotovideo.activities.PortraitActivity.2
            @Override // java.lang.Runnable
            public void run() {
                PortraitActivity.this.dripViewImage.post(new Runnable() { // from class: com.gallery.photos.editphotovideo.activities.PortraitActivity.2.1
                    @Override // java.lang.Runnable
                    public void run() {
                        if (PortraitActivity.this.isFirst) {
                            PortraitActivity.this.isFirst = false;
                            PortraitActivity.this.initBitmap();
                        }
                    }
                });
            }
        }, 1000L);
        findViewById(R.id.imageViewCloseDrip).setOnClickListener(new View.OnClickListener() { // from class: com.gallery.photos.editphotovideo.activities.PortraitActivity.3
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                PortraitActivity.this.onBackPressed();
            }
        });
        findViewById(R.id.imageViewSaveDrip).setOnClickListener(new View.OnClickListener() { // from class: com.gallery.photos.editphotovideo.activities.PortraitActivity.4
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {

                new saveFile().execute(new String[0]);

            }
        });
        findViewById(R.id.linearLayoutEraser).setOnClickListener(new View.OnClickListener() { // from class: com.gallery.photos.editphotovideo.activities.PortraitActivity.5
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                EraserBgActivity.b = PortraitActivity.this.foreground;
                Intent intent = new Intent(PortraitActivity.this, (Class<?>) EraserBgActivity.class);
                intent.putExtra(Constants.KEY_OPEN_FROM, Constants.VALUE_OPEN_FROM_ART);
                PortraitActivity.this.startActivityForResult(intent, 1024);
            }
        });
        for (int i = 1; i <= 13; i++) {
            this.dripEffectList.add("frame_" + i);
        }
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerViewStyle);
        this.recyclerViewStyle = recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        setDripList();
        this.dripViewImage.post(new Runnable() { // from class: com.gallery.photos.editphotovideo.activities.PortraitActivity.6
            @Override // java.lang.Runnable
            public void run() {
                PortraitActivity.this.initBitmap();
            }
        });
        ColorSlider colorSlider = (ColorSlider) findViewById(R.id.color_slider);
        colorSlider.setSelectorColor(-1);
        colorSlider.setListener(this.mListener);
        updateView(colorSlider.getSelectedColor());
        findViewById(R.id.linearLayoutStyle).setOnClickListener(new View.OnClickListener() { // from class: com.gallery.photos.editphotovideo.activities.PortraitActivity.7
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                PortraitActivity.this.imageViewStyle.setColorFilter(PortraitActivity.this.getResources().getColor(R.color.mainColor));
                PortraitActivity.this.imageViewBg.setColorFilter(PortraitActivity.this.getResources().getColor(R.color.iconColor));
                PortraitActivity.this.imageViewColor.setColorFilter(PortraitActivity.this.getResources().getColor(R.color.iconColor));
                PortraitActivity.this.textViewStyle.setTextColor(PortraitActivity.this.getResources().getColor(R.color.mainColor));
                PortraitActivity.this.textViewBg.setTextColor(PortraitActivity.this.getResources().getColor(R.color.iconColor));
                PortraitActivity.this.textViewColor.setTextColor(PortraitActivity.this.getResources().getColor(R.color.iconColor));
                PortraitActivity.this.recyclerViewStyle.setVisibility(View.VISIBLE);
                PortraitActivity.this.linLayoutColor.setVisibility(View.GONE);
            }
        });
        findViewById(R.id.linearLayoutColor).setOnClickListener(new View.OnClickListener() { // from class: com.gallery.photos.editphotovideo.activities.PortraitActivity.8
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                PortraitActivity.this.selectedFeatures = FEATURES.COLOR;
                PortraitActivity.this.imageViewStyle.setColorFilter(PortraitActivity.this.getResources().getColor(R.color.iconColor));
                PortraitActivity.this.imageViewBg.setColorFilter(PortraitActivity.this.getResources().getColor(R.color.iconColor));
                PortraitActivity.this.imageViewColor.setColorFilter(PortraitActivity.this.getResources().getColor(R.color.mainColor));
                PortraitActivity.this.textViewStyle.setTextColor(PortraitActivity.this.getResources().getColor(R.color.iconColor));
                PortraitActivity.this.textViewBg.setTextColor(PortraitActivity.this.getResources().getColor(R.color.iconColor));
                PortraitActivity.this.textViewColor.setTextColor(PortraitActivity.this.getResources().getColor(R.color.mainColor));
                PortraitActivity.this.recyclerViewStyle.setVisibility(View.GONE);
                PortraitActivity.this.linLayoutColor.setVisibility(View.VISIBLE);
            }
        });
        findViewById(R.id.linearLayoutBg).setOnClickListener(new View.OnClickListener() { // from class: com.gallery.photos.editphotovideo.activities.PortraitActivity.9
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                PortraitActivity.this.selectedFeatures = FEATURES.BACKGROUND;
                PortraitActivity.this.imageViewStyle.setColorFilter(PortraitActivity.this.getResources().getColor(R.color.iconColor));
                PortraitActivity.this.imageViewBg.setColorFilter(PortraitActivity.this.getResources().getColor(R.color.mainColor));
                PortraitActivity.this.imageViewColor.setColorFilter(PortraitActivity.this.getResources().getColor(R.color.iconColor));
                PortraitActivity.this.textViewStyle.setTextColor(PortraitActivity.this.getResources().getColor(R.color.iconColor));
                PortraitActivity.this.textViewBg.setTextColor(PortraitActivity.this.getResources().getColor(R.color.mainColor));
                PortraitActivity.this.textViewColor.setTextColor(PortraitActivity.this.getResources().getColor(R.color.iconColor));
                PortraitActivity.this.recyclerViewStyle.setVisibility(View.GONE);
                PortraitActivity.this.linLayoutColor.setVisibility(View.VISIBLE);
            }
        });
        findViewById(R.id.imageViewColorPicker).setOnClickListener(new View.OnClickListener() { // from class: com.gallery.photos.editphotovideo.activities.PortraitActivity.10
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                new AmbilWarnaDialog(PortraitActivity.this, Color.parseColor("#0090FF"), true, new AmbilWarnaDialog.OnAmbilWarnaListener() { // from class: com.gallery.photos.editphotovideo.activities.PortraitActivity.10.1
                    @Override // yuku.ambilwarna.AmbilWarnaDialog.OnAmbilWarnaListener
                    public void onCancel(AmbilWarnaDialog ambilWarnaDialog) {
                    }

                    @Override // yuku.ambilwarna.AmbilWarnaDialog.OnAmbilWarnaListener
                    public void onOk(AmbilWarnaDialog ambilWarnaDialog, int i2) {
                        PortraitActivity.this.updateView(i2);
                    }
                }).show();
            }
        });
        findViewById(R.id.linearLayoutFlip).setOnClickListener(new View.OnClickListener() { // from class: com.gallery.photos.editphotovideo.activities.PortraitActivity.11
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                ImageFlipper.flip(PortraitActivity.this.dripViewStyle, FlipDirection.HORIZONTAL);
                ImageFlipper.flip(PortraitActivity.this.dripViewBackground, FlipDirection.HORIZONTAL);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateView(int i) {
        if (this.selectedFeatures == FEATURES.COLOR) {
            this.dripViewBackground.setColorFilter(i);
            this.dripViewStyle.setColorFilter(i);
        } else if (this.selectedFeatures == FEATURES.BACKGROUND) {
            this.frameLayoutBackground.setBackgroundColor(i);
        }
    }

    public static void setFaceBitmap(Bitmap bitmap) {
        faceBitmap = bitmap;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void initBitmap() {
        Bitmap bitmap = faceBitmap;
        if (bitmap != null) {
            this.selectedBitmap = ImageUtils.getBitmapResize(this, bitmap, 1024, 1024);
            Glide.with((FragmentActivity) this).load(Integer.valueOf(R.drawable.frame_1_f)).into(this.dripViewStyle);
            Glide.with((FragmentActivity) this).load(Integer.valueOf(R.drawable.frame_1_b)).into(this.dripViewBackground);
            setStart();
        }
    }

    @Override
    // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, android.app.Activity
    protected void onActivityResult(int i, int i2, Intent intent) {
        Bitmap bitmap;
        super.onActivityResult(i, i2, intent);
        if (i2 == -1 && i == 1024 && (bitmap = resultBmp) != null) {
            this.foreground = bitmap;
            this.dripViewImage.setImageBitmap(bitmap);
        }
    }

    /* JADX WARN: Type inference failed for: r9v0, types: [com.gallery.photos.editphotovideo.activities.PortraitActivity$13] */
    public void setStart() {
        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.crop_progress_bar);
        progressBar.setVisibility(View.VISIBLE);
        new CountDownTimer(5000L, 1000L) { // from class: com.gallery.photos.editphotovideo.activities.PortraitActivity.13
            @Override // android.os.CountDownTimer
            public void onFinish() {
            }

            @Override // android.os.CountDownTimer
            public void onTick(long j) {
                PortraitActivity.this.count++;
                if (progressBar.getProgress() <= 90) {
                    progressBar.setProgress(PortraitActivity.this.count * 5);
                }
            }
        }.start();
      /*  new MLCropAsyncTask(new MLOnCropTaskCompleted() { // from class: com.gallery.photos.editphotovideo.activities.PortraitActivity.14
            @Override // com.gallery.photos.editphotovideo.crop.MLOnCropTaskCompleted
            public void onTaskCompleted(Bitmap bitmap, Bitmap bitmap2, int i, int i2) {
                PortraitActivity.this.selectedBitmap.getWidth();
                PortraitActivity.this.selectedBitmap.getHeight();
                int width = PortraitActivity.this.selectedBitmap.getWidth();
                int height = PortraitActivity.this.selectedBitmap.getHeight();
                int i3 = width * height;
                PortraitActivity.this.selectedBitmap.getPixels(new int[i3], 0, width, 0, 0, width, height);
                int[] iArr = new int[i3];
                Bitmap createBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                createBitmap.setPixels(iArr, 0, width, 0, 0, width, height);
                PortraitActivity portraitActivity = PortraitActivity.this;
                portraitActivity.foreground = ImageUtils.getMask(portraitActivity, portraitActivity.selectedBitmap, createBitmap, width, height);
                PortraitActivity.this.foreground = Bitmap.createScaledBitmap(bitmap, PortraitActivity.this.foreground.getWidth(), PortraitActivity.this.foreground.getHeight(), false);
                PortraitActivity.this.runOnUiThread(new Runnable() { // from class: com.gallery.photos.editphotovideo.activities.PortraitActivity.14.1
                    @Override // java.lang.Runnable
                    public void run() {
                        if (Palette.from(PortraitActivity.this.foreground).generate().getDominantSwatch() == null) {
                            Toast.makeText(PortraitActivity.this, PortraitActivity.this.getString(R.string.txt_not_detect_human), 0).show();
                        }
                        BitmapTransfer.bitmap = PortraitActivity.this.foreground;
                        PortraitActivity.this.dripViewImage.setImageBitmap(BitmapTransfer.bitmap);
                    }
                });
            }
        }, this, progressBar).execute(new Void[0]);*/
    }

    @Override // com.gallery.photos.editphotovideo.listener.LayoutItemListener
    public void onLayoutListClick(View view, int i) {
        Bitmap bitmapFromAsset = DripUtils.getBitmapFromAsset(this, "art/" + this.dripItemAdapter.getItemList().get(i) + "_f.webp");
        Bitmap bitmapFromAsset2 = DripUtils.getBitmapFromAsset(this, "art/" + this.dripItemAdapter.getItemList().get(i) + "_b.webp");
        if (!"none".equals(this.dripItemAdapter.getItemList().get(i))) {
            this.OverLayFront = bitmapFromAsset;
            this.OverLayBack = bitmapFromAsset2;
            this.dripViewStyle.setImageBitmap(bitmapFromAsset);
            this.dripViewBackground.setImageBitmap(this.OverLayBack);
            return;
        }
        this.OverLayFront = null;
        this.OverLayBack = null;
    }

    public void setDripList() {
        PortraitAdapter portraitAdapter = new PortraitAdapter(this);
        this.dripItemAdapter = portraitAdapter;
        portraitAdapter.setLayoutItenListener(this);
        this.recyclerViewStyle.setAdapter(this.dripItemAdapter);
        this.dripItemAdapter.addData(this.dripEffectList);
    }

    private class saveFile extends AsyncTask<String, Bitmap, Bitmap> {
        private saveFile() {
        }

        @Override // android.os.AsyncTask
        protected void onPreExecute() {
            super.onPreExecute();
        }

        public Bitmap getBitmapFromView(View view) {
            Bitmap createBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
            view.draw(new Canvas(createBitmap));
            return createBitmap;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public Bitmap doInBackground(String... strArr) {
            PortraitActivity.this.frameLayoutBackground.setDrawingCacheEnabled(true);
            try {
                Bitmap bitmapFromView = getBitmapFromView(PortraitActivity.this.frameLayoutBackground);
                PortraitActivity.this.frameLayoutBackground.setDrawingCacheEnabled(false);
                return bitmapFromView;
            } catch (Exception unused) {
                PortraitActivity.this.frameLayoutBackground.setDrawingCacheEnabled(false);
                return null;
            } catch (Throwable th) {
                PortraitActivity.this.frameLayoutBackground.setDrawingCacheEnabled(false);
                throw th;
            }
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if (bitmap != null) {
                BitmapTransfer.setBitmap(bitmap);
            }
            Intent intent = new Intent(PortraitActivity.this, (Class<?>) PhotoEditorActivity.class);
            intent.putExtra("MESSAGE", "done");
            PortraitActivity.this.setResult(-1, intent);
            PortraitActivity.this.startActivity(intent);
            PortraitActivity.this.finish();
        }
    }


}
