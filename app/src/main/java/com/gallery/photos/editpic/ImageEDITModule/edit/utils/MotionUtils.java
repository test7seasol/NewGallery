package com.gallery.photos.editpic.ImageEDITModule.edit.utils;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;

/* loaded from: classes.dex */
public class MotionUtils {
    public static Bitmap bitmap2;

    public static Bitmap cropBitmapWithMask(Bitmap bitmap, Bitmap bitmap3, int i, int i2) {
        if (bitmap != null && bitmap3 != null) {
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            if (width > 0 && height > 0) {
                Bitmap createBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(createBitmap);
                Paint paint = new Paint(1);
                paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
                canvas.drawBitmap(bitmap, 0.0f, 0.0f, (Paint) null);
                canvas.drawBitmap(bitmap3, i, i2, paint);
                paint.setXfermode(null);
                return createBitmap;
            }
        }
        return null;
    }

    public static Bitmap changeAlpha(Bitmap bitmap, int i) {
        Bitmap createBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(createBitmap);
        Paint paint = new Paint();
        paint.setAlpha(i);
        canvas.drawBitmap(bitmap, 0.0f, 0.0f, paint);
        return createBitmap;
    }

    public static int dpToPx(int i) {
        return (int) (i * Resources.getSystem().getDisplayMetrics().density);
    }
}
