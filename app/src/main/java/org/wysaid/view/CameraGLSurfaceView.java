package org.wysaid.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;

import org.wysaid.camera.CameraInstance;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/* loaded from: classes4.dex */
public class CameraGLSurfaceView extends GLSurfaceView implements GLSurfaceView.Renderer {
    public static final String LOG_TAG = "libCGE_java";
    protected Viewport mDrawViewport;
    protected boolean mFitFullView;
    protected boolean mIsCameraBackForward;
    protected int mMaxPreviewHeight;
    protected int mMaxPreviewWidth;
    public int mMaxTextureSize;
    protected OnCreateCallback mOnCreateCallback;
    protected int mRecordHeight;
    protected int mRecordWidth;
    protected int mViewHeight;
    protected int mViewWidth;

    public interface OnCreateCallback {
        void createOver();
    }

    public interface ReleaseOKCallback {
        void releaseOK();
    }

    public interface TakePictureCallback {
        void takePictureOK(Bitmap bitmap);
    }

    public static class Viewport {
        public int height;
        public int width;
        public int x;
        public int y;
    }

    @Override // android.opengl.GLSurfaceView.Renderer
    public void onDrawFrame(GL10 gl10) {
    }

    protected void onRelease() {
    }

    protected void onSwitchCamera() {
    }

    public void resumePreview() {
    }

    public void takeShot(TakePictureCallback takePictureCallback) {
    }

    public CameraGLSurfaceView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mMaxTextureSize = 0;
        this.mRecordWidth = 480;
        this.mRecordHeight = 640;
        this.mMaxPreviewWidth = 1280;
        this.mMaxPreviewHeight = 1280;
        this.mDrawViewport = new Viewport();
        this.mFitFullView = false;
        this.mIsCameraBackForward = true;
        setEGLContextClientVersion(2);
        setEGLConfigChooser(8, 8, 8, 8, 0, 0);
        getHolder().setFormat(1);
        setRenderer(this);
        setRenderMode(0);
    }

    public void setPictureSize(int i, int i2, boolean z) {
        cameraInstance().setPictureSize(i2, i, z);
    }

    public synchronized boolean setFlashLightMode(String str) {
        if (!getContext().getPackageManager().hasSystemFeature("android.hardware.camera.flash")) {
            Log.e("libCGE_java", "No flash light is supported by current device!");
            return false;
        }
        if (!this.mIsCameraBackForward) {
            return false;
        }
        Camera.Parameters params = cameraInstance().getParams();
        if (params == null) {
            return false;
        }
        try {
            if (!params.getSupportedFlashModes().contains(str)) {
                Log.e("libCGE_java", "Invalid Flash Light Mode!!!");
                return false;
            }
            params.setFlashMode(str);
            cameraInstance().setParams(params);
            return true;
        } catch (Exception unused) {
            Log.e("libCGE_java", "Switch flash light failed, check if you're using front camera.");
            return false;
        }
    }

    public Viewport getDrawViewport() {
        return this.mDrawViewport;
    }

    void setMaxPreviewSize(int i, int i2) {
        this.mMaxPreviewWidth = i;
        this.mMaxPreviewHeight = i2;
    }

    public void setFitFullView(boolean z) {
        this.mFitFullView = z;
        calcViewport();
    }

    public boolean isCameraBackForward() {
        return this.mIsCameraBackForward;
    }

    public CameraInstance cameraInstance() {
        return CameraInstance.getInstance();
    }

    public void presetCameraForward(boolean z) {
        this.mIsCameraBackForward = z;
    }

    public void presetRecordingSize(int i, int i2) {
        int i3 = this.mMaxPreviewWidth;
        if (i > i3 || i2 > this.mMaxPreviewHeight) {
            float f = i;
            float f2 = i2;
            float min = Math.min(i3 / f, this.mMaxPreviewHeight / f2);
            i = (int) (f * min);
            i2 = (int) (f2 * min);
        }
        this.mRecordWidth = i;
        this.mRecordHeight = i2;
        cameraInstance().setPreferPreviewSize(i, i2);
    }

    public void stopPreview() {
        queueEvent(new Runnable() { // from class: org.wysaid.view.CameraGLSurfaceView.1
            @Override // java.lang.Runnable
            public void run() {
                CameraGLSurfaceView.this.cameraInstance().stopPreview();
            }
        });
    }

    public final void switchCamera() {
        this.mIsCameraBackForward = !this.mIsCameraBackForward;
        queueEvent(new Runnable() { // from class: org.wysaid.view.CameraGLSurfaceView.2
            @Override // java.lang.Runnable
            public void run() {
                CameraGLSurfaceView.this.cameraInstance().stopCamera();
                CameraGLSurfaceView.this.onSwitchCamera();
                CameraGLSurfaceView.this.cameraInstance().tryOpenCamera(new CameraInstance.CameraOpenCallback() { // from class: org.wysaid.view.CameraGLSurfaceView.2.1
                    @Override // org.wysaid.camera.CameraInstance.CameraOpenCallback
                    public void cameraReady() {
                        CameraGLSurfaceView.this.resumePreview();
                    }
                }, !CameraGLSurfaceView.this.mIsCameraBackForward ? 1 : 0);
                CameraGLSurfaceView.this.requestRender();
            }
        });
    }

    public void focusAtPoint(float f, float f2, Camera.AutoFocusCallback autoFocusCallback) {
        cameraInstance().focusAtPoint(f2, 1.0f - f, autoFocusCallback);
    }

    @Override // android.opengl.GLSurfaceView, android.view.SurfaceHolder.Callback
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        super.surfaceDestroyed(surfaceHolder);
        cameraInstance().stopCamera();
    }

    public void setOnCreateCallback(OnCreateCallback onCreateCallback) {
        this.mOnCreateCallback = onCreateCallback;
    }

    @Override // android.opengl.GLSurfaceView.Renderer
    public void onSurfaceCreated(GL10 gl10, EGLConfig eGLConfig) {
        Log.i("libCGE_java", "onSurfaceCreated...");
        GLES20.glDisable(2929);
        GLES20.glDisable(2960);
        GLES20.glBlendFunc(770, 771);
        int[] iArr = new int[1];
        GLES20.glGetIntegerv(3379, iArr, 0);
        this.mMaxTextureSize = iArr[0];
        OnCreateCallback onCreateCallback = this.mOnCreateCallback;
        if (onCreateCallback != null) {
            onCreateCallback.createOver();
        }
    }

    @Override // android.opengl.GLSurfaceView.Renderer
    public void onSurfaceChanged(GL10 gl10, int i, int i2) {
        Log.i("libCGE_java", String.format("onSurfaceChanged: %d x %d", Integer.valueOf(i), Integer.valueOf(i2)));
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        this.mViewWidth = i;
        this.mViewHeight = i2;
        calcViewport();
    }

    @Override // android.opengl.GLSurfaceView
    public void onResume() {
        super.onResume();
        Log.i("libCGE_java", "glsurfaceview onResume...");
    }

    @Override // android.opengl.GLSurfaceView
    public void onPause() {
        Log.i("libCGE_java", "glsurfaceview onPause in...");
        cameraInstance().stopCamera();
        super.onPause();
        Log.i("libCGE_java", "glsurfaceview onPause out...");
    }

    public final void release(final ReleaseOKCallback releaseOKCallback) {
        queueEvent(new Runnable() { // from class: org.wysaid.view.CameraGLSurfaceView.3
            @Override // java.lang.Runnable
            public void run() {
                CameraGLSurfaceView.this.onRelease();
                Log.i("libCGE_java", "GLSurfaceview release...");
                ReleaseOKCallback releaseOKCallback2 = releaseOKCallback;
                if (releaseOKCallback2 != null) {
                    releaseOKCallback2.releaseOK();
                }
            }
        });
    }

    protected void calcViewport() {
        float f = this.mRecordWidth / this.mRecordHeight;
        int i = this.mViewWidth;
        int i2 = this.mViewHeight;
        float f2 = f / (i / i2);
        if (!this.mFitFullView ? f2 > 1.0d : f2 <= 1.0d) {
            i = (int) (i2 * f);
        } else {
            i2 = (int) (i / f);
        }
        this.mDrawViewport.width = i;
        this.mDrawViewport.height = i2;
        Viewport viewport = this.mDrawViewport;
        viewport.x = (this.mViewWidth - viewport.width) / 2;
        Viewport viewport2 = this.mDrawViewport;
        viewport2.y = (this.mViewHeight - viewport2.height) / 2;
        Log.i("libCGE_java", String.format("View port: %d, %d, %d, %d", Integer.valueOf(this.mDrawViewport.x), Integer.valueOf(this.mDrawViewport.y), Integer.valueOf(this.mDrawViewport.width), Integer.valueOf(this.mDrawViewport.height)));
    }
}
