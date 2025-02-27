package org.wysaid.texUtils;

import android.opengl.GLES20;
import android.util.Log;

import org.wysaid.common.FrameBufferObject;
import org.wysaid.common.ProgramObject;

/* loaded from: classes4.dex */
public class TextureRendererBlur extends TextureRendererDrawOrigin {
    private static final String SAMPLER_STEPS = "samplerSteps";
    private static final String fshBlur = "precision highp float;\nvarying vec2 texCoord;\nuniform %s inputImageTexture;\nuniform vec2 samplerSteps;\nconst int samplerRadius = 5;\nconst float samplerRadiusFloat = 5.0;\nfloat random(vec2 seed)\n{\n  return fract(sin(dot(seed ,vec2(12.9898,78.233))) * 43758.5453);\n}\nvoid main()\n{\n  vec3 resultColor = vec3(0.0);\n  float blurPixels = 0.0;\n  float offset = random(texCoord) - 0.5;\n  \n  for(int i = -samplerRadius; i <= samplerRadius; ++i)\n  {\n    float percent = (float(i) + offset) / samplerRadiusFloat;\n    float weight = 1.0 - abs(percent);\n    vec2 coord = texCoord + samplerSteps * percent;\n    resultColor += texture2D(inputImageTexture, coord).rgb * weight;\n    blurPixels += weight;\n  }\n  gl_FragColor = vec4(resultColor / blurPixels, 1.0);\n}";
    private static final String vshBlur = "attribute vec2 vPosition;\nvarying vec2 texCoord;\nuniform mat4 transform;\nuniform mat2 rotation;\nuniform vec2 flipScale;\nvoid main()\n{\n   gl_Position = vec4(vPosition, 0.0, 1.0);\n   vec2 coord = flipScale * (vPosition / 2.0 * rotation) + 0.5;\n   texCoord = (transform * vec4(coord, 0.0, 1.0)).xy;\n}";
    private static final String vshBlurCache = "attribute vec2 vPosition;\nvarying vec2 texCoord;\nvoid main()\n{\n   gl_Position = vec4(vPosition, 0.0, 1.0);\n   texCoord = vPosition / 2.0 + 0.5;\n}";
    protected int mCacheTexHeight;
    protected int mCacheTexWidth;
    protected FrameBufferObject mFBO;
    private ProgramObject mProgramDrawCache;
    protected int mTexCache = 0;
    private int mStepsLoc = 0;
    private int mStepsLocCache = 0;
    private float mSamplerScale = 1.0f;

    @Override // org.wysaid.texUtils.TextureRendererDrawOrigin, org.wysaid.texUtils.TextureRenderer
    public String getFragmentShaderString() {
        return fshBlur;
    }

    @Override // org.wysaid.texUtils.TextureRendererDrawOrigin, org.wysaid.texUtils.TextureRenderer
    public String getVertexShaderString() {
        return vshBlur;
    }

    public static TextureRendererBlur create(boolean z) {
        TextureRendererBlur textureRendererBlur = new TextureRendererBlur();
        if (textureRendererBlur.init(z)) {
            return textureRendererBlur;
        }
        textureRendererBlur.release();
        return null;
    }

    public void setSamplerRadius(float f) {
        this.mSamplerScale = f / 4.0f;
    }

    @Override // org.wysaid.texUtils.TextureRendererDrawOrigin, org.wysaid.texUtils.TextureRenderer
    public boolean init(boolean z) {
        this.TEXTURE_2D_BINDABLE = z ? 36197 : 3553;
        StringBuilder sb = new StringBuilder();
        sb.append(z ? "#extension GL_OES_EGL_image_external : require\n" : "");
        Object[] objArr = new Object[1];
        objArr[0] = z ? "samplerExternalOES" : "sampler2D";
        sb.append(String.format(fshBlur, objArr));
        String sb2 = sb.toString();
        String format = String.format(fshBlur, "sampler2D");
        this.mFBO = new FrameBufferObject();
        ProgramObject programObject = new ProgramObject();
        this.mProgramDrawCache = programObject;
        programObject.bindAttribLocation("vPosition", 0);
        if (!this.mProgramDrawCache.init(vshBlurCache, sb2)) {
            Log.e("libCGE_java", "blur filter program init failed - 1...");
            return false;
        }
        this.mProgramDrawCache.bind();
        this.mStepsLocCache = this.mProgramDrawCache.getUniformLoc(SAMPLER_STEPS);
        this.mProgram = new ProgramObject();
        this.mProgram.bindAttribLocation("vPosition", 0);
        if (!this.mProgram.init(vshBlur, format)) {
            Log.e("libCGE_java", "blur filter program init failed - 2...");
            return false;
        }
        this.mProgram.bind();
        this.mStepsLoc = this.mProgram.getUniformLoc(SAMPLER_STEPS);
        setRotation(0.0f);
        return true;
    }

    @Override // org.wysaid.texUtils.TextureRenderer
    public void release() {
        if (this.mProgramDrawCache != this.mProgram) {
            this.mProgramDrawCache.release();
        }
        super.release();
        GLES20.glBindFramebuffer(36160, 0);
        this.mFBO.release();
        this.mFBO = null;
        GLES20.glDeleteTextures(1, new int[]{this.mTexCache}, 0);
        this.mTexCache = 0;
        this.mProgramDrawCache = null;
    }

    @Override // org.wysaid.texUtils.TextureRendererDrawOrigin, org.wysaid.texUtils.TextureRenderer
    public void renderTexture(int i, Viewport viewport) {
        if (this.mTexCache == 0 || this.mCacheTexWidth != this.mTextureWidth || this.mCacheTexHeight != this.mTextureHeight) {
            resetCacheTexture();
        }
        this.mFBO.bind();
        GLES20.glViewport(0, 0, this.mCacheTexWidth, this.mCacheTexHeight);
        GLES20.glBindBuffer(34962, this.mVertexBuffer);
        GLES20.glEnableVertexAttribArray(0);
        GLES20.glVertexAttribPointer(0, 2, 5126, false, 0, 0);
        GLES20.glActiveTexture(33984);
        GLES20.glBindTexture(this.TEXTURE_2D_BINDABLE, i);
        this.mProgramDrawCache.bind();
        GLES20.glUniform2f(this.mStepsLocCache, (1.0f / this.mTextureWidth) * this.mSamplerScale, 0.0f);
        GLES20.glDrawArrays(6, 0, 4);
        if (viewport != null) {
            GLES20.glViewport(viewport.x, viewport.y, viewport.width, viewport.height);
        }
        this.mProgram.bind();
        GLES20.glBindFramebuffer(36160, 0);
        GLES20.glBindTexture(3553, this.mTexCache);
        GLES20.glUniform2f(this.mStepsLoc, 0.0f, (1.0f / this.mCacheTexWidth) * this.mSamplerScale);
        GLES20.glDrawArrays(6, 0, 4);
    }

    @Override // org.wysaid.texUtils.TextureRendererDrawOrigin, org.wysaid.texUtils.TextureRenderer
    public void setTextureSize(int i, int i2) {
        super.setTextureSize(i, i2);
    }

    protected void resetCacheTexture() {
        Log.i("libCGE_java", "resetCacheTexture...");
        this.mCacheTexWidth = this.mTextureWidth;
        this.mCacheTexHeight = this.mTextureHeight;
        if (this.mTexCache == 0) {
            int[] iArr = new int[1];
            GLES20.glGenTextures(1, iArr, 0);
            this.mTexCache = iArr[0];
        }
        GLES20.glBindTexture(3553, this.mTexCache);
        GLES20.glTexImage2D(3553, 0, 6408, this.mCacheTexWidth, this.mCacheTexHeight, 0, 6408, 5121, null);
        GLES20.glTexParameteri(3553, 10241, 9729);
        GLES20.glTexParameteri(3553, 10240, 9729);
        GLES20.glTexParameteri(3553, 10242, 33071);
        GLES20.glTexParameteri(3553, 10243, 33071);
        this.mFBO.bindTexture(this.mTexCache);
    }
}
