package com.gallery.photos.editpic.ImageEDITModule.edit.utils;

import android.graphics.Bitmap;

/* loaded from: classes.dex */
public class BitmapTransfer {
    private static Bitmap bitmap;

    public static void setBitmap(Bitmap b) {
        // Clear previous bitmap if exists
        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
        }
        bitmap = b;
    }

    public static Bitmap getBitmap() {
        if (bitmap == null || bitmap.isRecycled()) {
            return null;
        }
        return bitmap.copy(Bitmap.Config.ARGB_8888, true);
    }

    public static void cleanup() {
        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
        }
        bitmap = null;
    }
}