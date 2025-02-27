package org.wysaid.common;

import android.opengl.GLES20;
import android.util.Log;

/* loaded from: classes4.dex */
public class ProgramObject {
    public static final String LOG_TAG = "libCGE_java";
    private ShaderObject mFragmentShader;
    private int mProgramID;
    private ShaderObject mVertexShader;

    public ProgramObject() {
        this.mProgramID = GLES20.glCreateProgram();
    }

    public ProgramObject(String str, String str2) {
        init(str, str2);
    }

    public int programID() {
        return this.mProgramID;
    }

    public final void release() {
        int i = this.mProgramID;
        if (i != 0) {
            GLES20.glDeleteProgram(i);
            this.mProgramID = 0;
        }
    }

    public boolean init(String str, String str2) {
        return init(str, str2, this.mProgramID);
    }

    public boolean init(String str, String str2, int i) {
        if (i == 0 && (i = GLES20.glCreateProgram()) == 0) {
            Log.e("libCGE_java", "Invalid Program ID! Check if the context is bound!");
            return false;
        }
        ShaderObject shaderObject = this.mVertexShader;
        if (shaderObject != null) {
            shaderObject.release();
        }
        ShaderObject shaderObject2 = this.mFragmentShader;
        if (shaderObject2 != null) {
            shaderObject2.release();
        }
        this.mVertexShader = new ShaderObject(str, 35633);
        this.mFragmentShader = new ShaderObject(str2, 35632);
        GLES20.glAttachShader(i, this.mVertexShader.shaderID());
        GLES20.glAttachShader(i, this.mFragmentShader.shaderID());
        Common.checkGLError("AttachShaders...");
        GLES20.glLinkProgram(i);
        int[] iArr = {0};
        GLES20.glGetProgramiv(i, 35714, iArr, 0);
        this.mVertexShader.release();
        this.mFragmentShader.release();
        this.mVertexShader = null;
        this.mFragmentShader = null;
        if (iArr[0] != 1) {
            Log.e("libCGE_java", GLES20.glGetProgramInfoLog(i));
            return false;
        }
        int i2 = this.mProgramID;
        if (i2 != i && i2 != 0) {
            GLES20.glDeleteProgram(i2);
        }
        this.mProgramID = i;
        return true;
    }

    public void bind() {
        GLES20.glUseProgram(this.mProgramID);
    }

    public int getUniformLoc(String str) {
        int glGetUniformLocation = GLES20.glGetUniformLocation(this.mProgramID, str);
        if (glGetUniformLocation < 0) {
            Log.e("libCGE_java", String.format("uniform name %s does not exist", str));
        }
        return glGetUniformLocation;
    }

    public void sendUniformf(String str, float f) {
        GLES20.glUniform1f(getUniformLoc(str), f);
    }

    public void sendUniformf(String str, float f, float f2) {
        GLES20.glUniform2f(getUniformLoc(str), f, f2);
    }

    public void sendUniformf(String str, float f, float f2, float f3) {
        GLES20.glUniform3f(getUniformLoc(str), f, f2, f3);
    }

    public void sendUniformf(String str, float f, float f2, float f3, float f4) {
        GLES20.glUniform4f(getUniformLoc(str), f, f2, f3, f4);
    }

    public void sendUniformi(String str, int i) {
        GLES20.glUniform1i(getUniformLoc(str), i);
    }

    public void sendUniformi(String str, int i, int i2) {
        GLES20.glUniform2i(getUniformLoc(str), i, i2);
    }

    public void sendUniformi(String str, int i, int i2, int i3) {
        GLES20.glUniform3i(getUniformLoc(str), i, i2, i3);
    }

    public void sendUniformi(String str, int i, int i2, int i3, int i4) {
        GLES20.glUniform4i(getUniformLoc(str), i, i2, i3, i4);
    }

    public void sendUniformMat2(String str, int i, boolean z, float[] fArr) {
        GLES20.glUniformMatrix2fv(getUniformLoc(str), i, z, fArr, 0);
    }

    public void sendUniformMat3(String str, int i, boolean z, float[] fArr) {
        GLES20.glUniformMatrix3fv(getUniformLoc(str), i, z, fArr, 0);
    }

    public void sendUniformMat4(String str, int i, boolean z, float[] fArr) {
        GLES20.glUniformMatrix4fv(getUniformLoc(str), i, z, fArr, 0);
    }

    public int attributeLocation(String str) {
        return GLES20.glGetAttribLocation(this.mProgramID, str);
    }

    public void bindAttribLocation(String str, int i) {
        GLES20.glBindAttribLocation(this.mProgramID, i, str);
    }

    public static class ShaderObject {
        static final   boolean $assertionsDisabled = false;
        private int mShaderID;
        private int mShaderType;

        public int shaderID() {
            return this.mShaderID;
        }

        public ShaderObject() {
            this.mShaderType = 0;
            this.mShaderID = 0;
        }

        public ShaderObject(String str, int i) {
            init(str, i);
        }

        public boolean init(String str, int i) {
            this.mShaderType = i;
            int loadShader = loadShader(i, str);
            this.mShaderID = loadShader;
            if (loadShader != 0) {
                return true;
            }
            Log.e("libCGE_java", "glCreateShader Failed!...");
            return false;
        }

        public final void release() {
            int i = this.mShaderID;
            if (i == 0) {
                return;
            }
            GLES20.glDeleteShader(i);
            this.mShaderID = 0;
        }

        public static int loadShader(int i, String str) {
            int glCreateShader = GLES20.glCreateShader(i);
            if (glCreateShader != 0) {
                GLES20.glShaderSource(glCreateShader, str);
                GLES20.glCompileShader(glCreateShader);
                int[] iArr = {0};
                GLES20.glGetShaderiv(glCreateShader, 35713, iArr, 0);
                if (iArr[0] != 1) {
                    Log.e("libCGE_java", GLES20.glGetShaderInfoLog(glCreateShader));
                    GLES20.glDeleteShader(glCreateShader);
                    return 0;
                }
            }
            return glCreateShader;
        }
    }
}
