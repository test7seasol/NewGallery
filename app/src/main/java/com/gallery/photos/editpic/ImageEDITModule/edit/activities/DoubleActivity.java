package com.gallery.photos.editpic.ImageEDITModule.edit.activities;

import android.content.Context;
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
import android.widget.RelativeLayout;
import android.widget.SeekBar;

import com.gallery.photos.editpic.ImageEDITModule.edit.listener.MultiTouchListener;
import com.gallery.photos.editpic.ImageEDITModule.edit.utils.BitmapTransfer;
import com.gallery.photos.editpic.ImageEDITModule.edit.utils.ColorFilterGenerator;
import com.gallery.photos.editpic.ImageEDITModule.edit.utils.ColorSeekBar;
import com.gallery.photos.editpic.ImageEDITModule.edit.utils.ImageUtils;
import com.gallery.photos.editpic.R;


/* loaded from: classes.dex */
public class DoubleActivity extends BaseActivity {
    private static Bitmap faceBitmap;
    private static Bitmap faceBitmap2;

    private Context context;
    private Bitmap foreground;
    private ImageView imageViewBackground;
    private ImageView imageViewCover;
    private ImageView imageViewWings;
    private RelativeLayout relativeLayoutRootView;
    public ColorSeekBar seekbar_adjustTemp;
    private Bitmap selectedBitmap;
    public int count = 0;
    boolean isFirst = true;



    public static void setFaceBitmap(Bitmap bitmap) {
        faceBitmap = bitmap;
    }

    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        requestWindowFeature(1);
        getWindow().setFlags(1024, 1024);
        setContentView(R.layout.activity_double);
        this.context = this;
        this.selectedBitmap = faceBitmap;

        new Handler().postDelayed(new Runnable() { // from class: com.gallery.photos.editphotovideo.activities.DoubleActivity.1
            @Override // java.lang.Runnable
            public void run() {
                DoubleActivity.this.imageViewBackground.post(new Runnable() { // from class: com.gallery.photos.editphotovideo.activities.DoubleActivity.1.1
                    @Override // java.lang.Runnable
                    public void run() {
                        if (!DoubleActivity.this.isFirst || DoubleActivity.this.selectedBitmap == null) {
                            return;
                        }
                        DoubleActivity.this.isFirst = false;
                        DoubleActivity.this.initBitmap();
                    }
                });
            }
        }, 1000L);
        initViews();
        ColorSeekBar colorSeekBar = (ColorSeekBar) findViewById(R.id.seekbar_adjustTemp);
        this.seekbar_adjustTemp = colorSeekBar;
        colorSeekBar.setOnColorChangeListener(new ColorSeekBar.OnColorChangeListener() { // from class: com.gallery.photos.editphotovideo.activities.DoubleActivity.2
            @Override // com.gallery.photos.editphotovideo.utils.ColorSeekBar.OnColorChangeListener
            public void onColorChangeListener(int i, int i2, int i3) {
                DoubleActivity.this.imageViewBackground.setColorFilter(ColorFilterGenerator.adjustTemperature(Color.red(i3), Color.green(i3), Color.blue(i3)));
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void initBitmap() {
        ImageView imageView;
        Bitmap bitmap = faceBitmap;
        if (bitmap != null) {
            this.selectedBitmap = ImageUtils.getBitmapResize(this.context, bitmap, this.imageViewBackground.getWidth(), this.imageViewBackground.getHeight());
            this.relativeLayoutRootView.setLayoutParams(new LinearLayout.LayoutParams(this.selectedBitmap.getWidth(), this.selectedBitmap.getHeight()));
            Bitmap bitmap2 = this.selectedBitmap;
            if (bitmap2 != null && (imageView = this.imageViewCover) != null) {
                imageView.setImageBitmap(bitmap2);
            }
            setStart();
        }
    }

    public void initViews() {
        this.relativeLayoutRootView = (RelativeLayout) findViewById(R.id.relativeLayoutRootView);
        this.imageViewWings = (ImageView) findViewById(R.id.imageViewWings);
        this.imageViewCover = (ImageView) findViewById(R.id.imageViewBackground);
        ImageView imageView = (ImageView) findViewById(R.id.imageViewCover);
        this.imageViewBackground = imageView;
        imageView.setOnTouchListener(new MultiTouchListener(this, true));
        findViewById(R.id.imageViewCloseWings).setOnClickListener(new View.OnClickListener() { // from class: com.gallery.photos.editphotovideo.activities.DoubleActivity.3
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                DoubleActivity.this.onBackPressed();
            }
        });
        this.imageViewCover.setRotationY(0.0f);
        this.imageViewBackground.post(new Runnable() { // from class: com.gallery.photos.editphotovideo.activities.DoubleActivity.4
            @Override // java.lang.Runnable
            public void run() {
                DoubleActivity.this.initBitmap();
            }
        });
        ((SeekBar) findViewById(R.id.seekbarOpacity)).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() { // from class: com.gallery.photos.editphotovideo.activities.DoubleActivity.5
            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
                if (DoubleActivity.this.imageViewBackground != null) {
                    DoubleActivity.this.imageViewBackground.setAlpha(i * 0.01f);
                }
            }
        });
        findViewById(R.id.imageViewSaveWings).setOnClickListener(new View.OnClickListener() { // from class: com.gallery.photos.editphotovideo.activities.DoubleActivity.6
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                new saveFile().execute(new String[0]);
            }
        });
    }

    /* JADX WARN: Type inference failed for: r9v0, types: [com.gallery.photos.editphotovideo.activities.DoubleActivity$7] */
    public void setStart() {
        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.crop_progress_bar);
        progressBar.setVisibility(View.VISIBLE);
        new CountDownTimer(5000L, 1000L) { // from class: com.gallery.photos.editphotovideo.activities.DoubleActivity.7
            @Override // android.os.CountDownTimer
            public void onFinish() {
            }

            @Override // android.os.CountDownTimer
            public void onTick(long j) {
                DoubleActivity.this.count++;
                if (progressBar.getProgress() <= 90) {
                    progressBar.setProgress(DoubleActivity.this.count * 5);
                }
            }
        }.start();
    /*    new MLCropAsyncTask(new MLOnCropTaskCompleted() { // from class: com.gallery.photos.editphotovideo.activities.DoubleActivity.8
            @Override // com.gallery.photos.editphotovideo.crop.MLOnCropTaskCompleted
            public void onTaskCompleted(Bitmap bitmap, Bitmap bitmap2, int i, int i2) {
                DoubleActivity.this.selectedBitmap.getWidth();
                DoubleActivity.this.selectedBitmap.getHeight();
                int width = DoubleActivity.this.selectedBitmap.getWidth();
                int height = DoubleActivity.this.selectedBitmap.getHeight();
                int i3 = width * height;
                DoubleActivity.this.selectedBitmap.getPixels(new int[i3], 0, width, 0, 0, width, height);
                int[] iArr = new int[i3];
                Bitmap createBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                createBitmap.setPixels(iArr, 0, width, 0, 0, width, height);
                DoubleActivity doubleActivity = DoubleActivity.this;
                doubleActivity.foreground = ImageUtils.getMask(doubleActivity, doubleActivity.selectedBitmap, createBitmap, width, height);
                DoubleActivity.this.foreground = Bitmap.createScaledBitmap(bitmap, DoubleActivity.this.foreground.getWidth(), DoubleActivity.this.foreground.getHeight(), false);
                DoubleActivity.this.runOnUiThread(new Runnable() { // from class: com.gallery.photos.editphotovideo.activities.DoubleActivity.8.1
                    @Override // java.lang.Runnable
                    public void run() {
                        if (Palette.from(DoubleActivity.this.foreground).generate().getDominantSwatch() == null) {
                            Toast.makeText(DoubleActivity.this, DoubleActivity.this.getString(R.string.txt_not_detect_human), 0).show();
                        }
                        BitmapTransfer.bitmap = DoubleActivity.this.foreground;
                        DoubleActivity.this.imageViewBackground.setImageBitmap(BitmapTransfer.bitmap);
                        DoubleActivity.this.imageViewWings.setImageBitmap(BitmapTransfer.bitmap);
                    }
                });
            }
        }, this, progressBar).execute(new Void[0]);*/
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
            DoubleActivity.this.relativeLayoutRootView.setDrawingCacheEnabled(true);
            Bitmap bitmapFromView = getBitmapFromView(DoubleActivity.this.relativeLayoutRootView);
            DoubleActivity.this.relativeLayoutRootView.setDrawingCacheEnabled(false);
            return bitmapFromView;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if (bitmap != null) {
                BitmapTransfer.setBitmap(bitmap);
            }
            Intent intent = new Intent(DoubleActivity.this, (Class<?>) PhotoEditorActivity.class);
            intent.putExtra("MESSAGE", "done");
            DoubleActivity.this.setResult(-1, intent);
            DoubleActivity.this.finish();
        }
    }
}
