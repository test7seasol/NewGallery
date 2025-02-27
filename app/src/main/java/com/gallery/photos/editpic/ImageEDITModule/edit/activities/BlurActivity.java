package com.gallery.photos.editpic.ImageEDITModule.edit.activities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.view.Display;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.gallery.photos.editpic.ImageEDITModule.edit.draw.BlurBrushView;
import com.gallery.photos.editpic.ImageEDITModule.edit.draw.BlurView;
import com.gallery.photos.editpic.ImageEDITModule.edit.utils.BitmapTransfer;
import com.gallery.photos.editpic.ImageEDITModule.github.siyamed.shapeimageview.RoundedImageView;
import com.gallery.photos.editpic.R;

/* loaded from: classes.dex */
public class BlurActivity extends BaseActivity implements SeekBar.OnSeekBarChangeListener {
    public static Bitmap bitmapBlur;
    public static Bitmap bitmapClear;
    public static BlurView blurView;
    public static BlurBrushView brushView;
    static int displayHight;
    public static int displayWidth;
    public static RoundedImageView prView;
    public static SeekBar seekBarBlur;
    public static SeekBar seekBarOffset;
    public static SeekBar seekBarSize;
    private boolean erase;
    private ImageView imageViewBlur;
    RelativeLayout imageViewContainer;
    private ImageView imageViewEraser;
    private ImageView imageViewZoom;
    private LinearLayout linearLayoutBlur;
    private LinearLayout linearLayoutEraser;
    private LinearLayout linearLayoutZoom;
    private ProgressDialog progressBlurring;
    private int startBlurSeekbarPosition;
    private TextView textViewBlur;
    private TextView textViewEraser;
    private TextView textViewZoom;



    public static Bitmap blur(Context context, Bitmap bitmap, int radius) {
        // Ensure the bitmap is valid before copying
        if (bitmap == null || bitmap.isRecycled()) {
            throw new IllegalStateException("Bitmap is null or already recycled");
        }

        Bitmap copy = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        Bitmap createBitmap = Bitmap.createBitmap(copy);

        RenderScript rs = RenderScript.create(context);
        ScriptIntrinsicBlur blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));

        Allocation input = Allocation.createFromBitmap(rs, copy);
        Allocation output = Allocation.createFromBitmap(rs, createBitmap);

        blurScript.setRadius(radius);
        blurScript.setInput(input);
        blurScript.forEach(output);

        output.copyTo(createBitmap);

        // Clean up RenderScript resources
        input.destroy();
        output.destroy();
        blurScript.destroy();
        rs.destroy();

        return createBitmap;
    }

    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        requestWindowFeature(1);
        setContentView(R.layout.activity_blur);
        getWindow().setFlags(1024, 1024);
        Display defaultDisplay = getWindowManager().getDefaultDisplay();
        Point point = new Point();
        defaultDisplay.getSize(point);

        displayWidth = point.x;
        displayHight = point.y;
        this.imageViewContainer = (RelativeLayout) findViewById(R.id.relativeLayoutContainer);
        blurView = (BlurView) findViewById(R.id.drawingImageView);
        if (BitmapTransfer.bitmap != null) {
            bitmapClear = BitmapTransfer.bitmap;
        }
        bitmapBlur = blur(this, bitmapClear, blurView.opacity);
        this.linearLayoutEraser = (LinearLayout) findViewById(R.id.linearLayoutEraser);
        this.linearLayoutBlur = (LinearLayout) findViewById(R.id.linearLayoutBlur);
        this.linearLayoutZoom = (LinearLayout) findViewById(R.id.linearLayoutZoom);
        this.textViewEraser = (TextView) findViewById(R.id.textViewEraser);
        this.textViewBlur = (TextView) findViewById(R.id.textViewBlur);
        this.textViewZoom = (TextView) findViewById(R.id.textViewZoom);
        this.imageViewEraser = (ImageView) findViewById(R.id.imageViewEraser);
        this.imageViewBlur = (ImageView) findViewById(R.id.imageViewBlur);
        this.imageViewZoom = (ImageView) findViewById(R.id.imageViewZoom);
        seekBarSize = (SeekBar) findViewById(R.id.seekBarSize);
        seekBarBlur = (SeekBar) findViewById(R.id.seekBarBlur);
        prView = (RoundedImageView) findViewById(R.id.preview);
        seekBarOffset = (SeekBar) findViewById(R.id.seekBarOffset);
        BlurBrushView blurBrushView = (BlurBrushView) findViewById(R.id.brushView);
        brushView = blurBrushView;
        blurBrushView.setShapeRadiusRatio(seekBarSize.getProgress() / seekBarSize.getMax());
        seekBarSize.setProgress((int) blurView.radius);
        seekBarBlur.setProgress(blurView.opacity);
        new Canvas(Bitmap.createBitmap(300, 300, Bitmap.Config.ARGB_8888).copy(Bitmap.Config.ARGB_8888, true));
        seekBarSize.setOnSeekBarChangeListener(this);
        seekBarBlur.setOnSeekBarChangeListener(this);
        seekBarOffset.setOnSeekBarChangeListener(this);
        blurView.initDrawing();
        this.progressBlurring = new ProgressDialog(this);
        findViewById(R.id.imageViewSaveBlur).setOnClickListener(new View.OnClickListener() { // from class: com.gallery.photos.editphotovideo.activities.BlurActivity.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {

                    BlurActivity.this.SaveView();

            }
        });
        findViewById(R.id.imageViewCloseBlur).setOnClickListener(new View.OnClickListener() { // from class: com.gallery.photos.editphotovideo.activities.BlurActivity.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                BlurActivity.this.onBackPressed();
                BlurActivity.this.finish();
            }
        });
        findViewById(R.id.linearLayoutEraser).setOnClickListener(new View.OnClickListener() { // from class: com.gallery.photos.editphotovideo.activities.BlurActivity.3
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                BlurActivity.this.imageViewEraser.setColorFilter(BlurActivity.this.getResources().getColor(R.color.mainColor));
                BlurActivity.this.imageViewBlur.setColorFilter(BlurActivity.this.getResources().getColor(R.color.iconColor));
                BlurActivity.this.imageViewZoom.setColorFilter(BlurActivity.this.getResources().getColor(R.color.iconColor));
                BlurActivity.this.textViewEraser.setTextColor(BlurActivity.this.getResources().getColor(R.color.mainColor));
                BlurActivity.this.textViewBlur.setTextColor(BlurActivity.this.getResources().getColor(R.color.iconColor));
                BlurActivity.this.textViewZoom.setTextColor(BlurActivity.this.getResources().getColor(R.color.iconColor));
                BlurActivity.this.erase = true;
                BlurActivity.blurView.mode = 0;
                BlurView blurView2 = BlurActivity.blurView;
                blurView2.splashBitmap = BlurActivity.bitmapClear;
                blurView2.updateRefMetrix();
                BlurActivity.blurView.changeShaderBitmap();
                BlurActivity.blurView.coloring = true;
            }
        });
        findViewById(R.id.linearLayoutBlur).setOnClickListener(new View.OnClickListener() { // from class: com.gallery.photos.editphotovideo.activities.BlurActivity.4
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                BlurActivity.this.imageViewEraser.setColorFilter(BlurActivity.this.getResources().getColor(R.color.iconColor));
                BlurActivity.this.imageViewBlur.setColorFilter(BlurActivity.this.getResources().getColor(R.color.mainColor));
                BlurActivity.this.imageViewZoom.setColorFilter(BlurActivity.this.getResources().getColor(R.color.iconColor));
                BlurActivity.this.textViewEraser.setTextColor(BlurActivity.this.getResources().getColor(R.color.iconColor));
                BlurActivity.this.textViewBlur.setTextColor(BlurActivity.this.getResources().getColor(R.color.mainColor));
                BlurActivity.this.textViewZoom.setTextColor(BlurActivity.this.getResources().getColor(R.color.iconColor));
                BlurActivity.this.erase = false;
                BlurActivity.blurView.mode = 0;
                BlurView blurView2 = BlurActivity.blurView;
                blurView2.splashBitmap = BlurActivity.bitmapBlur;
                blurView2.updateRefMetrix();
                BlurActivity.blurView.changeShaderBitmap();
                BlurActivity.blurView.coloring = false;
            }
        });
        findViewById(R.id.linearLayoutReset).setOnClickListener(new View.OnClickListener() { // from class: com.gallery.photos.editphotovideo.activities.BlurActivity.5
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                BlurActivity.this.imageViewEraser.setColorFilter(BlurActivity.this.getResources().getColor(R.color.mainColor));
                BlurActivity.this.imageViewBlur.setColorFilter(BlurActivity.this.getResources().getColor(R.color.iconColor));
                BlurActivity.this.imageViewZoom.setColorFilter(BlurActivity.this.getResources().getColor(R.color.iconColor));
                BlurActivity.this.textViewEraser.setTextColor(BlurActivity.this.getResources().getColor(R.color.mainColor));
                BlurActivity.this.textViewBlur.setTextColor(BlurActivity.this.getResources().getColor(R.color.iconColor));
                BlurActivity.this.textViewZoom.setTextColor(BlurActivity.this.getResources().getColor(R.color.iconColor));
                BlurActivity.blurView.initDrawing();
                BlurActivity.blurView.saveScale = 1.0f;
                BlurActivity.blurView.fitScreen();
                BlurActivity.blurView.updatePreviewPaint();
                BlurActivity.blurView.updatePaintBrush();
            }
        });
        findViewById(R.id.linearLayoutFit).setOnClickListener(new View.OnClickListener() { // from class: com.gallery.photos.editphotovideo.activities.BlurActivity.6
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                BlurView blurView2 = BlurActivity.blurView;
                blurView2.saveScale = 1.0f;
                blurView2.radius = (BlurActivity.seekBarSize.getProgress() + 10) / BlurActivity.blurView.saveScale;
                BlurActivity.brushView.setShapeRadiusRatio((BlurActivity.seekBarSize.getProgress() + 10) / BlurActivity.blurView.saveScale);
                BlurActivity.blurView.fitScreen();
                BlurActivity.blurView.updatePreviewPaint();
            }
        });
        findViewById(R.id.linearLayoutZoom).setOnClickListener(new View.OnClickListener() { // from class: com.gallery.photos.editphotovideo.activities.BlurActivity.7
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                BlurActivity.blurView.mode = 1;
                BlurActivity.this.imageViewEraser.setColorFilter(BlurActivity.this.getResources().getColor(R.color.iconColor));
                BlurActivity.this.imageViewBlur.setColorFilter(BlurActivity.this.getResources().getColor(R.color.iconColor));
                BlurActivity.this.imageViewZoom.setColorFilter(BlurActivity.this.getResources().getColor(R.color.mainColor));
                BlurActivity.this.textViewEraser.setTextColor(BlurActivity.this.getResources().getColor(R.color.iconColor));
                BlurActivity.this.textViewBlur.setTextColor(BlurActivity.this.getResources().getColor(R.color.iconColor));
                BlurActivity.this.textViewZoom.setTextColor(BlurActivity.this.getResources().getColor(R.color.mainColor));
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void SaveView() {
        if (blurView.drawingBitmap != null) {
            BitmapTransfer.bitmap = blurView.drawingBitmap;
        }
        Intent intent = new Intent(this, (Class<?>) PhotoEditorActivity.class);
        intent.putExtra("MESSAGE", "done");
        setResult(-1, intent);
        finish();
    }

    @Override // android.widget.SeekBar.OnSeekBarChangeListener
    public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
        int id = seekBar.getId();
        if (id == R.id.seekBarBlur) {
            BlurBrushView blurBrushView = brushView;
            blurBrushView.isBrushSize = false;
            blurBrushView.setShapeRadiusRatio(blurView.radius);
            brushView.brushSize.setPaintOpacity(seekBarBlur.getProgress());
            brushView.invalidate();
            BlurView blurView2 = blurView;
            blurView2.opacity = i + 1;
            blurView2.updatePaintBrush();
            return;
        }
        if (id != R.id.seekBarSize) {
            if (id == R.id.seekBarOffset) {
                Bitmap.createBitmap(300, 300, Bitmap.Config.ARGB_8888).copy(Bitmap.Config.ARGB_8888, true);
                return;
            }
            return;
        }
        BlurBrushView blurBrushView2 = brushView;
        blurBrushView2.isBrushSize = true;
        blurBrushView2.brushSize.setPaintOpacity(255);
        brushView.setShapeRadiusRatio((seekBarSize.getProgress() + 10) / blurView.saveScale);
        brushView.invalidate();
        blurView.radius = (seekBarSize.getProgress() + 10) / blurView.saveScale;
        blurView.updatePaintBrush();
    }

    @Override // android.widget.SeekBar.OnSeekBarChangeListener
    public void onStartTrackingTouch(SeekBar seekBar) {
        int id = seekBar.getId();
        if (id == R.id.seekBarBlur) {
            this.startBlurSeekbarPosition = seekBarBlur.getProgress();
        } else if (id == R.id.seekBarOffset) {
            Bitmap.createBitmap(300, 300, Bitmap.Config.ARGB_8888).copy(Bitmap.Config.ARGB_8888, true);
        }
    }

    @Override // android.widget.SeekBar.OnSeekBarChangeListener
    public void onStopTrackingTouch(SeekBar seekBar) {
        if (seekBar.getId() == R.id.seekBarBlur) {
            final Dialog dialog = new Dialog(this, R.style.UploadDialog);
            dialog.requestWindowFeature(1);
            dialog.setContentView(R.layout.dialog_warning);
            dialog.setCancelable(true);
            dialog.show();
            TextView textView = (TextView) dialog.findViewById(R.id.textViewCancel);
            ((TextView) dialog.findViewById(R.id.textViewContinue)).setOnClickListener(new View.OnClickListener() { // from class: com.gallery.photos.editphotovideo.activities.BlurActivity$$ExternalSyntheticLambda1
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    BlurActivity.this.m248x975c2219(dialog, view);
                }
            });
            textView.setOnClickListener(new View.OnClickListener() { // from class: com.gallery.photos.editphotovideo.activities.BlurActivity$$ExternalSyntheticLambda2
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    BlurActivity.this.m249x8905c838(dialog, view);
                }
            });
            return;
        }
        seekBar.getId();
    }

    /* renamed from: lambda$onStopTrackingTouch$1$com-artRoom-photo-editor-activities-BlurActivity, reason: not valid java name */
      void m248x975c2219(Dialog dialog, View view) {
        new BlurUpdater().execute(new String[0]);
        dialog.dismiss();
    }

    /* renamed from: lambda$onStopTrackingTouch$2$com-artRoom-photo-editor-activities-BlurActivity, reason: not valid java name */
      void m249x8905c838(Dialog dialog, View view) {
        seekBarBlur.setProgress(this.startBlurSeekbarPosition);
        dialog.dismiss();
    }

    private class BlurUpdater extends AsyncTask<String, Integer, Bitmap> {
        private BlurUpdater() {
        }

        @Override // android.os.AsyncTask
        public void onPreExecute() {
            super.onPreExecute();
            BlurActivity.this.progressBlurring.setMessage("Blurring...");
            BlurActivity.this.progressBlurring.setIndeterminate(true);
            BlurActivity.this.progressBlurring.setCancelable(false);
            BlurActivity.this.progressBlurring.show();
        }

        @Override // android.os.AsyncTask
        public Bitmap doInBackground(String... strArr) {
            BlurActivity.bitmapBlur = BlurActivity.blur(BlurActivity.this.getApplicationContext(), BlurActivity.bitmapClear, BlurActivity.blurView.opacity);
            return BlurActivity.bitmapBlur;
        }

        @Override // android.os.AsyncTask
        public void onProgressUpdate(Integer... numArr) {
            super.onProgressUpdate(numArr);
        }

        @Override // android.os.AsyncTask
        public void onPostExecute(Bitmap bitmap) {
            super.onPostExecute( bitmap);
            if (!BlurActivity.this.erase) {
                BlurActivity.blurView.splashBitmap = BlurActivity.bitmapBlur;
                BlurActivity.blurView.updateRefMetrix();
                BlurActivity.blurView.changeShaderBitmap();
            }
            BlurActivity.blurView.initDrawing();
            BlurActivity.blurView.saveScale = 1.0f;
            BlurActivity.blurView.fitScreen();
            BlurActivity.blurView.updatePreviewPaint();
            BlurActivity.blurView.updatePaintBrush();
            if (BlurActivity.this.progressBlurring.isShowing()) {
                BlurActivity.this.progressBlurring.dismiss();
            }
        }
    }


}
