package org.wysaid.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;

import org.wysaid.nativePort.CGEImageHandler;
import org.wysaid.texUtils.TextureRenderer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/* loaded from: classes4.dex */
public class ImageGLSurfaceView extends GLSurfaceView implements GLSurfaceView.Renderer {
    public static final String LOG_TAG = "libCGE_java";
    protected DisplayMode mDisplayMode;
    protected float mFilterIntensity;
    protected CGEImageHandler mImageHandler;
    protected int mImageHeight;
    protected int mImageWidth;
    protected TextureRenderer.Viewport mRenderViewport;
    protected int mSettingIntensityCount;
    protected final Object mSettingIntensityLock;
    protected OnSurfaceCreatedCallback mSurfaceCreatedCallback;
    protected int mViewHeight;
    protected int mViewWidth;

    public enum DisplayMode {
        DISPLAY_SCALE_TO_FILL,
        DISPLAY_ASPECT_FILL,
        DISPLAY_ASPECT_FIT
    }

    public interface OnSurfaceCreatedCallback {
        void surfaceCreated();
    }

    public interface QueryResultBitmapCallback {
        void get(Bitmap bitmap);
    }

    public CGEImageHandler getImageHandler() {
        return this.mImageHandler;
    }

    public TextureRenderer.Viewport getRenderViewport() {
        return this.mRenderViewport;
    }

    public int getImageWidth() {
        return this.mImageWidth;
    }

    public int getImageheight() {
        return this.mImageHeight;
    }

    public DisplayMode getDisplayMode() {
        return this.mDisplayMode;
    }

    public void setDisplayMode(DisplayMode displayMode) {
        this.mDisplayMode = displayMode;
        calcViewport();
        requestRender();
    }

    public void setFilterWithConfig(final String str) {
        if (this.mImageHandler == null) {
            return;
        }
        queueEvent(new Runnable() { // from class: org.wysaid.view.ImageGLSurfaceView.1
            @Override // java.lang.Runnable
            public void run() {
                if (ImageGLSurfaceView.this.mImageHandler == null) {
                    Log.e("libCGE_java", "set config after release!!");
                } else {
                    ImageGLSurfaceView.this.mImageHandler.setFilterWithConfig(str);
                    ImageGLSurfaceView.this.requestRender();
                }
            }
        });
    }

    public void setFilterIntensityForIndex(float f, int i) {
        setFilterIntensityForIndex(f, i, true);
    }

    public void setFilterIntensityForIndex(float f, final int i, final boolean z) {
        if (this.mImageHandler == null) {
            return;
        }
        this.mFilterIntensity = f;
        synchronized (this.mSettingIntensityLock) {
            int i2 = this.mSettingIntensityCount;
            if (i2 <= 0) {
                Log.i("libCGE_java", "Too fast, skipping...");
            } else {
                this.mSettingIntensityCount = i2 - 1;
                queueEvent(new Runnable() { // from class: org.wysaid.view.ImageGLSurfaceView.2
                    @Override // java.lang.Runnable
                    public void run() {
                        if (ImageGLSurfaceView.this.mImageHandler == null) {
                            Log.e("libCGE_java", "set intensity after release!!");
                        } else {
                            ImageGLSurfaceView.this.mImageHandler.setFilterIntensityAtIndex(ImageGLSurfaceView.this.mFilterIntensity, i, z);
                            if (z) {
                                ImageGLSurfaceView.this.requestRender();
                            }
                        }
                        synchronized (ImageGLSurfaceView.this.mSettingIntensityLock) {
                            ImageGLSurfaceView.this.mSettingIntensityCount++;
                        }
                    }
                });
            }
        }
    }

    public void setFilterIntensity(float f) {
        if (this.mImageHandler == null) {
            return;
        }
        this.mFilterIntensity = f;
        synchronized (this.mSettingIntensityLock) {
            int i = this.mSettingIntensityCount;
            if (i <= 0) {
                Log.i("libCGE_java", "Too fast, skipping...");
            } else {
                this.mSettingIntensityCount = i - 1;
                queueEvent(new Runnable() { // from class: org.wysaid.view.ImageGLSurfaceView.3
                    @Override // java.lang.Runnable
                    public void run() {
                        if (ImageGLSurfaceView.this.mImageHandler == null) {
                            Log.e("libCGE_java", "set intensity after release!!");
                        } else {
                            ImageGLSurfaceView.this.mImageHandler.setFilterIntensity(ImageGLSurfaceView.this.mFilterIntensity, true);
                            ImageGLSurfaceView.this.requestRender();
                        }
                        synchronized (ImageGLSurfaceView.this.mSettingIntensityLock) {
                            ImageGLSurfaceView.this.mSettingIntensityCount++;
                        }
                    }
                });
            }
        }
    }

    public void flush(final boolean z, final Runnable runnable) {
        if (this.mImageHandler == null || runnable == null) {
            return;
        }
        queueEvent(new Runnable() { // from class: org.wysaid.view.ImageGLSurfaceView.4
            @Override // java.lang.Runnable
            public void run() {
                if (ImageGLSurfaceView.this.mImageHandler == null) {
                    Log.e("libCGE_java", "flush after release!!");
                    return;
                }
                runnable.run();
                if (z) {
                    ImageGLSurfaceView.this.mImageHandler.revertImage();
                    ImageGLSurfaceView.this.mImageHandler.processFilters();
                }
                ImageGLSurfaceView.this.requestRender();
            }
        });
    }

    public void lazyFlush(final boolean z, final Runnable runnable) {
        if (this.mImageHandler == null || runnable == null) {
            return;
        }
        synchronized (this.mSettingIntensityLock) {
            int i = this.mSettingIntensityCount;
            if (i <= 0) {
                Log.i("libCGE_java", "Too fast, skipping...");
            } else {
                this.mSettingIntensityCount = i - 1;
                queueEvent(new Runnable() { // from class: org.wysaid.view.ImageGLSurfaceView.5
                    @Override // java.lang.Runnable
                    public void run() {
                        if (ImageGLSurfaceView.this.mImageHandler == null) {
                            Log.e("libCGE_java", "flush after release!!");
                        } else {
                            if (z) {
                                ImageGLSurfaceView.this.mImageHandler.revertImage();
                                ImageGLSurfaceView.this.mImageHandler.processFilters();
                            }
                            runnable.run();
                            ImageGLSurfaceView.this.requestRender();
                        }
                        synchronized (ImageGLSurfaceView.this.mSettingIntensityLock) {
                            ImageGLSurfaceView.this.mSettingIntensityCount++;
                        }
                    }
                });
            }
        }
    }

    public void setImageBitmap(final Bitmap bitmap) {
        if (bitmap == null) {
            return;
        }
        if (this.mImageHandler == null) {
            Log.e("libCGE_java", "Handler not initialized!");
            return;
        }
        this.mImageWidth = bitmap.getWidth();
        this.mImageHeight = bitmap.getHeight();
        queueEvent(new Runnable() { // from class: org.wysaid.view.ImageGLSurfaceView.6
            @Override // java.lang.Runnable
            public void run() {
                if (ImageGLSurfaceView.this.mImageHandler == null) {
                    Log.e("libCGE_java", "set image after release!!");
                } else if (ImageGLSurfaceView.this.mImageHandler.initWithBitmap(bitmap)) {
                    ImageGLSurfaceView.this.calcViewport();
                    ImageGLSurfaceView.this.requestRender();
                } else {
                    Log.e("libCGE_java", "setImageBitmap: init handler failed!");
                }
            }
        });
    }

    public void getResultBitmap(final QueryResultBitmapCallback queryResultBitmapCallback) {
        if (queryResultBitmapCallback == null) {
            return;
        }
        queueEvent(new Runnable() { // from class: org.wysaid.view.ImageGLSurfaceView.7
            @Override // java.lang.Runnable
            public void run() {
                queryResultBitmapCallback.get(ImageGLSurfaceView.this.mImageHandler.getResultBitmap());
            }
        });
    }

    public ImageGLSurfaceView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mFilterIntensity = 1.0f;
        this.mRenderViewport = new TextureRenderer.Viewport();
        this.mDisplayMode = DisplayMode.DISPLAY_SCALE_TO_FILL;
        this.mSettingIntensityLock = new Object();
        this.mSettingIntensityCount = 1;
        setEGLContextClientVersion(2);
        setEGLConfigChooser(8, 8, 8, 8, 8, 0);
        getHolder().setFormat(1);
        setRenderer(this);
        setRenderMode(0);
        Log.i("libCGE_java", "ImageGLSurfaceView Construct...");
    }

    public void setSurfaceCreatedCallback(OnSurfaceCreatedCallback onSurfaceCreatedCallback) {
        this.mSurfaceCreatedCallback = onSurfaceCreatedCallback;
    }

    @Override // android.opengl.GLSurfaceView.Renderer
    public void onSurfaceCreated(GL10 gl10, EGLConfig eGLConfig) {
        Log.i("libCGE_java", "ImageGLSurfaceView onSurfaceCreated...");
        GLES20.glDisable(2929);
        GLES20.glDisable(2960);
        CGEImageHandler cGEImageHandler = new CGEImageHandler();
        this.mImageHandler = cGEImageHandler;
        cGEImageHandler.setDrawerFlipScale(1.0f, -1.0f);
        OnSurfaceCreatedCallback onSurfaceCreatedCallback = this.mSurfaceCreatedCallback;
        if (onSurfaceCreatedCallback != null) {
            onSurfaceCreatedCallback.surfaceCreated();
        }
    }

    @Override // android.opengl.GLSurfaceView.Renderer
    public void onSurfaceChanged(GL10 gl10, int i, int i2) {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        this.mViewWidth = i;
        this.mViewHeight = i2;
        calcViewport();
    }

    @Override // android.opengl.GLSurfaceView.Renderer
    public void onDrawFrame(GL10 gl10) {
        GLES20.glBindFramebuffer(36160, 0);
        GLES20.glClear(16384);
        if (this.mImageHandler == null) {
            return;
        }
        GLES20.glViewport(this.mRenderViewport.x, this.mRenderViewport.y, this.mRenderViewport.width, this.mRenderViewport.height);
        this.mImageHandler.drawResult();
    }

    public void release() {
        if (this.mImageHandler != null) {
            queueEvent(new Runnable() { // from class: org.wysaid.view.ImageGLSurfaceView.8
                @Override // java.lang.Runnable
                public void run() {
                    Log.i("libCGE_java", "ImageGLSurfaceView release...");
                    if (ImageGLSurfaceView.this.mImageHandler != null) {
                        ImageGLSurfaceView.this.mImageHandler.release();
                        ImageGLSurfaceView.this.mImageHandler = null;
                    }
                }
            });
        }
    }

    protected void calcViewport() {
        int i;
        int i2;
        int i3;
        if (this.mDisplayMode == DisplayMode.DISPLAY_SCALE_TO_FILL) {
            this.mRenderViewport.x = 0;
            this.mRenderViewport.y = 0;
            this.mRenderViewport.width = this.mViewWidth;
            this.mRenderViewport.height = this.mViewHeight;
            return;
        }
        float f = (float) this.mImageWidth / this.mImageHeight;
        float f2 = f / ((float) this.mViewWidth / this.mViewHeight);
        int i4 = AnonymousClass9.$SwitchMap$org$wysaid$view$ImageGLSurfaceView$DisplayMode[this.mDisplayMode.ordinal()];
        if (i4 != 1) {
            if (i4 != 2) {
                Log.i("libCGE_java", "Error occured, please check the code...");
                return;
            } else if (f2 < 1.0d) {
                i2 = this.mViewHeight;
                i3 = (int) (i2 * f);
            } else {
                i = this.mViewWidth;
                int i5 = i;
                i2 = (int) (i / f);
                i3 = i5;
            }
        } else if (f2 > 1.0d) {
            i2 = this.mViewHeight;
            i3 = (int) (i2 * f);
        } else {
            i = this.mViewWidth;
            int i52 = i;
            i2 = (int) (i / f);
            i3 = i52;
        }
        this.mRenderViewport.width = i3;
        this.mRenderViewport.height = i2;
        this.mRenderViewport.x = (this.mViewWidth - i3) / 2;
        this.mRenderViewport.y = (this.mViewHeight - i2) / 2;
        Log.i("libCGE_java", String.format("View port: %d, %d, %d, %d", Integer.valueOf(this.mRenderViewport.x), Integer.valueOf(this.mRenderViewport.y), Integer.valueOf(this.mRenderViewport.width), Integer.valueOf(this.mRenderViewport.height)));
    }

    /* renamed from: org.wysaid.view.ImageGLSurfaceView$9, reason: invalid class name */
    static   class AnonymousClass9 {
        static final   int[] $SwitchMap$org$wysaid$view$ImageGLSurfaceView$DisplayMode;

        static {
            int[] iArr = new int[DisplayMode.values().length];
            $SwitchMap$org$wysaid$view$ImageGLSurfaceView$DisplayMode = iArr;
            try {
                iArr[DisplayMode.DISPLAY_ASPECT_FILL.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$org$wysaid$view$ImageGLSurfaceView$DisplayMode[DisplayMode.DISPLAY_ASPECT_FIT.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
        }
    }
}
