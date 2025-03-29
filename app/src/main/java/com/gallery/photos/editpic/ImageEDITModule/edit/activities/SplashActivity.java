package com.gallery.photos.editpic.ImageEDITModule.edit.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.core.content.FileProvider;

import com.gallery.photos.editpic.ImageEDITModule.edit.draw.SplashBrushView;
import com.gallery.photos.editpic.ImageEDITModule.edit.draw.SplashView;
import com.gallery.photos.editpic.ImageEDITModule.edit.utils.BitmapTransfer;
import com.gallery.photos.editpic.ImageEDITModule.edit.utils.ColorSlider;
import com.gallery.photos.editpic.ImageEDITModule.edit.views.SupportedClass;
import com.gallery.photos.editpic.ImageEDITModule.github.siyamed.shapeimageview.RoundedImageView;
import com.gallery.photos.editpic.R;

import java.io.File;
import java.util.Vector;

import yuku.ambilwarna.AmbilWarnaDialog;

/* loaded from: classes.dex */
public class SplashActivity extends BaseActivity implements SeekBar.OnSeekBarChangeListener {
    public static final int REQUEST_CODE_CAMERA = 2;
    public static final int REQUEST_CODE_GALLERY = 3;
    public static SplashBrushView brushView;
    public static Bitmap colorBitmap;
    public static int displayHight;
    public static int displayWidth;
    public static String drawPath;
    public static Bitmap grayBitmap;
    public static RoundedImageView prView;
    public static SeekBar seekBarOffset;
    public static SeekBar seekBarOpacity;
    public static SeekBar seekBarSize;
    public static SplashView splashView;
    public static Vector vector;
    private ImageView imageViewColor;
    private ImageView imageViewGray;
    private ImageView imageViewManual;
    private ImageView imageViewZoom;
    private LinearLayout linearLayoutColor;
    private ColorSlider.OnColorSelectedListener mListener = new ColorSlider.OnColorSelectedListener() { // from class: com.gallery.photos.editphotovideo.activities.SplashActivity.10
        @Override // com.gallery.photos.editphotovideo.utils.ColorSlider.OnColorSelectedListener
        public void onColorChanged(int i, int i2) {
            SplashActivity.this.updateView(i2);
        }
    };
    private RelativeLayout relativeLayoutContainer;
    private Runnable runnableCode;
    public String selectedImagePath;
    public Uri selectedImageUri;
    public String selectedOutputPath;
    private TextView textViewColor;
    private TextView textViewGray;
    private TextView textViewManual;
    private TextView textViewZoom;


    @Override // android.widget.SeekBar.OnSeekBarChangeListener
    public void onStopTrackingTouch(SeekBar seekBar) {
    }

    @Override
    // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        requestWindowFeature(1);
        getWindow().setFlags(1024, 1024);
        setContentView(R.layout.activity_splash_edit);
        ColorSlider colorSlider = (ColorSlider) findViewById(R.id.color_slider);
        colorSlider.setSelectorColor(-1);
        colorSlider.setListener(this.mListener);
        this.relativeLayoutContainer = (RelativeLayout) findViewById(R.id.relativeLayoutContainer);
        brushView = (SplashBrushView) findViewById(R.id.brushView);
        vector = new Vector();
        Display defaultDisplay = getWindowManager().getDefaultDisplay();
        Point point = new Point();
        defaultDisplay.getSize(point);
        displayWidth = point.x;
        displayHight = point.y;
        splashView = (SplashView) findViewById(R.id.drawingImageView);
//        if (BitmapTransfer.getBitmap() != null) {
//            colorBitmap = BitmapTransfer.getBitmap();
//        }
        Bitmap transferredBitmap = BitmapTransfer.getBitmap();
        if (transferredBitmap != null && !transferredBitmap.isRecycled()) {
            colorBitmap = transferredBitmap.copy(Bitmap.Config.ARGB_8888, true);
        } else {
            // Handle error case - maybe finish activity or show error
            finish();
            return;
        }

        grayBitmap = grayScaleBitmap(colorBitmap);
        this.textViewColor = (TextView) findViewById(R.id.textViewColor);
        this.textViewGray = (TextView) findViewById(R.id.textViewGray);
        prView = (RoundedImageView) findViewById(R.id.preview);
        this.textViewManual = (TextView) findViewById(R.id.textViewManual);
        this.linearLayoutColor = (LinearLayout) findViewById(R.id.linLayoutColor);
        this.textViewZoom = (TextView) findViewById(R.id.textViewZoom);
        this.imageViewColor = (ImageView) findViewById(R.id.imageViewColor);
        this.imageViewGray = (ImageView) findViewById(R.id.imageViewGray);
        this.imageViewManual = (ImageView) findViewById(R.id.imageViewManual);
        this.imageViewZoom = (ImageView) findViewById(R.id.imageViewZoom);
        seekBarSize = (SeekBar) findViewById(R.id.seekBarSize);
        seekBarOffset = (SeekBar) findViewById(R.id.seekBarOffset);
        seekBarOpacity = (SeekBar) findViewById(R.id.seekBarOpacity);
        seekBarSize.setMax(100);
        seekBarOpacity.setMax(240);
        new Canvas(Bitmap.createBitmap(300, 300, Bitmap.Config.ARGB_8888).copy(Bitmap.Config.ARGB_8888, true));
        seekBarOffset.setMax(100);
        seekBarOffset.setProgress(50);
        seekBarSize.setProgress((int) splashView.radius);
        seekBarOpacity.setProgress(splashView.opacity);
        seekBarSize.setOnSeekBarChangeListener(this);
        seekBarOpacity.setOnSeekBarChangeListener(this);
        seekBarOffset.setOnSeekBarChangeListener(this);
        splashView.initDrawing();
        final Handler handler = new Handler();
        Runnable runnable = new Runnable() { // from class: com.gallery.photos.editphotovideo.activities.SplashActivity.1
            @Override // java.lang.Runnable
            public void run() {
                handler.postDelayed(SplashActivity.this.runnableCode, 2000L);
            }
        };
        this.runnableCode = runnable;
        handler.post(runnable);
        findViewById(R.id.imageViewSaveSplash).setOnClickListener(new View.OnClickListener() { // from class: com.gallery.photos.editphotovideo.activities.SplashActivity.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {

                SplashActivity.this.SaveView();

            }
        });
        findViewById(R.id.imageViewCloseSplash).setOnClickListener(new View.OnClickListener() { // from class: com.gallery.photos.editphotovideo.activities.SplashActivity.3
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                SplashActivity.this.onBackPressed();
                SplashActivity.this.finish();
            }
        });
        findViewById(R.id.linearLayoutManual).setOnClickListener(new View.OnClickListener() { // from class: com.gallery.photos.editphotovideo.activities.SplashActivity.4
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                SplashActivity.this.imageViewManual.setColorFilter(SplashActivity.this.getResources().getColor(R.color.mainColor));
                SplashActivity.this.imageViewColor.setColorFilter(SplashActivity.this.getResources().getColor(R.color.iconColor));
                SplashActivity.this.imageViewGray.setColorFilter(SplashActivity.this.getResources().getColor(R.color.iconColor));
                SplashActivity.this.imageViewZoom.setColorFilter(SplashActivity.this.getResources().getColor(R.color.iconColor));
                SplashActivity.this.textViewManual.setTextColor(SplashActivity.this.getResources().getColor(R.color.mainColor));
                SplashActivity.this.textViewColor.setTextColor(SplashActivity.this.getResources().getColor(R.color.iconColor));
                SplashActivity.this.textViewGray.setTextColor(SplashActivity.this.getResources().getColor(R.color.iconColor));
                SplashActivity.this.textViewZoom.setTextColor(SplashActivity.this.getResources().getColor(R.color.iconColor));
                SplashActivity.this.linearLayoutColor.setVisibility(View.VISIBLE);
                SplashActivity.splashView.mode = 0;
                SplashActivity.splashView.splashBitmap = SplashActivity.this.grayScaleBitmap(SplashActivity.colorBitmap);
                SplashActivity.splashView.updateRefMetrix();
                SplashActivity.splashView.changeShaderBitmap();
                SplashActivity.splashView.coloring = -2;
            }
        });
        findViewById(R.id.linearLayoutColor).setOnClickListener(new View.OnClickListener() { // from class: com.gallery.photos.editphotovideo.activities.SplashActivity.5
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                SplashActivity.this.imageViewManual.setColorFilter(SplashActivity.this.getResources().getColor(R.color.iconColor));
                SplashActivity.this.imageViewColor.setColorFilter(SplashActivity.this.getResources().getColor(R.color.mainColor));
                SplashActivity.this.imageViewGray.setColorFilter(SplashActivity.this.getResources().getColor(R.color.iconColor));
                SplashActivity.this.imageViewZoom.setColorFilter(SplashActivity.this.getResources().getColor(R.color.iconColor));
                SplashActivity.this.textViewManual.setTextColor(SplashActivity.this.getResources().getColor(R.color.iconColor));
                SplashActivity.this.textViewColor.setTextColor(SplashActivity.this.getResources().getColor(R.color.mainColor));
                SplashActivity.this.textViewGray.setTextColor(SplashActivity.this.getResources().getColor(R.color.iconColor));
                SplashActivity.this.textViewZoom.setTextColor(SplashActivity.this.getResources().getColor(R.color.iconColor));
                SplashActivity.this.linearLayoutColor.setVisibility(View.GONE);
                SplashActivity.splashView.mode = 0;
                SplashView splashView2 = SplashActivity.splashView;
                splashView2.splashBitmap = SplashActivity.colorBitmap;
                splashView2.updateRefMetrix();
                SplashActivity.splashView.changeShaderBitmap();
                SplashActivity.splashView.coloring = -1;
            }
        });
        findViewById(R.id.linearLayoutGray).setOnClickListener(new View.OnClickListener() { // from class: com.gallery.photos.editphotovideo.activities.SplashActivity.6
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                SplashActivity.this.imageViewManual.setColorFilter(SplashActivity.this.getResources().getColor(R.color.iconColor));
                SplashActivity.this.imageViewColor.setColorFilter(SplashActivity.this.getResources().getColor(R.color.iconColor));
                SplashActivity.this.imageViewGray.setColorFilter(SplashActivity.this.getResources().getColor(R.color.mainColor));
                SplashActivity.this.imageViewZoom.setColorFilter(SplashActivity.this.getResources().getColor(R.color.iconColor));
                SplashActivity.this.textViewManual.setTextColor(SplashActivity.this.getResources().getColor(R.color.iconColor));
                SplashActivity.this.textViewColor.setTextColor(SplashActivity.this.getResources().getColor(R.color.iconColor));
                SplashActivity.this.textViewGray.setTextColor(SplashActivity.this.getResources().getColor(R.color.mainColor));
                SplashActivity.this.textViewZoom.setTextColor(SplashActivity.this.getResources().getColor(R.color.iconColor));
                SplashActivity.this.linearLayoutColor.setVisibility(View.GONE);
                SplashActivity.splashView.mode = 0;
                SplashActivity.splashView.splashBitmap = SplashActivity.this.grayScaleBitmap(SplashActivity.colorBitmap);
                SplashActivity.splashView.updateRefMetrix();
                SplashActivity.splashView.changeShaderBitmap();
                SplashActivity.splashView.coloring = -2;
            }
        });
        findViewById(R.id.linearLayoutReset).setOnClickListener(new View.OnClickListener() { // from class: com.gallery.photos.editphotovideo.activities.SplashActivity$$ExternalSyntheticLambda1
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                SplashActivity.this.m279xdd43758b(view);
            }
        });
        findViewById(R.id.imageViewColorPicker).setOnClickListener(new View.OnClickListener() { // from class: com.gallery.photos.editphotovideo.activities.SplashActivity.7
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                SplashActivity splashActivity = SplashActivity.this;
                new AmbilWarnaDialog(splashActivity, splashActivity.getResources().getColor(R.color.mainColor), true, new AmbilWarnaDialog.OnAmbilWarnaListener() { // from class: com.gallery.photos.editphotovideo.activities.SplashActivity.7.1
                    @Override // yuku.ambilwarna.AmbilWarnaDialog.OnAmbilWarnaListener
                    public void onCancel(AmbilWarnaDialog ambilWarnaDialog) {
                    }

                    @Override // yuku.ambilwarna.AmbilWarnaDialog.OnAmbilWarnaListener
                    public void onOk(AmbilWarnaDialog ambilWarnaDialog, int i) {
                        SplashActivity.this.updateView(i);
                    }
                }).show();
            }
        });
        findViewById(R.id.linearLayoutFit).setOnClickListener(new View.OnClickListener() { // from class: com.gallery.photos.editphotovideo.activities.SplashActivity.8
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                SplashView splashView2 = SplashActivity.splashView;
                splashView2.saveScale = 1.0f;
                splashView2.radius = (SplashActivity.seekBarSize.getProgress() + 10) / SplashActivity.splashView.saveScale;
                SplashActivity.splashView.fitScreen();
                SplashActivity.splashView.updatePreviewPaint();
            }
        });
        findViewById(R.id.linearLayoutZoom).setOnClickListener(new View.OnClickListener() { // from class: com.gallery.photos.editphotovideo.activities.SplashActivity.9
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                SplashActivity.this.imageViewManual.setColorFilter(SplashActivity.this.getResources().getColor(R.color.iconColor));
                SplashActivity.this.imageViewColor.setColorFilter(SplashActivity.this.getResources().getColor(R.color.iconColor));
                SplashActivity.this.imageViewGray.setColorFilter(SplashActivity.this.getResources().getColor(R.color.iconColor));
                SplashActivity.this.imageViewZoom.setColorFilter(SplashActivity.this.getResources().getColor(R.color.mainColor));
                SplashActivity.this.textViewManual.setTextColor(SplashActivity.this.getResources().getColor(R.color.iconColor));
                SplashActivity.this.textViewColor.setTextColor(SplashActivity.this.getResources().getColor(R.color.iconColor));
                SplashActivity.this.textViewGray.setTextColor(SplashActivity.this.getResources().getColor(R.color.iconColor));
                SplashActivity.this.textViewZoom.setTextColor(SplashActivity.this.getResources().getColor(R.color.mainColor));
                SplashActivity.splashView.mode = 1;
            }
        });
    }

    /* renamed from: lambda$onCreate$1$com-artRoom-photo-editor-activities-SplashActivity, reason: not valid java name */
      void m279xdd43758b(View view) {
        this.imageViewManual.setColorFilter(getResources().getColor(R.color.iconColor));
        this.imageViewColor.setColorFilter(getResources().getColor(R.color.mainColor));
        this.imageViewGray.setColorFilter(getResources().getColor(R.color.iconColor));
        this.imageViewZoom.setColorFilter(getResources().getColor(R.color.iconColor));
        this.textViewManual.setTextColor(getResources().getColor(R.color.iconColor));
        this.textViewColor.setTextColor(getResources().getColor(R.color.mainColor));
        this.textViewGray.setTextColor(getResources().getColor(R.color.iconColor));
        this.textViewZoom.setTextColor(getResources().getColor(R.color.iconColor));
        this.linearLayoutColor.setVisibility(View.GONE);
        grayBitmap = grayScaleBitmap(colorBitmap);
        splashView.initDrawing();
        splashView.saveScale = 1.0f;
        splashView.fitScreen();
        splashView.mode = 0;
        splashView.updatePreviewPaint();
        splashView.updatePaintBrush();
        vector.clear();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateView(int i) {
        grayBitmap = grayScaleBitmap(colorBitmap);
        Canvas canvas = new Canvas(grayBitmap);
        Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(new float[]{((i >> 16) & 255) / 256.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, ((i >> 8) & 255) / 256.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, (i & 255) / 256.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, ((i >> 24) & 255) / 256.0f, 0.0f}));
        canvas.drawBitmap(grayBitmap, 0.0f, 0.0f, paint);
        splashView.splashBitmap = grayBitmap;
        splashView.updateRefMetrix();
        splashView.changeShaderBitmap();
        splashView.coloring = i;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void SaveView() {
        if (splashView.drawingBitmap != null) {
            BitmapTransfer.setBitmap(splashView.drawingBitmap);
        }
        Intent intent = new Intent(this, (Class<?>) PhotoEditorActivity.class);
        intent.putExtra("MESSAGE", "done");
        setResult(-1, intent);
        finish();
    }

    @Override // androidx.activity.ComponentActivity, android.app.Activity
    public void onBackPressed() {
        super.onBackPressed();
    }

    public Bitmap grayScaleBitmap(Bitmap bitmap) {
        if (bitmap == null || bitmap.isRecycled()) {
            return null;
        }

        try {
            Bitmap createBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(createBitmap);
            Paint paint = new Paint();
            ColorMatrix colorMatrix = new ColorMatrix();
            colorMatrix.setSaturation(0.0f);
            paint.setColorFilter(new ColorMatrixColorFilter(colorMatrix));
            canvas.drawBitmap(bitmap, 0.0f, 0.0f, paint);
            return createBitmap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override // android.widget.SeekBar.OnSeekBarChangeListener
    public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
        int id = seekBar.getId();
        if (id == R.id.seekBarOpacity) {
            SplashBrushView splashBrushView = brushView;
            splashBrushView.isBrushSize = false;
            splashBrushView.setShapeRadiusRatio(splashView.radius);
            brushView.brushSize.setPaintOpacity(seekBarOpacity.getProgress());
            brushView.invalidate();
            SplashView splashView2 = splashView;
            splashView2.opacity = i + 15;
            splashView2.updatePaintBrush();
            return;
        }
        if (id != R.id.seekBarSize) {
            if (id == R.id.seekBarOffset) {
                Bitmap.createBitmap(300, 300, Bitmap.Config.ARGB_8888).copy(Bitmap.Config.ARGB_8888, true);
                return;
            }
            return;
        }
        Log.wtf("radious :", seekBarSize.getProgress() + "");
        splashView.radius = ((float) (seekBarSize.getProgress() + 10)) / splashView.saveScale;
        splashView.updatePaintBrush();
    }

    @Override
    // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, android.app.Activity
    protected void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (i2 == -1 && i == 2) {
            String str = this.selectedOutputPath;
            this.selectedImagePath = str;
            if (SupportedClass.stringIsNotEmpty(str)) {
                File file = new File(this.selectedImagePath);
                if (file.exists()) {
                    if (Build.VERSION.SDK_INT < 24) {
                        this.selectedImageUri = Uri.fromFile(file);
                    } else {
                        this.selectedImageUri = FileProvider.getUriForFile(this, "com.gallery.photos.editphotovideo.provider", file);
                    }
                    onPhotoTakenApp();
                    return;
                }
                return;
            }
            return;
        }
        if (intent != null && intent.getData() != null) {
            if (i == 3) {
                this.selectedImageUri = intent.getData();
            } else {
                this.selectedImagePath = this.selectedOutputPath;
            }
            if (SupportedClass.stringIsNotEmpty(this.selectedImagePath)) {
                onPhotoTakenApp();
                return;
            }
            return;
        }
        Log.e("TAG", "");
    }

    public void onPhotoTakenApp() {
        this.relativeLayoutContainer.post(new Runnable() {
            @Override
            public void run() {
                // Add null/recycled checks
                if (colorBitmap == null || colorBitmap.isRecycled()) {
                    return;
                }

                Bitmap newGrayBitmap = grayScaleBitmap(colorBitmap);
                if (newGrayBitmap != null) {
                    // Clean up old bitmap if exists
                    safeRecycle(grayBitmap);
                    grayBitmap = newGrayBitmap;

                    if (splashView != null) {
                        splashView.initDrawing();
                        splashView.saveScale = 1.0f;
                        splashView.fitScreen();
                        splashView.updatePreviewPaint();
                        splashView.updatePaintBrush();
                    }

                    if (vector != null) {
                        vector.clear();
                    }
                }
            }
        });
    }

    private void recycleBitmaps() {
        safeRecycle(colorBitmap);
        safeRecycle(grayBitmap);
        // Add any other bitmaps you use
    }

    private void safeRecycle(Bitmap bitmap) {
        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
        }
    }

    private boolean isConnectedNetwork() {
        boolean z = false;
        boolean z2 = false;
        for (NetworkInfo networkInfo : ((ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE)).getAllNetworkInfo()) {
            if (networkInfo.getTypeName().equalsIgnoreCase("WIFI") && networkInfo.isConnected()) {
                z2 = true;
            }
            if (networkInfo.getTypeName().equalsIgnoreCase("MOBILE") && networkInfo.isConnected()) {
                z = true;
            }
        }
        return z || z2;
    }

    @Override // android.widget.SeekBar.OnSeekBarChangeListener
    public void onStartTrackingTouch(SeekBar seekBar) {
        if (seekBar.getId() != R.id.seekBarOffset) {
            Bitmap.createBitmap(300, 300, Bitmap.Config.ARGB_8888).copy(Bitmap.Config.ARGB_8888, true);
        }
    }

    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onResume() {
        super.onResume();
    }

    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onPause() {
        super.onPause();
    }

    @Override
    // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onDestroy() {
        super.onDestroy();
        recycleBitmaps();
    }


}
