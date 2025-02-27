package com.gallery.photos.editpic.ImageEDITModule.edit.sticker;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.RectF;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/* loaded from: classes.dex */
class StickerUtils {
    StickerUtils() {
    }

    public static void notifySystemGallery(Context context, File file) {
        if (file != null && file.exists()) {
            try {
                MediaStore.Images.Media.insertImage(context.getContentResolver(), file.getAbsolutePath(), file.getName(), (String) null);
                context.sendBroadcast(new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE", Uri.fromFile(file)));
                return;
            } catch (FileNotFoundException unused) {
                throw new IllegalStateException("File couldn't be found");
            }
        }
        throw new IllegalArgumentException("bmp should not be null");
    }

    public static File saveImageToGallery(File file, Bitmap bitmap) {
        if (bitmap != null) {
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
                fileOutputStream.flush();
                fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.e("StickerView", "saveImageToGallery: the path of bmp is " + file.getAbsolutePath());
            return file;
        }
        throw new IllegalArgumentException("bmp should not be null");
    }

    public static void trapToRect(RectF rectF, float[] fArr) {
        rectF.set(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY, Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY);
        for (int i = 1; i < fArr.length; i += 2) {
            float round = Math.round(fArr[i - 1] * 10.0f) / 10.0f;
            float round2 = Math.round(fArr[i] * 10.0f) / 10.0f;
            rectF.left = round < rectF.left ? round : rectF.left;
            rectF.top = round2 < rectF.top ? round2 : rectF.top;
            if (round <= rectF.right) {
                round = rectF.right;
            }
            rectF.right = round;
            if (round2 <= rectF.bottom) {
                round2 = rectF.bottom;
            }
            rectF.bottom = round2;
        }
        rectF.sort();
    }
}
