package org.wysaid.common;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;

import java.nio.FloatBuffer;

/* loaded from: classes4.dex */
public class Common {
    public static final String BUILD_COMMIT = "0c44a45bbaef0b4830b77563bc0c2a91e8cf5f47";
    public static final boolean DEBUG = true;
    public static final float[] FULLSCREEN_VERTICES = {-1.0f, -1.0f, 1.0f, -1.0f, 1.0f, 1.0f, -1.0f, 1.0f};
    public static final String LOG_TAG = "libCGE_java";

    public static void checkGLError(String str) {
        String str2;
        int glGetError = GLES20.glGetError();
        for (int i = 0; i < 32 && glGetError != 0; i++) {
            switch (glGetError) {
                case 1280:
                    str2 = "invalid enum";
                    break;
                case 1281:
                    str2 = "invalid value";
                    break;
                case 1282:
                    str2 = "invalid operation";
                    break;
                case 1283:
                case 1284:
                default:
                    str2 = "unknown error";
                    break;
                case 1285:
                    str2 = "out of memory";
                    break;
                case 1286:
                    str2 = "invalid framebuffer operation";
                    break;
            }
            Log.e("libCGE_java", String.format("After tag \"%s\" glGetError %s(0x%x) ", str, str2, Integer.valueOf(glGetError)));
            glGetError = GLES20.glGetError();
        }
    }

    public static void texParamHelper(int i, int i2, int i3) {
        float f = i2;
        GLES20.glTexParameterf(i, 10241, f);
        GLES20.glTexParameterf(i, 10240, f);
        GLES20.glTexParameteri(i, 10242, i3);
        GLES20.glTexParameteri(i, 10243, i3);
    }

    public static int genBlankTextureID(int i, int i2) {
        return genBlankTextureID(i, i2, 9729, 33071);
    }

    public static int genBlankTextureID(int i, int i2, int i3, int i4) {
        int[] iArr = new int[1];
        GLES20.glGenTextures(1, iArr, 0);
        GLES20.glBindTexture(3553, iArr[0]);
        GLES20.glTexImage2D(3553, 0, 6408, i, i2, 0, 6408, 5121, null);
        texParamHelper(3553, i3, i4);
        return iArr[0];
    }

    public static int genNormalTextureID(Bitmap bitmap) {
        return genNormalTextureID(bitmap, 9729, 33071);
    }

    public static int genNormalTextureID(Bitmap bitmap, int i, int i2) {
        int[] iArr = new int[1];
        GLES20.glGenTextures(1, iArr, 0);
        GLES20.glBindTexture(3553, iArr[0]);
        GLUtils.texImage2D(3553, 0, bitmap, 0);
        texParamHelper(3553, i, i2);
        return iArr[0];
    }

    public static int genSurfaceTextureID() {
        int[] iArr = new int[1];
        GLES20.glGenTextures(1, iArr, 0);
        GLES20.glBindTexture(36197, iArr[0]);
        texParamHelper(36197, 9729, 33071);
        return iArr[0];
    }

    public static void deleteTextureID(int i) {
        GLES20.glDeleteTextures(1, new int[]{i}, 0);
    }

    public static int genFullscreenVertexArrayBuffer() {
        int[] iArr = new int[1];
        GLES20.glGenBuffers(1, iArr, 0);
        int i = iArr[0];
        if (i == 0) {
            Log.e("libCGE_java", "Invalid VertexBuffer! You must call this within an OpenGL thread!");
            return 0;
        }
        GLES20.glBindBuffer(34962, i);
        float[] fArr = FULLSCREEN_VERTICES;
        FloatBuffer allocate = FloatBuffer.allocate(fArr.length);
        allocate.put(fArr).position(0);
        GLES20.glBufferData(34962, 32, allocate, 35044);
        return iArr[0];
    }
}
