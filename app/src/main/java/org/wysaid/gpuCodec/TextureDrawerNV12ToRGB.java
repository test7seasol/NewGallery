package org.wysaid.gpuCodec;

import android.util.Log;

/* loaded from: classes4.dex */
public class TextureDrawerNV12ToRGB extends TextureDrawerNV21ToRGB {
    private static final String fshNV12ToRGB = "precision mediump float;\nvarying vec2 texCoord;\nuniform sampler2D textureY;\nuniform sampler2D textureUV;\nuniform mat3 colorConversion;\nvoid main()\n{\n    vec3 yuv;\n    yuv.x = texture2D(textureY, texCoord).r;\n    yuv.yz = texture2D(textureUV, texCoord).ar - vec2(0.5, 0.5);\n    vec3 rgb = colorConversion * yuv;\n    gl_FragColor = vec4(rgb, 1.0);\n}";

    public static TextureDrawerNV12ToRGB create() {
        TextureDrawerNV12ToRGB textureDrawerNV12ToRGB = new TextureDrawerNV12ToRGB();
        if (textureDrawerNV12ToRGB.init("attribute vec2 vPosition;\nvarying vec2 texCoord;\nuniform mat2 rotation;\nuniform vec2 flipScale;\nvoid main()\n{\n   gl_Position = vec4(vPosition, 0.0, 1.0);\n   texCoord = flipScale * (vPosition / 2.0 * rotation) + 0.5;\n}", fshNV12ToRGB)) {
            return textureDrawerNV12ToRGB;
        }
        Log.e("libCGE_java", "TextureDrawerNV12ToRGB create failed!");
        textureDrawerNV12ToRGB.release();
        return null;
    }
}
