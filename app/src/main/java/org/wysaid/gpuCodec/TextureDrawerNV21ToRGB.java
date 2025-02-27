package org.wysaid.gpuCodec;

import android.opengl.GLES20;
import android.util.Log;

/* loaded from: classes4.dex */
public class TextureDrawerNV21ToRGB extends TextureDrawerCodec {
    private static final String fshNV21ToRGB = "precision mediump float;\nvarying vec2 texCoord;\nuniform sampler2D textureY;\nuniform sampler2D textureUV;\nuniform mat3 colorConversion;\nvoid main()\n{\n    vec3 yuv;\n    yuv.x = texture2D(textureY, texCoord).r;\n    yuv.yz = texture2D(textureUV, texCoord).ra - vec2(0.5, 0.5);\n    vec3 rgb = colorConversion * yuv;\n    gl_FragColor = vec4(rgb, 1.0);\n}";

    public static TextureDrawerNV21ToRGB create() {
        TextureDrawerNV21ToRGB textureDrawerNV21ToRGB = new TextureDrawerNV21ToRGB();
        if (textureDrawerNV21ToRGB.init("attribute vec2 vPosition;\nvarying vec2 texCoord;\nuniform mat2 rotation;\nuniform vec2 flipScale;\nvoid main()\n{\n   gl_Position = vec4(vPosition, 0.0, 1.0);\n   texCoord = flipScale * (vPosition / 2.0 * rotation) + 0.5;\n}", fshNV21ToRGB)) {
            return textureDrawerNV21ToRGB;
        }
        Log.e("libCGE_java", "TextureDrawerNV21ToRGB create failed!");
        textureDrawerNV21ToRGB.release();
        return null;
    }

    @Override // org.wysaid.common.TextureDrawer
    protected boolean init(String str, String str2) {
        if (!super.init(str, str2)) {
            return false;
        }
        this.mProgram.bind();
        this.mProgram.sendUniformi("textureUV", 1);
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

    public void drawTextures(int i, int i2) {
        GLES20.glActiveTexture(33984);
        GLES20.glBindTexture(3553, i);
        GLES20.glActiveTexture(33985);
        GLES20.glBindTexture(3553, i2);
        drawTextures();
    }
}
