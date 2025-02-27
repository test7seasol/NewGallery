package com.gallery.photos.editpic.ImageEDITModule.edit.mirror;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.media.ExifInterface;
import android.os.Debug;
import android.util.Log;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/* loaded from: classes.dex */
public class BitmapResizer {
    public static Bitmap decodeFile(File file, int i) {
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            int i2 = 1;
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(file), null, options);
            int i3 = options.outWidth;
            int i4 = options.outHeight;
            while (i3 / 2 >= i && i4 / 2 >= i) {
                i3 /= 2;
                i4 /= 2;
                i2 *= 2;
            }
            BitmapFactory.Options options2 = new BitmapFactory.Options();
            options2.inSampleSize = i2;
            return BitmapFactory.decodeStream(new FileInputStream(file), null, options2);
        } catch (FileNotFoundException unused) {
            return null;
        }
    }

    public static int maxSizeForDimension(Context context) {
        return (int) Math.sqrt(getFreeMemory(context) / 80.0d);
    }

    public static long getFreeMemory(Context context) {
        return (((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass() * 1048576) - Debug.getNativeHeapAllocatedSize();
    }

    public static Bitmap decodeX(String str, int i, int[] iArr, int[] iArr2) {
        String str2;
        try {
            str2 = new ExifInterface(str).getAttribute(androidx.exifinterface.media.ExifInterface.TAG_ORIENTATION);
        } catch (IOException e) {
            e.printStackTrace();
            str2 = "";
        }
        String str3 = str2 != null ? str2 : "";
        File file = new File(str);
        if (str3.contentEquals("6")) {
            iArr2[0] = 90;
            Bitmap decodeFile = decodeFile(file, i, iArr);
            Matrix matrix = new Matrix();
            matrix.postRotate(90.0f);
            Bitmap createBitmap = Bitmap.createBitmap(decodeFile, 0, 0, decodeFile.getWidth(), decodeFile.getHeight(), matrix, false);
            decodeFile.recycle();
            return createBitmap;
        }
        if (str3.contentEquals("8")) {
            iArr2[0] = 270;
            Bitmap decodeFile2 = decodeFile(file, i, iArr);
            Matrix matrix2 = new Matrix();
            matrix2.postRotate(270.0f);
            Bitmap createBitmap2 = Bitmap.createBitmap(decodeFile2, 0, 0, decodeFile2.getWidth(), decodeFile2.getHeight(), matrix2, false);
            decodeFile2.recycle();
            return createBitmap2;
        }
        if (!str3.contentEquals(androidx.exifinterface.media.ExifInterface.GPS_MEASUREMENT_3D)) {
            return decodeFile(file, i, iArr);
        }
        iArr2[0] = 180;
        Bitmap decodeFile3 = decodeFile(file, i, iArr);
        Matrix matrix3 = new Matrix();
        matrix3.postRotate(180.0f);
        Bitmap createBitmap3 = Bitmap.createBitmap(decodeFile3, 0, 0, decodeFile3.getWidth(), decodeFile3.getHeight(), matrix3, false);
        decodeFile3.recycle();
        return createBitmap3;
    }

    public static Bitmap decodeFile(File file, int i, int[] iArr) {
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            int i2 = 1;
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(file), null, options);
            int max = Math.max(options.outWidth, options.outHeight);
            while (max / 2 >= i && max / 2 >= i) {
                max /= 2;
                i2 *= 2;
            }
            BitmapFactory.Options options2 = new BitmapFactory.Options();
            options2.inSampleSize = i2;
            iArr[0] = i2;
            return BitmapFactory.decodeStream(new FileInputStream(file), null, options2);
        } catch (FileNotFoundException unused) {
            return null;
        }
    }

    public static Point decodeFileSize(File file, int i) {
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(file), null, options);
            int i2 = options.outWidth;
            int i3 = options.outHeight;
            int i4 = 1;
            while (Math.max(i2, i3) / 2 > i) {
                i2 /= 2;
                i3 /= 2;
                i4 *= 2;
            }
            if (i4 == 1) {
                return new Point(-1, -1);
            }
            return new Point(i2, i3);
        } catch (FileNotFoundException unused) {
            return null;
        }
    }

    public static Point getFileSize(File file, int i) {
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(file), null, options);
            return new Point(options.outWidth, options.outHeight);
        } catch (FileNotFoundException unused) {
            return null;
        }
    }

    public static Bitmap decodeBitmapFromFile(String str, int i) {
        try {
            new ExifInterface(str).getAttributeInt(androidx.exifinterface.media.ExifInterface.TAG_ORIENTATION, 0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Bitmap decodeFile = decodeFile(str, i);
        if (decodeFile == null) {
            return null;
        }
        return decodeFile;
    }



    private static Bitmap decodeFile(String str, int i) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        int i2 = 1;
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(str, options);
        int i3 = options.outWidth;
        int i4 = options.outHeight;
        while (Math.max(i3, i4) / 2 > i) {
            i3 /= 2;
            i4 /= 2;
            i2 *= 2;
        }
        BitmapFactory.Options options2 = new BitmapFactory.Options();
        options2.inSampleSize = i2;
        Bitmap decodeFile = BitmapFactory.decodeFile(str, options2);
        if (decodeFile != null) {
            Log.e("decoded file height", String.valueOf(decodeFile.getHeight()));
            Log.e("decoded file width", String.valueOf(decodeFile.getWidth()));
        }
        return decodeFile;
    }
}
