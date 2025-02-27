package com.gallery.photos.editpic.ImageEDITModule.edit.constants;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Environment;

import com.gallery.photos.editpic.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class StoreManager {

    private static final String BITMAP_CROPPED_FILE_NAME = "temp_cropped_bitmap.png";
    private static final String BITMAP_CROPPED_MASK_FILE_NAME = "temp_cropped_mask_bitmap.png";
    private static final String BITMAP_FILE_NAME = "temp_bitmap.png";
    private static final String BITMAP_ORIGINAL_FILE_NAME = "temp_original_bitmap.png";

    public static int croppedLeft = 0;
    public static int croppedTop = 0;
    public static boolean isNull = false;

    public static Bitmap getCurrentCroppedMaskBitmap(Activity activity) {
        return isNull ? null : getBitmapByFileName(activity, BITMAP_CROPPED_MASK_FILE_NAME);
    }

    public static Bitmap getCurrentCroppedBitmap(Activity activity) {
        return isNull ? null : getBitmapByFileName(activity, BITMAP_CROPPED_FILE_NAME);
    }

    public static Bitmap getCurrentOriginalBitmap(Activity activity) {
        return getBitmapByFileName(activity, BITMAP_ORIGINAL_FILE_NAME);
    }

    private static Bitmap getBitmapByFileName(Activity activity, String fileName) {
        String filePath = getWorkspaceDirPath(activity) + File.separator + fileName;
        return BitmapFactory.decodeFile(filePath);
    }

    public static String getWorkspaceDirPath(Context context) {
        if (Build.VERSION.SDK_INT < 29) {
            return Environment.getExternalStorageDirectory().getAbsolutePath() +
                    context.getResources().getString(R.string.directory);
        }
        File externalDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return (externalDir != null) ? externalDir.getAbsolutePath() : "";
    }

    public static String getWorkspaceDirPathWithSeparator(Context context) {
        return getWorkspaceDirPath(context) + File.separator;
    }

    public static void saveFile(Context context, Bitmap bitmap, String fileName) {
        if (bitmap == null) return;

        File directory = new File(getWorkspaceDirPath(context));
        if (!directory.exists()) {
            directory.mkdirs();
        }

        File file = new File(directory, fileName);
        try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
            fileOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void deleteFile(Context context, String fileName) {
        File file = new File(getWorkspaceDirPath(context) + File.separator + fileName);
        if (file.exists()) {
            file.delete();
        }
    }

    public static void setCurrentCroppedBitmap(Activity activity, Bitmap bitmap) {
        if (bitmap == null) {
            deleteFile(activity, BITMAP_CROPPED_FILE_NAME);
            isNull = true;
        } else {
            isNull = false;
            saveFile(activity, bitmap, BITMAP_CROPPED_FILE_NAME);
        }
    }

    public static void setCurrentCroppedMaskBitmap(Activity activity, Bitmap bitmap) {
        if (bitmap == null) {
            deleteFile(activity, BITMAP_CROPPED_MASK_FILE_NAME);
        } else {
            saveFile(activity, bitmap, BITMAP_CROPPED_MASK_FILE_NAME);
        }
    }

    public static void setCurrentOriginalBitmap(Activity activity, Bitmap bitmap) {
        if (bitmap == null) {
            deleteFile(activity, BITMAP_ORIGINAL_FILE_NAME);
        } else {
            saveFile(activity, bitmap, BITMAP_ORIGINAL_FILE_NAME);
        }
    }
}
