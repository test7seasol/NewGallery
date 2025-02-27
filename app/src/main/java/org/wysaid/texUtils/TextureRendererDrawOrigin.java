package org.wysaid.texUtils;

import android.opengl.GLES20;

/* loaded from: classes4.dex */
public class TextureRendererDrawOrigin extends TextureRenderer {
    private static final String fshDrawOrigin = "precision mediump float;\nvarying vec2 texCoord;\nuniform %s inputImageTexture;\nvoid main()\n{\n   gl_FragColor = texture2D(inputImageTexture, texCoord);\n}";

    @Override // org.wysaid.texUtils.TextureRenderer
    public String getFragmentShaderString() {
        return fshDrawOrigin;
    }

    @Override // org.wysaid.texUtils.TextureRenderer
    public String getVertexShaderString() {
        return "attribute vec2 vPosition;\nvarying vec2 texCoord;\nuniform mat4 transform;\nuniform mat2 rotation;\nuniform vec2 flipScale;\nvoid main()\n{\n   gl_Position = vec4(vPosition, 0.0, 1.0);\n   vec2 coord = flipScale * (vPosition / 2.0 * rotation) + 0.5;\n   texCoord = (transform * vec4(coord, 0.0, 1.0)).xy;\n}";
    }

    protected TextureRendererDrawOrigin() {
        defaultInitialize();
    }

    protected TextureRendererDrawOrigin(boolean z) {
        if (z) {
            return;
        }
        defaultInitialize();
    }

    public static TextureRendererDrawOrigin create(boolean z) {
        TextureRendererDrawOrigin textureRendererDrawOrigin = new TextureRendererDrawOrigin();
        if (textureRendererDrawOrigin.init(z)) {
            return textureRendererDrawOrigin;
        }
        textureRendererDrawOrigin.release();
        return null;
    }

    @Override // org.wysaid.texUtils.TextureRenderer
    public boolean init(boolean z) {
        return setProgramDefault(getVertexShaderString(), getFragmentShaderString(), z);
    }

    @Override // org.wysaid.texUtils.TextureRenderer
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
        GLES20.glDrawArrays(6, 0, 4);
    }

    @Override // org.wysaid.texUtils.TextureRenderer
    public void setTextureSize(int i, int i2) {
        this.mTextureWidth = i;
        this.mTextureHeight = i2;
    }
}
