package org.wysaid.nativePort;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

/* loaded from: classes4.dex */
public class CGEMultiInputFilterWrapper {
    IntBuffer mInputTextureBuffer;
    private long mNativeAddress = 0;

    protected static native long nativeCreate(String str, String str2);

    protected static native void nativeRelease(long j);

    protected native void nativeUpdateInputTextures(long j, IntBuffer intBuffer, int i);

    static {
        NativeLibraryLoader.load();
    }

    private CGEMultiInputFilterWrapper() {
    }

    public static CGEMultiInputFilterWrapper create(String str, String str2) {
        CGEMultiInputFilterWrapper cGEMultiInputFilterWrapper = new CGEMultiInputFilterWrapper();
        long nativeCreate = nativeCreate(str, str2);
        cGEMultiInputFilterWrapper.mNativeAddress = nativeCreate;
        if (nativeCreate == 0) {
            return null;
        }
        return cGEMultiInputFilterWrapper;
    }

    public void release(boolean z) {
        long j = this.mNativeAddress;
        if (j != 0) {
            if (z) {
                nativeRelease(j);
            }
            this.mNativeAddress = 0L;
        }
    }

    public long getNativeAddress() {
        return this.mNativeAddress;
    }

    public void updateInputTextures(IntBuffer intBuffer, int i) {
        nativeUpdateInputTextures(this.mNativeAddress, intBuffer, i);
    }

    public void updateInputTextures(int[] iArr) {
        IntBuffer intBuffer = this.mInputTextureBuffer;
        if (intBuffer == null || intBuffer.capacity() < iArr.length) {
            this.mInputTextureBuffer = ByteBuffer.allocateDirect(iArr.length * 4).order(ByteOrder.nativeOrder()).asIntBuffer();
        }
        this.mInputTextureBuffer.put(iArr);
        this.mInputTextureBuffer.position(0);
        nativeUpdateInputTextures(this.mNativeAddress, this.mInputTextureBuffer, iArr.length);
    }
}
