package com.gallery.photos.editpic.ImageEDITModule.edit.utils;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/* loaded from: classes.dex */
public class ResizeImages extends AppCompatActivity {
    private int imageheight;
    private int imagwidth;
    private int maxResolution;
    private float orientation;
    private Bitmap originalbm = null;

    public Bitmap getScaledBitamp(String str, int i) {
        this.maxResolution = i;
        this.orientation = getImageOrientation(str);
        getAspectRatio(str);
        if (BitmapTransfer.bitmap != null) {
            this.originalbm = BitmapTransfer.bitmap;
        }
        Bitmap resizedOriginalBitmap = getResizedOriginalBitmap(str);
        this.originalbm = resizedOriginalBitmap;
        return resizedOriginalBitmap;
    }

    private float getImageOrientation(String str) {
        try {
            int attributeInt = new ExifInterface(str).getAttributeInt("Orientation", 1);
            if (attributeInt == 6) {
                return 90.0f;
            }
            if (attributeInt == 3) {
                return 180.0f;
            }
            return attributeInt == 8 ? 270.0f : 0.0f;
        } catch (IOException e) {
            e.printStackTrace();
            return 0.0f;
        }
    }

    private void getAspectRatio(String str) {
        float f;
        float f2;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(str, options);
        float f3 = options.outWidth / options.outHeight;
        if (f3 > 1.0f) {
            f = this.maxResolution;
            f2 = f / f3;
        } else {
            float f4 = this.maxResolution;
            f = f3 * f4;
            f2 = f4;
        }
        this.imagwidth = (int) f;
        this.imageheight = (int) f2;
    }

    private Bitmap getResizedOriginalBitmap(String str) {
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            int i = 1;
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(str), null, options);
            int i2 = options.outWidth;
            int i3 = options.outHeight;
            int i4 = this.imagwidth;
            int i5 = this.imageheight;
            while (i2 / 2 > i4) {
                i2 /= 2;
                i3 /= 2;
                i *= 2;
            }
            float f = i4 / i2;
            float f2 = i5 / i3;
            options.inJustDecodeBounds = false;
            options.inDither = false;
            options.inSampleSize = i;
            options.inScaled = false;
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            Bitmap decodeStream = BitmapFactory.decodeStream(new FileInputStream(str), null, options);
            Matrix matrix = new Matrix();
            matrix.postScale(f, f2);
            matrix.postRotate(this.orientation);
            return Bitmap.createBitmap(decodeStream, 0, 0, decodeStream.getWidth(), decodeStream.getHeight(), matrix, true);
        } catch (FileNotFoundException unused) {
            return null;
        }
    }

    public String saveBitmap(Context context, String str, Bitmap bitmap) throws FileNotFoundException {
        File saveBitMap = saveBitMap(context, bitmap, str);
        return (saveBitMap == null || !saveBitMap.exists()) ? "" : saveBitMap.getAbsolutePath();
    }

    private File saveBitMap(Context context, Bitmap bitmap, String str) {
        File file = new File(new ContextWrapper(context).getDir(str, 0), File.separator + System.currentTimeMillis() + ".jpg");
        try {
            file.createNewFile();
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            Log.i("TAG", "There was an issue saving the image.");
        }
        scanGallery(context, file.getAbsolutePath());
        return file;
    }

    private void scanGallery(Context context, String str) {
        try {
            MediaScannerConnection.scanFile(context, new String[]{str}, null, new MediaScannerConnection.OnScanCompletedListener() { // from class: com.gallery.photos.editphotovideo.utils.ResizeImages.1
                @Override // android.media.MediaScannerConnection.OnScanCompletedListener
                public void onScanCompleted(String str2, Uri uri) {
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("TAG", "There was an issue scanning gallery.");
        }
    }
}
