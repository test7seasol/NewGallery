package org.wysaid.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.opengl.GLES20;
import android.util.AttributeSet;
import android.util.Log;

import org.wysaid.camera.CameraInstance;
import org.wysaid.common.Common;
import org.wysaid.gpuCodec.TextureDrawerNV12ToRGB;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/* loaded from: classes4.dex */
public class CameraGLSurfaceViewWithBuffer extends CameraGLSurfaceView implements Camera.PreviewCallback {
    protected int mBufferSize;
    protected ByteBuffer mBufferUV;
    protected final int[] mBufferUpdateLock;
    protected boolean mBufferUpdated;
    protected ByteBuffer mBufferY;
    protected byte[] mPreviewBuffer0;
    protected byte[] mPreviewBuffer1;
    protected SurfaceTexture mSurfaceTexture;
    protected int mTextureHeight;
    protected int mTextureUV;
    protected int mTextureWidth;
    protected int mTextureY;
    protected int mUVSize;
    protected int mYSize;
    protected TextureDrawerNV12ToRGB mYUVDrawer;

    public CameraGLSurfaceViewWithBuffer(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mBufferUpdated = false;
        this.mBufferUpdateLock = new int[0];
        setRenderMode(1);
    }

    @Override // org.wysaid.view.CameraGLSurfaceView
    protected void onRelease() {
        super.onRelease();
        TextureDrawerNV12ToRGB textureDrawerNV12ToRGB = this.mYUVDrawer;
        if (textureDrawerNV12ToRGB != null) {
            textureDrawerNV12ToRGB.release();
            this.mYUVDrawer = null;
        }
        SurfaceTexture surfaceTexture = this.mSurfaceTexture;
        if (surfaceTexture != null) {
            surfaceTexture.release();
            this.mSurfaceTexture = null;
        }
        int i = this.mTextureY;
        if (i == 0 && this.mTextureUV == 0) {
            return;
        }
        GLES20.glDeleteTextures(2, new int[]{i, this.mTextureUV}, 0);
        this.mTextureUV = 0;
        this.mTextureY = 0;
        this.mTextureWidth = 0;
        this.mTextureHeight = 0;
    }

    protected void resizeTextures() {
        if (this.mTextureY == 0 || this.mTextureUV == 0) {
            int[] iArr = new int[2];
            GLES20.glGenTextures(2, iArr, 0);
            int i = iArr[0];
            this.mTextureY = i;
            this.mTextureUV = iArr[1];
            GLES20.glBindTexture(3553, i);
            Common.texParamHelper(3553, 9729, 33071);
            GLES20.glBindTexture(3553, this.mTextureUV);
            Common.texParamHelper(3553, 9729, 33071);
        }
        int previewWidth = cameraInstance().previewWidth();
        int previewHeight = cameraInstance().previewHeight();
        if (this.mTextureWidth == previewWidth && this.mTextureHeight == previewHeight) {
            return;
        }
        this.mTextureWidth = previewWidth;
        this.mTextureHeight = previewHeight;
        GLES20.glBindTexture(3553, this.mTextureY);
        GLES20.glTexImage2D(3553, 0, 6409, this.mTextureWidth, this.mTextureHeight, 0, 6409, 5121, null);
        GLES20.glBindTexture(3553, this.mTextureUV);
        GLES20.glTexImage2D(3553, 0, 6410, this.mTextureWidth / 2, this.mTextureHeight / 2, 0, 6410, 5121, null);
    }

    protected void updateTextures() {
        if (this.mBufferUpdated) {
            synchronized (this.mBufferUpdateLock) {
                GLES20.glActiveTexture(33984);
                GLES20.glBindTexture(3553, this.mTextureY);
                GLES20.glTexSubImage2D(3553, 0, 0, 0, this.mTextureWidth, this.mTextureHeight, 6409, 5121, this.mBufferY.position(0));
                GLES20.glActiveTexture(33985);
                GLES20.glBindTexture(3553, this.mTextureUV);
                GLES20.glTexSubImage2D(3553, 0, 0, 0, this.mTextureWidth / 2, this.mTextureHeight / 2, 6410, 5121, this.mBufferUV.position(0));
                this.mBufferUpdated = false;
            }
            return;
        }
        GLES20.glActiveTexture(33984);
        GLES20.glBindTexture(3553, this.mTextureY);
        GLES20.glActiveTexture(33985);
        GLES20.glBindTexture(3553, this.mTextureUV);
    }

    @Override // org.wysaid.view.CameraGLSurfaceView, android.opengl.GLSurfaceView.Renderer
    public void onSurfaceCreated(GL10 gl10, EGLConfig eGLConfig) {
        super.onSurfaceCreated(gl10, eGLConfig);
        TextureDrawerNV12ToRGB create = TextureDrawerNV12ToRGB.create();
        this.mYUVDrawer = create;
        create.setFlipScale(1.0f, 1.0f);
        this.mYUVDrawer.setRotation(1.5707964f);
        this.mSurfaceTexture = new SurfaceTexture(0);
    }

    @Override // org.wysaid.view.CameraGLSurfaceView
    public void resumePreview() {
        if (this.mYUVDrawer == null) {
            return;
        }
        if (!cameraInstance().isCameraOpened()) {
            cameraInstance().tryOpenCamera(new CameraInstance.CameraOpenCallback() { // from class: org.wysaid.view.CameraGLSurfaceViewWithBuffer.1
                @Override // org.wysaid.camera.CameraInstance.CameraOpenCallback
                public void cameraReady() {
                    Log.i("libCGE_java", "tryOpenCamera OK...");
                }
            }, !this.mIsCameraBackForward ? 1 : 0);
        }
        if (!cameraInstance().isPreviewing()) {
            Camera cameraDevice = cameraInstance().getCameraDevice();
            Camera.Parameters parameters = cameraDevice.getParameters();
            parameters.getPreviewFormat();
            Camera.Size previewSize = parameters.getPreviewSize();
            int previewFormat = parameters.getPreviewFormat();
            if (previewFormat != 17) {
                try {
                    parameters.setPreviewFormat(17);
                    cameraDevice.setParameters(parameters);
                    previewFormat = 17;
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
            }
            int i = previewSize.width * previewSize.height;
            this.mYSize = i;
            @SuppressLint("WrongConstant") int bitsPerPixel = (i * ImageFormat.getBitsPerPixel(previewFormat)) / 8;
            if (this.mBufferSize != bitsPerPixel) {
                this.mBufferSize = bitsPerPixel;
                int i2 = this.mYSize;
                this.mUVSize = bitsPerPixel - i2;
                this.mBufferY = ByteBuffer.allocateDirect(i2).order(ByteOrder.nativeOrder());
                this.mBufferUV = ByteBuffer.allocateDirect(this.mUVSize).order(ByteOrder.nativeOrder());
                int i3 = this.mBufferSize;
                this.mPreviewBuffer0 = new byte[i3];
                this.mPreviewBuffer1 = new byte[i3];
            }
            cameraDevice.addCallbackBuffer(this.mPreviewBuffer0);
            cameraDevice.addCallbackBuffer(this.mPreviewBuffer1);
            cameraInstance().startPreview(this.mSurfaceTexture, this);
        }
        if (this.mIsCameraBackForward) {
            this.mYUVDrawer.setFlipScale(-1.0f, 1.0f);
            this.mYUVDrawer.setRotation(1.5707964f);
        } else {
            this.mYUVDrawer.setFlipScale(1.0f, 1.0f);
            this.mYUVDrawer.setRotation(1.5707964f);
        }
        resizeTextures();
    }

    @Override // org.wysaid.view.CameraGLSurfaceView, android.opengl.GLSurfaceView.Renderer
    public void onSurfaceChanged(GL10 gl10, int i, int i2) {
        super.onSurfaceChanged(gl10, i, i2);
        if (cameraInstance().isPreviewing()) {
            return;
        }
        resumePreview();
    }

    public void drawCurrentFrame() {
        if (this.mYUVDrawer == null) {
            return;
        }
        GLES20.glBindFramebuffer(36160, 0);
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        GLES20.glClear(16384);
        GLES20.glViewport(this.mDrawViewport.x, this.mDrawViewport.y, this.mDrawViewport.width, this.mDrawViewport.height);
        updateTextures();
        this.mYUVDrawer.drawTextures();
    }

    @Override // org.wysaid.view.CameraGLSurfaceView, android.opengl.GLSurfaceView.Renderer
    public void onDrawFrame(GL10 gl10) {
        drawCurrentFrame();
    }

    @Override // android.hardware.Camera.PreviewCallback
    public void onPreviewFrame(byte[] bArr, Camera camera) {
        synchronized (this.mBufferUpdateLock) {
            this.mBufferY.position(0);
            this.mBufferUV.position(0);
            this.mBufferY.put(bArr, 0, this.mYSize);
            this.mBufferUV.put(bArr, this.mYSize, this.mUVSize);
            this.mBufferUpdated = true;
        }
        camera.addCallbackBuffer(bArr);
    }
}
