package com.gallery.photos.editpic.ImageEDITModule.edit.utils;

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
import java.io.IOException;

/* loaded from: classes.dex */
public class ImageUtils {
    public static BitmapFactory.Options getResampling(int i, int i2, int i3) {
        float f;
        float f2;
        BitmapFactory.Options options = new BitmapFactory.Options();
        if (i > i2 || i2 <= i) {
            f = i3;
            f2 = i;
        } else {
            f = i3;
            f2 = i2;
        }
        float f3 = f / f2;
        options.outWidth = (int) ((i * f3) + 0.5f);
        options.outHeight = (int) ((i2 * f3) + 0.5f);
        return options;
    }

    public static Bitmap getMask(Context context, Bitmap bitmap, Bitmap bitmap2, int i, int i2) {
        Bitmap createScaledBitmap = Bitmap.createScaledBitmap(bitmap, i, i2, true);
        Bitmap createScaledBitmap2 = Bitmap.createScaledBitmap(bitmap2, i, i2, true);
        Bitmap createBitmap = Bitmap.createBitmap(createScaledBitmap2.getWidth(), createScaledBitmap2.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(createBitmap);
        Paint paint = new Paint(1);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        canvas.drawBitmap(createScaledBitmap, 0.0f, 0.0f, (Paint) null);
        canvas.drawBitmap(createScaledBitmap2, 0.0f, 0.0f, paint);
        paint.setXfermode(null);
        return createBitmap;
    }

    public static int getClosestResampleSize(int i, int i2, int i3) {
        int max = Math.max(i, i2);
        int i4 = 1;
        while (true) {
            if (i4 >= Integer.MAX_VALUE) {
                break;
            }
            if (i4 * i3 > max) {
                i4--;
                break;
            }
            i4++;
        }
        if (i4 > 0) {
            return i4;
        }
        return 1;
    }

    public static Bitmap getBitmapFromAsset(Context context, String str) {
        try {
            return BitmapFactory.decodeStream(context.getAssets().open(str));
        } catch (IOException unused) {
            Log.e("", "");
            return null;
        }
    }

    public static Bitmap getBitmapResize(Context context, Bitmap bitmap, int i, int i2) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        if (width >= height) {
            int i3 = (height * i) / width;
            if (i3 > i2) {
                i = (i * i2) / i3;
            } else {
                i2 = i3;
            }
        } else {
            int i4 = (width * i2) / height;
            if (i4 > i) {
                i2 = (i2 * i) / i4;
            } else {
                i = i4;
            }
        }
        return Bitmap.createScaledBitmap(bitmap, i, i2, true);
    }

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
