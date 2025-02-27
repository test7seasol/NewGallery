package org.wysaid.gpuCodec;

import android.opengl.GLES20;
import android.util.Log;

/* loaded from: classes4.dex */
public class TextureDrawerI420ToRGB extends TextureDrawerCodec {
    protected static final String fshI420ToRGB = "precision mediump float;\nvarying vec2 texCoord;\nuniform sampler2D textureY;\nuniform sampler2D textureU;\nuniform sampler2D textureV;\nuniform mat3 colorConversion;\nvoid main()\n{\n    vec3 yuv;\n    yuv.x = texture2D(textureY, texCoord).r;\n    yuv.y = texture2D(textureU, texCoord).r - 0.5;\n    yuv.z = texture2D(textureV, texCoord).r - 0.5;\n    vec3 rgb = colorConversion * yuv;\n    gl_FragColor = vec4(rgb, 1.0);\n}";

    public static TextureDrawerI420ToRGB create() {
        TextureDrawerI420ToRGB textureDrawerI420ToRGB = new TextureDrawerI420ToRGB();
        if (textureDrawerI420ToRGB.init("attribute vec2 vPosition;\nvarying vec2 texCoord;\nuniform mat2 rotation;\nuniform vec2 flipScale;\nvoid main()\n{\n   gl_Position = vec4(vPosition, 0.0, 1.0);\n   texCoord = flipScale * (vPosition / 2.0 * rotation) + 0.5;\n}", fshI420ToRGB)) {
            return textureDrawerI420ToRGB;
        }
        Log.e("libCGE_java", "TextureDrawerI420ToRGB create failed!");
        textureDrawerI420ToRGB.release();
        return null;
    }

    @Override // org.wysaid.common.TextureDrawer
    protected boolean init(String str, String str2) {
        if (!super.init(str, str2)) {
            return false;
        }
        this.mProgram.bind();
        this.mProgram.sendUniformi("textureU", 1);
        this.mProgram.sendUniformi("textureV", 2);
        this.mProgram.sendUniformMat3(TextureDrawerCodec.COLOR_CONVERSION_NAME, 1, false, MATRIX_YUV2RGB);
        return true;
    }

    public void drawTextures() {
        this.mProgram.bind();
        GLES20.glBindBuffer(34962, this.mVertBuffer);
        GLES20.glEnableVertexAttribArray(0);
        GLES20.glVertexAttribPointer(0, 2, 5126, false, 0, 0);
        GLES20.glDrawArrays(6, 0, 4);
    }

    public void drawTextures(int i, int i2, int i3) {
        GLES20.glActiveTexture(33984);
        GLES20.glBindTexture(3553, i);
        GLES20.glActiveTexture(33985);
        GLES20.glBindTexture(3553, i2);
        GLES20.glActiveTexture(33986);
        GLES20.glBindTexture(3553, i3);
        drawTextures();
    }
}
