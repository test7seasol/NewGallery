package org.wysaid.texUtils;

import android.opengl.GLES20;

/* loaded from: classes4.dex */
public class TextureRendererWave extends TextureRendererDrawOrigin {
    private static final String fshWave = "precision mediump float;\nvarying vec2 texCoord;\nuniform %s inputImageTexture;\nuniform float motion;\nconst float angle = 20.0;void main()\n{\n   vec2 coord;\n   coord.x = texCoord.x + 0.01 * sin(motion + texCoord.x * angle);\n   coord.y = texCoord.y + 0.01 * sin(motion + texCoord.y * angle);\n   gl_FragColor = texture2D(inputImageTexture, coord);\n}";
    private int mMotionLoc = 0;
    private boolean mAutoMotion = false;
    private float mMotion = 0.0f;
    private float mMotionSpeed = 0.0f;

    public static TextureRendererWave create(boolean z) {
        TextureRendererWave textureRendererWave = new TextureRendererWave();
        if (textureRendererWave.init(z)) {
            return textureRendererWave;
        }
        textureRendererWave.release();
        return null;
    }

    @Override // org.wysaid.texUtils.TextureRendererDrawOrigin, org.wysaid.texUtils.TextureRenderer
    public boolean init(boolean z) {
        if (!setProgramDefault("attribute vec2 vPosition;\nvarying vec2 texCoord;\nuniform mat4 transform;\nuniform mat2 rotation;\nuniform vec2 flipScale;\nvoid main()\n{\n   gl_Position = vec4(vPosition, 0.0, 1.0);\n   vec2 coord = flipScale * (vPosition / 2.0 * rotation) + 0.5;\n   texCoord = (transform * vec4(coord, 0.0, 1.0)).xy;\n}", fshWave, z)) {
            return false;
        }
        this.mProgram.bind();
        this.mMotionLoc = this.mProgram.getUniformLoc("motion");
        return true;
    }

    public void setWaveMotion(float f) {
        this.mProgram.bind();
        GLES20.glUniform1f(this.mMotionLoc, f);
    }

    public void setAutoMotion(float f) {
        this.mMotionSpeed = f;
        this.mAutoMotion = f != 0.0f;
    }

    @Override // org.wysaid.texUtils.TextureRendererDrawOrigin, org.wysaid.texUtils.TextureRenderer
    public void renderTexture(int i, Viewport viewport) {
        if (viewport != null) {
            GLES20.glViewport(viewport.x, viewport.y, viewport.width, viewport.height);
        }
        GLES20.glActiveTexture(33984);
        GLES20.glBindTexture(this.TEXTURE_2D_BINDABLE, i);
        GLES20.glBindBuffer(34962, this.mVertexBuffer);
        GLES20.glEnableVertexAttribArray(0);
        GLES20.glVertexAttribPointer(0, 2, 5126, false, 0, 0);
        this.mProgram.bind();
        if (this.mAutoMotion) {
            float f = this.mMotion + this.mMotionSpeed;
            this.mMotion = f;
            GLES20.glUniform1f(this.mMotionLoc, f);
            float f2 = this.mMotion;
            if (f2 > 62.83185307179586d) {
                this.mMotion = (float) (f2 - 62.83185307179586d);
            }
        }
        GLES20.glDrawArrays(6, 0, 4);
    }
}
