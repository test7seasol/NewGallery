package org.wysaid.nativePort;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import org.wysaid.common.Common;

/* loaded from: classes4.dex */
public class CGENativeLibrary {
    static Object callbackArg;
    static LoadImageCallback loadImageCallback;

    public enum BlendFilterType {
        BLEND_NORMAL,
        BLEND_KEEP_RATIO,
        BLEND_TILE
    }

    public interface LoadImageCallback {
        Bitmap loadImage(String str, Object obj);

        void loadImageOK(Bitmap bitmap, Object obj);
    }

    public enum TextureBlendMode {
        CGE_BLEND_MIX,
        CGE_BLEND_DISSOLVE,
        CGE_BLEND_DARKEN,
        CGE_BLEND_MULTIPLY,
        CGE_BLEND_COLORBURN,
        CGE_BLEND_LINEARBURN,
        CGE_BLEND_DARKER_COLOR,
        CGE_BLEND_LIGHTEN,
        CGE_BLEND_SCREEN,
        CGE_BLEND_COLORDODGE,
        CGE_BLEND_LINEARDODGE,
        CGE_BLEND_LIGHTERCOLOR,
        CGE_BLEND_OVERLAY,
        CGE_BLEND_SOFTLIGHT,
        CGE_BLEND_HARDLIGHT,
        CGE_BLEND_VIVIDLIGHT,
        CGE_BLEND_LINEARLIGHT,
        CGE_BLEND_PINLIGHT,
        CGE_BLEND_HARDMIX,
        CGE_BLEND_DIFFERENCE,
        CGE_BLEND_EXCLUDE,
        CGE_BLEND_SUBTRACT,
        CGE_BLEND_DIVIDE,
        CGE_BLEND_HUE,
        CGE_BLEND_SATURATION,
        CGE_BLEND_COLOR,
        CGE_BLEND_LUMINOSITY,
        CGE_BLEND_ADD,
        CGE_BLEND_ADDREV,
        CGE_BLEND_COLORBW,
        CGE_BLEND_TYPE_MAX_NUM
    }

    public static class TextureResult {
        int height;
        int texID;
        int width;
    }

    public static native long cgeCreateBlendFilter(int i, int i2, int i3, int i4, int i5, float f);

    public static native long cgeCreateCustomNativeFilter(int i, float f, boolean z);

    public static native long cgeCreateFilterWithConfig(String str, float f);

    public static native void cgeDeleteFilterWithAddress(long j);

    public static native Bitmap cgeFilterImageWithCustomFilter(Bitmap bitmap, int i, float f, boolean z, boolean z2);

    public static native Bitmap cgeFilterImage_MultipleEffects(Bitmap bitmap, String str, float f);

    public static native void cgeFilterImage_MultipleEffectsWriteBack(Bitmap bitmap, String str, float f);

    public static native int cgeGetCustomFilterNum();

    static {
        NativeLibraryLoader.load();
    }

    public static void setLoadImageCallback(LoadImageCallback loadImageCallback2, Object obj) {
        loadImageCallback = loadImageCallback2;
        callbackArg = obj;
    }

    public static TextureResult loadTextureByName(String str) {
        LoadImageCallback loadImageCallback2 = loadImageCallback;
        if (loadImageCallback2 == null) {
            Log.i("libCGE_java", "The loading callback is not set!");
            return null;
        }
        Bitmap loadImage = loadImageCallback2.loadImage(str, callbackArg);
        if (loadImage == null) {
            return null;
        }
        TextureResult loadTextureByBitmap = loadTextureByBitmap(loadImage);
        loadImageCallback.loadImageOK(loadImage, callbackArg);
        return loadTextureByBitmap;
    }

    public static TextureResult loadTextureByBitmap(Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }
        TextureResult textureResult = new TextureResult();
        textureResult.texID = Common.genNormalTextureID(bitmap);
        textureResult.width = bitmap.getWidth();
        textureResult.height = bitmap.getHeight();
        return textureResult;
    }

    public static TextureResult loadTextureByFile(String str) {
        Bitmap decodeFile = BitmapFactory.decodeFile(str);
        TextureResult loadTextureByBitmap = loadTextureByBitmap(decodeFile);
        decodeFile.recycle();
        return loadTextureByBitmap;
    }

    public static Bitmap filterImage_MultipleEffects(Bitmap bitmap, String str, float f) {
        return (str == null || str.length() == 0) ? bitmap : cgeFilterImage_MultipleEffects(bitmap, str, f);
    }

    public static void filterImage_MultipleEffectsWriteBack(Bitmap bitmap, String str, float f) {
        if (str == null || str.length() == 0) {
            return;
        }
        cgeFilterImage_MultipleEffectsWriteBack(bitmap, str, f);
    }

    public static long createBlendFilter(TextureBlendMode textureBlendMode, int i, int i2, int i3, BlendFilterType blendFilterType, float f) {
        return cgeCreateBlendFilter(textureBlendMode.ordinal(), i, i2, i3, blendFilterType.ordinal(), f);
    }
}
