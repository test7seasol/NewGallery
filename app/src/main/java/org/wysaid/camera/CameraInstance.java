package org.wysaid.camera;

import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.util.Log;

import androidx.core.app.NotificationManagerCompat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import kotlinx.coroutines.DebugKt;

/* loaded from: classes4.dex */
public class CameraInstance {
    static final   boolean $assertionsDisabled = false;
    private static final String ASSERT_MSG = "检测到CameraDevice 为 null! 请检查";
    public static final int DEFAULT_PREVIEW_RATE = 30;
    public static final String LOG_TAG = "libCGE_java";
    private static CameraInstance mThisInstance;
    private Camera mCameraDevice;
    private Camera.Parameters mParams;
    private int mPreviewHeight;
    private int mPreviewWidth;
    private boolean mIsPreviewing = false;
    private int mDefaultCameraID = -1;
    private int mPictureWidth = 1000;
    private int mPictureHeight = 1000;
    private int mPreferPreviewWidth = 640;
    private int mPreferPreviewHeight = 640;
    private int mFacing = 0;
    private Comparator<Camera.Size> comparatorBigger = new Comparator<Camera.Size>() { // from class: org.wysaid.camera.CameraInstance.1
        @Override // java.util.Comparator
        public int compare(Camera.Size size, Camera.Size size2) {
            int i = size2.width - size.width;
            return i == 0 ? size2.height - size.height : i;
        }
    };
    private Comparator<Camera.Size> comparatorSmaller = new Comparator<Camera.Size>() { // from class: org.wysaid.camera.CameraInstance.2
        @Override // java.util.Comparator
        public int compare(Camera.Size size, Camera.Size size2) {
            int i = size.width - size2.width;
            return i == 0 ? size.height - size2.height : i;
        }
    };

    public interface CameraOpenCallback {
        void cameraReady();
    }

    private CameraInstance() {
    }

    public static synchronized CameraInstance getInstance() {
        CameraInstance cameraInstance;
        synchronized (CameraInstance.class) {
            if (mThisInstance == null) {
                mThisInstance = new CameraInstance();
            }
            cameraInstance = mThisInstance;
        }
        return cameraInstance;
    }

    public boolean isPreviewing() {
        return this.mIsPreviewing;
    }

    public int previewWidth() {
        return this.mPreviewWidth;
    }

    public int previewHeight() {
        return this.mPreviewHeight;
    }

    public int pictureWidth() {
        return this.mPictureWidth;
    }

    public int pictureHeight() {
        return this.mPictureHeight;
    }

    public void setPreferPreviewSize(int i, int i2) {
        this.mPreferPreviewHeight = i;
        this.mPreferPreviewWidth = i2;
    }

    public boolean tryOpenCamera(CameraOpenCallback cameraOpenCallback) {
        return tryOpenCamera(cameraOpenCallback, 0);
    }

    public int getFacing() {
        return this.mFacing;
    }

    public synchronized boolean tryOpenCamera(CameraOpenCallback cameraOpenCallback, int i) {
        Log.i("libCGE_java", "try open camera...");
        try {
            int numberOfCameras = Camera.getNumberOfCameras();
            Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
            for (int i2 = 0; i2 < numberOfCameras; i2++) {
                Camera.getCameraInfo(i2, cameraInfo);
                if (cameraInfo.facing == i) {
                    this.mDefaultCameraID = i2;
                    this.mFacing = i;
                }
            }
            stopPreview();
            Camera camera = this.mCameraDevice;
            if (camera != null) {
                camera.release();
            }
            int i3 = this.mDefaultCameraID;
            if (i3 >= 0) {
                this.mCameraDevice = Camera.open(i3);
            } else {
                this.mCameraDevice = Camera.open();
                this.mFacing = 0;
            }
            if (this.mCameraDevice == null) {
                return false;
            }
            Log.i("libCGE_java", "Camera opened!");
            try {
                initCamera(30);
                if (cameraOpenCallback != null) {
                    cameraOpenCallback.cameraReady();
                }
                return true;
            } catch (Exception unused) {
                this.mCameraDevice.release();
                this.mCameraDevice = null;
                return false;
            }
        } catch (Exception e) {
            Log.e("libCGE_java", "Open Camera Failed!");
            e.printStackTrace();
            this.mCameraDevice = null;
            return false;
        }
    }

    public synchronized void stopCamera() {
        Camera camera = this.mCameraDevice;
        if (camera != null) {
            this.mIsPreviewing = false;
            camera.stopPreview();
            this.mCameraDevice.setPreviewCallback(null);
            this.mCameraDevice.release();
            this.mCameraDevice = null;
        }
    }

    public boolean isCameraOpened() {
        return this.mCameraDevice != null;
    }

    public synchronized void startPreview(SurfaceTexture surfaceTexture, Camera.PreviewCallback previewCallback) {
        Log.i("libCGE_java", "Camera startPreview...");
        if (this.mIsPreviewing) {
            Log.e("libCGE_java", "Err: camera is previewing...");
            return;
        }
        Camera camera = this.mCameraDevice;
        if (camera != null) {
            try {
                camera.setPreviewTexture(surfaceTexture);
                this.mCameraDevice.setPreviewCallbackWithBuffer(previewCallback);
            } catch (IOException e) {
                e.printStackTrace();
            }
            this.mCameraDevice.startPreview();
            this.mIsPreviewing = true;
        }
    }

    public void startPreview(SurfaceTexture surfaceTexture) {
        startPreview(surfaceTexture, null);
    }

    public void startPreview(Camera.PreviewCallback previewCallback) {
        startPreview(null, previewCallback);
    }

    public synchronized void stopPreview() {
        if (this.mIsPreviewing && this.mCameraDevice != null) {
            Log.i("libCGE_java", "Camera stopPreview...");
            this.mIsPreviewing = false;
            this.mCameraDevice.stopPreview();
        }
    }

    public synchronized Camera.Parameters getParams() {
        Camera camera = this.mCameraDevice;
        if (camera == null) {
            return null;
        }
        return camera.getParameters();
    }

    public synchronized void setParams(Camera.Parameters parameters) {
        Camera camera = this.mCameraDevice;
        if (camera != null) {
            this.mParams = parameters;
            camera.setParameters(parameters);
        }
    }

    public Camera getCameraDevice() {
        return this.mCameraDevice;
    }

    public void initCamera(int i) {
        Camera camera = this.mCameraDevice;
        if (camera == null) {
            Log.e("libCGE_java", "initCamera: Camera is not opened!");
            return;
        }
        Camera.Parameters parameters = camera.getParameters();
        this.mParams = parameters;
        Iterator<Integer> it = parameters.getSupportedPictureFormats().iterator();
        while (it.hasNext()) {
            Log.i("libCGE_java", String.format("Picture Format: %x", Integer.valueOf(it.next().intValue())));
        }
        this.mParams.setPictureFormat(256);
        List<Camera.Size> supportedPictureSizes = this.mParams.getSupportedPictureSizes();
        Collections.sort(supportedPictureSizes, this.comparatorBigger);
        Camera.Size size = null;
        Camera.Size size2 = null;
        for (Camera.Size size3 : supportedPictureSizes) {
            Log.i("libCGE_java", String.format("Supported picture size: %d x %d", Integer.valueOf(size3.width), Integer.valueOf(size3.height)));
            if (size2 == null || (size3.width >= this.mPictureWidth && size3.height >= this.mPictureHeight)) {
                size2 = size3;
            }
        }
        List<Camera.Size> supportedPreviewSizes = this.mParams.getSupportedPreviewSizes();
        Collections.sort(supportedPreviewSizes, this.comparatorBigger);
        for (Camera.Size size4 : supportedPreviewSizes) {
            Log.i("libCGE_java", String.format("Supported preview size: %d x %d", Integer.valueOf(size4.width), Integer.valueOf(size4.height)));
            if (size == null || (size4.width >= this.mPreferPreviewWidth && size4.height >= this.mPreferPreviewHeight)) {
                size = size4;
            }
        }
        int i2 = 0;
        for (Integer num : this.mParams.getSupportedPreviewFrameRates()) {
            Log.i("libCGE_java", "Supported frame rate: " + num);
            if (i2 < num.intValue()) {
                i2 = num.intValue();
            }
        }
        this.mParams.setPreviewSize(size.width, size.height);
        this.mParams.setPictureSize(size2.width, size2.height);
        if (this.mParams.getSupportedFocusModes().contains("continuous-video")) {
            this.mParams.setFocusMode("continuous-video");
        }
        this.mParams.setPreviewFrameRate(i2);
        try {
            this.mCameraDevice.setParameters(this.mParams);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Camera.Parameters parameters2 = this.mCameraDevice.getParameters();
        this.mParams = parameters2;
        Camera.Size pictureSize = parameters2.getPictureSize();
        Camera.Size previewSize = this.mParams.getPreviewSize();
        this.mPreviewWidth = previewSize.width;
        this.mPreviewHeight = previewSize.height;
        this.mPictureWidth = pictureSize.width;
        this.mPictureHeight = pictureSize.height;
        Log.i("libCGE_java", String.format("Camera Picture Size: %d x %d", Integer.valueOf(pictureSize.width), Integer.valueOf(pictureSize.height)));
        Log.i("libCGE_java", String.format("Camera Preview Size: %d x %d", Integer.valueOf(previewSize.width), Integer.valueOf(previewSize.height)));
    }

    public synchronized void setFocusMode(String str) {
        Camera camera = this.mCameraDevice;
        if (camera == null) {
            return;
        }
        Camera.Parameters parameters = camera.getParameters();
        this.mParams = parameters;
        if (parameters.getSupportedFocusModes().contains(str)) {
            this.mParams.setFocusMode(str);
        }
    }

    public synchronized void setPictureSize(int i, int i2, boolean z) {
        Camera camera = this.mCameraDevice;
        if (camera == null) {
            this.mPictureWidth = i;
            this.mPictureHeight = i2;
            return;
        }
        Camera.Parameters parameters = camera.getParameters();
        this.mParams = parameters;
        List<Camera.Size> supportedPictureSizes = parameters.getSupportedPictureSizes();
        Camera.Size size = null;
        if (z) {
            Collections.sort(supportedPictureSizes, this.comparatorBigger);
            for (Camera.Size size2 : supportedPictureSizes) {
                if (size == null || (size2.width >= i && size2.height >= i2)) {
                    size = size2;
                }
            }
        } else {
            Collections.sort(supportedPictureSizes, this.comparatorSmaller);
            for (Camera.Size size3 : supportedPictureSizes) {
                if (size == null || (size3.width <= i && size3.height <= i2)) {
                    size = size3;
                }
            }
        }
        this.mPictureWidth = size.width;
        int i3 = size.height;
        this.mPictureHeight = i3;
        try {
            this.mParams.setPictureSize(this.mPictureWidth, i3);
            this.mCameraDevice.setParameters(this.mParams);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void focusAtPoint(float f, float f2, Camera.AutoFocusCallback autoFocusCallback) {
        focusAtPoint(f, f2, 0.2f, autoFocusCallback);
    }

    public synchronized void focusAtPoint(float f, float f2, float f3, Camera.AutoFocusCallback autoFocusCallback) {
        Camera camera = this.mCameraDevice;
        if (camera == null) {
            Log.e("libCGE_java", "Error: focus after release.");
            return;
        }
        Camera.Parameters parameters = camera.getParameters();
        this.mParams = parameters;
        if (parameters.getMaxNumMeteringAreas() > 0) {
            int i = (int) (f3 * 1000.0f);
            int i2 = ((int) ((f * 2000.0f) - 1000.0f)) - i;
            int i3 = ((int) ((f2 * 2000.0f) - 1000.0f)) - i;
            Rect rect = new Rect();
            rect.left = Math.max(i2, NotificationManagerCompat.IMPORTANCE_UNSPECIFIED);
            rect.top = Math.max(i3, NotificationManagerCompat.IMPORTANCE_UNSPECIFIED);
            rect.right = Math.min(i2 + i, 1000);
            rect.bottom = Math.min(i3 + i, 1000);
            ArrayList arrayList = new ArrayList();
            arrayList.add(new Camera.Area(rect, 800));
            try {
                this.mCameraDevice.cancelAutoFocus();
                this.mParams.setFocusMode(DebugKt.DEBUG_PROPERTY_VALUE_AUTO);
                this.mParams.setFocusAreas(arrayList);
                this.mCameraDevice.setParameters(this.mParams);
                this.mCameraDevice.autoFocus(autoFocusCallback);
            } catch (Exception e) {
                Log.e("libCGE_java", "Error: focusAtPoint failed: " + e.toString());
            }
            return;
        }
        Log.i("libCGE_java", "The device does not support metering areas...");
        try {
            this.mCameraDevice.autoFocus(autoFocusCallback);
        } catch (Exception e2) {
            Log.e("libCGE_java", "Error: focusAtPoint failed: " + e2.toString());
        }
        return;
    }
}
