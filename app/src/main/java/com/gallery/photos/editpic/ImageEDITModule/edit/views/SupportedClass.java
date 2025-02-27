package com.gallery.photos.editpic.ImageEDITModule.edit.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.view.WindowManager;

/* loaded from: classes.dex */
public class SupportedClass {
    public static int getWidth(Context context) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }

    public static Bitmap cropBitmapWithMask(Bitmap bitmap, Bitmap bitmap2, int i, int i2) {
        if (bitmap != null && bitmap2 != null) {
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            if (width > 0 && height > 0) {
                Bitmap createBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(createBitmap);
                Paint paint = new Paint(1);
                paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
                canvas.drawBitmap(bitmap, 0.0f, 0.0f, (Paint) null);
                canvas.drawBitmap(bitmap2, i, i2, paint);
                paint.setXfermode(null);
                return createBitmap;
            }
        }
        return null;
    }

    public static boolean stringIsNotEmpty(String str) {
        return (str == null || str.equals("null") || str.trim().equals("")) ? false : true;
    }

    public static Bitmap tfResizeBilinear(Bitmap bitmap, int i, int i2) {
        if (bitmap == null) {
            return null;
        }
        Bitmap createBitmap = Bitmap.createBitmap(i, i2, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(createBitmap);
        Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        Rect rect2 = new Rect(0, 0, i, i2);
        canvas.drawBitmap(bitmap, rect, rect2, (Paint) null);
        return createBitmap;
    }
}
