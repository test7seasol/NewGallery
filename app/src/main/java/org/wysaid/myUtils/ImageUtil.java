package org.wysaid.myUtils;

import android.graphics.Bitmap;
import android.media.FaceDetector;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/* loaded from: classes4.dex */
public class ImageUtil extends FileUtil {

    public static class FaceRects {
        public FaceDetector.Face[] faces;
        public int numOfFaces;
    }

    public static String saveBitmap(Bitmap bitmap) {
        return saveBitmap(bitmap, getPath() + "/" + System.currentTimeMillis() + ".jpg");
    }

    public static String saveBitmap(Bitmap bitmap, String str) {
        Log.i("libCGE_java", "saving Bitmap : " + str);
        try {
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(str));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bufferedOutputStream);
            bufferedOutputStream.flush();
            bufferedOutputStream.close();
            Log.i("libCGE_java", "Bitmap " + str + " saved!");
            return str;
        } catch (IOException e) {
            Log.e("libCGE_java", "Err when saving bitmap...");
            e.printStackTrace();
            return null;
        }
    }

    public static FaceRects findFaceByBitmap(Bitmap bitmap) {
        return findFaceByBitmap(bitmap, 1);
    }

    public static FaceRects findFaceByBitmap(Bitmap bitmap, int i) {
        if (bitmap == null) {
            Log.e("libCGE_java", "Invalid Bitmap for Face Detection!");
            return null;
        }
        Bitmap copy = bitmap.getConfig() != Bitmap.Config.RGB_565 ? bitmap.copy(Bitmap.Config.RGB_565, false) : bitmap;
        FaceRects faceRects = new FaceRects();
        faceRects.faces = new FaceDetector.Face[i];
        try {
            faceRects.numOfFaces = new FaceDetector(copy.getWidth(), copy.getHeight(), i).findFaces(copy, faceRects.faces);
            if (copy != bitmap) {
                copy.recycle();
            }
            return faceRects;
        } catch (Exception e) {
            Log.e("libCGE_java", "findFaceByBitmap error: " + e.getMessage());
            return null;
        }
    }
}
