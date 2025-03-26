package com.gallery.photos.editpic.ImageEDITModule.edit.utils;

import android.graphics.Bitmap;
import android.util.Log;

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

    public static Bitmap getBlackAndWhiteImageFromBitmap(Bitmap originalBitmap) {
        // Add null and dimension checks
        if (originalBitmap == null || originalBitmap.isRecycled()) {
            Log.e("FilterUtils", "Original bitmap is null or recycled");
            return null;
        }

        if (originalBitmap.getWidth() <= 0 || originalBitmap.getHeight() <= 0) {
            Log.e("FilterUtils", "Invalid bitmap dimensions");
            return null;
        }

        try {
            CGEImageHandler handler = new CGEImageHandler();
            handler.initWithBitmap(originalBitmap);
            handler.setFilterWithConfig("@adjust lut 3dlut/bw.png");

            // Add additional safety check
            Bitmap result = handler.getResultBitmap();
            if (result == null) {
                Log.e("FilterUtils", "Failed to get result bitmap");
                return originalBitmap.copy(Bitmap.Config.ARGB_8888, true);
            }
            return result;
        } catch (Exception e) {
            Log.e("FilterUtils", "Error processing bitmap", e);
            return originalBitmap.copy(Bitmap.Config.ARGB_8888, true);
        }
    }}
