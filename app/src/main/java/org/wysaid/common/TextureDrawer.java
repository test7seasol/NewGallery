package org.wysaid.common;

import android.opengl.GLES20;
import android.util.Log;

import androidx.constraintlayout.motion.widget.Key;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/* loaded from: classes4.dex */
public class TextureDrawer {
    public static final int DRAW_FUNCTION = 6;
    protected static final String fshDrawer = "precision mediump float;\nvarying vec2 texCoord;\nuniform sampler2D inputImageTexture;\nvoid main()\n{\n   gl_FragColor = texture2D(inputImageTexture, texCoord);\n}";
    public static final float[] vertices = {-1.0f, -1.0f, 1.0f, -1.0f, 1.0f, 1.0f, -1.0f, 1.0f};
    protected static final String vshDrawer = "attribute vec2 vPosition;\nvarying vec2 texCoord;\nuniform mat2 rotation;\nuniform vec2 flipScale;\nvoid main()\n{\n   gl_Position = vec4(vPosition, 0.0, 1.0);\n   texCoord = flipScale * (vPosition / 2.0 * rotation) + 0.5;\n}";
    protected int mFlipScaleLoc;
    protected ProgramObject mProgram;
    protected int mRotLoc;
    protected int mVertBuffer;

    public ProgramObject getProgram() {
        return this.mProgram;
    }

    protected TextureDrawer() {
    }

    protected boolean init(String str, String str2) {
        ProgramObject programObject = new ProgramObject();
        this.mProgram = programObject;
        programObject.bindAttribLocation("vPosition", 0);
        if (!this.mProgram.init(str, str2)) {
            this.mProgram.release();
            this.mProgram = null;
            return false;
        }
        this.mProgram.bind();
        this.mRotLoc = this.mProgram.getUniformLoc(Key.ROTATION);
        this.mFlipScaleLoc = this.mProgram.getUniformLoc("flipScale");
        int[] iArr = new int[1];
        GLES20.glGenBuffers(1, iArr, 0);
        int i = iArr[0];
        this.mVertBuffer = i;
        GLES20.glBindBuffer(34962, i);
        float[] fArr = vertices;
        FloatBuffer asFloatBuffer = ByteBuffer.allocateDirect(fArr.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        asFloatBuffer.put(fArr).position(0);
        GLES20.glBufferData(34962, 32, asFloatBuffer, 35044);
        setRotation(0.0f);
        setFlipScale(1.0f, 1.0f);
        return true;
    }

    public static TextureDrawer create() {
        TextureDrawer textureDrawer = new TextureDrawer();
        if (textureDrawer.init(vshDrawer, fshDrawer)) {
            return textureDrawer;
        }
        Log.e("libCGE_java", "TextureDrawer create failed!");
        textureDrawer.release();
        return null;
    }

    public void release() {
        ProgramObject programObject = this.mProgram;
        if (programObject != null) {
            programObject.release();
            this.mProgram = null;
        }
        GLES20.glDeleteBuffers(1, new int[]{this.mVertBuffer}, 0);
        this.mVertBuffer = 0;
    }

    public void drawTexture(int i) {
        drawTexture(i, 3553);
    }

    public void drawTexture(int i, int i2) {
        GLES20.glActiveTexture(33984);
        GLES20.glBindTexture(i2, i);
        GLES20.glBindBuffer(34962, this.mVertBuffer);
        GLES20.glEnableVertexAttribArray(0);
        GLES20.glVertexAttribPointer(0, 2, 5126, false, 0, 0);
        this.mProgram.bind();
        GLES20.glDrawArrays(6, 0, 4);
    }

    public void bindVertexBuffer() {
        GLES20.glBindBuffer(34962, this.mVertBuffer);
    }

    public void setRotation(float f) {
        _rotate(this.mRotLoc, f);
    }

    public void setFlipScale(float f, float f2) {
        this.mProgram.bind();
        GLES20.glUniform2f(this.mFlipScaleLoc, f, f2);
    }

    private void _rotate(int i, float f) {
        double d = f;
        float cos = (float) Math.cos(d);
        float sin = (float) Math.sin(d);
        this.mProgram.bind();
        GLES20.glUniformMatrix2fv(i, 1, false, new float[]{cos, sin, -sin, cos}, 0);
    }
}
