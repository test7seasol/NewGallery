package com.gallery.photos.editpic.ImageEDITModule.edit.eraser;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Shader;
import android.util.Log;
import com.gallery.photos.editpic.R;

/* loaded from: classes.dex */
public class ImageUtils {
    public static Bitmap resizeBitmap(Bitmap bitmap, int i, int i2) {
        if (bitmap == null) {
            return null;
        }
        float f = i;
        float f2 = i2;
        float width = bitmap.getWidth();
        float height = bitmap.getHeight();
        Log.i("testings", f + "  " + f2 + "  and  " + width + "  " + height);
        float f3 = width / height;
        float f4 = height / width;
        if (width > f) {
            float f5 = f4 * f;
            Log.i("testings", "if (wd > wr) " + f + "  " + f5);
            if (f5 > f2) {
                float f6 = f3 * f2;
                Log.i("testings", "  if (he > hr) " + f6 + "  " + f2);
                return Bitmap.createScaledBitmap(bitmap, (int) f6, (int) f2, false);
            }
            Log.i("testings", " in else " + f + "  " + f5);
            return Bitmap.createScaledBitmap(bitmap, (int) f, (int) f5, false);
        }
        if (height > f2) {
            float f7 = f3 * f2;
            Log.i("testings", "  if (he > hr) " + f7 + "  " + f2);
            if (f7 <= f) {
                Log.i("testings", " in else " + f7 + "  " + f2);
                return Bitmap.createScaledBitmap(bitmap, (int) f7, (int) f2, false);
            }
        } else if (f3 > 0.75f) {
            float f8 = f * f4;
            Log.i("testings", " if (rat1 > .75f) ");
            if (f8 > f2) {
                float f9 = f3 * f2;
                Log.i("testings", "  if (he > hr) " + f9 + "  " + f2);
                return Bitmap.createScaledBitmap(bitmap, (int) f9, (int) f2, false);
            }
            Log.i("testings", " in else " + f + "  " + f8);
        } else if (f4 > 1.5f) {
            float f10 = f3 * f2;
            Log.i("testings", " if (rat2 > 1.5f) ");
            if (f10 <= f) {
                Log.i("testings", " in else " + f10 + "  " + f2);
                return Bitmap.createScaledBitmap(bitmap, (int) f10, (int) f2, false);
            }
        } else {
            float f11 = f * f4;
            Log.i("testings", " in else ");
            if (f11 > f2) {
                float f12 = f3 * f2;
                Log.i("testings", "  if (he > hr) " + f12 + "  " + f2);
                return Bitmap.createScaledBitmap(bitmap, (int) f12, (int) f2, false);
            }
            Log.i("testings", " in else " + f + "  " + f11);
        }
        return Bitmap.createScaledBitmap(bitmap, (int) f, (int) (f4 * f), false);
    }

    public static int dpToPx(Context context, int i) {
        context.getResources();
        return (int) (i * Resources.getSystem().getDisplayMetrics().density);
    }

    public static int dpToPx(Context context, float f) {
        context.getResources();
        return (int) (f * Resources.getSystem().getDisplayMetrics().density);
    }

    public static Bitmap bitmapmasking(Bitmap bitmap, Bitmap bitmap2) {
        Bitmap createBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(createBitmap);
        Paint paint = new Paint(1);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        canvas.drawBitmap(bitmap, 0.0f, 0.0f, (Paint) null);
        canvas.drawBitmap(bitmap2, 0.0f, 0.0f, paint);
        paint.setXfermode(null);
        return createBitmap;
    }

    public static Bitmap getTiledBitmap(Context context, int i, int i2, int i3) {
        Rect rect = new Rect(0, 0, i2, i3);
        Paint paint = new Paint();
        paint.setShader(new BitmapShader(BitmapFactory.decodeResource(context.getResources(), i, new BitmapFactory.Options()), Shader.TileMode.REPEAT, Shader.TileMode.REPEAT));
        Bitmap createBitmap = Bitmap.createBitmap(i2, i3, Bitmap.Config.ARGB_8888);
        new Canvas(createBitmap).drawRect(rect, paint);
        return createBitmap;
    }

    public static Bitmap getBgCircleBit(Context context, int i) {
        int dpToPx = dpToPx(context, 150);
        return bitmapmasking1(getTiledBitmap(context, i, dpToPx, dpToPx), Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.circle), dpToPx, dpToPx, true));
    }

    public static Bitmap bitmapmasking1(Bitmap bitmap, Bitmap bitmap2) {
        Bitmap createBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(createBitmap);
        Paint paint = new Paint(1);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        canvas.drawBitmap(bitmap, 0.0f, 0.0f, (Paint) null);
        canvas.drawBitmap(bitmap2, 0.0f, 0.0f, paint);
        paint.setXfermode(null);
        return createBitmap;
    }
}
