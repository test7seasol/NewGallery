package com.gallery.photos.editpic.ImageEDITModule.edit.constants;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;

/* loaded from: classes.dex */
public class Constants {
    public static int BORDER_WIDTH = 5;
    public static String FACEBOOK = "com.facebook.katana";
    public static String INSTAGRAM = "com.instagram.android";
    public static String MESSEGER = "com.facebook.orca";
    public static String TWITTER = "com.twitter.android";
    public static String WHATSAPP = "com.whatsapp";
    public static Bitmap bitmap;
    public static Context context;

    public static String getPath(Context context2, Uri uri) {
        Cursor query = context2.getContentResolver().query(uri, new String[]{"_data"}, null, null, null);
        if (query == null) {
            return null;
        }
        int columnIndexOrThrow = query.getColumnIndexOrThrow("_data");
        query.moveToFirst();
        String string = query.getString(columnIndexOrThrow);
        query.close();
        return string;
    }

    public static Bitmap rotate(Bitmap bitmap2, float f) {
        Matrix matrix = new Matrix();
        matrix.postRotate(f);
        return Bitmap.createBitmap(bitmap2, 0, 0, bitmap2.getWidth(), bitmap2.getHeight(), matrix, true);
    }

    public static Bitmap flip(Bitmap bitmap2, boolean z, boolean z2) {
        Matrix matrix = new Matrix();
        matrix.preScale(z ? -1.0f : 1.0f, z2 ? -1.0f : 1.0f);
        return Bitmap.createBitmap(bitmap2, 0, 0, bitmap2.getWidth(), bitmap2.getHeight(), matrix, true);
    }

    public static int dpToPx(Context context2, int i) {
        context2.getResources();
        return (int) (Resources.getSystem().getDisplayMetrics().density * i);
    }
}
