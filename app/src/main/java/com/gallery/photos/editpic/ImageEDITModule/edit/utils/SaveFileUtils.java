package com.gallery.photos.editpic.ImageEDITModule.edit.utils;

import android.os.Environment;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;

public class SaveFileUtils {

    private static final String TAG = "SaveFileUtils";

    private static boolean isExternalStorageWritable() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    public static File saveBitmapFileRemove(Context context, Bitmap bitmap, String fileName, String subFolder) throws IOException {
        if (Build.VERSION.SDK_INT >= 29) {
            if (!isExternalStorageWritable()) {
                return null;
            }

            String relativePath = Environment.DIRECTORY_PICTURES;
            if (subFolder != null) {
                relativePath += File.separator + subFolder;
            }

            ContentResolver resolver = context.getContentResolver();
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/png");
            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, relativePath);

            Uri imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
            if (imageUri == null) {
                Log.e(TAG, "Failed to create new MediaStore record.");
                return null;
            }

            try (OutputStream outputStream = resolver.openOutputStream(imageUri)) {
                if (outputStream == null) {
                    Log.e(TAG, "Failed to get output stream.");
                    return null;
                }

                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                outputStream.flush();
                return new File(FilePathUtil.getPath(context, imageUri));
            } catch (IOException e) {
                Log.e(TAG, "File save exception: " + e.getMessage(), e);
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        } else {
            File picturesDir = new File(Environment.getExternalStorageDirectory(), Environment.DIRECTORY_PICTURES);
            if (!picturesDir.exists()) {
                picturesDir.mkdir();
            }

            File imageFile = new File(picturesDir, fileName + ".png");
            try (FileOutputStream outputStream = new FileOutputStream(imageFile)) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                outputStream.flush();
            }

            return imageFile;
        }

        return null;
    }

    public static File saveBitmapFileRemoveBg(Context context, Bitmap bitmap, String fileName, String subFolder) throws IOException {
        if (bitmap == null || bitmap.isRecycled()) {
            Log.e(TAG, "Bitmap is null or already recycled. Skipping save operation.");
            return null;
        }


        if (Build.VERSION.SDK_INT >= 29) {
            if (!isExternalStorageWritable()) {
                return null;
            }

            String relativePath = Environment.DIRECTORY_PICTURES;
            if (subFolder != null) {
                relativePath += File.separator + subFolder;
            }

            ContentResolver resolver = context.getContentResolver();
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, relativePath);

            Uri imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
            if (imageUri == null) {
                Log.e(TAG, "Failed to create new MediaStore record.");
                return null;
            }

            try (OutputStream outputStream = resolver.openOutputStream(imageUri)) {
                if (outputStream == null) {
                    Log.e(TAG, "Failed to get output stream.");
                    return null;
                }

                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                outputStream.flush();
                return new File(FilePathUtil.getPath(context, imageUri));
            } catch (IOException | URISyntaxException e) {
                Log.e(TAG, "File save exception: " + e.getMessage(), e);
            }
        } else {
            File picturesDir = new File(Environment.getExternalStorageDirectory(), Environment.DIRECTORY_PICTURES);
            if (!picturesDir.exists()) {
                picturesDir.mkdir();
            }

            File imageFile = new File(picturesDir, fileName + ".jpeg");
            try (FileOutputStream outputStream = new FileOutputStream(imageFile)) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                outputStream.flush();
            }

            return imageFile;
        }

        return null;
    }
}
