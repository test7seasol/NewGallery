package org.wysaid.texUtils;

/* loaded from: classes4.dex */
public class TextureRendererThreshold extends TextureRendererDrawOrigin {
    protected static final String THRESHOLD_VALUE = "thresholdValue";
    private static final String fshThreshold = "precision mediump float;\nvarying vec2 texCoord;\n uniform %s inputImageTexture;\n uniform float thresholdValue;\n void main()\n{\n    vec4 color = texture2D(inputImageTexture, texCoord);\n    \n    float weight = (color.r + color.g + color.b) / 3.0;\n    color.a = smoothstep(0.0, thresholdValue, weight);\n    \n    gl_FragColor = color;\n}";

    @Override // org.wysaid.texUtils.TextureRendererDrawOrigin, org.wysaid.texUtils.TextureRenderer
    public String getFragmentShaderString() {
        return fshThreshold;
    }

    public static TextureRendererThreshold create(boolean z) {
        TextureRendererThreshold textureRendererThreshold = new TextureRendererThreshold();
        if (textureRendererThreshold.init(z)) {
            return textureRendererThreshold;
        }
        textureRendererThreshold.release();
        return null;
    }

    public void setThresholdValue(float f) {
        this.mProgram.bind();
        this.mProgram.sendUniformf(THRESHOLD_VALUE, f);
    }
}
