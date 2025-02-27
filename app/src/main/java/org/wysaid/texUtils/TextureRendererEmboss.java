package org.wysaid.texUtils;

/* loaded from: classes4.dex */
public class TextureRendererEmboss extends TextureRendererDrawOrigin {
    protected static final String SAMPLER_STEPS = "samplerSteps";
    private static final String fshEmboss = "precision mediump float;\nuniform %s inputImageTexture;\nvarying vec2 texCoord;\nuniform vec2 samplerSteps;\nconst float stride = 2.0;\nconst vec2 norm = vec2(0.72, 0.72);\nvoid main() {\n  vec4 src = texture2D(inputImageTexture, texCoord);\n  vec3 tmp = texture2D(inputImageTexture, texCoord + samplerSteps * stride * norm).rgb - src.rgb + 0.5;\n  float f = (tmp.r + tmp.g + tmp.b) / 3.0;\n  gl_FragColor = vec4(f, f, f, src.a);\n}";

    @Override // org.wysaid.texUtils.TextureRendererDrawOrigin, org.wysaid.texUtils.TextureRenderer
    public String getFragmentShaderString() {
        return fshEmboss;
    }

    public static TextureRendererEmboss create(boolean z) {
        TextureRendererEmboss textureRendererEmboss = new TextureRendererEmboss();
        if (textureRendererEmboss.init(z)) {
            return textureRendererEmboss;
        }
        textureRendererEmboss.release();
        return null;
    }

    @Override // org.wysaid.texUtils.TextureRendererDrawOrigin, org.wysaid.texUtils.TextureRenderer
    public boolean init(boolean z) {
        if (!setProgramDefault(getVertexShaderString(), getFragmentShaderString(), z)) {
            return false;
        }
        this.mProgram.bind();
        this.mProgram.sendUniformf(SAMPLER_STEPS, 0.0015625f, 0.0015625f);
        return true;
    }

    @Override // org.wysaid.texUtils.TextureRendererDrawOrigin, org.wysaid.texUtils.TextureRenderer
    public void setTextureSize(int i, int i2) {
        super.setTextureSize(i, i2);
        this.mProgram.bind();
        this.mProgram.sendUniformf(SAMPLER_STEPS, 1.0f / i, 1.0f / i2);
    }
}
