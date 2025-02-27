package org.wysaid.texUtils;

/* loaded from: classes4.dex */
public class TextureRendererEdge extends TextureRendererEmboss {
    private static final String fshEdge = "precision mediump float;\nvarying vec2 texCoord;\nuniform %s inputImageTexture;\nvarying vec2 coords[8];\nvoid main()\n{\n  vec3 colors[8];\n  for(int i = 0; i < 8; ++i)\n  {\n    colors[i] = texture2D(inputImageTexture, coords[i]).rgb;\n  }\n  vec4 src = texture2D(inputImageTexture, texCoord);\n  vec3 h = -colors[0] - 2.0 * colors[1] - colors[2] + colors[5] + 2.0 * colors[6] + colors[7];\n  vec3 v = -colors[0] + colors[2] - 2.0 * colors[3] + 2.0 * colors[4] - colors[5] + colors[7];\n  gl_FragColor = vec4(sqrt(h * h + v * v), 1.0);\n}";
    private static final String vshEdge = "attribute vec2 vPosition;\nvarying vec2 texCoord;\nvarying vec2 coords[8];\nuniform mat4 transform;\nuniform mat2 rotation;\nuniform vec2 flipScale;\nuniform vec2 samplerSteps;\nconst float stride = 2.0;\nvoid main()\n{\n  gl_Position = vec4(vPosition, 0.0, 1.0);\n  vec2 coord = flipScale * (vPosition / 2.0 * rotation) + 0.5;\n  texCoord = (transform * vec4(coord, 0.0, 1.0)).xy;\n  coords[0] = texCoord - samplerSteps * stride;\n  coords[1] = texCoord + vec2(0.0, -samplerSteps.y) * stride;\n  coords[2] = texCoord + vec2(samplerSteps.x, -samplerSteps.y) * stride;\n  coords[3] = texCoord - vec2(samplerSteps.x, 0.0) * stride;\n  coords[4] = texCoord + vec2(samplerSteps.x, 0.0) * stride;\n  coords[5] = texCoord + vec2(-samplerSteps.x, samplerSteps.y) * stride;\n  coords[6] = texCoord + vec2(0.0, samplerSteps.y) * stride;\n  coords[7] = texCoord + vec2(samplerSteps.x, samplerSteps.y) * stride;\n}";

    @Override // org.wysaid.texUtils.TextureRendererEmboss, org.wysaid.texUtils.TextureRendererDrawOrigin, org.wysaid.texUtils.TextureRenderer
    public String getFragmentShaderString() {
        return fshEdge;
    }

    @Override // org.wysaid.texUtils.TextureRendererDrawOrigin, org.wysaid.texUtils.TextureRenderer
    public String getVertexShaderString() {
        return vshEdge;
    }

    public static TextureRendererEdge create(boolean z) {
        TextureRendererEdge textureRendererEdge = new TextureRendererEdge();
        if (textureRendererEdge.init(z)) {
            return textureRendererEdge;
        }
        textureRendererEdge.release();
        return null;
    }
}
