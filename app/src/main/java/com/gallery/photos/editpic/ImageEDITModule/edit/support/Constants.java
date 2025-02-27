package com.gallery.photos.editpic.ImageEDITModule.edit.support;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import androidx.exifinterface.media.ExifInterface;

import com.gallery.photos.editpic.ImageEDITModule.edit.utils.ImageUtils;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/* loaded from: classes.dex */
public class Constants {
    public static String KEY_OPEN_FROM = "openFrom";
    public static String TEMP_FOLDER_NAME = "temp";
    public static String VALUE_OPEN_FROM_ART = "openFromArt";
    public static String VALUE_OPEN_FROM_BG_CHANGE = "openFromBgChange";
    public static String VALUE_OPEN_FROM_DRIP = "openFromDrip";
    public static String VALUE_OPEN_FROM_HOME = "openFromHome";
    public static String VALUE_OPEN_FROM_HOME_CHANGE = "openFromChange";
    public static String VALUE_OPEN_FROM_HOME_NEON = "openFromHomeNeon";
    public static String VALUE_OPEN_FROM_HOME_REMOVE = "openFromRemove";
    public static String VALUE_OPEN_FROM_NEON = "openFromNeon";
    public static String VALUE_OPEN_FROM_REMOVE_BG = "openFromRemoveBg";
    public static String VALUE_OPEN_FROM_TOOL = "openFromTool";
    public static String VALUE_OPEN_FROM_TOOL_CHANGE = "openFromChange";

    public Constants(Context context) {
    }

    public static String convertMediaUriToPath(Context context, Uri uri) {
        if (Build.VERSION.SDK_INT < 24) {
            return getPath(context, uri);
        }
        return getFilePathForN(uri, context);
    }

    private static String getFilePathForN(Uri uri, Context context) {
        Cursor query = context.getContentResolver().query(uri, null, null, null, null);
        int columnIndex = query.getColumnIndex("_display_name");
        int columnIndex2 = query.getColumnIndex("_size");
        query.moveToFirst();
        String string = query.getString(columnIndex);
        Long.toString(query.getLong(columnIndex2));
        File file = new File(context.getFilesDir(), string);
        try {
            InputStream openInputStream = context.getContentResolver().openInputStream(uri);
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            byte[] bArr = new byte[Math.min(openInputStream.available(), 1048576)];
            while (true) {
                int read = openInputStream.read(bArr);
                if (read == -1) {
                    break;
                }
                fileOutputStream.write(bArr, 0, read);
            }
            Log.e("File Size", "Size " + file.length());
            openInputStream.close();
            fileOutputStream.close();
            Log.e("File Path", "Path " + file.getPath());
            Log.e("File Size", "Size " + file.length());
        } catch (Exception e) {
            Log.e("Exception", e.getMessage());
        }
        return file.getPath();
    }

    public static Bitmap getBitmapFromUri(Context context, Uri uri, float f, float f2) throws IOException {
        try {
            ParcelFileDescriptor openFileDescriptor = context.getContentResolver().openFileDescriptor(uri, "r");
            FileDescriptor fileDescriptor = openFileDescriptor.getFileDescriptor();
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options);
            BitmapFactory.Options options2 = new BitmapFactory.Options();
            if (f <= f2) {
                f = f2;
            }
            int i = (int) f;
            options2.inSampleSize = ImageUtils.getClosestResampleSize(options.outWidth, options.outHeight, i);
            Bitmap decodeFileDescriptor = BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options2);
            Matrix matrix = new Matrix();
            if (decodeFileDescriptor.getWidth() > i || decodeFileDescriptor.getHeight() > i) {
                BitmapFactory.Options resampling = ImageUtils.getResampling(decodeFileDescriptor.getWidth(), decodeFileDescriptor.getHeight(), i);
                matrix.postScale(resampling.outWidth / decodeFileDescriptor.getWidth(), resampling.outHeight / decodeFileDescriptor.getHeight());
            }
            openFileDescriptor.close();
            return convertMediaUriToPath(context, uri) != null ? modifyOrientation(decodeFileDescriptor, convertMediaUriToPath(context, uri)) : decodeFileDescriptor;
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Bitmap modifyOrientation(Bitmap bitmap, String str) throws IOException {
        int attributeInt = new ExifInterface(str).getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
        if (attributeInt == 2) {
            return flip(bitmap, true, false);
        }
        if (attributeInt == 3) {
            return rotate(bitmap, 180.0f);
        }
        if (attributeInt == 4) {
            return flip(bitmap, false, true);
        }
        if (attributeInt == 6) {
            return rotate(bitmap, 90.0f);
        }
        return attributeInt != 8 ? bitmap : rotate(bitmap, 270.0f);
    }

    public static Bitmap flip(Bitmap bitmap, boolean z, boolean z2) {
        Matrix matrix = new Matrix();
        matrix.preScale(z ? -1.0f : 1.0f, z2 ? -1.0f : 1.0f);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    public static String getPath(Context context, Uri uri) {
        Cursor query = context.getContentResolver().query(uri, new String[]{"_data"}, null, null, null);
        if (query == null) {
            return null;
        }
        int columnIndexOrThrow = query.getColumnIndexOrThrow("_data");
        query.moveToFirst();
        String string = query.getString(columnIndexOrThrow);
        query.close();
        return string;
    }

    public static Bitmap rotate(Bitmap bitmap, float f) {
        Matrix matrix = new Matrix();
        matrix.postRotate(f);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    public static int dpToPx(Context context, int i) {
        context.getResources();
        return (int) (Resources.getSystem().getDisplayMetrics().density * i);
    }
}
