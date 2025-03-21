package com.gallery.photos.editpic.ImageEDITModule.edit.utils;

import android.graphics.Bitmap;
import java.text.MessageFormat;
import org.wysaid.common.SharedContext;
import org.wysaid.nativePort.CGEImageHandler;

/* loaded from: classes.dex */
public class FilterUtils {
    public static Bitmap getBlurImageFromBitmap(Bitmap bitmap) {
        SharedContext create = SharedContext.create();
        create.makeCurrent();
        CGEImageHandler cGEImageHandler = new CGEImageHandler();
        cGEImageHandler.initWithBitmap(bitmap);
        cGEImageHandler.setFilterWithConfig("@blur lerp 0.6");
        cGEImageHandler.processFilters();
        Bitmap resultBitmap = cGEImageHandler.getResultBitmap();
        create.release();
        return resultBitmap;
    }

    public static Bitmap getBlurImageFromBitmap(Bitmap bitmap, float f) {
        SharedContext create = SharedContext.create();
        create.makeCurrent();
        CGEImageHandler cGEImageHandler = new CGEImageHandler();
        cGEImageHandler.initWithBitmap(bitmap);
        cGEImageHandler.setFilterWithConfig(MessageFormat.format("@blur lerp {0}", (f / 10.0f) + ""));
        cGEImageHandler.processFilters();
        Bitmap resultBitmap = cGEImageHandler.getResultBitmap();
        create.release();
        return resultBitmap;
    }

    public static Bitmap cloneBitmap(Bitmap bitmap) {
        SharedContext create = SharedContext.create();
        create.makeCurrent();
        CGEImageHandler cGEImageHandler = new CGEImageHandler();
        cGEImageHandler.initWithBitmap(bitmap);
        cGEImageHandler.setFilterWithConfig("");
        cGEImageHandler.processFilters();
        Bitmap resultBitmap = cGEImageHandler.getResultBitmap();
        create.release();
        return resultBitmap;
    }

    public static Bitmap getBlackAndWhiteImageFromBitmap(Bitmap bitmap) {
        if (bitmap == null || bitmap.getWidth() <= 0 || bitmap.getHeight() <= 0) {
            return null; // Or return a default bitmap instead
        }

        SharedContext create = SharedContext.create();
        create.makeCurrent();

        CGEImageHandler cGEImageHandler = new CGEImageHandler();
        cGEImageHandler.initWithBitmap(bitmap);
        cGEImageHandler.setFilterWithConfig("@adjust saturation 0");
        cGEImageHandler.processFilters();

        Bitmap resultBitmap = cGEImageHandler.getResultBitmap();

        create.release();

        return (resultBitmap != null && resultBitmap.getWidth() > 0 && resultBitmap.getHeight() > 0)
                ? resultBitmap
                : bitmap; // Return original bitmap if processing failed
    }
}
