package com.gallery.photos.editpic.ImageEDITModule.github.flipzeus;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

/* loaded from: classes.dex */
public class ImageFlipper {
    public static void flip(ImageView imageView, FlipDirection flipDirection) {
        imageView.setImageDrawable(flip(imageView.getDrawable(), flipDirection));
    }

    public static Drawable flip(Drawable drawable, FlipDirection flipDirection) {
        if (drawable == null) {
            return null;
        }
        return new BitmapDrawable(Resources.getSystem(), flip(((BitmapDrawable) drawable).getBitmap(), flipDirection));
    }

    /* renamed from: com.github.flipzeus.ImageFlipper$1, reason: invalid class name */
    static   class AnonymousClass1 {
        static final   int[] $SwitchMap$com$github$flipzeus$FlipDirection;

        static {
            int[] iArr = new int[FlipDirection.values().length];
            $SwitchMap$com$github$flipzeus$FlipDirection = iArr;
            try {
                iArr[FlipDirection.VERTICAL.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$github$flipzeus$FlipDirection[FlipDirection.HORIZONTAL.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
        }
    }

    public static Bitmap flip(Bitmap bitmap, FlipDirection flipDirection) {
        int i = AnonymousClass1.$SwitchMap$com$github$flipzeus$FlipDirection[flipDirection.ordinal()];
        if (i == 1) {
            return flipVertical(bitmap);
        }
        if (i != 2) {
            return null;
        }
        return flipHorizontal(bitmap);
    }

    private static Bitmap flipVertical(Bitmap bitmap) {
        Matrix matrix = new Matrix();
        matrix.postScale(1.0f, -1.0f, bitmap.getWidth() / 2, bitmap.getHeight() / 2);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, false);
    }

    private static Bitmap flipHorizontal(Bitmap bitmap) {
        Matrix matrix = new Matrix();
        matrix.postScale(-1.0f, 1.0f, bitmap.getWidth() / 2, bitmap.getHeight() / 2);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, false);
    }
}
