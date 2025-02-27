package com.gallery.photos.editpic.ImageEDITModule.edit.utils;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.provider.MediaStore;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;

public class CapturePhotoUtils {

    public interface PhotoLoadResponse {
        void loadResponse(Bitmap bitmap, int width, int height);
    }

    private static String saveImage(Bitmap bitmap, String fileName, ContentResolver contentResolver) {
        if (bitmap == null || contentResolver == null) {
            return null;
        }

        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
        contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
        contentValues.put(MediaStore.Images.Media.RELATIVE_PATH, "DCIM/");

        Uri imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
        if (imageUri == null) {
            return null;
        }

        try (OutputStream outputStream = contentResolver.openOutputStream(imageUri)) {
            if (outputStream != null) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return imageUri.toString();
    }

    public static String insertImage(ContentResolver contentResolver, Bitmap bitmap, String title, String description) {
        if (bitmap == null || contentResolver == null) {
            return null;
        }

        if (android.os.Build.VERSION.SDK_INT >= 29) {
            return saveImage(bitmap, title, contentResolver);
        }

        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE, title);
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, description);
        contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
        contentValues.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis() / 1000);
        contentValues.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        contentValues.put(MediaStore.Images.Media.DATE_MODIFIED, System.currentTimeMillis() / 1000);

        Uri imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
        if (imageUri == null) {
            return null;
        }

        try (OutputStream outputStream = contentResolver.openOutputStream(imageUri)) {
            if (outputStream != null) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            }
        } catch (IOException e) {
            contentResolver.delete(imageUri, null, null);
            e.printStackTrace();
            return null;
        }

        return imageUri.toString();
    }

    private static Bitmap storeThumbnail(ContentResolver contentResolver, Bitmap bitmap, long imageId, int kind) {
        if (bitmap == null || contentResolver == null) {
            return null;
        }

        Matrix matrix = new Matrix();
        float scaleX = 50.0f / bitmap.getWidth();
        float scaleY = 50.0f / bitmap.getHeight();
        matrix.setScale(scaleX, scaleY);

        Bitmap thumbnail = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Thumbnails.KIND, kind);
        contentValues.put(MediaStore.Images.Thumbnails.IMAGE_ID, imageId);
        contentValues.put(MediaStore.Images.Thumbnails.HEIGHT, thumbnail.getHeight());
        contentValues.put(MediaStore.Images.Thumbnails.WIDTH, thumbnail.getWidth());

        try (OutputStream outputStream = contentResolver.openOutputStream(contentResolver.insert(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, contentValues))) {
            if (outputStream != null) {
                thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return thumbnail;
    }

    public static void getBitmapFromDisk(final int width, final int height, final String fileName, final PhotoLoadResponse response, final Activity activity) {
        new Thread(() -> {
            try {
                File file = new File(activity.getFilesDir(), fileName);
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inMutable = true;
                Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(file), null, options);

                if (bitmap == null) {
                    activity.runOnUiThread(() -> response.loadResponse(null, width, height));
                    return;
                }

                if (!bitmap.isMutable()) {
                    Bitmap mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
                    bitmap.recycle();
                    bitmap = mutableBitmap;
                }

                final Bitmap finalBitmap = bitmap;
                activity.runOnUiThread(() -> response.loadResponse(finalBitmap, width, height));

            } catch (FileNotFoundException e) {
                activity.runOnUiThread(() -> response.loadResponse(null, width, height));
                e.printStackTrace();
            }
        }).start();
    }
}
