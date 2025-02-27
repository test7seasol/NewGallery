package com.gallery.photos.editpic.ImageEDITModule.edit.Editor;

import android.graphics.Bitmap;

/* loaded from: classes.dex */
class BitmapUtil {
    BitmapUtil() {
    }

    static Bitmap removeTransparency(Bitmap bitmap) {
        int i;
        int i2;
        int i3;
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int[] iArr = new int[bitmap.getWidth() * bitmap.getHeight()];
        bitmap.getPixels(iArr, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        int i4 = 0;
        loop0: while (true) {
            if (i4 >= bitmap.getWidth()) {
                i = 0;
                break;
            }
            for (int i5 = 0; i5 < bitmap.getHeight(); i5++) {
                if (iArr[(bitmap.getWidth() * i5) + i4] != 0) {
                    i = i4;
                    break loop0;
                }
            }
            i4++;
        }
        int i6 = 0;
        loop2: while (true) {
            if (i6 >= bitmap.getHeight()) {
                i2 = 0;
                break;
            }
            for (int i7 = i; i7 < bitmap.getHeight(); i7++) {
                if (iArr[(bitmap.getWidth() * i6) + i7] != 0) {
                    i2 = i6;
                    break loop2;
                }
            }
            i6++;
        }
        int width2 = bitmap.getWidth() - 1;
        loop4: while (true) {
            if (width2 < i) {
                i3 = width;
                break;
            }
            for (int height2 = bitmap.getHeight() - 1; height2 >= i2; height2--) {
                if (iArr[(bitmap.getWidth() * height2) + width2] != 0) {
                    i3 = width2;
                    break loop4;
                }
            }
            width2--;
        }
        int height3 = bitmap.getHeight() - 1;
        loop6: while (true) {
            if (height3 < i2) {
                break;
            }
            for (int width3 = bitmap.getWidth() - 1; width3 >= i; width3--) {
                if (iArr[(bitmap.getWidth() * height3) + width3] != 0) {
                    height = height3;
                    break loop6;
                }
            }
            height3--;
        }
        return Bitmap.createBitmap(bitmap, i, i2, i3 - i, height - i2);
    }
}
