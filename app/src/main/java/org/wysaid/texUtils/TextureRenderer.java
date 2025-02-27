package org.wysaid.texUtils;

import android.opengl.GLES20;
import android.util.Log;

import org.wysaid.common.ProgramObject;

import java.nio.FloatBuffer;

/* loaded from: classes4.dex */
public abstract class TextureRenderer {
    static final   boolean $assertionsDisabled = false;
    public static final int DRAW_FUNCTION = 6;
    protected static final String FLIPSCALE_NAME = "flipScale";
    public static final String LOG_TAG = "libCGE_java";
    protected static final String POSITION_NAME = "vPosition";
    protected static final String REQUIRE_STRING_EXTERNAL_OES = "#extension GL_OES_EGL_image_external : require\n";
    protected static final String ROTATION_NAME = "rotation";
    protected static final String SAMPLER2D_VAR = "sampler2D";
    protected static final String SAMPLER2D_VAR_EXTERNAL_OES = "samplerExternalOES";
    protected static final String TRANSFORM_NAME = "transform";
    public static final float[] vertices = {-1.0f, -1.0f, 1.0f, -1.0f, 1.0f, 1.0f, -1.0f, 1.0f};
    protected static final String vshDrawDefault = "attribute vec2 vPosition;\nvarying vec2 texCoord;\nuniform mat4 transform;\nuniform mat2 rotation;\nuniform vec2 flipScale;\nvoid main()\n{\n   gl_Position = vec4(vPosition, 0.0, 1.0);\n   vec2 coord = flipScale * (vPosition / 2.0 * rotation) + 0.5;\n   texCoord = (transform * vec4(coord, 0.0, 1.0)).xy;\n}";
    protected int TEXTURE_2D_BINDABLE;
    protected int mFlipScaleLoc;
    protected ProgramObject mProgram;
    protected int mRotationLoc;
    protected int mTextureHeight;
    protected int mTextureWidth;
    protected int mTransformLoc;
    protected int mVertexBuffer;

    public abstract String getFragmentShaderString();

    public abstract String getVertexShaderString();

    public abstract boolean init(boolean z);

    public abstract void renderTexture(int i, Viewport viewport);

    public abstract void setTextureSize(int i, int i2);

    public void release() {
        int i = this.mVertexBuffer;
        if (i != 0) {
            GLES20.glDeleteBuffers(1, new int[]{i}, 0);
            this.mVertexBuffer = 0;
        }
        ProgramObject programObject = this.mProgram;
        if (programObject != null) {
            programObject.release();
            this.mProgram = null;
        }
    }

    public static class Viewport {
        public int height;
        public int width;
        public int x;
        public int y;

        public Viewport() {
        }

        public Viewport(int i, int i2, int i3, int i4) {
            this.x = i;
            this.y = i2;
            this.width = i3;
            this.height = i4;
        }
    }

    public void setRotation(float f) {
        double d = f;
        float cos = (float) Math.cos(d);
        float sin = (float) Math.sin(d);
        this.mProgram.bind();
        GLES20.glUniformMatrix2fv(this.mRotationLoc, 1, false, new float[]{cos, sin, -sin, cos}, 0);
    }

    public void setFlipscale(float f, float f2) {
        this.mProgram.bind();
        GLES20.glUniform2f(this.mFlipScaleLoc, f, f2);
    }

    public void setTransform(float[] fArr) {
        this.mProgram.bind();
        GLES20.glUniformMatrix4fv(this.mTransformLoc, 1, false, fArr, 0);
    }

    protected boolean setProgramDefault(String str, String str2, boolean z) {
        this.TEXTURE_2D_BINDABLE = z ? 36197 : 3553;
        ProgramObject programObject = new ProgramObject();
        this.mProgram = programObject;
        programObject.bindAttribLocation(POSITION_NAME, 0);
        StringBuilder sb = new StringBuilder();
        sb.append(z ? REQUIRE_STRING_EXTERNAL_OES : "");
        Object[] objArr = new Object[1];
        objArr[0] = z ? SAMPLER2D_VAR_EXTERNAL_OES : SAMPLER2D_VAR;
        sb.append(String.format(str2, objArr));
        if (!this.mProgram.init(str, sb.toString())) {
            return false;
        }
        this.mRotationLoc = this.mProgram.getUniformLoc("rotation");
        this.mFlipScaleLoc = this.mProgram.getUniformLoc(FLIPSCALE_NAME);
        this.mTransformLoc = this.mProgram.getUniformLoc(TRANSFORM_NAME);
        setRotation(0.0f);
        setFlipscale(1.0f, 1.0f);
        setTransform(new float[]{1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f});
        return true;
    }

    protected void defaultInitialize() {
        int[] iArr = new int[1];
        GLES20.glGenBuffers(1, iArr, 0);
        int i = iArr[0];
        this.mVertexBuffer = i;
        if (i == 0) {
            Log.e("libCGE_java", "Invalid VertexBuffer!");
        }
        GLES20.glBindBuffer(34962, this.mVertexBuffer);
        float[] fArr = vertices;
        FloatBuffer allocate = FloatBuffer.allocate(fArr.length);
        allocate.put(fArr).position(0);
        GLES20.glBufferData(34962, 32, allocate, 35044);
    }
}
