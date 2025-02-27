package com.gallery.photos.editpic.ImageEDITModule.edit.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.ViewFlipper;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.gallery.photos.editpic.ImageEDITModule.edit.constants.StoreManager;
import com.gallery.photos.editpic.ImageEDITModule.edit.mirror.MirrorImageMode;
import com.gallery.photos.editpic.ImageEDITModule.edit.mirror.Utils;
import com.gallery.photos.editpic.ImageEDITModule.edit.utils.BitmapTransfer;
import com.gallery.photos.editpic.ImageEDITModule.github.siyamed.shapeimageview.RoundedImageView;
import com.gallery.photos.editpic.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/* loaded from: classes.dex */
public class MirrorActivity extends AppCompatActivity {
    private static final String TAG = "MirrorImageActivity";
    RoundedImageView[] d3ButtonArray;
    Bitmap filterBitmap;
    RelativeLayout mainLayout;
    RoundedImageView[] mirrorButtonArray;
    MirrorView mirrorView;
    AlertDialog saveImageAlert;
    int screenHeightPixels;
    int screenWidthPixels;
    private Animation slideLeftIn;
    private Animation slideLeftOut;
    private Animation slideRightIn;
    private Animation slideRightOut;
    Bitmap sourceBitmap;
    View[] tabButtonList;
    ViewFlipper viewFlipper;
    int D3_BUTTON_SIZE = 24;
    int MIRROR_BUTTON_SIZE = 15;
    int currentSelectedTabIndex = -1;
    private int[] d3resList = {R.drawable.mirror_3d_14, R.drawable.mirror_3d_14, R.drawable.mirror_3d_10, R.drawable.mirror_3d_10, R.drawable.mirror_3d_11, R.drawable.mirror_3d_11, R.drawable.mirror_3d_4, R.drawable.mirror_3d_4, R.drawable.mirror_3d_3, R.drawable.mirror_3d_3, R.drawable.mirror_3d_1, R.drawable.mirror_3d_1, R.drawable.mirror_3d_6, R.drawable.mirror_3d_6, R.drawable.mirror_3d_13, R.drawable.mirror_3d_13, R.drawable.mirror_3d_15, R.drawable.mirror_3d_15, R.drawable.mirror_3d_15, R.drawable.mirror_3d_15, R.drawable.mirror_3d_16, R.drawable.mirror_3d_16, R.drawable.mirror_3d_16, R.drawable.mirror_3d_16};
    int initialYPos = 0;
    Matrix matrix1 = new Matrix();
    Matrix matrix2 = new Matrix();
    Matrix matrix3 = new Matrix();
    Matrix matrix4 = new Matrix();
    float mulX = 16.0f;
    float mulY = 16.0f;

    class MirrorView extends View {
        int currentModeIndex;
        Bitmap d3Bitmap;
        boolean d3Mode;
        int defaultColor;
        RectF destRect1;
        RectF destRect1X;
        RectF destRect1Y;
        RectF destRect2;
        RectF destRect2X;
        RectF destRect2Y;
        RectF destRect3;
        RectF destRect4;
        boolean drawSavedImage;
        RectF dstRectPaper1;
        RectF dstRectPaper2;
        RectF dstRectPaper3;
        RectF dstRectPaper4;
        Matrix f2071m1;
        Matrix f2072m2;
        Matrix f2073m3;
        final Matrix f510I;
        Bitmap frameBitmap;
        Paint framePaint;
        int height;
        boolean isTouchStartedLeft;
        boolean isTouchStartedTop;
        boolean isVerticle;
        MirrorImageMode[] mirrorModeList;
        MirrorImageMode modeX;
        MirrorImageMode modeX10;
        MirrorImageMode modeX11;
        MirrorImageMode modeX12;
        MirrorImageMode modeX13;
        MirrorImageMode modeX14;
        MirrorImageMode modeX15;
        MirrorImageMode modeX16;
        MirrorImageMode modeX17;
        MirrorImageMode modeX18;
        MirrorImageMode modeX19;
        MirrorImageMode modeX2;
        MirrorImageMode modeX20;
        MirrorImageMode modeX3;
        MirrorImageMode modeX4;
        MirrorImageMode modeX5;
        MirrorImageMode modeX6;
        MirrorImageMode modeX7;
        MirrorImageMode modeX8;
        MirrorImageMode modeX9;
        float oldX;
        float oldY;
        RectF srcRect1;
        RectF srcRect2;
        RectF srcRect3;
        RectF srcRectPaper;
        int tMode1;
        int tMode2;
        int tMode3;
        Matrix textMatrix;
        Paint textRectPaint;
        RectF totalArea1;
        RectF totalArea2;
        RectF totalArea3;
        int width;

        public MirrorView(Context context, int i, int i2) {
            super(context);
            this.currentModeIndex = 0;
            this.d3Mode = false;
            this.defaultColor = R.color.BackgroundCardColor;
            this.drawSavedImage = false;
            this.f510I = new Matrix();
            this.framePaint = new Paint();
            this.isVerticle = false;
            this.f2071m1 = new Matrix();
            this.f2072m2 = new Matrix();
            this.f2073m3 = new Matrix();
            this.mirrorModeList = new MirrorImageMode[20];
            this.textMatrix = new Matrix();
            this.textRectPaint = new Paint(1);
            this.width = MirrorActivity.this.sourceBitmap.getWidth();
            this.height = MirrorActivity.this.sourceBitmap.getHeight();
            createMatrix(i, i2);
            createRectX(i, i2);
            createRectY(i, i2);
            createRectXY(i, i2);
            createModes();
            this.framePaint.setAntiAlias(true);
            this.framePaint.setFilterBitmap(true);
            this.framePaint.setDither(true);
            this.textRectPaint.setColor(getResources().getColor(R.color.BackgroundCardColor));
        }

        public void reset(int i, int i2, boolean z) {
            createMatrix(i, i2);
            createRectX(i, i2);
            createRectY(i, i2);
            createRectXY(i, i2);
            createModes();
            if (z) {
                postInvalidate();
            }
        }

        public String saveBitmap(boolean z, int i, int i2) {
            Bitmap bitmap;
            float maxSizeForSave = Utils.maxSizeForSave();
            float min = maxSizeForSave / Math.min(i, i2);
            Log.e(MirrorActivity.TAG, "upperScale" + maxSizeForSave);
            Log.e(MirrorActivity.TAG, "scale" + min);
            if (MirrorActivity.this.mulY > MirrorActivity.this.mulX) {
                float f = MirrorActivity.this.mulX;
                min = (min * 1.0f) / MirrorActivity.this.mulY;
            }
            float f2 = min > 0.0f ? min : 1.0f;
            Log.e(MirrorActivity.TAG, "scale" + f2);
            int round = Math.round(((float) i) * f2);
            int round2 = Math.round(((float) i2) * f2);
            RectF srcRect = this.mirrorModeList[this.currentModeIndex].getSrcRect();
            reset(round, round2, false);
            int round3 = Math.round(MirrorActivity.this.mirrorView.getCurrentMirrorMode().rectTotalArea.width());
            int round4 = Math.round(MirrorActivity.this.mirrorView.getCurrentMirrorMode().rectTotalArea.height());
            if (round3 % 2 == 1) {
                round3--;
            }
            if (round4 % 2 == 1) {
                round4--;
            }
            Bitmap createBitmap = Bitmap.createBitmap(round3, round4, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(createBitmap);
            Matrix matrix = new Matrix();
            matrix.reset();
            Log.e(MirrorActivity.TAG, "btmWidth " + round3);
            Log.e(MirrorActivity.TAG, "btmHeight " + round4);
            matrix.postTranslate(((float) (-(round - round3))) / 2.0f, ((float) (-(round2 - round4))) / 2.0f);
            MirrorImageMode mirrorImageMode = this.mirrorModeList[this.currentModeIndex];
            mirrorImageMode.setSrcRect(srcRect);
            if (MirrorActivity.this.filterBitmap == null) {
                drawMode(canvas, MirrorActivity.this.sourceBitmap, mirrorImageMode, matrix);
            } else {
                drawMode(canvas, MirrorActivity.this.filterBitmap, mirrorImageMode, matrix);
            }
            String str = null;
            if (this.d3Mode && (bitmap = this.d3Bitmap) != null && !bitmap.isRecycled()) {
                canvas.setMatrix(matrix);
                canvas.drawBitmap(this.d3Bitmap, (Rect) null, this.mirrorModeList[this.currentModeIndex].rectTotalArea, this.framePaint);
            }
            Bitmap bitmap2 = this.frameBitmap;
            if (bitmap2 != null && !bitmap2.isRecycled()) {
                canvas.setMatrix(matrix);
                canvas.drawBitmap(this.frameBitmap, (Rect) null, this.mirrorModeList[this.currentModeIndex].rectTotalArea, this.framePaint);
            }
            if (z) {
                str = StoreManager.getWorkspaceDirPathWithSeparator(getContext()) + System.currentTimeMillis() + ".jpg";
                new File(str).getParentFile().mkdirs();
                try {
                    FileOutputStream fileOutputStream = new FileOutputStream(str);
                    createBitmap.compress(Bitmap.CompressFormat.JPEG, 90, fileOutputStream);
                    fileOutputStream.flush();
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            createBitmap.recycle();
            reset(i, i2, false);
            this.mirrorModeList[this.currentModeIndex].setSrcRect(srcRect);
            return str;
        }

        public void setCurrentMode(int i) {
            this.currentModeIndex = i;
        }

        public MirrorImageMode getCurrentMirrorMode() {
            return this.mirrorModeList[this.currentModeIndex];
        }

        private void createModes() {
            RectF rectF = this.srcRect3;
            RectF rectF2 = this.destRect1;
            RectF rectF3 = this.destRect3;
            this.modeX = new MirrorImageMode(4, rectF, rectF2, rectF2, rectF3, rectF3, MirrorActivity.this.matrix1, this.f510I, MirrorActivity.this.matrix1, this.tMode3, this.totalArea3);
            RectF rectF4 = this.srcRect3;
            RectF rectF5 = this.destRect1;
            RectF rectF6 = this.destRect4;
            this.modeX2 = new MirrorImageMode(4, rectF4, rectF5, rectF6, rectF5, rectF6, MirrorActivity.this.matrix1, MirrorActivity.this.matrix1, this.f510I, this.tMode3, this.totalArea3);
            RectF rectF7 = this.srcRect3;
            RectF rectF8 = this.destRect3;
            RectF rectF9 = this.destRect2;
            this.modeX3 = new MirrorImageMode(4, rectF7, rectF8, rectF9, rectF8, rectF9, MirrorActivity.this.matrix1, MirrorActivity.this.matrix1, this.f510I, this.tMode3, this.totalArea3);
            RectF rectF10 = this.srcRect3;
            RectF rectF11 = this.destRect1;
            this.modeX8 = new MirrorImageMode(4, rectF10, rectF11, rectF11, rectF11, rectF11, MirrorActivity.this.matrix1, MirrorActivity.this.matrix2, MirrorActivity.this.matrix3, this.tMode3, this.totalArea3);
            int i = this.tMode3 == 0 ? 0 : 4;
            RectF rectF12 = this.srcRect3;
            RectF rectF13 = this.destRect2;
            this.modeX9 = new MirrorImageMode(4, rectF12, rectF13, rectF13, rectF13, rectF13, MirrorActivity.this.matrix1, MirrorActivity.this.matrix2, MirrorActivity.this.matrix3, i, this.totalArea3);
            int i2 = this.tMode3 == 1 ? 1 : 3;
            RectF rectF14 = this.srcRect3;
            RectF rectF15 = this.destRect3;
            this.modeX10 = new MirrorImageMode(4, rectF14, rectF15, rectF15, rectF15, rectF15, MirrorActivity.this.matrix1, MirrorActivity.this.matrix2, MirrorActivity.this.matrix3, i2, this.totalArea3);
            int i3 = this.tMode3 == 0 ? 3 : 4;
            RectF rectF16 = this.srcRect3;
            RectF rectF17 = this.destRect4;
            this.modeX11 = new MirrorImageMode(4, rectF16, rectF17, rectF17, rectF17, rectF17, MirrorActivity.this.matrix1, MirrorActivity.this.matrix2, MirrorActivity.this.matrix3, i3, this.totalArea3);
            RectF rectF18 = this.srcRect1;
            RectF rectF19 = this.destRect1X;
            this.modeX4 = new MirrorImageMode(2, rectF18, rectF19, rectF19, MirrorActivity.this.matrix1, this.tMode1, this.totalArea1);
            int i4 = this.tMode1;
            int i5 = i4 == 0 ? 0 : i4 == 5 ? 5 : 4;
            RectF rectF20 = this.srcRect1;
            RectF rectF21 = this.destRect2X;
            this.modeX5 = new MirrorImageMode(2, rectF20, rectF21, rectF21, MirrorActivity.this.matrix1, i5, this.totalArea1);
            RectF rectF22 = this.srcRect2;
            RectF rectF23 = this.destRect1Y;
            this.modeX6 = new MirrorImageMode(2, rectF22, rectF23, rectF23, MirrorActivity.this.matrix2, this.tMode2, this.totalArea2);
            int i6 = this.tMode2;
            int i7 = i6 == 1 ? 1 : i6 == 6 ? 6 : 3;
            RectF rectF24 = this.srcRect2;
            RectF rectF25 = this.destRect2Y;
            this.modeX7 = new MirrorImageMode(2, rectF24, rectF25, rectF25, MirrorActivity.this.matrix2, i7, this.totalArea2);
            this.modeX12 = new MirrorImageMode(2, this.srcRect1, this.destRect1X, this.destRect2X, MirrorActivity.this.matrix4, this.tMode1, this.totalArea1);
            this.modeX13 = new MirrorImageMode(2, this.srcRect2, this.destRect1Y, this.destRect2Y, MirrorActivity.this.matrix4, this.tMode2, this.totalArea2);
            RectF rectF26 = this.srcRect1;
            RectF rectF27 = this.destRect1X;
            this.modeX14 = new MirrorImageMode(2, rectF26, rectF27, rectF27, MirrorActivity.this.matrix3, this.tMode1, this.totalArea1);
            RectF rectF28 = this.srcRect2;
            RectF rectF29 = this.destRect1Y;
            this.modeX15 = new MirrorImageMode(2, rectF28, rectF29, rectF29, MirrorActivity.this.matrix3, this.tMode2, this.totalArea2);
            this.modeX16 = new MirrorImageMode(4, this.srcRectPaper, this.dstRectPaper1, this.dstRectPaper2, this.dstRectPaper3, this.dstRectPaper4, MirrorActivity.this.matrix1, MirrorActivity.this.matrix1, this.f510I, this.tMode1, this.totalArea1);
            RectF rectF30 = this.srcRectPaper;
            RectF rectF31 = this.dstRectPaper1;
            RectF rectF32 = this.dstRectPaper3;
            this.modeX17 = new MirrorImageMode(4, rectF30, rectF31, rectF32, rectF32, rectF31, this.f510I, MirrorActivity.this.matrix1, MirrorActivity.this.matrix1, this.tMode1, this.totalArea1);
            RectF rectF33 = this.srcRectPaper;
            RectF rectF34 = this.dstRectPaper2;
            RectF rectF35 = this.dstRectPaper4;
            this.modeX18 = new MirrorImageMode(4, rectF33, rectF34, rectF35, rectF34, rectF35, this.f510I, MirrorActivity.this.matrix1, MirrorActivity.this.matrix1, this.tMode1, this.totalArea1);
            RectF rectF36 = this.srcRectPaper;
            RectF rectF37 = this.dstRectPaper1;
            RectF rectF38 = this.dstRectPaper2;
            this.modeX19 = new MirrorImageMode(4, rectF36, rectF37, rectF38, rectF38, rectF37, this.f510I, MirrorActivity.this.matrix1, MirrorActivity.this.matrix1, this.tMode1, this.totalArea1);
            RectF rectF39 = this.srcRectPaper;
            RectF rectF40 = this.dstRectPaper4;
            RectF rectF41 = this.dstRectPaper3;
            MirrorImageMode mirrorImageMode = new MirrorImageMode(4, rectF39, rectF40, rectF41, rectF41, rectF40, this.f510I, MirrorActivity.this.matrix1, MirrorActivity.this.matrix1, this.tMode1, this.totalArea1);
            this.modeX20 = mirrorImageMode;
            MirrorImageMode[] mirrorImageModeArr = this.mirrorModeList;
            mirrorImageModeArr[0] = this.modeX4;
            mirrorImageModeArr[1] = this.modeX5;
            mirrorImageModeArr[2] = this.modeX6;
            MirrorImageMode mirrorImageMode2 = this.modeX7;
            mirrorImageModeArr[3] = mirrorImageMode2;
            mirrorImageModeArr[4] = this.modeX8;
            mirrorImageModeArr[5] = this.modeX9;
            mirrorImageModeArr[6] = this.modeX10;
            mirrorImageModeArr[7] = this.modeX11;
            mirrorImageModeArr[8] = this.modeX12;
            mirrorImageModeArr[9] = this.modeX13;
            mirrorImageModeArr[10] = this.modeX14;
            mirrorImageModeArr[11] = this.modeX15;
            mirrorImageModeArr[12] = this.modeX;
            mirrorImageModeArr[13] = this.modeX2;
            mirrorImageModeArr[14] = this.modeX3;
            mirrorImageModeArr[15] = mirrorImageMode2;
            mirrorImageModeArr[16] = this.modeX17;
            mirrorImageModeArr[17] = this.modeX18;
            mirrorImageModeArr[18] = this.modeX19;
            mirrorImageModeArr[19] = mirrorImageMode;
        }

        public Bitmap getBitmap() {
            setDrawingCacheEnabled(true);
            buildDrawingCache();
            Bitmap createBitmap = Bitmap.createBitmap(getDrawingCache());
            setDrawingCacheEnabled(false);
            return createBitmap;
        }

        private void createMatrix(int i, int i2) {
            this.f510I.reset();
            MirrorActivity.this.matrix1.reset();
            MirrorActivity.this.matrix1.postScale(-1.0f, 1.0f);
            float f = i;
            MirrorActivity.this.matrix1.postTranslate(f, 0.0f);
            MirrorActivity.this.matrix2.reset();
            MirrorActivity.this.matrix2.postScale(1.0f, -1.0f);
            float f2 = i2;
            MirrorActivity.this.matrix2.postTranslate(0.0f, f2);
            MirrorActivity.this.matrix3.reset();
            MirrorActivity.this.matrix3.postScale(-1.0f, -1.0f);
            MirrorActivity.this.matrix3.postTranslate(f, f2);
        }

        private void createRectX(int i, int i2) {
            float f;
            float f2;
            float f3;
            float f4;
            float f5;
            float f6 = i;
            float f7 = (MirrorActivity.this.mulY / MirrorActivity.this.mulX) * f6;
            float f8 = f6 / 2.0f;
            int i3 = MirrorActivity.this.initialYPos;
            float f9 = i2;
            float f10 = 0.0f;
            if (f7 > f9) {
                f2 = ((MirrorActivity.this.mulX / MirrorActivity.this.mulY) * f9) / 2.0f;
                f3 = f8 - f2;
                f = f9;
            } else {
                f = f7;
                f2 = f8;
                f3 = 0.0f;
            }
            float f11 = MirrorActivity.this.initialYPos + ((f9 - f) / 2.0f);
            float f12 = this.width;
            float f13 = this.height;
            float f14 = f2 + f3;
            float f15 = f + f11;
            this.destRect1X = new RectF(f3, f11, f14, f15);
            float f16 = f2 + f14;
            this.destRect2X = new RectF(f14, f11, f16, f15);
            this.totalArea1 = new RectF(f3, f11, f16, f15);
            this.tMode1 = 1;
            float f17 = MirrorActivity.this.mulX * this.height;
            float f18 = MirrorActivity.this.mulY * 2.0f;
            int i4 = this.width;
            if (f17 <= f18 * i4) {
                float f19 = (i4 - (((MirrorActivity.this.mulX / MirrorActivity.this.mulY) * this.height) / 2.0f)) / 2.0f;
                f10 = f19;
                f12 = (((MirrorActivity.this.mulX / MirrorActivity.this.mulY) * this.height) / 2.0f) + f19;
                f5 = f13;
                f4 = 0.0f;
            } else {
                f4 = (this.height - ((i4 * 2) * (MirrorActivity.this.mulY / MirrorActivity.this.mulX))) / 2.0f;
                f5 = (this.width * 2 * (MirrorActivity.this.mulY / MirrorActivity.this.mulX)) + f4;
                this.tMode1 = 5;
            }
            this.srcRect1 = new RectF(f10, f4, f12, f5);
            this.srcRectPaper = new RectF(f10, f4, ((f12 - f10) / 2.0f) + f10, f5);
            float f20 = f2 / 2.0f;
            float f21 = f20 + f3;
            this.dstRectPaper1 = new RectF(f3, f11, f21, f15);
            float f22 = f20 + f21;
            this.dstRectPaper2 = new RectF(f21, f11, f22, f15);
            float f23 = f20 + f22;
            this.dstRectPaper3 = new RectF(f22, f11, f23, f15);
            this.dstRectPaper4 = new RectF(f23, f11, f20 + f23, f15);
        }

        private void createRectY(int i, int i2) {
            float f;
            float f2;
            float f3;
            float f4;
            float f5 = i;
            float f6 = ((MirrorActivity.this.mulY / MirrorActivity.this.mulX) * f5) / 2.0f;
            int i3 = MirrorActivity.this.initialYPos;
            float f7 = i2;
            float f8 = 0.0f;
            if (f6 > f7) {
                f2 = ((MirrorActivity.this.mulX / MirrorActivity.this.mulY) * f7) / 2.0f;
                f3 = (f5 / 2.0f) - f2;
                f = f7;
            } else {
                f = f6;
                f2 = f5;
                f3 = 0.0f;
            }
            float f9 = MirrorActivity.this.initialYPos + ((f7 - (f * 2.0f)) / 2.0f);
            float f10 = f2 + f3;
            float f11 = f + f9;
            this.destRect1Y = new RectF(f3, f9, f10, f11);
            float f12 = f + f11;
            this.destRect2Y = new RectF(f3, f11, f10, f12);
            this.totalArea2 = new RectF(f3, f9, f10, f12);
            float f13 = this.width;
            float f14 = this.height;
            this.tMode2 = 0;
            float f15 = MirrorActivity.this.mulX * 2.0f * this.height;
            float f16 = MirrorActivity.this.mulY;
            int i4 = this.width;
            if (f15 > f16 * i4) {
                float f17 = (this.height - (((MirrorActivity.this.mulY / MirrorActivity.this.mulX) * this.width) / 2.0f)) / 2.0f;
                f4 = f17;
                f14 = (((MirrorActivity.this.mulY / MirrorActivity.this.mulX) * this.width) / 2.0f) + f17;
            } else {
                float f18 = (i4 - ((this.height * 2) * (MirrorActivity.this.mulX / MirrorActivity.this.mulY))) / 2.0f;
                float f19 = (this.height * 2 * (MirrorActivity.this.mulX / MirrorActivity.this.mulY)) + f18;
                this.tMode2 = 6;
                f8 = f18;
                f13 = f19;
                f4 = 0.0f;
            }
            this.srcRect2 = new RectF(f8, f4, f13, f14);
        }

        private void createRectXY(int i, int i2) {
            float f;
            float f2;
            float f3;
            float f4;
            float f5 = i;
            float f6 = ((MirrorActivity.this.mulY / MirrorActivity.this.mulX) * f5) / 2.0f;
            float f7 = f5 / 2.0f;
            int i3 = MirrorActivity.this.initialYPos;
            float f8 = i2;
            float f9 = 0.0f;
            if (f6 > f8) {
                f2 = ((MirrorActivity.this.mulX / MirrorActivity.this.mulY) * f8) / 2.0f;
                f3 = f7 - f2;
                f = f8;
            } else {
                f = f6;
                f2 = f7;
                f3 = 0.0f;
            }
            float f10 = MirrorActivity.this.initialYPos + ((f8 - (f * 2.0f)) / 2.0f);
            float f11 = this.width;
            float f12 = this.height;
            float f13 = f2 + f3;
            float f14 = f + f10;
            this.destRect1 = new RectF(f3, f10, f13, f14);
            float f15 = f2 + f13;
            this.destRect2 = new RectF(f13, f10, f15, f14);
            float f16 = f + f14;
            this.destRect3 = new RectF(f3, f14, f13, f16);
            this.destRect4 = new RectF(f13, f14, f15, f16);
            this.totalArea3 = new RectF(f3, f10, f15, f16);
            float f17 = MirrorActivity.this.mulX * this.height;
            float f18 = MirrorActivity.this.mulY;
            int i4 = this.width;
            if (f17 <= f18 * i4) {
                float f19 = (i4 - ((MirrorActivity.this.mulX / MirrorActivity.this.mulY) * this.height)) / 2.0f;
                f11 = ((MirrorActivity.this.mulX / MirrorActivity.this.mulY) * this.height) + f19;
                this.tMode3 = 1;
                f9 = f19;
                f4 = 0.0f;
            } else {
                f4 = (this.height - (i4 * (MirrorActivity.this.mulY / MirrorActivity.this.mulX))) / 2.0f;
                f12 = f4 + (this.width * (MirrorActivity.this.mulY / MirrorActivity.this.mulX));
                this.tMode3 = 0;
            }
            this.srcRect3 = new RectF(f9, f4, f11, f12);
        }

        @Override // android.view.View
        public void onDraw(Canvas canvas) {
            Bitmap bitmap;
            canvas.drawColor(getResources().getColor(this.defaultColor));
            if (MirrorActivity.this.filterBitmap == null) {
                drawMode(canvas, MirrorActivity.this.sourceBitmap, this.mirrorModeList[this.currentModeIndex], this.f510I);
            } else {
                drawMode(canvas, MirrorActivity.this.filterBitmap, this.mirrorModeList[this.currentModeIndex], this.f510I);
            }
            if (this.d3Mode && (bitmap = this.d3Bitmap) != null && !bitmap.isRecycled()) {
                canvas.setMatrix(this.f510I);
                canvas.drawBitmap(this.d3Bitmap, (Rect) null, this.mirrorModeList[this.currentModeIndex].rectTotalArea, this.framePaint);
            }
            Bitmap bitmap2 = this.frameBitmap;
            if (bitmap2 != null && !bitmap2.isRecycled()) {
                canvas.setMatrix(this.f510I);
                canvas.drawBitmap(this.frameBitmap, (Rect) null, this.mirrorModeList[this.currentModeIndex].rectTotalArea, this.framePaint);
            }
            super.onDraw(canvas);
        }

        private void drawMode(Canvas canvas, Bitmap bitmap, MirrorImageMode mirrorImageMode, Matrix matrix) {
            canvas.setMatrix(matrix);
            canvas.drawBitmap(bitmap, mirrorImageMode.getDrawBitmapSrc(), mirrorImageMode.rect1, this.framePaint);
            this.f2071m1.set(mirrorImageMode.matrix1);
            this.f2071m1.postConcat(matrix);
            canvas.setMatrix(this.f2071m1);
            if (bitmap != null && !bitmap.isRecycled()) {
                canvas.drawBitmap(bitmap, mirrorImageMode.getDrawBitmapSrc(), mirrorImageMode.rect2, this.framePaint);
            }
            if (mirrorImageMode.count == 4) {
                this.f2072m2.set(mirrorImageMode.matrix2);
                this.f2072m2.postConcat(matrix);
                canvas.setMatrix(this.f2072m2);
                if (bitmap != null && !bitmap.isRecycled()) {
                    canvas.drawBitmap(bitmap, mirrorImageMode.getDrawBitmapSrc(), mirrorImageMode.rect3, this.framePaint);
                }
                this.f2073m3.set(mirrorImageMode.matrix3);
                this.f2073m3.postConcat(matrix);
                canvas.setMatrix(this.f2073m3);
                if (bitmap == null || bitmap.isRecycled()) {
                    return;
                }
                canvas.drawBitmap(bitmap, mirrorImageMode.getDrawBitmapSrc(), mirrorImageMode.rect4, this.framePaint);
            }
        }

        @Override // android.view.View
        public boolean onTouchEvent(MotionEvent motionEvent) {
            float x = motionEvent.getX();
            float y = motionEvent.getY();
            int action = motionEvent.getAction();
            if (action == 0) {
                if (x < MirrorActivity.this.screenWidthPixels / 2) {
                    this.isTouchStartedLeft = true;
                } else {
                    this.isTouchStartedLeft = false;
                }
                if (y < MirrorActivity.this.screenHeightPixels / 2) {
                    this.isTouchStartedTop = true;
                } else {
                    this.isTouchStartedTop = false;
                }
                this.oldX = x;
                this.oldY = y;
            } else if (action == 2) {
                moveGrid(this.mirrorModeList[this.currentModeIndex].getSrcRect(), x - this.oldX, y - this.oldY);
                this.mirrorModeList[this.currentModeIndex].updateBitmapSrc();
                this.oldX = x;
                this.oldY = y;
            }
            postInvalidate();
            return true;
        }

        public void moveGrid(RectF rectF, float f, float f2) {
            if (this.mirrorModeList[this.currentModeIndex].touchMode == 1 || this.mirrorModeList[this.currentModeIndex].touchMode == 4 || this.mirrorModeList[this.currentModeIndex].touchMode == 6) {
                if (this.mirrorModeList[this.currentModeIndex].touchMode == 4) {
                    f *= -1.0f;
                }
                if (this.isTouchStartedLeft && this.mirrorModeList[this.currentModeIndex].touchMode != 6) {
                    f *= -1.0f;
                }
                if (rectF.left + f < 0.0f) {
                    f = -rectF.left;
                }
                float f3 = rectF.right + f;
                int i = this.width;
                if (f3 >= i) {
                    f = i - rectF.right;
                }
                rectF.left += f;
                rectF.right += f;
                return;
            }
            if (this.mirrorModeList[this.currentModeIndex].touchMode == 0 || this.mirrorModeList[this.currentModeIndex].touchMode == 3 || this.mirrorModeList[this.currentModeIndex].touchMode == 5) {
                if (this.mirrorModeList[this.currentModeIndex].touchMode == 3) {
                    f2 *= -1.0f;
                }
                if (this.isTouchStartedTop && this.mirrorModeList[this.currentModeIndex].touchMode != 5) {
                    f2 *= -1.0f;
                }
                if (rectF.top + f2 < 0.0f) {
                    f2 = -rectF.top;
                }
                float f4 = rectF.bottom + f2;
                int i2 = this.height;
                if (f4 >= i2) {
                    f2 = i2 - rectF.bottom;
                }
                rectF.top += f2;
                rectF.bottom += f2;
            }
        }
    }

    final class MyMediaScannerConnectionClient implements MediaScannerConnection.MediaScannerConnectionClient {
        private MediaScannerConnection mConn;
        private String mFilename;
        private String mMimetype;

        public MyMediaScannerConnectionClient(Context context, File file, String str) {
            this.mFilename = file.getAbsolutePath();
            MediaScannerConnection mediaScannerConnection = new MediaScannerConnection(context, this);
            this.mConn = mediaScannerConnection;
            mediaScannerConnection.connect();
        }

        @Override // android.media.MediaScannerConnection.MediaScannerConnectionClient
        public void onMediaScannerConnected() {
            this.mConn.scanFile(this.mFilename, this.mMimetype);
        }

        @Override // android.media.MediaScannerConnection.OnScanCompletedListener
        public void onScanCompleted(String str, Uri uri) {
            this.mConn.disconnect();
        }
    }

    private class SaveImageTask extends AsyncTask<Object, Object, Object> {
        Bitmap bitmapPath;
        ProgressDialog progressDialog;
        String resultPath;

        private SaveImageTask() {
            this.resultPath = null;
        }

        @Override // android.os.AsyncTask
        public Object doInBackground(Object... objArr) {
            String saveBitmap = MirrorActivity.this.mirrorView.saveBitmap(true, MirrorActivity.this.screenWidthPixels, MirrorActivity.this.screenHeightPixels);
            this.resultPath = saveBitmap;
            this.bitmapPath = BitmapFactory.decodeFile(saveBitmap);
            return null;
        }

        @Override // android.os.AsyncTask
        public void onPreExecute() {
            ProgressDialog progressDialog = new ProgressDialog(MirrorActivity.this);
            this.progressDialog = progressDialog;
            progressDialog.setMessage("Saving image ...");
            this.progressDialog.show();
        }

        @Override // android.os.AsyncTask
        public void onPostExecute(Object obj) {
            super.onPostExecute(obj);
            ProgressDialog progressDialog = this.progressDialog;
            if (progressDialog != null && progressDialog.isShowing()) {
                this.progressDialog.cancel();
            }
            if (this.resultPath != null) {
                BitmapTransfer.bitmap = this.bitmapPath;
                Intent intent = new Intent(MirrorActivity.this, (Class<?>) PhotoEditorActivity.class);
                intent.putExtra("MESSAGE", "done");
                MirrorActivity.this.setResult(-1, intent);
                MirrorActivity.this.finish();
            }
            MirrorActivity mirrorActivity = MirrorActivity.this;
            mirrorActivity.new MyMediaScannerConnectionClient(mirrorActivity.getApplicationContext(), new File(this.resultPath), null);
        }
    }

    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getWindow().addFlags(1024);
        getIntent().getExtras();
        if (BitmapTransfer.bitmap != null) {
            this.sourceBitmap = BitmapTransfer.bitmap;
        }
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        this.screenHeightPixels = displayMetrics.heightPixels;
        this.screenWidthPixels = displayMetrics.widthPixels;
        Display defaultDisplay = getWindowManager().getDefaultDisplay();
        int width = defaultDisplay.getWidth();
        int height = defaultDisplay.getHeight();
        if (this.screenWidthPixels <= 0) {
            this.screenWidthPixels = width;
        }
        if (this.screenHeightPixels <= 0) {
            this.screenHeightPixels = height;
        }
        this.mirrorView = new MirrorView(this, this.screenWidthPixels, this.screenHeightPixels);
        setContentView(R.layout.activity_mirror);
        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.layout_mirror_activity);
        this.mainLayout = relativeLayout;
        relativeLayout.addView(this.mirrorView);
        ViewFlipper viewFlipper = (ViewFlipper) findViewById(R.id.mirror_view_flipper);
        this.viewFlipper = viewFlipper;
        viewFlipper.bringToFront();
        findViewById(R.id.mirror_footer).bringToFront();
        this.slideLeftIn = AnimationUtils.loadAnimation(this, R.anim.slide_in_left);
        this.slideLeftOut = AnimationUtils.loadAnimation(this, R.anim.slide_out_left);
        this.slideRightIn = AnimationUtils.loadAnimation(this, R.anim.slide_in_right);
        this.slideRightOut = AnimationUtils.loadAnimation(this, R.anim.slide_out_right);
        findViewById(R.id.constraint_layout_confirm_blur).bringToFront();
        setSelectedTab(0);
    }

    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onPause() {
        super.onPause();
    }

    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onResume() {
        super.onResume();
    }

    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onDestroy() {
        super.onDestroy();
        Bitmap bitmap = this.sourceBitmap;
        if (bitmap != null) {
            bitmap.recycle();
        }
        Bitmap bitmap2 = this.filterBitmap;
        if (bitmap2 != null) {
            bitmap2.recycle();
        }
    }

    @Override // androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
    }

    @Override // android.app.Activity
    public void onRestoreInstanceState(Bundle bundle) {
        super.onRestoreInstanceState(bundle);
    }

    public void myClickHandler(View view) {
        int id = view.getId();
        this.mirrorView.drawSavedImage = false;
        if (id == R.id.imageViewSaveDrip) {
            new SaveImageTask().execute(new Object[0]);
            return;
        }
        if (id == R.id.imageViewCloseDrip) {
            backButtonAlertBuilder();
            return;
        }
        if (id == R.id.linearLayout2D) {
            setSelectedTab(0);
            return;
        }
        if (id == R.id.linearLayout3D) {
            setSelectedTab(1);
            return;
        }
        if (id == R.id.button_3d_1) {
            set3dMode(0);
            return;
        }
        if (id == R.id.button_3d_2) {
            set3dMode(1);
            return;
        }
        if (id == R.id.button_3d_3) {
            set3dMode(2);
            return;
        }
        if (id == R.id.button_3d_4) {
            set3dMode(3);
            return;
        }
        if (id == R.id.button_3d_5) {
            set3dMode(4);
            return;
        }
        if (id == R.id.button_3d_6) {
            set3dMode(5);
            return;
        }
        if (id == R.id.button_3d_7) {
            set3dMode(6);
            return;
        }
        if (id == R.id.button_3d_8) {
            set3dMode(7);
            return;
        }
        if (id == R.id.button_3d_9) {
            set3dMode(8);
            return;
        }
        if (id == R.id.button_3d_10) {
            set3dMode(9);
            return;
        }
        if (id == R.id.button_3d_11) {
            set3dMode(10);
            return;
        }
        if (id == R.id.button_3d_12) {
            set3dMode(11);
            return;
        }
        if (id == R.id.button_3d_13) {
            set3dMode(12);
            return;
        }
        if (id == R.id.button_3d_14) {
            set3dMode(13);
            return;
        }
        if (id == R.id.button_3d_15) {
            set3dMode(14);
            return;
        }
        if (id == R.id.button_3d_16) {
            set3dMode(15);
            return;
        }
        if (id == R.id.button_3d_17) {
            set3dMode(16);
            return;
        }
        if (id == R.id.button_3d_18) {
            set3dMode(17);
            return;
        }
        if (id == R.id.button_3d_19) {
            set3dMode(18);
            return;
        }
        if (id == R.id.button_3d_20) {
            set3dMode(19);
            return;
        }
        if (id == R.id.button_3d_21) {
            set3dMode(20);
            return;
        }
        if (id == R.id.button_3d_22) {
            set3dMode(21);
            return;
        }
        if (id == R.id.button_3d_23) {
            set3dMode(22);
            return;
        }
        if (id == R.id.button_3d_24) {
            set3dMode(23);
            return;
        }
        if (id == R.id.button_m1) {
            this.mirrorView.setCurrentMode(0);
            this.mirrorView.d3Mode = false;
            this.mirrorView.reset(this.screenWidthPixels, this.screenHeightPixels, true);
            setMirrorButtonBg(0);
            return;
        }
        if (id == R.id.button_m2) {
            this.mirrorView.setCurrentMode(1);
            this.mirrorView.d3Mode = false;
            this.mirrorView.reset(this.screenWidthPixels, this.screenHeightPixels, true);
            setMirrorButtonBg(1);
            return;
        }
        if (id == R.id.button_m3) {
            this.mirrorView.setCurrentMode(2);
            this.mirrorView.d3Mode = false;
            this.mirrorView.reset(this.screenWidthPixels, this.screenHeightPixels, true);
            setMirrorButtonBg(2);
            return;
        }
        if (id == R.id.button_m4) {
            this.mirrorView.setCurrentMode(3);
            this.mirrorView.d3Mode = false;
            this.mirrorView.reset(this.screenWidthPixels, this.screenHeightPixels, true);
            setMirrorButtonBg(3);
            return;
        }
        if (id == R.id.button_m5) {
            this.mirrorView.setCurrentMode(4);
            this.mirrorView.d3Mode = false;
            this.mirrorView.reset(this.screenWidthPixels, this.screenHeightPixels, true);
            setMirrorButtonBg(4);
            return;
        }
        if (id == R.id.button_m6) {
            this.mirrorView.setCurrentMode(5);
            this.mirrorView.d3Mode = false;
            this.mirrorView.reset(this.screenWidthPixels, this.screenHeightPixels, true);
            setMirrorButtonBg(5);
            return;
        }
        if (id == R.id.button_m7) {
            this.mirrorView.setCurrentMode(6);
            this.mirrorView.d3Mode = false;
            this.mirrorView.reset(this.screenWidthPixels, this.screenHeightPixels, true);
            setMirrorButtonBg(6);
            return;
        }
        if (id == R.id.button_m8) {
            this.mirrorView.setCurrentMode(7);
            this.mirrorView.d3Mode = false;
            this.mirrorView.reset(this.screenWidthPixels, this.screenHeightPixels, true);
            setMirrorButtonBg(7);
            return;
        }
        if (id == R.id.button_m9) {
            this.mirrorView.setCurrentMode(8);
            this.mirrorView.d3Mode = false;
            this.mirrorView.reset(this.screenWidthPixels, this.screenHeightPixels, true);
            setMirrorButtonBg(8);
            return;
        }
        if (id == R.id.button_m10) {
            this.mirrorView.setCurrentMode(9);
            this.mirrorView.d3Mode = false;
            this.mirrorView.reset(this.screenWidthPixels, this.screenHeightPixels, true);
            setMirrorButtonBg(9);
            return;
        }
        if (id == R.id.button_m11) {
            this.mirrorView.setCurrentMode(10);
            this.mirrorView.d3Mode = false;
            this.mirrorView.reset(this.screenWidthPixels, this.screenHeightPixels, true);
            setMirrorButtonBg(10);
            return;
        }
        if (id == R.id.button_m12) {
            this.mirrorView.setCurrentMode(11);
            this.mirrorView.d3Mode = false;
            this.mirrorView.reset(this.screenWidthPixels, this.screenHeightPixels, true);
            setMirrorButtonBg(11);
            return;
        }
        if (id == R.id.button_m13) {
            this.mirrorView.setCurrentMode(12);
            this.mirrorView.d3Mode = false;
            this.mirrorView.reset(this.screenWidthPixels, this.screenHeightPixels, true);
            setMirrorButtonBg(12);
            return;
        }
        if (id == R.id.button_m14) {
            this.mirrorView.setCurrentMode(13);
            this.mirrorView.d3Mode = false;
            this.mirrorView.reset(this.screenWidthPixels, this.screenHeightPixels, true);
            setMirrorButtonBg(13);
            return;
        }
        if (id == R.id.button_m15) {
            this.mirrorView.setCurrentMode(14);
            this.mirrorView.d3Mode = false;
            this.mirrorView.reset(this.screenWidthPixels, this.screenHeightPixels, true);
            setMirrorButtonBg(14);
        }
    }

    private void set3dMode(int i) {
        this.mirrorView.d3Mode = true;
        if (i > 15 && i < 20) {
            this.mirrorView.setCurrentMode(i);
        } else if (i > 19) {
            this.mirrorView.setCurrentMode(i - 4);
        } else if (i % 2 == 0) {
            this.mirrorView.setCurrentMode(0);
        } else {
            this.mirrorView.setCurrentMode(1);
        }
        this.mirrorView.reset(this.screenWidthPixels, this.screenHeightPixels, false);
        loadInBitmap(this.d3resList[i]);
        this.mirrorView.postInvalidate();
        setD3ButtonBg(i);
    }

    private void loadInBitmap(int i) {
        Log.e(TAG, "loadInBitmap");
        BitmapFactory.Options options = new BitmapFactory.Options();
        if (this.mirrorView.d3Bitmap == null || this.mirrorView.d3Bitmap.isRecycled()) {
            options.inJustDecodeBounds = true;
            options.inMutable = true;
            BitmapFactory.decodeResource(getResources(), i, options);
            this.mirrorView.d3Bitmap = Bitmap.createBitmap(options.outWidth, options.outHeight, Bitmap.Config.ARGB_8888);
        }
        options.inJustDecodeBounds = false;
        options.inSampleSize = 1;
        options.inBitmap = this.mirrorView.d3Bitmap;
        try {
            this.mirrorView.d3Bitmap = BitmapFactory.decodeResource(getResources(), i, options);
        } catch (Exception e) {
            Log.e(TAG, e.toString());
            if (this.mirrorView.d3Bitmap != null && !this.mirrorView.d3Bitmap.isRecycled()) {
                this.mirrorView.d3Bitmap.recycle();
            }
            this.mirrorView.d3Bitmap = BitmapFactory.decodeResource(getResources(), i);
        }
    }

    private void setD3ButtonBg(int i) {
        if (this.d3ButtonArray == null) {
            RoundedImageView[] roundedImageViewArr = new RoundedImageView[this.D3_BUTTON_SIZE];
            this.d3ButtonArray = roundedImageViewArr;
            roundedImageViewArr[0] = (RoundedImageView) findViewById(R.id.button_3d_1);
            this.d3ButtonArray[1] = (RoundedImageView) findViewById(R.id.button_3d_2);
            this.d3ButtonArray[2] = (RoundedImageView) findViewById(R.id.button_3d_3);
            this.d3ButtonArray[3] = (RoundedImageView) findViewById(R.id.button_3d_4);
            this.d3ButtonArray[4] = (RoundedImageView) findViewById(R.id.button_3d_5);
            this.d3ButtonArray[5] = (RoundedImageView) findViewById(R.id.button_3d_6);
            this.d3ButtonArray[6] = (RoundedImageView) findViewById(R.id.button_3d_7);
            this.d3ButtonArray[7] = (RoundedImageView) findViewById(R.id.button_3d_8);
            this.d3ButtonArray[8] = (RoundedImageView) findViewById(R.id.button_3d_9);
            this.d3ButtonArray[9] = (RoundedImageView) findViewById(R.id.button_3d_10);
            this.d3ButtonArray[10] = (RoundedImageView) findViewById(R.id.button_3d_11);
            this.d3ButtonArray[11] = (RoundedImageView) findViewById(R.id.button_3d_12);
            this.d3ButtonArray[12] = (RoundedImageView) findViewById(R.id.button_3d_13);
            this.d3ButtonArray[13] = (RoundedImageView) findViewById(R.id.button_3d_14);
            this.d3ButtonArray[14] = (RoundedImageView) findViewById(R.id.button_3d_15);
            this.d3ButtonArray[15] = (RoundedImageView) findViewById(R.id.button_3d_16);
            this.d3ButtonArray[16] = (RoundedImageView) findViewById(R.id.button_3d_17);
            this.d3ButtonArray[17] = (RoundedImageView) findViewById(R.id.button_3d_18);
            this.d3ButtonArray[18] = (RoundedImageView) findViewById(R.id.button_3d_19);
            this.d3ButtonArray[19] = (RoundedImageView) findViewById(R.id.button_3d_20);
            this.d3ButtonArray[20] = (RoundedImageView) findViewById(R.id.button_3d_21);
            this.d3ButtonArray[21] = (RoundedImageView) findViewById(R.id.button_3d_22);
            this.d3ButtonArray[22] = (RoundedImageView) findViewById(R.id.button_3d_23);
            this.d3ButtonArray[23] = (RoundedImageView) findViewById(R.id.button_3d_24);
        }
    }

    private void setMirrorButtonBg(int i) {
        if (this.mirrorButtonArray == null) {
            RoundedImageView[] roundedImageViewArr = new RoundedImageView[this.MIRROR_BUTTON_SIZE];
            this.mirrorButtonArray = roundedImageViewArr;
            roundedImageViewArr[0] = (RoundedImageView) findViewById(R.id.button_m1);
            this.mirrorButtonArray[1] = (RoundedImageView) findViewById(R.id.button_m2);
            this.mirrorButtonArray[2] = (RoundedImageView) findViewById(R.id.button_m3);
            this.mirrorButtonArray[3] = (RoundedImageView) findViewById(R.id.button_m4);
            this.mirrorButtonArray[4] = (RoundedImageView) findViewById(R.id.button_m5);
            this.mirrorButtonArray[5] = (RoundedImageView) findViewById(R.id.button_m6);
            this.mirrorButtonArray[6] = (RoundedImageView) findViewById(R.id.button_m7);
            this.mirrorButtonArray[7] = (RoundedImageView) findViewById(R.id.button_m8);
            this.mirrorButtonArray[8] = (RoundedImageView) findViewById(R.id.button_m9);
            this.mirrorButtonArray[9] = (RoundedImageView) findViewById(R.id.button_m10);
            this.mirrorButtonArray[10] = (RoundedImageView) findViewById(R.id.button_m11);
            this.mirrorButtonArray[11] = (RoundedImageView) findViewById(R.id.button_m12);
            this.mirrorButtonArray[12] = (RoundedImageView) findViewById(R.id.button_m13);
            this.mirrorButtonArray[13] = (RoundedImageView) findViewById(R.id.button_m14);
            this.mirrorButtonArray[14] = (RoundedImageView) findViewById(R.id.button_m15);
        }
    }

    public void setSelectedTab(int i) {
        setTabBg(0);
        int displayedChild = this.viewFlipper.getDisplayedChild();
        if (i == 0) {
            if (displayedChild == 0) {
                return;
            }
            this.viewFlipper.setInAnimation(this.slideLeftIn);
            this.viewFlipper.setOutAnimation(this.slideRightOut);
            this.viewFlipper.setDisplayedChild(0);
        }
        if (i == 1) {
            setTabBg(1);
            if (displayedChild == 1) {
                return;
            }
            if (displayedChild == 0) {
                this.viewFlipper.setInAnimation(this.slideRightIn);
                this.viewFlipper.setOutAnimation(this.slideLeftOut);
            } else {
                this.viewFlipper.setInAnimation(this.slideLeftIn);
                this.viewFlipper.setOutAnimation(this.slideRightOut);
            }
            this.viewFlipper.setDisplayedChild(1);
        }
        if (i == 7) {
            setTabBg(-1);
            if (displayedChild != 4) {
                this.viewFlipper.setInAnimation(this.slideRightIn);
                this.viewFlipper.setOutAnimation(this.slideLeftOut);
                this.viewFlipper.setDisplayedChild(4);
            }
        }
    }

    private void setTabBg(int i) {
        this.currentSelectedTabIndex = i;
        if (this.tabButtonList == null) {
            View[] viewArr = new View[6];
            this.tabButtonList = viewArr;
            viewArr[0] = findViewById(R.id.linearLayout2D);
            this.tabButtonList[1] = findViewById(R.id.linearLayout3D);
        }
    }

    public void clearViewFlipper() {
        this.viewFlipper.setInAnimation(null);
        this.viewFlipper.setOutAnimation(null);
        this.viewFlipper.setDisplayedChild(4);
        setTabBg(-1);
    }

    @Override // androidx.activity.ComponentActivity, android.app.Activity
    public void onBackPressed() {
        super.onBackPressed();
        if (this.viewFlipper.getDisplayedChild() != 4) {
            clearViewFlipper();
        } else {
            backButtonAlertBuilder();
        }
    }

    private void backButtonAlertBuilder() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Would you like to save image ?").setCancelable(true).setPositiveButton("Yes", new DialogInterface.OnClickListener() { // from class: com.gallery.photos.editphotovideo.activities.MirrorActivity.3
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                new SaveImageTask().execute(new Object[0]);
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() { // from class: com.gallery.photos.editphotovideo.activities.MirrorActivity.2
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        }).setNeutralButton("No", new DialogInterface.OnClickListener() { // from class: com.gallery.photos.editphotovideo.activities.MirrorActivity.1
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                MirrorActivity.this.finish();
            }
        });
        AlertDialog create = builder.create();
        this.saveImageAlert = create;
        create.show();
    }
}
