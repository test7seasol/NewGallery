package org.wysaid.nativePort;

import android.graphics.Bitmap;

/* loaded from: classes4.dex */
public class CGEImageHandler {
    protected long mNativeAddress = nativeCreateHandler();

    protected native void nativeBindTargetFBO(long j);

    protected native long nativeCreateHandler();

    protected native void nativeDrawResult(long j);

    protected native Bitmap nativeGetResultBitmap(long j);

    protected native boolean nativeInitWithBitmap(long j, Bitmap bitmap);

    protected native boolean nativeInitWithSize(long j, int i, int i2);

    protected native void nativeProcessWithFilter(long j, long j2);

    protected native void nativeProcessingFilters(long j);

    protected native void nativeRelease(long j);

    protected native void nativeRevertImage(long j);

    protected native void nativeSetAsTarget(long j);

    protected native void nativeSetDrawerFlipScale(long j, float f, float f2);

    protected native void nativeSetDrawerRotation(long j, float f);

    protected native void nativeSetFilterIntensity(long j, float f, boolean z);

    protected native boolean nativeSetFilterIntensityAtIndex(long j, float f, int i, boolean z);

    protected native void nativeSetFilterWithAddress(long j, long j2);

    protected native boolean nativeSetFilterWithConfig(long j, String str, boolean z, boolean z2);

    protected native void nativeSwapBufferFBO(long j);

    static {
        NativeLibraryLoader.load();
    }

    public boolean initWithBitmap(Bitmap bitmap) {
        if (bitmap == null) {
            return false;
        }
        if (bitmap.getConfig() != Bitmap.Config.ARGB_8888) {
            bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, false);
        }
        return nativeInitWithBitmap(this.mNativeAddress, bitmap);
    }

    public boolean initWithSize(int i, int i2) {
        return nativeInitWithSize(this.mNativeAddress, i, i2);
    }

    public Bitmap getResultBitmap() {
        return nativeGetResultBitmap(this.mNativeAddress);
    }

    public void setDrawerRotation(float f) {
        nativeSetDrawerRotation(this.mNativeAddress, f);
    }

    public void setDrawerFlipScale(float f, float f2) {
        nativeSetDrawerFlipScale(this.mNativeAddress, f, f2);
    }

    public void setFilterWithConfig(String str) {
        nativeSetFilterWithConfig(this.mNativeAddress, str, true, true);
    }

    public void setFilterWithConfig(String str, boolean z, boolean z2) {
        nativeSetFilterWithConfig(this.mNativeAddress, str, z, z2);
    }

    public void setFilterIntensity(float f) {
        nativeSetFilterIntensity(this.mNativeAddress, f, true);
    }

    public void setFilterIntensity(float f, boolean z) {
        nativeSetFilterIntensity(this.mNativeAddress, f, z);
    }

    public boolean setFilterIntensityAtIndex(float f, int i, boolean z) {
        return nativeSetFilterIntensityAtIndex(this.mNativeAddress, f, i, z);
    }

    public void drawResult() {
        nativeDrawResult(this.mNativeAddress);
    }

    public void bindTargetFBO() {
        nativeBindTargetFBO(this.mNativeAddress);
    }

    public void setAsTarget() {
        nativeSetAsTarget(this.mNativeAddress);
    }

    public void swapBufferFBO() {
        nativeSwapBufferFBO(this.mNativeAddress);
    }

    public void revertImage() {
        nativeRevertImage(this.mNativeAddress);
    }

    public void processFilters() {
        nativeProcessingFilters(this.mNativeAddress);
    }

    public void processWithFilter(long j) {
        nativeProcessWithFilter(this.mNativeAddress, j);
    }

    public void release() {
        long j = this.mNativeAddress;
        if (j != 0) {
            nativeRelease(j);
            this.mNativeAddress = 0L;
        }
    }

    public void setFilterWithAddres(long j) {
        nativeSetFilterWithAddress(this.mNativeAddress, j);
    }
}
