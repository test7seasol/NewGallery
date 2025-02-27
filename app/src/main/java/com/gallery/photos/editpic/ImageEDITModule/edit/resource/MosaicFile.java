package com.gallery.photos.editpic.ImageEDITModule.edit.resource;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

/* loaded from: classes.dex */
public class MosaicFile {
    public static Bitmap getMosaic(Bitmap bitmap) {
        Bitmap bitmap2;
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Bitmap createBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(createBitmap);
        int ceil = (int) Math.ceil(width / 50.0f);
        int ceil2 = (int) Math.ceil(height / 50.0f);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        for (int i = 0; i < ceil; i++) {
            for (int i2 = 0; i2 < ceil2; i2++) {
                int i3 = 50 * i;
                int i4 = 50 * i2;
                int i5 = i3 + 50;
                if (i5 > width) {
                    i5 = width;
                }
                int i6 = i4 + 50;
                if (i6 > height) {
                    bitmap2 = bitmap;
                    i6 = height;
                } else {
                    bitmap2 = bitmap;
                }
                int pixel = bitmap2.getPixel(i3, i4);
                Rect rect = new Rect(i3, i4, i5, i6);
                paint.setColor(pixel);
                canvas.drawRect(rect, paint);
            }
        }
        canvas.save();
        return createBitmap;
    }
}
