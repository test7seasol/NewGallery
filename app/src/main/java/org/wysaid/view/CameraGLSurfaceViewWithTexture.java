package org.wysaid.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.opengl.GLES20;
import android.util.AttributeSet;
import android.util.Log;

import org.wysaid.camera.CameraInstance;
import org.wysaid.common.Common;
import org.wysaid.common.FrameBufferObject;
import org.wysaid.nativePort.CGEFrameRenderer;

import java.nio.IntBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/* loaded from: classes4.dex */
public class CameraGLSurfaceViewWithTexture extends CameraGLSurfaceView implements SurfaceTexture.OnFrameAvailableListener {
    static final   boolean $assertionsDisabled = false;
    protected CGEFrameRenderer mFrameRenderer;
    protected boolean mIsTransformMatrixSet;
    protected SurfaceTexture mSurfaceTexture;
    protected int mTextureID;
    protected float[] mTransformMatrix;

    public CGEFrameRenderer getRecorder() {
        return this.mFrameRenderer;
    }

    public synchronized void setFilterWithConfig(final String str) {
        queueEvent(new Runnable() { // from class: org.wysaid.view.CameraGLSurfaceViewWithTexture.1
            @Override // java.lang.Runnable
            public void run() {
                if (CameraGLSurfaceViewWithTexture.this.mFrameRenderer != null) {
                    CameraGLSurfaceViewWithTexture.this.mFrameRenderer.setFilterWidthConfig(str);
                } else {
                    Log.e("libCGE_java", "setFilterWithConfig after release!!");
                }
            }
        });
    }

    public void setFilterIntensity(final float f) {
        queueEvent(new Runnable() { // from class: org.wysaid.view.CameraGLSurfaceViewWithTexture.2
            @Override // java.lang.Runnable
            public void run() {
                if (CameraGLSurfaceViewWithTexture.this.mFrameRenderer != null) {
                    CameraGLSurfaceViewWithTexture.this.mFrameRenderer.setFilterIntensity(f);
                } else {
                    Log.e("libCGE_java", "setFilterIntensity after release!!");
                }
            }
        });
    }

    @Override // org.wysaid.view.CameraGLSurfaceView
    public void setOnCreateCallback(final OnCreateCallback onCreateCallback) {
        if (this.mFrameRenderer == null || onCreateCallback == null) {
            this.mOnCreateCallback = onCreateCallback;
        } else {
            queueEvent(new Runnable() { // from class: org.wysaid.view.CameraGLSurfaceViewWithTexture.3
                @Override // java.lang.Runnable
                public void run() {
                    onCreateCallback.createOver();
                }
            });
        }
    }

    public CameraGLSurfaceViewWithTexture(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mIsTransformMatrixSet = false;
        this.mTransformMatrix = new float[16];
    }

    @Override // org.wysaid.view.CameraGLSurfaceView, android.opengl.GLSurfaceView.Renderer
    public void onSurfaceCreated(GL10 gl10, EGLConfig eGLConfig) {
        CGEFrameRenderer cGEFrameRenderer = new CGEFrameRenderer();
        this.mFrameRenderer = cGEFrameRenderer;
        this.mIsTransformMatrixSet = false;
        if (!cGEFrameRenderer.init(this.mRecordWidth, this.mRecordHeight, this.mRecordWidth, this.mRecordHeight)) {
            Log.e("libCGE_java", "Frame Recorder init failed!");
        }
        this.mFrameRenderer.setSrcRotation(1.5707964f);
        this.mFrameRenderer.setSrcFlipScale(1.0f, -1.0f);
        this.mFrameRenderer.setRenderFlipScale(1.0f, -1.0f);
        this.mTextureID = Common.genSurfaceTextureID();
        SurfaceTexture surfaceTexture = new SurfaceTexture(this.mTextureID);
        this.mSurfaceTexture = surfaceTexture;
        surfaceTexture.setOnFrameAvailableListener(this);
        super.onSurfaceCreated(gl10, eGLConfig);
    }

    @Override // org.wysaid.view.CameraGLSurfaceView
    protected void onRelease() {
        super.onRelease();
        SurfaceTexture surfaceTexture = this.mSurfaceTexture;
        if (surfaceTexture != null) {
            surfaceTexture.release();
            this.mSurfaceTexture = null;
        }
        int i = this.mTextureID;
        if (i != 0) {
            Common.deleteTextureID(i);
            this.mTextureID = 0;
        }
        CGEFrameRenderer cGEFrameRenderer = this.mFrameRenderer;
        if (cGEFrameRenderer != null) {
            cGEFrameRenderer.release();
            this.mFrameRenderer = null;
        }
    }

    @Override // org.wysaid.view.CameraGLSurfaceView, android.opengl.GLSurfaceView.Renderer
    public void onSurfaceChanged(GL10 gl10, int i, int i2) {
        super.onSurfaceChanged(gl10, i, i2);
        if (cameraInstance().isPreviewing()) {
            return;
        }
        resumePreview();
    }

    @Override // org.wysaid.view.CameraGLSurfaceView
    public void resumePreview() {
        if (this.mFrameRenderer == null) {
            Log.e("libCGE_java", "resumePreview after release!!");
            return;
        }
        if (!cameraInstance().isCameraOpened()) {
            cameraInstance().tryOpenCamera(new CameraInstance.CameraOpenCallback() { // from class: org.wysaid.view.CameraGLSurfaceViewWithTexture.4
                @Override // org.wysaid.camera.CameraInstance.CameraOpenCallback
                public void cameraReady() {
                    Log.i("libCGE_java", "tryOpenCamera OK...");
                }
            }, !this.mIsCameraBackForward ? 1 : 0);
        }
        if (!cameraInstance().isPreviewing()) {
            cameraInstance().startPreview(this.mSurfaceTexture);
            this.mFrameRenderer.srcResize(cameraInstance().previewHeight(), cameraInstance().previewWidth());
        }
        requestRender();
    }

    @Override // org.wysaid.view.CameraGLSurfaceView, android.opengl.GLSurfaceView.Renderer
    public void onDrawFrame(GL10 gl10) {
        if (this.mSurfaceTexture == null || !cameraInstance().isPreviewing()) {
            return;
        }
        this.mSurfaceTexture.updateTexImage();
        this.mSurfaceTexture.getTransformMatrix(this.mTransformMatrix);
        this.mFrameRenderer.update(this.mTextureID, this.mTransformMatrix);
        this.mFrameRenderer.runProc();
        GLES20.glBindFramebuffer(36160, 0);
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        GLES20.glClear(16384);
        this.mFrameRenderer.render(this.mDrawViewport.x, this.mDrawViewport.y, this.mDrawViewport.width, this.mDrawViewport.height);
    }

    @Override // android.graphics.SurfaceTexture.OnFrameAvailableListener
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        requestRender();
    }

    @Override // org.wysaid.view.CameraGLSurfaceView
    protected void onSwitchCamera() {
        super.onSwitchCamera();
        CGEFrameRenderer cGEFrameRenderer = this.mFrameRenderer;
        if (cGEFrameRenderer != null) {
            cGEFrameRenderer.setSrcRotation(1.5707964f);
            this.mFrameRenderer.setRenderFlipScale(1.0f, -1.0f);
        }
    }

    @Override // org.wysaid.view.CameraGLSurfaceView
    public void takeShot(final TakePictureCallback takePictureCallback) {
        if (this.mFrameRenderer == null) {
            Log.e("libCGE_java", "Recorder not initialized!");
            takePictureCallback.takePictureOK(null);
        } else {
            queueEvent(new Runnable() { // from class: org.wysaid.view.CameraGLSurfaceViewWithTexture.5
                @Override // java.lang.Runnable
                public void run() {
                    FrameBufferObject frameBufferObject = new FrameBufferObject();
                    int genBlankTextureID = Common.genBlankTextureID(CameraGLSurfaceViewWithTexture.this.mRecordWidth, CameraGLSurfaceViewWithTexture.this.mRecordHeight);
                    frameBufferObject.bindTexture(genBlankTextureID);
                    GLES20.glViewport(0, 0, CameraGLSurfaceViewWithTexture.this.mRecordWidth, CameraGLSurfaceViewWithTexture.this.mRecordHeight);
                    CameraGLSurfaceViewWithTexture.this.mFrameRenderer.drawCache();
                    IntBuffer allocate = IntBuffer.allocate(CameraGLSurfaceViewWithTexture.this.mRecordWidth * CameraGLSurfaceViewWithTexture.this.mRecordHeight);
                    GLES20.glReadPixels(0, 0, CameraGLSurfaceViewWithTexture.this.mRecordWidth, CameraGLSurfaceViewWithTexture.this.mRecordHeight, 6408, 5121, allocate);
                    Bitmap createBitmap = Bitmap.createBitmap(CameraGLSurfaceViewWithTexture.this.mRecordWidth, CameraGLSurfaceViewWithTexture.this.mRecordHeight, Bitmap.Config.ARGB_8888);
                    createBitmap.copyPixelsFromBuffer(allocate);
                    Log.i("libCGE_java", String.format("w: %d, h: %d", Integer.valueOf(CameraGLSurfaceViewWithTexture.this.mRecordWidth), Integer.valueOf(CameraGLSurfaceViewWithTexture.this.mRecordHeight)));
                    frameBufferObject.release();
                    GLES20.glDeleteTextures(1, new int[]{genBlankTextureID}, 0);
                    takePictureCallback.takePictureOK(createBitmap);
                }
            });
        }
    }

    @Override // org.wysaid.view.CameraGLSurfaceView
    public void setPictureSize(int i, int i2, boolean z) {
        cameraInstance().setPictureSize(i2, i, z);
    }

   /* public synchronized void takePicture(final TakePictureCallback takePictureCallback, Camera.ShutterCallback shutterCallback, final String str, final float f, final boolean z) {
        Camera.Parameters params = cameraInstance().getParams();
        if (takePictureCallback == null || params == null) {
            Log.e("libCGE_java", "takePicture after release!");
            if (takePictureCallback != null) {
                takePictureCallback.takePictureOK(null);
            }
            return;
        }
        try {
            params.setRotation(90);
            cameraInstance().setParams(params);
            cameraInstance().getCameraDevice().takePicture(shutterCallback, null, new Camera.PictureCallback() { // from class: org.wysaid.view.CameraGLSurfaceViewWithTexture.6
                *//* JADX WARN: Removed duplicated region for block: B:20:0x01a1  *//*
                @Override // android.hardware.Camera.PictureCallback
                *//*
                    Code decompiled incorrectly, please refer to instructions dump.
                    To view partially-correct code enable 'Show inconsistent code' option in preferences
                *//*
                public void onPictureTaken(byte[] r13, Camera r14) {
                    *//*
                        Method dump skipped, instructions count: 451
                        To view this dump change 'Code comments level' option to 'DEBUG'
                    *//*
                    throw new UnsupportedOperationException("Method not decompiled: org.wysaid.view.CameraGLSurfaceViewWithTexture.AnonymousClass6.onPictureTaken(byte[], android.hardware.Camera):void");
                }
            });
        } catch (Exception e) {
            Log.e("libCGE_java", "Error when takePicture: " + e.toString());
            if (takePictureCallback != null) {
                takePictureCallback.takePictureOK(null);
            }
        }
    }*/
}
