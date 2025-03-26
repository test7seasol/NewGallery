package com.gallery.photos.editpic.ImageEDITModule.edit.mirror;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Debug;
import android.provider.MediaStore;
import android.util.Log;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/* loaded from: classes.dex */
public class Utils {
    private static final String TAG = "Utils";
    private static final float limitDivider = 30.0f;

    public static Bitmap getScaledBitmapFromId(Context context, long j, int i, int i2, boolean z) {
        AssetFileDescriptor assetFileDescriptor;
        Uri withAppendedPath = Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, Long.toString(j));
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        try {
            assetFileDescriptor = context.getContentResolver().openAssetFileDescriptor(withAppendedPath, "r");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            assetFileDescriptor = null;
        }
        if (assetFileDescriptor == null) {
            return null;
        }
        BitmapFactory.decodeFileDescriptor(assetFileDescriptor.getFileDescriptor(), null, options);
        BitmapFactory.Options options2 = new BitmapFactory.Options();
        options2.inSampleSize = calculateInSampleSize(options, i2, i2);
        if (z) {
            options2.inMutable = true;
        }
        Bitmap decodeFileDescriptor = BitmapFactory.decodeFileDescriptor(assetFileDescriptor.getFileDescriptor(), null, options2);
        if (decodeFileDescriptor == null) {
            return null;
        }
        Bitmap rotateImage = rotateImage(decodeFileDescriptor, i);
        if (rotateImage != null && decodeFileDescriptor != rotateImage) {
            decodeFileDescriptor.recycle();
        }
        if (rotateImage.isMutable() || !z) {
            return rotateImage;
        }
        Log.e(TAG, "bitmap is not mutable");
        Bitmap copy = rotateImage.copy(Bitmap.Config.ARGB_8888, true);
        if (copy == rotateImage) {
            return copy;
        }
        rotateImage.recycle();
        return copy;
    }

    public static Bitmap decodeFile(String str, int i, boolean z) {
        ExifInterface exifInterface;
        try {
            File file = new File(str);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(file), null, options);
            BitmapFactory.Options options2 = new BitmapFactory.Options();
            if (z) {
                options2.inMutable = true;
            }
            options2.inSampleSize = calculateInSampleSize(options, i, i);
            Bitmap decodeStream = BitmapFactory.decodeStream(new FileInputStream(file), null, options2);
            try {
                exifInterface = new ExifInterface(str);
            } catch (IOException e) {
                e.printStackTrace();
                exifInterface = null;
            }
            Bitmap rotateImage = rotateImage(decodeStream, exifInterface.getAttributeInt(androidx.exifinterface.media.ExifInterface.TAG_ORIENTATION, 0));
            if (rotateImage.isMutable()) {
                return rotateImage;
            }
            Bitmap copy = rotateImage.copy(Bitmap.Config.ARGB_8888, true);
            if (copy != rotateImage) {
                rotateImage.recycle();
            }
            return copy;
        } catch (FileNotFoundException unused) {
            return null;
        }
    }

    private static Bitmap rotateImage(Bitmap bitmap, int i) {
        Matrix matrix = new Matrix();
        if (i == 90) {
            matrix.postRotate(90.0f);
        } else if (i == 180) {
            matrix.postRotate(180.0f);
        } else if (i == 270) {
            matrix.postRotate(270.0f);
        }
        return i == 0 ? bitmap : Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    public static double getLeftSizeOfMemory() {
        return (Double.valueOf(Runtime.getRuntime().maxMemory()).doubleValue() - (Double.valueOf(Runtime.getRuntime().totalMemory()).doubleValue() - Double.valueOf(Runtime.getRuntime().freeMemory()).doubleValue())) - Double.valueOf(Debug.getNativeHeapAllocatedSize()).doubleValue();
    }

    private static int getDefaultLimit(int i, float f) {
        int sqrt = (int) (f / Math.sqrt(i));
        Log.e(TAG, "limit = " + sqrt);
        return sqrt;
    }



    public static int maxSizeForSave() {
        int sqrt = (int) Math.sqrt(getLeftSizeOfMemory() / 40.0d);
        if (sqrt > 1080) {
            return 1080;
        }
        return sqrt;
    }
}
